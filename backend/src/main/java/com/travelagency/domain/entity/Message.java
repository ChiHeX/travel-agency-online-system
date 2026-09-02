package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.time.LocalDateTime;

@TableName("sys_message")
public class Message extends BaseEntity {
    public Long userId;
    public String type;
    public String title;
    public String content;
    public Integer readFlag;
    public LocalDateTime readAt;
}
