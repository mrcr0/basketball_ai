<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-orb auth-orb-1"></div>
      <div class="auth-orb auth-orb-2"></div>
      <div class="auth-orb auth-orb-3"></div>
    </div>
    <div class="auth-container">
      <div class="auth-card scale-in">
        <div class="auth-header">
          <router-link to="/" class="auth-logo">
            <span>🏀</span>
          </router-link>
          <h1>找回密码</h1>
          <p>输入注册邮箱，我们将发送密码重置链接</p>
        </div>
        
        <form @submit.prevent="handleSubmit" class="auth-form">
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
            <el-form-item label="邮箱" prop="email">
              <el-input 
                v-model="form.email" 
                type="email" 
                placeholder="请输入注册时使用的邮箱" 
                size="large"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" native-type="submit" class="auth-btn" size="large" :loading="loading">
                {{ loading ? '发送中...' : '发送重置链接' }}
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="auth-links">
            <router-link to="/login" class="auth-link"><span class="accent">← 返回登录</span></router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { forgotPassword } from '../api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  email: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

const formRef = ref(null)

const handleSubmit = async () => {
  const isValid = await formRef.value.validate()
  if (!isValid) return

  loading.value = true
  try {
    const response = await forgotPassword({ email: form.email })
    if (response.code === 200) {
      ElMessage.success(response.message)
      router.push('/login')
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('操作失败：' + (error.response?.data?.message || error.message))
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
  opacity: 0.12;
}

.auth-orb-1 {
  width: 400px;
  height: 400px;
  background: var(--color-accent-400);
  top: -100px;
  right: -100px;
}

.auth-orb-2 {
  width: 300px;
  height: 300px;
  background: var(--color-primary-500);
  bottom: -50px;
  left: -50px;
}

.auth-orb-3 {
  width: 200px;
  height: 200px;
  background: var(--color-warm-400);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  opacity: 0.06;
}

.auth-container {
  width: 100%;
  max-width: 420px;
  padding: var(--space-6);
  position: relative;
  z-index: 1;
}

.auth-card {
  background: rgba(255, 255, 255, 0.97);
  border-radius: var(--radius-3xl);
  padding: var(--space-10) var(--space-8);
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
  width: 56px;
  height: 56px;
  background: var(--gradient-accent);
  border-radius: var(--radius-xl);
  font-size: 1.5rem;
  margin-bottom: var(--space-5);
  text-decoration: none;
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
  padding: var(--space-3) 0;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-xl);
  background: var(--gradient-accent);
  border: none;
  transition: all var(--transition-base);
}

.auth-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-glow-accent);
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
  }
}
</style>