<template>
  <div class="discussion-panel">
    <div class="panel-header">
      <div class="panel-title">
        <span class="title-icon">💬</span>
        <div>
          <h3>赛事讨论</h3>
          <p class="panel-desc">体育赛事 · 战术分析 · 球迷互动</p>
        </div>
      </div>
    </div>

    <div class="league-tabs">
      <button
        v-for="l in leagues"
        :key="l.value"
        :class="['league-tab', { active: localLeague === l.value }]"
        @click="selectLeague(l.value)"
      >
        {{ l.icon }} {{ l.label }}
      </button>
    </div>

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
          <p class="discussion-text">{{ truncate(d.content, 80) }}</p>
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

    <div class="panel-actions">
      <button class="new-discussion-btn" @click="$emit('new-discussion', localLeague)">
        <span>✏️</span>
        <span>发起新讨论</span>
      </button>
    </div>

    <div class="hot-discussions">
      <h4 class="section-title">🔥 热门讨论话题</h4>
      <div class="hot-list">
        <button
          v-for="topic in hotTopics"
          :key="topic.id"
          class="hot-topic-item"
          @click="$emit('new-discussion', topic.league)"
        >
          <span class="hot-icon">{{ topic.icon }}</span>
          <div class="hot-info">
            <span class="hot-text">{{ topic.text }}</span>
            <span class="hot-count">{{ topic.count }}人参与</span>
          </div>
          <span class="hot-arrow">›</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

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

const localLeague = ref(props.activeLeague)

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
  { id: 1, icon: '🏆', text: 'NBA总决赛谁能夺冠？', count: 432, league: 'NBA' },
  { id: 2, icon: '⭐', text: '东契奇能否成为历史最佳控卫？', count: 315, league: 'NBA' },
  { id: 3, icon: '🌍', text: '中国男篮世界杯前景如何？', count: 267, league: 'FIBA' },
  { id: 4, icon: '🇨🇳', text: 'CBA新赛季哪支球队最强？', count: 198, league: 'CBA' },
  { id: 5, icon: '🎯', text: '文班亚马的上限在哪里？', count: 289, league: 'NBA' },
  { id: 6, icon: '🏅', text: '奥运会男篮金牌花落谁家？', count: 176, league: '奥运会' }
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
.discussion-panel {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.25rem;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d3748 100%);
  color: #fff;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.title-icon {
  font-size: 1.5rem;
}

.panel-title h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
}

.panel-desc {
  margin: 0.1rem 0 0;
  font-size: 0.7rem;
  opacity: 0.6;
}

.league-tabs {
  display: flex;
  padding: 0.5rem 0.75rem;
  gap: 0.35rem;
  border-bottom: 1px solid #f1f5f9;
  overflow-x: auto;
}

.league-tab {
  padding: 0.3rem 0.75rem;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.75rem;
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

.discussion-list {
  padding: 0.25rem 0;
  max-height: 380px;
  overflow-y: auto;
}

.empty-discussion {
  text-align: center;
  padding: 2rem 1rem;
  color: #94a3b8;
  font-size: 0.875rem;
}

.empty-discussion span {
  font-size: 2rem;
  display: block;
  margin-bottom: 0.5rem;
}

.discussion-item {
  display: flex;
  padding: 0.75rem 1.25rem;
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
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
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
  margin-bottom: 0.25rem;
}

.discussion-user {
  font-size: 0.8rem;
  font-weight: 600;
  color: #1e293b;
}

.discussion-time {
  font-size: 0.7rem;
  color: #94a3b8;
}

.discussion-text {
  margin: 0 0 0.35rem;
  font-size: 0.8rem;
  color: #475569;
  line-height: 1.4;
}

.discussion-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.discussion-tags {
  display: flex;
  gap: 0.3rem;
}

.disc-tag {
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  font-size: 0.6rem;
  font-weight: 600;
  background: #fff7ed;
  color: #ea580c;
}

.discussion-stats {
  display: flex;
  gap: 0.5rem;
  font-size: 0.65rem;
  color: #94a3b8;
}

.panel-actions {
  padding: 0.6rem 1.25rem;
  border-top: 1px solid #f1f5f9;
}

.new-discussion-btn {
  width: 100%;
  padding: 0.6rem;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-family: inherit;
  transition: all 0.2s;
}

.new-discussion-btn:hover {
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  transform: translateY(-1px);
}

.hot-discussions {
  padding: 0.75rem 1.25rem 1rem;
  border-top: 1px solid #f1f5f9;
}

.section-title {
  margin: 0 0 0.5rem;
  font-size: 0.8rem;
  font-weight: 700;
  color: #1e293b;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.45rem 0.6rem;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  cursor: pointer;
  font-family: inherit;
  font-size: 0.78rem;
  transition: all 0.2s;
  text-align: left;
  width: 100%;
  color: inherit;
}

.hot-topic-item:hover {
  background: #fff7ed;
  border-color: #fed7aa;
}

.hot-icon {
  font-size: 1rem;
  flex-shrink: 0;
}

.hot-info {
  flex: 1;
  min-width: 0;
}

.hot-text {
  display: block;
  font-size: 0.78rem;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hot-count {
  display: block;
  font-size: 0.65rem;
  color: #94a3b8;
  margin-top: 2px;
}

.hot-arrow {
  font-size: 1.1rem;
  color: #cbd5e1;
  flex-shrink: 0;
}
</style>
