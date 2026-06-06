<template>
  <AppLayout>
    <div class="topic-detail-page">
      <div class="topic-header">
        <button @click="goBack" class="back-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          <span>返回</span>
        </button>
      </div>

      <div class="container">
        <div class="topic-card">
          <div class="topic-badge">{{ topic.icon }}</div>
          <h1 class="topic-title">{{ topic.text }}</h1>
          <div class="topic-meta">
            <span class="topic-count">{{ topic.count }}人参与讨论</span>
            <span class="topic-league">{{ topic.league }}</span>
            <span class="topic-source">📊 数据来源：贴吧平台</span>
          </div>
          <div class="topic-bar">
            <div class="bar-fill" :style="{ width: participationRate + '%' }"></div>
          </div>
        </div>

        <div class="discussion-area">
          <div class="discussion-header">
            <div class="header-left">
              <h2>💬 话题讨论</h2>
              <span class="discussion-count">{{ filteredDiscussions.length }}条讨论</span>
            </div>
            <div class="sort-controls">
              <button 
                v-for="s in sortOptions" 
                :key="s.value"
                :class="['sort-btn', { active: sortBy === s.value }]"
                @click="sortBy = s.value"
              >
                {{ s.label }}
              </button>
            </div>
          </div>

          <div class="quick-post">
            <div class="user-avatar">
              <span>👤</span>
            </div>
            <textarea
              v-model="newComment"
              placeholder="分享你的观点..."
              class="comment-input"
              rows="3"
              maxlength="500"
            ></textarea>
            <div class="quick-post-actions">
              <span class="char-count">{{ newComment.length }}/500</span>
              <span v-if="contentWarning" class="warning-text">⚠️ 内容包含敏感信息</span>
              <button @click="submitComment" class="submit-btn" :disabled="!newComment.trim() || contentWarning">
                发表
              </button>
            </div>
          </div>

          <div v-if="filteredDiscussions.length === 0" class="empty-discussion">
            <span>💭</span>
            <p>暂无讨论，快来发表你的观点吧！</p>
          </div>

          <div class="discussion-list">
            <div
              v-for="d in filteredDiscussions"
              :key="d.id"
              class="discussion-item"
              :class="{ 'quality-high': d.quality === 'high', 'quality-medium': d.quality === 'medium' }"
            >
              <div class="discussion-avatar">
                <span>{{ d.user?.avatarUrl || '👤' }}</span>
              </div>
              <div class="discussion-body">
                <div class="discussion-header">
                  <div class="user-info">
                    <span class="discussion-user">{{ d.user?.nickname || '用户' }}</span>
                    <span v-if="d.verified" class="verified-badge">✓ 认证</span>
                  </div>
                  <div class="meta-info">
                    <span class="discussion-time">{{ formatTime(d.createdAt) }}</span>
                    <span v-if="d.quality === 'high'" class="quality-badge">⭐ 精华</span>
                  </div>
                </div>
                <p class="discussion-text">{{ d.content }}</p>
                <div class="discussion-footer">
                  <button
                    :class="['like-btn', { liked: d.liked }]"
                    @click="toggleLike(d)"
                  >
                    <span>{{ d.liked ? '❤️' : '👍' }}</span>
                    <span>{{ d.likeCount }}</span>
                  </button>
                  <button class="reply-btn" @click="replyTo(d)">
                    <span>💬</span>
                    <span>{{ d.replyCount || d.replies?.length || 0 }} 回复</span>
                  </button>
                  <button class="share-btn" @click="shareDiscussion(d)">
                    <span>🔗</span>
                    <span>分享</span>
                  </button>
                  <button class="report-btn" @click="reportDiscussion(d)">
                    <span>⚠️</span>
                    <span>举报</span>
                  </button>
                </div>

                <div v-if="d.showReply" class="reply-section">
                  <textarea
                    v-model="d.replyContent"
                    placeholder="写下你的回复..."
                    class="reply-input"
                    rows="2"
                  ></textarea>
                  <div class="reply-actions">
                    <button @click="cancelReply(d)" class="cancel-btn">取消</button>
                    <button @click="submitReply(d)" class="submit-reply-btn">回复</button>
                  </div>
                </div>

                <div v-if="d.replies && d.replies.length > 0" class="replies-list">
                  <div v-for="reply in d.replies" :key="reply.id" class="reply-item">
                    <span class="reply-user">{{ reply.user?.nickname || '用户' }}：</span>
                    <span class="reply-content">{{ reply.content }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="filteredDiscussions.length > 0" class="load-more">
            <button @click="loadMore" class="load-more-btn">
              加载更多
            </button>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/AppLayout.vue'

const route = useRoute()
const router = useRouter()

const topicId = ref(Number(route.params.id))
const newComment = ref('')
const sortBy = ref('hot')
const displayCount = ref(20)

const topics = [
  { id: 1, icon: '🏆', text: 'NBA总决赛谁能夺冠？', count: 432, league: 'NBA' },
  { id: 2, icon: '⭐', text: '东契奇能否成为历史最佳控卫？', count: 315, league: 'NBA' },
  { id: 3, icon: '🌍', text: '中国男篮世界杯前景如何？', count: 267, league: 'FIBA' },
  { id: 4, icon: '🇨🇳', text: 'CBA新赛季哪支球队最强？', count: 198, league: 'CBA' },
  { id: 5, icon: '🎯', text: '文班亚马的上限在哪里？', count: 289, league: 'NBA' },
  { id: 6, icon: '🏅', text: '奥运会男篮金牌花落谁家？', count: 176, league: '奥运会' }
]

const topic = computed(() => {
  return topics.find(t => t.id === topicId.value) || topics[0]
})

const participationRate = computed(() => {
  const maxCount = Math.max(...topics.map(t => t.count))
  return (topic.value.count / maxCount) * 100
})

const sortOptions = [
  { label: '🔥 最热', value: 'hot' },
  { label: '⏰ 最新', value: 'time' },
  { label: '⭐ 精华', value: 'quality' }
]

const sensitiveWords = ['敏感词1', '敏感词2', '敏感词3']

const contentWarning = computed(() => {
  if (!newComment.value) return false
  return sensitiveWords.some(word => newComment.value.includes(word))
})

const createDefaultDiscussions = () => {
  const result = []
  const userNames = ['篮球爱好者', '资深球迷', '篮球评论员', '篮球迷妹', '篮球分析师', '篮球教练', 'NBA球迷', 'CBA球迷']
  const contents = [
    '这个话题真的很有意思，值得深入讨论！',
    '期待看到更多精彩的观点！',
    '篮球的魅力就在于充满了不确定性！',
    '享受篮球带来的快乐就好！',
    '让我们用数据说话，理性讨论！',
    '战术执行和团队配合是获胜的关键！',
    '希望看到更多精彩的比赛！',
    '希望中国篮球越来越好！'
  ]
  const avatars = ['🏀', '👴', '🎤', '💕', '📊', '🎯', '⚽', '🇨🇳']
  
  for (let i = 0; i < 20; i++) {
    result.push({
      id: i + 1,
      user: { nickname: userNames[i % userNames.length], avatarUrl: avatars[i % avatars.length] },
      content: contents[i % contents.length],
      createdAt: Date.now() - (i * 3600000),
      likeCount: Math.floor(Math.random() * 200) + 50,
      liked: false,
      showReply: false,
      replyContent: '',
      replies: [],
      quality: i < 5 ? 'high' : 'medium',
      verified: i < 3,
      replyCount: Math.floor(Math.random() * 10),
      images: []
    })
  }
  return result
}

const discussions = ref(createDefaultDiscussions())

const filteredDiscussions = computed(() => {
  let result = [...discussions.value].filter(d => d.quality !== 'low')
  if (sortBy.value === 'hot') {
    result.sort((a, b) => b.likeCount - a.likeCount)
  } else if (sortBy.value === 'time') {
    result.sort((a, b) => b.createdAt - a.createdAt)
  } else if (sortBy.value === 'quality') {
    result.sort((a, b) => {
      const qualityRank = { high: 3, medium: 2, low: 1 }
      if (qualityRank[a.quality] !== qualityRank[b.quality]) {
        return qualityRank[b.quality] - qualityRank[a.quality]
      }
      return b.likeCount - a.likeCount
    })
  }
  return result.slice(0, displayCount.value)
})

const goBack = () => {
  router.back()
}

const formatTime = (timestamp) => {
  const now = Date.now()
  const diff = now - timestamp
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  const date = new Date(timestamp)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const submitComment = () => {
  if (!newComment.value.trim() || contentWarning.value) return
  
  discussions.value.unshift({
    id: Date.now(),
    user: { nickname: '我', avatarUrl: '👤' },
    content: newComment.value,
    createdAt: Date.now(),
    likeCount: 0,
    liked: false,
    showReply: false,
    replyContent: '',
    replies: [],
    quality: 'medium',
    verified: false,
    replyCount: 0,
    images: []
  })
  
  ElMessage.success('发表成功！')
  newComment.value = ''
}

const toggleLike = (d) => {
  d.liked = !d.liked
  d.likeCount += d.liked ? 1 : -1
}

const replyTo = (d) => {
  d.showReply = !d.showReply
}

const cancelReply = (d) => {
  d.showReply = false
  d.replyContent = ''
}

const submitReply = (d) => {
  if (!d.replyContent.trim()) return
  
  if (!d.replies) d.replies = []
  d.replies.push({
    id: Date.now(),
    user: { nickname: '我' },
    content: d.replyContent
  })
  d.replyCount = (d.replyCount || 0) + 1
  
  ElMessage.success('回复成功！')
  d.replyContent = ''
  d.showReply = false
}

const shareDiscussion = (d) => {
  ElMessage.success('分享成功！')
}

const reportDiscussion = (d) => {
  ElMessage.success('举报已提交，我们会尽快处理！')
}

const loadMore = () => {
  displayCount.value += 10
  ElMessage.success('加载成功！')
}

onMounted(() => {
  document.title = topic.value.text + ' - 篮球社区'
})
</script>

<style scoped>
.topic-detail-page {
  min-height: 100vh;
  background: #f8fafc;
}

.topic-header {
  background: #fff;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 64px;
  z-index: 100;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f1f5f9;
  border: none;
  border-radius: 8px;
  color: #475569;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #e2e8f0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 1.5rem 1rem;
}

.topic-card {
  background: linear-gradient(135deg, #1e293b, #334155);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  color: #fff;
  margin-bottom: 1.5rem;
}

.topic-badge {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.topic-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 0 0.75rem;
}

.topic-meta {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  opacity: 0.8;
  flex-wrap: wrap;
}

.topic-count {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
}

.topic-league {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
}

.topic-source {
  background: rgba(255, 255, 255, 0.15);
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8rem;
}

.topic-bar {
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #f97316, #ea580c);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.discussion-area {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.discussion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #f1f5f9;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.discussion-header h2 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.discussion-count {
  font-size: 0.85rem;
  color: #94a3b8;
}

.sort-controls {
  display: flex;
  gap: 0.5rem;
}

.sort-btn {
  padding: 0.35rem 0.75rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.8rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-btn:hover {
  background: #e2e8f0;
}

.sort-btn.active {
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  border-color: #ea580c;
}

.quick-post {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #f1f5f9;
}

.quick-post .user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f97316, #ea580c);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  float: left;
  margin-right: 0.75rem;
}

.comment-input {
  width: calc(100% - 52px);
  padding: 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.9rem;
  resize: none;
  font-family: inherit;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.comment-input:focus {
  outline: none;
  border-color: #f97316;
}

.comment-input::placeholder {
  color: #cbd5e1;
}

.quick-post-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 1rem;
  margin-top: 0.75rem;
  flex-wrap: wrap;
}

.char-count {
  font-size: 0.8rem;
  color: #94a3b8;
}

.warning-text {
  font-size: 0.8rem;
  color: #ef4444;
}

.submit-btn {
  padding: 0.5rem 1.25rem;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.3);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-discussion {
  text-align: center;
  padding: 3rem 1rem;
  color: #94a3b8;
}

.empty-discussion span {
  font-size: 3rem;
  display: block;
  margin-bottom: 0.5rem;
}

.empty-discussion p {
  margin: 0;
}

.discussion-list {
  padding: 0.5rem 0;
}

.discussion-item {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #f8fafc;
  transition: background-color 0.2s;
}

.discussion-item:hover {
  background: #fafbfc;
}

.discussion-item.quality-high {
  background: linear-gradient(90deg, #fffbeb, #fff);
  border-left: 3px solid #f59e0b;
}

.discussion-item.quality-high:hover {
  background: linear-gradient(90deg, #fef3c7, #fafbfc);
}

.discussion-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #64748b, #475569);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.discussion-body {
  flex: 1;
  min-width: 0;
}

.discussion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  border: none;
  margin-bottom: 0.4rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.discussion-user {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1e293b;
}

.verified-badge {
  background: #3b82f6;
  color: #fff;
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  font-size: 0.7rem;
}

.discussion-time {
  font-size: 0.75rem;
  color: #94a3b8;
}

.quality-badge {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: #fff;
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  font-size: 0.7rem;
}

.discussion-text {
  margin: 0 0 0.5rem;
  font-size: 0.9rem;
  line-height: 1.7;
  color: #334155;
  word-break: break-word;
}

.discussion-footer {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.like-btn,
.reply-btn,
.share-btn,
.report-btn {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.3rem 0.6rem;
  background: none;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.like-btn:hover,
.reply-btn:hover,
.share-btn:hover {
  background: #f1f5f9;
}

.report-btn:hover {
  background: #fef2f2;
  color: #ef4444;
}

.like-btn.liked {
  color: #ef4444;
}

.reply-section {
  margin-top: 0.75rem;
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 8px;
}

.reply-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.85rem;
  resize: none;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 0.5rem;
}

.reply-input:focus {
  outline: none;
  border-color: #f97316;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.cancel-btn {
  padding: 0.35rem 0.75rem;
  background: #f1f5f9;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
  color: #64748b;
  cursor: pointer;
}

.cancel-btn:hover {
  background: #e2e8f0;
}

.submit-reply-btn {
  padding: 0.35rem 0.75rem;
  background: #f97316;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
  cursor: pointer;
}

.submit-reply-btn:hover {
  background: #ea580c;
}

.replies-list {
  margin-top: 0.75rem;
  padding-left: 0.75rem;
  border-left: 2px solid #e2e8f0;
}

.reply-item {
  padding: 0.5rem 0;
  font-size: 0.85rem;
  color: #475569;
}

.reply-user {
  font-weight: 600;
  color: #1e293b;
}

.reply-content {
  color: #475569;
}

.load-more {
  padding: 1rem;
  text-align: center;
  border-top: 1px solid #f1f5f9;
}

.load-more-btn {
  padding: 0.5rem 2rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #475569;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.load-more-btn:hover {
  background: #e2e8f0;
}

@media (max-width: 600px) {
  .topic-card {
    padding: 1.5rem 1rem;
  }
  
  .topic-title {
    font-size: 1.25rem;
  }
  
  .discussion-item {
    padding: 0.75rem;
  }
  
  .sort-controls {
    flex-wrap: wrap;
  }
}
</style>
