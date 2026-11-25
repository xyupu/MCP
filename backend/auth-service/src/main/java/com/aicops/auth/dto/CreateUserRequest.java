package com.aicops.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增用户请求。
 */
public record CreateUserRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 64, message = "用户名长度不能超过64")
        String username,

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 64, message = "密码长度需在6到64之间")
        String password,

        Long tenantId,

        @Email(message = "邮箱格式不正确")
        @Size(max = 128, message = "邮箱长度不能超过128")
        String email,

        @Size(max = 32, message = "手机号长度不能超过32")
        String phone,

        Integer status
) {
}
