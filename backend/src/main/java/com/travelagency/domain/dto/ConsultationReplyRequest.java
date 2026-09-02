package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultationReplyRequest(
        @NotBlank(message = "回复内容不能为空") @Size(max = 2000, message = "回复内容不能超过 2000 字") String content) {
}
