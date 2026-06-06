<template>
  <AppLayout>
    <div class="page-banner">
      <div class="banner-bg">
        <div class="banner-orb banner-orb-1"></div>
        <div class="banner-orb banner-orb-2"></div>
      </div>
      <div class="container">
        <div class="banner-content">
          <span class="banner-badge">
            <span class="badge-dot"></span>
            <span>个人中心</span>
          </span>
          <h1 class="banner-title">👤 个人中心</h1>
          <p class="banner-desc">管理个人资料、查看训练计划和训练记录</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="profile-content">
        <div class="profile-header-card">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <div class="avatar" @click="handleAvatarClick">
                <img v-if="userStore.user?.avatarUrl" :src="userStore.user.avatarUrl" class="avatar-img" />
                <span v-else class="avatar-icon">👤</span>
              </div>
              <div class="avatar-overlay" @click="handleAvatarClick">
                <span class="overlay-icon">{{ uploadingAvatar ? '⏳' : '📷' }}</span>
                <span class="overlay-text">{{ uploadingAvatar ? '上传中...' : '更换头像' }}</span>
              </div>
            </div>
            <input
              ref="avatarInputRef"
              type="file"
              accept=".jpg,.jpeg,.png,.webp"
              style="display: none"
              @change="handleAvatarChange"
            />
            <div class="user-info">
              <h2>{{ profile.nickname }}</h2>
              <span :class="['role-badge', profile.role]">
                {{ profile.role === 'admin' ? '管理员' : '普通用户' }}
              </span>
            </div>
          </div>
          <div class="profile-stats">
            <div class="mini-stat">
              <span class="mini-stat-value">{{ plans.length }}</span>
              <span class="mini-stat-label">训练计划</span>
            </div>
            <div class="mini-stat">
              <span class="mini-stat-value">{{ records.length }}</span>
              <span class="mini-stat-label">训练记录</span>
            </div>
          </div>
        </div>

        <div class="profile-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            :class="['tab-btn', { active: activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            <span>{{ tab.label }}</span>
          </button>
        </div>

        <div class="tab-content">
          <div v-if="activeTab === 'dashboard'" class="dashboard-view">
            <div class="chart-controls">
              <h3>📈 训练数据可视化</h3>
              <div class="time-filters">
                <button
                  v-for="t in timeOptions"
                  :key="t.value"
                  :class="['time-btn', { active: timeRange === t.value }]"
                  @click="switchTimeRange(t.value)"
                >{{ t.label }}</button>
              </div>
            </div>
            <div class="charts-grid">
              <div class="chart-card">
                <h4 class="chart-title">📊 训练频率趋势（柱状图）</h4>
                <div ref="barChartRef" class="chart-box"></div>
              </div>
              <div class="chart-card">
                <h4 class="chart-title">📈 训练完成度趋势（折线图）</h4>
                <div ref="lineChartRef" class="chart-box"></div>
              </div>
            </div>
            <div class="stats-summary">
              <div class="summary-card">
                <span class="summary-value">{{ totalWorkouts }}</span>
                <span class="summary-label">总训练次数</span>
              </div>
              <div class="summary-card">
                <span class="summary-value">{{ avgCompletion }}%</span>
                <span class="summary-label">平均完成度</span>
              </div>
              <div class="summary-card">
                <span class="summary-value">{{ totalMinutes }}分钟</span>
                <span class="summary-label">总运动时长</span>
              </div>
              <div class="summary-card">
                <span class="summary-value">{{ topItem }}</span>
                <span class="summary-label">最爱项目</span>
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'info'" class="info-form">
            <h3>📝 个人资料</h3>
            <el-form :model="form" label-width="100px" size="default">
              <div class="form-grid">
                <el-form-item label="用户名">
                  <el-input v-model="form.username" disabled />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="form.nickname" placeholder="设置昵称" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="form.email" placeholder="绑定邮箱" />
                </el-form-item>
                <el-form-item label="球龄(年)">
                  <el-input v-model.number="form.experienceYears" placeholder="如：2" />
                </el-form-item>
                <el-form-item label="身高(cm)">
                  <el-input v-model.number="form.height" placeholder="如：180" />
                </el-form-item>
                <el-form-item label="体重(kg)">
                  <el-input v-model.number="form.weight" placeholder="如：75" />
                </el-form-item>
                <el-form-item label="场上位置">
                  <el-select v-model="form.position">
                    <el-option label="🏀 后卫" value="后卫" />
                    <el-option label="🏀 前锋" value="前锋" />
                    <el-option label="🏀 中锋" value="中锋" />
                  </el-select>
                </el-form-item>
                <el-form-item label="技术水平">
                  <el-select v-model="form.skillLevel">
                    <el-option label="🌱 新手入门" value="新手入门" />
                    <el-option label="📈 进阶提升" value="进阶提升" />
                    <el-option label="💎 专业强化" value="专业强化" />
                  </el-select>
                </el-form-item>
              </div>
              <el-form-item label="薄弱项">
                <el-input v-model="form.weakPoints" type="textarea" :rows="2" placeholder="如：投篮、运球、防守" />
              </el-form-item>
              <el-form-item label="训练目标">
                <el-input v-model="form.trainingGoal" type="textarea" :rows="2" placeholder="如：提升投篮命中率" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveProfile" class="save-btn">
                  💾 保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <div v-if="activeTab === 'plans'" class="plans-list">
            <h3>📋 我的训练计划</h3>
            <div v-if="plans.length === 0" class="empty-state">
              <div class="empty-icon">📋</div>
              <p>暂无训练计划</p>
              <router-link to="/plans" class="empty-link">去创建训练计划 →</router-link>
            </div>
            <div v-else class="plans-grid">
              <div v-for="plan in plans" :key="plan.id" class="plan-card">
                <div class="plan-card-header">
                  <h4>{{ plan.planName }}</h4>
                  <span :class="['tag', plan.isCustomized === 1 ? 'customized' : 'ai']">
                    {{ plan.isCustomized === 1 ? '已自定义' : 'AI生成' }}
                  </span>
                </div>
                <div class="plan-card-meta">
                  <span>{{ plan.trainingCycle }}计划 · {{ plan.cycleCount }}周期</span>
                  <span class="plan-date">{{ plan.createdAt }}</span>
                </div>
                <button @click="viewPlan(plan.id)" class="view-btn">查看详情 →</button>
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'records'" class="records-list">
            <h3>📊 训练记录</h3>
            <div v-if="records.length === 0" class="empty-state">
              <div class="empty-icon">📝</div>
              <p>暂无训练记录</p>
              <router-link to="/training" class="empty-link">去打卡训练 →</router-link>
            </div>
            <div v-else class="records-table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>训练项目</th>
                    <th>命中率</th>
                    <th>时长</th>
                    <th>完成度</th>
                    <th>打卡时间</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="record in records" :key="record.id">
                    <td>
                      <span class="training-item">{{ record.trainingItem }}</span>
                    </td>
                    <td>
                      <span :class="['metric', record.hitRate >= 70 ? 'good' : record.hitRate >= 40 ? 'normal' : 'low']">
                        {{ record.hitRate }}%
                      </span>
                    </td>
                    <td>{{ record.duration }}分钟</td>
                    <td>
                      <div class="completion-bar">
                        <div class="completion-fill" :style="{ width: record.completionRate + '%' }"></div>
                      </div>
                      <span class="completion-text">{{ record.completionRate }}%</span>
                    </td>
                    <td class="time-cell">{{ record.checkInTime }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import { getProfile, updateProfile } from '../api/user'
import { getUserPlans } from '../api/plan'
import { getUserRecords } from '../api/record'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/AppLayout.vue'
import * as echarts from 'echarts'
import { uploadImage, validateImage } from '../api/upload.js'
import { useUserStore } from '../store'

const userStore = useUserStore()
const activeTab = ref('dashboard')
const profile = ref({})
const plans = ref([])
const records = ref([])
const barChartRef = ref(null)
const lineChartRef = ref(null)
let barChart = null
let lineChart = null
const timeRange = ref('month')
const avatarInputRef = ref(null)
const uploadingAvatar = ref(false)

const timeOptions = [
  { value: 'day', label: '今日' },
  { value: 'week', label: '本周' },
  { value: 'month', label: '本月' },
  { value: 'year', label: '全年' }
]

const totalWorkouts = computed(() => getFilteredRecords().length)
const avgCompletion = computed(() => {
  const filtered = getFilteredRecords()
  if (filtered.length === 0) return 0
  const sum = filtered.reduce((acc, r) => acc + (r.completionRate || 0), 0)
  return Math.round(sum / filtered.length)
})
const totalMinutes = computed(() => {
  return getFilteredRecords().reduce((acc, r) => acc + Math.round(r.duration || 0), 0)
})
const topItem = computed(() => {
  const filtered = getFilteredRecords()
  if (filtered.length === 0) return '--'
  const count = {}
  filtered.forEach(r => {
    const item = r.trainingItem || '未分类'
    count[item] = (count[item] || 0) + 1
  })
  return Object.entries(count).sort((a, b) => b[1] - a[1])[0][0]
})

const tabs = [
  { key: 'dashboard', label: '数据看板', icon: '📈' },
  { key: 'info', label: '个人资料', icon: '📝' },
  { key: 'plans', label: '训练计划', icon: '📋' },
  { key: 'records', label: '训练记录', icon: '📊' }
]

const form = reactive({})

const loadProfile = async () => {
  try {
    const response = await getProfile()
    if (response.code === 200) {
      profile.value = response.data
      Object.assign(form, response.data)
      if (response.data.avatarUrl && userStore.user) {
        userStore.updateAvatar(response.data.avatarUrl)
      }
    }
  } catch (error) {
    console.error('加载资料失败', error)
  }
}

const loadPlans = async () => {
  try {
    const response = await getUserPlans()
    if (response.code === 200) {
      plans.value = response.data
    }
  } catch (error) {
    console.error('加载计划失败', error)
  }
}

const loadRecords = async () => {
  try {
    const response = await getUserRecords()
    if (response.code === 200) {
      records.value = response.data
    }
  } catch (error) {
    console.error('加载记录失败', error)
  }
}

const handleAvatarClick = () => {
  avatarInputRef.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  const error = validateImage(file)
  if (error) {
    ElMessage.error(error)
    event.target.value = ''
    return
  }

  uploadingAvatar.value = true
  try {
    const result = await uploadImage(file)
    if (result.success && result.accessUrl) {
      form.avatarUrl = result.accessUrl
      profile.value.avatarUrl = result.accessUrl
      userStore.updateAvatar(result.accessUrl)
      await updateProfile({ ...form })
      ElMessage.success('头像更新成功')
    } else {
      ElMessage.error('头像上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败：' + error.message)
  } finally {
    uploadingAvatar.value = false
    event.target.value = ''
  }
}

const saveProfile = async () => {
  try {
    const response = await updateProfile(form)
    if (response.code === 200) {
      ElMessage.success('保存成功')
      profile.value = response.data
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}

const viewPlan = (id) => {
  window.location.href = `/plans/${id}`
}

const getFilteredRecords = () => {
  const now = new Date()
  const filtered = records.value.filter(r => r.checkInTime)
  switch (timeRange.value) {
    case 'day':
      return filtered.filter(r => {
        const d = new Date(r.checkInTime)
        return d.toDateString() === now.toDateString()
      })
    case 'week': {
      const weekAgo = new Date(now)
      weekAgo.setDate(now.getDate() - 7)
      return filtered.filter(r => new Date(r.checkInTime) >= weekAgo)
    }
    case 'month': {
      const monthAgo = new Date(now)
      monthAgo.setMonth(now.getMonth() - 1)
      return filtered.filter(r => new Date(r.checkInTime) >= monthAgo)
    }
    case 'year':
    default:
      return filtered
  }
}

const getChartTimeLabels = () => {
  const now = new Date()
  const labels = []
  let count = 7
  switch (timeRange.value) {
    case 'day': count = 24; break
    case 'week': count = 7; break
    case 'month': count = 30; break
    case 'year': count = 12; break
  }
  for (let i = count - 1; i >= 0; i--) {
    if (timeRange.value === 'day') {
      labels.push(`${String(now.getHours() - i).padStart(2, '0')}:00`)
    } else if (timeRange.value === 'year') {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
      labels.push(`${d.getFullYear()}年${d.getMonth() + 1}月`)
    } else {
      const d = new Date(now)
      d.setDate(now.getDate() - i)
      labels.push(`${d.getMonth() + 1}/${d.getDate()}`)
    }
  }
  return labels
}

const getChartData = () => {
  const filtered = getFilteredRecords()
  const labels = getChartTimeLabels()
  const freqData = new Array(labels.length).fill(0)
  const completionData = new Array(labels.length).fill(0)
  const countData = new Array(labels.length).fill(0)

  filtered.forEach(r => {
    if (!r.checkInTime) return
    const d = new Date(r.checkInTime)
    for (let i = labels.length - 1; i >= 0; i--) {
      const label = labels[labels.length - 1 - i]
      if (timeRange.value === 'day') {
        const hour = parseInt(label)
        if (d.getHours() === hour) {
          freqData[i]++
          completionData[i] += r.completionRate || 0
          countData[i]++
        }
      } else if (timeRange.value === 'year') {
        if (d.getFullYear() + '年' + (d.getMonth() + 1) + '月' === label) {
          freqData[i]++
          completionData[i] += r.completionRate || 0
          countData[i]++
        }
      } else {
        const lbl = `${d.getMonth() + 1}/${d.getDate()}`
        if (lbl === label) {
          freqData[i]++
          completionData[i] += r.completionRate || 0
          countData[i]++
        }
      }
    }
  })

  const avgCompletionData = completionData.map((sum, i) =>
    countData[i] > 0 ? Math.round(sum / countData[i]) : 0
  )

  return { labels, freqData, avgCompletionData }
}

const initCharts = () => {
  nextTick(() => {
    if (barChartRef.value) {
      barChart = echarts.init(barChartRef.value)
    }
    if (lineChartRef.value) {
      lineChart = echarts.init(lineChartRef.value)
    }
    renderCharts()
    window.addEventListener('resize', handleResize)
  })
}

const renderCharts = () => {
  const { labels, freqData, avgCompletionData } = getChartData()

  if (barChart) {
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { top: 20, right: 20, bottom: 30, left: 40 },
      xAxis: { type: 'category', data: labels, axisLabel: { fontSize: 10, rotate: timeRange.value === 'day' ? 45 : 0 } },
      yAxis: { type: 'value', name: '次数', minInterval: 1 },
      series: [{
        type: 'bar',
        data: freqData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#22c55e' },
            { offset: 1, color: '#86efac' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        barWidth: timeRange.value === 'day' ? 8 : 16,
        emphasis: { itemStyle: { color: '#16a34a' } }
      }]
    }, true)
  }

  if (lineChart) {
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { top: 20, right: 20, bottom: 30, left: 40 },
      xAxis: { type: 'category', data: labels, axisLabel: { fontSize: 10, rotate: timeRange.value === 'day' ? 45 : 0 } },
      yAxis: { type: 'value', name: '%', min: 0, max: 100 },
      series: [{
        type: 'line',
        data: avgCompletionData,
        smooth: true,
        lineStyle: { width: 3, color: '#3b82f6' },
        itemStyle: { color: '#3b82f6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.02)' }
          ])
        },
        symbol: 'circle',
        symbolSize: 6,
        emphasis: { symbolSize: 10 }
      }]
    }, true)
  }
}

