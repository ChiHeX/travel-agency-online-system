package com.travelagency.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travelagency.common.enums.DepartureStatus;
import com.travelagency.common.enums.OrderStatus;
import com.travelagency.common.enums.PaymentStatus;
import com.travelagency.common.enums.RefundStatus;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.common.security.UserPrincipal;
import com.travelagency.domain.dto.CreateOrderRequest;
import com.travelagency.domain.dto.OrderDetailResponse;
import com.travelagency.domain.dto.PaymentStartResponse;
import com.travelagency.domain.dto.RefundRequest;
import com.travelagency.domain.dto.ReviewRequest;
import com.travelagency.domain.dto.TravelerView;
import com.travelagency.domain.entity.Departure;
import com.travelagency.domain.entity.Message;
import com.travelagency.domain.entity.OrderTraveler;
import com.travelagency.domain.entity.Payment;
import com.travelagency.domain.entity.Refund;
import com.travelagency.domain.entity.Review;
import com.travelagency.domain.entity.TravelOrder;
import com.travelagency.domain.entity.TravelRoute;
import com.travelagency.domain.mapper.DepartureMapper;
import com.travelagency.domain.mapper.MessageMapper;
import com.travelagency.domain.mapper.OrderTravelerMapper;
import com.travelagency.domain.mapper.PaymentMapper;
import com.travelagency.domain.mapper.RefundMapper;
import com.travelagency.domain.mapper.ReviewMapper;
import com.travelagency.domain.mapper.TravelOrderMapper;
import com.travelagency.domain.mapper.TravelRouteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final TravelOrderMapper orderMapper;
    private final DepartureMapper departureMapper;
    private final TravelRouteMapper routeMapper;
    private final OrderTravelerMapper orderTravelerMapper;
    private final PaymentMapper paymentMapper;
    private final RefundMapper refundMapper;
    private final ReviewMapper reviewMapper;
    private final MessageMapper messageMapper;

    public OrderService(
            TravelOrderMapper orderMapper,
            DepartureMapper departureMapper,
            TravelRouteMapper routeMapper,
            OrderTravelerMapper orderTravelerMapper,
            PaymentMapper paymentMapper,
            RefundMapper refundMapper,
            ReviewMapper reviewMapper,
            MessageMapper messageMapper) {
        this.orderMapper = orderMapper;
        this.departureMapper = departureMapper;
        this.routeMapper = routeMapper;
        this.orderTravelerMapper = orderTravelerMapper;
        this.paymentMapper = paymentMapper;
        this.refundMapper = refundMapper;
        this.reviewMapper = reviewMapper;
        this.messageMapper = messageMapper;
    }

    @Transactional
    public TravelOrder create(Long userId, CreateOrderRequest request) {
        int participantCount = request.adultCount() + request.childCount();
        if (participantCount <= 0) {
            throw new BusinessException("至少选择一位成人或儿童");
        }
        if (request.travelers().size() != participantCount) {
            throw new BusinessException("出行人数量必须与成人和儿童人数一致");
        }
        Departure departure = departureMapper.selectById(request.departureId());
        if (departure == null || !DepartureStatus.OPEN.equals(departure.status)) {
            throw new BusinessException("团期已关闭或不存在");
        }
        int reserved = valueOrZero(departure.reservedPeople);
        int confirmed = valueOrZero(departure.confirmedPeople);
        int max = valueOrZero(departure.maxPeople);
        if (reserved + confirmed + participantCount > max) {
            throw new BusinessException("团期剩余名额不足");
        }

        UpdateWrapper<Departure> reserve = new UpdateWrapper<>();
        reserve.eq("id", departure.id)
                .eq("status", DepartureStatus.OPEN)
                .apply("COALESCE(reserved_people, 0) + COALESCE(confirmed_people, 0) + {0} <= max_people", participantCount)
                .setSql("reserved_people = COALESCE(reserved_people, 0) + " + participantCount);
        if (departureMapper.update(null, reserve) != 1) {
            throw new BusinessException("名额刚刚被其他用户占用，请重新选择团期");
        }

        TravelOrder order = new TravelOrder();
        order.orderNo = generateOrderNo();
        order.userId = userId;
        order.routeId = departure.routeId;
        order.departureId = departure.id;
        order.contactName = request.contactName();
        order.contactPhone = request.contactPhone();
        order.contactEmail = request.contactEmail();
        order.adultCount = request.adultCount();
        order.childCount = request.childCount();
        order.adultUnitPrice = defaultAmount(departure.adultPrice);
        order.childUnitPrice = defaultAmount(departure.childPrice);
        order.totalAmount = order.adultUnitPrice.multiply(BigDecimal.valueOf(request.adultCount()))
                .add(order.childUnitPrice.multiply(BigDecimal.valueOf(request.childCount())))
                .setScale(2, RoundingMode.HALF_UP);
        order.status = OrderStatus.WAIT_PAY;
        order.paymentStatus = PaymentStatus.UNPAID;
        order.remark = request.remark();
        orderMapper.insert(order);

        for (CreateOrderRequest.TravelerSnapshotRequest requestTraveler : request.travelers()) {
            OrderTraveler snapshot = new OrderTraveler();
            snapshot.orderId = order.id;
            snapshot.name = requestTraveler.name();
            snapshot.gender = requestTraveler.gender();
            snapshot.birthDate = requestTraveler.birthDate();
            snapshot.idType = requestTraveler.idType();
            snapshot.idNo = requestTraveler.idNo();
            snapshot.phone = requestTraveler.phone();
            snapshot.emergencyName = requestTraveler.emergencyName();
            snapshot.emergencyPhone = requestTraveler.emergencyPhone();
            orderTravelerMapper.insert(snapshot);
        }

        Payment payment = new Payment();
        payment.orderId = order.id;
        payment.paymentNo = "PAY" + order.orderNo;
        payment.channel = "ALIPAY_SANDBOX";
        payment.amount = order.totalAmount;
        payment.status = PaymentStatus.UNPAID;
        paymentMapper.insert(payment);
        return order;
    }

    public List<TravelOrder> listMine(Long userId, String status) {
        QueryWrapper<TravelOrder> query = new QueryWrapper<TravelOrder>().eq("user_id", userId);
        if (status != null && !status.isBlank()) {
            query.eq("status", status);
        }
        return orderMapper.selectList(query.orderByDesc("created_at"));
    }

    public OrderDetailResponse detail(String orderNo, UserPrincipal requester) {
        TravelOrder order = findByNo(orderNo);
        boolean staff = requester.roles().stream().anyMatch(role ->
                "STAFF".equals(role) || "ADMIN".equals(role) || "ROLE_STAFF".equals(role) || "ROLE_ADMIN".equals(role));
        if (!staff && !order.userId.equals(requester.userId())) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return toDetail(order);
    }

    @Transactional
    public PaymentStartResponse startPayment(String orderNo, Long userId) {
        TravelOrder order = findByNo(orderNo);
        ensureOwner(order, userId);
        if (!OrderStatus.WAIT_PAY.equals(order.status)) {
            throw new BusinessException("当前订单状态不允许支付");
        }
        Payment payment = paymentFor(order.id);
        payment.status = PaymentStatus.PAYING;
        paymentMapper.updateById(payment);
        return new PaymentStartResponse(order.orderNo, payment.channel, payment.status, order.totalAmount,
                "/api/payments/alipay/callback", "当前为支付宝沙箱演示环境，请由后端回调确认支付结果");
    }

    @Transactional
    public void cancel(String orderNo, Long userId) {
        TravelOrder order = findByNo(orderNo);
        ensureOwner(order, userId);
        if (!OrderStatus.WAIT_PAY.equals(order.status)) {
            throw new BusinessException("仅待支付订单可以直接取消，已支付订单请申请退款");
        }
        order.status = OrderStatus.CANCELLED;
        order.cancelledAt = LocalDateTime.now();
        orderMapper.updateById(order);
        releaseReserved(order);
    }

    /**
     * This method is called only after the payment adapter has verified Alipay's signature.
     * It is idempotent so a repeated notification cannot advance the order twice.
     */
    @Transactional
    public void markPaid(String orderNo, String tradeNo) {
        TravelOrder order = findByNo(orderNo);
        Payment payment = paymentFor(order.id);
        if (PaymentStatus.PAID.equals(payment.status)) {
            return;
        }
        if (!OrderStatus.WAIT_PAY.equals(order.status)) {
            throw new BusinessException("订单当前状态不接受支付回调");
        }
        payment.status = PaymentStatus.PAID;
        payment.thirdPartyTradeNo = tradeNo;
        payment.paidAt = LocalDateTime.now();
        paymentMapper.updateById(payment);

        order.paymentStatus = PaymentStatus.PAID;
        order.status = OrderStatus.PAID_WAIT_CONFIRM;
        order.paidAt = LocalDateTime.now();
        orderMapper.updateById(order);
        notify(order.userId, "支付成功", "订单 " + order.orderNo + " 已支付，等待旅行社确认报名。", "PAYMENT_SUCCESS");
    }

    @Transactional
    public void confirm(String orderNo, Long operatorId) {
        TravelOrder order = findByNo(orderNo);
        if (!OrderStatus.PAID_WAIT_CONFIRM.equals(order.status)) {
            throw new BusinessException("只有待确认订单可以审核");
        }
        Departure departure = departureMapper.selectById(order.departureId);
        if (departure == null || !DepartureStatus.OPEN.equals(departure.status)) {
            throw new BusinessException("团期已关闭，无法确认报名");
        }
        int participantCount = participants(order);
        UpdateWrapper<Departure> confirm = new UpdateWrapper<>();
        confirm.eq("id", departure.id)
                .eq("status", DepartureStatus.OPEN)
                .apply("COALESCE(confirmed_people, 0) + {0} <= max_people", participantCount)
                .setSql("reserved_people = GREATEST(COALESCE(reserved_people, 0) - " + participantCount + ", 0)")
                .setSql("confirmed_people = COALESCE(confirmed_people, 0) + " + participantCount);
        if (departureMapper.update(null, confirm) != 1) {
            throw new BusinessException("团期名额已不足，暂不能确认报名");
        }
        order.status = OrderStatus.CONFIRMED;
        order.confirmedAt = LocalDateTime.now();
        orderMapper.updateById(order);
        routeMapper.update(null, new UpdateWrapper<TravelRoute>()
                .eq("id", order.routeId)
                .setSql("valid_booking_count = COALESCE(valid_booking_count, 0) + 1"));
        notify(order.userId, "报名已确认", "订单 " + order.orderNo + " 已通过旅行社审核。", "ORDER_CONFIRMED");
    }

    @Transactional
    public void applyRefund(String orderNo, Long userId, RefundRequest request) {
        TravelOrder order = findByNo(orderNo);
        ensureOwner(order, userId);
        if (!(OrderStatus.PAID_WAIT_CONFIRM.equals(order.status)
                || OrderStatus.CONFIRMED.equals(order.status))) {
            throw new BusinessException("当前订单状态不允许申请退款");
        }
        Refund existing = refundMapper.selectOne(new QueryWrapper<Refund>()
                .eq("order_id", order.id).in("status", RefundStatus.APPLYING, RefundStatus.PROCESSING));
        if (existing != null) {
            throw new BusinessException("该订单已有处理中退款申请");
        }
        Refund refund = new Refund();
        refund.orderId = order.id;
        refund.userId = userId;
        refund.amount = order.totalAmount;
        refund.reason = request.reason();
        refund.originalOrderStatus = order.status;
        refund.status = RefundStatus.APPLYING;
        refundMapper.insert(refund);
        order.status = OrderStatus.REFUND_APPLYING;
        orderMapper.updateById(order);
    }

    @Transactional
    public void processRefund(Long refundId, String action, String comment, Long reviewerId) {
        Refund refund = refundMapper.selectById(refundId);
        if (refund == null || !RefundStatus.APPLYING.equals(refund.status)) {
            throw new BusinessException("退款申请不存在或已处理");
        }
        TravelOrder order = orderMapper.selectById(refund.orderId);
        if (order == null) {
            throw new BusinessException("关联订单不存在");
        }
        refund.reviewedBy = reviewerId;
        refund.reviewedAt = LocalDateTime.now();
        refund.reviewComment = comment;
        if ("APPROVE".equalsIgnoreCase(action)) {
            refund.status = RefundStatus.PROCESSING;
            refundMapper.updateById(refund);
            releaseCapacity(order, refund.originalOrderStatus);
            refund.status = RefundStatus.REFUNDED;
            refundMapper.updateById(refund);
            order.status = OrderStatus.REFUNDED;
            order.paymentStatus = PaymentStatus.REFUNDED;
            orderMapper.updateById(order);
            if (OrderStatus.CONFIRMED.equals(refund.originalOrderStatus)
                    || OrderStatus.TRAVELLING.equals(refund.originalOrderStatus)) {
                routeMapper.update(null, new UpdateWrapper<TravelRoute>()
                        .eq("id", order.routeId)
                        .setSql("valid_booking_count = GREATEST(COALESCE(valid_booking_count, 0) - 1, 0)"));
            }
            Payment payment = paymentFor(order.id);
            payment.status = PaymentStatus.REFUNDED;
            paymentMapper.updateById(payment);
            notify(order.userId, "退款审核通过", "订单 " + order.orderNo + " 的退款已处理完成。", "REFUND_APPROVED");
        } else if ("REJECT".equalsIgnoreCase(action)) {
            refund.status = RefundStatus.REJECTED;
            refundMapper.updateById(refund);
            order.status = refund.originalOrderStatus;
            orderMapper.updateById(order);
            notify(order.userId, "退款申请未通过", "订单 " + order.orderNo + " 的退款申请未通过。", "REFUND_REJECTED");
        } else {
            throw new BusinessException("审核动作只能是 APPROVE 或 REJECT");
        }
    }

    @Transactional
    public void review(String orderNo, Long userId, ReviewRequest request) {
        TravelOrder order = findByNo(orderNo);
        ensureOwner(order, userId);
        if (!OrderStatus.COMPLETED.equals(order.status)) {
            throw new BusinessException("行程完成后才可以评价");
        }
        Review existing = reviewMapper.selectOne(new QueryWrapper<Review>().eq("order_id", order.id));
        if (existing != null) {
            throw new BusinessException("每个订单只能评价一次");
        }
        Review review = new Review();
        review.orderId = order.id;
        review.userId = userId;
        review.routeId = order.routeId;
        review.rating = request.rating();
        review.content = request.content();
        review.status = "VISIBLE";
        reviewMapper.insert(review);
        refreshRouteRating(order.routeId);
    }

    public TravelOrder findByNo(String orderNo) {
        TravelOrder order = orderMapper.selectOne(new QueryWrapper<TravelOrder>().eq("order_no", orderNo));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    public Payment paymentFor(Long orderId) {
        Payment payment = paymentMapper.selectOne(new QueryWrapper<Payment>().eq("order_id", orderId));
        if (payment == null) {
            throw new BusinessException("订单支付记录不存在");
        }
        return payment;
    }

    private OrderDetailResponse toDetail(TravelOrder order) {
        List<TravelerView> travelers = orderTravelerMapper.selectList(new QueryWrapper<OrderTraveler>()
                        .eq("order_id", order.id).orderByAsc("id"))
                .stream()
                .map(snapshot -> new TravelerView(snapshot.id, snapshot.name, snapshot.gender, snapshot.birthDate,
                        snapshot.idType, maskId(snapshot.idNo), snapshot.phone, snapshot.emergencyName, snapshot.emergencyPhone))
                .toList();
        Refund refund = refundMapper.selectOne(new QueryWrapper<Refund>().eq("order_id", order.id)
                .orderByDesc("created_at").last("LIMIT 1"));
        Payment payment = paymentFor(order.id);
        payment.callbackPayload = null;
        return new OrderDetailResponse(order, routeMapper.selectById(order.routeId),
                departureMapper.selectById(order.departureId), travelers, payment, refund);
    }

    private void ensureOwner(TravelOrder order, Long userId) {
        if (!order.userId.equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }
    }

    private void releaseReserved(TravelOrder order) {
        releaseCapacity(order, OrderStatus.PAID_WAIT_CONFIRM);
    }

    private void releaseCapacity(TravelOrder order, String originalStatus) {
        int count = participants(order);
        if (OrderStatus.PAID_WAIT_CONFIRM.equals(originalStatus)) {
            departureMapper.update(null, new UpdateWrapper<Departure>()
                    .eq("id", order.departureId)
                    .setSql("reserved_people = GREATEST(COALESCE(reserved_people, 0) - " + count + ", 0)"));
        } else if (OrderStatus.CONFIRMED.equals(originalStatus) || OrderStatus.TRAVELLING.equals(originalStatus)) {
            departureMapper.update(null, new UpdateWrapper<Departure>()
                    .eq("id", order.departureId)
                    .setSql("confirmed_people = GREATEST(COALESCE(confirmed_people, 0) - " + count + ", 0)"));
        }
    }

    private void refreshRouteRating(Long routeId) {
        List<Review> reviews = reviewMapper.selectList(new QueryWrapper<Review>()
                .eq("route_id", routeId).eq("status", "VISIBLE"));
        TravelRoute route = routeMapper.selectById(routeId);
        if (route == null) {
            return;
        }
        route.ratingCount = reviews.size();
        route.ratingAvg = reviews.isEmpty() ? BigDecimal.ZERO : reviews.stream()
                .map(review -> BigDecimal.valueOf(review.rating))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
        routeMapper.updateById(route);
    }

    private void notify(Long userId, String title, String content, String type) {
        Message message = new Message();
        message.userId = userId;
        message.title = title;
        message.content = content;
        message.type = type;
        message.readFlag = 0;
        messageMapper.insert(message);
    }

    private static int participants(TravelOrder order) {
        return valueOrZero(order.adultCount) + valueOrZero(order.childCount);
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private static BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private static String generateOrderNo() {
        return "TA" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    public static String maskId(String idNo) {
        if (idNo == null || idNo.isBlank()) {
            return "";
        }
        if (idNo.length() <= 6) {
            return "******";
        }
        return idNo.substring(0, 3) + "***********" + idNo.substring(idNo.length() - 3);
    }
}
