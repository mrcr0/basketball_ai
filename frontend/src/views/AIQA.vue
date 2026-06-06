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
            <span>AI智能</span>
          </span>
          <h1 class="banner-title">🤖 AI智能问答</h1>
          <p class="banner-desc">基于大语言模型的智能助手，覆盖篮球、历史、科学等多领域知识，随时为你答疑解惑</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="qa-layout">
        <div class="chat-container">
          <div class="chat-header">
            <div class="header-info">
              <h2>🎯 AI智能助手</h2>
              <p>{{ currentDomainLabel }}</p>
            </div>
            <div class="header-actions">
              <button @click="startNewChat" class="action-btn">
                <span>+</span>
                <span>新对话</span>
              </button>
              <button @click="toggleHistory" class="action-btn">
                <span>📋</span>
                <span>历史</span>
              </button>
            </div>
          </div>

          <Transition name="slide">
            <div v-if="showHistory" class="history-panel">
              <div class="history-header">
                <h3>对话历史</h3>
                <button @click="showHistory = false" class="close-btn">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                  </svg>
                </button>
              </div>
              <div class="history-list">
                <div
                  v-for="conv in conversations"
                  :key="conv.sessionId"
                  :class="['history-item', { active: conv.sessionId === currentSession }]"
                  @click="loadConversation(conv.sessionId)"
                >
                  <div class="history-topic">{{ conv.topic || '新对话' }}</div>
                  <div class="history-meta">
                    <span class="domain-tag">{{ conv.domain }}</span>
                    <span class="msg-count">{{ conv.messageCount }}条消息</span>
                  </div>
                  <button
                    @click.stop="deleteConv(conv.sessionId)"
                    class="delete-btn"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                  </button>
                </div>
                <div v-if="conversations.length === 0" class="empty-history">
                  暂无历史记录
                </div>
              </div>
            </div>
          </Transition>

          <div class="chat-messages" ref="messagesContainer">
            <div v-if="messages.length === 0" class="welcome-message">
              <div class="welcome-icon">🤖</div>
              <h3>你好！我是AI智能助手</h3>
              <p>我可以回答各种领域的问题，还能告诉你当前日期和时间哦！</p>
              <div class="domain-chips">
                <span
                  v-for="d in domains"
                  :key="d.value"
                  :class="['domain-chip', { active: currentDomain === d.value }]"
                  @click="filterDomain(d.value)"
                >
                  {{ d.label }}
                </span>
              </div>
            </div>

            <div
              v-for="(msg, index) in messages"
              :key="index"
              :class="['message', msg.role]"
            >
              <div class="avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
              <div class="msg-content">
                <div v-if="msg.domain && msg.role === 'assistant'" class="msg-domain">
                  {{ msg.domainLabel }}
                </div>
                <pre>{{ msg.content }}</pre>
              </div>
            </div>

            <div v-if="loading" class="message assistant loading">
              <div class="avatar">🤖</div>
              <div class="msg-content">
                <div class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
                <p class="thinking-text">AI正在分析问题...</p>
              </div>
            </div>
          </div>

          <div class="chat-input">
            <textarea
              v-model="question"
              :placeholder="inputPlaceholder"
              @keyup.enter.exact.prevent="sendMessage"
              class="input-area"
              :disabled="loading"
              rows="2"
            ></textarea>
            <button
              @click="sendMessage"
              :disabled="!question.trim() || loading"
              class="send-btn"
            >
              <svg v-if="!loading" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="22" y1="2" x2="11" y2="13"></line>
                <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
              </svg>
              <span v-else>...</span>
            </button>
          </div>
        </div>

        <aside class="sidebar">
          <div class="quick-questions-card">
            <h3>💬 快速提问</h3>
            <div class="quick-buttons">
              <button
                v-for="q in currentQuickQuestions"
                :key="q.text"
                @click="askQuick(q.text)"
                class="quick-btn"
                :class="q.category"
              >
                {{ q.text }}
              </button>
            </div>
          </div>

          <div class="domain-card">
            <h3>📂 知识领域</h3>
            <div class="domain-list">
              <button
                v-for="d in domains"
                :key="d.value"
                :class="['domain-btn', { active: currentDomain === d.value }]"
                @click="filterDomain(d.value)"
              >
                <span class="domain-icon">{{ d.label.slice(0, 2) }}</span>
                <span class="domain-name">{{ d.label.slice(2) }}</span>
              </button>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { chat, getConversations, getHistory, deleteConversation, askAgent } from '../api/ai'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/AppLayout.vue'
