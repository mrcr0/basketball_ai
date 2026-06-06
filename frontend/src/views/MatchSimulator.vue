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
            <span>实时模拟</span>
          </span>
          <h1 class="banner-title">🏀 实时比赛模拟</h1>
          <p class="banner-desc">模拟真实篮球比赛数据，实时更新比分、节次和时间，体验比赛节奏</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="match-layout">
        <div v-if="match" class="match-info">
          <div class="match-header">
            <h3>{{ match.matchName }}</h3>
            <span :class="['status-badge', match.status]">{{ statusText }}</span>
          </div>

          <div class="score-board">
            <div :class="['team', { possession: match.homePossession }]">
              <div class="team-name">{{ match.homeTeam }}</div>
              <div class="team-score">{{ match.homeScore }}</div>
              <div v-if="match.homePossession" class="ball-indicator">🏀</div>
            </div>
            <div class="vs">VS</div>
            <div :class="['team', { possession: !match.homePossession }]">
              <div class="team-name">{{ match.awayTeam }}</div>
              <div class="team-score">{{ match.awayScore }}</div>
              <div v-if="!match.homePossession" class="ball-indicator">🏀</div>
            </div>
          </div>

          <div class="match-status">
            <span class="quarter">📋 第 {{ match.currentQuarter }} 节</span>
            <span class="timer">⏱ {{ formatTime(match.remainingTime) }}</span>
            <span class="possession">
              🏀 {{ match.homePossession ? '主队控球' : '客队控球' }}
            </span>
          </div>

          <div class="controls">
            <button @click="toggleMatch" :class="['btn', match.status === '进行中' ? 'btn-stop' : 'btn-start']">
              {{ match.status === '进行中' ? '⏸ 暂停' : '▶ 开始' }}
            </button>
            <button @click="resetMatch" class="btn btn-reset">🔄 重置</button>
            <button @click="updateMatch" class="btn btn-update">⚡ 手动更新</button>
          </div>
        </div>

        <div v-else class="create-section">
          <div class="create-icon">🏟️</div>
          <h3>还没有比赛数据</h3>
          <p>创建一场模拟比赛来开始</p>
          <button @click="createMatch" class="btn-create">➕ 创建模拟比赛</button>
        </div>

        <div class="update-log">
          <div class="log-header">
            <h4>📝 更新日志</h4>
            <span v-if="updateLogs.length > 0" class="log-count">{{ updateLogs.length }}</span>
          </div>
          <div class="log-list">
            <div v-for="(log, index) in updateLogs" :key="index" class="log-item">
              <span class="log-time">{{ log.time }}</span>
              <span class="log-content">{{ log.content }}</span>
            </div>
            <div v-if="updateLogs.length === 0" class="log-empty">
              <span>📭</span>
              <span>暂无更新记录</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from '../utils/axios'
import AppLayout from '../components/AppLayout.vue'

const match = ref(null)
const updateLogs = ref([])
let updateInterval = null

const statusText = computed(() => {
  if (!match.value) return ''
  const statusMap = {
    '未开始': '⏳ 未开始',
    '进行中': '⚡ 进行中',
    '暂停': '⏸️ 暂停',
    '已结束': '🏆 已结束'
  }
  return statusMap[match.value.status] || match.value.status
})

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const addLog = (content) => {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
  updateLogs.value.unshift({ time, content })
  if (updateLogs.value.length > 20) {
    updateLogs.value.pop()
  }
}

const createMatch = async () => {
  try {
    const response = await axios.post('/api/match/create')
    if (response.code === 200) {
      match.value = response.data
      addLog(`比赛创建成功: ${match.value.matchName}`)
    }
  } catch (error) {
    console.error('创建比赛失败:', error)
  }
}

const toggleMatch = async () => {
  try {
    const response = await axios.put(`/api/match/${match.value.id}/toggle`)
    if (response.code === 200) {
      match.value = response.data
      addLog(`比赛状态变更: ${statusText.value}`)
      if (match.value.status === '进行中') {
        startAutoUpdate()
      } else {
        stopAutoUpdate()
      }
    }
  } catch (error) {
    console.error('切换比赛状态失败:', error)
  }
}

const resetMatch = async () => {
  try {
    const response = await axios.put(`/api/match/${match.value.id}/reset`)
    if (response.code === 200) {
      match.value = response.data
      stopAutoUpdate()
      addLog('比赛已重置')
    }
  } catch (error) {
    console.error('重置比赛失败:', error)
  }
}

const updateMatch = async () => {
  try {
    const response = await axios.put(`/api/match/${match.value.id}/update`)
    if (response.code === 200) {
      const oldHome = match.value.homeScore
      const oldAway = match.value.awayScore
      match.value = response.data
      
      if (match.value.homeScore > oldHome) {
        addLog(`🏀 ${match.value.homeTeam} 得分！${oldHome} → ${match.value.homeScore}`)
      } else if (match.value.awayScore > oldAway) {
        addLog(`🏀 ${match.value.awayTeam} 得分！${oldAway} → ${match.value.awayScore}`)
      } else if (match.value.status === '已结束') {
        addLog(`🏆 比赛结束！${match.value.homeTeam} ${match.value.homeScore} - ${match.value.awayScore} ${match.value.awayTeam}`)
        stopAutoUpdate()
      } else {
        addLog(`时间更新: ${formatTime(match.value.remainingTime)}`)
      }
    }
  } catch (error) {
    console.error('更新比赛失败:', error)
  }
}

const startAutoUpdate = () => {
  stopAutoUpdate()
  updateInterval = setInterval(() => {
    updateMatch()
  }, 1000)
  addLog('⏰ 自动更新已启动')
}

