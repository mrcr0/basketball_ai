<template>
  <div class="plan-document" :class="{ full: full, compact: !full }">
    <div class="doc-header">
      <div class="doc-title-row">
        <span class="doc-icon">📄</span>
        <h2 class="doc-title">{{ plan.planName || 'AI训练计划' }}</h2>
      </div>
      <div class="doc-meta">
        <span class="doc-meta-item">
          <span class="meta-dot"></span>
          {{ plan.trainingCycle }}计划 · {{ plan.cycleCount }}周期
        </span>
        <span class="doc-meta-item" v-if="normalizedPlan.objective">
          <span class="meta-dot"></span>
          目标：{{ normalizedPlan.objective }}
        </span>
      </div>
    </div>

    <div class="doc-body">
      <template v-if="isParsed">
        <section class="doc-section" v-if="normalizedPlan.objective">
          <h3 class="section-heading">
            <span class="heading-icon">🎯</span>
            <span>训练目标</span>
          </h3>
          <div class="section-body" v-html="normalizedPlan.objective"></div>
        </section>

        <section class="doc-section" v-if="normalizedPlan.dailyPlan && normalizedPlan.dailyPlan.length">
          <h3 class="section-heading">
            <span class="heading-icon">📅</span>
            <span>每日计划</span>
          </h3>
          <div class="section-body">
            <div class="daily-item" v-for="(day, idx) in normalizedPlan.dailyPlan" :key="idx">
              <div class="daily-header">
                <span class="daily-badge">第{{ day.day || idx + 1 }}天</span>
                <span class="daily-focus" v-if="day.name">{{ day.name }}</span>
              </div>
              <div class="training-group" v-if="day.items && day.items.length">
                <div class="training-row" v-for="(item, ti) in day.items" :key="ti">
                  <div class="training-name">
                    <span class="training-num">{{ ti + 1 }}</span>
                    <strong>{{ item.name }}</strong>
                  </div>
                  <div class="training-meta">
                    <span v-if="item.sets" class="training-tag">🏋️ {{ item.sets }}组</span>
                    <span v-if="item.reps" class="training-tag">🔄 {{ item.reps }}次</span>
                    <span v-if="item.duration" class="training-tag">⏱️ {{ item.duration }}分钟</span>
                    <span v-if="item.rest" class="training-tag">💤 休息{{ item.rest }}</span>
                  </div>
                  <p class="training-point" v-if="item.points">💡 {{ item.points }}</p>
                </div>
              </div>
              <ul class="daily-tasks" v-if="day.tasks && day.tasks.length">
                <li v-for="(task, ti) in day.tasks" :key="ti">{{ task }}</li>
              </ul>
              <p class="daily-desc" v-if="day.description">{{ day.description }}</p>
            </div>
          </div>
        </section>

        <section class="doc-section" v-if="normalizedPlan.actions && normalizedPlan.actions.length">
          <h3 class="section-heading">
            <span class="heading-icon">🏀</span>
            <span>动作说明</span>
          </h3>
          <div class="section-body">
            <div class="action-item" v-for="(act, idx) in normalizedPlan.actions" :key="idx">
              <div class="action-name">
                <span class="action-num">{{ idx + 1 }}</span>
                <strong>{{ act.name }}</strong>
                <span class="action-sets" v-if="act.sets">{{ act.sets }}</span>
              </div>
              <p class="action-desc" v-if="act.description">{{ act.description }}</p>
              <ul class="action-points" v-if="act.keyPoints && act.keyPoints.length">
                <li v-for="(kp, ki) in act.keyPoints" :key="ki">{{ kp }}</li>
              </ul>
            </div>
          </div>
        </section>

        <section class="doc-section warning-section" v-if="normalizedPlan.notes && normalizedPlan.notes.length">
          <h3 class="section-heading">
            <span class="heading-icon">⚠️</span>
            <span>注意事项</span>
          </h3>
          <div class="section-body">
            <ul class="notes-list">
              <li v-for="(note, idx) in normalizedPlan.notes" :key="idx">{{ note }}</li>
            </ul>
          </div>
        </section>
      </template>

      <div v-else class="raw-content">
        <pre>{{ plan.aiContent }}</pre>
      </div>
    </div>

    <div class="doc-footer" v-if="full">
      <span class="footer-text">AI篮球训练平台 · 科学训练 智能提升</span>
      <span class="footer-text" v-if="plan.createdAt">生成日期：{{ plan.createdAt }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  plan: { type: Object, required: true },
  full: { type: Boolean, default: false }
})

const parsedPlan = computed(() => {
  const content = props.plan.aiContent || ''
  try {
    let json
    if (typeof content === 'string' && (content.startsWith('{') || content.startsWith('['))) {
      json = JSON.parse(content)
    } else if (typeof content === 'object') {
      json = content
    }
    return json || null
  } catch {
    return null
  }
})

