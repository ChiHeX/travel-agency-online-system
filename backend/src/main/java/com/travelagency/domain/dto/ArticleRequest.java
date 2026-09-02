package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ArticleRequest(
        @NotBlank(message = "攻略标题不能为空") String title,
        String summary,
        @NotBlank(message = "攻略内容不能为空") String content,
        String city,
        String destination,
        Long attractionId,
        String coverUrl,
        String status) {
}
