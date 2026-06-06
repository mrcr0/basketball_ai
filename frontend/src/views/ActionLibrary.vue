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
            <span>专业动作库</span>
          </span>
          <h1 class="banner-title">🎯 篮球动作库</h1>
          <p class="banner-desc">涵盖运球、投篮、突破、防守等全方位篮球技术动作，每个动作配有详细训练要点</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="filters-section">
        <div class="filter-group">
          <span class="filter-label">技术分类</span>
          <div class="filter-pills">
            <button
              v-for="cat in categories"
              :key="cat"
              :class="['filter-pill', { active: selectedCategory === cat }]"
              @click="selectedCategory = cat"
            >
              {{ cat }}
            </button>
          </div>
        </div>
        <div class="filter-group">
          <span class="filter-label">难度等级</span>
          <div class="filter-pills">
            <button
              v-for="diff in difficulties"
              :key="diff"
              :class="['filter-pill', { active: selectedDifficulty === diff }]"
              @click="selectedDifficulty = diff"
            >
              {{ diff }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="filteredActions.length > 0" class="actions-grid">
        <div
          v-for="action in filteredActions"
          :key="action.id"
          class="action-card"
          @click="viewAction(action)"
        >
          <div class="card-glow"></div>
          <div class="action-icon">{{ getActionIcon(action.category) }}</div>
          <h3 class="action-name">{{ action.name }}</h3>
          <div class="action-tags">
            <span class="tag category-tag">{{ action.category }}</span>
            <span :class="['tag', 'difficulty-tag', getDifficultyClass(action.difficulty)]">
              {{ action.difficulty }}
            </span>
          </div>
          <p class="action-desc">{{ truncate(action.trainingPoints, 80) }}</p>
          <div class="card-hint">点击查看详情 →</div>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">🔍</div>
        <h3>暂无符合条件的动作</h3>
        <p>请尝试调整筛选条件</p>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="selectedAction" class="modal-overlay" @click.self="selectedAction = null">
          <div class="modal-container">
            <div class="modal-header">
              <div class="modal-header-left">
                <span class="modal-icon">{{ getActionIcon(selectedAction.category) }}</span>
                <h2>{{ selectedAction.name }}</h2>
              </div>
              <button @click="selectedAction = null" class="modal-close">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div v-if="selectedAction.videoUrl" class="video-section">
                <h3 class="detail-section-title">🎬 教学视频（抖音）</h3>
                <div class="video-container">
                  <iframe
                    :src="getDouyinEmbedUrl(selectedAction.videoUrl)"
                    class="video-iframe"
                    allowfullscreen
                    scrolling="no"
                    frameborder="0"
                    allow="autoplay; encrypted-media; picture-in-picture"
                  ></iframe>
                </div>
                <p class="video-hint">💡 视频来源于抖音平台，请确保网络连接正常。如无法播放，可
                  <a :href="selectedAction.videoUrl" target="_blank" class="text-link">点击此处</a>查看原视频
                </p>
              </div>

              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">技术分类</span>
                  <span class="detail-value">{{ selectedAction.category }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">难度等级</span>
                  <span :class="['detail-value', 'difficulty-value', getDifficultyClass(selectedAction.difficulty)]">
                    {{ selectedAction.difficulty }}
                  </span>
                </div>
                <div v-if="selectedAction.videoUrl" class="detail-item">
                  <span class="detail-label">教学视频</span>
                  <a :href="selectedAction.videoUrl" target="_blank" class="video-link">
                    <span>▶</span>
                    <span>点击观看</span>
                  </a>
                </div>
              </div>

              <div class="detail-section">
                <h3 class="detail-section-title">📝 训练要点</h3>
                <div class="detail-content">
                  <pre>{{ selectedAction.trainingPoints }}</pre>
                </div>
              </div>

              <div class="detail-section ai-analysis" v-if="selectedAction.name">
                <h3 class="detail-section-title">
                  <span class="ai-icon">🤖</span>
                  AI动作要领分析
                </h3>
                <div class="analysis-body" v-html="getAIAnalysis(selectedAction.name)"></div>
                <div class="analysis-actions">
                  <span class="ai-tag">AI篮球大师</span>
                  <div class="speech-controls">
                    <button 
                      :class="['speak-btn', { speaking: isSpeaking }]" 
                      @click="speakAnalysis(selectedAction.name)"
                      :aria-label="isSpeaking ? '停止语音讲解' : '开始语音讲解'"
                    >
                      <span>{{ isSpeaking ? '⏹️' : '🔊' }}</span>
                      <span>{{ isSpeaking ? '停止讲解' : '语音讲解' }}</span>
                    </button>
                    <span v-if="isSpeaking" class="speaking-indicator">
                      <span class="wave wave-1"></span>
                      <span class="wave wave-2"></span>
                      <span class="wave wave-3"></span>
                    </span>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3 class="detail-section-title">⚠️ 注意事项</h3>
                <div class="detail-content tips-content">
                  <p>{{ selectedAction.tips }}</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="handleCloseModal" class="btn-close-modal">关闭</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getActions } from '../api/action'
import AppLayout from '../components/AppLayout.vue'

const categories = ['全部', '运球', '投篮', '突破', '防守', '传球', '脚步', '篮板']
const difficulties = ['全部', '新手入门', '进阶提升', '专业强化']

const selectedCategory = ref('全部')
const selectedDifficulty = ref('全部')
const actions = ref([])
const selectedAction = ref(null)

const filteredActions = computed(() => {
  return actions.value.filter(action => {
    const categoryMatch = selectedCategory.value === '全部' || action.category === selectedCategory.value
    const difficultyMatch = selectedDifficulty.value === '全部' || action.difficulty === selectedDifficulty.value
    return categoryMatch && difficultyMatch
  })
})

const loadActions = async () => {
  try {
    const response = await getActions()
    if (response.code === 200) {
      actions.value = response.data
    }
  } catch (error) {
    console.error('加载动作失败', error)
  }
}

const viewAction = (action) => {
  selectedAction.value = action
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const getDifficultyClass = (difficulty) => {
  switch (difficulty) {
    case '新手入门': return 'beginner'
    case '进阶提升': return 'intermediate'
    case '专业强化': return 'advanced'
    default: return ''
  }
}

const getActionIcon = (category) => {
  const icons = {
    '运球': '🏐',
    '投篮': '🏀',
    '突破': '⚡',
    '防守': '🛡️',
    '传球': '🤝',
    '脚步': '👟',
    '篮板': '📦'
  }
  return icons[category] || '🎯'
}

const getDouyinEmbedUrl = (url) => {
  if (!url) return ''
  const douyinIdMatch = url.match(/video\/(\d+)/i)
  if (douyinIdMatch) {
    return `https://open.douyin.com/player/video?vid=${douyinIdMatch[1]}&autoplay=0`
  }
  if (url.includes('douyin.com') || url.includes('iesdouyin.com')) {
    return url.replace('/video/', '/embed/video/')
  }
  return url
}

const actionAnalysisDB = {
  '原地单手肩上投篮': `<h4>🎯 核心要领</h4><ul><li><strong>站位：</strong>双脚与肩同宽，投篮手同侧脚略前，膝盖微屈</li><li><strong>持球：</strong>投篮手掌心空出，辅助手在球侧面稳定，球置于额头前上方</li><li><strong>发力链：</strong>腿部蹬伸→核心传导→肩部→肘部伸展→手腕下压→手指拨球</li><li><strong>出手点：</strong>球在最高点出手，手臂完全伸展，形成"鹅颈"姿势</li></ul><h4>⚠️ 常见错误</h4><ul><li>辅助手参与发力（应用手掌而非手指拨球）</li><li>肘部外展（应内收与篮筐成直线）</li><li>投篮后手臂立即收回（应保持跟随动作1秒）</li></ul><h4>📈 训练建议</h4><p>每天Form Shooting 100次（近距离单手），逐步增加距离。目标：3米处命中率>80%。</p>`,
  '行进间运球': `<h4>🎯 核心要领</h4><ul><li><strong>姿势：</strong>抬头挺胸，目光前视，膝盖弯曲保持低重心</li><li><strong>手部：</strong>用手指尖控制球，手腕放松有弹性，球弹起高度不过腰</li><li><strong>节奏：</strong>保持稳定的运球频率，快走或慢跑中运球不丢</li><li><strong>护球：</strong>非运球手张开，用身体挡在球和防守者之间</li></ul><h4>⚠️ 常见错误</h4><ul><li>低头看球（失去场上视野）</li><li>运球过高（容易被抢断）</li><li>手掌拍球（应用手指控制）</li><li>脚步不协调（手脚步频率不一致）</li></ul><h4>📈 训练建议</h4><p>全场折返运球10组，锥桶绕桩运球计时训练，目标：全场运球上篮<6秒。</p>`,
  '交叉步突破': `<h4>🎯 核心要领</h4><ul><li><strong>启动姿势：</strong>三威胁姿势（可投/可传/可突），重心低</li><li><strong>第一步：</strong>突破脚（与突破方向相反）用力蹬地，第一步要大且快</li><li><strong>第二步：</strong>交叉步跨出，身体侧转护球</li><li><strong>加速：</strong>突破后立即加速，超越防守者</li></ul><h4>⚠️ 常见错误</h4><ul><li>第一步太小（无法摆脱防守）</li><li>起步前暴露意图（眼神或身体朝向）</li><li>突破后减速（应全速完成）</li><li>未护球（球暴露在防守范围内）</li></ul><h4>📈 训练建议</h4><p>锥桶设为防守人，练习左右两侧突破。每次记录突破后上篮时间，目标<1.5秒。</p>`,
  '防守站位': `<h4>🎯 核心要领</h4><ul><li><strong>站姿：</strong>双脚宽于肩，屈膝屈髋，重心落在前脚掌，臀部后坐</li><li><strong>手臂：</strong>一手高举防投篮，一手侧展防传球/突破</li><li><strong>视线：</strong>紧盯对手腰部（最难做假动作的部位）</li><li><strong>距离：</strong>一臂距离——既能干扰投篮，又能及时反应突破</li></ul><h4>⚠️ 常见错误</h4><ul><li>站位过高（容易被过）</li><li>双脚并拢（横向移动慢）</li><li>注意力被球吸引（应看对手身体重心）</li><li>手臂下垂（无法及时干扰）</li></ul><h4>📈 训练建议</h4><p>防守滑步底线到底线<12秒，Zig-zag防守滑步配合转身练习。</p>`,
  '体前变向运球': `<h4>🎯 核心要领</h4><ul><li><strong>运球前奏：</strong>先向一侧做出突破假动作（肩膀下沉+头部假动作）</li><li><strong>变向时机：</strong>防守人重心偏移的瞬间，将球从体前快速拉向另一侧</li><li><strong>重心转移：</strong>变向后立即将重心转移到新方向脚</li><li><strong>加速：</strong>变向完成后立即加速突破</li></ul><h4>⚠️ 常见错误</h4><ul><li>变向幅度太大（球暴露时间长）</li><li>重心太高（变向后无法快速启动）</li><li>没有假动作（防守不买账）</li></ul><h4>📈 训练建议</h4><p>原地Crossover Drill 左右各50次，对墙Crossover提高速度。</p>`
}

const getAIAnalysis = (actionName) => {
  if (actionAnalysisDB[actionName]) {
    return actionAnalysisDB[actionName]
  }
  return `<h4>🎯 AI分析</h4><p>正在为该动作深度学习分析中...</p>
  <h4>📋 通用训练框架</h4>
  <ul>
    <li><strong>动作规范：</strong>确保每个环节姿势正确，质量优于数量</li>
    <li><strong>渐进训练：</strong>从慢速无对抗→中速有对抗→全速实战</li>
    <li><strong>录像分析：</strong>建议录制训练视频进行自我纠错</li>
    <li><strong>量化目标：</strong>设定可衡量的训练目标追踪进步</li>
  </ul>`
}

const isSpeaking = ref(false)
const currentUtterance = ref(null)

const stopSpeaking = () => {
  if (window.speechSynthesis) {
    window.speechSynthesis.cancel()
    isSpeaking.value = false
    currentUtterance.value = null
  }
}

const speakAnalysis = (actionName) => {
  if (!window.speechSynthesis) {
    alert('您的浏览器不支持语音合成功能')
    return
  }
  
  if (isSpeaking.value) {
    stopSpeaking()
    return
  }
  
  const analysis = getAIAnalysis(actionName)
  const text = analysis.replace(/<[^>]*>/g, ' ')
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'zh-CN'
  utterance.rate = 0.9
  utterance.pitch = 1.1
  
  utterance.onstart = () => {
    isSpeaking.value = true
    currentUtterance.value = utterance
  }
  
  utterance.onend = () => {
    isSpeaking.value = false
    currentUtterance.value = null
  }
  
  utterance.onerror = () => {
    isSpeaking.value = false
    currentUtterance.value = null
  }
  
  window.speechSynthesis.speak(utterance)
}

const handleCloseModal = () => {
  stopSpeaking()
  selectedAction.value = null
}

onMounted(() => {
  loadActions()
})

onUnmounted(() => {
  stopSpeaking()
})
</script>

<style scoped>
.page-banner {
  position: relative;
  background: linear-gradient(135deg, var(--color-dark-900) 0%, var(--color-dark-700) 50%, var(--color-primary-900) 100%);
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
  opacity: 0.15;
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
  background: rgba(74, 222, 128, 0.12);
  border: 1px solid rgba(74, 222, 128, 0.25);
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.filters-section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: 2rem 0;
  border-bottom: 1px solid var(--color-neutral-200);
  margin-bottom: 2rem;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.filter-label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-500);
  min-width: 70px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.filter-pills {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-pill {
  padding: 0.45rem 1rem;
  background: var(--color-neutral-100);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.filter-pill:hover {
  background: var(--color-neutral-200);
  color: var(--color-neutral-800);
}

.filter-pill.active {
  background: var(--color-accent-500);
  color: #fff;
  border-color: var(--color-accent-500);
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.3);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  padding-bottom: 3rem;
}

.action-card {
  position: relative;
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.75rem;
  border: 1px solid var(--color-neutral-200);
  cursor: pointer;
  transition: all var(--transition-base);
  overflow: hidden;
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-accent-400), var(--color-primary-400), var(--color-accent-400));
  background-size: 200% 100%;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-accent-300);
}

