package com.aicops.agent.controller;

import com.aicops.agent.chat.ChatService;
import com.aicops.common.api.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {

    private final ChatService chatService;

    /**
     * 流式对话：SSE 返回。前端 fetch 读取流。
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "");
        if (message.isBlank()) {
            throw new IllegalArgumentException("message 不能为空");
        }
        SseEmitter emitter = new SseEmitter(0L); // 不设超时，由服务端控制
        chatService.streamChat(emitter, message);
        return emitter;
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("agent-service up");
    }
}