const handleResize = () => {
  barChart?.resize()
  lineChart?.resize()
}

const switchTimeRange = async (range) => {
  timeRange.value = range
  await nextTick()
  renderCharts()
}

watch(records, () => {
  renderCharts()
})

onMounted(() => {
  loadProfile()
  loadPlans()
  loadRecords()
  initCharts()
})
</script>

<style scoped>
.page-banner {
  position: relative;
  background: linear-gradient(135deg, var(--color-dark-900) 0%, var(--color-dark-700) 50%, #0f4c3a 100%);
  padding: 3rem 0 2.5rem;
  overflow: hidden;
}

.banner-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.banner-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
}

.banner-orb-1 {
  width: 300px;
  height: 300px;
  background: var(--color-accent-400);
  top: -50%;
  right: -10%;
  animation: orbFloat 8s ease-in-out infinite;
}

.banner-orb-2 {
  width: 200px;
  height: 200px;
  background: var(--color-primary-400);
  bottom: -30%;
  left: -5%;
  animation: orbFloat 6s ease-in-out infinite reverse;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(30px, -20px) scale(1.1); }
}

.banner-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.banner-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.35rem 1rem;
  background: rgba(34, 197, 94, 0.12);
  border: 1px solid rgba(34, 197, 94, 0.25);
  border-radius: var(--radius-full);
  color: var(--color-accent-400);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  margin-bottom: 1rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-accent-400);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.banner-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-extrabold);
  color: #fff;
  margin: 0 0 0.75rem;
  letter-spacing: var(--letter-spacing-tight);
}

