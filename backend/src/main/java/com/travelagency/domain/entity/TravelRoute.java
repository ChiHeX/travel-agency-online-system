package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;

@TableName("travel_route")
public class TravelRoute extends BaseEntity {
    public String name;
    public String departureCity;
    public String destination;
    public Integer durationDays;
    public String description;
    public String coverUrl;
    public String included;
    public String excluded;
    public String bookingNotice;
    public String status;
    public BigDecimal ratingAvg;
    public Integer ratingCount;
    public Integer validBookingCount;
    public Long createdBy;
    public Integer deleted;
    @TableField(exist = false)
    public BigDecimal minAdultPrice;
}
