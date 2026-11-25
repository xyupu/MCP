import { useAuthStore } from '@/stores/auth'

/**
 * 流式对话：通过 fetch 读取 SSE 流。
 * 事件回调：onMessage(文本增量)、onToolCall、onToolResult、onDone、onError。
 */
export interface ChatStreamHandlers {
  onMessage: (text: string) => void
  onToolCall: (name: string, args: string) => void
  onToolResult: (name: string, result: string, durationMs: number) => void
  onDone: () => void
  onError: (message: string) => void
}

export async function streamChat(message: string, handlers: ChatStreamHandlers) {
  const auth = useAuthStore()
  try {
    const response = await fetch('/api/v1/agent/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${auth.token}`
      },
      body: JSON.stringify({ message })
    })

    if (!response.ok || !response.body) {
      handlers.onError(`请求失败: HTTP ${response.status}`)
      return
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })

      // SSE 事件以空行分隔
      let idx: number
      while ((idx = buffer.indexOf('\n\n')) >= 0) {
        const rawEvent = buffer.slice(0, idx)
        buffer = buffer.slice(idx + 2)
        processEvent(rawEvent, handlers)
      }
    }
    // 处理剩余 buffer
    if (buffer.trim()) {
      processEvent(buffer, handlers)
    }
  } catch (e: any) {
    handlers.onError(e?.message || '网络错误')
  }
}

function processEvent(rawEvent: string, handlers: ChatStreamHandlers) {
  let eventType = ''
  let data = ''
  for (const line of rawEvent.split('\n')) {
    if (line.startsWith('event:')) {
      eventType = line.slice(6).trim()
    } else if (line.startsWith('data:')) {
      data += line.slice(5).trim()
    }
  }
  if (!eventType || !data) return

  try {
    const payload = JSON.parse(data)
    const inner = payload?.data ?? payload
    switch (eventType) {
      case 'message':
        handlers.onMessage(String(inner ?? ''))
        break
      case 'tool_call':
        handlers.onToolCall(inner?.name ?? '', inner?.arguments ?? '')
        break
      case 'tool_result':
        handlers.onToolResult(inner?.name ?? '', inner?.result ?? '', inner?.durationMs ?? 0)
        break
      case 'done':
        handlers.onDone()
        break
      case 'error':
        handlers.onError(String(inner ?? '未知错误'))
        break
    }
  } catch {
    // 忽略解析失败的事件
  }
}
