<template>
  <div class="dynamic-publisher">
    <div class="publisher-header">
      <div class="user-avatar">
        <span>👤</span>
      </div>
      <button class="publisher-trigger" @click="openModal" aria-label="打开动态发布弹窗">
        <span>动态发布，分享你的篮球生活...</span>
      </button>
    </div>

    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showModal"
          class="modal-overlay"
          @click.self="closeModal"
          role="dialog"
          aria-modal="true"
          aria-labelledby="modal-title"
          aria-describedby="modal-description"
          ref="modalRef"
        >
          <div class="modal-container" :class="['type-' + currentType]" role="document">
            <div class="modal-header" :class="'header-' + currentType">
              <div class="header-info">
                <span class="header-icon" aria-hidden="true">{{ currentTypeInfo.icon }}</span>
                <div>
                  <h2 class="header-title" id="modal-title">{{ currentTypeInfo.title }}</h2>
                  <p class="header-subtitle" id="modal-description">{{ currentTypeInfo.subtitle }}</p>
                </div>
              </div>
              <button @click="closeModal" class="modal-close" aria-label="关闭弹窗">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>

            <div class="modal-body">
              <div class="type-switcher" role="radiogroup" aria-label="选择动态类型">
                <button
                  v-for="type in typeOptions"
                  :key="type.value"
                  :class="['type-btn', { active: currentType === type.value }]"
                  @click="switchType(type.value)"
                  :aria-checked="currentType === type.value"
                  role="radio"
                  :tabindex="currentType === type.value ? 0 : -1"
                  @keydown.enter="switchType(type.value)"
                  @keydown.space.prevent="switchType(type.value)"
                >
                  <span class="type-icon" aria-hidden="true">{{ type.icon }}</span>
                  <span class="type-text">{{ type.label }}</span>
                </button>
              </div>

              <div class="rich-editor-toolbar">
                <button class="toolbar-btn" @click="formatText('bold')" title="加粗">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M5 3h4.5a3.5 3.5 0 0 1 0 7H6a3 3 0 0 1 0 6H5V3zm0 7h4a1.5 1.5 0 1 0 0-3H6v3zm0-6h3.5a1.5 1.5 0 1 0 0-3H6v3z"/>
                  </svg>
                </button>
                <button class="toolbar-btn" @click="formatText('italic')" title="斜体">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M9 3h-2l1.5 10h2L9 3z"/>
                  </svg>
                </button>
                <button class="toolbar-btn" @click="formatText('underline')" title="下划线">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M4 3v5a4 4 0 0 0 8 0V3H10v5a2 2 0 0 1-4 0V3H4zm1 11h6v1H5v-1z"/>
                  </svg>
                </button>
                <div class="toolbar-divider"></div>
                <button class="toolbar-btn" @click="insertLink" title="插入链接">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M4.5 7.5a3 3 0 0 1 3-3h2a.5.5 0 0 1 0 1h-2a2 2 0 0 0 0 4h2a.5.5 0 0 1 0 1h-2a3 3 0 0 1-3-3zm7-4a3 3 0 0 1 3 3v.5a3 3 0 0 1-3 3h-2a.5.5 0 0 1 0-1h2a2 2 0 0 0 0-4h-2a.5.5 0 0 1 0-1h2z"/>
                  </svg>
                </button>
              </div>
              <div
                ref="editorRef"
                class="rich-editor"
                contenteditable="true"
                :placeholder="currentTypeInfo.placeholder"
                @input="handleEditorInput"
                @keyup="handleEditorKeyup"
                @paste="handleEditorPaste"
                @dragenter.prevent
                @dragover.prevent
              ></div>
              <div class="char-count" :class="{ 'count-warning': charCount > 450, 'count-danger': charCount >= 500 }" id="char-count-info">
                <span>{{ charCount }}/500</span>
                <span v-if="charCount >= 500" class="warning-text">已达到最大字符限制</span>
              </div>

              <div v-if="currentType !== 'text'" class="media-section">
                <span class="section-label">{{ currentTypeInfo.mediaLabel }}</span>
                <div v-if="currentType === 'image'"
                  class="image-uploader"
                  @dragover.prevent="handleImageDragOver"
                  @dragleave.prevent="handleImageDragLeave"
                  @drop.prevent="handleImageDrop"
                  :class="{ 'drag-over': isImageDragging }"
                >
                  <div
                    v-for="(img, idx) in imagePreviewList"
                    :key="'img-' + idx"
                    class="image-preview-item"
                    role="group"
                    :aria-label="'图片 ' + (idx + 1)"
                  >
                    <img :src="img" class="preview-img" alt="预览图片" />
                    <button @click="removeImage(idx)" class="remove-btn" :aria-label="'删除图片 ' + (idx + 1)">×</button>
                  </div>
                  <label v-if="imagePreviewList.length < 9" class="upload-trigger" role="button" tabindex="0" @keydown.enter.prevent="$refs.imageInput.click()" @keydown.space.prevent="$refs.imageInput.click()">
                    <input
                      ref="imageInput"
                      type="file"
                      accept="image/*"
                      multiple
                      @change="handleImageUpload"
                      class="hidden-input"
                      aria-label="上传图片"
                    />
                    <div class="upload-icon-wrapper">
                      <span class="upload-icon">📷</span>
                      <span class="upload-plus">+</span>
                    </div>
                    <span class="upload-text">点击或拖拽上传</span>
                    <span class="upload-hint">{{ imagePreviewList.length }}/9</span>
                  </label>
                  <div v-if="isImageDragging" class="drag-overlay">
                    <span class="drag-icon">📥</span>
                    <span class="drag-text">松开鼠标上传图片</span>
                  </div>
                </div>

                <div v-if="currentType === 'video'" class="video-uploader">
                  <div v-for="(vid, idx) in videoPreviewList" :key="'vid-' + idx" class="video-preview-item" role="group" :aria-label="'视频 ' + (idx + 1)">
                    <div class="video-thumbnail-wrapper">
                      <img :src="videoThumbnails[idx]" class="video-thumbnail" alt="视频缩略图" />
                      <span class="video-play-icon">▶</span>
                    </div>
                    <div class="video-info">
                      <span class="video-name">{{ videoFileNames[idx] }}</span>
                      <span class="video-size">{{ formatFileSize(videoFiles[idx]?.size) }}</span>
                    </div>
                    <button @click="removeVideo(idx)" class="remove-btn" :aria-label="'删除视频 ' + (idx + 1)">×</button>
                  </div>
                  <label v-if="videoPreviewList.length === 0 && !videoUploading" class="upload-trigger video-trigger" role="button" tabindex="0" @keydown.enter.prevent="$refs.videoInput.click()" @keydown.space.prevent="$refs.videoInput.click()">
                    <input
                      ref="videoInput"
                      type="file"
                      accept="video/mp4,video/quicktime,video/x-msvideo,video/webm"
                      @change="handleVideoUpload"
                      class="hidden-input"
                      aria-label="上传视频"
                    />
                    <span class="upload-icon">🎥</span>
                    <span class="upload-text">选择视频文件</span>
                    <span class="upload-hint">支持 MP4、MOV 等格式，最大 100MB</span>
                  </label>
                  <div v-if="videoUploading" class="video-upload-progress" role="progressbar" :aria-valuenow="videoUploadProgress" aria-valuemin="0" aria-valuemax="100">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: videoUploadProgress + '%' }"></div>
                    </div>
                    <span class="progress-text">{{ videoUploadProgress }}% - 上传中...</span>
                  </div>
                </div>
              </div>

              <div class="emoji-section">
                <span class="section-label">表情</span>
                <div class="emoji-list" role="list" aria-label="选择表情">
                  <span
                    v-for="(emoji, idx) in emojis"
                    :key="emoji"
                    class="emoji-item"
                    @click="addEmoji(emoji)"
                    role="button"
                    tabindex="0"
                    @keydown.enter.prevent="addEmoji(emoji)"
                    @keydown.space.prevent="addEmoji(emoji)"
                    :aria-label="'添加表情 ' + emoji"
                  >{{ emoji }}</span>
                </div>
              </div>

              <div class="hot-topics-section">
                <span class="section-label">🔥 热门话题</span>
                <div class="hot-topics-list" role="list" aria-label="选择热门话题">
                  <div
                    v-for="topic in hotTopics"
                    :key="topic.id"
                    :class="['hot-topic-item', { active: selectedTopic?.id === topic.id }]"
                    @click="selectTopic(topic)"
                    role="button"
                    tabindex="0"
                    @keydown.enter.prevent="selectTopic(topic)"
                    @keydown.space.prevent="selectTopic(topic)"
                    :aria-pressed="selectedTopic?.id === topic.id"
                    :aria-label="'选择话题 ' + topic.text"
                  >
                    <span class="topic-icon" aria-hidden="true">{{ topic.icon }}</span>
                    <span class="topic-text">{{ topic.text }}</span>
                  </div>
                </div>
              </div>

              <div class="meta-section">
                <div class="tags-section">
                  <span class="section-label">标签（可选）</span>
                  <div class="tag-options" role="group" aria-label="选择标签">
                    <button
                      v-for="t in availableTags"
                      :key="t"
                      :class="['tag-ch', { active: newDynamic.tags && newDynamic.tags.includes(t) }]"
                      @click="toggleTag(t)"
                      :aria-pressed="newDynamic.tags && newDynamic.tags.includes(t)"
                      :aria-label="'标签 ' + (hashTags[t] || t)"
                    >{{ hashTags[t] || t }}</button>
                  </div>
                </div>

                <div class="privacy-section">
                  <span class="section-label">隐私设置</span>
                  <div class="type-options" role="radiogroup" aria-label="选择隐私设置">
                    <button
                      :class="['type-option', { active: newDynamic.privacy === 'public' }]"
                      @click="newDynamic.privacy = 'public'"
                      role="radio"
                      :aria-checked="newDynamic.privacy === 'public'"
                      tabindex="0"
                      @keydown.enter.prevent="newDynamic.privacy = 'public'"
                      @keydown.space.prevent="newDynamic.privacy = 'public'"
                    >🌐 公开</button>
                    <button
                      :class="['type-option', { active: newDynamic.privacy === 'friends' }]"
                      @click="newDynamic.privacy = 'friends'"
                      role="radio"
                      :aria-checked="newDynamic.privacy === 'friends'"
                      tabindex="0"
                      @keydown.enter.prevent="newDynamic.privacy = 'friends'"
                      @keydown.space.prevent="newDynamic.privacy = 'friends'"
                    >👥 好友</button>
                    <button
                      :class="['type-option', { active: newDynamic.privacy === 'private' }]"
                      @click="newDynamic.privacy = 'private'"
                      role="radio"
                      :aria-checked="newDynamic.privacy === 'private'"
                      tabindex="0"
                      @keydown.enter.prevent="newDynamic.privacy = 'private'"
                      @keydown.space.prevent="newDynamic.privacy = 'private'"
                    >🔒 私密</button>
                  </div>
                </div>
              </div>

              <div v-if="newDynamic.content" class="preview-section">
                <span class="section-label">实时预览</span>
                <div class="preview-card" role="region" aria-label="动态预览">
                  <div class="preview-header">
                    <span class="preview-avatar">👤</span>
                    <div>
                      <span class="preview-name">我的{{ currentTypeInfo.title }}</span>
                      <span class="preview-time">刚刚</span>
                    </div>
                    <span class="preview-type">{{ currentTypeInfo.icon }} {{ currentTypeInfo.title }}</span>
                  </div>
                  <p class="preview-content">{{ newDynamic.content }}</p>
                  <div v-if="imagePreviewList.length > 0" class="preview-images">
                    <img v-for="(img, idx) in imagePreviewList.slice(0, 3)" :key="idx" :src="img" class="preview-thumb" :alt="'预览图片 ' + (idx + 1)" />
                  </div>
                  <div v-if="videoPreviewList.length > 0" class="preview-videos">
                    <video :src="videoPreviewList[0]" class="preview-video-thumb" controls aria-label="预览视频"></video>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <button
                @click="closeModal"
                class="btn-cancel"
                :disabled="submitLoading"
                aria-label="取消发布"
              >取消</button>
              <button
                @click="handlePublish"
                class="btn-submit"
                :class="'btn-' + currentType"
                :disabled="!canPublish || submitLoading"
                aria-busy="submitLoading"
                aria-label="发布动态"
              >
                <span v-if="submitLoading" class="btn-spinner" aria-hidden="true"></span>
                {{ submitLoading ? '发布中...' : currentTypeInfo.publishText }}
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { createDynamic as apiCreateDynamic } from '../api/dynamic'

