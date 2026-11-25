package com.aicops.alert.controller;

import com.aicops.alert.entity.Alert;
import com.aicops.alert.repository.AlertRepository;
import com.aicops.alert.service.AlertService;
import com.aicops.common.api.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final AlertRepository alertRepository;

    /**
     * Alertmanager Webhook 入口（无需鉴权，阶段0 网关路由已允许）。
     */
    @PostMapping("/ingest")
    public Result<Void> ingest(@RequestBody Map<String, Object> webhook) {
        alertService.ingest(webhook);
        return Result.ok();
    }

    @GetMapping
    public Result<List<Alert>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity) {
        if (status != null && severity != null) {
            return Result.ok(alertRepository.findAll().stream()
                    .filter(a -> status.equals(a.getStatus()) && severity.equals(a.getSeverity()))
                    .toList());
        }
        return Result.ok(alertRepository.findAll());
    }

    @GetMapping("/{id}")
    public Result<Alert> get(@PathVariable Long id) {
        return Result.ok(alertRepository.findById(id).orElse(null));
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("alert-service up");
    }
}
