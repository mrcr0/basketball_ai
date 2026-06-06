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
            <span>互动社区</span>
          </span>
          <h1 class="banner-title">💬 篮球社区</h1>
          <p class="banner-desc">参与赛事讨论，与热爱篮球的朋友一起交流</p>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="community-layout">
        <div class="dynamics-feed">
          <DynamicPublisher
            ref="publisherRef"
            @published="handleDynamicPublished"
          />
          
          <div v-if="userDynamics.length > 0" class="dynamics-list">
            <div
              v-for="dynamic in userDynamics"
              :key="dynamic.id"
              :id="`dynamic-${dynamic.id}`"
              class="dynamic-card"
            >
              <div class="dynamic-header">
                <div class="user-avatar">
                  <span>{{ dynamic.user?.avatarUrl || '👤' }}</span>
                </div>
                <div class="user-meta">
                  <span class="user-name">{{ dynamic.user?.nickname || '用户' }}</span>
                  <span class="post-time">{{ formatTime(dynamic.createdAt) }}</span>
                </div>
                <span class="post-type-badge" :class="dynamic.type">
                  {{ getTypeLabel(dynamic.type) }}
                </span>
              </div>
              
              <div class="dynamic-body">
                <p class="dynamic-text" v-html="dynamic.content"></p>
                
                <div v-if="dynamic.imageUrls?.length > 0" class="dynamic-images">
                  <img
                    v-for="(img, idx) in dynamic.imageUrls"
                    :key="idx"
                    :src="img"
                    class="dynamic-image"
                    alt="动态图片"
                  />
                </div>
                
                <div v-if="dynamic.videoUrls?.length > 0" class="dynamic-videos">
                  <video
                    v-for="(vid, idx) in dynamic.videoUrls"
                    :key="idx"
                    :src="vid"
                    class="dynamic-video"
                    controls
                  ></video>
                </div>
              </div>
              
              <div class="dynamic-actions">
                <button
                  :class="['action-btn', { liked: dynamic.liked }]"
                  @click="dynamic.liked = !dynamic.liked"
                >
                  <span>{{ dynamic.liked ? '❤️' : '🤍' }}</span>
                  <span>{{ dynamic.likes || 0 }}</span>
                </button>
                <button
                  :class="['action-btn', { active: dynamic.showComments }]"
                  @click="dynamic.showComments = !dynamic.showComments"
                >
                  <span>💬</span>
                  <span>{{ dynamic.comments?.length || 0 }}</span>
                </button>
                <button class="action-btn">
                  <span>🔗</span>
                  <span>分享</span>
                </button>
              </div>
              
              <div v-if="dynamic.showComments" class="comments-section">
                <div v-if="dynamic.comments?.length > 0" class="comments-list">
                  <div v-for="(comment, idx) in dynamic.comments" :key="idx" class="comment-item">
                    <div class="comment-avatar">👤</div>
                    <div class="comment-content">
                      <div class="comment-meta">
                        <span class="comment-user">用户</span>
                        <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                      </div>
                      <p class="comment-text">{{ comment.content }}</p>
                    </div>
                  </div>
                </div>
                <div v-else class="no-comments">
                  <p>暂无评论，快来抢沙发吧！</p>
                </div>
                
                <div class="comment-input-row">
                  <textarea
                    v-model="dynamic.newComment"
                    class="comment-textarea"
                    placeholder="写下你的评论..."
                    rows="2"
                  ></textarea>
                  <button
                    class="send-btn"
                    :disabled="!dynamic.newComment?.trim()"
                    @click="addComment(dynamic)"
                  >
                    发送
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <aside class="community-sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">🏀 篮球社区</h3>
            <p class="sidebar-desc">赛事讨论 · 战术分析 · 球迷互动</p>
            <div class="sidebar-stats">
              <div class="sidebar-stat">
                <span class="s-val">{{ userDynamics.length }}</span>
                <span class="s-lbl">用户动态</span>
              </div>
              <div class="sidebar-stat">
                <span class="s-val">{{ matchDiscussions.length }}</span>
                <span class="s-lbl">赛事讨论</span>
              </div>
            </div>
          </div>
          
          <DiscussionSection
            class="discussion-in-sidebar"
            :discussions="matchDiscussions"
            :active-league="discussionLeague"
            @update:active-league="discussionLeague = $event"
            @new-discussion="triggerNewDiscussion"
            @view-discussion="viewDiscussion"
          />
        </aside>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDynamics } from '../api/dynamic'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/AppLayout.vue'
import DiscussionSection from '../components/DiscussionSection.vue'
import DynamicPublisher from '../components/DynamicPublisher.vue'