const props = defineProps({
  presetContent: String,
  presetType: {
    type: String,
    default: 'text'
  }
})

const emit = defineEmits(['published'])

const showModal = ref(false)
const currentType = ref(props.presetType || 'text')
const submitLoading = ref(false)
const imageFiles = ref([])
const imagePreviewList = ref([])
const videoFiles = ref([])
const videoPreviewList = ref([])
const videoThumbnails = ref([])
const videoFileNames = ref([])
const videoUploading = ref(false)
const videoUploadProgress = ref(0)
const isImageDragging = ref(false)
const modalRef = ref(null)
const editorRef = ref(null)
const textareaRef = ref(null)
const focusableElements = ref([])
const currentFocusIndex = ref(0)
const charCount = ref(0)

// 监听预设内容和类型变化
watch(() => props.presetContent, (newVal) => {
  if (newVal) {
    newDynamic.value.content = newVal
  }
})

watch(() => props.presetType, (newVal) => {
  if (newVal) {
    currentType.value = newVal
    newDynamic.value.type = newVal
  }
})

watch(showModal, (newVal) => {
  if (newVal) {
    nextTick(() => {
      initFocusManager()
      if (editorRef.value) {
        editorRef.value.focus()
      }
    })
  }
})

// 暴露外部方法
defineExpose({
  openPublisher: (type = 'text', content = '') => {
    currentType.value = type
    newDynamic.value.content = content
    newDynamic.value.type = type
    showModal.value = true
  }
})

