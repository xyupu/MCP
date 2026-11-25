package com.aicops.auth.controller;

import com.aicops.auth.dto.CreateUserRequest;
import com.aicops.auth.dto.UserResponse;
import com.aicops.auth.service.UserService;
import com.aicops.common.api.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理接口。路径前缀与认证接口一致：/api/v1/auth。
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "新增用户")
    public Result<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return Result.ok(userService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息")
    public Result<UserResponse> get(@PathVariable("id") Long id) {
        return Result.ok(userService.get(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return Result.ok();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public Result<UserResponse> update(@PathVariable("id") Long id,
                                       @RequestBody Map<String, Object> updates) {
        return Result.ok(userService.update(id, updates));
    }
}
