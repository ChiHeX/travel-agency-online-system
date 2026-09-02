package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("favorite")
public class Favorite extends BaseEntity {
    public Long userId;
    public Long routeId;
}