const showShareModal = ref(false)
const shareUrl = ref('')
const dynamics = ref([])
const publisherRef = ref(null)

const discussionLeague = ref('all')

const matchDiscussions = computed(() => {
  return dynamics.value
    .filter(d => d.type === 'match')
    .slice(0, 10)
})

const userDynamics = computed(() => {
  return dynamics.value
    .filter(d => d.type !== 'match')
})

const handleDynamicPublished = (newDynamic) => {
  dynamics.value.unshift({
    ...newDynamic,
    id: Date.now(),
    createdAt: new Date().toISOString(),
    showComments: false,
    newComment: '',
    liked: false,
    comments: []
  })
}

const triggerNewDiscussion = (league) => {
  ElMessage.info('赛事讨论功能已保留，请在赛事讨论区参与讨论')
}

const viewDiscussion = (dynamic) => {
  dynamic.showComments = !dynamic.showComments
  const el = document.querySelector(`#discussion-${dynamic.id}`)
  if (el) el.scrollIntoView({ behavior: 'smooth' })
}

const getTypeLabel = (type) => {
  const labels = {
    text: '文字动态',
    image: '图片动态',
    video: '视频动态',
    match: '赛事讨论'
  }
  return labels[type] || '动态'
}

const addComment = (dynamic) => {
  if (!dynamic.newComment?.trim()) return
  
  if (!dynamic.comments) {
    dynamic.comments = []
  }
  
  dynamic.comments.push({
    id: Date.now(),
    content: dynamic.newComment,
    createdAt: new Date().toISOString()
  })
  
  dynamic.newComment = ''
}