.banner-desc {
  color: var(--color-neutral-400);
  font-size: var(--font-size-lg);
  max-width: 600px;
  margin: 0 auto;
  line-height: var(--line-height-relaxed);
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.profile-content {
  padding: 1.5rem 0 3rem;
}

.profile-header-card {
  background: #fff;
  border-radius: var(--radius-2xl);
  padding: 1.75rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.25rem;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--color-accent-400) 0%, var(--color-accent-500) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
  flex-shrink: 0;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-icon {
  font-size: 2.5rem;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.overlay-icon {
  font-size: 1.25rem;
}

.overlay-text {
  font-size: 0.65rem;
  color: #fff;
  font-weight: 500;
}

.user-info h2 {
  margin: 0 0 0.5rem;
  color: var(--color-neutral-800);
  font-size: var(--font-size-xl);
}

.role-badge {
  padding: 0.2rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.role-badge.admin {
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
}

.role-badge.user {
  background: var(--color-neutral-100);
  color: var(--color-neutral-500);
}

.profile-stats {
  display: flex;
  gap: 1.5rem;
}

.mini-stat {
  text-align: center;
  padding: 0.75rem 1.25rem;
  background: var(--color-neutral-50);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-100);
}

.mini-stat-value {
  display: block;
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-extrabold);
  color: var(--color-accent-500);
  line-height: 1.1;
}

.mini-stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
}

