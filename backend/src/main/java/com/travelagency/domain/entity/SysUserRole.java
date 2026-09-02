package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {
    public Long userId;
    public Long roleId;
}