const newDynamic = ref({
  content: '',
  type: 'text',
  tags: [],
  privacy: 'public'
})

const selectedTopic = ref(null)

const hotTopics = [
  { id: 1, icon: '🏆', text: 'NBA总决赛谁能夺冠？', count: 432, league: 'NBA' },
  { id: 2, icon: '⭐', text: '东契奇能否成为历史最佳控卫？', count: 315, league: 'NBA' },
  { id: 3, icon: '🌍', text: '中国男篮世界杯前景如何？', count: 267, league: 'FIBA' },
  { id: 4, icon: '🇨🇳', text: 'CBA新赛季哪支球队最强？', count: 198, league: 'CBA' },
  { id: 5, icon: '🎯', text: '文班亚马的上限在哪里？', count: 289, league: 'NBA' },
  { id: 6, icon: '🏅', text: '奥运会男篮金牌花落谁家？', count: 176, league: '奥运会' }
]

const selectTopic = (topic) => {
  if (selectedTopic.value?.id === topic.id) {
    selectedTopic.value = null
    const topicTag = topic.text.replace(/[？？]/g, '')
    newDynamic.value.tags = newDynamic.value.tags.filter(t => t !== topicTag)
  } else {
    selectedTopic.value = topic
    const topicTag = topic.text.replace(/[？？]/g, '')
    if (!newDynamic.value.tags.includes(topicTag)) {
      newDynamic.value.tags.push(topicTag)
    }
  }
}