.action-card:hover .card-glow {
  opacity: 1;
  animation: shimmer 2s ease-in-out infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.action-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  transition: transform var(--transition-bounce);
}

.action-card:hover .action-icon {
  transform: scale(1.1);
}

.action-name {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-900);
  margin: 0 0 0.75rem;
}

.action-tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.tag {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.category-tag {
  background: var(--color-info-light);
  color: var(--color-info);
}

.difficulty-tag.beginner {
  background: var(--color-success-light);
  color: var(--color-success);
}

.difficulty-tag.intermediate {
  background: var(--color-warning-light);
  color: var(--color-warning);
}

.difficulty-tag.advanced {
  background: var(--color-danger-light);
  color: var(--color-danger);
}

.action-desc {
  color: var(--color-neutral-500);
  font-size: var(--font-size-sm);
  line-height: var(--line-height-normal);
  margin: 0;
}

.card-hint {
  margin-top: 1rem;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
  opacity: 0;
  transform: translateY(4px);
  transition: all var(--transition-base);
}

.action-card:hover .card-hint {
  opacity: 1;
  transform: translateY(0);
}

.empty-state {
  text-align: center;
  padding: 5rem 2rem;
  background: #fff;
  border-radius: var(--radius-xl);
  border: 1px dashed var(--color-neutral-300);
  margin-bottom: 3rem;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: var(--font-size-xl);
  color: var(--color-neutral-700);
  margin: 0 0 0.5rem;
}

.empty-state p {
  color: var(--color-neutral-400);
  margin: 0;
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
  max-width: 700px;
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
  background: linear-gradient(135deg, var(--color-accent-50) 0%, var(--color-primary-50) 100%);
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
}

.modal-header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.modal-icon {
  font-size: 1.75rem;
}

.modal-header h2 {
  margin: 0;
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-neutral-900);
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
  padding: 1.75rem;
  overflow-y: auto;
  flex: 1;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.video-section {
  margin-bottom: 1.5rem;
}

.video-container {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.video-iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
}

.video-hint {
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0;
}

.text-link {
  color: #3b82f6;
  text-decoration: underline;
}

.ai-analysis {
  background: linear-gradient(135deg, #f0f9ff, #eef2ff);
  border: 1px solid #c7d2fe;
  border-radius: 12px;
  padding: 1rem 1.25rem;
}

.ai-icon {
  font-size: 1.1rem;
}

.analysis-body {
  font-size: 0.8rem;
  color: #475569;
  line-height: 1.7;
}

.analysis-body h4 {
  margin: 0.75rem 0 0.35rem;
  font-size: 0.85rem;
  color: #4338ca;
}

.analysis-body h4:first-child {
  margin-top: 0;
}

.analysis-body ul {
  margin: 0.25rem 0;
  padding-left: 1.25rem;
}

.analysis-body li {
  margin-bottom: 0.15rem;
}

.analysis-body p {
  margin: 0.25rem 0;
}

.analysis-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.75rem;
  padding-top: 0.5rem;
  border-top: 1px solid #e0e7ff;
}

.ai-tag {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  padding: 0.2rem 0.6rem;
  border-radius: 6px;
  font-size: 0.7rem;
  font-weight: 600;
}

.speech-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.speak-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.3rem 0.75rem;
  background: #fff;
  border: 1px solid #c7d2fe;
  border-radius: 6px;
  font-size: 0.75rem;
  color: #4338ca;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
}

