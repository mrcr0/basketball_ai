<template>
  <div class="comment-item" :class="{ 'reply-item': isReply }">
    <div class="comment-avatar">{{ comment.user?.avatarUrl || '👤' }}</div>
    <div class="comment-body">
      <div class="comment-header">
        <span class="comment-user">👤 {{ comment.user?.nickname || '用户' }}</span>
        <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
      </div>
      <div v-if="!isEditing" class="comment-content">{{ comment.content }}</div>
      <div v-else class="edit-container">
        <textarea 
          v-model="editContent" 
          class="edit-input"
          placeholder="编辑评论..."
          rows="3"
        ></textarea>
        <div class="edit-actions">
          <button @click="cancelEdit" class="cancel-btn">取消</button>
          <button @click="saveEdit" class="save-btn">保存</button>
        </div>
      </div>
      <div class="comment-actions">
        <button class="reply-btn" @click="toggleReply">
          {{ showReplyInput ? '取消回复' : '回复' }}
        </button>
        <button v-if="canEdit" class="edit-btn" @click="startEdit">
          编辑
        </button>
        <button v-if="canDelete" class="delete-btn" @click="handleDelete">
          删除
        </button>
      </div>
      <div v-if="showReplyInput" class="reply-input-container">
        <textarea 
          v-model="replyContent" 
          class="reply-input"
          :placeholder="`回复 ${comment.user?.nickname || '用户'}...`"
          rows="2"
          @keyup.ctrl.enter="submitReply"
        ></textarea>
        <div class="reply-actions">
          <button @click="submitReply" class="submit-reply-btn">发送</button>
        </div>
      </div>
      <div v-if="replies.length > 0" class="replies-list">
        <CommentItem 
          v-for="reply in replies" 
          :key="reply.id"
          :comment="reply"
          :dynamic-id="dynamicId"
          :all-comments="allComments"
          :current-user-id="currentUserId"
          :depth="depth + 1"
          @reply="handleReply"
          @edit="handleEdit"
          @delete="handleDelete"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addComment, updateComment, deleteComment } from '../api/dynamic'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  dynamicId: {
    type: Number,
    required: true
  },
  allComments: {
    type: Array,
    default: () => []
  },
  currentUserId: {
    type: Number,
    default: 0
  },
  depth: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['reply', 'edit', 'delete'])

const isReply = computed(() => props.depth > 0)
const showReplyInput = ref(false)
const replyContent = ref('')
const isEditing = ref(false)
const editContent = ref('')

const replies = computed(() => {
  return props.allComments.filter(c => c.parentId === props.comment.id)
})

const canEdit = computed(() => {
  return props.comment.userId === props.currentUserId || props.currentUserId === 0
})

const canDelete = computed(() => {
  return props.comment.userId === props.currentUserId || props.currentUserId === 0
})

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return date.toLocaleDateString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }
}

const toggleReply = () => {
  showReplyInput.value = !showReplyInput.value
  if (showReplyInput.value) {
    replyContent.value = ''
  }
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    const response = await addComment(props.dynamicId, replyContent.value, props.comment.id)
    if (response.code === 200) {
      ElMessage.success('回复成功')
      replyContent.value = ''
      showReplyInput.value = false
      emit('reply', { parentId: props.comment.id, content: replyContent.value })
    }
  } catch (error) {
    console.error('回复失败', error)
    ElMessage.error('回复失败，请稍后重试')
  }
}

const startEdit = () => {
  isEditing.value = true
  editContent.value = props.comment.content
}

const cancelEdit = () => {
  isEditing.value = false
  editContent.value = ''
}

const saveEdit = async () => {
  if (!editContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    const response = await updateComment(props.comment.id, editContent.value)
    if (response.code === 200) {
      ElMessage.success('编辑成功')
      isEditing.value = false
      emit('edit', { commentId: props.comment.id, content: editContent.value })
    }
  } catch (error) {
    console.error('编辑失败', error)
    ElMessage.error('编辑失败，请稍后重试')
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await deleteComment(props.comment.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      emit('delete', props.comment.id)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
.comment-item {
  display: flex;
  gap: 0.75rem;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  margin-bottom: 0.75rem;
}

.reply-item {
  margin-left: 3rem;
  background: rgba(0, 0, 0, 0.1);
  padding: 0.75rem;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.comment-user {
  font-weight: bold;
  color: #fff;
  font-size: 0.9rem;
}

.comment-time {
  font-size: 0.75rem;
  color: #6b7280;
}

.comment-content {
  color: #e5e7eb;
  font-size: 0.95rem;
  line-height: 1.6;
  white-space: pre-wrap;
}

.edit-container {
  margin: 0.5rem 0;
}

.edit-input {
  width: 100%;
  padding: 0.75rem;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #fff;
  font-size: 0.95rem;
  resize: vertical;
  font-family: inherit;
}

.edit-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.edit-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  justify-content: flex-end;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 6px;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.save-btn {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.comment-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.reply-btn,
.edit-btn,
.delete-btn {
  background: transparent;
  border: none;
  color: #3b82f6;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0.25rem 0;
  transition: all 0.2s ease;
}

.reply-btn:hover,
.edit-btn:hover {
  text-decoration: underline;
}

.delete-btn {
  color: #ef4444;
}

.delete-btn:hover {
  color: #dc2626;
}

.reply-input-container {
  margin-top: 0.75rem;
  padding: 0.75rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
}

.reply-input {
  width: 100%;
  padding: 0.75rem;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #fff;
  font-size: 0.9rem;
  resize: vertical;
  font-family: inherit;
}

.reply-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.reply-input::placeholder {
  color: #6b7280;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 0.5rem;
}

.submit-reply-btn {
  padding: 0.5rem 1.25rem;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-reply-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.replies-list {
  margin-top: 0.75rem;
}

@media (max-width: 768px) {
  .reply-item {
    margin-left: 1.5rem;
  }

  .comment-avatar {
    width: 32px;
    height: 32px;
    font-size: 0.9rem;
  }

  .comment-user {
    font-size: 0.85rem;
  }

  .comment-content {
    font-size: 0.9rem;
  }

  .reply-btn,
  .edit-btn,
  .delete-btn {
    font-size: 0.8rem;
  }
}
</style>