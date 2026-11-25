package com.aicops.metrics.client;

import com.aicops.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Prometheus HTTP API 封装。
 * 仅做只读查询：instant / range。
 */
@Slf4j
@Component
public class PrometheusClient {

    private final RestClient restClient;
    private final String baseUrl;

    public PrometheusClient(@Value("${app.prometheus.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder().build();
    }

    /**
     * 即时查询：GET /api/v1/query?query=...&time=...
     */
    public Map<String, Object> instantQuery(String query, String time) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v1/query")
                .queryParam("query", query)
                .queryParam("time", time)
                .build()
                .toUriString();
        return doGet(uri);
    }

    /**
     * 区间查询：GET /api/v1/query_range?query=...&start=...&end=...&step=...
     */
    public Map<String, Object> rangeQuery(String query, String start, String end, String step) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v1/query_range")
                .queryParam("query", query)
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("step", step)
                .build()
                .toUriString();
        return doGet(uri);
    }

    private Map<String, Object> doGet(String uri) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(Map.class);
            return result;
        } catch (Exception e) {
            log.error("Prometheus 查询失败: uri={}", uri, e);
            throw new BizException(50001, "Prometheus 查询失败: " + e.getMessage());
        }
    }
}
