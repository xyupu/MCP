package com.aicops.mcphub.tool;

import com.aicops.mcphub.client.MetricsServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 运维 MCP 工具集。
 * 通过 @Tool 注解暴露为 MCP Server 的 tool，由 agent-service（MCP Client）发现并调用。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpsTools {

    private final MetricsServiceClient metricsServiceClient;

    @Tool(description = "Prometheus 即时查询（instant query）。输入 PromQL 表达式，返回当前时刻或指定时刻的指标值。" +
            "适用于查询 CPU 使用率、内存、错误率、QPS 等实时指标。")
    public String queryPrometheusInstant(
            @ToolParam(description = "PromQL 查询表达式，例如 up 或 rate(http_requests_total[5m])") String query,
            @ToolParam(description = "可选，查询时间点（RFC3339 或 Unix 时间戳）。不传则用当前时间。", required = false) String time) {
        log.info("MCP 工具调用 queryPrometheusInstant: query={}, time={}", query, time);
        Map<String, Object> result = metricsServiceClient.instantQuery(query, time);
        return toJson(result);
    }

    private String toJson(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
