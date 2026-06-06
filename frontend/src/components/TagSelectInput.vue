<template>
  <div class="tag-select-input">
    <div class="tag-options">
      <button
        v-for="option in options"
        :key="option.value"
        type="button"
        :class="['tag-chip', { active: isSelected(option.value) }]"
        @click="toggleTag(option.value)"
      >
        <span class="tag-icon" v-if="option.icon">{{ option.icon }}</span>
        <span>{{ option.label }}</span>
        <span class="tag-check" v-if="isSelected(option.value)">✓</span>
      </button>
    </div>
    <div class="custom-input-wrap">
      <div class="custom-tags" v-if="customTags.length > 0">
        <span
          v-for="(tag, idx) in customTags"
          :key="idx"
          class="custom-tag"
        >
          {{ tag }}
          <button type="button" class="custom-tag-remove" @click="removeCustomTag(idx)">×</button>
        </span>
      </div>
      <div class="input-row">
        <input
          ref="inputRef"
          v-model="inputValue"
          type="text"
          :placeholder="placeholder"
          class="custom-input"
          @keydown.enter.prevent="addCustomTag"
        />
        <button
          type="button"
          class="add-btn"
          :disabled="!inputValue.trim()"
          @click="addCustomTag"
        >
          + 添加
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  options: { type: Array, default: () => [] },
  placeholder: { type: String, default: '输入自定义内容后按回车添加...' }
})

const emit = defineEmits(['update:modelValue'])

const inputValue = ref('')
const inputRef = ref(null)
const customTags = ref([])
const selectedPresets = ref([])

// Parse modelValue into selected presets + custom tags
function parseValue(val) {
  if (!val || !val.trim()) return
  var items = val.split(',').map(function(s) { return s.trim() }).filter(Boolean)
  var presetValues = props.options.map(function(o) { return o.value })
  selectedPresets.value = items.filter(function(item) { return presetValues.indexOf(item) !== -1 })
  customTags.value = items.filter(function(item) { return presetValues.indexOf(item) === -1 })
}

watch(function() { return props.modelValue }, function(newVal) {
  parseValue(newVal)
}, { immediate: true })

function isSelected(value) {
  return selectedPresets.value.indexOf(value) !== -1
}

function toggleTag(value) {
  var idx = selectedPresets.value.indexOf(value)
  if (idx === -1) {
    selectedPresets.value.push(value)
  } else {
    selectedPresets.value.splice(idx, 1)
  }
  emitUpdate()
}

function addCustomTag() {
  var text = inputValue.value.trim()
  if (!text) return
  customTags.value.push(text)
  inputValue.value = ''
  emitUpdate()
  nextTick(function() {
    if (inputRef.value) inputRef.value.focus()
  })
}

function removeCustomTag(idx) {
  customTags.value.splice(idx, 1)
  emitUpdate()
}

function emitUpdate() {
  var all = selectedPresets.value.concat(customTags.value)
  emit('update:modelValue', all.join(','))
}
</script>

<style scoped>
.tag-select-input {
  width: 100%;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.4rem 0.85rem;
  background: #f1f5f9;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.82rem;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  user-select: none;
}

.tag-chip:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

.tag-chip.active {
  background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%);
  border-color: #7c3aed;
  color: #5b21b6;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(124, 58, 237, 0.15);
}

.tag-chip.active .tag-check {
  opacity: 1;
}

.tag-icon {
  font-size: 0.9rem;
}

.tag-check {
  font-size: 0.65rem;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.custom-input-wrap {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 0.6rem;
  transition: border-color 0.2s ease;
}

.custom-input-wrap:focus-within {
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.08);
}

.custom-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
  margin-bottom: 0.5rem;
}

.custom-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.2rem 0.6rem;
  background: #dbeafe;
  border: 1px solid #93c5fd;
  border-radius: 6px;
  font-size: 0.75rem;
  color: #1d4ed8;
  font-weight: 500;
}

.custom-tag-remove {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: none;
  background: rgba(29, 78, 216, 0.15);
  color: #1d4ed8;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.15s ease;
  padding: 0;
  line-height: 1;
  font-family: inherit;
}

.custom-tag-remove:hover {
  background: #1d4ed8;
  color: #fff;
}

.input-row {
  display: flex;
  gap: 0.5rem;
}

.custom-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 0.85rem;
  color: #334155;
  outline: none;
  font-family: inherit;
  padding: 0.25rem 0;
}

.custom-input::placeholder {
  color: #94a3b8;
}

.add-btn {
  padding: 0.3rem 0.85rem;
  background: #7c3aed;
  color: #fff;
  border: none;
  border-radius: 7px;
  font-size: 0.78rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  white-space: nowrap;
}

.add-btn:hover:not(:disabled) {
  background: #6d28d9;
  transform: translateY(-1px);
}

.add-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}
</style>