const loadDynamics = async () => {
  try {
    const response = await getDynamics()
    if (response.code === 200) {
      dynamics.value = response.data.map(d => ({
        ...d,
        showComments: false,
        newComment: '',
        liked: false
      }))
    }
  } catch (error) {
    console.error('加载赛事讨论失败', error)
    ElMessage.error('加载赛事讨论失败，请稍后重试')
  }
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const mins = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (mins < 1) return '刚刚'
  if (mins < 60) return `${mins}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadDynamics()
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
  background: rgba(249, 115, 22, 0.12);
  border: 1px solid rgba(249, 115, 22, 0.25);
  border-radius: var(--radius-full);
  color: var(--color-warm-400);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  margin-bottom: 1rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-warm-400);
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
  margin: 0 auto 1.5rem;
  line-height: var(--line-height-relaxed);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.community-layout {
  padding: 2rem 0 3rem;
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 1.5rem;
  align-items: start;
}

.community-sidebar {
  position: sticky;
  top: 88px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.discussion-in-sidebar {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.discussion-in-sidebar .section-card {
  background: transparent;
  border: none;
  border-radius: 0;
  box-shadow: none;
  padding: 0;
}

.discussion-in-sidebar .section-header {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #e2e8f0;
}

.discussion-in-sidebar .section-title {
  font-size: 0.85rem;
}

.discussion-in-sidebar .section-desc {
  font-size: 0.7rem;
}

.discussion-in-sidebar .new-discussion-btn {
  padding: 0.4rem 0.8rem;
  font-size: 0.75rem;
}

.discussion-in-sidebar .league-tabs {
  padding: 0.75rem 1rem;
  gap: 0.35rem;
}

.discussion-in-sidebar .league-tab {
  padding: 0.35rem 0.65rem;
  font-size: 0.7rem;
}

.discussion-in-sidebar .discussion-content {
  padding: 0.75rem;
}

.discussion-in-sidebar .discussion-item {
  padding: 0.65rem;
}

.discussion-in-sidebar .discussion-text {
  font-size: 0.75rem;
  line-height: 1.5;
}

.discussion-in-sidebar .discussion-header {
  margin-bottom: 0.35rem;
}

.sidebar-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.25rem;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.sidebar-title {
  margin: 0 0 0.35rem;
  font-size: 0.9rem;
  font-weight: 700;
  color: #334155;
}

.sidebar-desc {
  margin: 0 0 0.75rem;
  font-size: 0.75rem;
  color: #94a3b8;
}

.sidebar-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.sidebar-stat {
  text-align: center;
  padding: 0.5rem;
  background: #f8fafc;
  border-radius: 8px;
}

.s-val {
  display: block;
  font-size: 1.2rem;
  font-weight: 800;
  color: #f97316;
}

.s-lbl {
  font-size: 0.65rem;
  color: #94a3b8;
}

.dynamics-feed {
  padding: 2rem 0 3rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.dynamic-card {
  background: #fff;
  border-radius: var(--radius-xl);
  padding: 1.5rem;
  border: 1px solid var(--color-neutral-200);
  transition: all var(--transition-base);
}

.dynamic-card:hover {
  box-shadow: var(--shadow-xl);
  border-color: var(--color-neutral-300);
}

.dynamic-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-accent-400) 0%, var(--color-primary-400) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  flex-shrink: 0;
}

.user-meta {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.user-name {
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-800);
  font-size: var(--font-size-base);
}

.post-time {
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
  margin-top: 0.15rem;
}

.post-type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
}

.post-type-badge.training { background: var(--color-info-light); color: var(--color-info); }
.post-type-badge.checkin { background: var(--color-success-light); color: var(--color-success); }
.post-type-badge.video { background: var(--color-danger-light); color: var(--color-danger); }
.post-type-badge.match { background: var(--color-warning-light); color: var(--color-warning); }
.post-type-badge.player { background: #f3e8ff; color: #7c3aed; }

.dynamic-body {
  margin-bottom: 1rem;
}

.dynamic-text {
  color: var(--color-neutral-700);
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
  margin: 0;
  white-space: pre-wrap;
}

.dynamic-images {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.75rem;
  flex-wrap: wrap;
}

.dynamic-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-200);
  transition: transform var(--transition-base);
}

.dynamic-image:hover {
  transform: scale(1.05);
}

.dynamic-videos {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 0.75rem;
}

.dynamic-video {
  width: 100%;
  max-width: 320px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-neutral-200);
  background: #000;
}

.media-section {
  margin-bottom: 0.75rem;
}

.btn-spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  margin-right: 0.25rem;
  vertical-align: middle;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.btn-submit:disabled,
.btn-cancel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.dynamic-actions {
  display: flex;
  gap: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-neutral-100);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: var(--font-size-sm);
  color: var(--color-neutral-400);
  padding: 0.35rem 0.75rem;
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
  font-family: inherit;
}

.action-btn:hover {
  color: var(--color-accent-500);
  background: var(--color-accent-50);
}

.action-btn.liked {
  color: var(--color-danger);
}

.action-btn.active {
  color: var(--color-primary-500);
  background: var(--color-primary-50);
}

.comments-section {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-neutral-100);
}

.no-comments {
  text-align: center;
  padding: 1.5rem;
  color: var(--color-neutral-400);
  font-size: var(--font-size-sm);
}

.no-comments p {
  margin: 0;
}

.comments-list {
  margin-bottom: 1rem;
}

.comment-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--color-neutral-100);
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-neutral-200);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 1rem;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.comment-user {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-700);
}

.comment-time {
  font-size: var(--font-size-xs);
  color: var(--color-neutral-400);
}

.comment-text {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  line-height: var(--line-height-relaxed);
}

.comment-input-row {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.comment-textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  background: var(--color-neutral-50);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-700);
  resize: vertical;
  font-family: inherit;
  transition: border-color var(--transition-fast);
}

.comment-textarea:focus {
  outline: none;
  border-color: var(--color-primary-400);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.comment-textarea::placeholder {
  color: var(--color-neutral-400);
}

.send-btn {
  align-self: flex-end;
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

.send-btn:hover {
  background: var(--color-primary-600);
}

.empty-state {
  text-align: center;
  padding: 5rem 2rem;
  background: #fff;
  border-radius: var(--radius-xl);
  border: 2px dashed var(--color-neutral-200);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: var(--font-size-xl);
  color: var(--color-neutral-700);
  margin: 0 0 0.5rem;
}

.empty-state p {
  color: var(--color-neutral-400);
  margin: 0 0 1.5rem;
}

.empty-btn {
  padding: 0.7rem 2rem;
  background: linear-gradient(135deg, var(--color-accent-500) 0%, var(--color-accent-600) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: inherit;
}

.empty-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(34, 197, 94, 0.4);
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
  max-width: 520px;
  width: 100%;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
}

.share-modal-container {
  max-width: 440px;
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

.modal-header h2 {
  margin: 0;
  font-size: var(--font-size-xl);
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
  padding: 1.5rem 1.75rem;
}

.content-input {
  width: 100%;
  min-height: 120px;
  padding: 1rem;
  background: var(--color-neutral-50);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-base);
  color: var(--color-neutral-700);
  resize: vertical;
  font-family: inherit;
  line-height: var(--line-height-relaxed);
  box-sizing: border-box;
  transition: border-color var(--transition-fast);
}

.content-input:focus {
  outline: none;
  border-color: var(--color-primary-400);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.content-input::placeholder {
  color: var(--color-neutral-400);
}

.char-count {
  text-align: right;
  font-size: 0.7rem;
  color: #94a3b8;
  margin-top: 0.25rem;
  margin-bottom: 0.5rem;
}
.emoji-section {
  margin-top: 1.25rem;
}

.section-label {
  display: block;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--color-neutral-400);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
}

.emoji-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.emoji-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  background: var(--color-neutral-50);
  border: 1px solid transparent;
}

.emoji-item:hover {
  background: var(--color-neutral-100);
  border-color: var(--color-neutral-200);
  transform: scale(1.15);
}

.type-section {
  margin-top: 1.25rem;
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.type-option {
  padding: 0.35rem 0.75rem;
  background: var(--color-neutral-50);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  color: var(--color-neutral-500);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
}

.type-option:hover {
  background: var(--color-neutral-100);
}

.type-option.active {
  background: var(--color-primary-500);
  color: #fff;
  border-color: var(--color-primary-500);
}

.tags-section,
.privacy-section,
.preview-section {
  margin-top: 1.25rem;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}

.tag-ch {
  padding: 0.25rem 0.6rem;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.7rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
}

.tag-ch:hover {
  border-color: #f97316;
  color: #f97316;
}

.tag-ch.active {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}

.preview-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 0.75rem 1rem;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.preview-avatar {
  width: 28px;
  height: 28px;
  background: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
}

.preview-name {
  display: block;
  font-size: 0.75rem;
  font-weight: 600;
  color: #334155;
}

.preview-time {
  font-size: 0.6rem;
  color: #cbd5e1;
}

.preview-type {
  margin-left: auto;
  font-size: 0.65rem;
  color: #64748b;
  background: #e2e8f0;
  padding: 0.15rem 0.45rem;
  border-radius: 4px;
}

.preview-content {
  margin: 0 0 0.4rem;
  font-size: 0.8rem;
  color: #475569;
  line-height: 1.5;
}

.preview-tags {
  display: flex;
  gap: 0.3rem;
  flex-wrap: wrap;
}

.preview-tag {
  font-size: 0.65rem;
  color: #f97316;
  background: #fff7ed;
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
}

.modal-footer {
  padding: 1rem 1.75rem;
  border-top: 1px solid var(--color-neutral-200);
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.btn-cancel {
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

.btn-cancel:hover {
  background: var(--color-neutral-200);
}

.btn-submit {
  padding: 0.6rem 1.5rem;
  background: linear-gradient(135deg, var(--color-accent-500) 0%, var(--color-accent-600) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: inherit;
}

.btn-submit:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
}

.share-url-group {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.25rem;
}

.share-url-input {
  flex: 1;
  padding: 0.7rem 1rem;
  background: var(--color-neutral-50);
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  color: var(--color-neutral-600);
  font-family: inherit;
}

.btn-copy {
  padding: 0.7rem 1.25rem;
  background: var(--color-primary-500);
  color: #fff;
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  white-space: nowrap;
}

.btn-copy:hover {
  background: var(--color-primary-600);
}

.share-options {
  padding-top: 1rem;
  border-top: 1px solid var(--color-neutral-100);
}

.share-buttons {
  display: flex;
  gap: 0.5rem;
}

.share-btn {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-neutral-200);
  border-radius: var(--radius-lg);
  background: #fff;
  font-size: var(--font-size-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-family: inherit;
  flex: 1;
}

.share-btn:hover {
  transform: translateY(-1px);
}

.share-btn.qq:hover { background: var(--color-info-light); border-color: var(--color-info); }
.share-btn.weibo:hover { background: var(--color-danger-light); border-color: var(--color-danger); }

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

.highlight-pulse {
  animation: highlightPulse 1.5s ease-in-out;
  border-radius: 16px;
}

@keyframes highlightPulse {
  0% {
    box-shadow: 0 0 0 0 rgba(249, 115, 22, 0.5);
  }
  50% {
    box-shadow: 0 0 0 16px rgba(249, 115, 22, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(249, 115, 22, 0);
  }
}

.discussion-in-feed {
  margin-top: 0;
}

@media (max-width: 900px) {
  .community-layout {
    grid-template-columns: 1fr;
  }

  .community-sidebar {
    position: static;
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

  .dynamic-actions {
    gap: 1rem;
  }

  .dynamic-header {
    flex-wrap: wrap;
  }

  .action-btn {
    font-size: var(--font-size-xs);
  }

  .modal-container {
    border-radius: var(--radius-xl);
  }

  .modal-header {
    border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  }

  .share-url-group {
    flex-direction: column;
  }

  .share-buttons {
    flex-direction: column;
  }
}
</style>