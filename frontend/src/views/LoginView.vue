<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="brand">
        <div class="brand-name">AIOps 助手</div>
        <p class="brand-line">值班运维的告警与 AI 排障控制台</p>
      </section>

      <section class="form-panel">
        <form v-if="mode === 'login'" class="form" @submit.prevent="handleLogin">
          <label class="field">
            <span class="field-label">用户名</span>
            <el-input v-model="form.username" placeholder="用户名" clearable />
          </label>
          <label class="field">
            <span class="field-label">密码</span>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              show-password
              @keyup.enter="handleLogin"
            />
          </label>
          <el-button type="primary" class="submit" :loading="loading" native-type="submit">
            登录
          </el-button>
          <button type="button" class="switch" @click="switchToRegister">没有账号？注册</button>
        </form>

        <form v-else class="form" @submit.prevent="handleRegister">
          <label class="field">
            <span class="field-label">用户名</span>
            <el-input v-model="reg.username" placeholder="用户名" clearable />
          </label>
          <label class="field">
            <span class="field-label">邮箱</span>
            <el-input v-model="reg.email" placeholder="邮箱" clearable />
          </label>
          <label class="field">
            <span class="field-label">手机号</span>
            <el-input v-model="reg.phone" placeholder="手机号" clearable />
          </label>
          <label class="field">
            <span class="field-label">密码</span>
            <el-input
              v-model="reg.password"
              type="password"
              placeholder="密码"
              show-password
            />
          </label>
          <label class="field">
            <span class="field-label">确认密码</span>
            <el-input
              v-model="reg.confirm"
              type="password"
              placeholder="确认密码"
              show-password
              @keyup.enter="handleRegister"
            />
          </label>
          <el-button type="primary" class="submit" :loading="loading" native-type="submit">
            注册
          </el-button>
          <button type="button" class="switch" @click="switchToLogin">已有账号？登录</button>
        </form>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { register } from '@/api/user'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const mode = ref<'login' | 'register'>('login')
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const reg = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirm: ''
})

function switchToRegister() {
  mode.value = 'register'
  loading.value = false
}

function switchToLogin() {
  mode.value = 'login'
  loading.value = false
}

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const result = await login(form)
    auth.setAuth(result)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } catch (e: any) {
    ElMessage.error(e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!reg.username || !reg.password || !reg.email || !reg.phone) {
    ElMessage.warning('请填写完整的注册信息')
    return
  }
  if (reg.password !== reg.confirm) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  loading.value = true
  try {
    await register({
      username: reg.username,
      password: reg.password,
      email: reg.email,
      phone: reg.phone
    })
    ElMessage.success('注册成功，请登录')
    form.username = reg.username
    form.password = ''
    switchToLogin()
  } catch (e: any) {
    ElMessage.error(e?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* A calm ink backdrop: a faint monochrome graticule plus a single tone shift; no hue, no glow */
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 32px;
  background-color: #0b0d14;
  background-image:
    linear-gradient(180deg, rgba(11, 13, 20, 0) 0%, rgba(6, 7, 11, 0.65) 100%),
    linear-gradient(rgba(238, 240, 245, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(238, 240, 245, 0.035) 1px, transparent 1px);
  background-size: 100% 100%, 64px 64px, 64px 64px;
}

/* One framed console window: two zones divided by a hairline */
.login-shell {
  width: 100%;
  max-width: 960px;
  display: grid;
  grid-template-columns: 1fr 400px;
  background: var(--ink-card);
  border: 1px solid var(--ink-hairline-strong);
  border-radius: 20px;
  overflow: hidden;
  box-shadow:
    0 24px 64px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

/* ---- Brand column: name and description on top, mark anchored to the bottom ---- */
.brand {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
  border-right: 1px solid var(--ink-hairline);
}

.brand-name {
  font-size: 18px;
  font-weight: 500;
  letter-spacing: -0.01em;
}

.brand-line {
  margin-top: 16px;
  max-width: 26ch;
  font-size: 15px;
  line-height: 1.6;
  color: var(--ink-text-2);
}

/* ---- Form column ---- */
.form-panel {
  padding: 40px;
  background: #0e1017;
  border-left: 1px solid var(--ink-hairline);
  display: flex;
  align-items: center;
  overflow-y: auto;
}

.form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  color: var(--ink-text-3);
}

.submit {
  width: 100%;
  margin-top: 4px;
  height: 40px;
}

.switch {
  border: 0;
  background: none;
  padding: 4px 0;
  align-self: center;
  font-size: 13px;
  font-family: inherit;
  color: var(--ink-text-3);
  cursor: pointer;
  transition: color var(--dur-fast) var(--ease);
}

.switch:hover {
  color: var(--ink-text);
}

@media (max-width: 760px) {
  .login-shell {
    grid-template-columns: 1fr;
    max-width: 400px;
  }

  .brand {
    padding: 28px 28px 24px;
    border-right: 0;
    border-bottom: 1px solid var(--ink-hairline);
  }

  .brand-line {
    max-width: none;
  }

  .form-panel {
    padding: 24px 28px 28px;
    border-left: 0;
  }
}
</style>
