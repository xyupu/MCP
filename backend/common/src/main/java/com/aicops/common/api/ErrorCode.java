package com.aicops.common.api;

import lombok.Getter;

/**
 * 通用业务错误码。
 * 约定：5 位数字，前 2 位为 HTTP 语义段，后 3 位为细分。
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未认证或认证失败"),
    FORBIDDEN(40300, "无权限访问"),
    NOT_FOUND(40400, "资源不存在"),
    INTERNAL_ERROR(50000, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
