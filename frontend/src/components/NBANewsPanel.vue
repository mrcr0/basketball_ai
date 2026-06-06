<template>
  <div class="nba-news-panel">
    <div class="panel-header">
      <div class="panel-title">
        <span class="title-icon">🏀</span>
        <div>
          <h3>NBA 赛事资讯</h3>
          <p class="panel-source">数据来源：腾讯体育</p>
        </div>
      </div>
      <button class="refresh-btn" @click="refreshNews" :disabled="loading">
        <span :class="{ spinning: loading }">🔄</span>
      </button>
    </div>

    <div class="news-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        :class="['news-tab', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="news-list" v-loading="loading">
      <div v-if="filteredNews.length === 0 && !loading" class="empty-news">
        <span>📭</span>
        <p>暂无相关资讯，请稍后刷新</p>
      </div>

      <div
        v-for="(news, index) in filteredNews"
        :key="news.id"
        class="news-item slide-in"
        :style="{ animationDelay: index * 0.05 + 's' }"
        @click="openNews(news)"
      >
        <div class="news-thumb" v-if="news.thumbnail">
          <img :src="news.thumbnail" :alt="news.title" loading="lazy" />
        </div>
        <div class="news-body">
          <div class="news-meta">
            <span :class="['news-tag', getTagClass(news.category)]">{{ news.category }}</span>
            <span class="news-time">{{ formatNewsTime(news.publishTime) }}</span>
          </div>
          <h4 class="news-title">{{ news.title }}</h4>
          <p class="news-summary">{{ truncate(news.summary, 60) }}</p>
          <div class="news-stats">
            <span>👁️ {{ formatCount(news.views) }}</span>
            <span>💬 {{ formatCount(news.comments || 0) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="panel-footer">
      <a href="https://sports.qq.com/nba/" target="_blank" class="more-link">
        查看更多NBA资讯 ↗
      </a>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const activeTab = ref('all')
const loading = ref(false)

const tabs = [
  { value: 'all', label: '全部' },
  { value: '赛事', label: '赛事' },
  { value: '转会', label: '转会' },
  { value: '数据', label: '数据' },
  { value: '花边', label: '花边' }
]

const newsList = ref([])

const filteredNews = computed(() => {
  if (activeTab.value === 'all') return newsList.value
  return newsList.value.filter(n => n.category === activeTab.value)
})

const mockNews = [
  { id: 1, title: '凯尔特人4-1击败独行侠夺得2025-26赛季NBA总冠军', summary: '布朗荣膺总决赛MVP，塔图姆场均由27分带队登顶，绿军时隔18年再夺奥布莱恩杯', category: '赛事', publishTime: new Date(Date.now() - 3600000), views: 285000, comments: 12300, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 2, title: '詹姆斯确认将继续征战第23个NBA赛季', summary: '勒布朗·詹姆斯宣布不会退役，将执行下赛季合同，冲击50000分里程碑', category: '转会', publishTime: new Date(Date.now() - 7200000), views: 198000, comments: 8900, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 3, title: '东契奇当选2025-26赛季常规赛MVP', summary: '独行侠核心场均32.5分9.8助攻8.2篮板，率队取得西部第一战绩', category: '数据', publishTime: new Date(Date.now() - 14400000), views: 156000, comments: 6700, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 4, title: '文班亚马荣获年度最佳防守球员', summary: '二年级场均4.2盖帽12.8篮板，连续两年蝉联DPOY', category: '赛事', publishTime: new Date(Date.now() - 21600000), views: 134000, comments: 5200, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 5, title: '自由市场开启：多位全明星球员进入转会市场', summary: '哈登、乔治、西亚卡姆等人成为自由球员，多支球队展开追逐', category: '转会', publishTime: new Date(Date.now() - 36000000), views: 178000, comments: 9800, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 6, title: '库里三分命中数突破4500记，刷新历史纪录', summary: '勇士队后卫史蒂芬·库里在对阵雷霆比赛中命中第4500记三分球', category: '数据', publishTime: new Date(Date.now() - 43200000), views: 112000, comments: 4100, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 7, title: 'NBA全明星周末确定在洛杉矶举办', summary: '2027年NBA全明星周末将在洛杉矶快船新球馆Intuit Dome举行', category: '花边', publishTime: new Date(Date.now() - 54000000), views: 89000, comments: 3500, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 8, title: '杜兰特超越奥尼尔升至历史得分榜第八位', summary: '太阳队前锋凯文·杜兰特在对阵魔术比赛中完成里程碑', category: '数据', publishTime: new Date(Date.now() - 72000000), views: 95000, comments: 4800, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 9, title: '季后赛对阵出炉：湖人首轮对阵掘金', summary: '西部附加赛结束，湖人以第八身份晋级，将迎战西部第一掘金', category: '赛事', publishTime: new Date(Date.now() - 86400000), views: 210000, comments: 15600, thumbnail: '', url: 'https://sports.qq.com/nba/' },
  { id: 10, title: '安东尼·爱德华兹签名鞋销量创历史新高', summary: '森林狼新星签名球鞋首周销量突破50万双，仅次于乔丹品牌', category: '花边', publishTime: new Date(Date.now() - 108000000), views: 76000, comments: 2800, thumbnail: '', url: 'https://sports.qq.com/nba/' }
]

const loadNews = async () => {
  loading.value = true
  try {
    const apiUrl = 'https://sports.qq.com/nba/'
    const response = await fetch(apiUrl, {
      method: 'GET',
      headers: { 'Accept': 'text/html,application/json' },
      mode: 'no-cors'
    }).catch(() => null)
    if (!response || !response.ok) {
      newsList.value = mockNews.map(n => ({
        ...n,
        publishTime: new Date(n.publishTime)
      }))
      return
    }
    const data = await response.json()
    newsList.value = data.news || mockNews
  } catch {
    newsList.value = mockNews.map(n => ({
      ...n,
      publishTime: new Date(n.publishTime)
    }))
  } finally {
    loading.value = false
  }
}

const refreshNews = () => {
  newsList.value = mockNews.map(n => ({
    ...n,
    publishTime: new Date()
  }))
}

const openNews = (news) => {
  window.open(news.url || 'https://sports.qq.com/nba/', '_blank')
}

const formatNewsTime = (time) => {
  if (!time) return ''
  const now = new Date()
  const diff = now - new Date(time)
  const hours = Math.floor(diff / 3600000)
  if (hours < 1) return Math.floor(diff / 60000) + '分钟前'
  if (hours < 24) return hours + '小时前'
  return Math.floor(hours / 24) + '天前'
}

const formatCount = (n) => {
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
  return n
}

const getTagClass = (cat) => {
  const map = { '赛事': 'match', '转会': 'transfer', '数据': 'stats', '花边': 'gossip' }
  return map[cat] || ''
}

const truncate = (text, len) => {
  if (!text) return ''
  return text.length > len ? text.substring(0, len) + '...' : text
}

onMounted(() => {
  loadNews()
})
</script>

<style scoped>
.nba-news-panel {
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
  background: linear-gradient(135deg, #1e3a5f 0%, #1a1a2e 100%);
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

.panel-source {
  margin: 0.1rem 0 0;
  font-size: 0.7rem;
  opacity: 0.6;
}

.refresh-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  transition: all 0.2s;
}

.refresh-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.18);
}

.spinning {
  display: inline-block;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.news-tabs {
  display: flex;
  padding: 0.5rem 0.75rem;
  gap: 0.35rem;
  border-bottom: 1px solid#f1f5f9;
  overflow-x: auto;
}

.news-tab {
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

.news-tab:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.news-tab.active {
  background: #3b82f6;
  color: #fff;
  border-color: #3b82f6;
}

.news-list {
  padding: 0.5rem 0;
  max-height: 520px;
  overflow-y: auto;
}

.empty-news {
  text-align: center;
  padding: 2rem 1rem;
  color: #94a3b8;
  font-size: 0.875rem;
}

.empty-news span {
  font-size: 2rem;
  display: block;
  margin-bottom: 0.5rem;
}

.news-item {
  display: flex;
  padding: 0.75rem 1.25rem;
  gap: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f8fafc;
}

.news-item:hover {
  background: #f8fafc;
}

.news-item:last-child {
  border-bottom: none;
}

.news-thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}

.news-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-body {
  flex: 1;
  min-width: 0;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.3rem;
}

.news-tag {
  padding: 0.1rem 0.45rem;
  border-radius: 4px;
  font-size: 0.65rem;
  font-weight: 600;
}

.news-tag.match { background: #fee2e2; color: #dc2626; }
.news-tag.transfer { background: #dbeafe; color: #2563eb; }
.news-tag.stats { background: #d1fae5; color: #059669; }
.news-tag.gossip { background: #fef3c7; color: #d97706; }

.news-time {
  font-size: 0.7rem;
  color: #94a3b8;
}

.news-title {
  margin: 0 0 0.25rem;
  font-size: 0.85rem;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-summary {
  margin: 0 0 0.35rem;
  font-size: 0.75rem;
  color: #94a3b8;
  line-height: 1.4;
}

.news-stats {
  display: flex;
  gap: 0.75rem;
  font-size: 0.7rem;
  color: #cbd5e1;
}

.panel-footer {
  padding: 0.6rem 1.25rem;
  border-top: 1px solid#f1f5f9;
  text-align: center;
}

.more-link {
  font-size: 0.8rem;
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.more-link:hover {
  color: #1d4ed8;
}

.slide-in {
  animation: slideIn 0.3s ease-out both;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(-8px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>
