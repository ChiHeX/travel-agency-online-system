package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;

@TableName("route_itinerary_item")
public class RouteItineraryItem extends BaseEntity {
    public Long dayId;
    public Integer sortNo;
    public String itemType;
    public String name;
    public String description;
    public Long attractionId;
    public BigDecimal longitude;
    public BigDecimal latitude;
}
