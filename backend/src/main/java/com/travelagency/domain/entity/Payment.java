package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("payment")
public class Payment extends BaseEntity {
    public Long orderId;
    public String paymentNo;
    public String channel;
    public BigDecimal amount;
    public String status;
    public String thirdPartyTradeNo;
    public LocalDateTime paidAt;
    public String callbackPayload;
}
