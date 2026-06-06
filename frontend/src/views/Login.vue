<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-orb auth-orb-1"></div>
      <div class="auth-orb auth-orb-2"></div>
      <div class="auth-orb auth-orb-3"></div>
    </div>
    <div class="auth-container">
      <router-link to="/" class="auth-back">
        <span>← 返回首页</span>
      </router-link>
      <div class="auth-card scale-in">
        <div class="auth-header">
          <div class="login-avatar-wrapper" @click="showAvatarTips">
            <div class="login-avatar">
              <span>🏀</span>
            </div>
            <div class="login-avatar-overlay">
              <span>登录</span>
            </div>
          </div>
          <h1>欢迎回来</h1>
          <p>登录你的AI篮球训练平台账号</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="handleLogin" class="auth-form" @submit.prevent="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              autocomplete="username"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              autocomplete="current-password"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleLogin" class="auth-btn" size="large" :loading="loading" round>
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>

          <div class="auth-links">
            <router-link to="/forgot-password" class="auth-link">忘记密码？</router-link>
            <router-link to="/register" class="auth-link">还没有账号？<span class="accent">立即注册</span></router-link>
          </div>
        </el-form>

        <div class="auth-footer">
          <p>测试账号：admin / admin123</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { login } from '../api/user'
import { useUserStore } from '../store'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 30, message: '用户名长度在 2 到 30 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度在 6 到 50 个字符', trigger: 'blur' }
  ]
}

const formRef = ref(null)

const showAvatarTips = () => {
  ElMessage.info('登录后可在个人中心更换头像')
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const response = await login({
      username: form.username,
      password: form.password
    })
    if (response.code === 200) {
      userStore.setToken(response.data.token)
      userStore.setUser(response.data.user)
      ElMessage.success('登录成功，欢迎回来！')
      router.push('/')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message || '网络异常，请检查后端服务是否启动'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: var(--gradient-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.auth-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  animation: orbFloat 20s ease-in-out infinite;
}

.auth-orb-1 {
  width: 500px;
  height: 500px;
  background: var(--color-accent-400);
  top: -150px;
  right: -100px;
  animation-delay: 0s;
}

.auth-orb-2 {
  width: 350px;
  height: 350px;
  background: var(--color-primary-500);
  bottom: -80px;
  left: -80px;
  animation-delay: -7s;
}

.auth-orb-3 {
  width: 220px;
  height: 220px;
  background: var(--color-warm-400);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.06;
  animation-delay: -14s;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.05); }
  50% { transform: translate(-20px, 30px) scale(0.95); }
  75% { transform: translate(40px, 20px) scale(1.02); }
}

.auth-container {
  width: 100%;
  max-width: 440px;
  padding: var(--space-6);
  position: relative;
  z-index: 1;
}

.auth-back {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
  text-decoration: none;
  margin-bottom: var(--space-4);
  transition: color var(--transition-fast);
}

.auth-back:hover {
  color: var(--color-accent-400);
}

.auth-card {
  background: rgba(255, 255, 255, 0.97);
  border-radius: var(--radius-3xl);
  padding: var(--space-12) var(--space-10);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.auth-header {
  text-align: center;
  margin-bottom: var(--space-10);
}

.login-avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto var(--space-5);
  cursor: pointer;
}

.login-avatar {
  width: 100%;
  height: 100%;
  background: var(--gradient-accent);
  border-radius: var(--radius-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  box-shadow: var(--shadow-glow-accent);
  transition: transform var(--transition-bounce);
}

.login-avatar-wrapper:hover .login-avatar {
  transform: scale(1.08);
}

.login-avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: var(--radius-2xl);
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.login-avatar-wrapper:hover .login-avatar-overlay {
  opacity: 1;
}

.login-avatar-overlay span {
  color: #fff;
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.auth-header h1 {
  color: var(--color-neutral-900);
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-2);
}

.auth-header p {
  color: var(--color-neutral-500);
  font-size: var(--font-size-sm);
}

.auth-form {
  margin-bottom: var(--space-6);
}

.auth-btn {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
}

.auth-links {
  display: flex;
  justify-content: space-between;
}

.auth-link {
  color: var(--color-neutral-500);
  font-size: var(--font-size-sm);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.auth-link:hover {
  color: var(--color-accent-600);
}

.auth-link .accent {
  color: var(--color-accent-600);
  font-weight: var(--font-weight-semibold);
}

.auth-footer {
  margin-top: var(--space-8);
  padding-top: var(--space-6);
  border-top: 1px solid var(--color-neutral-100);
  text-align: center;
}

.auth-footer p {
  color: var(--color-neutral-400);
  font-size: var(--font-size-xs);
}

@media (max-width: 480px) {
  .auth-card {
    padding: var(--space-8) var(--space-6);
    border-radius: var(--radius-2xl);
  }

  .auth-header h1 {
    font-size: var(--font-size-2xl);
  }

  .auth-links {
    flex-direction: column;
    align-items: center;
    gap: var(--space-3);
  }

  .auth-back {
    padding-left: var(--space-2);
  }
}
</style>