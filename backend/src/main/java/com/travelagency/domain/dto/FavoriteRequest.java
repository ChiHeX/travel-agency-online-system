package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotNull;

public record FavoriteRequest(@NotNull(message = "线路不能为空") Long routeId) {
}
