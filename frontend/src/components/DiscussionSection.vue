<template>
  <div class="discussion-section">
    <div class="section-card">
      <div class="section-header">
        <div class="header-left">
          <span class="section-icon">💬</span>
          <div>
            <h2 class="section-title">赛事讨论区</h2>
            <p class="section-desc">体育赛事 · 战术分析 · 球迷互动</p>
          </div>
        </div>
        <button class="new-discussion-btn" @click="$emit('new-discussion', activeLeague)">
          <span>✏️</span>
          <span>发起新讨论</span>
        </button>
      </div>

      <div class="league-tabs">
        <button
          v-for="l in leagues"
          :key="l.value"
          :class="['league-tab', { active: activeLeague === l.value }]"
          @click="selectLeague(l.value)"
        >
          {{ l.icon }} {{ l.label }}
        </button>
      </div>

      <div class="discussion-content">
        <div class="discussion-list">
          <div v-if="filteredDiscussions.length === 0" class="empty-discussion">
            <span>💭</span>
            <p>暂无讨论，快来发起话题吧！</p>
          </div>

          <div
            v-for="d in filteredDiscussions"
            :key="d.id"
            class="discussion-item"
            @click="$emit('view-discussion', d)"
          >
            <div class="discussion-avatar">
              <span>{{ d.user?.avatarUrl || '👤' }}</span>
            </div>
            <div class="discussion-body">
              <div class="discussion-header">
                <span class="discussion-user">{{ d.user?.nickname || '篮球爱好者' }}</span>
                <span class="discussion-time">{{ formatTime(d.createdAt) }}</span>
              </div>
              <p class="discussion-text">{{ truncate(d.content, 100) }}</p>
              <div class="discussion-footer">
                <div class="discussion-tags" v-if="d.tags">
                  <span v-for="tag in tagList(d.tags)" :key="tag" class="disc-tag">{{ tag }}</span>
                </div>
                <div class="discussion-stats">
                  <span>👍 {{ d.likeCount || 0 }}</span>
                  <span>💬 {{ d.commentCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="hot-discussions">
          <h4 class="hot-title">🔥 热门话题</h4>
          <div class="hot-list">
            <div
              v-for="topic in hotTopics"
              :key="topic.id"
              class="hot-topic-item"
              @click="goToTopic(topic.id)"
            >
              <span class="hot-icon">{{ topic.icon }}</span>
              <div class="hot-info">
                <span class="hot-text">{{ topic.text }}</span>
                <span class="hot-count">{{ topic.count }}人参与</span>
              </div>
              <span class="hot-arrow">→</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  discussions: {
    type: Array,
    default: () => []
  },
  activeLeague: {
    type: String,
    default: 'all'
  }
})

const emit = defineEmits(['update:activeLeague', 'new-discussion', 'view-discussion'])
const router = useRouter()

const localLeague = ref(props.activeLeague)

const goToTopic = (topicId) => {
  router.push(`/topic/${topicId}`)
}

watch(() => props.activeLeague, (val) => {
  localLeague.value = val
})

const leagues = [
  { value: 'all', label: '全部', icon: '🌐' },
  { value: 'NBA', label: 'NBA', icon: '🏀' },
  { value: 'FIBA', label: 'FIBA', icon: '🌍' },
  { value: 'CBA', label: 'CBA', icon: '🇨🇳' },
  { value: '奥运会', label: '奥运', icon: '🏅' },
  { value: '欧洲杯', label: '欧洲杯', icon: '⚽' }
]

const hotTopics = [
  { id: 1, icon: '🏆', text: 'NBA总决赛谁能夺冠？', count: 432, league: 'NBA', url: 'https://sports.qq.com/nba/' },
  { id: 2, icon: '⭐', text: '东契奇能否成为历史最佳控卫？', count: 315, league: 'NBA', url: 'https://sports.qq.com/nba/' },
  { id: 3, icon: '🌍', text: '中国男篮世界杯前景如何？', count: 267, league: 'FIBA', url: 'https://sports.qq.com/cba/' },
  { id: 4, icon: '🇨🇳', text: 'CBA新赛季哪支球队最强？', count: 198, league: 'CBA', url: 'https://sports.qq.com/cba/' },
  { id: 5, icon: '🎯', text: '文班亚马的上限在哪里？', count: 289, league: 'NBA', url: 'https://sports.qq.com/nba/' },
  { id: 6, icon: '🏅', text: '奥运会男篮金牌花落谁家？', count: 176, league: '奥运会', url: 'https://sports.qq.com/olympics/' }
]

