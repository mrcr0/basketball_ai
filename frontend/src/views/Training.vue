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
            <span>训练记录</span>
          </span>
          <h1 class="banner-title">🏀 训练打卡</h1>
          <p class="banner-desc">记录每一次训练，追踪进步轨迹，用数据见证你的成长</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="training-layout">
        <div class="stats-bar">
          <div class="stat-item">
            <div class="stat-value">{{ totalRecords }}</div>
            <div class="stat-label">累计打卡</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ totalDuration }}</div>
            <div class="stat-label">累计时长(分)</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ avgHitRate }}%</div>
            <div class="stat-label">平均命中率</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ totalCalories }}</div>
            <div class="stat-label">消耗(卡路里)</div>
          </div>
        </div>

        <div class="content-grid">
          <div class="form-card">
            <h2>📝 今日训练</h2>
            <div class="training-form">
              <el-form :model="form" label-width="100px" size="default">
                <el-form-item label="训练项目">
                  <el-select v-model="form.trainingItem" placeholder="选择训练项目">
                    <el-option label="🏀 运球训练" value="运球训练" />
                    <el-option label="🎯 投篮训练" value="投篮训练" />
                    <el-option label="🤝 传球训练" value="传球训练" />
                    <el-option label="🛡️ 防守训练" value="防守训练" />
                    <el-option label="💪 体能训练" value="体能训练" />
                    <el-option label="📋 战术配合" value="战术配合" />
                  </el-select>
                </el-form-item>

                <div class="shooting-section">
                  <h3 class="section-label">🎯 命中率计算器</h3>
                  <div class="form-row three-col">
                    <div class="form-field">
                      <label class="field-label">投中数</label>
                      <el-input v-model.number="form.hitCount" placeholder="命中次数" @input="calcHitRate">
                        <template #append>球</template>
                      </el-input>
                    </div>
                    <div class="form-field">
                      <label class="field-label">总投篮数</label>
                      <el-input v-model.number="form.totalShots" placeholder="总出手次数" @input="calcHitRate">
                        <template #append>球</template>
                      </el-input>
                    </div>
                    <div class="form-field">
                      <label class="field-label">命中率</label>
                      <div class="hit-rate-display" :class="getHitRateClass()">
                        <span class="rate-value">{{ calcHitRateText() }}</span>
                        <span class="rate-unit">%</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="time-section">
                  <h3 class="section-label">⏱️ 运动时间选择器</h3>
                  <div class="time-selector-row">
                    <div class="time-field">
                      <label class="field-label">时</label>
                      <el-select v-model="form.durationHours" @change="updateDuration">
                        <el-option v-for="h in 24" :key="h-1" :label="String(h-1).padStart(2,'0')" :value="h-1" />
                      </el-select>
                    </div>
                    <span class="time-sep">:</span>
                    <div class="time-field">
                      <label class="field-label">分</label>
                      <el-select v-model="form.durationMinutes" @change="updateDuration">
                        <el-option v-for="m in 60" :key="m-1" :label="String(m-1).padStart(2,'0')" :value="m-1" />
                      </el-select>
                    </div>
                    <span class="time-sep">:</span>
                    <div class="time-field">
                      <label class="field-label">秒</label>
                      <el-select v-model="form.durationSeconds" @change="updateDuration">
                        <el-option v-for="s in 60" :key="s-1" :label="String(s-1).padStart(2,'0')" :value="s-1" />
                      </el-select>
                    </div>
                    <div class="time-total">
                      <span class="total-label">总计</span>
                      <span class="total-value">{{ form.durationMinutes }}分{{ form.durationSeconds }}秒</span>
                    </div>
                  </div>
                </div>

                <div class="form-row">
                  <el-form-item label="训练强度">
                    <el-select v-model="form.intensity" @change="calcCalories">
                  <el-option label="🟢 低" value="低" />
                  <el-option label="🟡 中" value="中" />
                  <el-option label="🔴 高" value="高" />
                </el-select>
                  </el-form-item>
                  <el-form-item label="消耗(卡路里)">
                    <el-input v-model.number="form.calories" placeholder="自动计算" disabled />
                  </el-form-item>
                </div>
                <div class="completion-section">
                  <h3 class="section-label">📊 完成度</h3>
                  <div class="slider-row">
                    <el-slider v-model="form.completionRate" :min="0" :max="100" :step="1" show-input />
                  </div>
                </div>

                <div class="calories-section">
                  <h3 class="section-label">🔥 卡路里消耗（自动计算）</h3>
                  <div class="calories-display">
                    <div class="calories-big">{{ calcCaloriesText() }}</div>
                    <div class="calories-unit">千卡 (kcal)</div>
                  </div>
                  <p class="calories-formula">基于 NBA 训练标准 · MET公式 · 科学计算</p>
                </div>
                <el-form-item label="训练备注">
                  <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="记录训练感受..." />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitRecord" class="submit-btn">
                    ✅ 完成打卡
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>

          <div class="chart-card">
            <h2>📊 训练数据可视化</h2>
            <div ref="chartRef" class="chart"></div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { createRecord, getUserRecords } from '../api/record'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import AppLayout from '../components/AppLayout.vue'

