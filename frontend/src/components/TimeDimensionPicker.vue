<template>
  <div class="time-dimension-picker">
    <div class="dimension-tabs">
      <button
        v-for="dim in dimensions"
        :key="dim.value"
        :class="['dim-tab', { active: currentDimension === dim.value }]"
        @click="switchDimension(dim.value)"
      >
        <span class="dim-icon">{{ dim.icon }}</span>
        <span class="dim-label">{{ dim.label }}</span>
      </button>
    </div>

    <div class="dimension-content">
      <div v-if="currentDimension === 'year'" class="year-view">
        <div class="nav-row">
          <button class="nav-btn" @click="changeYear(-1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
          </button>
          <span class="current-label">{{ selectedYear }}年</span>
          <button class="nav-btn" @click="changeYear(1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
        </div>
        <div class="month-grid">
          <button
            v-for="m in months"
            :key="m.value"
            :class="['month-cell', { active: selectedMonth === m.value, current: isCurrentMonth(m.value) }]"
            @click="selectMonth(m.value)"
          >
            <span class="cell-label">{{ m.label }}</span>
            <span v-if="isCurrentMonth(m.value)" class="current-dot"></span>
          </button>
        </div>
      </div>

      <div v-if="currentDimension === 'month'" class="month-view">
        <div class="nav-row">
          <button class="nav-btn" @click="changeMonth(-1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
          </button>
          <span class="current-label">{{ selectedYear }}年{{ selectedMonth }}月</span>
          <button class="nav-btn" @click="changeMonth(1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
        </div>
        <div class="weekday-header">
          <span v-for="w in weekHeaders" :key="w" class="weekday">{{ w }}</span>
        </div>
        <div class="day-grid">
          <button
            v-for="(day, idx) in calendarDays"
            :key="idx"
            :class="['day-cell', {
              active: isSelectedDate(day),
              today: isToday(day),
              other: !day || day.isOtherMonth
            }]"
            :disabled="!day"
            @click="day && selectDate(day)"
          >
            <span v-if="day" class="day-num">{{ day.date }}</span>
          </button>
        </div>
      </div>

      <div v-if="currentDimension === 'week'" class="week-view">
        <div class="nav-row">
          <button class="nav-btn" @click="changeWeek(-1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
          </button>
          <span class="current-label">{{ weekRangeText }}</span>
          <button class="nav-btn" @click="changeWeek(1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
        </div>
        <div class="week-day-list">
          <button
            v-for="d in weekDays"
            :key="d.dayOfWeek"
            :class="['week-day-item', { active: isSelectedWeekDay(d), today: d.isToday }]"
            @click="selectWeekDay(d)"
          >
            <span class="day-name">{{ d.weekLabel }}</span>
            <span class="day-date">{{ d.date }}</span>
            <span class="day-month">{{ d.monthDisplay }}</span>
            <span v-if="d.isToday" class="today-badge">今天</span>
          </button>
        </div>
      </div>

      <div v-if="currentDimension === 'day'" class="day-view">
        <div class="nav-row">
          <button class="nav-btn" @click="changeDay(-1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
          </button>
          <span class="current-label">{{ dayDisplay }}</span>
          <button class="nav-btn" @click="changeDay(1)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </button>
        </div>
        <div class="day-detail">
          <div class="day-big">{{ selectedDay }}</div>
          <div class="day-info">
            <span>{{ selectedYear }}年{{ selectedMonth }}月{{ selectedDay }}日</span>
            <span>{{ getWeekdayLabel(dateRef) }}</span>
          </div>
        </div>
        <div class="hour-scroll">
          <button
            v-for="h in 24"
            :key="h"
            :class="['hour-cell', { active: selectedHour === h - 1 }]"
            @click="selectHour(h - 1)"
          >
            {{ String(h - 1).padStart(2, '0') }}:00
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Date, default: () => new Date() },
  defaultDimension: { type: String, default: 'month' }
})

const emit = defineEmits(['update:modelValue', 'dimension-change'])

const dimensions = [
  { value: 'year', label: '年', icon: '📅' },
  { value: 'month', label: '月', icon: '🗓️' },
  { value: 'week', label: '周', icon: '📆' },
  { value: 'day', label: '日', icon: '☀️' }
]

const months = [
  { value: 1, label: '1月' }, { value: 2, label: '2月' }, { value: 3, label: '3月' },
  { value: 4, label: '4月' }, { value: 5, label: '5月' }, { value: 6, label: '6月' },
  { value: 7, label: '7月' }, { value: 8, label: '8月' }, { value: 9, label: '9月' },
  { value: 10, label: '10月' }, { value: 11, label: '11月' }, { value: 12, label: '12月' }
]

