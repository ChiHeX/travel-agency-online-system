package com.travelagency.common.api;

import java.util.UUID;

public record ApiResponse<T>(boolean success, String message, T data, String traceId) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "ok", data, UUID.randomUUID().toString());
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, UUID.randomUUID().toString());
    }
}
