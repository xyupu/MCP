# AIOps 智能运维助手（DevOps-AIOps-Assistant）

基于 **Vue 3 + Java(Spring Boot/Spring Cloud) + MCP + DeepSeek** 的智能运维助手项目。
方向：告警分析 + 工单自动化。

> 项目规划文档见 [docs/DevOps-AIOps-Assistant-项目规划.md](docs/DevOps-AIOps-Assistant-项目规划.md)。

## 目录结构

```
.
├── backend/     # Java 后端（Maven 多模块）
│   ├── pom.xml          # 父 POM
│   ├── common/          # 通用模块：统一响应/异常/日志
│   ├── gateway/         # API 网关（8080）
│   ├── auth-service/    # 认证服务（8081，JWT + RBAC）
│   ├── agent-service/   # AI 编排（8082，DeepSeek + MCP Client + SSE）
│   ├── alert-service/   # 告警服务（8083，Alertmanager Webhook）
│   ├── incident-service/# 工单服务（8084，阶段3实现）
│   ├── mcp-hub/         # MCP 工具网关（8085，MCP Server）
│   └── metrics-service/ # 指标查询（8086，Prometheus 封装）
├── frontend/    # Vue 3 + TS + Element Plus 前端（5173）
├── infra/       # 基础设施 Docker Compose + 监控配置
└── docs/        # 规划文档
```

## 环境要求

| 工具 | 版本 |
|---|---|
| JDK | 21 |
| Maven | 3.9+ |
| Node.js | 20+ |
| Docker / Docker Compose | 24+ |
| DeepSeek API Key | 需设置环境变量 `DEEPSEEK_API_KEY` |

## 快速启动

### 1. 启动基础设施

```bash
cd infra
docker compose up -d
```

| 服务 | 地址 |
|---|---|
| PostgreSQL | localhost:5432（库 aicops / 账号 aicops / 密码 aicops） |
| RabbitMQ 管理台 | http://localhost:15672（aicops/aicops） |
| Prometheus | http://localhost:9090 |
| Alertmanager | http://localhost:9093 |
| Loki | http://localhost:3100 |
| Grafana | http://localhost:3000（admin/admin） |

### 2. 启动后端

> **重要**：本机 `.m2` 仓库若不可写（或想隔离依赖），用工作区内仓库：
> ```powershell
> $env:MAVEN_OPTS="-Dmaven.repo.local=D:\Workspase\test\.m2repo"
> ```
> 或在每条 `mvn` 命令后追加 `-Dmaven.repo.local=D:\Workspase\test\.m2repo`。

```bash
cd backend
mvn -pl common install -DskipTests   # 先安装 common 模块
```

然后分别启动各服务（阶段1 需要同时启动以下服务）：

```bash
# 认证服务（8081）
mvn -pl auth-service spring-boot:run

# 指标查询服务（8086）
mvn -pl metrics-service spring-boot:run

# MCP 工具网关（8085，MCP Server）
mvn -pl mcp-hub spring-boot:run

# AI 编排服务（8082，MCP Client + DeepSeek）
# 需设置环境变量 DEEPSEEK_API_KEY
mvn -pl agent-service spring-boot:run

# 告警服务（8083）
mvn -pl alert-service spring-boot:run

# API 网关（8080，前端统一入口）
mvn -pl gateway spring-boot:run
```

> Windows PowerShell 设置环境变量：`$env:DEEPSEEK_API_KEY="sk-xxx"`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

> 若 PowerShell 报"禁止运行脚本"，用 `npm.cmd install` 或改执行策略。

访问 http://localhost:5173 ，默认账号 `admin / admin123`。

## 核心链路（阶段1 验收）

```
前端 AI 助手页
  │ POST /api/v1/agent/chat（SSE）
  ▼
gateway(8080) → agent-service(8082)
  │ MCP Client（Spring AI）
  ▼
mcp-hub(8085, MCP Server)
  │ query_prometheus_instant 工具
  ▼
metrics-service(8086) → Prometheus(9090)
```

## 阶段 1 验收清单

- [ ] `docker compose up -d` 基础设施全部健康
- [ ] auth-service 启动，Flyway 建表（含 roles/permissions），admin/admin123 登录返回含角色 ADMIN 的 JWT
- [ ] `GET /api/v1/auth/me`（带 JWT）返回用户信息与角色
- [ ] metrics-service `/api/v1/metrics/query?query=up` 返回 Prometheus 数据
- [ ] mcp-hub 启动，MCP Server 暴露 `queryPrometheusInstant` 工具（SSE 端点 `/sse`）
- [ ] agent-service 启动，能连接 mcp-hub（MCP Client），日志显示发现工具
- [ ] 前端 AI 助手页提问"查询 up 指标"，流式输出 + 工具调用链可视化（tool_call/tool_result）
- [ ] alert-service `/api/v1/alerts/ingest` 能接收 Alertmanager Webhook 并入库
- [ ] 前端告警中心能展示告警列表，大屏能显示统计数字

## 端口分配

| 服务 | 端口 |
|---|---|
| gateway | 8080 |
| auth-service | 8081 |
| agent-service | 8082 |
| alert-service | 8083 |
| incident-service | 8084 |
| mcp-hub | 8085 |
| metrics-service | 8086 |
| 前端 Vite | 5173 |

## 阶段进度

- [x] 阶段 0：脚手架（common/gateway/骨架服务/前端骨架/infra）
- [x] 阶段 1：MVP 打通（RBAC + 告警接入 + DeepSeek + MCP 工具端到端 + 前端页面）
- [ ] 阶段 2：微服务深化（gRPC + RabbitMQ + log/k8s-service + Chaos Mesh）
- [ ] 阶段 3：工单 + 自动化 + 审批
- [ ] 阶段 4：知识库 RAG
- [ ] 阶段 5：可观测 + K8s 部署
