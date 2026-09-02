package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;

@TableName("attraction")
public class Attraction extends BaseEntity {
    public String name;
    public String city;
    public String address;
    public BigDecimal longitude;
    public BigDecimal latitude;
    public String intro;
    public String dataSource;
    public Integer status;
}