.profile-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.25rem;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 0.4rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  padding: 0.7rem 1rem;
  background: transparent;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-neutral-500);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: inherit;
}

.tab-btn:hover {
  color: var(--color-accent-500);
  background: var(--color-accent-50);
}

.tab-btn.active {
  background: linear-gradient(135deg, var(--color-accent-500) 0%, var(--color-accent-600) 100%);
  color: #fff;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.3);
}

.tab-icon {
  font-size: 1rem;
}

.tab-content {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.75rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.dashboard-view {
  animation: fadeInSlide 0.35s ease-out;
}

@keyframes fadeInSlide {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.chart-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.25rem;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.chart-controls h3 {
  margin: 0;
  color: var(--color-neutral-800);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.time-filters {
  display: flex;
  gap: 0.35rem;
  background: var(--color-neutral-100);
  border-radius: var(--radius-lg);
  padding: 0.25rem;
}

.time-btn {
  padding: 0.4rem 0.85rem;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--color-neutral-500);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.time-btn:hover {
  color: var(--color-neutral-700);
}

.time-btn.active {
  background: var(--color-accent-500);
  color: #fff;
  box-shadow: 0 2px 6px rgba(34, 197, 94, 0.25);
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.chart-card {
  background: #f8fafc;
  border-radius: var(--radius-lg);
  border: 1px solid #e2e8f0;
  padding: 1rem;
}

.chart-title {
  margin: 0 0 0.75rem;
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
}

.chart-box {
  width: 100%;
  height: 280px;
}

.stats-summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.75rem;
}

.summary-card {
  background: linear-gradient(135deg, var(--color-neutral-50) 0%, #fff 100%);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  padding: 1rem;
  text-align: center;
}

.summary-value {
  display: block;
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--color-accent-500);
  line-height: 1.2;
}

.summary-label {
  font-size: 0.75rem;
  color: var(--color-neutral-400);
  margin-top: 0.25rem;
  display: block;
}

.info-form h3,
.plans-list h3,
.records-list h3 {
  margin: 0 0 1.5rem;
  color: var(--color-neutral-800);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 1rem;
}

.save-btn {
  padding: 0.65rem 1.5rem;
  font-weight: var(--font-weight-semibold);
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  color: var(--color-neutral-400);
}

.empty-icon {
  font-size: 3.5rem;
  margin-bottom: 0.75rem;
}

.empty-state p {
  margin: 0 0 1rem;
  font-size: var(--font-size-sm);
}

.empty-link {
  color: var(--color-accent-500);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
  transition: color var(--transition-fast);
}

.empty-link:hover {
  color: var(--color-accent-600);
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.plan-card {
  background: var(--color-neutral-50);
  padding: 1.25rem;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-100);
  transition: all var(--transition-base);
}

.plan-card:hover {
  border-color: var(--color-accent-200);
  box-shadow: var(--shadow-md);
}

.plan-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.plan-card-header h4 {
  margin: 0;
  color: var(--color-neutral-800);
  font-size: var(--font-size-base);
}

.tag {
  padding: 0.2rem 0.6rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  flex-shrink: 0;
}

.tag.ai { background: var(--color-info-light); color: var(--color-info); }
.tag.customized { background: var(--color-warning-light); color: var(--color-warning); }

.plan-card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
}

.plan-date {
  color: var(--color-neutral-300);
}

.view-btn {
  width: 100%;
  padding: 0.5rem 1rem;
  background: var(--color-accent-500);
  color: var(--color-dark-900);
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.view-btn:hover {
  background: var(--color-accent-600);
  color: #fff;
}

.records-table-wrapper {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.records-table-wrapper table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--font-size-sm);
}

.records-table-wrapper th {
  padding: 0.75rem 1rem;
  text-align: left;
  background: var(--color-neutral-50);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-600);
  border-bottom: 2px solid var(--color-neutral-200);
  white-space: nowrap;
}

.records-table-wrapper td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--color-neutral-100);
  color: var(--color-neutral-600);
  vertical-align: middle;
}