const typeOptions = [
  { value: 'text', label: '文字动态', icon: '📝', color: 'blue' },
  { value: 'image', label: '图片动态', icon: '📷', color: 'green' },
  { value: 'video', label: '视频动态', icon: '🎥', color: 'purple' }
]

const currentTypeInfo = computed(() => {
  const map = {
    text: {
      title: '文字动态',
      subtitle: '用文字记录你的篮球心得',
      icon: '📝',
      placeholder: '分享你的训练心得、比赛感受或篮球故事...',
      mediaLabel: '',
      publishText: '发布文字动态'
    },
    image: {
      title: '图片动态',
      subtitle: '用图片展示精彩瞬间',
      icon: '📷',
      placeholder: '为这组图片添加描述...',
      mediaLabel: '📷 图片（最多9张）',
      publishText: '发布图片动态'
    },
    video: {
      title: '视频动态',
      subtitle: '用视频分享你的篮球时刻',
      icon: '🎥',
      placeholder: '为这段视频添加描述...',
      mediaLabel: '🎥 视频（最多3个）',
      publishText: '发布视频动态'
    }
  }
  return map[currentType.value]
})

const canPublish = computed(() => {
  if (!newDynamic.value.content.trim()) return false
  if (currentType.value === 'image' && imagePreviewList.value.length === 0) return false
  if (currentType.value === 'video' && videoPreviewList.value.length === 0) return false
  return true
})

const emojis = ['🏀', '💪', '🔥', '⚡', '🎯', '⭐', '👏', '🎉', '🏆', '💯', '✅', '💥']

const availableTags = ['投篮', '运球', '防守', '体能', '战术', 'NBA', '日常', '心得']

const hashTags = {
  '投篮': '#投篮训练', '运球': '#运球技巧', '防守': '#防守大闸',
  '体能': '#体能训练', '战术': '#战术分析', 'NBA': '#NBA赛事',
  '日常': '#训练日常', '心得': '#心得体会'
}

const initFocusManager = () => {
  if (modalRef.value) {
    focusableElements.value = Array.from(modalRef.value.querySelectorAll(
      'button, [role="button"], textarea, [tabindex]:not([tabindex="-1"])'
    )).filter(el => !el.disabled && el.offsetParent !== null)
    currentFocusIndex.value = 0
  }
}

const handleKeydown = (e) => {
  if (!showModal.value) return

  if (e.key === 'Escape') {
    closeModal()
    return
  }

  if (e.key === 'Tab') {
    e.preventDefault()
    if (focusableElements.value.length === 0) return

    if (e.shiftKey) {
      currentFocusIndex.value = (currentFocusIndex.value - 1 + focusableElements.value.length) % focusableElements.value.length
    } else {
      currentFocusIndex.value = (currentFocusIndex.value + 1) % focusableElements.value.length
    }

    focusableElements.value[currentFocusIndex.value].focus()
  }
}

const openModal = () => {
  resetForm()
  showModal.value = true
}

const openPublisher = (type = 'text', content = '') => {
  currentType.value = type
  resetForm()
  if (content) {
    newDynamic.value.content = content
  }
  showModal.value = true
}

