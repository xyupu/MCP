package com.aicops.metrics.controller;

import com.aicops.common.api.Result;
import com.aicops.metrics.client.PrometheusClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final PrometheusClient prometheusClient;

    @GetMapping("/query")
    public Result<Map<String, Object>> instantQuery(
            @RequestParam String query,
            @RequestParam(required = false) String time) {
        return Result.ok(prometheusClient.instantQuery(query, time));
    }

    @GetMapping("/query_range")
    public Result<Map<String, Object>> rangeQuery(
            @RequestParam String query,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "15s") String step) {
        return Result.ok(prometheusClient.rangeQuery(query, start, end, step));
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("metrics-service up");
    }
}
