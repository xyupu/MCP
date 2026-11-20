package com.aicops.auth.controller;

import com.aicops.auth.dto.LoginRequest;
import com.aicops.auth.dto.LoginResponse;
import com.aicops.auth.service.AuthService;
import com.aicops.common.api.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("auth-service up");
    }
}
