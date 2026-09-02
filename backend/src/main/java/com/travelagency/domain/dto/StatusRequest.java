package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record StatusRequest(@NotBlank(message = "状态不能为空") String status) {
}
