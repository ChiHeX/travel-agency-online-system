package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("refund")
public class Refund extends BaseEntity {
    public Long orderId;
    public Long userId;
    public BigDecimal amount;
    public String reason;
    public String originalOrderStatus;
    public String status;
    public Long reviewedBy;
    public LocalDateTime reviewedAt;
    public String reviewComment;
}
