<template>
  <div class="dashboard">
    <el-header class="header">
      <span class="title">运维大屏</span>
      <span class="user">{{ auth.username }}</span>
      <el-button size="small" @click="handleLogout">退出</el-button>
    </el-header>
    <el-main>
      <el-empty description="阶段0：大屏内容将在阶段1补充（告警统计、趋势图等）" />
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

function handleLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #e4e7ed;
}
.title {
  font-size: 18px;
  font-weight: 600;
}
.user {
  margin-left: auto;
  color: #606266;
}
</style>
