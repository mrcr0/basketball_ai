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
            <span>知识体系</span>
          </span>
          <h1 class="banner-title">📖 新手入门与篮球知识库</h1>
          <p class="banner-desc">从新手入门到专业知识，系统全面的篮球知识体系，涵盖入门、规则、战术、技术和训练五大核心模块</p>
        </div>
      </div>
    </div>

    <div class="container">
      <!-- Category Tabs -->
      <div class="category-bar">
        <div class="category-pills">
          <button
            v-for="cat in categories"
            :key="cat.value"
            :class="['category-pill', { active: activeCategory === cat.value }]"
            @click="activeCategory = cat.value"
          >
            <span class="pill-icon">{{ cat.icon }}</span>
            <span>{{ cat.label }}</span>
            <span v-if="cat.value !== '新手入门'" class="pill-count">{{ getCategoryCount(cat.value) }}</span>
          </button>
        </div>
      </div>

      <!-- Sub Category -->
      <div v-if="subCategories.length > 0 && activeCategory !== '新手入门'" class="sub-bar">
        <button
          v-for="sub in subCategories"
          :key="sub"
          :class="['sub-pill', { active: activeSub === sub }]"
          @click="activeSub = activeSub === sub ? '' : sub"
        >{{ sub }}</button>
      </div>

      <!-- 新手入门内容 -->
      <div v-if="activeCategory === '新手入门'" class="beginner-content">
        <div class="content-wrapper">
          <section class="section">
            <div class="section-header">
              <span class="section-icon">📖</span>
              <h2>一、篮球基本构成</h2>
            </div>

            <div class="knowledge-grid">
              <div class="knowledge-card">
                <div class="card-icon">🏟️</div>
                <h3>球场规格</h3>
                <p>标准篮球场长28米、宽15米。场地上有三分线、罚球线、禁区（三秒区）等标记。篮筐距离地面3.05米。</p>
              </div>
              <div class="knowledge-card">
                <div class="card-icon">👥</div>
                <h3>上场人数</h3>
                <p>每队上场<b>5人</b>，分别担任控球后卫(PG)、得分后卫(SG)、小前锋(SF)、大前锋(PF)、中锋(C)。每队最多可有12名球员。</p>
              </div>
              <div class="knowledge-card">
                <div class="card-icon">🏀</div>
                <h3>比赛用球</h3>
                <p>男子比赛使用7号球（周长75-78cm），女子比赛使用6号球（周长72-74cm）。球的气压要适中，落地反弹高度到腰部。</p>
              </div>
              <div class="knowledge-card">
                <div class="card-icon">⏱️</div>
                <h3>比赛时间</h3>
                <p>NBA：4节×12分钟，加时5分钟。<br>FIBA（国际）：4节×10分钟，加时5分钟。比赛中死球时停表。</p>
              </div>
            </div>

            <div class="positions-section">
              <h3 class="subsection-title">球员位置详解（1-5号位）</h3>
              <div class="positions-grid">
                <div class="position-card" v-for="pos in positions" :key="pos.number">
                  <div class="pos-header">
                    <span class="pos-number">{{ pos.number }}</span>
                    <div>
                      <h4>{{ pos.name }}</h4>
                      <span class="pos-en">{{ pos.en }}</span>
                    </div>
                  </div>
                  <p class="pos-role">{{ pos.role }}</p>
                  <p class="pos-rep">
                    <b>代表人物：</b>{{ pos.representatives }}
                  </p>
                </div>
              </div>
            </div>
          </section>

          <section class="section">
            <div class="section-header">
              <span class="section-icon">📋</span>
              <h2>二、基本规则</h2>
            </div>

            <div class="rules-grid">
              <div class="rule-card">
                <div class="rule-num">01</div>
                <h3>得分规则</h3>
                <ul>
                  <li>罚球：每球得 <b>1分</b></li>
                  <li>三分线内投篮：每球得 <b>2分</b></li>
                  <li>三分线外投篮：每球得 <b>3分</b></li>
                </ul>
              </div>
              <div class="rule-card">
                <div class="rule-num">02</div>
                <h3>进攻时间</h3>
                <ul>
                  <li>每次进攻必须在 <b>24秒</b> 内完成投篮</li>
                  <li>进攻方抢到前场篮板后，进攻时间重置为 <b>14秒</b></li>
                  <li>球必须触及篮筐，否则24秒违例</li>
                </ul>
              </div>
              <div class="rule-card">
                <div class="rule-num">03</div>
                <h3>犯规规则</h3>
                <ul>
                  <li>NBA：个人累计 <b>6次犯规</b> 被罚下场</li>
                  <li>FIBA：个人累计 <b>5次犯规</b> 被罚下场</li>
                  <li>单节团队犯规满4次后，对方罚球</li>
                </ul>
              </div>
              <div class="rule-card">
                <div class="rule-num">04</div>
                <h3>常见违例</h3>
                <ul>
                  <li><b>走步：</b>持球移动超过两步</li>
                  <li><b>二次运球：</b>双手持球后再运球</li>
                  <li><b>回场：</b>球回后场</li>
                  <li><b>8秒：</b>8秒内未过半场</li>
                  <li><b>3秒：</b>在禁区停留超过3秒</li>
                </ul>
              </div>
            </div>
          </section>

          <section class="section">
            <div class="section-header">
              <span class="section-icon">🏃</span>
              <h2>三、基础知识</h2>
            </div>

            <div class="basics-tabs">
              <div class="tab-card" v-for="tab in basics" :key="tab.id">
                <div class="tab-header">
                  <span class="tab-icon">{{ tab.icon }}</span>
                  <h3>{{ tab.title }}</h3>
                </div>
                <p class="tab-desc">{{ tab.desc }}</p>
                <ul class="tab-points">
                  <li v-for="point in tab.points" :key="point">{{ point }}</li>
                </ul>
              </div>
            </div>
          </section>

          <section class="section cta-section">
            <div class="cta-card">
              <div class="cta-content">
                <h2>准备好了吗？</h2>
                <p>掌握基础知识后，去看看专业的篮球动作库，或让AI为你制定个性化训练计划！</p>
                <div class="cta-buttons">
                  <router-link to="/actions" class="cta-btn primary">🎯 浏览动作库</router-link>
                  <router-link to="/tactics" class="cta-btn secondary">📚 学习战术</router-link>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>

      <!-- Content List -->
      <div v-else-if="filteredKnowledge.length > 0" class="knowledge-grid">
        <div
          v-for="item in filteredKnowledge"
          :key="item.id"
          class="knowledge-card"
          @click="viewDetail(item)"
        >
          <div class="card-category">
            <span :class="'cat-tag cat-' + item.category">{{ getCategoryLabel(item.category) }}</span>
            <span v-if="item.difficulty" class="diff-tag">{{ item.difficulty }}</span>
          </div>
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-preview">{{ truncateContent(item.content, 100) }}</p>
          <div class="card-footer">
            <span class="footer-item">
              <span>👁️</span> {{ item.viewCount || 0 }}
            </span>
            <span class="footer-item">
              <span>📅</span> {{ formatDate(item.createdAt) }}
            </span>
          </div>
          <div class="card-hint">点击查看详情 →</div>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">📭</div>
        <h3>暂无内容</h3>
        <p>该分类下暂无知识条目，请尝试切换分类</p>
      </div>

      <!-- Detail Dialog -->
      <el-dialog
        v-model="detailVisible"
        :title="currentItem?.title"
        width="800px"
        class="knowledge-dialog"
        destroy-on-close
      >
        <div v-if="currentItem" class="detail-content">
          <div class="detail-meta">
            <span :class="'cat-tag cat-' + currentItem.category">{{ getCategoryLabel(currentItem.category) }}</span>
            <span v-if="currentItem.subCategory" class="meta-tag">{{ currentItem.subCategory }}</span>
            <span v-if="currentItem.difficulty" class="diff-tag">{{ currentItem.difficulty }}</span>
            <span class="meta-tag">👁️ {{ currentItem.viewCount }}</span>
          </div>
          <div class="detail-body" v-html="formatContent(currentItem.content)"></div>
          <div v-if="currentItem.tags" class="detail-tags">
            <span v-for="tag in (currentItem.tags || '').split(',')" :key="tag" class="tag-item">{{ tag.trim() }}</span>
          </div>
        </div>
      </el-dialog>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '../components/AppLayout.vue'
