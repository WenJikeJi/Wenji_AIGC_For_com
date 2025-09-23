<template>
  <!-- 弹窗遮罩 -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click.self="$emit('close')">
    <!-- 弹窗主体 -->
    <div class="bg-white rounded-lg shadow-xl relative" style="width: 700px; max-height: 80vh; overflow-y: auto; padding: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 8px;">
      <!-- 关闭按钮 -->
      <button 
        @click="$emit('close')"
        class="absolute top-3 right-3 w-8 h-8 flex items-center justify-center text-gray-400 hover:text-gray-600 rounded-full transition-colors"
        type="button"
        title="关闭"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
      </button>
      
      <!-- 标题 -->
      <div class="mb-6">
        <h2 class="text-lg font-bold text-left mb-2" style="font-size: 18px; color: #333; font-weight: bold;">
          任务详情
        </h2>
        <p class="text-sm text-gray-600" style="font-size: 12px; color: #666;">
          查看和管理任务的详细信息
        </p>
      </div>

      <!-- 任务基本信息 -->
      <div class="mb-6">
        <div class="bg-gray-50 rounded-lg p-4">
          <div class="flex items-start justify-between mb-4">
            <!-- 左侧：平台和账号信息 -->
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 rounded-full bg-white flex items-center justify-center shadow-sm">
                <i :class="getPlatformIcon(task.platform)" class="text-lg text-gray-600"></i>
              </div>
              <div>
                <h3 class="text-lg font-medium text-gray-900">{{ task.title }}</h3>
                <p class="text-sm text-gray-500">{{ getPlatformName(task.platform) }} · {{ task.account }}</p>
              </div>
            </div>
            
            <!-- 右侧：状态标签 -->
            <span 
              class="px-3 py-1 text-sm font-medium rounded-full"
              :class="getTaskStatusClass(task.status)"
            >
              {{ getTaskStatusText(task.status) }}
            </span>
          </div>
          
          <!-- 时间信息 -->
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span class="text-gray-500">发布时间：</span>
              <span class="text-gray-900">{{ formatScheduleTime(task.scheduleTime) }}</span>
            </div>
            <div>
              <span class="text-gray-500">创建时间：</span>
              <span class="text-gray-900">{{ formatScheduleTime(task.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 发布内容预览 -->
      <div class="mb-6">
        <h4 class="text-md font-medium text-gray-900 mb-3">发布内容</h4>
        <div class="bg-white border border-gray-200 rounded-lg p-4">
          <!-- 文案内容 -->
          <div class="mb-4">
            <p class="text-sm text-gray-700 leading-relaxed">{{ task.content }}</p>
          </div>
          
          <!-- 媒体文件预览（如果有） -->
          <div v-if="task.media && task.media.length > 0" class="mb-4">
            <h5 class="text-sm font-medium text-gray-700 mb-2">媒体文件</h5>
            <div class="grid grid-cols-3 gap-2">
              <div 
                v-for="(media, index) in task.media"
                :key="index"
                class="relative aspect-square bg-gray-100 rounded-lg overflow-hidden cursor-pointer hover:opacity-80 transition-opacity"
                @click="previewMedia(media)"
              >
                <img 
                  v-if="media.type === 'image'"
                  :src="media.url" 
                  :alt="media.name"
                  class="w-full h-full object-cover"
                />
                <div 
                  v-else-if="media.type === 'video'"
                  class="w-full h-full flex items-center justify-center bg-gray-200"
                >
                  <i class="fas fa-play-circle text-2xl text-gray-500"></i>
                </div>
                <!-- 文件类型标识 -->
                <div class="absolute top-1 right-1 bg-black bg-opacity-50 text-white text-xs px-1 py-0.5 rounded">
                  {{ media.type === 'image' ? 'IMG' : 'VID' }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 标签（如果有） -->
          <div v-if="task.tags && task.tags.length > 0" class="flex flex-wrap gap-2">
            <span 
              v-for="tag in task.tags"
              :key="tag"
              class="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full"
            >
              #{{ tag }}
            </span>
          </div>
        </div>
      </div>

      <!-- 执行日志 -->
      <div v-if="task.logs && task.logs.length > 0" class="mb-6">
        <h4 class="text-md font-medium text-gray-900 mb-3">执行日志</h4>
        <div class="bg-gray-50 rounded-lg p-4 max-h-40 overflow-y-auto">
          <div 
            v-for="(log, index) in task.logs"
            :key="index"
            class="mb-2 last:mb-0"
          >
            <div class="flex items-start space-x-2">
              <span 
                class="inline-block w-2 h-2 rounded-full mt-2 flex-shrink-0"
                :class="getLogStatusColor(log.level)"
              ></span>
              <div class="flex-1">
                <div class="flex items-center justify-between">
                  <span class="text-xs text-gray-500">{{ formatLogTime(log.timestamp) }}</span>
                  <span 
                    class="text-xs px-2 py-0.5 rounded"
                    :class="getLogLevelClass(log.level)"
                  >
                    {{ log.level.toUpperCase() }}
                  </span>
                </div>
                <p class="text-sm text-gray-700 mt-1">{{ log.message }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 失败原因（仅失败状态显示） -->
      <div v-if="task.status === 'failed' && task.errorMessage" class="mb-6">
        <h4 class="text-md font-medium text-gray-900 mb-3">失败原因</h4>
        <div class="bg-red-50 border border-red-200 rounded-lg p-4">
          <div class="flex items-start space-x-2">
            <i class="fas fa-exclamation-triangle text-red-500 mt-0.5"></i>
            <div>
              <p class="text-sm text-red-800 font-medium">{{ task.errorType || '执行失败' }}</p>
              <p class="text-sm text-red-700 mt-1">{{ task.errorMessage }}</p>
              <p v-if="task.errorSolution" class="text-sm text-red-600 mt-2">
                <strong>建议解决方案：</strong>{{ task.errorSolution }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="flex justify-end space-x-3 pt-4 border-t border-gray-200">
        <!-- 立即执行 - 仅待执行状态显示 -->
        <button 
          v-if="task.status === 'pending'"
          @click="executeTask"
          class="px-4 py-2 bg-green-600 text-white text-sm rounded-lg hover:bg-green-700 transition-colors"
        >
          <i class="fas fa-play mr-2"></i>
          立即执行
        </button>
        
        <!-- 重新执行 - 仅失败状态显示 -->
        <button 
          v-if="task.status === 'failed'"
          @click="retryTask"
          class="px-4 py-2 bg-orange-600 text-white text-sm rounded-lg hover:bg-orange-700 transition-colors"
        >
          <i class="fas fa-redo mr-2"></i>
          重新执行
        </button>
        
        <!-- 编辑 - 待执行和失败状态显示 -->
        <button 
          v-if="task.status === 'pending' || task.status === 'failed'"
          @click="editTask"
          class="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors"
        >
          <i class="fas fa-edit mr-2"></i>
          编辑任务
        </button>
        
        <!-- 复制 - 所有状态都可复制 -->
        <button 
          @click="copyTask"
          class="px-4 py-2 bg-purple-600 text-white text-sm rounded-lg hover:bg-purple-700 transition-colors"
        >
          <i class="fas fa-copy mr-2"></i>
          复制任务
        </button>
        
        <!-- 删除 - 所有状态都可删除 -->
        <button 
          @click="deleteTask"
          class="px-4 py-2 bg-red-600 text-white text-sm rounded-lg hover:bg-red-700 transition-colors"
        >
          <i class="fas fa-trash mr-2"></i>
          删除任务
        </button>
        
        <!-- 关闭 -->
        <button 
          @click="$emit('close')"
          class="px-4 py-2 bg-gray-300 text-gray-700 text-sm rounded-lg hover:bg-gray-400 transition-colors"
        >
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { showNotification } from '@/utils/notification'

// Props
const props = defineProps({
  task: {
    type: Object,
    required: true
  }
})

// Emits
const emit = defineEmits(['close', 'execute', 'retry', 'edit', 'copy', 'delete'])

// 方法
const getPlatformIcon = (platform) => {
  const icons = {
    facebook: 'fab fa-facebook-f',
    instagram: 'fab fa-instagram',
    tiktok: 'fab fa-tiktok'
  }
  return icons[platform] || 'fas fa-globe'
}

const getPlatformName = (platform) => {
  const names = {
    facebook: 'Facebook',
    instagram: 'Instagram',
    tiktok: 'TikTok'
  }
  return names[platform] || platform
}

const getTaskStatusClass = (status) => {
  switch (status) {
    case 'pending':
      return 'bg-yellow-100 text-yellow-800'
    case 'running':
      return 'bg-blue-100 text-blue-800'
    case 'success':
      return 'bg-green-100 text-green-800'
    case 'failed':
      return 'bg-red-100 text-red-800'
    case 'cancelled':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getTaskStatusText = (status) => {
  switch (status) {
    case 'pending':
      return '待执行'
    case 'running':
      return '执行中'
    case 'success':
      return '成功'
    case 'failed':
      return '失败'
    case 'cancelled':
      return '已取消'
    default:
      return '未知'
  }
}

const getLogStatusColor = (level) => {
  switch (level) {
    case 'error':
      return 'bg-red-500'
    case 'warning':
      return 'bg-yellow-500'
    case 'info':
      return 'bg-blue-500'
    case 'success':
      return 'bg-green-500'
    default:
      return 'bg-gray-500'
  }
}

const getLogLevelClass = (level) => {
  switch (level) {
    case 'error':
      return 'bg-red-100 text-red-800'
    case 'warning':
      return 'bg-yellow-100 text-yellow-800'
    case 'info':
      return 'bg-blue-100 text-blue-800'
    case 'success':
      return 'bg-green-100 text-green-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const formatScheduleTime = (timeString) => {
  try {
    const date = new Date(timeString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    return timeString
  }
}

const formatLogTime = (timeString) => {
  try {
    const date = new Date(timeString)
    return date.toLocaleString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    return timeString
  }
}

const previewMedia = (media) => {
  showNotification('媒体预览功能开发中...', 'info')
  // 这里应该打开媒体预览弹窗
}

const executeTask = () => {
  emit('execute', props.task.id)
}

const retryTask = () => {
  emit('retry', props.task.id)
}

const editTask = () => {
  emit('edit', props.task.id)
}

const copyTask = () => {
  emit('copy', props.task.id)
}

const deleteTask = () => {
  emit('delete', props.task.id)
}
</script>

<style scoped>
/* 状态标签样式 */
.bg-yellow-100 {
  background-color: #fef3c7;
}

.text-yellow-800 {
  color: #92400e;
}

.bg-blue-100 {
  background-color: #dbeafe;
}

.text-blue-800 {
  color: #1e40af;
}

.bg-green-100 {
  background-color: #e8f5e9;
}

.text-green-800 {
  color: #2ecc71;
}

.bg-red-100 {
  background-color: #ffebee;
}

.text-red-800 {
  color: #e74c3c;
}

.bg-gray-100 {
  background-color: #f3f4f6;
}

.text-gray-800 {
  color: #374151;
}

/* 日志级别样式 */
.bg-red-50 {
  background-color: #fef2f2;
}

.border-red-200 {
  border-color: #fecaca;
}

.text-red-500 {
  color: #ef4444;
}

.text-red-600 {
  color: #dc2626;
}

.text-red-700 {
  color: #b91c1c;
}

.text-red-800 {
  color: #991b1b;
}
</style>