const form = reactive({
  trainingItem: '运球训练',
  hitCount: null,
  totalShots: null,
  hitRate: null,
  durationHours: 0,
  durationMinutes: 30,
  durationSeconds: 0,
  duration: null,
  intensity: '中',
  calories: null,
  completionRate: 70,
  notes: ''
})

const calcHitRate = () => {
  if (form.hitCount != null && form.totalShots != null && form.totalShots > 0) {
    form.hitRate = Math.round((form.hitCount / form.totalShots) * 10000) / 100
  } else {
    form.hitRate = null
  }
}

const calcHitRateText = () => {
  if (form.hitRate != null && form.hitRate !== undefined && !isNaN(form.hitRate)) {
    return form.hitRate.toFixed(1)
  }
  return '--'
}

const getHitRateClass = () => {
  if (form.hitRate == null || form.hitRate === undefined || isNaN(form.hitRate)) return ''
  if (form.hitRate >= 80) return 'excellent'
  if (form.hitRate >= 60) return 'good'
  if (form.hitRate >= 40) return 'average'
  return 'poor'
}

const updateDuration = () => {
  form.duration = form.durationHours * 60 + form.durationMinutes + form.durationSeconds / 60
  calcCalories()
}

const calcCalories = () => {
  const metMap = { '低': 4.5, '中': 6.5, '高': 8.0 }
  const met = metMap[form.intensity] || 6.5
  const totalHours = form.durationHours + form.durationMinutes / 60 + form.durationSeconds / 3600
  const weight = 75
  if (totalHours <= 0) { form.calories = 0; return }
  form.calories = Math.round(met * weight * totalHours)
}

const calcCaloriesText = () => {
  if (form.calories == null || form.calories === undefined || isNaN(form.calories)) {
    calcCalories()
  }
  return form.calories || '--'
}

const records = ref([])
const chartRef = ref(null)

const totalRecords = computed(() => records.value.length)
const totalDuration = computed(() => records.value.reduce((sum, r) => sum + (r.duration || 0), 0))
const avgHitRate = computed(() => {
  const valid = records.value.filter(r => r.hitRate)
  if (valid.length === 0) return 0
  return Math.round(valid.reduce((sum, r) => sum + r.hitRate, 0) / valid.length)
})
const totalCalories = computed(() => records.value.reduce((sum, r) => sum + (r.calories || 0), 0))

const loadRecords = async () => {
  try {
    const response = await getUserRecords()
    if (response.code === 200) {
      records.value = response.data
      nextTick(() => {
        renderChart()
      })
    }
  } catch (error) {
    console.error('加载记录失败', error)
  }
}

const submitRecord = async () => {
  try {
    const response = await createRecord(form)
    if (response.code === 200) {
      ElMessage.success('打卡成功')
      records.value.unshift(response.data)
      form.hitRate = null
      form.duration = null
      form.calories = null
      form.completionRate = null
      form.notes = ''
      nextTick(() => {
        renderChart()
      })
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('打卡失败：' + error.message)
  }
}

const renderChart = () => {
  if (!chartRef.value) return
  
  const chart = echarts.init(chartRef.value)
  
  const dates = [...new Set(records.value.map(r => {
    const date = new Date(r.checkInTime)
    return `${date.getMonth() + 1}/${date.getDate()}`
  }))].slice(-7)
  
  const dataByDate = {}
  records.value.forEach(r => {
    const date = new Date(r.checkInTime)
    const key = `${date.getMonth() + 1}/${date.getDate()}`
    if (!dataByDate[key]) {
      dataByDate[key] = { duration: 0, count: 0 }
    }
    dataByDate[key].duration += r.duration || 0
    dataByDate[key].count++
  })
  
  const durationData = dates.map(d => dataByDate[d]?.duration || 0)
  const countData = dates.map(d => dataByDate[d]?.count || 0)
  
  chart.setOption({
    title: {
      text: '近7天训练统计',
      left: 'center',
      textStyle: { color: '#374151', fontSize: 14, fontWeight: 600 }
    },
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['训练时长(分钟)', '打卡次数'],
      bottom: 0,
      textStyle: { fontSize: 12 }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '18%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#6b7280' }
    },
    yAxis: [
      {
        type: 'value',
        name: '时长',
        nameTextStyle: { color: '#6b7280' },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: '#f3f4f6' } }
      },
      {
        type: 'value',
        name: '次数',
        nameTextStyle: { color: '#6b7280' },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '训练时长(分钟)',
        type: 'bar',
        data: durationData,
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: '#4ade80'
        }
      },
      {
        name: '打卡次数',
        type: 'line',
        yAxisIndex: 1,
        data: countData,
        smooth: true,
        itemStyle: { color: '#3b82f6' },
        lineStyle: { width: 2 },
        symbol: 'circle',
        symbolSize: 6
      }
    ]
  })
}

