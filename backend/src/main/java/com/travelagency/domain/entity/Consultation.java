package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("consultation")
public class Consultation extends BaseEntity {
    public Long userId;
    public String title;
    public String content;
    public String status;
}
