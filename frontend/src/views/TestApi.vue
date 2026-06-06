<template>
  <div class="test-page">
    <div class="test-container">
      <h1>API Connection Test</h1>
      
      <div class="test-section">
        <h2>1. Login Test</h2>
        <button @click="testLogin" :disabled="loading.login">
          {{ loading.login ? 'Testing...' : 'Test Login (admin/123456)' }}
        </button>
        <div v-if="results.login" class="result">
          <pre>{{ JSON.stringify(results.login, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h2>2. Register Test</h2>
        <button @click="testRegister" :disabled="loading.register">
          {{ loading.register ? 'Testing...' : 'Test Register' }}
        </button>
        <div v-if="results.register" class="result">
          <pre>{{ JSON.stringify(results.register, null, 2) }}</pre>
        </div>
      </div>

      <div class="test-section">
        <h2>3. Network Request Log</h2>
        <div class="log">
          <div v-for="(log, index) in logs" :key="index" :class="['log-item', log.type]">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
      </div>

      <div class="test-section">
        <h2>Debug Info</h2>
        <div class="debug-info">
          <p><strong>Base URL:</strong> {{ baseURL }}</p>
          <p><strong>Token:</strong> {{ token ? token.substring(0, 20) + '...' : 'None' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const baseURL = '/api'
const token = ref(localStorage.getItem('token'))

const loading = ref({
  login: false,
  register: false
})

const results = ref({
  login: null,
  register: null
})

const logs = ref([])

const addLog = (type, message) => {
  const time = new Date().toLocaleTimeString()
  logs.value.unshift({ type, message, time })
}

const testLogin = async () => {
  loading.value.login = true
  results.value.login = null
  addLog('info', 'Starting login test...')

  try {
    const response = await axios.post(`${baseURL}/users/login`, {
      username: 'admin',
      password: '123456'
    })
    
    results.value.login = response.data
    addLog('success', `Login successful! Token: ${response.data.data?.token?.substring(0, 30)}...`)
    
    if (response.data.data?.token) {
      localStorage.setItem('token', response.data.data.token)
      token.value = response.data.data.token
    }
  } catch (error) {
    const errorMsg = error.response?.data || error.message
    results.value.login = { error: errorMsg }
    addLog('error', `Login failed: ${JSON.stringify(errorMsg)}`)
    console.error('Login Error:', error)
  } finally {
    loading.value.login = false
  }
}

const testRegister = async () => {
  loading.value.register = true
  results.value.register = null
  addLog('info', 'Starting register test...')

  const timestamp = Date.now()
  try {
    const response = await axios.post(`${baseURL}/users/register`, {
      username: `testuser${timestamp}`,
      password: '123456',
      nickname: `Test${timestamp}`
    })
    
    results.value.register = response.data
    addLog('success', `Register successful! UserID: ${response.data.data?.id}`)
  } catch (error) {
    const errorMsg = error.response?.data || error.message
    results.value.register = { error: errorMsg }
    addLog('error', `Register failed: ${JSON.stringify(errorMsg)}`)
    console.error('Register Error:', error)
  } finally {
    loading.value.register = false
  }
}

onMounted(() => {
  addLog('info', 'Test page loaded')
  addLog('info', `BaseURL: ${baseURL}`)
})
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
}

.test-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

h1 {
  color: #1a1a2e;
  margin-bottom: 30px;
}

h2 {
  color: #333;
  font-size: 1.2rem;
  margin: 20px 0 10px;
}

button {
  padding: 10px 20px;
  background: #4ade80;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
}

button:hover {
  background: #22c55e;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.result {
  margin-top: 15px;
  padding: 15px;
  background: #f0f0f0;
  border-radius: 5px;
  overflow-x: auto;
}

.result pre {
  margin: 0;
  font-size: 0.9rem;
}

.log {
  max-height: 200px;
  overflow-y: auto;
  background: #1a1a2e;
  padding: 10px;
  border-radius: 5px;
}

.log-item {
  padding: 5px 10px;
  margin: 5px 0;
  border-radius: 3px;
  font-family: monospace;
  font-size: 0.9rem;
}

.log-item.info {
  background: #3b82f6;
  color: white;
}

.log-item.success {
  background: #22c55e;
  color: white;
}

.log-item.error {
  background: #ef4444;
  color: white;
}

.log-time {
  margin-right: 10px;
  opacity: 0.7;
}

.debug-info {
  padding: 15px;
  background: #fef3c7;
  border-radius: 5px;
}

.debug-info p {
  margin: 5px 0;
}
</style>