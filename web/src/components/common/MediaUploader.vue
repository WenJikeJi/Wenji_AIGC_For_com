<template>
  <div class="media-uploader">
    <!-- 上传区域 -->
    <div 
      class="upload-area"
      :class="{ 
        'drag-over': isDragOver,
        'has-files': uploadedFiles.length > 0
      }"
      @dragover.prevent
      @dragenter.prevent="handleDragEnter"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleFileDrop"
    >
      <input 
        type="file" 
        ref="fileInput" 
        @change="handleFileUpload" 
        multiple 
        :accept="acceptedFormats"
        class="hidden"
      >
      
      <div class="upload-content">
        <div class="upload-icon">
          <i class="fas fa-cloud-upload-alt"></i>
        </div>
        <div class="upload-text">
          <p class="main-text">拖拽文件到此处或点击上传</p>
          <p class="sub-text">支持 {{ formatText }} 格式，单文件最大 {{ maxSizeMB }}MB</p>
        </div>
        <button 
          type="button"
          @click="$refs.fileInput.click()"
          class="upload-button"
        >
          <i class="fas fa-plus mr-2"></i>
          选择文件
        </button>
      </div>
    </div>

    <!-- 文件列表 -->
    <div v-if="uploadedFiles.length > 0" class="file-list">
      <div class="file-list-header">
        <h4>已选择文件 ({{ uploadedFiles.length }})</h4>
        <button 
          type="button"
          @click="clearAllFiles"
          class="clear-all-btn"
        >
          <i class="fas fa-trash mr-1"></i>
          清空全部
        </button>
      </div>
      
      <div class="file-grid">
        <div 
          v-for="(file, index) in uploadedFiles" 
          :key="file.id"
          class="file-item"
          :class="{ 'uploading': file.uploading, 'error': file.error }"
        >
          <!-- 预览区域 -->
          <div class="file-preview" @click="openPreview(file)">
            <img 
              v-if="file.type.startsWith('image/')"
              :src="file.preview"
              :alt="file.name"
              class="preview-image"
            />
            <div 
              v-else-if="file.type.startsWith('video/')"
              class="preview-video"
            >
              <video 
                :src="file.preview"
                class="preview-video-element"
                muted
              ></video>
              <div class="video-overlay">
                <i class="fas fa-play-circle"></i>
              </div>
            </div>
            <div v-else class="preview-unknown">
              <i class="fas fa-file"></i>
            </div>
            
            <!-- 文件类型标识 -->
            <div class="file-type-badge">
              {{ getFileTypeBadge(file.type) }}
            </div>
            
            <!-- 上传进度 -->
            <div v-if="file.uploading" class="upload-progress">
              <div class="progress-bar">
                <div 
                  class="progress-fill"
                  :style="{ width: file.progress + '%' }"
                ></div>
              </div>
              <span class="progress-text">{{ file.progress }}%</span>
            </div>
          </div>
          
          <!-- 文件信息 -->
          <div class="file-info">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
            <div class="file-size">{{ formatFileSize(file.size) }}</div>
            <div v-if="file.error" class="file-error">{{ file.error }}</div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="file-actions">
            <button 
              type="button"
              @click="openPreview(file)"
              class="action-btn preview-btn"
              title="预览"
            >
              <i class="fas fa-eye"></i>
            </button>
            <button 
              type="button"
              @click="removeFile(index)"
              class="action-btn remove-btn"
              title="删除"
            >
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="uploadError" class="error-message">
      <i class="fas fa-exclamation-triangle mr-2"></i>
      {{ uploadError }}
    </div>

    <!-- 预览弹窗 -->
    <div v-if="showPreview" class="preview-modal" @click="closePreview">
      <div class="preview-content" @click.stop>
        <button 
          type="button"
          @click="closePreview"
          class="preview-close"
        >
          <i class="fas fa-times"></i>
        </button>
        
        <div class="preview-media">
          <img 
            v-if="previewFile && previewFile.type.startsWith('image/')"
            :src="previewFile.preview"
            :alt="previewFile.name"
            class="preview-full-image"
          />
          <video 
            v-else-if="previewFile && previewFile.type.startsWith('video/')"
            :src="previewFile.preview"
            controls
            class="preview-full-video"
          ></video>
        </div>
        
        <div class="preview-info">
          <h3>{{ previewFile?.name }}</h3>
          <p>大小: {{ formatFileSize(previewFile?.size) }}</p>
          <p>类型: {{ previewFile?.type }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

// Props
const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxFiles: {
    type: Number,
    default: 10
  },
  maxSize: {
    type: Number,
    default: 100 * 1024 * 1024 // 100MB
  },
  acceptedTypes: {
    type: Array,
    default: () => ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'video/mp4', 'video/webm', 'video/mov']
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'file-added', 'file-removed', 'error'])

// 响应式数据
const isDragOver = ref(false)
const uploadedFiles = ref([...props.modelValue])
const uploadError = ref('')
const showPreview = ref(false)
const previewFile = ref(null)
const dragCounter = ref(0)

// 计算属性
const acceptedFormats = computed(() => {
  return props.acceptedTypes.join(',')
})

const formatText = computed(() => {
  const imageFormats = props.acceptedTypes.filter(type => type.startsWith('image/')).map(type => type.split('/')[1].toUpperCase())
  const videoFormats = props.acceptedTypes.filter(type => type.startsWith('video/')).map(type => type.split('/')[1].toUpperCase())
  
  const formats = []
  if (imageFormats.length > 0) formats.push(`图片(${imageFormats.join(', ')})`)
  if (videoFormats.length > 0) formats.push(`视频(${videoFormats.join(', ')})`)
  
  return formats.join('、')
})

