package com.travelagency.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuideAccountRequest(
        @NotBlank(message = "导游账号不能为空") String username,
        @NotBlank(message = "初始密码不能为空") @Size(min = 8, max = 64, message = "密码长度应为 8-64 位") String password,
        @NotBlank(message = "导游姓名不能为空") String name,
        String phone,
        String intro) {
}
