package com.travelagency.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @Size(max = 32, message = "昵称不能超过 32 个字符") String nickname,
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
        @Email(message = "邮箱格式不正确") String email,
        String realName,
        String avatar) {
}