.speak-btn:hover {
  background: #eef2ff;
  border-color: #818cf8;
}

.speak-btn.speaking {
  background: #ef4444;
  border-color: #dc2626;
  color: #fff;
}

.speak-btn.speaking:hover {
  background: #dc2626;
  border-color: #b91c1c;
}

.speak-btn:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(67, 56, 202, 0.2);
}

.speak-btn.speaking:focus {
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
}

.speaking-indicator {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 0.25rem 0.4rem;
  background: rgba(34, 197, 94, 0.1);
  border-radius: 4px;
}

.wave {
  width: 3px;
  height: 12px;
  background: #22c55e;
  border-radius: 2px;
  animation: waveAnim 0.6s ease-in-out infinite;
}

.wave-1 {
  animation-delay: 0s;
}

.wave-2 {
  animation-delay: 0.2s;
}

.wave-3 {
  animation-delay: 0.4s;
}

@keyframes waveAnim {
  0%, 100% {
    height: 6px;
  }
  50% {
    height: 16px;
  }
}

.detail-item {
  padding: 1rem;
  background: var(--color-neutral-50);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-200);
}

.detail-label {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
  margin-bottom: 0.35rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-800);
}

.difficulty-value.beginner { color: var(--color-success); }
.difficulty-value.intermediate { color: var(--color-warning); }
.difficulty-value.advanced { color: var(--color-danger); }

