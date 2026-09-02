package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("travel_order")
public class TravelOrder extends BaseEntity {
    public String orderNo;
    public Long userId;
    public Long routeId;
    public Long departureId;
    public String contactName;
    public String contactPhone;
    public String contactEmail;
    public Integer adultCount;
    public Integer childCount;
    public BigDecimal adultUnitPrice;
    public BigDecimal childUnitPrice;
    public BigDecimal totalAmount;
    public String status;
    public String paymentStatus;
    public LocalDateTime paidAt;
    public LocalDateTime confirmedAt;
    public LocalDateTime cancelledAt;
    public LocalDateTime completedAt;
    public String remark;
}
