package com.aicops.agent.sse;

/**
 * SSE 事件类型（与前端约定）。
 */
public final class SseEventType {
    public static final String MESSAGE = "message";          // 文本增量
    public static final String TOOL_CALL = "tool_call";      // 工具调用开始
    public static final String TOOL_RESULT = "tool_result";  // 工具调用结束
    public static final String DONE = "done";                // 结束
    public static final String ERROR = "error";              // 错误

    private SseEventType() {
    }
}
