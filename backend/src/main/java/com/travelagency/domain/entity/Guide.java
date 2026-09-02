package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("guide")
public class Guide extends BaseEntity {
    public Long userId;
    public String name;
    public String phone;
    public String intro;
    public String status;
}
