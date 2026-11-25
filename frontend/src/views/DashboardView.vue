<template>
  <div class="app-page">
    <header class="page-head">
      <div>
        <h1 class="page-title">运维大屏</h1>
        <p class="page-sub">告警与实例状态总览</p>
      </div>
      <span class="page-meta tabular">{{ updatedAt }}</span>
    </header>

    <section class="stat-grid">
      <div class="stat">
        <div class="stat-label">告警总数</div>
        <div class="stat-value tabular">{{ total }}</div>
        <div class="stat-note">全部记录</div>
      </div>
      <div class="stat">
        <div class="stat-label">
          <span class="dot dot-live" aria-hidden="true"></span>
          活跃告警
        </div>
        <div class="stat-value accent tabular">{{ firing }}</div>
        <div class="stat-note">处于 firing 状态</div>
      </div>
      <div class="stat">
        <div class="stat-label">
          <span class="dot dot-negative" aria-hidden="true"></span>
          严重告警
        </div>
        <div class="stat-value tabular">{{ critical }}</div>
        <div class="stat-note">severity = critical</div>
      </div>
      <div class="stat">
        <div class="stat-label">
          <span class="dot dot-positive" aria-hidden="true"></span>
          已恢复
        </div>
        <div class="stat-value tabular">{{ resolved }}</div>
        <div class="stat-note">已标记 resolved</div>
      </div>
    </section>

    <section class="panel">
      <div class="panel-head">
        <h2 class="panel-title">最近告警</h2>
        <span class="panel-meta">{{ recent.length }} 条</span>
      </div>

      <div v-if="recent.length" class="rows">
        <div class="row row-head">
          <span>时间</span>
          <span>严重度</span>
          <span>告警</span>
          <span>服务</span>
          <span>实例</span>
        </div>
        <div v-for="a in recent" :key="a.id" class="row">
          <span class="mono muted tabular">{{ clock(a.startsAt) }}</span>
          <span class="sev">
            <span class="dot" :class="sevDot(a.severity)"></span>
            <span class="mono">{{ a.severity }}</span>
          </span>
          <span class="cell-strong">{{ a.alertName }}</span>
          <span class="mono muted">{{ a.service || '-' }}</span>
          <span class="mono muted">{{ a.instance || '-' }}</span>
        </div>
      </div>
      <div v-else class="empty-line">暂无告警记录</div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { listAlerts, type Alert } from '@/api/alert'

const alerts = ref<Alert[]>([])
const updatedAt = ref('')

const total = computed(() => alerts.value.length)
const firing = computed(() => alerts.value.filter((a) => a.status === 'firing').length)
const critical = computed(() => alerts.value.filter((a) => a.severity === 'critical').length)
const resolved = computed(() => alerts.value.filter((a) => a.status === 'resolved').length)

const recent = computed(() =>
  [...alerts.value]
    .sort((a, b) => (b.startsAt ?? '').localeCompare(a.startsAt ?? ''))
    .slice(0, 8)
)

function sevDot(severity: string) {
  if (severity === 'critical') return 'dot-negative'
  if (severity === 'warning') return 'dot-warning'
  return 'dot-muted'
}

function clock(t?: string) {
  if (!t) return '-'
  const d = new Date(t)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(async () => {
  updatedAt.value = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  try {
    alerts.value = await listAlerts()
  } catch {
    alerts.value = []
  }
})
</script>

<style scoped>
/* Four compact stats share one surface, separated by hairlines */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: var(--ink-card);
  border: 1px solid var(--ink-hairline);
  border-radius: 14px;
  overflow: hidden;
  margin-bottom: 24px;
}

.stat {
  padding: 16px 20px;
  border-left: 1px solid var(--ink-hairline);
}

.stat:first-child {
  border-left: 0;
}

.stat-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--ink-text-3);
}

.stat-value {
  margin-top: 8px;
  font-family: var(--font-mono);
  font-size: 28px;
  font-weight: 500;
  line-height: 1;
  letter-spacing: -0.01em;
}

.stat-value.accent {
  color: var(--accent);
}

.stat-note {
  margin-top: 8px;
  font-size: 12px;
  color: var(--ink-text-3);
}

/* Alert rows */
.rows {
  border-top: 1px solid var(--ink-hairline);
}

.row {
  display: grid;
  grid-template-columns: 80px 104px minmax(160px, 1fr) 120px 160px;
  align-items: center;
  gap: 16px;
  height: 44px;
  padding: 0 20px;
  border-top: 1px solid var(--ink-hairline);
  font-size: 13px;
  transition: background-color var(--dur-fast) var(--ease);
}

.row:first-child {
  border-top: 0;
}

.row:not(.row-head):hover {
  background: var(--ink-sunken);
}

.row-head {
  height: 36px;
  font-size: 12px;
  color: var(--ink-text-3);
}

.cell-strong {
  color: var(--ink-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sev {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--ink-text-2);
}

.row .muted {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 1024px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat:nth-child(3) {
    border-left: 0;
  }

  .stat:nth-child(n + 3) {
    border-top: 1px solid var(--ink-hairline);
  }

  .rows {
    overflow-x: auto;
  }

  .row {
    min-width: 720px;
  }
}
</style>
