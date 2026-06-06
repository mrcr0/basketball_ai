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
            <span>新手入门</span>
          </span>
          <h1 class="banner-title">🏀 篮球新手入门指南</h1>
          <p class="banner-desc">从零开始，系统学习篮球的基本构成、核心规则和基础知识</p>
        </div>
      </div>
    </div>

    <div class="container">
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
                <li>球必须碰触篮筐，否则24秒违例</li>
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
  </AppLayout>
</template>

<script setup>
import AppLayout from '../components/AppLayout.vue'

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
      '保持防守姿态：屈膝、降低重心、手臂张开',
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
</script>

<style scoped>
.page-banner {
  position: relative;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
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
  background: #e94560;
  top: -50%;
  right: -10%;
  animation: orbFloat 8s ease-in-out infinite;
}

.banner-orb-2 {
  width: 200px;
  height: 200px;
  background: #0f3460;
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
  background: rgba(233, 69, 96, 0.12);
  border: 1px solid rgba(233, 69, 96, 0.25);
  border-radius: 999px;
  color: #e94560;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 1rem;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #e94560;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.banner-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #fff;
  margin: 0 0 0.75rem;
  letter-spacing: -0.02em;
}

.banner-desc {
  color: #94a3b8;
  font-size: 1.125rem;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.7;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-wrapper {
  padding: 2.5rem 0 3rem;
}

.section {
  margin-bottom: 3rem;
}

.section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.section-icon {
  font-size: 1.75rem;
}

.section-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}

.subsection-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #334155;
  margin: 2rem 0 1.25rem;
}

.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

.knowledge-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.knowledge-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  border-color: #cbd5e1;
}

.card-icon {
  font-size: 2rem;
  margin-bottom: 0.75rem;
}

.knowledge-card h3 {
  margin: 0 0 0.5rem;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
}

.knowledge-card p {
  margin: 0;
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.7;
}

.positions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 1rem;
}

.position-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  border-left: 4px solid #f97316;
  transition: all 0.3s ease;
}

.position-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  border-left-color: #f97316;
}

.pos-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.pos-number {
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

.pos-header h4 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
}

.pos-en {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
}

.pos-role {
  margin: 0 0 0.5rem;
  font-size: 0.875rem;
  color: #475569;
  line-height: 1.6;
}

.pos-rep {
  margin: 0;
  font-size: 0.8rem;
  color: #94a3b8;
}

.pos-rep b {
  color: #64748b;
}

.rules-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

.rule-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  position: relative;
  overflow: hidden;
}

.rule-num {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 2rem;
  font-weight: 800;
  color: #f1f5f9;
  line-height: 1;
}

.rule-card h3 {
  margin: 0 0 0.75rem;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  position: relative;
}

.rule-card ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.rule-card li {
  padding: 0.3rem 0;
  font-size: 0.875rem;
  color: #475569;
  line-height: 1.6;
}

.rule-card li::before {
  content: '•';
  color: #f97316;
  font-weight: bold;
  margin-right: 0.5rem;
}

.basics-tabs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

.tab-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.tab-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.tab-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.tab-icon {
  font-size: 1.75rem;
}

.tab-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
}

.tab-desc {
  margin: 0 0 0.75rem;
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.6;
}

.tab-points {
  margin: 0;
  padding: 0;
  list-style: none;
}

.tab-points li {
  padding: 0.25rem 0;
  font-size: 0.85rem;
  color: #475569;
  line-height: 1.6;
}

.tab-points li::before {
  content: '✅';
  margin-right: 0.5rem;
  font-size: 0.75rem;
}

.cta-section {
  margin-top: 1rem;
}

.cta-card {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-radius: 20px;
  padding: 3rem 2.5rem;
  text-align: center;
  color: #fff;
}

.cta-content h2 {
  margin: 0 0 0.75rem;
  font-size: 1.75rem;
  font-weight: 700;
  color: #fff;
}

.cta-content p {
  margin: 0 0 1.5rem;
  font-size: 1rem;
  opacity: 0.9;
  line-height: 1.6;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-btn {
  padding: 0.75rem 2rem;
  border-radius: 999px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.cta-btn.primary {
  background: #fff;
  color: #ea580c;
}

.cta-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.cta-btn.secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.cta-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .knowledge-grid,
  .rules-grid,
  .basics-tabs {
    grid-template-columns: 1fr;
  }

  .positions-grid {
    grid-template-columns: 1fr;
  }

  .banner-title {
    font-size: 1.75rem;
  }

  .cta-card {
    padding: 2rem 1.5rem;
  }
}
</style>