onMounted(() => {
  loadRecords()
  updateDuration()
})
</script>

<style scoped>
.page-banner {
  position: relative;
  background: linear-gradient(135deg, var(--color-dark-900) 0%, var(--color-dark-700) 50%, var(--color-accent-900) 100%);
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
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.training-layout {
  padding: 2rem 0 3rem;
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  text-align: center;
  padding: 1.25rem 0.75rem;
  background: #fff;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
  transition: all var(--transition-base);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-xl);
}

.stat-value {
  font-size: 2rem;
  font-weight: var(--font-weight-extrabold);
  color: var(--color-accent-500);
  line-height: 1.2;
}

.stat-label {
  color: var(--color-neutral-400);
  font-size: var(--font-size-xs);
  margin-top: 0.25rem;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  align-items: start;
}

.form-card,
.chart-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.5rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.form-card h2,
.chart-card h2 {
  margin: 0 0 1.25rem;
  color: var(--color-neutral-800);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 1rem;
}

.form-row.three-col {
  grid-template-columns: 1fr 1fr 1fr;
}

.shooting-section,
.time-section,
.completion-section,
.calories-section {
  margin-bottom: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.section-label {
  margin: 0 0 0.75rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #334155;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.field-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.hit-rate-display {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.15rem;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0 0.5rem;
}

.rate-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: #64748b;
}

.rate-unit {
  font-size: 0.8rem;
  color: #94a3b8;
}

.hit-rate-display.excellent { background: #dcfce7; border-color: #86efac; }
.hit-rate-display.excellent .rate-value { color: #16a34a; }
.hit-rate-display.good { background: #dbeafe; border-color: #93c5fd; }
.hit-rate-display.good .rate-value { color: #2563eb; }
.hit-rate-display.average { background: #fef9c3; border-color: #fde047; }
.hit-rate-display.average .rate-value { color: #ca8a04; }
.hit-rate-display.poor { background: #fee2e2; border-color: #fca5a5; }
.hit-rate-display.poor .rate-value { color: #dc2626; }

.time-selector-row {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
}

.time-field {
  flex: 1;
  max-width: 80px;
}

.time-field :deep(.el-select) {
  width: 100%;
}

.time-sep {
  font-size: 1.25rem;
  font-weight: 700;
  color: #94a3b8;
  padding-bottom: 0.5rem;
}

.time-total {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
  padding-bottom: 0.3rem;
}

.total-label {
  font-size: 0.7rem;
  color: #94a3b8;
  font-weight: 600;
}

.total-value {
  font-size: 0.95rem;
  font-weight: 700;
  color: #f97316;
}

.slider-row {
  padding: 0.25rem 0;
}

.slider-row :deep(.el-slider) {
  --el-slider-main-bg-color: #22c55e;
  --el-slider-stop-bg-color: #22c55e;
}

.slider-row :deep(.el-slider__input) {
  width: 60px;
}

.calories-display {
  text-align: center;
  padding: 0.75rem 0 0.5rem;
}

.calories-big {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #f97316, #ef4444);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.1;
  transition: all 0.3s;
}

.calories-unit {
  font-size: 0.8rem;
  color: #94a3b8;
  margin-top: 0.15rem;
}

.calories-formula {
  text-align: center;
  font-size: 0.65rem;
  color: #cbd5e1;
  margin: 0.3rem 0 0;
}

.submit-btn {
  width: 100%;
  padding: 0.7rem 1.5rem;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
}

.chart {
  height: 320px;
}

@media (max-width: 768px) {
  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .stats-bar {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-bar {
    gap: 0.5rem;
  }

  .stat-value {
    font-size: 1.5rem;
  }
}
</style>