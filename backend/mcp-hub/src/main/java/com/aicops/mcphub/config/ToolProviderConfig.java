package com.aicops.mcphub.config;

import com.aicops.mcphub.tool.OpsTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将 @Tool 注解的工具类注册为 ToolCallbackProvider，
 * 由 MCP Server 自动配置发现并暴露为 MCP 工具。
 */
@Configuration
public class ToolProviderConfig {

    @Bean
    public ToolCallbackProvider opsToolCallbackProvider(OpsTools opsTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(opsTools)
                .build();
    }
}
