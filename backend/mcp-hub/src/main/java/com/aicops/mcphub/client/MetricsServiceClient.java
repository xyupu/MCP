package com.aicops.mcphub.client;

import com.aicops.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * 调用 metrics-service 的 REST 接口。
 */
@Slf4j
@Component
public class MetricsServiceClient {

    private final RestClient restClient;
    private final String baseUrl;

    public MetricsServiceClient(@Value("${app.metrics.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder().build();
    }

    public Map<String, Object> instantQuery(String query, String time) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/metrics/query")
                            .queryParam("query", query)
                            .queryParam("time", time)
                            .build())
                    .retrieve()
                    .body(Map.class);
            return result;
        } catch (Exception e) {
            log.error("调用 metrics-service 失败", e);
            throw new BizException(50002, "指标查询失败: " + e.getMessage());
        }
    }
}
