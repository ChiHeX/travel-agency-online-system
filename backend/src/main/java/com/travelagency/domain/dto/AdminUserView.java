package com.travelagency.domain.dto;

import java.time.LocalDateTime;

public record AdminUserView(
        Long id,
        String username,
        String nickname,
        String realName,
        String phone,
        String email,
        String avatar,
        Integer status,
        LocalDateTime createdAt) {
}
