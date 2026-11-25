package com.aicops.auth.dto;

public record MeResponse(
        Long userId,
        String username,
        Long tenantId,
        java.util.List<String> roles
) {
}
