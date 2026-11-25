<template>
  <div class="assistant app-page">
    <header class="page-head">
      <div>
        <h1 class="page-title">AI 助手</h1>
        <p class="page-sub">通过 MCP 调用指标与告警能力</p>
      </div>
      <span class="page-meta">{{ streaming ? '响应中' : '就绪' }}</span>
    </header>

    <section class="panel chat-panel">
      <div class="chat">
        <div class="message-list" ref="messageListRef">
          <div v-if="messages.length === 0" class="empty-state">
            <p class="empty-title">向 AI 助手提问</p>
            <p class="empty-line-text">例如：查询当前所有 up 状态的实例</p>
          </div>

          <div v-for="(msg, i) in messages" :key="i" class="message" :class="msg.role">
            <div class="bubble">
              <template v-if="msg.role === 'user'">
                <div class="content">{{ msg.content }}</div>
              </template>
              <template v-else>
                <div class="content">{{ msg.content }}</div>

                <div v-for="(tool, ti) in msg.tools" :key="ti" class="tool-call">
                  <div class="tool-call-header">
                    <span class="dot" :class="toolDot(tool.status)"></span>
                    <span class="tool-name mono">{{ tool.name }}</span>
                    <span class="tool-state mono">{{ tool.status === 'running' ? 'running' : tool.status === 'done' ? `${tool.durationMs}ms` : 'failed' }}</span>
                  </div>
                  <div class="tool-block">
                    <div class="tool-label mono">args</div>
                    <pre>{{ tool.args }}</pre>
                  </div>
                  <div v-if="tool.result" class="tool-block">
                    <div class="tool-label mono">result</div>
                    <pre>{{ truncate(tool.result) }}</pre>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="input"
            type="textarea"
            :rows="3"
            resize="none"
            placeholder="输入问题，Enter 发送，Shift+Enter 换行"
            :disabled="streaming"
            @keydown.enter.exact.prevent="send"
          />
          <el-button type="primary" :loading="streaming" class="send-btn" @click="send">
            {{ streaming ? '思考中' : '发送' }}
          </el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { streamChat } from '@/api/chat'

interface ToolCall {
  name: string
  args: string
  result: string
  durationMs: number
  status: 'running' | 'done' | 'error'
}

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  tools: ToolCall[]
}

const messages = ref<ChatMessage[]>([])
const input = ref('')
const streaming = ref(false)
const messageListRef = ref<HTMLElement>()

function toolDot(status: ToolCall['status']) {
  if (status === 'running') return 'dot-live'
  if (status === 'done') return 'dot-positive'
  return 'dot-negative'
}

function scrollToBottom() {
  nextTick(() => {
    messageListRef.value?.scrollTo({ top: messageListRef.value.scrollHeight, behavior: 'smooth' })
  })
}

function truncate(s: string, max = 500) {
  return s.length > max ? s.slice(0, max) + '…' : s
}

async function send() {
  const text = input.value.trim()
  if (!text || streaming.value) return
  input.value = ''

  messages.value.push({ role: 'user', content: text, tools: [] })
  const assistantMsg: ChatMessage = { role: 'assistant', content: '', tools: [] }
  messages.value.push(assistantMsg)
  scrollToBottom()

  streaming.value = true
  let currentTool: ToolCall | null = null

  await streamChat(text, {
    onMessage: (chunk) => {
      assistantMsg.content += chunk
      scrollToBottom()
    },
    onToolCall: (name, args) => {
      currentTool = { name, args, result: '', durationMs: 0, status: 'running' }
      assistantMsg.tools.push(currentTool)
      scrollToBottom()
    },
    onToolResult: (name, result, durationMs) => {
      if (currentTool && currentTool.name === name) {
        currentTool.result = result
        currentTool.durationMs = durationMs
        currentTool.status = 'done'
      } else {
        assistantMsg.tools.push({ name, args: '', result, durationMs, status: 'done' })
      }
      currentTool = null
      scrollToBottom()
    },
    onDone: () => {
      streaming.value = false
      scrollToBottom()
    },
    onError: (msg) => {
      streaming.value = false
      if (currentTool) {
        currentTool.status = 'error'
        currentTool.result = msg
        currentTool = null
      }
      ElMessage.error(msg)
      scrollToBottom()
    }
  })
}
</script>

<style scoped>
.assistant {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-panel {
  flex: 1;
  min-height: 0;
  display: flex;
}

.chat {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.message-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 24px;
}

/* Empty state: quiet, no illustration */
.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.empty-title {
  font-size: 14px;
  color: var(--ink-text-2);
}

.empty-line-text {
  font-size: 13px;
  color: var(--ink-text-3);
}

/* Messages */
.message {
  display: flex;
  margin-bottom: 16px;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: 76%;
  font-size: 14px;
  line-height: 1.65;
}

.message.user .bubble {
  background: var(--ink-raised);
  border: 1px solid var(--ink-hairline);
  color: var(--ink-text);
  padding: 10px 14px;
  border-radius: 12px;
}

.message.assistant .bubble {
  color: var(--ink-text);
  padding: 2px 0;
}

.content {
  white-space: pre-wrap;
  word-break: break-word;
}

/* Tool call card */
.tool-call {
  margin-top: 12px;
  padding: 12px 14px;
  background: var(--ink-sunken);
  border: 1px solid var(--ink-hairline);
  border-radius: 10px;
}

.tool-call-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.tool-name {
  font-size: 12px;
  color: var(--ink-text);
}

.tool-state {
  margin-left: auto;
  font-size: 12px;
  color: var(--ink-text-3);
}

.tool-block + .tool-block {
  margin-top: 8px;
}

.tool-label {
  font-size: 11px;
  color: var(--ink-text-3);
  margin-bottom: 4px;
}

.tool-block pre {
  margin: 0;
  padding: 8px 10px;
  background: var(--ink-page);
  border: 1px solid var(--ink-hairline);
  border-radius: 6px;
  font-family: var(--font-mono);
  font-size: 12px;
  line-height: 1.5;
  color: var(--ink-text-2);
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 160px;
  overflow-y: auto;
}

/* Input */
.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  padding: 16px 20px;
  border-top: 1px solid var(--ink-hairline);
}

.input-area :deep(.el-textarea__inner) {
  background-color: var(--ink-sunken);
  box-shadow: 0 0 0 1px var(--ink-hairline-strong) inset;
  border-radius: 8px;
  color: var(--ink-text);
  font-family: var(--font-ui);
  font-size: 14px;
  line-height: 1.5;
  padding: 10px 12px;
  transition: box-shadow var(--dur-fast) var(--ease);
}

.input-area :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px var(--accent) inset;
}

.send-btn {
  height: 40px;
  width: 88px;
  flex: none;
}
</style>
