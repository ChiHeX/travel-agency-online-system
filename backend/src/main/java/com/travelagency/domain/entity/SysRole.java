package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("sys_role")
public class SysRole extends BaseEntity {
    public String code;
    public String name;
    public String description;
}