import { getKnowledge } from '../api/knowledge'
import { ElMessage } from 'element-plus'

const categories = [
  { value: '新手入门', label: '新手入门', icon: '📖' },
  { value: '篮球规则', label: '篮球规则', icon: '📋' },
  { value: '战术理论', label: '战术理论', icon: '♟️' },
  { value: '技术要领', label: '技术要领', icon: '🏀' },
  { value: '训练方法', label: '训练方法', icon: '💪' }
]

const positions = [
  {
    number: 1,
    name: '控球后卫',
    en: 'Point Guard (PG)',
    role: '球队的大脑和组织核心，负责运球过半场、分配球权、发起战术。需要出色的控球、传球和球场视野。',
    representatives: '库里、保罗、东契奇'
  },
  {
    number: 2,
    name: '得分后卫',
    en: 'Shooting Guard (SG)',
    role: '外线主攻手，擅长三分、突破和无球跑位。需要精准的投篮和摆脱防守的能力，同时兼顾防守。',
    representatives: '乔丹、科比、汤普森'
  },
  {
    number: 3,
    name: '小前锋',
    en: 'Small Forward (SF)',
    role: '全能战士，得分、篮板、防守兼备。是场上技术最全面的位置，需要适应多种战术体系。',
    representatives: '詹姆斯、杜兰特、伦纳德'
  },
  {
    number: 4,
    name: '大前锋',
    en: 'Power Forward (PF)',
    role: '内线蓝领兼辅助得分手，负责卡位抢板、内线防守和中远距离投射。现代大前锋需要更多外线能力。',
    representatives: '邓肯、字母哥、格林'
  },
  {
    number: 5,
    name: '中锋',
    en: 'Center (C)',
    role: '禁区的守护神，保护篮筐、抢篮板和内线终结。现代中锋还需具备三分投射与高位策应能力。',
    representatives: '奥尼尔、约基奇、恩比德'
  }
]