const switchType = (type) => {
  currentType.value = type
  newDynamic.value.type = type
  nextTick(() => {
    initFocusManager()
    if (editorRef.value) {
      editorRef.value.focus()
    }
  })
}

// 富文本编辑器相关方法
const formatText = (command) => {
  document.execCommand(command, false, null)
  if (editorRef.value) {
    editorRef.value.focus()
  }
}

const insertLink = () => {
  const url = prompt('请输入链接地址：', 'https://')
  if (url) {
    document.execCommand('createLink', false, url)
  }
}

const handleEditorInput = () => {
  if (editorRef.value) {
    const text = editorRef.value.innerText
    if (text.length > 500) {
      editorRef.value.innerText = text.slice(0, 500)
      // 将光标移动到末尾
      const range = document.createRange()
      range.selectNodeContents(editorRef.value)
      range.collapse(false)
      const sel = window.getSelection()
      sel.removeAllRanges()
      sel.addRange(range)
    }
    charCount.value = editorRef.value.innerText.length
    newDynamic.value.content = editorRef.value.innerHTML
  }
}

const handleEditorKeyup = () => {
  if (editorRef.value) {
    charCount.value = editorRef.value.innerText.length
    newDynamic.value.content = editorRef.value.innerHTML
  }
}

const handleEditorPaste = (e) => {
  e.preventDefault()
  const text = e.clipboardData.getData('text/plain')
  if (editorRef.value) {
    const currentText = editorRef.value.innerText
    const remaining = 500 - currentText.length
    const textToInsert = text.slice(0, remaining)
    document.execCommand('insertText', false, textToInsert)
    charCount.value = editorRef.value.innerText.length
    newDynamic.value.content = editorRef.value.innerHTML
  }
}

// 图片拖拽上传方法
const handleImageDragOver = () => {
  isImageDragging.value = true
}

const handleImageDragLeave = () => {
  isImageDragging.value = false
}

const handleImageDrop = (e) => {
  isImageDragging.value = false
  const files = Array.from(e.dataTransfer.files || [])
  const imageFiles = files.filter(file => file.type.startsWith('image/'))
  
  if (imageFiles.length === 0) {
    ElMessage.warning('请上传图片文件')
    return
  }

  const remaining = 9 - imagePreviewList.value.length
  const filesToAdd = imageFiles.slice(0, remaining)

  filesToAdd.forEach(file => {
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.warning(`图片 ${file.name} 超过10MB，已跳过`)
      return
    }
    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreviewList.value.push(e.target.result)
      imageFiles.value.push(file)
    }
    reader.readAsDataURL(file)
  })
}

// 视频上传进度和缩略图方法
const handleVideoUpload = (e) => {
  const file = e.target.files?.[0]
  if (!file) return

  if (file.size > 100 * 1024 * 1024) {
    ElMessage.error('视频文件不能超过100MB')
    return
  }

  videoUploading.value = true
  videoUploadProgress.value = 0

  // 模拟上传进度
  const progressInterval = setInterval(() => {
    if (videoUploadProgress.value < 90) {
      videoUploadProgress.value += Math.random() * 15
    }
  }, 200)

  // 读取视频文件
  const reader = new FileReader()
  reader.onload = (e) => {
    videoPreviewList.value.push(e.target.result)
    videoFiles.value.push(file)
    videoFileNames.value.push(file.name)
    
    // 生成视频缩略图
    generateVideoThumbnail(file)
    
    // 完成模拟上传
    videoUploadProgress.value = 100
    clearInterval(progressInterval)
    setTimeout(() => {
      videoUploading.value = false
    }, 500)
  }
  reader.readAsDataURL(file)

  e.target.value = ''
}

const generateVideoThumbnail = (file) => {
  const video = document.createElement('video')
  video.src = URL.createObjectURL(file)
  video.crossOrigin = 'anonymous'
  
  video.addEventListener('loadeddata', () => {
    video.currentTime = 1 // 获取第1秒的帧
    
    const canvas = document.createElement('canvas')
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    const ctx = canvas.getContext('2d')
    
    video.addEventListener('seeked', () => {
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
      videoThumbnails.value.push(canvas.toDataURL('image/jpeg', 0.8))
      URL.revokeObjectURL(video.src)
    })
  })
}