const stopAutoUpdate = () => {
  if (updateInterval) {
    clearInterval(updateInterval)
    updateInterval = null
    addLog('⏹ 自动更新已停止')
  }
}

onMounted(() => {
  addLog('页面加载完成')
})

onUnmounted(() => {
  stopAutoUpdate()
})
</script>

<style scoped>
.page-banner {
  position: relative;
  background: linear-gradient(135deg, var(--color-dark-900) 0%, var(--color-dark-700) 50%, var(--color-danger-900) 100%);
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
  background: var(--color-danger-400);
  top: -50%;
  right: -10%;
  animation: orbFloat 8s ease-in-out infinite;
}

.banner-orb-2 {
  width: 200px;
  height: 200px;
  background: var(--color-warm-400);
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
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.25);
  border-radius: var(--radius-full);
  color: var(--color-danger-300);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  margin-bottom: 1rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-danger-400);
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
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.match-layout {
  padding: 2rem 0 3rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.match-info {
  background: linear-gradient(135deg, var(--color-primary-800) 0%, var(--color-primary-600) 100%);
  border-radius: var(--radius-2xl);
  padding: 1.75rem;
  color: #fff;
  box-shadow: var(--shadow-xl);
}

.match-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.match-header h3 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  opacity: 0.9;
}

.status-badge {
  padding: 0.3rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
}

.status-badge.未开始 { background: var(--color-warm-500); }
.status-badge.进行中 {
  background: var(--color-accent-500);
  animation: statusPulse 1.5s ease-in-out infinite;
}

.status-badge.暂停 { background: var(--color-neutral-500); }
.status-badge.已结束 { background: #8b5cf6; }

@keyframes statusPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.65; }
}

.score-board {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.25rem;
}

.team {
  flex: 1;
  text-align: center;
  padding: 1.25rem 1rem;
  border-radius: var(--radius-xl);
  background: rgba(255, 255, 255, 0.08);
  transition: all var(--transition-base);
  position: relative;
}

.team.possession {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 24px rgba(255, 255, 255, 0.15);
  transform: scale(1.03);
}

.team-name {
  font-size: var(--font-size-base);
  margin-bottom: 0.5rem;
  opacity: 0.85;
  font-weight: var(--font-weight-medium);
}

.team-score {
  font-size: 3rem;
  font-weight: var(--font-weight-extrabold);
  line-height: 1;
}

.ball-indicator {
  margin-top: 0.5rem;
  font-size: 1.2rem;
  animation: bounce 0.6s ease-in-out infinite alternate;
}

@keyframes bounce {
  from { transform: translateY(0); }
  to { transform: translateY(-6px); }
}

.vs {
  font-size: 1.25rem;
  font-weight: var(--font-weight-bold);
  margin: 0 1rem;
  opacity: 0.6;
}

.match-status {
  display: flex;
  justify-content: space-around;
  padding: 0.75rem 1rem;
  background: rgba(0, 0, 0, 0.15);
  border-radius: var(--radius-lg);
  margin-bottom: 1.25rem;
  font-size: var(--font-size-sm);
}

.timer {
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 1.1rem;
  font-weight: var(--font-weight-semibold);
  letter-spacing: 0.05em;
}

.controls {
  display: flex;
  gap: 0.6rem;
}

.btn {
  flex: 1;
  padding: 0.7rem 1rem;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: inherit;
}

.btn:hover {
  transform: translateY(-2px);
}

.btn-start {
  background: var(--color-accent-500);
  color: var(--color-dark-900);
}

.btn-start:hover {
  box-shadow: 0 4px 16px rgba(34, 197, 94, 0.5);
}

.btn-stop {
  background: var(--color-warm-500);
  color: #fff;
}

.btn-stop:hover {
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.5);
}

.btn-reset {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.btn-reset:hover {
  background: rgba(255, 255, 255, 0.3);
}

.btn-update {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.btn-update:hover {
  background: rgba(255, 255, 255, 0.3);
}

.create-section {
  text-align: center;
  padding: 3.5rem 2rem;
  background: #fff;
  border-radius: var(--radius-2xl);
  border: 2px dashed var(--color-neutral-200);
  box-shadow: var(--shadow-md);
}

.create-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.create-section h3 {
  margin: 0 0 0.5rem;
  font-size: var(--font-size-xl);
  color: var(--color-neutral-800);
}

.create-section p {
  color: var(--color-neutral-400);
  margin: 0 0 1.5rem;
  font-size: var(--font-size-sm);
}

.btn-create {
  padding: 0.8rem 2rem;
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-primary-600) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: inherit;
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.update-log {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.25rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.log-header h4 {
  margin: 0;
  color: var(--color-neutral-800);
  font-size: var(--font-size-base);
}

.log-count {
  font-size: var(--font-size-xs);
  background: var(--color-neutral-100);
  padding: 0.15rem 0.5rem;
  border-radius: var(--radius-full);
  color: var(--color-neutral-500);
}

.log-list {
  max-height: 220px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-neutral-100);
  font-size: var(--font-size-xs);
}

.log-item:last-child {
  border-bottom: none;
}

.log-time {
  color: var(--color-neutral-400);
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  min-width: 72px;
  flex-shrink: 0;
}

.log-content {
  color: var(--color-neutral-600);
}

.log-empty {
  text-align: center;
  color: var(--color-neutral-400);
  padding: 1.5rem;
  font-size: var(--font-size-sm);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
}

@media (max-width: 600px) {
  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .team-score {
    font-size: 2.25rem;
  }

  .controls {
    flex-wrap: wrap;
  }

  .btn {
    min-width: calc(50% - 0.3rem);
    flex: none;
  }
}
</style>