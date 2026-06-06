<template>
  <div class="media-uploader">
    <!-- 图片上传区域 -->
    <div class="upload-section">
      <span class="section-label">📷 图片（JPG/PNG/WEBP，单文件≤10MB）</span>
      
      <div class="preview-grid" v-if="imagePreviews.length > 0">
        <div v-for="(item, idx) in imagePreviews" :key="'img-' + idx" class="preview-item">
          <img :src="item.preview" class="preview-img" />
          <div class="preview-overlay" v-if="item.uploading">
            <div class="progress-ring">
              <span class="progress-text">{{ item.progress }}%</span>
            </div>
          </div>
          <div class="preview-overlay error-overlay" v-if="item.error">
            <span class="error-text">{{ item.error }}</span>
            <button class="retry-btn" @click="retryUpload('images', idx)">🔄 重试</button>
          </div>
          <button class="remove-btn" @click="removeFile('images', idx)">×</button>
          <span class="file-size">{{ formatSize(item.file.size) }}</span>
        </div>
      </div>

      <label class="upload-trigger">
        <input
          type="file"
          accept=".jpg,.jpeg,.png,.webp"
          multiple
          @change="handleFilesSelected($event, 'images')"
          class="file-input"
        />
        <div class="upload-placeholder">
          <span>🖼️</span>
          <span>点击选择图片或拖拽到此处</span>
          <span class="upload-hint">已选 {{ imagePreviews.length }} 个文件</span>
        </div>
      </label>
    </div>

    <!-- 视频上传区域 -->
    <div class="upload-section">
      <span class="section-label">🎬 视频（MP4/AVI/MOV，单文件≤200MB）</span>
      
      <div class="preview-grid" v-if="videoPreviews.length > 0">
        <div v-for="(item, idx) in videoPreviews" :key="'vid-' + idx" class="preview-item video-item">
          <video :src="item.preview" class="preview-video" v-if="item.preview && !item.uploading"></video>
          <div class="video-placeholder" v-else>
            <span>🎬</span>
            <span>{{ item.file.name }}</span>
          </div>
          <div class="preview-overlay" v-if="item.uploading">
            <div class="progress-bar-wrap">
              <div class="progress-bar-fill" :style="{ width: item.progress + '%' }"></div>
            </div>
            <span class="progress-text">{{ item.progress }}%</span>
          </div>
          <div class="preview-overlay error-overlay" v-if="item.error">
            <span class="error-text">{{ item.error }}</span>
            <button class="retry-btn" @click="retryUpload('videos', idx)">🔄 重试</button>
          </div>
          <button class="remove-btn" @click="removeFile('videos', idx)">×</button>
          <span class="file-size">{{ formatSize(item.file.size) }}</span>
        </div>
      </div>

      <label class="upload-trigger">
        <input
          type="file"
          accept=".mp4,.avi,.mov"
          multiple
          @change="handleFilesSelected($event, 'videos')"
          class="file-input"
        />
        <div class="upload-placeholder">
          <span>🎥</span>
          <span>点击选择视频或拖拽到此处</span>
          <span class="upload-hint">已选 {{ videoPreviews.length }} 个文件</span>
        </div>
      </label>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { validateImage, validateVideo, formatSize } from '../api/upload.js'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ imageUrls: [], videoUrls: [] })
  }
})

const emit = defineEmits(['update:modelValue'])

const imagePreviews = ref([])
const videoPreviews = ref([])

// ===== 文件选择 =====

function handleFilesSelected(event, category) {
  const files = Array.from(event.target.files || [])
  if (files.length === 0) return

  const previews = category === 'images' ? imagePreviews : videoPreviews
  const validator = category === 'images' ? validateImage : validateVideo

  files.forEach(file => {
    const error = validator(file)
    const entry = {
      file,
      preview: null,
      progress: 0,
      uploading: false,
      uploaded: false,
      error: error || null,
      accessUrl: null
    }

    // 生成本地预览
    if (!error && file.type.startsWith('image/')) {
      entry.preview = URL.createObjectURL(file)
    }
    if (!error && file.type.startsWith('video/')) {
      entry.preview = URL.createObjectURL(file)
    }

    previews.value.push(entry)
  })

  // 清除 input 的值，以允许重复选择相同文件
  event.target.value = ''
}

// ===== 移除文件 =====

function removeFile(category, idx) {
  const previews = category === 'images' ? imagePreviews : videoPreviews
  const entry = previews.value[idx]
  if (entry && entry.preview && entry.preview.startsWith('blob:')) {
    URL.revokeObjectURL(entry.preview)
  }
  previews.value.splice(idx, 1)
  emitUploadedUrls()
}

// ===== 重试上传 =====

function retryUpload(category, idx) {
  const previews = category === 'images' ? imagePreviews : videoPreviews
  const entry = previews.value[idx]
  if (entry) {
    entry.error = null
    entry.progress = 0
    entry.uploading = false
    entry.uploaded = false
  }
}

// ===== 向上抛出已成功上传的URL =====