.video-link {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  color: var(--color-primary-500);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: color var(--transition-fast);
}

.video-link:hover {
  color: var(--color-primary-700);
}

.detail-section {
  margin-bottom: 1.5rem;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-800);
  margin: 0 0 0.75rem;
}

.detail-content {
  background: var(--color-neutral-50);
  border-radius: var(--radius-lg);
  padding: 1.25rem;
  border: 1px solid var(--color-neutral-200);
}

.detail-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: var(--font-family-base);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-700);
  line-height: var(--line-height-relaxed);
}

.tips-content p {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  line-height: var(--line-height-relaxed);
}

.modal-footer {
  padding: 1rem 1.75rem;
  border-top: 1px solid var(--color-neutral-200);
  display: flex;
  justify-content: flex-end;
}

.btn-close-modal {
  padding: 0.6rem 1.75rem;
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

.btn-close-modal:hover {
  background: var(--color-neutral-200);
  color: var(--color-neutral-800);
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

@media (max-width: 900px) {
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .banner-title {
    font-size: var(--font-size-3xl);
  }
}

@media (max-width: 600px) {
  .actions-grid {
    grid-template-columns: 1fr;
  }

  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .banner-desc {
    font-size: var(--font-size-base);
  }

  .filter-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .modal-container {
    max-height: 90vh;
    border-radius: var(--radius-xl);
  }

  .modal-header {
    border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>