const basics = [
  {
    id: 'dribbling',
    icon: '🏐',
    title: '运球（Dribbling）',
    desc: '运球是篮球运动的基础，好的运球能让你在比赛中自由移动和创造机会。',
    points: [
      '用手指而非手掌触球，保持对球的控制',
      '膝盖微屈，重心降低，运球高度不过腰',
      '练习时保持抬头，不要盯着球看',
      '左右手都要练习，比赛中才不会受限',
      '从原地运球开始，逐步增加移动和变向'
    ]
  },
  {
    id: 'passing',
    icon: '🤝',
    title: '传球（Passing）',
    desc: '传球是团队配合的核心，好的传球比投篮更让防守头疼。',
    points: [
      '双手胸前传球是最基本的方式，从胸前推出',
      '传球要有力和准确，避免软绵绵的传球被抢断',
      '传到队友处于空位的一侧，远离防守人',
      '击地传球适合在防守人举起手臂时使用',
      '传球前先看清队友位置，不要盲目传球'
    ]
  },
  {
    id: 'shooting',
    icon: '🎯',
    title: '投篮（Shooting）',
    desc: '投篮是得分的手段，标准的投篮姿势是命中率的基础。',
    points: [
      '双脚与肩同宽，身体正对篮筐',
      '投篮手托在球的下方，辅助手在球的侧面',
      '蹬地发力，力量从腿部传递到手臂和手腕',
      '出手时手腕柔和下压，让球有后旋',
      '瞄准篮筐前沿，每天坚持练习500次以上'
    ]
  },
  {
    id: 'defense',
    icon: '🛡️',
    title: '防守（Defense）',
    desc: '防守赢得冠军！好的防守能让球队立于不败之地。',
    points: [
      '保持防守姿势：屈膝、降低重心、手臂张开',
      '防守时用滑步移动，不要交叉双脚',
      '始终保持眼睛同时看到球和自己防守的人',
      '不要轻易起跳封盖，保持站位更重要',
      '积极沟通，换防和协防需要大声喊出来'
    ]
  },
  {
    id: 'footwork',
    icon: '👟',
    title: '脚步（Footwork）',
    desc: '篮球场上的一切动作都离不开脚步，好的脚步让你更快、更稳。',
    points: [
      '练习前后左右四个方向的滑步移动',
      '急停技术：跳步急停和跨步急停',
      '转身：以一只脚为轴心，另一只脚移动',
      '进攻脚步：三步上篮节奏、后撤步',
      '跳绳是训练脚步灵活性和协调性的最好辅助练习'
    ]
  },
  {
    id: 'rules',
    icon: '📏',
    title: '比赛常识',
    desc: '了解基本规则才能在比赛中不犯规、不掉坑。',
    points: [
      '篮球比赛由2队各5人进行，通过投篮得分',
      '得分多的队获胜，平局时进行加时赛',
      '犯规后对方发球或罚球，严重犯规有额外处罚',
      '出界：球或持球人触及界线或界外即为出界',
      '比赛由裁判管理，尊重裁判的判罚是基本素养'
    ]
  }
]

const activeCategory = ref('新手入门')
const activeSub = ref('')
const allKnowledge = ref([])
const detailVisible = ref(false)
const currentItem = ref(null)

const subCategories = computed(() => {
  const cats = new Set()
  allKnowledge.value
    .filter(k => k.category === activeCategory.value)
    .forEach(k => { if (k.subCategory) cats.add(k.subCategory) })
  return [...cats]
})

