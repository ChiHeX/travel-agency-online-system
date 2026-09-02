package com.travelagency.auth.dto;

import java.util.List;

public record UserView(
        Long id,
        String username,
        String nickname,
        String realName,
        String phone,
        String email,
        String avatar,
        List<String> roles) {
}