import { localResponse, generateSmartPrompt } from '../utils/basketballKnowledge.js'

const messages = ref([])
const question = ref('')
const loading = ref(false)
const currentSession = ref(null)
const showHistory = ref(false)
const conversations = ref([])
const currentDomain = ref(null)
const messagesContainer = ref(null)
const responseSpeed = ref(0)

const domains = [
  { value: 'basketball', label: '🏀 篮球' },
  { value: 'history', label: '📜 历史' },
  { value: 'science', label: '🔬 科学' },
  { value: 'technology', label: '💻 技术' },
  { value: 'health', label: '🏥 健康' },
  { value: 'general', label: '🌐 综合' }
]

const quickQuestionsByDomain = {
  basketball: [
    { text: '如何提高投篮命中率？', category: 'basketball' },
    { text: '后卫应该如何训练？', category: 'basketball' },
    { text: '什么是挡拆战术？', category: 'basketball' },
    { text: '如何预防篮球运动损伤？', category: 'basketball' },
    { text: '怎么练习运球？', category: 'basketball' },
    { text: '篮球五大位置是什么？', category: 'basketball' },
    { text: '如何增强弹跳力？', category: 'basketball' }
  ],
  history: [
    { text: '唐朝为什么能够繁荣昌盛？', category: 'history' },
    { text: '秦始皇统一六国的过程', category: 'history' },
    { text: '三国时期有哪些著名战役？', category: 'history' }
  ],
  science: [
    { text: '为什么天空是蓝色的？', category: 'science' },
    { text: '什么是相对论？', category: 'science' },
    { text: 'DNA双螺旋结构是怎么发现的？', category: 'science' }
  ],
  technology: [
    { text: 'Python和JavaScript有什么区别？', category: 'technology' },
    { text: '如何学习机器学习？', category: 'technology' },
    { text: '什么是区块链？', category: 'technology' }
  ],
  health: [
    { text: '如何提高免疫力？', category: 'health' },
    { text: '运动后如何快速恢复？', category: 'health' },
    { text: '健康的饮食习惯有哪些？', category: 'health' }
  ],
  general: [
    { text: '今天几号？', category: 'general' },
    { text: '现在几点？', category: 'general' },
    { text: '今天星期几？', category: 'general' },
    { text: '如何高效学习新技能？', category: 'general' },
    { text: '时间管理有哪些好方法？', category: 'general' },
    { text: '如何缓解工作压力？', category: 'general' }
  ]
}

const currentDomainLabel = computed(() => {
  if (!currentDomain.value) return '🌐 综合问答'
  const d = domains.find(dom => dom.value === currentDomain.value)
  return d ? d.label : '🌐 综合问答'
})

const currentQuickQuestions = computed(() => {
  if (!currentDomain.value) {
    return [
      ...quickQuestionsByDomain.basketball,
      ...quickQuestionsByDomain.general.slice(0, 2)
    ]
  }
  return quickQuestionsByDomain[currentDomain.value] || quickQuestionsByDomain.general
})