function emitUploadedUrls() {
  const imageUrls = imagePreviews.value
    .filter(p => p.uploaded && p.accessUrl)
    .map(p => p.accessUrl)
  const videoUrls = videoPreviews.value
    .filter(p => p.uploaded && p.accessUrl)
    .map(p => p.accessUrl)
  emit('update:modelValue', { imageUrls, videoUrls })
}

// 对外暴露：执行所有待上传文件的上传
// 外部调用：uploaderRef.value.uploadAll()
async function uploadAll() {
  const imagePending = imagePreviews.value.filter(p => !p.uploaded && !p.error)
  const videoPending = videoPreviews.value.filter(p => !p.uploaded && !p.error)

  await uploadBatch(imagePending, 'images', imagePreviews)
  await uploadBatch(videoPending, 'videos', videoPreviews)

  emitUploadedUrls()

  // 检查是否有失败项
  const failedImages = imagePreviews.value.filter(p => p.error && !p.uploaded)
  const failedVideos = videoPreviews.value.filter(p => p.error && !p.uploaded)
  return { failedImages, failedVideos }
}

async function uploadBatch(pendingList, category, previewsRef) {
  const { uploadFiles } = await import('../api/upload.js')

  if (pendingList.length === 0) return

  // 收集需要上传的文件
  const files = pendingList.map(p => p.file)

  // 标记为上传中
  pendingList.forEach(p => { p.uploading = true; p.progress = 0 })

  // 找到 pendingList 中每个文件在 previewsRef 中的索引
  const indexMap = pendingList.map(p => previewsRef.value.indexOf(p))

  try {
    const results = await uploadFiles(files, category, (fileIdx, percent) => {
      const realIdx = indexMap[fileIdx]
      if (realIdx >= 0 && previewsRef.value[realIdx]) {
        previewsRef.value[realIdx].progress = percent
      }
    })

    // 处理结果
    results.forEach((result, i) => {
      const realIdx = indexMap[i]
      if (realIdx >= 0 && previewsRef.value[realIdx]) {
        const entry = previewsRef.value[realIdx]
        entry.uploading = false
        if (result.success) {
          entry.uploaded = true
          entry.accessUrl = result.accessUrl
          entry.progress = 100
        } else {
          entry.error = result.error || '上传失败'
        }
      }
    })
  } catch (err) {
    pendingList.forEach((p, i) => {
      const realIdx = indexMap[i]
      if (realIdx >= 0 && previewsRef.value[realIdx]) {
        previewsRef.value[realIdx].uploading = false
        previewsRef.value[realIdx].error = err.message || '上传异常'
      }
    })
  }
}

// 清理所有 blob URL
function cleanup() {
  ;[...imagePreviews.value, ...videoPreviews.value].forEach(entry => {
    if (entry.preview && entry.preview.startsWith('blob:')) {
      URL.revokeObjectURL(entry.preview)
    }
  })
  imagePreviews.value = []
  videoPreviews.value = []
}

// 对外暴露方法
defineExpose({ uploadAll, cleanup })
</script>

<style scoped>
.media-uploader {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.section-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #475569;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 0.5rem;
}

.preview-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  aspect-ratio: 1;
  background: #f8fafc;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #000;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  font-size: 0.7rem;
  color: #64748b;
  background: #1e293b;
}

.video-placeholder span:first-child {
  font-size: 1.5rem;
}

.preview-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
}

.error-overlay {
  background: rgba(220, 38, 38, 0.85);
}

.progress-ring {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-bar-wrap {
  width: 80%;
  height: 4px;
  background: rgba(255,255,255,0.3);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: #fff;
  border-radius: 2px;
  transition: width 0.2s;
}

.progress-text {
  font-size: 0.7rem;
  color: #fff;
  font-weight: 600;
}

.error-text {
  font-size: 0.65rem;
  color: #fff;
  text-align: center;
  padding: 0 0.25rem;
  line-height: 1.3;
}

.retry-btn {
  padding: 0.15rem 0.5rem;
  background: rgba(255,255,255,0.2);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.4);
  border-radius: 4px;
  font-size: 0.65rem;
  cursor: pointer;
  font-family: inherit;
}

.retry-btn:hover {
  background: rgba(255,255,255,0.35);
}

.remove-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(0,0,0,0.6);
  color: #fff;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  line-height: 1;
  z-index: 2;
}

.remove-btn:hover {
  background: rgba(220, 38, 38, 0.9);
}

.file-size {
  position: absolute;
  bottom: 2px;
  right: 4px;
  font-size: 0.6rem;
  color: #fff;
  background: rgba(0,0,0,0.5);
  padding: 0 3px;
  border-radius: 2px;
}

.upload-trigger {
  cursor: pointer;
}

.file-input {
  display: none;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  padding: 1.25rem;
  border: 2px dashed #cbd5e1;
  border-radius: 10px;
  color: #94a3b8;
  font-size: 0.8rem;
  transition: all 0.2s;
}

.upload-placeholder:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #f0f7ff;
}

.upload-hint {
  font-size: 0.65rem;
  color: #cbd5e1;
  margin-top: 0.15rem;
}
</style>
