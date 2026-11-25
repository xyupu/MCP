package com.aicops.auth.service;

import com.aicops.auth.dto.LoginRequest;
import com.aicops.auth.dto.LoginResponse;
import com.aicops.auth.entity.Role;
import com.aicops.auth.entity.User;
import com.aicops.auth.repository.UserRepository;
import com.aicops.auth.security.JwtUtil;
import com.aicops.common.exception.BizException;
import com.aicops.common.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED.getCode(), "用户名或密码错误"));
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ErrorCode.FORBIDDEN.getCode(), "账号已被禁用");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BizException(ErrorCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        List<String> roles = user.getRoles().stream().map(Role::getCode).toList();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getTenantId(), roles);
        return new LoginResponse(token, "Bearer", jwtUtil.getExpireSeconds(), user.getId(), user.getUsername());
    }
}
