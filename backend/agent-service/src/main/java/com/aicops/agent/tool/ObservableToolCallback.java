package com.aicops.agent.tool;

import com.aicops.agent.chat.SseEventPublisher;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolMetadata;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 工具回调装饰器：在真实工具调用前后发出 SSE 事件，让前端可视化工具调用链。
 */
public class ObservableToolCallback implements ToolCallback {

    private final ToolCallback delegate;
    private final SseEventPublisher publisher;
    private final SseEmitter emitter;

    public ObservableToolCallback(ToolCallback delegate, SseEventPublisher publisher, SseEmitter emitter) {
        this.delegate = delegate;
        this.publisher = publisher;
        this.emitter = emitter;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        return call(toolInput, null);
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        String name = delegate.getToolDefinition().name();
        long start = System.currentTimeMillis();
        publisher.toolCall(emitter, name, toolInput);
        try {
            String result = delegate.call(toolInput, toolContext);
            publisher.toolResult(emitter, name, result, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            publisher.toolResult(emitter, name, "ERROR: " + e.getMessage(), System.currentTimeMillis() - start);
            throw e;
        }
    }
}
