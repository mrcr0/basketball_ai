<template>
  <AppLayout>
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-orb hero-orb-1"></div>
        <div class="hero-orb hero-orb-2"></div>
        <div class="hero-orb hero-orb-3"></div>
      </div>
      <div class="hero-content container">
        <div class="hero-badge slide-up">
          <span class="badge-dot"></span>
          <span>AI 驱动 · 智能训练</span>
        </div>
        <h1 class="hero-title slide-up" style="animation-delay: 0.1s">
          科学训练<span class="highlight">，</span>智能提升
        </h1>
        <p class="hero-desc slide-up" style="animation-delay: 0.2s">
          基于AI大模型的个性化篮球训练方案，助你快速提升球技
        </p>
        <div class="hero-actions slide-up" style="animation-delay: 0.3s">
          <router-link to="/plans" class="hero-btn hero-btn-primary">
            <span>🚀</span>
            <span>开始训练</span>
          </router-link>
          <router-link to="/actions" class="hero-btn hero-btn-secondary">
            <span>📖</span>
            <span>浏览动作库</span>
          </router-link>
        </div>
        <div class="hero-stats slide-up" style="animation-delay: 0.4s">
          <div class="hero-stat">
            <span class="hero-stat-value">10,000+</span>
            <span class="hero-stat-label">注册用户</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat">
            <span class="hero-stat-value">500+</span>
            <span class="hero-stat-label">训练计划</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat">
            <span class="hero-stat-value">100+</span>
            <span class="hero-stat-label">动作教程</span>
          </div>
          <div class="hero-stat-divider"></div>
          <div class="hero-stat">
            <span class="hero-stat-value">50+</span>
            <span class="hero-stat-label">战术课程</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section features">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">核心功能</span>
          <h2 class="section-title">全方位的训练解决方案</h2>
          <p class="section-desc">从AI训练计划生成到动作识别纠错，覆盖篮球训练全场景</p>
        </div>
        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in features" :key="index"
            :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="feature-icon-wrapper">
              <span class="feature-icon">{{ feature.icon }}</span>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.desc }}</p>
            <ul class="feature-list">
              <li v-for="item in feature.items" :key="item">{{ item }}</li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <section class="section workflow">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">训练流程</span>
          <h2 class="section-title">三步开启智能训练</h2>
          <p class="section-desc">简单快捷的操作流程，让你专注于训练本身</p>
        </div>
        <div class="workflow-steps">
          <div class="workflow-step" v-for="(step, index) in workflowSteps" :key="index">
            <div class="step-number">
              <span>{{ String(index + 1).padStart(2, '0') }}</span>
              <div class="step-line" v-if="index < workflowSteps.length - 1"></div>
            </div>
            <div class="step-content">
              <div class="step-icon">{{ step.icon }}</div>
              <h3>{{ step.title }}</h3>
              <p>{{ step.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section cta">
      <div class="container">
        <div class="cta-card">
          <div class="cta-bg-orb"></div>
          <h2>准备好开始你的篮球训练之旅了吗？</h2>
          <p>加入 10,000+ 篮球爱好者，用AI科技提升你的球技</p>
          <div class="cta-actions">
            <router-link to="/register" class="cta-btn cta-btn-primary">立即注册</router-link>
            <router-link to="/ai-qa" class="cta-btn cta-btn-secondary">咨询AI助手</router-link>
          </div>
        </div>
      </div>
    </section>
  </AppLayout>
</template>

<script setup>
import AppLayout from '../components/AppLayout.vue'

const features = [
  {
    icon: '🤖',
    title: 'AI训练计划',
    desc: '智能生成个性化训练方案，根据你的身体条件和技术水平量身定制',
    items: ['位置与水平分析', '薄弱项针对性训练', '周期化训练安排']
  },
  {
    icon: '🎯',
    title: '动作识别纠错',
    desc: '摄像头实时捕捉动作，AI精准分析并给出专业改进建议',
    items: ['实时动作捕捉', '姿态对比分析', '个性化纠错建议']
  },
  {
    icon: '📊',
    title: '数据可视化',
    desc: '训练数据全面统计分析，直观展示你的进步轨迹与成长曲线',
    items: ['训练数据追踪', '进步趋势图表', '能力雷达分析']
  },
  {
    icon: '📚',
    title: '战术知识库',
    desc: '丰富的战术教学内容与AI讲解，助你深入理解篮球智慧',
    items: ['战术视频教学', 'AI智能讲解', '实战模拟训练']
  }
]

const workflowSteps = [
  { icon: '📝', title: '填写信息', desc: '输入你的身体数据、技术水平和训练目标' },
  { icon: '🤖', title: 'AI生成计划', desc: 'AI根据你的信息自动生成个性化训练方案' },
  { icon: '🏀', title: '开始训练', desc: '按照计划进行训练，记录数据，持续进步' }
]
</script>

<style scoped>
/* ========== Hero Section ========== */
.hero {
  position: relative;
  min-height: calc(100vh - var(--header-height));
  display: flex;
  align-items: center;
  background: var(--gradient-dark);
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: orbFloat 8s ease-in-out infinite;
}

.hero-orb-1 {
  width: 500px;
  height: 500px;
  background: var(--color-accent-400);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.hero-orb-2 {
  width: 400px;
  height: 400px;
  background: var(--color-primary-500);
  bottom: -50px;
  left: -50px;
  animation-delay: -3s;
}

.hero-orb-3 {
  width: 300px;
  height: 300px;
  background: var(--color-warm-400);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -6s;
  opacity: 0.08;
}

@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: var(--space-20) var(--space-6);
  max-width: 800px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-4);
  background: rgba(74, 222, 128, 0.1);
  border: 1px solid rgba(74, 222, 128, 0.2);
  border-radius: var(--radius-full);
  margin-bottom: var(--space-6);
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: var(--color-accent-400);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

.hero-badge span {
  color: var(--color-accent-400);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.hero-title {
  color: #fff;
  font-size: var(--font-size-6xl);
  font-weight: var(--font-weight-extrabold);
  line-height: var(--line-height-tight);
  letter-spacing: var(--letter-spacing-tight);
  margin-bottom: var(--space-6);
}

.hero-title .highlight {
  color: var(--color-accent-400);
}

.hero-desc {
  color: var(--color-neutral-400);
  font-size: var(--font-size-xl);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--space-8);
  max-width: 560px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  margin-bottom: var(--space-12);
}

.hero-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-8);
  border-radius: var(--radius-full);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  transition: all var(--transition-bounce);
}

