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
            <span>深度学习</span>
          </span>
          <h1 class="banner-title">📚 战术学习</h1>
          <p class="banner-desc">掌握专业篮球战术体系，AI智能讲解助你透彻理解每一个战术细节</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="content-wrapper">
        <div class="main-content">
          <div class="filters-section">
            <div class="filter-pills">
              <button
                v-for="type in types"
                :key="type.value"
                :class="['filter-pill', { active: selectedType === type.value }]"
                @click="selectedType = type.value"
              >
                {{ type.label }}
              </button>
            </div>
          </div>

          <div v-if="filteredTactics.length > 0" class="tactics-list">
            <div
              v-for="tactic in filteredTactics"
              :key="tactic.id"
              class="tactic-card"
              @click="viewTactic(tactic)"
            >
              <div class="tactic-card-header">
                <h3 class="tactic-title">{{ tactic.title }}</h3>
                <span :class="['type-badge', tactic.type]">{{ getTypeLabel(tactic.type) }}</span>
              </div>
              <p class="tactic-preview">{{ truncate(tactic.content, 150) }}</p>
              <div class="tactic-meta">
                <span class="meta-item">
                  <span>👁️</span>
                  <span>{{ tactic.viewCount }} 次浏览</span>
                </span>
                <span class="meta-item">
                  <span>📅</span>
                  <span>{{ tactic.createdAt }}</span>
                </span>
              </div>
              <div class="card-hint">点击查看详情 →</div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon">📭</div>
            <h3>暂无内容</h3>
            <p>请尝试调整筛选条件</p>
          </div>
        </div>

        <aside class="sidebar">
          <div class="ai-explain-card">
            <div class="ai-card-header">
              <span class="ai-icon">🤖</span>
              <div>
                <h3>AI战术讲解</h3>
                <p>输入战术名称，AI为你深度解析</p>
              </div>
            </div>
            <div class="ai-card-body">
              <div class="explain-input-group">
                <el-input
                  v-model="tacticName"
                  placeholder="输入战术名称，如：挡拆、联防..."
                  size="large"
                  @keyup.enter="handleExplainTactic"
                />
                <el-button
                  type="primary"
                  @click="handleExplainTactic"
                  :loading="explaining"
                  class="explain-btn"
                >
                  {{ explaining ? '解析中...' : '解析' }}
                </el-button>
              </div>

              <div v-if="explanation" class="explanation-result slide-up">
                <div class="explanation-header">
                  <span>📖 讲解结果</span>
                </div>
                <div class="explanation-content">
                  <pre>{{ explanation }}</pre>
                </div>
              </div>

              <div v-if="!explanation && !explaining" class="explain-hints">
                <span class="hint-label">💡 试试这些战术：</span>
                <div class="hint-tags">
                  <button
                    v-for="hint in tacticHints"
                    :key="hint"
                    class="hint-tag"
                    @click="tacticName = hint; handleExplainTactic()"
                  >
                    {{ hint }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="modal">
        <div v-if="selectedTactic" class="modal-overlay" @click.self="selectedTactic = null">
          <div class="modal-container">
            <div class="modal-header">
              <div class="modal-header-left">
                <span class="modal-type-badge" :class="selectedTactic.type">
                  {{ getTypeLabel(selectedTactic.type) }}
                </span>
                <h2>{{ selectedTactic.title }}</h2>
              </div>
              <button @click="selectedTactic = null" class="modal-close">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="tactic-meta-row">
                <span class="meta-badge">👁️ {{ selectedTactic.viewCount }} 次浏览</span>
                <span class="meta-badge">📅 {{ selectedTactic.createdAt }}</span>
              </div>
              <div class="tactic-full-content">
                <pre>{{ selectedTactic.content }}</pre>
              </div>
            </div>
            <div class="modal-footer">
              <button @click="selectedTactic = null" class="btn-close-modal">关闭</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTactics, explainTactic as apiExplainTactic } from '../api/tactic'
import { getKnowledge } from '../api/knowledge'
import { getNews } from '../api/news'
import AppLayout from '../components/AppLayout.vue'

const types = [
  { value: 'all', label: '全部' },
  { value: '战术', label: '战术体系' },
  { value: '进攻', label: '进攻战术' },
  { value: '防守', label: '防守战术' },
  { value: '知识', label: '篮球知识' },
  { value: '资讯', label: '赛事资讯' }
]