const formatFileSize = (bytes) => {
  if (!bytes) return ''
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const resetForm = () => {
  newDynamic.value = {
    content: '',
    type: currentType.value,
    tags: [],
    privacy: 'public'
  }
  imageFiles.value = []
  imagePreviewList.value = []
  videoFiles.value = []
  videoPreviewList.value = []
  videoThumbnails.value = []
  videoFileNames.value = []
  videoUploading.value = false
  videoUploadProgress.value = 0
  selectedTopic.value = null
  currentFocusIndex.value = 0
  charCount.value = 0
  if (editorRef.value) {
    editorRef.value.innerHTML = ''
  }
}

const addEmoji = (emoji) => {
  if (editorRef.value) {
    const selection = window.getSelection()
    if (selection.rangeCount > 0) {
      const range = selection.getRangeAt(0)
      range.insertNode(document.createTextNode(emoji))
      range.collapse(false)
      selection.removeAllRanges()
      selection.addRange(range)
    } else {
      editorRef.value.innerHTML += emoji
    }
    handleEditorInput()
  }
}

const toggleTag = (tag) => {
  if (!newDynamic.value.tags) newDynamic.value.tags = []
  const idx = newDynamic.value.tags.indexOf(tag)
  if (idx > -1) newDynamic.value.tags.splice(idx, 1)
  else newDynamic.value.tags.push(tag)
}

const handleImageUpload = (e) => {
  const files = Array.from(e.target.files || [])
  if (files.length === 0) return

  const remaining = 9 - imagePreviewList.value.length
  const filesToAdd = files.slice(0, remaining)

  filesToAdd.forEach(file => {
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.warning(`图片 ${file.name} 超过10MB，已跳过`)
      return
    }
    const reader = new FileReader()
    reader.onload = (e) => {
      imagePreviewList.value.push(e.target.result)
      imageFiles.value.push(file)
    }
    reader.readAsDataURL(file)
  })

  e.target.value = ''
}

const removeImage = (idx) => {
  imagePreviewList.value.splice(idx, 1)
  imageFiles.value.splice(idx, 1)
}

const removeVideo = (idx) => {
  videoPreviewList.value.splice(idx, 1)
  videoFiles.value.splice(idx, 1)
}

const uploadMediaFiles = async () => {
  const uploadedImages = []
  const uploadedVideos = []

  // 真实环境应调用上传接口，此处用本地预览URL作为模拟
  for (const img of imagePreviewList.value) {
    uploadedImages.push(img)
  }
  for (const vid of videoPreviewList.value) {
    uploadedVideos.push(vid)
  }

  return { imageUrls: uploadedImages, videoUrls: uploadedVideos }
}

