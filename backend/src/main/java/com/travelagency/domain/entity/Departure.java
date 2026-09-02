package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("departure")
public class Departure extends BaseEntity {
    public Long routeId;
    public LocalDate startDate;
    public LocalDate endDate;
    public BigDecimal adultPrice;
    public BigDecimal childPrice;
    public Integer maxPeople;
    public Integer reservedPeople;
    public Integer confirmedPeople;
    public Long guideId;
    public String status;
    public Integer version;
}
