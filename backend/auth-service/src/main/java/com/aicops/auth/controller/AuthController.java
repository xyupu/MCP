package com.aicops.auth.controller;

import com.aicops.auth.dto.LoginRequest;
import com.aicops.auth.dto.LoginResponse;
import com.aicops.auth.dto.MeResponse;
import com.aicops.auth.entity.User;
import com.aicops.auth.repository.UserRepository;
import com.aicops.auth.service.AuthService;
import com.aicops.common.api.Result;
import com.aicops.common.exception.BizException;
import com.aicops.common.api.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "")
    public Result<MeResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = (Long) authentication.getDetails();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED));
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .toList();
        return Result.ok(new MeResponse(user.getId(), user.getUsername(), user.getTenantId(), roles));
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("auth-service up");
    }
}