const handlePublish = async () => {
  if (!newDynamic.value.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  if (currentType.value === 'image' && imagePreviewList.value.length === 0) {
    ElMessage.warning('请至少添加一张图片')
    return
  }

  if (currentType.value === 'video' && videoPreviewList.value.length === 0) {
    ElMessage.warning('请添加一段视频')
    return
  }

  submitLoading.value = true
  try {
    const { imageUrls, videoUrls } = await uploadMediaFiles()

    const response = await apiCreateDynamic({
      content: newDynamic.value.content,
      imageUrls: imageUrls.length > 0 ? imageUrls : null,
      videoUrls: videoUrls.length > 0 ? videoUrls : null,
      type: currentType.value,
      tags: newDynamic.value.tags,
      privacy: newDynamic.value.privacy
    })

    if (response.code === 200) {
      ElMessage.success(`${currentTypeInfo.value.title}发布成功 🎉`)
      emit('published', {
        ...response.data,
        user: { nickname: '我', avatarUrl: '👤' },
        showComments: false,
        newComment: '',
        liked: false,
        comments: []
      })
      closeModal()
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    ElMessage.error('发布失败：' + (error.message || '网络异常'))
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.dynamic-publisher {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 1.25rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.publisher-header {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  margin-bottom: 1rem;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f97316, #ea580c);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  flex-shrink: 0;
}

.publisher-trigger {
  flex: 1;
  text-align: left;
  padding: 0.75rem 1.25rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  color: #94a3b8;
  font-size: 0.9rem;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
}

.publisher-trigger:hover,
.publisher-trigger:focus {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.publisher-trigger:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-container {
  background: #fff;
  border-radius: 20px;
  max-width: 560px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.75rem;
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  background: inherit;
  z-index: 10;
}

.modal-header.header-text {
  background: linear-gradient(135deg, #dbeafe 0%, #fff 100%);
}

.modal-header.header-image {
  background: linear-gradient(135deg, #dcfce7 0%, #fff 100%);
}

.modal-header.header-video {
  background: linear-gradient(135deg, #f3e8ff 0%, #fff 100%);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.header-icon {
  font-size: 1.75rem;
}

.header-title {
  margin: 0;
  font-size: 1.15rem;
  font-weight: 700;
  color: #1e293b;
}

.header-subtitle {
  margin: 0.15rem 0 0;
  font-size: 0.8rem;
  color: #64748b;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  transition: all 0.2s;
}

.modal-close:hover,
.modal-close:focus {
  background: #fee2e2;
  color: #dc2626;
}

.modal-close:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.2);
}

.modal-body {
  padding: 1.5rem 1.75rem;
}

.type-switcher {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.5rem;
  margin-bottom: 1.25rem;
  padding: 0.25rem;
  background: #f8fafc;
  border-radius: 12px;
}

.type-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  padding: 0.6rem;
  background: transparent;
  border: none;
  border-radius: 10px;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
}

.type-btn:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.15);
}

.type-btn .type-icon {
  font-size: 1.25rem;
}

.type-btn .type-text {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 500;
}

.type-btn.active {
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.type-btn.active .type-text {
  color: #f97316;
  font-weight: 600;
}

.content-input {
  width: 100%;
  min-height: 100px;
  padding: 1rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.95rem;
  color: #1e293b;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
  box-sizing: border-box;
  transition: all 0.2s;
}

.content-input:focus {
  outline: none;
  border-color: #f97316;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.content-input.content-overlimit {
  border-color: #f59e0b;
}

.content-input::placeholder {
  color: #94a3b8;
}

.char-count {
  text-align: right;
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 0.35rem;
  margin-bottom: 0.5rem;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 0.5rem;
}

.count-warning {
  color: #f59e0b;
  font-weight: 600;
}

.count-danger {
  color: #dc2626;
  font-weight: 600;
}

.warning-text {
  font-size: 0.7rem;
}

.section-label {
  display: block;
  font-size: 0.8rem;
  font-weight: 600;
  color: #475569;
  margin-bottom: 0.5rem;
}

.media-section {
  margin-top: 1.25rem;
}

.image-uploader,
.video-uploader {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.5rem;
}

.image-preview-item,
.video-preview-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.video-preview-item {
  aspect-ratio: 16/9;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  cursor: pointer;
  font-size: 1rem;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.remove-btn:hover,
.remove-btn:focus {
  background: #dc2626;
}

.remove-btn:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.5), 0 0 0 4px rgba(220, 38, 38, 0.3);
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  aspect-ratio: 1;
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  color: #64748b;
}

.upload-trigger:hover,
.upload-trigger:focus {
  background: #fff7ed;
  border-color: #f97316;
  color: #f97316;
}

.upload-trigger:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.video-trigger {
  aspect-ratio: 16/9;
}

.upload-icon {
  font-size: 1.75rem;
  font-weight: 300;
}

.video-trigger .upload-icon {
  font-size: 1.5rem;
}

.upload-text {
  font-size: 0.8rem;
  font-weight: 500;
}

.upload-hint {
  font-size: 0.65rem;
  color: #94a3b8;
}

.hidden-input {
  display: none;
}

.emoji-section {
  margin-top: 1.25rem;
}

.hot-topics-section {
  margin-top: 1.25rem;
}

.hot-topics-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.5rem 0.75rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.85rem;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.hot-topic-item:hover,
.hot-topic-item:focus {
  background: #fff7ed;
  border-color: #fed7aa;
  color: #ea580c;
}

.hot-topic-item:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.hot-topic-item.active {
  background: #fff7ed;
  border-color: #f97316;
  color: #ea580c;
}

.topic-icon {
  font-size: 1.1rem;
  flex-shrink: 0;
}

.topic-text {
  flex: 1;
}

.emoji-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.emoji-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: #f8fafc;
  border: 1px solid transparent;
}

.emoji-item:hover,
.emoji-item:focus {
  background: #fff7ed;
  border-color: #f97316;
  transform: scale(1.15);
}

.emoji-item:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.15);
}

.meta-section {
  margin-top: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.tag-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}

.tag-ch {
  padding: 0.3rem 0.7rem;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.75rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
}

.tag-ch:hover,
.tag-ch:focus {
  border-color: #f97316;
  color: #f97316;
}

.tag-ch:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.1);
}

.tag-ch.active {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.type-option {
  padding: 0.35rem 0.85rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  font-size: 0.8rem;
  color: #64748b;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
}

.type-option:hover,
.type-option:focus {
  background: #f1f5f9;
}

.type-option:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(148, 163, 184, 0.2);
}

.type-option.active {
  background: #f97316;
  color: #fff;
  border-color: #f97316;
}

.preview-section {
  margin-top: 1.25rem;
}

.preview-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.85rem 1rem;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.preview-avatar {
  width: 32px;
  height: 32px;
  background: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
}

.preview-name {
  display: block;
  font-size: 0.8rem;
  font-weight: 600;
  color: #334155;
}

.preview-time {
  font-size: 0.65rem;
  color: #94a3b8;
}

