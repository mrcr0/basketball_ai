<template>
  <div class="admin-page">
    <aside class="sidebar">
      <div class="logo">
        <h1>🏀 管理后台</h1>
      </div>
      <nav class="menu">
        <button 
          v-for="item in menuItems" 
          :key="item.key"
          :class="['menu-item', { active: activeMenu === item.key }]"
          @click="activeMenu = item.key; loadData()"
        >
          {{ item.icon }} {{ item.label }}
        </button>
      </nav>
    </aside>

    <main class="content">
      <header class="content-header">
        <h2>{{ currentMenuLabel }}</h2>
        <button @click="logout" class="logout-btn">退出登录</button>
      </header>

      <div class="content-body">
        <!-- 数据看板 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.dynamicCount }}</div>
              <div class="stat-label">动态总数</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.pendingCount }}</div>
              <div class="stat-label">待审核</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.loginSuccessCount }}</div>
              <div class="stat-label">今日登录</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ dashboardData.backupCount }}</div>
              <div class="stat-label">备份数</div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-section">
          <div class="search-bar">
            <input v-model="searchKeyword" placeholder="搜索用户名" />
            <button @click="searchUsers">搜索</button>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>昵称</th>
                <th>角色</th>
                <th>位置</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.nickname }}</td>
                <td>{{ user.role === 'admin' ? '管理员' : '普通用户' }}</td>
                <td>{{ user.position || '-' }}</td>
                <td>
                  <span :class="['status-tag', user.status === 1 ? 'active' : 'inactive']">
                    {{ user.status === 1 ? '正常' : '禁用' }}
                  </span>
                </td>
                <td>
                  <button 
                    @click="toggleUserStatus(user)" 
                    class="action-btn"
                  >
                    {{ user.status === 1 ? '禁用' : '启用' }}
                  </button>
                  <button 
                    v-if="user.role !== 'admin'"
                    @click="setAdmin(user)" 
                    class="action-btn edit"
                  >
                    {{ user.role === 'admin' ? '取消管理员' : '设为管理员' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 动作库管理 -->
        <div v-if="activeMenu === 'actions'" class="actions-section">
          <button @click="showActionForm = true" class="add-btn">添加动作</button>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>名称</th>
                <th>分类</th>
                <th>难度</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="action in actions" :key="action.id">
                <td>{{ action.id }}</td>
                <td>{{ action.name }}</td>
                <td>{{ action.category }}</td>
                <td>{{ action.difficulty }}</td>
                <td>
                  <span :class="['status-tag', action.isPublished === 1 ? 'active' : 'inactive']">
                    {{ action.isPublished === 1 ? '发布' : '未发布' }}
                  </span>
                </td>
                <td>
                  <button class="action-btn edit">编辑</button>
                  <button @click="deleteAction(action.id)" class="action-btn delete">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 战术资讯管理 -->
        <div v-if="activeMenu === 'tactics'" class="tactics-section">
          <button @click="showTacticForm = true" class="add-btn">添加资讯</button>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>标题</th>
                <th>类型</th>
                <th>浏览量</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="tactic in tactics" :key="tactic.id">
                <td>{{ tactic.id }}</td>
                <td>{{ tactic.title }}</td>
                <td>{{ getTypeLabel(tactic.type) }}</td>
                <td>{{ tactic.viewCount }}</td>
                <td>
                  <span :class="['status-tag', tactic.isPublished === 1 ? 'active' : 'inactive']">
                    {{ tactic.isPublished === 1 ? '上架' : '下架' }}
                  </span>
                </td>
                <td>
                  <button class="action-btn edit">编辑</button>
                  <button @click="deleteTactic(tactic.id)" class="action-btn delete">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 社区内容管理 -->
        <div v-if="activeMenu === 'dynamics'" class="dynamics-section">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>内容</th>
                <th>作者</th>
                <th>点赞</th>
                <th>评论</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="dynamic in dynamics" :key="dynamic.id">
                <td>{{ dynamic.id }}</td>
                <td>{{ truncate(dynamic.content, 50) }}</td>
                <td>{{ dynamic.user?.nickname }}</td>
                <td>{{ dynamic.likeCount }}</td>
                <td>{{ dynamic.commentCount }}</td>
                <td>
                  <span :class="['status-tag', dynamic.isApproved === 1 ? 'active' : 'pending']">
                    {{ dynamic.isApproved === 1 ? '已审核' : '待审核' }}
                  </span>
                </td>
                <td>
                  <button 
                    v-if="dynamic.isApproved === 0"
                    @click="approveDynamic(dynamic.id)" 
                    class="action-btn"
                  >
                    审核通过
                  </button>
                  <button @click="deleteDynamic(dynamic.id)" class="action-btn delete">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 登录日志 -->
        <div v-if="activeMenu === 'loginLogs'" class="logs-section">
          <div class="search-bar">
            <input v-model="logSearchKeyword" placeholder="搜索用户名" />
            <select v-model="logStatusFilter">
              <option value="">全部状态</option>
              <option value="SUCCESS">成功</option>
              <option value="FAILED">失败</option>
            </select>
            <button @click="searchLogs">搜索</button>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>登录时间</th>
                <th>IP地址</th>
                <th>状态</th>
                <th>错误信息</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in loginLogs" :key="log.id">
                <td>{{ log.id }}</td>
                <td>{{ log.username }}</td>
                <td>{{ formatDateTime(log.loginTime) }}</td>
                <td>{{ log.loginIp }}</td>
                <td>
                  <span :class="['status-tag', log.loginStatus === 'SUCCESS' ? 'active' : 'inactive']">
                    {{ log.loginStatus === 'SUCCESS' ? '成功' : '失败' }}
                  </span>
                </td>
                <td>{{ log.errorMessage || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 操作日志 -->
        <div v-if="activeMenu === 'operationLogs'" class="logs-section">
          <div class="search-bar">
            <input v-model="opLogSearchKeyword" placeholder="搜索用户名" />
            <select v-model="opModuleFilter">
              <option value="">全部模块</option>
              <option value="用户管理">用户管理</option>
              <option value="训练计划">训练计划</option>
              <option value="动作库">动作库</option>
              <option value="战术管理">战术管理</option>
              <option value="社区动态">社区动态</option>
            </select>
            <button @click="searchOpLogs">搜索</button>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>用户名</th>
                <th>操作时间</th>
                <th>模块</th>
                <th>类型</th>
                <th>描述</th>
                <th>IP</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in operationLogs" :key="log.id">
                <td>{{ log.id }}</td>
                <td>{{ log.username }}</td>
                <td>{{ formatDateTime(log.operationTime) }}</td>
                <td>{{ log.operationModule }}</td>
                <td>
                  <span :class="['status-tag', getOpTypeClass(log.operationType)]">
                    {{ log.operationType }}
                  </span>
                </td>
                <td>{{ truncate(log.operationDesc, 30) }}</td>
                <td>{{ log.operationIp }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 数据备份 -->
        <div v-if="activeMenu === 'backups'" class="backups-section">
          <button @click="createBackup" class="add-btn">创建备份</button>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>备份名称</th>
                <th>类型</th>
                <th>大小</th>
                <th>状态</th>
                <th>备份时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="backup in backups" :key="backup.id">
                <td>{{ backup.id }}</td>
                <td>{{ backup.backupName }}</td>
                <td>{{ backup.backupType }}</td>
                <td>{{ formatFileSize(backup.fileSize) }}</td>
                <td>
                  <span :class="['status-tag', backup.status === 'completed' ? 'active' : 'pending']">
                    {{ backup.status === 'completed' ? '已完成' : backup.status }}
                  </span>
                </td>
                <td>{{ formatDateTime(backup.backupTime) }}</td>
                <td>
                  <button @click="restoreBackup(backup.id)" class="action-btn">恢复</button>
                  <button @click="deleteBackup(backup.id)" class="action-btn delete">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store'
import axios from '../../utils/axios'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('dashboard')
const searchKeyword = ref('')
const logSearchKeyword = ref('')
const logStatusFilter = ref('')
const opLogSearchKeyword = ref('')
const opModuleFilter = ref('')

const menuItems = [
  { key: 'dashboard', label: '数据看板', icon: '📊' },
  { key: 'users', label: '用户管理', icon: '👥' },
  { key: 'actions', label: '动作库管理', icon: '🎯' },
  { key: 'tactics', label: '战术资讯管理', icon: '📚' },
  { key: 'dynamics', label: '社区内容管理', icon: '💬' },
  { key: 'loginLogs', label: '登录日志', icon: '🔐' },
  { key: 'operationLogs', label: '操作日志', icon: '📝' },
  { key: 'backups', label: '数据备份', icon: '💾' }
]

const currentMenuLabel = computed(() => {
  const item = menuItems.find(i => i.key === activeMenu.value)
  return item ? item.label : ''
})

const dashboardData = ref({
  userCount: 0,
  dynamicCount: 0,
  activeUsers: 0,
  pendingCount: 0,
  loginSuccessCount: 0,
  backupCount: 0
})

const users = ref([])
const actions = ref([])
const tactics = ref([])
const dynamics = ref([])
const loginLogs = ref([])
const operationLogs = ref([])
const backups = ref([])

const showActionForm = ref(false)
const showTacticForm = ref(false)

const logout = () => {
  userStore.logout()
  router.push('/login')
}

const getTypeLabel = (type) => {
  const types = {
    tactic: '战术',
    knowledge: '科普',
    news: '资讯'
  }
  return types[type] || type
}

const getOpTypeClass = (type) => {
  if (type === '新增') return 'active'
  if (type === '删除') return 'inactive'
  return 'pending'
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN')
}

const formatFileSize = (size) => {
  if (!size) return '0 B'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

const loadData = async () => {
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    switch (activeMenu.value) {
      case 'dashboard':
        const dashboardRes = await axios.get('/api/admin/dashboard')
        dashboardData.value = dashboardRes.data.data
        break
      case 'users':
        const usersRes = await axios.get('/api/admin/users')
        users.value = usersRes.data.data
        break
      case 'dynamics':
        const dynamicsRes = await axios.get('/api/admin/dynamics/pending')
        const allDynamicsRes = await axios.get('/api/dynamics/list')
        dynamics.value = [...dynamicsRes.data.data, ...allDynamicsRes.data.data.filter(d => d.isApproved === 1)]
        break
      case 'loginLogs':
        const loginLogsRes = await axios.get('/api/admin/login-logs')
        loginLogs.value = loginLogsRes.data.data
        break
      case 'operationLogs':
        const opLogsRes = await axios.get('/api/admin/operation-logs')
        operationLogs.value = opLogsRes.data.data
        break
      case 'backups':
        const backupsRes = await axios.get('/api/admin/backups')
        backups.value = backupsRes.data.data
        break
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const toggleUserStatus = async (user) => {
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    const newStatus = user.status === 1 ? 0 : 1
    await axios.put(`/api/admin/users/${user.id}/status?status=${newStatus}`)
    user.status = newStatus
  } catch (error) {
    alert('操作失败')
  }
}

const setAdmin = async (user) => {
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    const newRole = user.role === 'admin' ? 'user' : 'admin'
    await axios.put(`/api/admin/users/${user.id}/role?role=${newRole}`)
    user.role = newRole
  } catch (error) {
    alert('操作失败')
  }
}

const deleteAction = (id) => {
  if (confirm('确定删除吗？')) {
    actions.value = actions.value.filter(a => a.id !== id)
  }
}

const deleteTactic = (id) => {
  if (confirm('确定删除吗？')) {
    tactics.value = tactics.value.filter(t => t.id !== id)
  }
}

const approveDynamic = async (id) => {
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    await axios.put(`/api/admin/dynamics/${id}/approve`)
    const dynamic = dynamics.value.find(d => d.id === id)
    if (dynamic) dynamic.isApproved = 1
  } catch (error) {
    alert('操作失败')
  }
}

const deleteDynamic = async (id) => {
  if (!confirm('确定删除吗？')) return
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    await axios.delete(`/api/admin/dynamics/${id}`)
    dynamics.value = dynamics.value.filter(d => d.id !== id)
  } catch (error) {
    alert('操作失败')
  }
}

const createBackup = async () => {
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    const res = await axios.post('/api/admin/backups/create')
    if (res.data.code === 200) {
      alert('备份成功')
      loadData()
    }
  } catch (error) {
    alert('备份失败')
  }
}

const restoreBackup = async (id) => {
  if (!confirm('确定恢复此备份吗？此操作将覆盖当前数据！')) return
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    await axios.post(`/api/admin/backups/${id}/restore`)
    alert('恢复成功')
  } catch (error) {
    alert('恢复失败')
  }
}

const deleteBackup = async (id) => {
  if (!confirm('确定删除此备份吗？')) return
  try {
    const token = localStorage.getItem('token')
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    await axios.delete(`/api/admin/backups/${id}`)
    backups.value = backups.value.filter(b => b.id !== id)
  } catch (error) {
    alert('删除失败')
  }
}

const searchUsers = () => {
  // 搜索逻辑
}

const searchLogs = () => {
  // 搜索逻辑
}

const searchOpLogs = () => {
  // 搜索逻辑
}

onMounted(() => {
  loadData()

  // 初始化模拟数据
  if (!users.value.length) {
    users.value = [
      { id: 1, username: 'admin', nickname: '管理员', role: 'admin', position: '-', status: 1 },
      { id: 2, username: 'player1', nickname: '篮球小将', role: 'user', position: '后卫', status: 1 },
      { id: 3, username: 'player2', nickname: '扣篮王', role: 'user', position: '前锋', status: 1 }
    ]
  }

  if (!actions.value.length) {
    actions.value = [
      { id: 1, name: '原地单手肩上投篮', category: '投篮', difficulty: '新手入门', isPublished: 1 },
      { id: 2, name: '行进间运球', category: '运球', difficulty: '新手入门', isPublished: 1 },
      { id: 3, name: '交叉步突破', category: '突破', difficulty: '进阶提升', isPublished: 1 }
    ]
  }

  if (!tactics.value.length) {
    tactics.value = [
      { id: 1, title: '基础挡拆战术详解', type: 'tactic', viewCount: 120, isPublished: 1 },
      { id: 2, title: '篮球热身运动指南', type: 'knowledge', viewCount: 80, isPublished: 1 },
      { id: 3, title: '2-3联防战术解析', type: 'tactic', viewCount: 95, isPublished: 1 }
    ]
  }

  if (!dynamics.value.length) {
    dynamics.value = [
      { id: 1, content: '今天完成了AI训练计划，效果很好！', user: { nickname: '篮球小将' }, likeCount: 10, commentCount: 3, isApproved: 1 },
      { id: 2, content: '分享我的投篮训练心得...', user: { nickname: '扣篮王' }, likeCount: 25, commentCount: 8, isApproved: 1 },
      { id: 3, content: '新用户发布的动态', user: { nickname: '新手球员' }, likeCount: 0, commentCount: 0, isApproved: 0 }
    ]
  }
})
</script>

<style scoped>
.admin-page {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  padding: 1.5rem;
}

.logo h1 {
  color: #fff;
  font-size: 1.2rem;
  margin: 0 0 2rem;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.menu-item {
  padding: 0.75rem 1rem;
  background: transparent;
  border: none;
  color: #9ca3af;
  text-align: left;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.menu-item:hover {
  background: rgba(74, 222, 128, 0.1);
  color: #4ade80;
}

.menu-item.active {
  background: #4ade80;
  color: #1a1a2e;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content-header h2 {
  margin: 0;
  color: #1a1a2e;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background: #ef4444;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.content-body {
  padding: 2rem;
}

.dashboard {
  padding: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 1.5rem;
}

.stat-card {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #4ade80;
}

.stat-label {
  color: #6b7280;
  margin-top: 0.5rem;
}

.search-bar {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  align-items: center;
}

.search-bar input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
}

.search-bar select {
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
}

.search-bar button {
  padding: 0.5rem 1rem;
  background: #4ade80;
  color: #1a1a2e;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.data-table th, .data-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #e5e7eb;
}

.data-table th {
  background: #f9fafb;
  font-weight: bold;
  color: #1a1a2e;
}

.status-tag {
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem;
}

.status-tag.active {
  background: #dcfce7;
  color: #16a34a;
}

.status-tag.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.status-tag.pending {
  background: #fef3c7;
  color: #d97706;
}

.action-btn {
  padding: 0.3rem 0.8rem;
  background: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  margin-right: 0.5rem;
}

.action-btn.edit {
  background: #f59e0b;
}

.action-btn.delete {
  background: #ef4444;
}

.add-btn {
  padding: 0.5rem 1rem;
  background: #4ade80;
  color: #1a1a2e;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 1.5rem;
}
</style>