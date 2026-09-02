package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("staff")
public class Staff extends BaseEntity {
    public Long userId;
    public String employeeNo;
    public String department;
    public String position;
}
