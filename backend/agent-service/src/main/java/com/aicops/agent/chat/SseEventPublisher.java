package com.aicops.agent.chat;

import com.aicops.agent.sse.SseEvent;
import com.aicops.agent.sse.SseEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * SSE 事件发布器：向单个 SseEmitter 推送事件。
 * 同时写日志，便于本地调试观察工具调用链。
 */
@Slf4j
@Component
public class SseEventPublisher {

    public void emit(SseEmitter emitter, String type, Object data) {
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name(type).data(new SseEvent(type, data)));
        } catch (IOException e) {
            log.warn("SSE 推送失败: type={}", type, e);
        }
    }

    public void message(SseEmitter emitter, String text) {
        emit(emitter, SseEventType.MESSAGE, text);
    }

    public void toolCall(SseEmitter emitter, String toolName, String arguments) {
        log.info("[tool_call] name={} args={}", toolName, arguments);
        emit(emitter, SseEventType.TOOL_CALL, java.util.Map.of("name", toolName, "arguments", arguments));
    }

    public void toolResult(SseEmitter emitter, String toolName, String result, long durationMs) {
        log.info("[tool_result] name={} durationMs={} result={}", toolName, durationMs, result);
        emit(emitter, SseEventType.TOOL_RESULT,
                java.util.Map.of("name", toolName, "result", result, "durationMs", durationMs));
    }

    public void done(SseEmitter emitter) {
        emit(emitter, SseEventType.DONE, "ok");
        if (emitter != null) {
            emitter.complete();
        }
    }

    public void error(SseEmitter emitter, String message) {
        emit(emitter, SseEventType.ERROR, message);
        if (emitter != null) {
            emitter.complete();
        }
    }
}
