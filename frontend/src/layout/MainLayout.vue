<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark" aria-hidden="true"></span>
        <span class="brand-name">AIOps 助手</span>
      </div>

      <nav class="nav">
        <router-link to="/dashboard" class="nav-item" active-class="is-active">
          <el-icon class="nav-icon"><Odometer /></el-icon>
          <span>运维大屏</span>
        </router-link>
        <router-link to="/assistant" class="nav-item" active-class="is-active">
          <el-icon class="nav-icon"><ChatDotRound /></el-icon>
          <span>AI 助手</span>
        </router-link>
        <router-link to="/alerts" class="nav-item" active-class="is-active">
          <el-icon class="nav-icon"><Bell /></el-icon>
          <span>告警中心</span>
        </router-link>
      </nav>

      <div class="sidebar-foot">
        <span class="mono">console</span>
      </div>
    </aside>

    <div class="workspace">
      <header class="topbar">
        <span class="topbar-meta mono">{{ currentPath }}</span>
        <div class="topbar-right">
          <span class="user">{{ auth.username }}</span>
          <button type="button" class="text-btn" @click="handleLogout">退出</button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Odometer, ChatDotRound, Bell } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const currentPath = computed(() => {
  const map: Record<string, string> = {
    '/dashboard': 'overview',
    '/assistant': 'assistant',
    '/alerts': 'alerts'
  }
  return map[route.path] ?? 'console'
})

function handleLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.shell {
  display: flex;
  height: 100vh;
  background: var(--ink-page);
}

/* ---- Sidebar ---- */
.sidebar {
  width: 248px;
  flex: none;
  display: flex;
  flex-direction: column;
  background: var(--ink-deepest);
  border-right: 1px solid var(--ink-hairline);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 56px;
  padding: 0 20px;
  border-bottom: 1px solid var(--ink-hairline);
}

.brand-mark {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  background: var(--accent);
  flex: none;
}

.brand-name {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: -0.01em;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 12px 12px 0;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  height: 36px;
  padding: 0 12px;
  border-radius: 8px;
  color: var(--ink-text-2);
  font-size: 14px;
  text-decoration: none;
  transition:
    background-color var(--dur-fast) var(--ease),
    color var(--dur-fast) var(--ease);
}

.nav-item:hover {
  background: var(--ink-card);
  color: var(--ink-text);
}

.nav-item.is-active {
  background: var(--ink-raised);
  color: var(--ink-text);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);
}

.nav-item.is-active::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 10px;
  bottom: 10px;
  width: 2px;
  border-radius: 2px;
  background: var(--accent);
}

.nav-icon {
  font-size: 16px;
  color: var(--ink-text-3);
}

.nav-item.is-active .nav-icon,
.nav-item:hover .nav-icon {
  color: var(--ink-text-2);
}

.sidebar-foot {
  margin-top: auto;
  padding: 16px 20px;
  border-top: 1px solid var(--ink-hairline);
  font-size: 12px;
  color: var(--ink-text-3);
}

/* ---- Workspace ---- */
.workspace {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  flex: none;
  padding: 0 32px;
  border-bottom: 1px solid var(--ink-hairline);
  background: var(--ink-page);
}

.topbar-meta {
  font-size: 12px;
  color: var(--ink-text-3);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user {
  font-size: 13px;
  color: var(--ink-text-2);
}

.text-btn {
  border: 0;
  background: none;
  padding: 4px 0;
  font-size: 13px;
  font-family: inherit;
  color: var(--ink-text-3);
  cursor: pointer;
  transition: color var(--dur-fast) var(--ease);
}

.text-btn:hover {
  color: var(--ink-text);
}

.content {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 24px 32px 32px;
}
</style>
