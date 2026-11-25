package com.aicops.alert.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 告警实体。
 */
@Getter
@Setter
@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_alerts_status", columnList = "status"),
        @Index(name = "idx_alerts_severity", columnList = "severity"),
        @Index(name = "idx_alerts_fingerprint", columnList = "fingerprint")
})
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fingerprint;

    @Column(name = "alert_name", nullable = false)
    private String alertName;

    @Column(nullable = false)
    private String severity; // critical / warning / info

    @Column(nullable = false)
    private String status;   // firing / resolved

    private String source;
    private String namespace;
    private String service;
    private String instance;

    @Column(columnDefinition = "jsonb")
    private String labels;

    @Column(columnDefinition = "jsonb")
    private String annotations;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
