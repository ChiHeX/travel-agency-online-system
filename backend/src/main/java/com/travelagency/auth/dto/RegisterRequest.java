package com.travelagency.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 4, max = 32, message = "用户名长度应为 4-32 位")
        String username,
        @NotBlank(message = "密码不能为空")
        @Size(min = 8, max = 64, message = "密码长度应为 8-64 位")
        String password,
        @NotBlank(message = "昵称不能为空")
        @Size(max = 32, message = "昵称不能超过 32 个字符")
        String nickname,
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String phone,
        @Email(message = "邮箱格式不正确")
        String email) {
}
