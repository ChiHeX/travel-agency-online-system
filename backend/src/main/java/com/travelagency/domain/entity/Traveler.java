package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.time.LocalDate;

@TableName("user_traveler")
public class Traveler extends BaseEntity {
    public Long userId;
    public String name;
    public String gender;
    public LocalDate birthDate;
    public String idType;
    public String idNo;
    public String phone;
    public String emergencyName;
    public String emergencyPhone;
}