.hero-btn-primary {
  background: var(--gradient-accent);
  color: var(--color-dark-900);
}

.hero-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-accent);
}

.hero-btn-secondary {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  border: 1.5px solid rgba(255, 255, 255, 0.15);
}

.hero-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-8);
  padding: var(--space-6);
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(10px);
}

.hero-stat {
  text-align: center;
}

.hero-stat-value {
  display: block;
  color: #fff;
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-1);
}

.hero-stat-label {
  color: var(--color-neutral-400);
  font-size: var(--font-size-xs);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.hero-stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
}

/* ========== Section Common ========== */
.section {
  padding: var(--space-20) 0;
}

.section-header {
  text-align: center;
  margin-bottom: var(--space-12);
}

.section-tag {
  display: inline-block;
  padding: var(--space-1) var(--space-4);
  background: rgba(74, 222, 128, 0.08);
  color: var(--color-accent-600);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--radius-full);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: var(--space-4);
}

.section-title {
  color: var(--color-neutral-900);
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-4);
}

.section-desc {
  color: var(--color-neutral-500);
  font-size: var(--font-size-lg);
  max-width: 560px;
  margin: 0 auto;
}

/* ========== Features Section ========== */
.features {
  background: var(--color-neutral-50);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-6);
}

.feature-card {
  background: #fff;
  padding: var(--space-8);
  border-radius: var(--radius-2xl);
  border: 1px solid var(--color-neutral-200);
  transition: all var(--transition-base);
  animation: slideUp 0.5s ease both;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-accent-200);
}

