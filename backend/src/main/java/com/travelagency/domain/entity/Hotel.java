package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;

@TableName("hotel")
public class Hotel extends BaseEntity {
    public String name;
    public String address;
    public String contactPhone;
    public BigDecimal longitude;
    public BigDecimal latitude;
    public String intro;
    public String dataSource;
    public Integer status;
}
