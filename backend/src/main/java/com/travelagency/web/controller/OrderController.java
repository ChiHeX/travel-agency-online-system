package com.travelagency.web.controller;

import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.security.CurrentUser;
import com.travelagency.domain.dto.CreateOrderRequest;
import com.travelagency.domain.dto.OrderDetailResponse;
import com.travelagency.domain.dto.PaymentStartResponse;
import com.travelagency.domain.dto.RefundRequest;
import com.travelagency.domain.dto.ReviewRequest;
import com.travelagency.domain.entity.TravelOrder;
import com.travelagency.domain.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<TravelOrder> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.create(CurrentUser.required().userId(), request));
    }

    @GetMapping
    public ApiResponse<List<TravelOrder>> mine(@RequestParam(required = false) String status) {
        return ApiResponse.ok(orderService.listMine(CurrentUser.required().userId(), status));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<OrderDetailResponse> detail(@PathVariable String orderNo) {
        return ApiResponse.ok(orderService.detail(orderNo, CurrentUser.required()));
    }

    @PostMapping("/{orderNo}/pay")
    public ApiResponse<PaymentStartResponse> pay(@PathVariable String orderNo) {
        return ApiResponse.ok(orderService.startPayment(orderNo, CurrentUser.required().userId()));
    }

    @PostMapping("/{orderNo}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String orderNo) {
        orderService.cancel(orderNo, CurrentUser.required().userId());
        return ApiResponse.ok();
    }

    @PostMapping("/{orderNo}/refunds")
    public ApiResponse<Void> refund(@PathVariable String orderNo, @Valid @RequestBody RefundRequest request) {
        orderService.applyRefund(orderNo, CurrentUser.required().userId(), request);
        return ApiResponse.ok();
    }

    @PostMapping("/{orderNo}/reviews")
    public ApiResponse<Void> review(@PathVariable String orderNo, @Valid @RequestBody ReviewRequest request) {
        orderService.review(orderNo, CurrentUser.required().userId(), request);
        return ApiResponse.ok();
    }
}
