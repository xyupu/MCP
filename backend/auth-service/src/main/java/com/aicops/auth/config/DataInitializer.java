package com.aicops.auth.config;

import com.aicops.auth.entity.Role;
import com.aicops.auth.entity.User;
import com.aicops.auth.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 阶段0 演示：启动时预置默认租户与管理员账号（admin / admin123），并赋予 ADMIN 角色。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }
        Role adminRole = entityManager
                .createQuery("select r from Role r where r.code = :code", Role.class)
                .setParameter("code", "ADMIN")
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("ADMIN 角色不存在，请确认 V2__rbac.sql 已执行"));

        User admin = new User();
        admin.setTenantId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@aicops.local");
        admin.setStatus(1);
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);
        log.info("已创建默认管理员账号 admin / admin123（角色 ADMIN）");
    }
}
