package com.aicops.auth.config;

import com.aicops.auth.entity.User;
import com.aicops.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 阶段0 演示：启动时预置默认租户与管理员账号（admin / admin123）。
 * 完整 RBAC 与租户管理在阶段1 实现。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setTenantId(1L);
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@aicops.local");
            admin.setStatus(1);
            userRepository.save(admin);
            log.info("已创建默认管理员账号 admin / admin123");
        }
    }
}
