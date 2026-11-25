package com.aicops.auth.dto;

import com.aicops.auth.entity.User;

import java.time.Instant;

/**
 * 用户信息响应（不含密码哈希等敏感字段）。
 */
public record UserResponse(
        Long id,
        Long tenantId,
        String username,
        String email,
        String phone,
        Integer status,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getTenantId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