const filteredKnowledge = computed(() => {
  let list = allKnowledge.value.filter(k => k.category === activeCategory.value)
  if (activeSub.value) {
    list = list.filter(k => k.subCategory === activeSub.value)
  }
  return list
})

const getCategoryCount = (cat) => {
  return allKnowledge.value.filter(k => k.category === cat).length
}

const getCategoryLabel = (cat) => {
  const map = { '新手入门': '入门', '篮球规则': '规则', '战术理论': '理论', '技术要领': '技术', '训练方法': '训练' }
  return map[cat] || cat
}

const fetchData = async () => {
  try {
    const res = await getKnowledge()
    allKnowledge.value = (res.data.data || res.data || [])
  } catch (e) {
    ElMessage.error('加载知识库失败')
  }
}

const truncateContent = (content, maxLen) => {
  if (!content) return ''
  const plain = content.replace(/【.*?】/g, '').replace(/\n/g, ' ').trim()
  return plain.length > maxLen ? plain.substring(0, maxLen) + '...' : plain
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.substring(0, 10)
}

const formatContent = (content) => {
  if (!content) return ''
  return content
    .replace(/\n/g, '<br>')
    .replace(/【(.*?)】/g, '<strong class="section-title">【$1】</strong>')
    .replace(/· /g, '&nbsp;&nbsp;· ')
}

const viewDetail = (item) => {
  currentItem.value = item
  detailVisible.value = true
}

onMounted(fetchData)
</script>

<style scoped>
/* Banner */
.page-banner {
  position: relative;
  padding: 60px 0 40px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
}
.banner-bg { position: absolute; inset: 0; }
.banner-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}
.banner-orb-1 {
  width: 400px; height: 400px;
  background: #667eea;
  top: -100px; right: -100px;
}
.banner-orb-2 {
  width: 300px; height: 300px;
  background: #764ba2;
  bottom: -100px; left: -50px;
}
.container {
  max-width: 1200px;
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
  background: #48bb78;
  border-radius: 50%;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
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

/* Category pills */
.category-bar {
  margin: 24px 0 16px;
}
.category-pills {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.category-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  font-size: 15px;
  color: #4a5568;
  transition: all 0.2s;
}
.category-pill:hover {
  border-color: #667eea;
  color: #667eea;
}
.category-pill.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-color: transparent;
}
.pill-icon { font-size: 18px; }
.pill-count {
  background: rgba(0,0,0,0.1);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}
.category-pill.active .pill-count {
  background: rgba(255,255,255,0.2);
}

