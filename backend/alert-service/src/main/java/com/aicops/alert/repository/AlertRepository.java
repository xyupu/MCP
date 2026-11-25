package com.aicops.alert.repository;

import com.aicops.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Optional<Alert> findByFingerprint(String fingerprint);
}
