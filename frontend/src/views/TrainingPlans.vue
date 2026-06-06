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
            <span>AI训练</span>
          </span>
          <h1 class="banner-title">🏀 训练计划</h1>
          <p class="banner-desc">基于AI智能分析，为你量身定制科学系统的篮球训练计划</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="plans-content" v-if="!showGenerateForm">
        <div class="actions">
          <button @click="showGenerateForm = true" class="generate-btn">
            <span>🤖</span>
            <span>生成AI训练计划</span>
          </button>
        </div>

        <div class="time-picker-section">
          <h3 class="section-subtitle">📅 时间维度选择</h3>
          <TimeDimensionPicker v-model="selectedDate" @dimension-change="onDimensionChange" />
          <p class="dim-hint">当前视图：<strong>{{ dimensionLabels[currentDimension] }}</strong> · 选中：{{ formatSelectedDate() }}</p>
        </div>

        <div v-if="plans.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <h3>暂无训练计划</h3>
          <p>点击上方按钮，让AI为你生成专业的训练计划</p>
        </div>

        <div v-else class="plans-grid">
          <div v-for="plan in plans" :key="plan.id" class="plan-card">
            <div class="plan-header">
              <h3>{{ plan.planName }}</h3>
              <span :class="['tag', plan.isCustomized === 1 ? 'customized' : 'ai']">
                {{ plan.isCustomized === 1 ? '已自定义' : 'AI生成' }}
              </span>
            </div>
            <div class="plan-meta">
              <span class="meta-item">
                <span class="meta-icon">📅</span>
                {{ plan.trainingCycle }}计划
              </span>
              <span class="meta-item">
                <span class="meta-icon">🔄</span>
                {{ plan.cycleCount }}周期
              </span>
              <span class="meta-item" v-if="plan.specialFocus">
                <span class="meta-icon">🎯</span>
                {{ plan.specialFocus }}
              </span>
            </div>
            <div class="plan-content">
              <PlanDocumentView :plan="plan" />
            </div>
            <div class="plan-actions">
              <button @click="viewPlan(plan)" class="view-btn">查看详情</button>
              <button @click="handleDeletePlan(plan.id)" class="delete-btn">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="generate-form">
        <div class="form-header">
          <h2>🤖 生成AI训练计划</h2>
          <p>填写你的基本信息，AI将为你生成专属训练计划</p>
        </div>

        <el-form :model="form" label-width="120px" class="form-body">
          <div class="form-grid">
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
            <el-form-item label="身高(cm)">
              <el-input v-model.number="form.height" placeholder="例如：180" />
            </el-form-item>
            <el-form-item label="体重(kg)">
              <el-input v-model.number="form.weight" placeholder="例如：75" />
            </el-form-item>
            <el-form-item label="球龄(年)">
              <el-input v-model.number="form.experienceYears" placeholder="例如：2" />
            </el-form-item>
            <el-form-item label="训练周期">
              <el-select v-model="form.cycleType">
                <el-option label="📅 年计划" value="年" />
                <el-option label="📅 月计划" value="月" />
                <el-option label="📅 周计划" value="周" />
                <el-option label="📅 日计划" value="日" />
              </el-select>
            </el-form-item>
            <el-form-item label="周期数量">
              <el-select v-model.number="form.cycleCount" :disabled="form.cycleType === '年'">
                <el-option
                  v-for="n in maxCycleCount"
                  :key="n"
                  :label="n + ' ' + form.cycleType"
                  :value="n"
                />
              </el-select>
            </el-form-item>
          </div>
          <el-form-item label="薄弱项">
            <TagSelectInput
              v-model="form.weakPoints"
              :options="weakPointsOptions"
              placeholder="输入自定义薄弱项后按回车添加..."
            />
          </el-form-item>
          <el-form-item label="训练目标">
            <TagSelectInput
              v-model="form.trainingGoal"
              :options="trainingGoalOptions"
              placeholder="输入自定义训练目标后按回车添加..."
            />
          </el-form-item>
          <div class="form-actions">
            <button @click="showGenerateForm = false" class="cancel-btn" type="button">返回列表</button>
            <el-button type="primary" @click="generatePlan" :loading="loading" class="submit-plan-btn">
              {{ loading ? '⏳ 生成中...' : '🚀 生成训练计划' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="selectedPlan" class="modal-overlay" @click.self="selectedPlan = null">
          <div class="modal-container">
            <div class="modal-header">
              <h3>{{ selectedPlan.planName }}</h3>
              <button @click="selectedPlan = null" class="modal-close">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <PlanDocumentView :plan="selectedPlan" :full="true" />
            </div>
            <div class="modal-footer">
              <button @click="selectedPlan = null" class="close-plan-btn">关闭</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </AppLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { generateAIPlan, getUserPlans, deletePlan as apiDeletePlan } from '../api/plan'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/AppLayout.vue'
import TimeDimensionPicker from '../components/TimeDimensionPicker.vue'
import PlanDocumentView from '../components/PlanDocumentView.vue'
import TagSelectInput from '../components/TagSelectInput.vue'

const showGenerateForm = ref(false)
const plans = ref([])
const selectedPlan = ref(null)
const loading = ref(false)
const selectedDate = ref(new Date())
const currentDimension = ref('month')

const dimensionLabels = { year: '年视图', month: '月视图', week: '周视图', day: '日视图' }

const weakPointsOptions = [
  { label: '投篮', value: '投篮', icon: '🎯' },
  { label: '运球', value: '运球', icon: '🤚' },
  { label: '传球', value: '传球', icon: '🤝' },
  { label: '防守', value: '防守', icon: '🛡️' },
  { label: '篮板', value: '篮板', icon: '🏀' },
  { label: '体能', value: '体能', icon: '💪' },
  { label: '速度', value: '速度', icon: '⚡' },
  { label: '弹跳', value: '弹跳', icon: '🦘' },
  { label: '脚步移动', value: '脚步移动', icon: '👣' },
  { label: '篮下终结', value: '篮下终结', icon: '🏀' },
  { label: '三分球', value: '三分球', icon: '🎯' },
  { label: '罚球', value: '罚球', icon: '📏' }
]

const trainingGoalOptions = [
  { label: '提升投篮命中率', value: '提升投篮命中率', icon: '🎯' },
  { label: '增强运球突破能力', value: '增强运球突破能力', icon: '⚡' },
  { label: '强化防守能力', value: '强化防守能力', icon: '🛡️' },
  { label: '提高体能耐力', value: '提高体能耐力', icon: '💪' },
  { label: '改善传球视野', value: '改善传球视野', icon: '👁️' },
  { label: '提升篮板能力', value: '提升篮板能力', icon: '🏀' },
  { label: '增强核心力量', value: '增强核心力量', icon: '🏋️' },
  { label: '提高弹跳高度', value: '提高弹跳高度', icon: '🦘' },
  { label: '改善脚步灵活性', value: '改善脚步灵活性', icon: '👣' },
  { label: '提升比赛意识', value: '提升比赛意识', icon: '🧠' },
  { label: '减脂增肌', value: '减脂增肌', icon: '🔥' },
  { label: '伤病恢复训练', value: '伤病恢复训练', icon: '🏥' }
]

const onDimensionChange = (dim) => { currentDimension.value = dim }

const formatSelectedDate = () => {
  const d = selectedDate.value
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const maxCycleCount = computed(() => {
  switch (form.cycleType) {
    case '日': return 30
    case '周': return 52
    case '月': return 12
    case '年': return 1
    default: return 12
  }
})

const form = reactive({
  position: '后卫',
  skillLevel: '新手入门',
  weakPoints: '',
  trainingGoal: '',
  height: null,
  weight: null,
  experienceYears: 0,
  cycleType: '周',
  cycleCount: 1
})

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

const generatePlan = async () => {
  loading.value = true
  try {
    const response = await generateAIPlan(form)
    if (response.code === 200) {
      ElMessage.success('训练计划生成成功')
      plans.value.unshift(response.data)
      showGenerateForm.value = false
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('生成失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const viewPlan = (plan) => {
  selectedPlan.value = plan
}

const handleDeletePlan = async (id) => {
  if (!confirm('确定删除这个训练计划吗？')) return
  
  try {
    const response = await apiDeletePlan(id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      plans.value = plans.value.filter(p => p.id !== id)
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('删除失败：' + error.message)
  }
}

onMounted(() => {
  loadPlans()
})
</script>

<style scoped>
.page-banner {
  position: relative;
  background: linear-gradient(135deg, var(--color-dark-900) 0%, var(--color-dark-700) 50%, #3b0764 100%);
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
  background: #7c3aed;
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
  background: rgba(124, 58, 237, 0.12);
  border: 1px solid rgba(124, 58, 237, 0.25);
  border-radius: var(--radius-full);
  color: #a78bfa;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  margin-bottom: 1rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #a78bfa;
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

.plans-content {
  padding: 2rem 0 3rem;
}

.actions {
  margin-bottom: 1.5rem;
}

.generate-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.9rem 2rem;
  background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: inherit;
  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.3);
}

.generate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(124, 58, 237, 0.45);
}

.time-picker-section {
  margin-bottom: 1.5rem;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.25rem;
  border: 1px solid var(--color-neutral-200);
  box-shadow: var(--shadow-md);
}

.section-subtitle {
  margin: 0 0 0.75rem;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-800);
}

.dim-hint {
  margin: 0.75rem 0 0;
  font-size: var(--font-size-sm);
  color: var(--color-neutral-400);
}

.dim-hint strong {
  color: var(--color-neutral-600);
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: #fff;
  border-radius: var(--radius-xl);
  border: 2px dashed var(--color-neutral-200);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 0.75rem;
}

.empty-state h3 {
  margin: 0 0 0.5rem;
  color: var(--color-neutral-700);
  font-size: var(--font-size-xl);
}

.empty-state p {
  color: var(--color-neutral-400);
  margin: 0;
}

.plans-grid {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.plan-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.5rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
  transition: all var(--transition-base);
}

.plan-card:hover {
  box-shadow: var(--shadow-xl);
  border-color: var(--color-primary-200);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.plan-header h3 {
  margin: 0;
  color: var(--color-neutral-800);
  font-size: var(--font-size-lg);
}

.tag {
  padding: 0.3rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.tag.ai { background: var(--color-info-light); color: var(--color-info); }
.tag.customized { background: var(--color-warning-light); color: var(--color-warning); }

.plan-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.meta-item {
  font-size: var(--font-size-sm);
  color: var(--color-neutral-500);
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.meta-icon {
  font-size: 0.9rem;
}

.plan-content {
  background: var(--color-neutral-50);
  padding: 1rem;
  border-radius: var(--radius-lg);
  max-height: 180px;
  overflow-y: auto;
  margin-bottom: 1rem;
  border: 1px solid var(--color-neutral-100);
}

.plan-content pre {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  line-height: var(--line-height-relaxed);
}

.plan-actions {
  display: flex;
  gap: 0.75rem;
}

.view-btn {
  padding: 0.5rem 1.25rem;
  background: var(--color-primary-500);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.view-btn:hover {
  background: var(--color-primary-600);
}

.delete-btn {
  padding: 0.5rem 0.75rem;
  background: var(--color-danger-light);
  color: var(--color-danger);
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
}

.delete-btn:hover {
  background: var(--color-danger);
  color: #fff;
}

.generate-form {
  padding: 2rem 0 3rem;
}

.form-header {
  margin-bottom: 1.5rem;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.5rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.form-header h2 {
  margin: 0 0 0.35rem;
  color: var(--color-neutral-800);
  font-size: var(--font-size-xl);
}

.form-header p {
  margin: 0;
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
}

.form-body {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.75rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 1rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
  padding-top: 1.25rem;
  border-top: 1px solid var(--color-neutral-100);
}

.cancel-btn {
  padding: 0.65rem 1.5rem;
  background: var(--color-neutral-100);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  font-weight: var(--font-weight-medium);
}

.cancel-btn:hover {
  background: var(--color-neutral-200);
}

.submit-plan-btn {
  padding: 0.65rem 1.5rem;
  font-weight: var(--font-weight-semibold);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-container {
  background: #fff;
  border-radius: var(--radius-2xl);
  max-width: 720px;
  width: 100%;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.75rem;
  border-bottom: 1px solid var(--color-neutral-200);
  background: linear-gradient(135deg, #f5f3ff 0%, var(--color-primary-50) 100%);
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: var(--font-size-xl);
  color: var(--color-neutral-800);
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: var(--color-neutral-100);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-neutral-500);
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--color-danger-light);
  color: var(--color-danger);
}

.modal-body {
  padding: 1.5rem 1.75rem;
  overflow-y: auto;
  flex: 1;
}

.modal-body pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  line-height: var(--line-height-relaxed);
  font-family: inherit;
}

.modal-footer {
  padding: 1rem 1.75rem;
  border-top: 1px solid var(--color-neutral-200);
  display: flex;
  justify-content: flex-end;
}

.close-plan-btn {
  padding: 0.6rem 1.5rem;
  background: var(--color-neutral-100);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-neutral-600);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.close-plan-btn:hover {
  background: var(--color-neutral-200);
}

.modal-enter-active,
.modal-leave-active {
  transition: all var(--transition-base);
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: all var(--transition-bounce);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container {
  transform: scale(0.95) translateY(20px);
  opacity: 0;
}

.modal-leave-to .modal-container {
  transform: scale(0.95) translateY(20px);
  opacity: 0;
}

@media (max-width: 768px) {
  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .plan-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
}

@media (max-width: 480px) {
  .form-actions {
    flex-direction: column;
  }

  .plan-actions {
    flex-direction: column;
  }
}
</style>