.feature-icon-wrapper {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(74, 222, 128, 0.1), rgba(74, 222, 128, 0.05));
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-5);
}

.feature-icon {
  font-size: 1.75rem;
}

.feature-title {
  color: var(--color-neutral-900);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--space-3);
}

.feature-desc {
  color: var(--color-neutral-500);
  font-size: var(--font-size-sm);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--space-4);
}

.feature-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.feature-list li {
  color: var(--color-neutral-600);
  font-size: var(--font-size-xs);
  padding-left: var(--space-5);
  position: relative;
}

.feature-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background: var(--color-accent-400);
  border-radius: 50%;
}

/* ========== Workflow Section ========== */
.workflow {
  background: #fff;
}

.workflow-steps {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-8);
  max-width: 900px;
  margin: 0 auto;
}

.workflow-step {
  text-align: center;
  position: relative;
}

.step-number {
  position: relative;
  margin-bottom: var(--space-6);
}

.step-number span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: var(--gradient-accent);
  color: var(--color-dark-900);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  border-radius: 50%;
  position: relative;
  z-index: 1;
}

.step-line {
  position: absolute;
  top: 24px;
  left: calc(50% + 30px);
  width: calc(100% - 12px);
  height: 2px;
  background: var(--color-neutral-200);
}

.step-icon {
  font-size: 2rem;
  margin-bottom: var(--space-3);
}

.step-content h3 {
  color: var(--color-neutral-900);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--space-2);
}

.step-content p {
  color: var(--color-neutral-500);
  font-size: var(--font-size-sm);
}

/* ========== CTA Section ========== */
.cta {
  background: var(--color-neutral-50);
}

.cta-card {
  position: relative;
  background: var(--gradient-dark);
  border-radius: var(--radius-3xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  overflow: hidden;
}

.cta-bg-orb {
  position: absolute;
  width: 400px;
  height: 400px;
  background: var(--color-accent-400);
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.1;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.cta-card h2 {
  position: relative;
  color: #fff;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-4);
}

.cta-card p {
  position: relative;
  color: var(--color-neutral-400);
  font-size: var(--font-size-lg);
  margin-bottom: var(--space-8);
}

.cta-actions {
  position: relative;
  display: flex;
  gap: var(--space-4);
  justify-content: center;
}

.cta-btn {
  padding: var(--space-3) var(--space-8);
  border-radius: var(--radius-full);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  text-decoration: none;
  transition: all var(--transition-bounce);
}

.cta-btn-primary {
  background: var(--gradient-accent);
  color: var(--color-dark-900);
}

.cta-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-accent);
}

.cta-btn-secondary {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  border: 1.5px solid rgba(255, 255, 255, 0.15);
}

.cta-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: translateY(-2px);
}

/* ========== Responsive ========== */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero {
    min-height: auto;
  }

  .hero-title {
    font-size: var(--font-size-4xl);
  }

  .hero-desc {
    font-size: var(--font-size-md);
  }

  .hero-stats {
    flex-wrap: wrap;
    gap: var(--space-4);
  }

  .hero-stat-divider {
    display: none;
  }

  .hero-stat {
    flex: 1 1 40%;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .hero-btn {
    width: 100%;
    justify-content: center;
  }

  .section {
    padding: var(--space-12) 0;
  }

  .section-title {
    font-size: var(--font-size-2xl);
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .workflow-steps {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .step-line {
    display: none;
  }

  .cta-card {
    padding: var(--space-10) var(--space-6);
  }

  .cta-card h2 {
    font-size: var(--font-size-2xl);
  }

  .cta-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .hero-stats {
    gap: var(--space-3);
  }

  .hero-stat-value {
    font-size: var(--font-size-lg);
  }

  .feature-card {
    padding: var(--space-6);
  }
}
</style>