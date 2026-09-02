package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminDecisionRequest(
        @NotBlank(message = "审核结果不能为空") String action,
        String comment) {
}