const normalizedPlan = computed(() => {
  const p = parsedPlan.value
  if (!p) return {}
  if (p.dailyPlan || p.objective || p.actions || p.notes) {
    return p
  }
  if (p.days && Array.isArray(p.days)) {
    return {
      objective: p.specialFocus ? '专项侧重：' + p.specialFocus : undefined,
      dailyPlan: p.days.map(function(d) { return d }),
      notes: p.specialFocus ? [
        '请根据个人身体状况调整训练强度',
        '训练前后做好充分热身和拉伸',
        '保证每天充足水分摄入（3-4升）',
        '如有不适请立即停止训练'
      ] : undefined
    }
  }
  return p
})

const isParsed = computed(() => {
  const p = normalizedPlan.value
  if (!p) return false
  return !!(p.objective || (p.dailyPlan && p.dailyPlan.length) || (p.actions && p.actions.length) || (p.notes && p.notes.length))
})
</script>

<style scoped>
.plan-document {
  background: linear-gradient(135deg, #fafbfc 0%, #f8f9fb 50%, #fff 100%);
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.plan-document.compact {
  font-size: 0.8rem;
}

.plan-document.compact .section-body {
  display: none;
}

.plan-document.compact .raw-content {
  display: none;
}

.plan-document.full {
  max-height: 70vh;
  overflow-y: auto;
}

.doc-header {
  padding: 1rem 1.25rem;
  background: linear-gradient(135deg, #7c3aed 0%, #6366f1 100%);
  color: #fff;
}

.doc-title-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.doc-icon {
  font-size: 1.25rem;
}

.doc-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
}

.doc-meta {
  display: flex;
  gap: 1rem;
  margin-top: 0.4rem;
  flex-wrap: wrap;
}

.doc-meta-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.7rem;
  opacity: 0.8;
}

.meta-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
}

.doc-body {
  padding: 1rem 1.25rem;
}

.doc-section {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f1f5f9;
}

.doc-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-heading {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin: 0 0 0.6rem;
  font-size: 0.9rem;
  font-weight: 700;
  color: #334155;
}

.heading-icon {
  font-size: 1rem;
}

.section-body {
  font-size: 0.83rem;
  color: #475569;
  line-height: 1.7;
}

.daily-item {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
}

.daily-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.4rem;
}

.daily-badge {
  background: #7c3aed;
  color: #fff;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
}

.daily-focus {
  font-size: 0.75rem;
  color: #7c3aed;
  font-weight: 600;
}

.daily-desc {
  margin: 0.3rem 0 0;
  font-size: 0.75rem;
  color: #94a3b8;
}

.daily-tasks {
  margin: 0.3rem 0 0;
  padding-left: 1.25rem;
  font-size: 0.78rem;
  color: #475569;
}

.daily-tasks li {
  margin-bottom: 0.15rem;
}

.training-group {
  margin-top: 0.35rem;
}

.training-row {
  background: #f1f5f9;
  border-radius: 8px;
  padding: 0.6rem 0.75rem;
  margin-bottom: 0.4rem;
}

.training-name {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.83rem;
  margin-bottom: 0.3rem;
}

.training-num {
  width: 18px;
  height: 18px;
  background: #6366f1;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 700;
  flex-shrink: 0;
}

.training-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
  margin-bottom: 0.25rem;
}

.training-tag {
  font-size: 0.68rem;
  background: #e0e7ff;
  color: #4338ca;
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  font-weight: 500;
}

.training-point {
  margin: 0.25rem 0 0;
  font-size: 0.72rem;
  color: #64748b;
  line-height: 1.5;
}

.action-item {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
}

.action-name {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.3rem;
  font-size: 0.83rem;
}

.action-num {
  width: 20px;
  height: 20px;
  background: #f97316;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 700;
  flex-shrink: 0;
}

.action-sets {
  background: #fef3c7;
  color: #d97706;
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  font-size: 0.7rem;
}

.action-desc {
  margin: 0.2rem 0 0.3rem;
  font-size: 0.75rem;
  color: #64748b;
}

.action-points {
  margin: 0;
  padding-left: 1.25rem;
  font-size: 0.73rem;
  color: #475569;
}

.action-points li {
  margin-bottom: 0.1rem;
}

.warning-section {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  margin-bottom: 0;
}

.notes-list {
  margin: 0;
  padding-left: 1.25rem;
  font-size: 0.78rem;
  color: #92400e;
}

.notes-list li {
  margin-bottom: 0.2rem;
}

.raw-content {
  background: #f8fafc;
  border-radius: 8px;
  padding: 0.75rem;
}

.raw-content pre {
  margin: 0;
  white-space: pre-wrap;
  font-size: 0.75rem;
  color: #64748b;
  font-family: 'SF Mono', 'Fira Code', monospace;
}

.doc-footer {
  padding: 0.6rem 1.25rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.footer-text {
  font-size: 0.65rem;
  color: #cbd5e1;
}
</style>