const filteredDiscussions = computed(() => {
  if (localLeague.value === 'all') return props.discussions
  return props.discussions.filter(d => {
    if (!d.tags) return false
    return d.tags.some(tag => tag === localLeague.value)
  })
})

const selectLeague = (league) => {
  localLeague.value = league
  emit('update:activeLeague', league)
}

const tagList = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags.slice(0, 3)
  return String(tags).split(',').map(t => t.trim()).slice(0, 3)
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
  if (mins < 60) return mins + '分钟前'
  if (hours < 24) return hours + '小时前'
  return days + '天前'
}

const truncate = (text, len) => {
  if (!text) return ''
  return text.length > len ? text.substring(0, len) + '...' : text
}
</script>

<style scoped>
.discussion-section {
  margin: 2rem 0;
}

.section-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d3748 100%);
  color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.section-icon {
  font-size: 1.75rem;
}

.section-title {
  margin: 0;
  font-size: 1.15rem;
  font-weight: 700;
}

.section-desc {
  margin: 0.1rem 0 0;
  font-size: 0.75rem;
  opacity: 0.7;
}

.new-discussion-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.55rem 1.1rem;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
  white-space: nowrap;
}

.new-discussion-btn:hover {
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
  transform: translateY(-1px);
}

.league-tabs {
  display: flex;
  padding: 0.75rem 1.5rem;
  gap: 0.4rem;
  border-bottom: 1px solid #f1f5f9;
  overflow-x: auto;
}

.league-tab {
  padding: 0.35rem 0.85rem;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.78rem;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
  font-family: inherit;
  transition: all 0.2s;
}

.league-tab:hover {
  border-color: #f97316;
  color: #f97316;
}

.league-tab.active {
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  border-color: transparent;
}

.discussion-content {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 0;
}

.discussion-list {
  padding: 0.5rem 0;
  max-height: 500px;
  overflow-y: auto;
  border-right: 1px solid #f1f5f9;
}

.empty-discussion {
  text-align: center;
  padding: 3rem 1rem;
  color: #94a3b8;
  font-size: 0.875rem;
}

.empty-discussion span {
  font-size: 2.5rem;
  display: block;
  margin-bottom: 0.5rem;
}

.empty-discussion p {
  margin: 0;
}

.discussion-item {
  display: flex;
  padding: 0.85rem 1.25rem;
  gap: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f8fafc;
}

.discussion-item:hover {
  background: #fff7ed;
}

.discussion-item:last-child {
  border-bottom: none;
}

.discussion-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f1f5f9;
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
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.3rem;
}

.discussion-user {
  font-size: 0.85rem;
  font-weight: 600;
  color: #1e293b;
}

.discussion-time {
  font-size: 0.7rem;
  color: #94a3b8;
}

.discussion-text {
  margin: 0 0 0.4rem;
  font-size: 0.85rem;
  color: #475569;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.discussion-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.discussion-tags {
  display: flex;
  gap: 0.3rem;
  flex-wrap: wrap;
}

.disc-tag {
  padding: 0.1rem 0.45rem;
  border-radius: 4px;
  font-size: 0.65rem;
  font-weight: 600;
  background: #fff7ed;
  color: #ea580c;
}

.discussion-stats {
  display: flex;
  gap: 0.6rem;
  font-size: 0.7rem;
  color: #94a3b8;
}

.hot-discussions {
  padding: 1rem 1.25rem;
  background: linear-gradient(180deg, #fafbfc, #fff);
  overflow-y: auto;
  max-height: 500px;
}

.hot-title {
  margin: 0 0 0.75rem;
  font-size: 0.85rem;
  font-weight: 700;
  color: #1e293b;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.5rem 0.7rem;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  font-family: inherit;
  font-size: 0.8rem;
  transition: all 0.2s;
  color: inherit;
  cursor: pointer;
}

.hot-topic-item:hover {
  background: #fff7ed;
  border-color: #fed7aa;
  transform: translateX(2px);
}

.hot-icon {
  font-size: 1.1rem;
  flex-shrink: 0;
}

.hot-info {
  flex: 1;
  min-width: 0;
}

.hot-text {
  display: block;
  font-size: 0.8rem;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.hot-count {
  display: block;
  font-size: 0.7rem;
  color: #94a3b8;
  margin-top: 2px;
}

.hot-arrow {
  font-size: 1.1rem;
  color: #cbd5e1;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .discussion-content {
    grid-template-columns: 1fr;
  }
  .discussion-list {
    border-right: none;
    border-bottom: 1px solid #f1f5f9;
  }
}

@media (max-width: 600px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  .new-discussion-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