.preview-type {
  margin-left: auto;
  font-size: 0.7rem;
  color: #f97316;
  background: #fff7ed;
  padding: 0.15rem 0.5rem;
  border-radius: 6px;
  font-weight: 500;
}

.preview-content {
  margin: 0 0 0.5rem;
  font-size: 0.85rem;
  color: #475569;
  line-height: 1.5;
  white-space: pre-wrap;
}

.preview-images {
  display: flex;
  gap: 0.4rem;
  margin-top: 0.4rem;
}

.preview-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}

.preview-videos {
  margin-top: 0.4rem;
}

.preview-video-thumb {
  width: 100%;
  max-width: 240px;
  border-radius: 6px;
}

.modal-footer {
  padding: 1rem 1.75rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  position: sticky;
  bottom: 0;
  background: #fff;
  z-index: 10;
}

.btn-cancel {
  padding: 0.6rem 1.5rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
}

.btn-cancel:hover:not(:disabled),
.btn-cancel:focus:not(:disabled) {
  background: #e2e8f0;
}

.btn-cancel:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.2);
}

.btn-submit {
  padding: 0.6rem 1.5rem;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-submit:focus:not(:disabled) {
  outline: none;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.2);
}

.btn-text {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.btn-text:hover:not(:disabled) {
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.btn-image {
  background: linear-gradient(135deg, #22c55e, #15803d);
}

.btn-image:hover:not(:disabled) {
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
}

.btn-video {
  background: linear-gradient(135deg, #a855f7, #7e22ce);
}

.btn-video:hover:not(:disabled) {
  box-shadow: 0 4px 12px rgba(168, 85, 247, 0.4);
}

.btn-submit:disabled,
.btn-cancel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  margin-right: 0.25rem;
  vertical-align: middle;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: all 0.3s;
}

.modal-enter-from .modal-container {
  transform: scale(0.95) translateY(20px);
  opacity: 0;
}

.modal-leave-to .modal-container {
  transform: scale(0.95) translateY(20px);
  opacity: 0;
}

@media (max-width: 600px) {
  .publisher-tabs {
    grid-template-columns: 1fr;
  }
  .image-uploader,
  .video-uploader {
    grid-template-columns: repeat(2, 1fr);
  }
  .modal-container {
    border-radius: 16px;
    margin: 0.5rem;
  }
  .modal-header,
  .modal-body,
  .modal-footer {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}

/* 富文本编辑器样式 */
.rich-editor-toolbar {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-bottom: none;
  border-radius: 12px 12px 0 0;
}

.toolbar-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
  font-family: inherit;
}

.toolbar-btn:hover,
.toolbar-btn:focus {
  background: #e2e8f0;
  color: #f97316;
}

.toolbar-btn:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.2);
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background: #e2e8f0;
  margin: 0 0.25rem;
}

.rich-editor {
  width: 100%;
  min-height: 100px;
  max-height: 200px;
  padding: 1rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-top: none;
  border-radius: 0 0 12px 12px;
  font-size: 0.95rem;
  color: #1e293b;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
  box-sizing: border-box;
  transition: all 0.2s;
  overflow-y: auto;
}

.rich-editor:focus {
  outline: none;
  border-color: #f97316;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.rich-editor:empty:before {
  content: attr(placeholder);
  color: #94a3b8;
  pointer-events: none;
}

/* 图片拖拽上传样式 */
.upload-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-plus {
  position: absolute;
  bottom: -4px;
  right: -4px;
  font-size: 1rem;
  font-weight: bold;
  color: #f97316;
  background: #fff;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.image-uploader.drag-over {
  border-color: #f97316;
  background: #fff7ed;
}

.drag-overlay {
  position: absolute;
  inset: 0;
  background: rgba(249, 115, 22, 0.1);
  border: 2px dashed #f97316;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  z-index: 5;
}

.drag-icon {
  font-size: 2rem;
}

.drag-text {
  font-size: 0.85rem;
  font-weight: 500;
  color: #f97316;
}

/* 视频上传进度和缩略图样式 */
.video-thumbnail-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  aspect-ratio: 16/9;
}

.video-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 2rem;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.video-play-icon:hover {
  background: rgba(0, 0, 0, 0.7);
  transform: translate(-50%, -50%) scale(1.1);
}

.video-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 0.5rem;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  color: #fff;
}

.video-name {
  display: block;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.video-size {
  display: block;
  font-size: 0.65rem;
  opacity: 0.9;
}

.video-upload-progress {
  grid-column: 1 / -1;
  padding: 1rem;
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: center;
  justify-content: center;
}

.progress-bar {
  width: 100%;
  max-width: 200px;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #f97316, #ea580c);
  border-radius: 4px;
  transition: width 0.2s;
}

.progress-text {
  font-size: 0.8rem;
  color: #64748b;
}
</style>
