package com.aicops.alert.service;

import com.aicops.alert.entity.Alert;
import com.aicops.alert.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 告警处理：接收 Alertmanager Webhook，去重后入库。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public void ingest(Map<String, Object> webhook) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> alerts = (List<Map<String, Object>>) webhook.get("alerts");
        if (alerts == null) {
            log.warn("告警 Webhook 缺少 alerts 字段");
            return;
        }
        for (Map<String, Object> raw : alerts) {
            processAlert(raw);
        }
    }

    private void processAlert(Map<String, Object> raw) {
        @SuppressWarnings("unchecked")
        Map<String, String> labels = (Map<String, String>) raw.getOrDefault("labels", Map.of());
        @SuppressWarnings("unchecked")
        Map<String, String> annotations = (Map<String, String>) raw.getOrDefault("annotations", Map.of());

        String fingerprint = (String) raw.getOrDefault("fingerprint",
                labels.getOrDefault("alertname", "unknown") + ":" + labels.getOrDefault("instance", "unknown"));

        String status = (String) raw.get("status"); // firing / resolved

        alertRepository.findByFingerprint(fingerprint).ifPresentOrElse(
                existing -> update(existing, status, raw, annotations),
                () -> create(fingerprint, status, raw, labels, annotations)
        );
    }

    private void create(String fingerprint, String status, Map<String, Object> raw,
                        Map<String, String> labels, Map<String, String> annotations) {
        Alert alert = new Alert();
        alert.setFingerprint(fingerprint);
        alert.setAlertName(labels.getOrDefault("alertname", "unknown"));
        alert.setSeverity(labels.getOrDefault("severity", "warning"));
        alert.setStatus(normalizeStatus(status));
        alert.setSource(labels.getOrDefault("source", "prometheus"));
        alert.setNamespace(labels.get("namespace"));
        alert.setService(labels.get("service"));
        alert.setInstance(labels.get("instance"));
        alert.setLabels(toJson(labels));
        alert.setAnnotations(toJson(annotations));
        alert.setStartsAt(parseInstant((String) raw.get("startsAt")));
        alert.setEndsAt(parseInstant((String) raw.get("endsAt")));
        alertRepository.save(alert);
        log.info("新增告警: fingerprint={}, status={}", fingerprint, status);
    }

    private void update(Alert alert, String status, Map<String, Object> raw, Map<String, String> annotations) {
        alert.setStatus(normalizeStatus(status));
        alert.setAnnotations(toJson(annotations));
        if ("resolved".equals(status)) {
            alert.setEndsAt(Instant.now());
        }
        alertRepository.save(alert);
        log.info("更新告警: fingerprint={}, status={}", alert.getFingerprint(), status);
    }

    private String normalizeStatus(String status) {
        return "resolved".equals(status) ? "resolved" : "firing";
    }

    private Instant parseInstant(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
