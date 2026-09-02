package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("sys_user")
public class SysUser extends BaseEntity {
    public String username;
    public String passwordHash;
    public String nickname;
    public String realName;
    public String phone;
    public String email;
    public String avatar;
    public Integer status;
    public Integer deleted;
}