const weekHeaders = ['日', '一', '二', '三', '四', '五', '六']

const currentDimension = ref(props.defaultDimension)
const dateRef = ref(new Date(props.modelValue))
const selectedYear = ref(dateRef.value.getFullYear())
const selectedMonth = ref(dateRef.value.getMonth() + 1)
const selectedDay = ref(dateRef.value.getDate())
const selectedHour = ref(dateRef.value.getHours())

watch(() => props.modelValue, (val) => {
  dateRef.value = new Date(val)
  selectedYear.value = dateRef.value.getFullYear()
  selectedMonth.value = dateRef.value.getMonth() + 1
  selectedDay.value = dateRef.value.getDate()
  selectedHour.value = dateRef.value.getHours()
})

const updateValue = () => {
  const d = new Date(selectedYear.value, selectedMonth.value - 1, selectedDay.value, selectedHour.value, 0, 0)
  dateRef.value = d
  emit('update:modelValue', d)
}

const switchDimension = (dim) => {
  currentDimension.value = dim
  emit('dimension-change', dim)
}

const changeYear = (delta) => {
  selectedYear.value += delta
  updateValue()
}

const changeMonth = (delta) => {
  let m = selectedMonth.value + delta
  if (m > 12) { selectedYear.value++; m = 1 }
  if (m < 1) { selectedYear.value--; m = 12 }
  selectedMonth.value = m
  updateValue()
}

const selectMonth = (m) => {
  selectedMonth.value = m
  updateValue()
}

const isCurrentMonth = (m) => {
  const now = new Date()
  return selectedYear.value === now.getFullYear() && m === now.getMonth() + 1
}

const calendarDays = computed(() => {
  const firstDay = new Date(selectedYear.value, selectedMonth.value - 1, 1)
  const lastDay = new Date(selectedYear.value, selectedMonth.value, 0)
  const startDow = firstDay.getDay()
  const totalDays = lastDay.getDate()
  const cells = []

  for (let i = 0; i < startDow; i++) {
    cells.push(null)
  }
  for (let d = 1; d <= totalDays; d++) {
    cells.push({ date: d, month: selectedMonth.value, year: selectedYear.value, isOtherMonth: false })
  }
  while (cells.length < 42) {
    cells.push({ date: cells.length - startDow - totalDays + 1, month: selectedMonth.value + 1, year: selectedYear.value, isOtherMonth: true })
  }
  return cells
})

const isSelectedDate = (day) => {
  return day && !day.isOtherMonth && day.date === selectedDay.value && day.month === selectedMonth.value
}

const isToday = (day) => {
  if (!day || day.isOtherMonth) return false
  const now = new Date()
  return day.date === now.getDate() && day.month === now.getMonth() + 1 && day.year === now.getFullYear()
}

const selectDate = (day) => {
  selectedDay.value = day.date
  updateValue()
}

const getWeekStart = () => {
  const now = new Date(dateRef.value)
  const day = now.getDay()
  const diff = now.getDate() - day
  return new Date(now.getFullYear(), now.getMonth(), diff)
}

const changeWeek = (delta) => {
  const ws = getWeekStart()
  ws.setDate(ws.getDate() + delta * 7)
  dateRef.value = ws
  selectedYear.value = ws.getFullYear()
  selectedMonth.value = ws.getMonth() + 1
  selectedDay.value = ws.getDate()
  updateValue()
}

const weekRangeText = computed(() => {
  const ws = getWeekStart()
  const we = new Date(ws)
  we.setDate(ws.getDate() + 6)
  return `${ws.getFullYear()}年${ws.getMonth() + 1}月${ws.getDate()}日 - ${we.getMonth() + 1}月${we.getDate()}日`
})

const weekDays = computed(() => {
  const ws = getWeekStart()
  const labels = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const today = new Date()
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(ws)
    d.setDate(ws.getDate() + i)
    return {
      date: d.getDate(),
      month: d.getMonth() + 1,
      year: d.getFullYear(),
      dayOfWeek: i,
      weekLabel: labels[i],
      monthDisplay: `${d.getMonth() + 1}月`,
      isToday: d.getDate() === today.getDate() && d.getMonth() === today.getMonth() && d.getFullYear() === today.getFullYear()
    }
  })
})

const isSelectedWeekDay = (d) => {
  return d.date === selectedDay.value && d.month === selectedMonth.value && d.year === selectedYear.value
}

