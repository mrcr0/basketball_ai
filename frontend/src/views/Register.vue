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
          <router-link to="/" class="auth-logo">
            <span>🏀</span>
          </router-link>
          <h1>创建账号</h1>
          <p>开启你的AI篮球训练之旅</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="auth-form" @submit.prevent="handleRegister">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名（3-50位）" size="large" autocomplete="username" />
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" size="large" />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码（至少6位）" size="large" show-password autocomplete="new-password" />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" size="large" show-password autocomplete="new-password" />
            </el-form-item>

            <el-form-item label="邮箱（选填）">
              <el-input v-model="form.email" type="email" placeholder="请输入邮箱" size="large" autocomplete="email" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleRegister" class="auth-btn" size="large" :loading="loading" round>
                {{ loading ? '注册中...' : '注册' }}
              </el-button>
            </el-form-item>

          <div class="auth-links">
            <router-link to="/login" class="auth-link">已有账号？<span class="accent">立即登录</span></router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { register } from '../api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

const formRef = ref(null)

const handleRegister = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    const response = await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    })

    if (response.code === 200) {
      ElMessage.success('注册成功，即将跳转到登录页')
      setTimeout(() => {
        router.push('/login')
      }, 1000)
    } else {
      ElMessage.error(response.message || '注册失败')
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
  max-width: 480px;
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
  padding: var(--space-10) var(--space-10);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.auth-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.auth-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: var(--gradient-accent);
  border-radius: var(--radius-2xl);
  font-size: 1.75rem;
  margin-bottom: var(--space-5);
  text-decoration: none;
  box-shadow: var(--shadow-glow-accent);
  transition: transform var(--transition-bounce);
}

.auth-logo:hover {
  transform: scale(1.08);
}

.auth-header h1 {
  color: var(--color-neutral-900);
  font-size: var(--font-size-2xl);
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
  justify-content: center;
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

@media (max-width: 480px) {
  .auth-card {
    padding: var(--space-8) var(--space-6);
    border-radius: var(--radius-2xl);
  }

  .auth-header h1 {
    font-size: var(--font-size-xl);
  }

  .auth-back {
    padding-left: var(--space-2);
  }
}
</style>