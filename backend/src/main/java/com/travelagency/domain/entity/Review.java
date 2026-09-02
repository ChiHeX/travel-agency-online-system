package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("review")
public class Review extends BaseEntity {
    public Long orderId;
    public Long userId;
    public Long routeId;
    public Integer rating;
    public String content;
    public String status;
}