const tacticHints = ['挡拆', '联防', '快攻', '三角进攻', '区域防守']

const selectedType = ref('all')
const tactics = ref([])
const knowledgeList = ref([])
const newsList = ref([])
const selectedTactic = ref(null)
const tacticName = ref('')
const explanation = ref('')
const explaining = ref(false)

const filteredTactics = computed(() => {
  if (selectedType.value === 'all') {
    const allItems = [
      ...tactics.value,
      ...knowledgeList.value.map(k => ({
        ...k,
        type: '知识',
        title: k.title,
        content: k.content,
        viewCount: k.viewCount || 0,
        createdAt: k.createdAt?.substring(0, 10) || ''
      })),
      ...newsList.value.map(n => ({
        ...n,
        type: '资讯',
        title: n.title,
        content: n.summary || n.content,
        viewCount: n.viewCount || 0,
        createdAt: n.publishTime?.substring(0, 10) || ''
      }))
    ]
    return allItems.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  }
  if (selectedType.value === '知识') {
    return knowledgeList.value.map(k => ({
      ...k,
      type: '知识',
      title: k.title,
      content: k.content,
      viewCount: k.viewCount || 0,
      createdAt: k.createdAt?.substring(0, 10) || ''
    }))
  }
  if (selectedType.value === '资讯') {
    return newsList.value.map(n => ({
      ...n,
      type: '资讯',
      title: n.title,
      content: n.summary || n.content,
      viewCount: n.viewCount || 0,
      createdAt: n.publishTime?.substring(0, 10) || ''
    }))
  }
  return tactics.value.filter(t => t.type === selectedType.value)
})

const loadTactics = async () => {
  try {
    const response = await getTactics()
    if (response.code === 200) {
      tactics.value = response.data
    }
  } catch (error) {
    console.error('加载战术失败', error)
  }
}

const loadKnowledge = async () => {
  try {
    const response = await getKnowledge()
    knowledgeList.value = response.data.data || response.data || []
  } catch (error) {
    console.error('加载知识失败', error)
  }
}

const loadNews = async () => {
  try {
    const response = await getNews({ limit: 20 })
    newsList.value = response.data.data || response.data || []
  } catch (error) {
    console.error('加载资讯失败', error)
  }
}

const viewTactic = (tactic) => {
  selectedTactic.value = tactic
}

const getTypeLabel = (type) => {
  const found = types.find(t => t.value === type)
  return found ? found.label : type
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const handleExplainTactic = async () => {
  if (!tacticName.value.trim()) return

  explaining.value = true
  explanation.value = ''
  try {
    const response = await apiExplainTactic(tacticName.value)
    if (response.code === 200) {
      explanation.value = response.data
    } else {
      explanation.value = response.message
    }
  } catch (error) {
    explanation.value = '讲解失败：' + error.message
  } finally {
    explaining.value = false
  }
}

onMounted(() => {
  loadTactics()
  loadKnowledge()
  loadNews()
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
  background: var(--color-primary-400);
  top: -50%;
  right: -10%;
  animation: orbFloat 8s ease-in-out infinite;
}

.banner-orb-2 {
  width: 200px;
  height: 200px;
  background: var(--color-accent-400);
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
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.25);
  border-radius: var(--radius-full);
  color: var(--color-primary-400);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  margin-bottom: 1rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-primary-400);
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

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 2rem;
  padding: 2rem 0 3rem;
  align-items: start;
}

.filters-section {
  margin-bottom: 1.5rem;
}

.filter-pills {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-pill {
  padding: 0.45rem 1.25rem;
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
  background: var(--color-primary-500);
  color: #fff;
  border-color: var(--color-primary-500);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.tactics-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.tactic-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.5rem;
  border: 1px solid var(--color-neutral-200);
  cursor: pointer;
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.tactic-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, var(--color-primary-400), var(--color-accent-400));
  border-radius: 0 2px 2px 0;
  opacity: 0;
  transition: opacity var(--transition-base);
}

.tactic-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-primary-200);
}

.tactic-card:hover::before {
  opacity: 1;
}

.tactic-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.tactic-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-900);
  margin: 0;
  flex: 1;
}

