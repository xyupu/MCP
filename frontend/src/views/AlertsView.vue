<template>
  <div class="app-page">
    <header class="page-head">
      <div>
        <h1 class="page-title">告警中心</h1>
        <p class="page-sub">来自 Prometheus Alertmanager 的告警</p>
      </div>
      <span class="page-meta tabular">{{ alerts.length }} 条</span>
    </header>

    <section class="panel">
      <div v-if="alerts.length" class="table-wrap">
        <div class="grid-row grid-head">
          <span>时间</span>
          <span>严重度</span>
          <span>状态</span>
          <span>告警名称</span>
          <span>服务</span>
          <span>实例</span>
          <span>来源</span>
        </div>

        <div v-for="a in alerts" :key="a.id" class="grid-row">
          <span class="mono muted tabular">{{ stamp(a.startsAt) }}</span>
          <span class="status">
            <span class="dot" :class="sevDot(a.severity)"></span>
            <span class="mono">{{ a.severity }}</span>
          </span>
          <span class="status">
            <span class="dot" :class="a.status === 'firing' ? 'dot-negative' : 'dot-positive'"></span>
            <span>{{ a.status === 'firing' ? '告警中' : '已恢复' }}</span>
          </span>
          <span class="cell-strong">{{ a.alertName }}</span>
          <span class="mono muted">{{ a.service || '-' }}</span>
          <span class="mono muted">{{ a.instance || '-' }}</span>
          <span class="mono muted">{{ a.source || '-' }}</span>
        </div>
      </div>

      <div v-else class="empty-line">
        {{ loading ? '加载中' : '暂无告警记录' }}
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listAlerts, type Alert } from '@/api/alert'

const alerts = ref<Alert[]>([])
const loading = ref(false)

function sevDot(severity: string) {
  if (severity === 'critical') return 'dot-negative'
  if (severity === 'warning') return 'dot-warning'
  return 'dot-muted'
}

function stamp(t?: string) {
  if (!t) return '-'
  const d = new Date(t)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

async function load() {
  loading.value = true
  try {
    alerts.value = await listAlerts()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.table-wrap {
  overflow-x: auto;
}

.grid-row {
  display: grid;
  grid-template-columns: 104px 104px 100px minmax(180px, 1fr) 120px 170px 110px;
  align-items: center;
  gap: 16px;
  min-width: 900px;
  height: 44px;
  padding: 0 20px;
  border-top: 1px solid var(--ink-hairline);
  font-size: 13px;
  transition: background-color var(--dur-fast) var(--ease);
}

.grid-head {
  height: 36px;
  border-top: 0;
  font-size: 12px;
  color: var(--ink-text-3);
}

.grid-row:not(.grid-head):hover {
  background: var(--ink-sunken);
}

.cell-strong {
  color: var(--ink-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--ink-text-2);
}

.grid-row .muted {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
