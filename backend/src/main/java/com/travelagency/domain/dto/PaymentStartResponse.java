package com.travelagency.domain.dto;

import java.math.BigDecimal;

public record PaymentStartResponse(
        String orderNo,
        String channel,
        String status,
        BigDecimal amount,
        String callbackEndpoint,
        String notice) {
}
