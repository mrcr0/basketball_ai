import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store'
import router from '../router'

const instance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true,
  maxRedirects: 0,
  validateStatus: (status) => status >= 200 && status < 300
})

let requestCount = 0
let loadingTimer = null
const pendingRequests = new Map()

const generateRequestKey = (config) => {
  return `${config.method || 'get'}-${config.url}-${JSON.stringify(config.params || {})}`
}

const showLoading = () => {
  requestCount++
  if (!loadingTimer) {
    loadingTimer = setTimeout(() => {
      const loadingEl = document.getElementById('global-loading')
      if (loadingEl) {
        loadingEl.style.display = 'flex'
      }
    }, 200)
  }
}

const hideLoading = () => {
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    if (loadingTimer) {
      clearTimeout(loadingTimer)
      loadingTimer = null
    }
    const loadingEl = document.getElementById('global-loading')
    if (loadingEl) {
      loadingEl.style.display = 'none'
    }
  }
}

instance.interceptors.request.use(
  (config) => {
    showLoading()
    
    const requestKey = generateRequestKey(config)
    if (pendingRequests.has(requestKey)) {
      pendingRequests.get(requestKey).cancel()
    }
    
    const source = axios.CancelToken.source()
    config.cancelToken = source.token
    pendingRequests.set(requestKey, source)
    
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = userStore.getAuthorization()
    }
    return config
  },
  (error) => {
    hideLoading()
    if (!axios.isCancel(error)) {
      ElMessage.error('请求失败，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    hideLoading()
    
    const requestKey = generateRequestKey(response.config)
    pendingRequests.delete(requestKey)
    
    return response.data
  },
  (error) => {
    hideLoading()
    
    const requestKey = generateRequestKey(error.config || {})
    pendingRequests.delete(requestKey)
    
    if (axios.isCancel(error)) {
      return Promise.reject(error)
    }
    
    const status = error.response?.status
    const message = error.response?.data?.message

    if (status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      return Promise.reject(error)
    }

    if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
      ElMessage.error('请求超时，请检查网络连接')
      return Promise.reject(error)
    }

    if (error.code === 'ERR_NETWORK') {
      ElMessage.error('网络连接失败，请检查网络设置')
      return Promise.reject(error)
    }

    ElMessage.error(message || error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default instance