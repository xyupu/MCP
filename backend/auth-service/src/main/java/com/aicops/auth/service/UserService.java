package com.aicops.auth.service;

import com.aicops.auth.dto.CreateUserRequest;
import com.aicops.auth.dto.UserResponse;
import com.aicops.auth.entity.User;
import com.aicops.auth.repository.UserRepository;
import com.aicops.common.api.ErrorCode;
import com.aicops.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 用户管理：新增、查询、删除。
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new BizException(ErrorCode.CONFLICT.getCode(), "用户名已存在");
        });

        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setTenantId(request.tenantId() == null ? 1L : request.tenantId());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status() == null ? 1 : request.status());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(findById(id));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND.getCode(), "用户不存在"));
    }

    @Transactional
    public UserResponse update(Long id, Map<String, Object> updates) {
        User user = findById(id);

        for (Map.Entry<String, Object> e : updates.entrySet()) {
            String field = e.getKey();
            Object value = e.getValue();
            switch (field) {
                case "tenantId" -> {
                    requireNonNull(value, field);
                    user.setTenantId(((Number) value).longValue());
                }
                case "username" -> {
                    String username = requireText(value, field);
                    userRepository.findByUsername(username)
                            .filter(other -> !other.getId().equals(id))
                            .ifPresent(other -> {
                                throw new BizException(ErrorCode.CONFLICT.getCode(), "用户名已存在");
                            });
                    user.setUsername(username);
                }
                case "password" -> {
                    String raw = requireText(value, field);
                    user.setPasswordHash(passwordEncoder.encode(raw));
                }
                case "email" -> user.setEmail(value == null ? null : value.toString());
                case "phone" -> user.setPhone(value == null ? null : value.toString());
                case "status" -> {
                    requireNonNull(value, field);
                    user.setStatus(((Number) value).intValue());
                }
                default -> throw new BizException(ErrorCode.BAD_REQUEST.getCode(), "不允许更新的字段: " + field);
            }
        }
        return UserResponse.from(user);
    }

    // 辅助方法
    private void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new BizException(ErrorCode.BAD_REQUEST.getCode(), field + " 不能为空");
        }
    }

    private String requireText(Object value, String field) {
        requireNonNull(value, field);
        String text = value.toString().trim();
        if (text.isEmpty()) {
            throw new BizException(ErrorCode.BAD_REQUEST.getCode(), field + " 不能为空");
        }
        return text;
    }
}