/* Sub category */
.sub-bar {
  display: flex;
  gap: 8px;
  margin: 0 0 20px;
  flex-wrap: wrap;
}
.sub-pill {
  padding: 6px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: #f7fafc;
  cursor: pointer;
  font-size: 13px;
  color: #718096;
  transition: all 0.2s;
}
.sub-pill:hover { border-color: #667eea; color: #667eea; }
.sub-pill.active {
  background: #667eea;
  color: #fff;
  border-color: #667eea;
}

/* Grid */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}
.knowledge-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 24px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.25s;
  position: relative;
  overflow: hidden;
}
.knowledge-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.12);
  border-color: #667eea;
}
.card-category {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}
.cat-tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 10px;
  font-weight: 600;
}
.cat-新手入门 { background: #fff7ed; color: #ea580c; }
.cat-篮球规则 { background: #ebf8ff; color: #3182ce; }
.cat-战术理论 { background: #faf5ff; color: #805ad5; }
.cat-技术要领 { background: #f0fff4; color: #38a169; }
.cat-训练方法 { background: #fff5f5; color: #e53e3e; }
.diff-tag {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 10px;
  background: #edf2f7;
  color: #718096;
}
.card-title {
  font-size: 17px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 8px;
  line-height: 1.4;
}
.card-preview {
  font-size: 13px;
  color: #718096;
  line-height: 1.6;
  margin: 0;
}
.card-footer {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
.footer-item {
  font-size: 12px;
  color: #a0aec0;
  display: flex;
  align-items: center;
  gap: 4px;
}
.card-hint {
  position: absolute;
  right: 24px;
  bottom: 20px;
  font-size: 12px;
  color: #667eea;
  opacity: 0;
  transition: opacity 0.2s;
}
.knowledge-card:hover .card-hint { opacity: 1; }

/* Dialog */
.detail-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}
.meta-tag {
  font-size: 12px;
  padding: 3px 10px;
  background: #edf2f7;
  border-radius: 10px;
  color: #718096;
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

/* Empty */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #a0aec0;
}
.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-state h3 { margin: 0 0 8px; color: #4a5568; }
.empty-state p { margin: 0; font-size: 14px; }

/* 新手入门内容样式 */
.beginner-content .content-wrapper {
  padding: 2.5rem 0 3rem;
}
.beginner-content .section {
  margin-bottom: 3rem;
}
.beginner-content .section:last-child {
  margin-bottom: 0;
}
.beginner-content .section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}
.beginner-content .section-icon {
  font-size: 1.75rem;
}
.beginner-content .section-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}
.beginner-content .subsection-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #334155;
  margin: 2rem 0 1.25rem;
}
.beginner-content .knowledge-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}
.beginner-content .knowledge-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  cursor: default;
}
.beginner-content .knowledge-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  border-color: #cbd5e1;
}
.beginner-content .card-icon {
  font-size: 2rem;
  margin-bottom: 0.75rem;
}
.beginner-content .knowledge-card h3 {
  margin: 0 0 0.5rem;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
}
.beginner-content .knowledge-card p {
  margin: 0;
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.7;
}
.beginner-content .positions-section {
  margin-top: 1.5rem;
}
.beginner-content .positions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 1rem;
}
.beginner-content .position-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  border-left: 4px solid #f97316;
  transition: all 0.3s ease;
}
.beginner-content .position-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  border-left-color: #f97316;
}
.beginner-content .pos-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}
.beginner-content .pos-number {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 800;
  flex-shrink: 0;
}
.beginner-content .pos-header h4 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
}
.beginner-content .pos-en {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
}
.beginner-content .pos-role {
  margin: 0 0 0.5rem;
  font-size: 0.875rem;
  color: #475569;
  line-height: 1.6;
}
.beginner-content .pos-rep {
  margin: 0;
  font-size: 0.8rem;
  color: #94a3b8;
}
.beginner-content .pos-rep b {
  color: #64748b;
}
.beginner-content .rules-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}
.beginner-content .rule-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  position: relative;
  overflow: hidden;
}
.beginner-content .rule-num {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 2rem;
  font-weight: 800;
  color: #f1f5f9;
  line-height: 1;
}
.beginner-content .rule-card h3 {
  margin: 0 0 0.75rem;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
  position: relative;
}
.beginner-content .rule-card ul {
  margin: 0;
  padding: 0;
  list-style: none;
}
.beginner-content .rule-card li {
  padding: 0.3rem 0;
  font-size: 0.875rem;
  color: #475569;
  line-height: 1.6;
}
.beginner-content .rule-card li::before {
  content: '•';
  color: #f97316;
  font-weight: bold;
  margin-right: 0.5rem;
}
.beginner-content .basics-tabs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}
.beginner-content .tab-card {
  background: #fff;
  border-radius: 14px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}
.beginner-content .tab-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.beginner-content .tab-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}
.beginner-content .tab-icon {
  font-size: 1.75rem;
}
.beginner-content .tab-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
}
.beginner-content .tab-desc {
  margin: 0 0 0.75rem;
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.6;
}
.beginner-content .tab-points {
  margin: 0;
  padding: 0;
  list-style: none;
}
.beginner-content .tab-points li {
  padding: 0.25rem 0;
  font-size: 0.85rem;
  color: #475569;
  line-height: 1.6;
}
.beginner-content .tab-points li::before {
  content: '✅';
  margin-right: 0.5rem;
  font-size: 0.75rem;
}
.beginner-content .cta-section {
  margin-top: 1rem;
}
.beginner-content .cta-card {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-radius: 20px;
  padding: 3rem 2.5rem;
  text-align: center;
  color: #fff;
}
.beginner-content .cta-content h2 {
  margin: 0 0 0.75rem;
  font-size: 1.75rem;
  font-weight: 700;
  color: #fff;
}
.beginner-content .cta-content p {
  margin: 0 0 1.5rem;
  font-size: 1rem;
  opacity: 0.9;
  line-height: 1.6;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}
.beginner-content .cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}
.beginner-content .cta-btn {
  padding: 0.75rem 2rem;
  border-radius: 999px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}
.beginner-content .cta-btn.primary {
  background: #fff;
  color: #ea580c;
}
.beginner-content .cta-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.2);
}
.beginner-content .cta-btn.secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.4);
}
.beginner-content .cta-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .knowledge-grid,
  .beginner-content .knowledge-grid,
  .beginner-content .rules-grid,
  .beginner-content .basics-tabs {
    grid-template-columns: 1fr;
  }
  .beginner-content .positions-grid {
    grid-template-columns: 1fr;
  }
  .banner-title { font-size: 26px; }
  .beginner-content .cta-card {
    padding: 2rem 1.5rem;
  }
}
</style>