.type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
}

.type-badge.战术 { background: var(--color-info-light); color: var(--color-info); }
.type-badge.防守 { background: var(--color-danger-light); color: var(--color-danger); }
.type-badge.知识 { background: var(--color-success-light); color: var(--color-success); }
.type-badge.资讯 { background: var(--color-warning-light); color: var(--color-warning); }

.tactic-preview {
  color: var(--color-neutral-500);
  font-size: var(--font-size-sm);
  line-height: var(--line-height-relaxed);
  margin: 0 0 0.75rem;
}

.tactic-meta {
  display: flex;
  gap: 1.25rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
}

.card-hint {
  margin-top: 0.75rem;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
  opacity: 0;
  transform: translateY(4px);
  transition: all var(--transition-base);
}

.tactic-card:hover .card-hint {
  opacity: 1;
  transform: translateY(0);
}

.empty-state {
  text-align: center;
  padding: 5rem 2rem;
  background: #fff;
  border-radius: var(--radius-xl);
  border: 1px dashed var(--color-neutral-300);
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

.ai-explain-card {
  background: #fff;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-neutral-200);
  overflow: hidden;
  position: sticky;
  top: 88px;
}

.ai-card-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, var(--color-primary-50) 0%, var(--color-accent-50) 100%);
  border-bottom: 1px solid var(--color-neutral-200);
}

.ai-icon {
  font-size: 2rem;
}

.ai-card-header h3 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-800);
}

.ai-card-header p {
  margin: 0.15rem 0 0;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-500);
}

.ai-card-body {
  padding: 1.25rem 1.5rem;
}

.explain-input-group {
  display: flex;
  gap: 0.5rem;
}

.explain-input-group :deep(.el-input) {
  flex: 1;
}

.explain-btn {
  flex-shrink: 0;
  min-width: 80px;
}

.explanation-result {
  margin-top: 1rem;
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.explanation-header {
  padding: 0.6rem 1rem;
  background: var(--color-neutral-50);
  border-bottom: 1px solid var(--color-neutral-200);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-neutral-700);
}

.explanation-content {
  padding: 1rem;
  max-height: 400px;
  overflow-y: auto;
}

.explanation-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: var(--font-family-base);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-700);
  line-height: var(--line-height-relaxed);
}

.explain-hints {
  margin-top: 1rem;
}

.hint-label {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
  margin-bottom: 0.5rem;
}

.hint-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.hint-tag {
  padding: 0.3rem 0.75rem;
  background: var(--color-neutral-100);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  color: var(--color-neutral-500);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.hint-tag:hover {
  background: var(--color-primary-50);
  border-color: var(--color-primary-300);
  color: var(--color-primary-600);
}

.slide-up {
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
  background: linear-gradient(135deg, var(--color-primary-50) 0%, var(--color-accent-50) 100%);
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
}

.modal-header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
  min-width: 0;
}

.modal-header-left h2 {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-neutral-900);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.modal-type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
  flex-shrink: 0;
}

.modal-type-badge.战术 { background: var(--color-info-light); color: var(--color-info); }
.modal-type-badge.防守 { background: var(--color-danger-light); color: var(--color-danger); }
.modal-type-badge.知识 { background: var(--color-success-light); color: var(--color-success); }
.modal-type-badge.资讯 { background: var(--color-warning-light); color: var(--color-warning); }

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
  flex-shrink: 0;
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

.tactic-meta-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.meta-badge {
  padding: 0.35rem 0.75rem;
  background: var(--color-neutral-100);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  color: var(--color-neutral-500);
}

.tactic-full-content {
  background: var(--color-neutral-50);
  border-radius: var(--radius-lg);
  padding: 1.25rem;
  border: 1px solid var(--color-neutral-200);
}

.tactic-full-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: var(--font-family-base);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-700);
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
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .ai-explain-card {
    position: static;
  }

  .banner-title {
    font-size: var(--font-size-3xl);
  }
}

@media (max-width: 600px) {
  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .banner-desc {
    font-size: var(--font-size-base);
  }

  .tactic-card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .explain-input-group {
    flex-direction: column;
  }

  .modal-container {
    max-height: 90vh;
    border-radius: var(--radius-xl);
  }

  .modal-header {
    border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  }
}
</style>