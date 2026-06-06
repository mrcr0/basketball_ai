import axios from '../utils/axios'

/**
 * 文件上传 API
 * 
 * 上传策略：
 * - 使用 XHR 而非 axios 以支持实时上传进度回调
 * - 图片/视频分别调用不同端点
 * - 单个文件上传失败不影响其他文件
 */

const UPLOAD_BASE = '/api/upload'

/**
 * 上传文件（带进度回调）
 * @param {File[]} files        - 文件列表
 * @param {'images'|'videos'} category - 文件分类
 * @param {Function} onProgress - 进度回调 (fileIndex, percent)
 * @returns {Promise<Array>}    - 上传结果数组 [{success, accessUrl, originalName, error}, ...]
 */
export function uploadFiles(files, category, onProgress) {
  return uploadSingleFile(files, 0, [], category, onProgress)
}

function uploadSingleFile(files, idx, results, category, onProgress) {
  return new Promise((resolve, reject) => {
    if (idx >= files.length) {
      resolve(results)
      return
    }

    const file = files[idx]
    const formData = new FormData()
    formData.append('files', file)

    const xhr = new XMLHttpRequest()
    const endpoint = category === 'images' ? `${UPLOAD_BASE}/images` : `${UPLOAD_BASE}/videos`

    xhr.open('POST', endpoint, true)
    // 注意：不要手动设置 Content-Type，浏览器会自动设置 multipart/form-data + boundary

    // 上传进度
    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable && onProgress) {
        const percent = Math.round((e.loaded / e.total) * 100)
        onProgress(idx, percent)
      }
    })

    xhr.addEventListener('load', () => {
      try {
        const response = JSON.parse(xhr.responseText)
        if (xhr.status === 200 && response.code === 200) {
          const dataItems = response.data || []
          dataItems.forEach(item => {
            results.push(item)
          })
          // 继续下一个文件
          uploadSingleFile(files, idx + 1, results, category, onProgress).then(resolve).catch(reject)
        } else {
          // 业务层错误
          results.push({
            originalName: file.name,
            success: false,
            error: response.message || '上传失败'
          })
          uploadSingleFile(files, idx + 1, results, category, onProgress).then(resolve).catch(reject)
        }
      } catch (err) {
        results.push({
          originalName: file.name,
          success: false,
          error: '响应解析失败'
        })
        uploadSingleFile(files, idx + 1, results, category, onProgress).then(resolve).catch(reject)
      }
    })

    xhr.addEventListener('error', () => {
      results.push({
        originalName: file.name,
        success: false,
        error: '网络错误，上传失败'
      })
      uploadSingleFile(files, idx + 1, results, category, onProgress).then(resolve).catch(reject)
    })

    xhr.addEventListener('abort', () => {
      reject(new Error('上传已取消'))
    })

    xhr.send(formData)
  })
}

/**
 * 上传单张图片（简化版，用于快速上传）
 */
export function uploadImage(file, onProgress) {
  return new Promise((resolve, reject) => {
    const formData = new FormData()
    formData.append('files', file)

    const xhr = new XMLHttpRequest()
    xhr.open('POST', `${UPLOAD_BASE}/images`, true)

    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable && onProgress) {
        onProgress(Math.round((e.loaded / e.total) * 100))
      }
    })

    xhr.addEventListener('load', () => {
      try {
        const response = JSON.parse(xhr.responseText)
        if (response.code === 200 && response.data && response.data.length > 0) {
          resolve(response.data[0])
        } else {
          reject(new Error(response.message || '上传失败'))
        }
      } catch (e) {
        reject(new Error('响应解析失败'))
      }
    })

    xhr.addEventListener('error', () => reject(new Error('网络错误')))
    xhr.send(formData)
  })
}

/**
 * 上传单个视频（简化版）
 */
export function uploadVideo(file, onProgress) {
  return new Promise((resolve, reject) => {
    const formData = new FormData()
    formData.append('files', file)

    const xhr = new XMLHttpRequest()
    xhr.open('POST', `${UPLOAD_BASE}/videos`, true)

    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable && onProgress) {
        onProgress(Math.round((e.loaded / e.total) * 100))
      }
    })

    xhr.addEventListener('load', () => {
      try {
        const response = JSON.parse(xhr.responseText)
        if (response.code === 200 && response.data && response.data.length > 0) {
          resolve(response.data[0])
        } else {
          reject(new Error(response.message || '上传失败'))
        }
      } catch (e) {
        reject(new Error('响应解析失败'))
      }
    })

    xhr.addEventListener('error', () => reject(new Error('网络错误')))
    xhr.send(formData)
  })
}

// ===== 格式验证工具 =====

const ALLOWED_IMAGES = ['image/jpeg', 'image/png', 'image/webp']
const ALLOWED_VIDEOS = ['video/mp4', 'video/x-msvideo', 'video/quicktime']
const IMAGE_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.webp']
const VIDEO_EXTENSIONS = ['.mp4', '.avi', '.mov']
const MAX_IMAGE_SIZE = 10 * 1024 * 1024   // 10MB
const MAX_VIDEO_SIZE = 200 * 1024 * 1024  // 200MB

/**
 * 验证图片文件
 * @param {File} file
 * @returns {string|null} 错误信息，null表示通过
 */
export function validateImage(file) {
  const ext = '.' + file.name.split('.').pop().toLowerCase()
  if (!IMAGE_EXTENSIONS.includes(ext)) {
    return '不支持的图片格式（仅允许 JPG/PNG/WEBP）'
  }
  if (!ALLOWED_IMAGES.includes(file.type)) {
    return '不支持的图片类型（仅允许 JPG/PNG/WEBP）'
  }
  if (file.size > MAX_IMAGE_SIZE) {
    return '图片过大（最大10MB，当前 ' + formatSize(file.size) + '）'
  }
  return null
}

/**
 * 验证视频文件
 * @param {File} file
 * @returns {string|null}
 */
export function validateVideo(file) {
  const ext = '.' + file.name.split('.').pop().toLowerCase()
  if (!VIDEO_EXTENSIONS.includes(ext)) {
    return '不支持的视频格式（仅允许 MP4/AVI/MOV）'
  }
  if (!ALLOWED_VIDEOS.includes(file.type)) {
    return '不支持的视频类型（仅允许 MP4/AVI/MOV）'
  }
  if (file.size > MAX_VIDEO_SIZE) {
    return '视频过大（最大200MB，当前 ' + formatSize(file.size) + '）'
  }
  return null
}

export function formatSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1048576).toFixed(1) + ' MB'
}
