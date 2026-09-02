package com.travelagency.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travelagency.common.enums.DepartureStatus;
import com.travelagency.common.enums.OrderStatus;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.domain.entity.Departure;
import com.travelagency.domain.entity.TravelOrder;
import com.travelagency.domain.mapper.DepartureMapper;
import com.travelagency.domain.mapper.TravelOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartureService {

    private final DepartureMapper departureMapper;
    private final TravelOrderMapper orderMapper;

    public DepartureService(DepartureMapper departureMapper, TravelOrderMapper orderMapper) {
        this.departureMapper = departureMapper;
        this.orderMapper = orderMapper;
    }

    public List<Departure> list(Long routeId, String status) {
        QueryWrapper<Departure> query = new QueryWrapper<>();
        if (routeId != null) {
            query.eq("route_id", routeId);
        }
        if (status != null && !status.isBlank()) {
            query.eq("status", status);
        }
        return departureMapper.selectList(query.orderByAsc("start_date"));
    }

    @Transactional
    public Departure save(Departure departure) {
        if (departure.startDate == null || departure.endDate == null || departure.endDate.isBefore(departure.startDate)) {
            throw new BusinessException("团期日期不合法");
        }
        if (departure.maxPeople == null || departure.maxPeople <= 0) {
            throw new BusinessException("最大人数必须大于 0");
        }
        if (departure.adultPrice == null || departure.adultPrice.signum() < 0
                || departure.childPrice == null || departure.childPrice.signum() < 0) {
            throw new BusinessException("团期价格不能为负数");
        }
        if (departure.status == null || departure.status.isBlank()) {
            departure.status = DepartureStatus.DRAFT;
        }
        if (departure.reservedPeople == null) {
            departure.reservedPeople = 0;
        }
        if (departure.confirmedPeople == null) {
            departure.confirmedPeople = 0;
        }
        checkGuideConflict(departure);
        if (departure.id == null) {
            departure.version = 0;
            departureMapper.insert(departure);
        } else {
            departureMapper.updateById(departure);
        }
        return departure;
    }

    @Transactional
    public void changeStatus(Long departureId, String status) {
        Departure departure = departureMapper.selectById(departureId);
        if (departure == null) {
            throw new BusinessException(404, "团期不存在");
        }
        if (!List.of(DepartureStatus.DRAFT, DepartureStatus.OPEN, DepartureStatus.FULL,
                DepartureStatus.CLOSED, DepartureStatus.TRAVELLING, DepartureStatus.FINISHED,
                DepartureStatus.CANCELLED).contains(status)) {
            throw new BusinessException("团期状态不合法");
        }
        departure.status = status;
        departureMapper.updateById(departure);
        if (DepartureStatus.TRAVELLING.equals(status)) {
            orderMapper.update(null, new UpdateWrapper<TravelOrder>()
                    .eq("departure_id", departureId).eq("status", OrderStatus.CONFIRMED)
                    .set("status", OrderStatus.TRAVELLING));
        } else if (DepartureStatus.FINISHED.equals(status)) {
            orderMapper.update(null, new UpdateWrapper<TravelOrder>()
                    .eq("departure_id", departureId)
                    .in("status", OrderStatus.CONFIRMED, OrderStatus.TRAVELLING)
                    .set("status", OrderStatus.COMPLETED).set("completed_at", LocalDateTime.now()));
        }
    }

    private void checkGuideConflict(Departure departure) {
        if (departure.guideId == null) {
            return;
        }
        QueryWrapper<Departure> query = new QueryWrapper<Departure>()
                .eq("guide_id", departure.guideId)
                .notIn("status", DepartureStatus.CANCELLED, DepartureStatus.FINISHED)
                .le("start_date", departure.endDate)
                .ge("end_date", departure.startDate);
        if (departure.id != null) {
            query.ne("id", departure.id);
        }
        if (departureMapper.selectCount(query) > 0) {
            throw new BusinessException("该导游在此时间范围内已有其他团期");
        }
    }
}