const maxSizeMB = computed(() => {
  return Math.round(props.maxSize / 1024 / 1024)
})

// 方法
const handleDragEnter = (e) => {
  dragCounter.value++
  isDragOver.value = true
}

const handleDragLeave = (e) => {
  dragCounter.value--
  if (dragCounter.value === 0) {
    isDragOver.value = false
  }
}

const handleFileDrop = (event) => {
  isDragOver.value = false
  dragCounter.value = 0
  const files = Array.from(event.dataTransfer.files)
  processFiles(files)
}

const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  processFiles(files)
  // 清空input值，允许重复选择同一文件
  event.target.value = ''
}

const processFiles = (files) => {
  uploadError.value = ''
  
  // 检查文件数量限制
  if (uploadedFiles.value.length + files.length > props.maxFiles) {
    uploadError.value = `最多只能上传 ${props.maxFiles} 个文件`
    return
  }
  
  files.forEach(file => {
    // 检查文件大小
    if (file.size > props.maxSize) {
      uploadError.value = `文件 "${file.name}" 大小超过 ${maxSizeMB.value}MB 限制`
      return
    }
    
    // 检查文件类型
    if (!props.acceptedTypes.includes(file.type)) {
      uploadError.value = `文件 "${file.name}" 格式不支持`
      return
    }
    
    // 检查是否已存在相同文件
    const existingFile = uploadedFiles.value.find(f => 
      f.name === file.name && f.size === file.size
    )
    if (existingFile) {
      uploadError.value = `文件 "${file.name}" 已存在`
      return
    }
    
    // 创建文件对象
    const fileObj = {
      id: Date.now() + Math.random(),
      file: file,
      name: file.name,
      size: file.size,
      type: file.type,
      preview: null,
      uploading: false,
      progress: 0,
      error: null
    }
    
    // 创建预览
    const reader = new FileReader()
    reader.onload = (e) => {
      fileObj.preview = e.target.result
    }
    reader.readAsDataURL(file)
    
    uploadedFiles.value.push(fileObj)
    emit('file-added', fileObj)
  })
  
  updateModelValue()
}

const removeFile = (index) => {
  const removedFile = uploadedFiles.value[index]
  uploadedFiles.value.splice(index, 1)
  emit('file-removed', removedFile)
  updateModelValue()
}

const clearAllFiles = () => {
  uploadedFiles.value = []
  updateModelValue()
}

const openPreview = (file) => {
  if (file.type.startsWith('image/') || file.type.startsWith('video/')) {
    previewFile.value = file
    showPreview.value = true
  }
}

const closePreview = () => {
  showPreview.value = false
  previewFile.value = null
}

const getFileTypeBadge = (type) => {
  if (type.startsWith('image/')) return 'IMG'
  if (type.startsWith('video/')) return 'VID'
  return 'FILE'
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const updateModelValue = () => {
  emit('update:modelValue', uploadedFiles.value)
}

// 监听props变化
watch(() => props.modelValue, (newValue) => {
  uploadedFiles.value = [...newValue]
}, { deep: true })
</script>

<style scoped>
.media-uploader {
  width: 100%;
}

.upload-area {
  border: 2px dashed #e0e0e0;
  border-radius: 12px;
  padding: 32px 24px;
  text-align: center;
  background: #fafbfc;
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-area:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.upload-area.drag-over {
  border-color: #667eea;
  background: #f0f4ff;
  transform: scale(1.02);
}

.upload-area.has-files {
  padding: 20px;
  margin-bottom: 20px;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  font-size: 48px;
  color: #667eea;
  opacity: 0.7;
}

.upload-text .main-text {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
}

.upload-text .sub-text {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.upload-button {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.file-list {
  margin-top: 20px;
}

.file-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.file-list-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.clear-all-btn {
  padding: 6px 12px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.clear-all-btn:hover {
  background: #ff4757;
  color: white;
  border-color: #ff4757;
}

.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.file-item {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
}

.file-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.file-item.uploading {
  opacity: 0.7;
}

.file-item.error {
  border-color: #ff4757;
}

.file-preview {
  position: relative;
  aspect-ratio: 16/9;
  background: #f5f5f5;
  cursor: pointer;
  overflow: hidden;
}

.preview-image,
.preview-video-element {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-video {
  position: relative;
  width: 100%;
  height: 100%;
}

.video-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 32px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.preview-unknown {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: 32px;
  color: #999;
}

.file-type-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
}

.upload-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: #667eea;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
}

.file-info {
  padding: 12px;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-size: 12px;
  color: #666;
}

.file-error {
  font-size: 12px;
  color: #ff4757;
  margin-top: 4px;
}

.file-actions {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.file-item:hover .file-actions {
  opacity: 1;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  transition: all 0.2s ease;
}

.preview-btn {
  background: rgba(102, 126, 234, 0.9);
  color: white;
}

.preview-btn:hover {
  background: #667eea;
}

.remove-btn {
  background: rgba(255, 71, 87, 0.9);
  color: white;
}

.remove-btn:hover {
  background: #ff4757;
}

.error-message {
  margin-top: 12px;
  padding: 12px;
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 8px;
  color: #c53030;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.preview-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.preview-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

.preview-media {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.preview-full-image,
.preview-full-video {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-info {
  padding: 20px;
  border-top: 1px solid #e0e0e0;
}

.preview-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.preview-info p {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .file-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
  
  .upload-area {
    padding: 24px 16px;
  }
  
  .upload-icon {
    font-size: 36px;
  }
}
</style>