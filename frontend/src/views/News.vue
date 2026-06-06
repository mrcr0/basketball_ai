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
            <span>实时资讯</span>
          </span>
          <h1 class="banner-title">🏆 赛事资讯</h1>
          <p class="banner-desc">NBA、FIBA、奥运会等全球篮球赛事一手资讯，比赛数据、战术分析、球队动态实时掌握</p>
        </div>
      </div>
    </div>

    <div class="container">
      <!-- Filters -->
      <div class="filters-row">
        <div class="filter-group">
          <span class="filter-label">联赛：</span>
          <div class="filter-pills">
            <button :class="['filter-pill', { active: activeLeague === '' }]" @click="activeLeague = ''">全部</button>
            <button v-for="l in leagues" :key="l" :class="['filter-pill', { active: activeLeague === l }]" @click="activeLeague = l">{{ l }}</button>
          </div>
        </div>
        <div class="filter-group">
          <span class="filter-label">类型：</span>
          <div class="filter-pills">
            <button :class="['filter-pill', { active: activeType === '' }]" @click="activeType = ''">全部</button>
            <button v-for="t in newsTypes" :key="t.value" :class="['filter-pill', { active: activeType === t.value }]" @click="activeType = t.value">{{ t.label }}</button>
          </div>
        </div>
        <button class="refresh-btn" @click="fetchNews" :disabled="loading">
          <span :class="{ spinning: loading }">🔄</span> 刷新
        </button>
      </div>



      <!-- Tencent Sports NBA Hot News Section -->
      <div v-if="tencentNews.length > 0" class="tencent-section">
        <div class="section-header">
          <div class="section-title">
            <span class="tencent-logo">🐧</span>
            <span>腾讯体育</span>
            <span class="nba-badge">NBA热门赛事</span>
          </div>
          <span class="section-hint">实时同步腾讯体育最新资讯</span>
        </div>
        <div class="tencent-news-list">
          <div v-for="item in tencentNews.slice(0, 5)" :key="item.id" class="tencent-news-item" @click="openTencentSports">
            <div class="news-rank">{{ tencentNews.indexOf(item) + 1 }}</div>
            <div class="news-info">
              <h4 class="news-title">{{ item.title }}</h4>
              <div class="news-meta">
                <span class="meta-tag">{{ item.newsType }}</span>
                <span class="meta-time">{{ formatTime(item.publishTime) }}</span>
                <span class="meta-views">👁️ {{ formatViews(item.viewCount) }}</span>
              </div>
            </div>
            <div class="news-arrow">→</div>
          </div>
        </div>
      </div>

      <!-- News List -->
      <div v-if="filteredNews.length > 0" class="news-list">
        <div v-for="item in filteredNews" :key="item.id" class="news-card" @click="viewDetail(item)">
          <div class="news-card-left">
            <div :class="'news-type-icon type-' + (item.newsType || '')">
              {{ getNewsTypeIcon(item.newsType) }}
            </div>
          </div>
          <div class="news-card-body">
            <div class="news-card-header">
              <span :class="'type-badge type-' + (item.newsType || '')">{{ getNewsTypeLabel(item.newsType) }}</span>
              <span v-if="item.league" class="league-badge">{{ item.league }}</span>
              <span class="time-text">{{ formatTime(item.publishTime) }}</span>
            </div>
            <h3 class="news-title">{{ item.title }}</h3>
            <p class="news-summary">{{ item.summary || truncate(item.content, 120) }}</p>
            <div class="news-card-footer">
              <span class="footer-tag" v-if="item.tags">
                <span v-for="tag in (item.tags || '').split(',').slice(0, 3)" :key="tag" class="mini-tag">{{ tag.trim() }}</span>
              </span>
              <span class="footer-meta">
                <span>👁️ {{ item.viewCount || 0 }}</span>
                <span v-if="item.source">· 来源：{{ item.source }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">📰</div>
        <h3>暂无资讯</h3>
        <p>当前筛选条件下没有资讯，请尝试调整筛选</p>
      </div>

      <!-- Detail Dialog -->
      <el-dialog
        v-model="detailVisible"
        :title="currentNews?.title"
        width="800px"
        class="news-dialog"
        destroy-on-close
      >
        <div v-if="currentNews" class="detail-content">
          <div class="detail-meta">
            <span :class="'type-badge type-' + (currentNews.newsType || '')">{{ getNewsTypeLabel(currentNews.newsType) }}</span>
            <span v-if="currentNews.league" class="league-badge">{{ currentNews.league }}</span>
            <span class="meta-text">{{ formatTime(currentNews.publishTime) }}</span>
            <span class="meta-text">👁️ {{ currentNews.viewCount }}</span>
            <span v-if="currentNews.source" class="meta-text">来源：{{ currentNews.source }}</span>
          </div>
          <div v-if="currentNews.summary" class="detail-summary">
            <strong>摘要：</strong>{{ currentNews.summary }}
          </div>
          <div class="detail-body" v-html="formatContent(currentNews.content)"></div>
          <div v-if="currentNews.tags" class="detail-tags">
            <span v-for="tag in (currentNews.tags || '').split(',')" :key="tag" class="tag-item">{{ tag.trim() }}</span>
          </div>
          <div v-if="currentNews.teamNames || currentNews.playerNames" class="detail-related">
            <div v-if="currentNews.teamNames">
              <strong>涉及球队：</strong>{{ currentNews.teamNames }}
            </div>
            <div v-if="currentNews.playerNames">
              <strong>涉及球员：</strong>{{ currentNews.playerNames }}
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '../components/AppLayout.vue'
import { getNews, getTencentNews } from '../api/news'
import { ElMessage } from 'element-plus'

const leagues = ref(['NBA', 'FIBA', 'CBA', '奥运会', '欧洲杯', '世界杯'])
const newsTypes = [
  { value: '比赛数据', label: '比赛数据' },
  { value: '球队动态', label: '球队动态' },
  { value: '球员表现', label: '球员表现' },
  { value: '战术分析', label: '战术分析' },
  { value: '转会新闻', label: '转会新闻' },
  { value: '花边新闻', label: '花边新闻' }
]

const activeLeague = ref('')
const activeType = ref('')
const allNews = ref([])
const tencentNews = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentNews = ref(null)

const totalNews = computed(() => allNews.value.length)

const filteredNews = computed(() => {
  let list = allNews.value
  if (activeLeague.value) list = list.filter(n => n.league === activeLeague.value)
  if (activeType.value) list = list.filter(n => n.newsType === activeType.value)
  return list
})

const fetchNews = async () => {
  loading.value = true
  try {
    const [newsRes, tencentRes] = await Promise.all([
      getNews({ limit: 30 }),
      getTencentNews()
    ])
    allNews.value = (newsRes.data.data || newsRes.data || [])
    tencentNews.value = (tencentRes.data.data || tencentRes.data || [])
  } catch (e) {
    ElMessage.error('加载赛事资讯失败')
  } finally {
    loading.value = false
  }
}

const getNewsTypeIcon = (type) => {
  const map = { '比赛数据': '📊', '球队动态': '📢', '球员表现': '⭐', '战术分析': '🎯', '转会新闻': '🔄' }
  return map[type] || '📰'
}

const getNewsTypeLabel = (type) => {
  return type || '综合'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const now = new Date()
  const diff = now - d
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return timeStr.substring(0, 10)
}

const formatViews = (count) => {
  if (!count || count === 0) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}

const truncate = (text, maxLen) => {
  if (!text) return ''
  const plain = text.replace(/【.*?】/g, '').replace(/\n/g, ' ')
  return plain.length > maxLen ? plain.substring(0, maxLen) + '...' : plain
}

const formatContent = (content) => {
  if (!content) return ''
  return content
    .replace(/\n/g, '<br>')
    .replace(/【(.*?)】/g, '<strong class="section-title">【$1】</strong>')
    .replace(/· /g, '&nbsp;&nbsp;· ')
}

const viewDetail = (item) => {
  currentNews.value = item
  detailVisible.value = true
}

const openTencentSports = () => {
  window.open('https://sports.qq.com/', '_blank')
}

onMounted(fetchNews)
</script>

<style scoped>
/* Banner */
.page-banner {
  position: relative;
  padding: 60px 0 40px;
  background: linear-gradient(135deg, #1a202c 0%, #2d3748 50%, #1a365d 100%);
  overflow: hidden;
}
.banner-bg { position: absolute; inset: 0; }
.banner-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
}
.banner-orb-1 {
  width: 400px; height: 400px;
  background: #ed8936;
  top: -100px; right: -100px;
}
.banner-orb-2 {
  width: 300px; height: 300px;
  background: #e53e3e;
  bottom: -100px; left: -50px;
}
.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
}
.banner-content { text-align: center; }
.banner-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(255,255,255,0.1);
  border-radius: 20px;
  color: #a0aec0;
  font-size: 13px;
  margin-bottom: 16px;
}
.badge-dot {
  width: 8px; height: 8px;
  background: #ed8936;
  border-radius: 50%;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
.banner-title {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
}
.banner-desc {
  color: #a0aec0;
  font-size: 16px;
  margin: 0;
}

/* Filters */
.filters-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-start;
  margin: 24px 0 16px;
  padding: 20px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
}
.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 200px;
}
.filter-label {
  font-size: 13px;
  font-weight: 600;
  color: #4a5568;
}
.filter-pills {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.filter-pill {
  padding: 5px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: #f7fafc;
  cursor: pointer;
  font-size: 13px;
  color: #718096;
  transition: all 0.2s;
}
.filter-pill:hover { border-color: #ed8936; color: #ed8936; }
.filter-pill.active {
  background: #ed8936;
  color: #fff;
  border-color: #ed8936;
}
.refresh-btn {
  padding: 8px 16px;
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
  color: #4a5568;
  white-space: nowrap;
  transition: all 0.2s;
}
.refresh-btn:hover { background: #edf2f7; }
.spinning { animation: spin 1s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }

/* Stats */
.stats-bar {
  display: flex;
  gap: 30px;
  padding: 16px 24px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  margin-bottom: 20px;
}

/* Tencent Sports Section */
.tencent-section {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}

.tencent-logo {
  font-size: 24px;
}

.nba-badge {
  padding: 2px 10px;
  background: linear-gradient(135deg, #e53935, #b71c1c);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  margin-left: 8px;
}

.section-hint {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.tencent-news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tencent-news-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  background: rgba(255, 255, 255, 0.03);
}

.tencent-news-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(4px);
}

.news-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}

.tencent-news-item:nth-child(1) .news-rank {
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  color: #1a1a2e;
}

.tencent-news-item:nth-child(2) .news-rank {
  background: linear-gradient(135deg, #c0c0c0, #a0a0a0);
  color: #1a1a2e;
}

.tencent-news-item:nth-child(3) .news-rank {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  color: #fff;
}

.news-info {
  flex: 1;
  min-width: 0;
}

.tencent-news-item .news-title {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.8);
}

.meta-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.meta-views {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.news-arrow {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.4);
  transition: all 0.2s;
}

.tencent-news-item:hover .news-arrow {
  color: rgba(255, 255, 255, 0.8);
  transform: translateX(4px);
}
.stat-item {
  display: flex;
  flex-direction: column;
}
.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #2d3748;
}
.stat-value.green { color: #38a169; }
.stat-label {
  font-size: 12px;
  color: #a0aec0;
  margin-top: 2px;
}

/* News List */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 40px;
}
.news-card {
  display: flex;
  gap: 16px;
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
}
.news-card:hover {
  border-color: #ed8936;
  box-shadow: 0 4px 16px rgba(237, 137, 54, 0.08);
  transform: translateX(4px);
}
.news-card-left { flex-shrink: 0; }
.news-type-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}
.type-比赛数据 { background: #ebf8ff; }
.type-球队动态 { background: #faf5ff; }
.type-球员表现 { background: #fff5f5; }
.type-战术分析 { background: #f0fff4; }
.type-转会新闻 { background: #fffbeb; }
.type-花边新闻 { background: #fdf2f8; }

.news-card-body { flex: 1; min-width: 0; }
.news-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}
.type-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 600;
}
.type-比赛数据 { background: #ebf8ff; color: #3182ce; }
.type-球队动态 { background: #faf5ff; color: #805ad5; }
.type-球员表现 { background: #fff5f5; color: #e53e3e; }
.type-战术分析 { background: #f0fff4; color: #38a169; }
.type-转会新闻 { background: #fffbeb; color: #d69e2e; }
.type-花边新闻 { background: #fdf2f8; color: #be185d; }

.league-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 8px;
  background: #edf2f7;
  color: #718096;
  font-weight: 600;
}
.time-text {
  font-size: 12px;
  color: #a0aec0;
  margin-left: auto;
}
.news-title {
  font-size: 16px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 6px;
  line-height: 1.4;
}
.news-summary {
  font-size: 13px;
  color: #718096;
  margin: 0 0 8px;
  line-height: 1.6;
}
.news-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.mini-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #f7fafc;
  border-radius: 10px;
  color: #a0aec0;
  margin-right: 4px;
}
.footer-meta {
  font-size: 12px;
  color: #a0aec0;
}

/* Dialog */
.detail-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}
.meta-text {
  font-size: 13px;
  color: #a0aec0;
}
.detail-summary {
  background: #f7fafc;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #4a5568;
  line-height: 1.7;
}
.detail-body {
  line-height: 1.9;
  color: #2d3748;
  font-size: 15px;
}
.detail-body :deep(.section-title) {
  display: block;
  font-size: 17px;
  margin-top: 20px;
  margin-bottom: 8px;
  color: #2d3748;
}
.detail-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}
.tag-item {
  font-size: 12px;
  padding: 4px 12px;
  background: #edf2f7;
  border-radius: 16px;
  color: #4a5568;
}
.detail-related {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f7fafc;
  border-radius: 10px;
  font-size: 14px;
  color: #4a5568;
  line-height: 1.8;
}

/* Empty */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #a0aec0;
}
.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-state h3 { margin: 0 0 8px; color: #4a5568; }
.empty-state p { margin: 0; font-size: 14px; }

@media (max-width: 768px) {
  .news-card { flex-direction: column; }
  .banner-title { font-size: 26px; }
}
</style>
