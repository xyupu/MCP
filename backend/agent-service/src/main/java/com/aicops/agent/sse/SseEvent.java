package com.aicops.agent.sse;

/**
 * SSE 事件负载，统一 JSON 序列化。
 */
public record SseEvent(String type, Object data) {
}