.training-item {
  font-weight: var(--font-weight-medium);
  color: var(--color-neutral-700);
}

.metric {
  font-weight: var(--font-weight-semibold);
  padding: 0.15rem 0.5rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
}

.metric.good { background: var(--color-accent-50); color: var(--color-accent-600); }
.metric.normal { background: var(--color-warning-50); color: var(--color-warning-600); }
.metric.low { background: var(--color-danger-50); color: var(--color-danger-600); }

.completion-bar {
  display: inline-block;
  width: 60px;
  height: 6px;
  background: var(--color-neutral-100);
  border-radius: var(--radius-full);
  margin-right: 0.5rem;
  vertical-align: middle;
  overflow: hidden;
}

.completion-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-accent-400), var(--color-accent-500));
  border-radius: var(--radius-full);
  transition: width var(--transition-base);
}

.completion-text {
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
  vertical-align: middle;
}

.time-cell {
  white-space: nowrap;
  color: var(--color-neutral-400);
  font-size: var(--font-size-xs);
}

@media (max-width: 768px) {
  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .profile-header-card {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .profile-stats {
    justify-content: center;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .plans-grid {
    grid-template-columns: 1fr;
  }

  .profile-tabs {
    flex-direction: column;
    gap: 0.35rem;
  }

  .tab-btn {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .avatar-overlay {
    opacity: 1;
    background: rgba(0, 0, 0, 0.3);
  }

  .overlay-text {
    display: none;
  }

  .records-table-wrapper th,
  .records-table-wrapper td {
    padding: 0.5rem 0.4rem;
    font-size: var(--font-size-xs);
  }
}
</style>