const selectWeekDay = (d) => {
  const ws = getWeekStart()
  ws.setDate(ws.getDate() + d.dayOfWeek)
  dateRef.value = ws
  selectedYear.value = ws.getFullYear()
  selectedMonth.value = ws.getMonth() + 1
  selectedDay.value = ws.getDate()
  updateValue()
}

const changeDay = (delta) => {
  const d = new Date(dateRef.value)
  d.setDate(d.getDate() + delta)
  dateRef.value = d
  selectedYear.value = d.getFullYear()
  selectedMonth.value = d.getMonth() + 1
  selectedDay.value = d.getDate()
  updateValue()
}

const dayDisplay = computed(() => {
  return `${selectedYear.value}年${selectedMonth.value}月${selectedDay.value}日`
})

const selectHour = (h) => {
  selectedHour.value = h
  updateValue()
}

const getWeekdayLabel = (d) => {
  const arr = ['日', '一', '二', '三', '四', '五', '六']
  return `星期${arr[d.getDay() || 0]}`
}
</script>

<style scoped>
.time-dimension-picker {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.dimension-tabs {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.dim-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  padding: 0.75rem 0.5rem;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  font-size: 0.875rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.dim-tab:hover {
  color: #334155;
  background: rgba(241, 245, 249, 0.5);
}

.dim-tab.active {
  color: #f97316;
  border-bottom-color: #f97316;
  background: #fff7ed;
}

.dim-icon {
  font-size: 1rem;
}

.dimension-content {
  padding: 1.25rem;
}

.nav-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.nav-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
}

.nav-btn:hover {
  background: #f1f5f9;
  color: #334155;
}

.current-label {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  min-width: 140px;
  text-align: center;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
}

.month-cell {
  position: relative;
  padding: 0.65rem;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.875rem;
  font-weight: 500;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  text-align: center;
}

.month-cell:hover {
  border-color: #f97316;
  background: #fff7ed;
}

.month-cell.active {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}

.month-cell.current {
  border-color: #f97316;
}

.current-dot {
  position: absolute;
  top: 4px;
  right: 6px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f97316;
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 0.25rem;
}

.weekday {
  text-align: center;
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
  padding: 0.35rem 0;
}

.day-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.day-cell {
  aspect-ratio: 1;
  border-radius: 8px;
  border: none;
  background: transparent;
  font-size: 0.85rem;
  color: #475569;
  cursor: pointer;
  transition: all 0.15s;
  font-family: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.day-cell:hover:not(:disabled) {
  background: #f1f5f9;
}

.day-cell.active {
  background: #f97316 !important;
  color: #fff !important;
  font-weight: 600;
}

.day-cell.today:not(.active) {
  color: #f97316;
  font-weight: 600;
}

.day-cell.today:not(.active)::after {
  content: '';
  position: absolute;
  bottom: 3px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #f97316;
}

.day-cell.other {
  opacity: 0;
  pointer-events: none;
}

.day-cell:disabled {
  opacity: 0;
  pointer-events: none;
}

.week-day-list {
  display: flex;
  gap: 0.4rem;
}

.week-day-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  padding: 0.75rem 0.25rem;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  position: relative;
}

.week-day-item:hover {
  border-color: #f97316;
  background: #fff7ed;
}

.week-day-item.active {
  background: #f97316;
  border-color: #f97316;
}

.week-day-item.active .day-name,
.week-day-item.active .day-date,
.week-day-item.active .day-month {
  color: #fff;
}

.day-name {
  font-size: 0.75rem;
  color: #94a3b8;
}

.day-date {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
}

.day-month {
  font-size: 0.7rem;
  color: #94a3b8;
}

.today-badge {
  position: absolute;
  top: 4px;
  right: 6px;
  font-size: 0.6rem;
  color: #f97316;
  font-weight: 600;
}

.week-day-item.active .today-badge {
  color: #ffedd5;
}

.day-detail {
  text-align: center;
  margin-bottom: 1.25rem;
}

.day-big {
  font-size: 4rem;
  font-weight: 800;
  color: #f97316;
  line-height: 1;
  margin-bottom: 0.5rem;
}

.day-info {
  display: flex;
  gap: 1rem;
  justify-content: center;
  font-size: 0.875rem;
  color: #64748b;
}

.hour-scroll {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 0.35rem;
  max-height: 200px;
  overflow-y: auto;
}

.hour-cell {
  padding: 0.5rem 0.35rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.75rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  text-align: center;
}

.hour-cell:hover {
  border-color: #f97316;
  background: #fff7ed;
}

.hour-cell.active {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}
</style>