const inputPlaceholder = computed(() => {
  const placeholders = {
    basketball: '输入关于篮球的问题，比如：如何提高投篮命中率？',
    history: '输入历史相关问题，比如：唐朝为什么繁荣？',
    science: '输入科学问题，比如：什么是相对论？',
    technology: '输入技术问题，比如：如何学习编程？',
    health: '输入健康问题，比如：如何提高免疫力？',
    general: '输入任何问题，我来帮你解答！'
  }
  return placeholders[currentDomain.value] || '输入你的问题，我来帮你解答！'
})

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!question.value.trim() || loading.value) return

  const userQuestion = question.value.trim()
  messages.value.push({
    role: 'user',
    content: userQuestion,
    domain: currentDomain.value,
    domainLabel: currentDomainLabel.value
  })

  question.value = ''
  loading.value = true
  scrollToBottom()

  const startTime = Date.now()

  const localAnswer = localResponse(userQuestion)
  if (localAnswer) {
    const elapsed = Date.now() - startTime
    responseSpeed.value = elapsed
    messages.value.push({
      role: 'assistant',
      content: localAnswer + `\n\n⚡ 响应时间：${elapsed}ms（本地知识图谱）`,
      domain: currentDomain.value || 'general',
      domainLabel: currentDomainLabel.value
    })
    loading.value = false
    scrollToBottom()
    return
  }

  try {
    let response
    let isAgentUsed = false

    try {
      response = await askAgent(userQuestion)
      isAgentUsed = true
    } catch (agentError) {
      console.error('独立智能体调用失败，尝试备用接口:', agentError.message)
      response = await chat(userQuestion, currentSession.value)
    }

    const elapsed = Date.now() - startTime
    responseSpeed.value = elapsed

    if (response.code === 200) {
      const data = response.data
      
      if (isAgentUsed) {
        messages.value.push({
          role: 'assistant',
          content: data + `\n\n⚡ 响应时间：${elapsed}ms`,
          domain: currentDomain.value || 'general',
          domainLabel: currentDomainLabel.value
        })
      } else {
        currentSession.value = data.sessionId
        messages.value.push({
          role: 'assistant',
          content: (data.answer || '') + `\n\n⚡ 响应时间：${elapsed}ms`,
          domain: data.domain,
          domainLabel: data.domainLabel || currentDomainLabel.value
        })
        loadConversations()
      }
    } else {
      messages.value.push({
        role: 'assistant',
        content: '抱歉，我无法回答这个问题。请稍后重试。'
      })
    }
  } catch (error) {
    console.error('Chat error:', error)
    messages.value.push({
      role: 'assistant',
      content: '网络错误，请检查网络连接后重试。'
    })
    ElMessage.error('发送失败，请重试')
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const askQuick = (text) => {
  question.value = text
  sendMessage()
}

const filterDomain = (domain) => {
  currentDomain.value = currentDomain.value === domain ? null : domain
}

const startNewChat = () => {
  messages.value = []
  currentSession.value = null
  currentDomain.value = null
  showHistory.value = false
}

const toggleHistory = () => {
  showHistory.value = !showHistory.value
  if (showHistory.value) {
    loadConversations()
  }
}

const loadConversations = async () => {
  try {
    const response = await getConversations()
    if (response.code === 200) {
      conversations.value = response.data || []
    }
  } catch (error) {
    console.error('Load conversations error:', error)
  }
}

const loadConversation = async (sessionId) => {
  try {
    const response = await getHistory(sessionId)
    if (response.code === 200) {
      const data = response.data
      messages.value = (data.messages || []).map(msg => ({
        role: msg.role,
        content: msg.content
      }))
      currentSession.value = sessionId
      currentDomain.value = data.domain
      showHistory.value = false
      scrollToBottom()
    }
  } catch (error) {
    console.error('Load history error:', error)
    ElMessage.error('加载历史记录失败')
  }
}

const deleteConv = async (sessionId) => {
  try {
    await deleteConversation(sessionId)
    ElMessage.success('删除成功')
    loadConversations()
    if (currentSession.value === sessionId) {
      startNewChat()
    }
  } catch (error) {
    console.error('Delete conversation error:', error)
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadConversations()
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
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.25);
  border-radius: var(--radius-full);
  color: var(--color-primary-300);
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

.qa-layout {
  padding: 2rem 0 3rem;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 1.5rem;
  align-items: start;
}

.chat-container {
  background: #fff;
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chat-header {
  background: linear-gradient(135deg, var(--color-accent-500) 0%, var(--color-accent-600) 100%);
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info h2 {
  margin: 0;
  color: var(--color-dark-900);
  font-size: var(--font-size-lg);
}

.header-info p {
  margin: 0.15rem 0 0;
  color: rgba(26, 26, 46, 0.65);
  font-size: var(--font-size-sm);
}

.header-actions {
  display: flex;
  gap: 0.4rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.35rem 0.75rem;
  background: rgba(255, 255, 255, 0.25);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--font-size-xs);
  color: var(--color-dark-900);
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
  font-family: inherit;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.45);
}

.history-panel {
  background: var(--color-neutral-50);
  border-bottom: 1px solid var(--color-neutral-200);
  max-height: 280px;
  overflow-y: auto;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--color-neutral-200);
  position: sticky;
  top: 0;
  background: var(--color-neutral-50);
  z-index: 1;
}

.history-header h3 {
  margin: 0;
  font-size: var(--font-size-base);
  color: var(--color-neutral-800);
}

.close-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: var(--color-neutral-200);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-neutral-500);
  transition: all var(--transition-fast);
}

