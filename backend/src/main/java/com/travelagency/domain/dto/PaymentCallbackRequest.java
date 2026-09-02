package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentCallbackRequest(
        @NotBlank String orderNo,
        @NotBlank String tradeNo,
        @NotBlank String result,
        @NotBlank String signature) {
}
