package com.travelagency.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    @TableId(type = IdType.AUTO)
    public Long id;

    public LocalDateTime createdAt;

    public LocalDateTime updatedAt;
}
