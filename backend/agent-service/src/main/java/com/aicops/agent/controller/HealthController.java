package com.aicops.agent.controller;

import com.aicops.common.api.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agent")
public class HealthController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("agent-service up");
    }
}