.close-btn:hover {
  background: var(--color-danger-light);
  color: var(--color-danger);
}

.history-list {
  padding: 0.5rem;
}

.history-item {
  padding: 0.75rem;
  border-radius: var(--radius-lg);
  cursor: pointer;
  margin-bottom: 0.5rem;
  background: #fff;
  border: 1px solid var(--color-neutral-200);
  transition: all var(--transition-fast);
  position: relative;
}

.history-item:hover {
  border-color: var(--color-accent-400);
  box-shadow: var(--shadow-glow-accent);
}

.history-item.active {
  border-color: var(--color-accent-400);
  background: var(--color-accent-50);
}

.history-topic {
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-800);
  margin-bottom: 0.25rem;
  padding-right: 44px;
}

.history-meta {
  display: flex;
  gap: 0.4rem;
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
}

.domain-tag {
  background: var(--color-neutral-200);
  padding: 0.1rem 0.4rem;
  border-radius: var(--radius-sm);
  font-size: 0.7rem;
}

.delete-btn {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  background: var(--color-danger-light);
  color: var(--color-danger);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.history-item:hover .delete-btn {
  opacity: 1;
}

.empty-history {
  text-align: center;
  padding: 2rem;
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
}

.chat-messages {
  padding: 1.25rem;
  flex: 1;
  max-height: 500px;
  overflow-y: auto;
  background: var(--color-neutral-50);
}

.welcome-message {
  text-align: center;
  padding: 2rem 1rem;
}

.welcome-icon {
  font-size: 4rem;
  margin-bottom: 0.75rem;
}

.welcome-message h3 {
  margin: 0 0 0.5rem;
  font-size: var(--font-size-lg);
  color: var(--color-neutral-800);
}

.welcome-message p {
  margin: 0 0 1.25rem;
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
}

.domain-chips {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.5rem;
}

.domain-chip {
  padding: 0.4rem 0.8rem;
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: var(--font-size-xs);
  background: var(--color-neutral-100);
  color: var(--color-neutral-500);
  border: 1px solid transparent;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.domain-chip:hover {
  background: var(--color-neutral-200);
  color: var(--color-neutral-700);
  transform: translateY(-1px);
}

.domain-chip.active {
  background: var(--color-accent-50);
  color: var(--color-accent-600);
  border-color: var(--color-accent-300);
}

.message {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
  animation: fadeInSlide 0.35s ease-out;
}

@keyframes fadeInSlide {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, var(--color-accent-400) 0%, var(--color-accent-600) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(74, 222, 128, 0.2);
}

.message.user .avatar {
  background: linear-gradient(135deg, var(--color-primary-400) 0%, var(--color-primary-600) 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
}

.msg-content {
  max-width: 75%;
  padding: 0.75rem 1rem;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  line-height: var(--line-height-relaxed);
}

.msg-domain {
  font-size: 0.7rem;
  color: var(--color-neutral-400);
  margin-bottom: 0.25rem;
  font-weight: var(--font-weight-medium);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.message.assistant .msg-content {
  background: #fff;
  color: var(--color-neutral-700);
  border-radius: var(--radius-lg) var(--radius-lg) var(--radius-lg) 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.message.user .msg-content {
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-primary-600) 100%);
  color: #fff;
  border-radius: var(--radius-lg) var(--radius-lg) 4px var(--radius-lg);
}

.msg-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
}

.message.loading .msg-content {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  margin-bottom: 0.5rem;
}

.typing-indicator span {
  width: 7px;
  height: 7px;
  background: var(--color-neutral-400);
  border-radius: 50%;
  animation: typing 1.4s ease-in-out infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

.thinking-text {
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0;
  font-style: italic;
}

@keyframes pulseText {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.thinking-text {
  animation: pulseText 1.5s ease-in-out infinite;
}

.chat-input {
  display: flex;
  gap: 0.5rem;
  padding: 1rem 1.25rem;
  border-top: 1px solid var(--color-neutral-200);
  background: #fff;
}

.input-area {
  flex: 1;
  padding: 0.7rem 1rem;
  background: var(--color-neutral-50);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-700);
  resize: none;
  font-family: inherit;
  line-height: var(--line-height-relaxed);
  transition: all var(--transition-fast);
}

.input-area:focus {
  outline: none;
  border-color: var(--color-primary-400);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-area::placeholder {
  color: var(--color-neutral-400);
}

.input-area:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, var(--color-accent-500) 0%, var(--color-accent-600) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.send-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.quick-questions-card,
.domain-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.25rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-neutral-200);
}

.quick-questions-card h3,
.domain-card h3 {
  margin: 0 0 0.75rem;
  font-size: var(--font-size-base);
  color: var(--color-neutral-800);
  font-weight: var(--font-weight-semibold);
}

.quick-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.quick-btn {
  padding: 0.4rem 0.7rem;
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: var(--font-size-xs);
  border: 1px solid var(--color-neutral-200);
  background: var(--color-neutral-50);
  color: var(--color-neutral-600);
  transition: all var(--transition-fast);
  font-family: inherit;
  white-space: nowrap;
}

.quick-btn:hover {
  background: var(--color-neutral-100);
  border-color: var(--color-neutral-300);
  transform: translateY(-1px);
}

.quick-btn.basketball:hover { background: var(--color-warning-light); border-color: var(--color-warning-300); color: var(--color-warning); }
.quick-btn.history:hover { background: #fef3c7; border-color: #cfa00e; color: #92400e; }
.quick-btn.science:hover { background: var(--color-info-light); border-color: var(--color-info); color: var(--color-info); }
.quick-btn.technology:hover { background: var(--color-accent-50); border-color: var(--color-accent-300); color: var(--color-accent-600); }
.quick-btn.health:hover { background: var(--color-danger-light); border-color: var(--color-danger-300); color: var(--color-danger); }
.quick-btn.general:hover { background: var(--color-neutral-100); border-color: var(--color-neutral-300); color: var(--color-neutral-700); }

.domain-list {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.domain-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.55rem 0.75rem;
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  background: #fff;
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  color: var(--color-neutral-600);
  font-size: var(--font-size-sm);
}

.domain-btn:hover {
  background: var(--color-neutral-50);
  border-color: var(--color-neutral-300);
}

.domain-btn.active {
  background: var(--color-primary-50);
  border-color: var(--color-primary-300);
  color: var(--color-primary-600);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.domain-icon {
  font-size: var(--font-size-base);
}

.domain-name {
  font-weight: var(--font-weight-medium);
}

.slide-enter-active,
.slide-leave-active {
  transition: all var(--transition-base);
}

.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
}

.slide-enter-to,
.slide-leave-from {
  max-height: 280px;
  opacity: 1;
}

@media (max-width: 768px) {
  .page-banner {
    padding: 2rem 0 1.5rem;
  }

  .banner-title {
    font-size: var(--font-size-2xl);
  }

  .banner-desc {
    font-size: var(--font-size-sm);
  }

  .qa-layout {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .sidebar {
    order: -1;
    flex-direction: row;
    gap: 0.75rem;
    overflow-x: auto;
    padding-bottom: 0.25rem;
  }

  .quick-questions-card,
  .domain-card {
    flex-shrink: 0;
    min-width: 200px;
  }

  .chat-messages {
    max-height: 400px;
  }

  .msg-content {
    max-width: 85%;
  }
}

@media (max-width: 480px) {
  .chat-header {
    padding: 0.75rem 1rem;
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .chat-input {
    padding: 0.75rem;
  }

  .msg-content {
    max-width: 90%;
  }
}
</style>