package com.aicops.agent.chat;

import com.aicops.agent.tool.ObservableToolCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * 对话服务：流式调用 DeepSeek，并将 MCP 工具调用过程通过 SSE 透出。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final SyncMcpToolCallbackProvider mcpToolProvider;
    private final SseEventPublisher publisher;

    public void streamChat(SseEmitter emitter, String message) {
        try {
            // 包装 MCP 工具，使其可观测（发出 tool_call/tool_result 事件）
            ToolCallback[] rawCallbacks = mcpToolProvider.getToolCallbacks();
            List<ToolCallback> observable = Arrays.stream(rawCallbacks)
                    .map(cb -> (ToolCallback) new ObservableToolCallback(cb, publisher, emitter))
                    .toList();

            Flux<String> contentFlux = chatClientBuilder.build()
                    .prompt()
                    .user(message)
                    .tools(observable)
                    .stream()
                    .content();

            contentFlux.subscribe(
                    chunk -> publisher.message(emitter, chunk),
                    error -> {
                        log.error("流式对话失败", error);
                        publisher.error(emitter, "对话失败: " + error.getMessage());
                    },
                    () -> publisher.done(emitter)
            );
        } catch (Exception e) {
            log.error("发起对话失败", e);
            publisher.error(emitter, "对话失败: " + e.getMessage());
        }
    }
}
