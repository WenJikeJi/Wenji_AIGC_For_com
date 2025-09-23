<template>
  <div class="w-full p-4">
    <div class="mb-4">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-2xl font-bold text-gray-800 mb-2">任务管理</h2>
          <p class="text-gray-600 text-base">管理您的定时发帖任务</p>
        </div>
        <div class="flex items-center space-x-3">
          <!-- 状态筛选 -->
          <select 
            v-model="statusFilter"
            @change="fetchTasks"
            class="px-3 py-2 border border-gray-300 bg-white text-gray-700 text-sm rounded-lg focus:ring-purple-500 focus:border-purple-500"
          >
            <option value="">全部状态</option>
            <option value="PENDING">待执行</option>
            <option value="RUNNING">执行中</option>
            <option value="SUCCESS">成功</option>
            <option value="FAILED">失败</option>
            <option value="CANCELLED">已取消</option>
          </select>
          
          <!-- 刷新按钮 -->
          <button 
            @click="fetchTasks"
            :disabled="isLoading"
            class="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 flex items-center"
          >
            <i :class="['fas', 'fa-sync-alt', 'mr-2', { 'fa-spin': isLoading }]"></i>
            刷新
          </button>
          
          <!-- 关闭按钮 -->
          <button 
            @click="closeTaskManagement"
            class="px-4 py-2 bg-gray-300 text-gray-700 text-sm rounded-lg hover:bg-gray-400 transition-colors flex items-center"
          >
            <i class="fas fa-times mr-2"></i>
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-5 gap-4 mb-6">
      <!-- 待执行任务 -->
      <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 transform hover:-translate-y-1 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/20 group">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
              <i class="fas fa-clock text-blue-600 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-600 font-medium mb-1">待执行</p>
              <p class="text-2xl font-bold text-gray-800">{{ taskStats.pending || 0 }}</p>
            </div>
          </div>
          <div class="text-right">
            <div class="w-8 h-8 bg-blue-50 rounded-full flex items-center justify-center">
              <div class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
            </div>
          </div>
        </div>
        <div class="mt-3 pt-3 border-t border-gray-200">
          <div class="flex items-center text-xs text-blue-600">
            <i class="fas fa-arrow-up mr-1"></i>
            <span>等待执行中</span>
          </div>
        </div>
      </div>
      
      <!-- 执行中任务 -->
      <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 transform hover:-translate-y-1 transition-all duration-300 hover:shadow-lg hover:shadow-yellow-500/20 group">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
              <i class="fas fa-spinner text-yellow-600 text-xl animate-spin"></i>
            </div>
            <div>
              <p class="text-sm text-gray-600 font-medium mb-1">执行中</p>
              <p class="text-2xl font-bold text-gray-800">{{ taskStats.running || 0 }}</p>
            </div>
          </div>
          <div class="text-right">
            <div class="w-8 h-8 bg-yellow-50 rounded-full flex items-center justify-center">
              <div class="w-2 h-2 bg-yellow-500 rounded-full animate-bounce"></div>
            </div>
          </div>
        </div>
        <div class="mt-3 pt-3 border-t border-gray-200">
          <div class="flex items-center text-xs text-yellow-600">
            <i class="fas fa-play mr-1"></i>
            <span>正在处理中</span>
          </div>
        </div>
      </div>
      
      <!-- 成功任务 -->
      <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 transform hover:-translate-y-1 transition-all duration-300 hover:shadow-lg hover:shadow-green-500/20 group">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
              <i class="fas fa-check-circle text-green-600 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-600 font-medium mb-1">成功</p>
              <p class="text-2xl font-bold text-green-600">{{ taskStats.success || 0 }}</p>
            </div>
          </div>
          <div class="text-right">
            <div class="w-8 h-8 bg-green-50 rounded-full flex items-center justify-center">
              <i class="fas fa-check text-green-600 text-xs"></i>
            </div>
          </div>
        </div>
        <div class="mt-3 pt-3 border-t border-gray-200">
          <div class="flex items-center text-xs text-green-600">
            <i class="fas fa-thumbs-up mr-1"></i>
            <span>执行完成</span>
          </div>
        </div>
      </div>
      
      <!-- 失败任务 -->
      <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 transform hover:-translate-y-1 transition-all duration-300 hover:shadow-lg hover:shadow-red-500/20 group">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
              <i class="fas fa-times-circle text-red-600 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-600 font-medium mb-1">失败</p>
              <p class="text-2xl font-bold text-red-600">{{ taskStats.failed || 0 }}</p>
            </div>
          </div>
          <div class="text-right">
            <div class="w-8 h-8 bg-red-50 rounded-full flex items-center justify-center">
              <i class="fas fa-exclamation text-red-600 text-xs"></i>
            </div>
          </div>
        </div>
        <div class="mt-3 pt-3 border-t border-gray-200">
          <div class="flex items-center text-xs text-red-600">
            <i class="fas fa-exclamation-triangle mr-1"></i>
            <span>需要处理</span>
          </div>
        </div>
      </div>
      
      <!-- 已取消任务 -->
      <div class="bg-gray-50 p-4 rounded-xl border border-gray-200 transform hover:-translate-y-1 transition-all duration-300 hover:shadow-lg hover:shadow-gray-500/20 group">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
              <i class="fas fa-ban text-gray-600 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-600 font-medium mb-1">已取消</p>
              <p class="text-2xl font-bold text-gray-800">{{ taskStats.cancelled || 0 }}</p>
            </div>
          </div>
          <div class="text-right">
            <div class="w-8 h-8 bg-gray-50 rounded-full flex items-center justify-center">
              <i class="fas fa-minus text-gray-600 text-xs"></i>
            </div>
          </div>
        </div>
        <div class="mt-3 pt-3 border-t border-gray-200">
          <div class="flex items-center text-xs text-gray-600">
            <i class="fas fa-stop mr-1"></i>
            <span>已停止</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 任务列表 -->
    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden shadow-sm">
      <div class="px-3 py-2 bg-gray-50 border-b border-gray-200">
        <h3 class="text-sm font-medium text-gray-800">任务列表</h3>
      </div>
      
      <div v-if="isLoading" class="p-8 text-center">
        <div class="flex flex-col items-center">
          <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
            <i class="fas fa-spinner fa-spin text-2xl text-blue-600"></i>
          </div>
          <p class="text-gray-700 text-sm font-medium mb-2">正在加载任务数据</p>
          <p class="text-gray-500 text-xs">请稍候...</p>
        </div>
      </div>
      
      <div v-else-if="tasks.length === 0" class="p-12 text-center">
        <div class="flex flex-col items-center">
          <!-- 空状态图标 -->
          <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
            <i class="fas fa-tasks text-3xl text-gray-500"></i>
          </div>
          
          <!-- 空状态标题 -->
          <h3 class="text-lg font-semibold text-gray-800 mb-3">暂无任务</h3>
          <p class="text-gray-600 text-sm mb-6 max-w-sm">
            您还没有创建任何定时发布任务。开始创建您的第一个任务，让内容自动发布到社交媒体平台。
          </p>
          
          <!-- 引导操作 -->
          <div class="flex flex-col sm:flex-row gap-3">
            <button 
              @click="$emit('create-task')"
              class="px-6 py-3 bg-gradient-to-r from-purple-600 to-blue-600 text-white text-sm font-medium rounded-lg hover:from-purple-700 hover:to-blue-700 transition-all duration-200 transform hover:-translate-y-0.5 hover:shadow-lg hover:shadow-purple-500/25 flex items-center"
            >
              <i class="fas fa-plus mr-2"></i>
              创建新任务
            </button>
            <button 
              @click="$emit('import-tasks')"
              class="px-6 py-3 bg-gray-200 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-300 transition-all duration-200 transform hover:-translate-y-0.5 hover:shadow-lg hover:shadow-gray-500/25 flex items-center"
            >
              <i class="fas fa-upload mr-2"></i>
              批量导入
            </button>
          </div>
          
          <!-- 帮助提示 -->
          <div class="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg max-w-md">
            <div class="flex items-start">
              <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center mr-3 flex-shrink-0">
                <i class="fas fa-lightbulb text-blue-600 text-sm"></i>
              </div>
              <div>
                <h4 class="text-sm font-medium text-blue-700 mb-1">小贴士</h4>
                <p class="text-xs text-blue-600">
                  您可以设置定时发布任务，系统会在指定时间自动将内容发布到选定的社交媒体平台。
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="divide-y divide-gray-200">
        <div 
          v-for="task in tasks" 
          :key="task.id"
          class="p-3 hover:bg-gray-50 transition-colors"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <!-- 任务基本信息 -->
              <div class="flex items-center mb-1">
                <span 
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium mr-2"
                  :class="getStatusClass(task.status)"
                >
                  <i :class="getStatusIcon(task.status)" class="mr-1"></i>
                  {{ getStatusText(task.status) }}
                </span>
                <span class="text-xs text-gray-500">任务 #{{ task.taskNumber }}</span>
              </div>
              
              <!-- 任务内容 -->
              <h4 class="text-sm font-medium text-gray-800 mb-1">
                {{ task.postTitle || '无标题' }}
              </h4>
              <p class="text-gray-600 text-xs mb-2 line-clamp-2">{{ task.postContent }}</p>
              
              <!-- 任务详情 -->
              <div class="grid grid-cols-1 md:grid-cols-3 gap-2 text-xs text-gray-500">
                <div class="flex items-center">
                  <i class="fas fa-calendar-alt mr-1 text-blue-500"></i>
                  <span>{{ formatDateTime(task.scheduleTime) }}</span>
                </div>
                <div class="flex items-center">
                  <i class="fas fa-share-alt mr-1 text-purple-500"></i>
                  <span>{{ getPlatformText(task.platformType) }}</span>
                </div>
                <div class="flex items-center">
                  <i class="fas fa-user mr-1 text-green-500"></i>
                  <span>{{ task.homepageName || '未知主页' }}</span>
                </div>
              </div>
              
              <!-- 执行信息 -->
              <div v-if="task.executeTime || task.errorMessage" class="mt-2 pt-2 border-t border-gray-200">
                <div v-if="task.executeTime" class="text-xs text-gray-500 mb-1">
                  <i class="fas fa-clock mr-1 text-blue-500"></i>
                  执行时间: {{ formatDateTime(task.executeTime) }}
                </div>
                <div v-if="task.errorMessage" class="text-xs text-red-600">
                  <i class="fas fa-exclamation-triangle mr-1"></i>
                  错误信息: {{ task.errorMessage }}
                </div>
              </div>
            </div>
            
            <!-- 操作按钮 -->
            <div class="flex items-center space-x-1 ml-2">
              <!-- 重试按钮 -->
              <button 
                v-if="task.status === 'FAILED'"
                @click="retryTask(task.id)"
                :disabled="isRetrying"
                class="px-2 py-1 bg-yellow-500 text-white text-xs rounded hover:bg-yellow-600 transition-colors disabled:opacity-50 flex items-center shadow-sm"
              >
                <i :class="['fas', 'mr-1', isRetrying ? 'fa-spinner fa-spin' : 'fa-redo']"></i>
                重试
              </button>
              
              <!-- 取消按钮 -->
              <button 
                v-if="task.status === 'PENDING'"
                @click="cancelTask(task.id)"
                :disabled="isCancelling"
                class="px-2 py-1 bg-red-500 text-white text-xs rounded hover:bg-red-600 transition-colors disabled:opacity-50 flex items-center shadow-sm"
              >
                <i :class="['fas', 'mr-1', isCancelling ? 'fa-spinner fa-spin' : 'fa-times']"></i>
                取消
              </button>
              
              <!-- 查看详情按钮 -->
              <button 
                @click="viewTaskDetail(task)"
                class="px-2 py-1 bg-gray-500 text-white text-xs rounded hover:bg-gray-600 transition-colors flex items-center shadow-sm"
              >
                <i class="fas fa-eye mr-1"></i>
                详情
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="mt-3 flex justify-center">
      <nav class="flex items-center space-x-1">
        <button 
          @click="changePage(currentPage - 1)"
          :disabled="currentPage <= 1"
          class="px-2 py-1 border border-gray-300 bg-white text-gray-700 text-xs rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <span class="px-3 py-1 text-xs text-gray-600">
          第 {{ currentPage }} 页，共 {{ totalPages }} 页
        </span>
        
        <button 
          @click="changePage(currentPage + 1)"
          :disabled="currentPage >= totalPages"
          class="px-2 py-1 border border-gray-300 bg-white text-gray-700 text-xs rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </nav>
    </div>

    <!-- 任务详情模态框 -->
    <div v-if="showDetailModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg max-w-xl w-full mx-4 max-h-[80vh] overflow-y-auto border border-gray-200 shadow-xl">
        <div class="p-4">
          <div class="flex justify-between items-center mb-3">
            <h3 class="text-lg font-bold text-gray-800">任务详情</h3>
            <button 
              @click="showDetailModal = false"
              class="text-gray-500 hover:text-gray-700 transition-colors"
            >
              <i class="fas fa-times text-lg"></i>
            </button>
          </div>
          
          <div v-if="selectedTask" class="space-y-3">
            <!-- 基本信息 -->
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-xs font-medium text-gray-600 mb-1">任务编号</label>
                <p class="text-xs text-gray-800">{{ selectedTask.taskNumber }}</p>
              </div>
              <div>
                <label class="block text-xs font-medium text-gray-600 mb-1">状态</label>
                <span 
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium"
                  :class="getStatusClass(selectedTask.status)"
                >
                  <i :class="getStatusIcon(selectedTask.status)" class="mr-1"></i>
                  {{ getStatusText(selectedTask.status) }}
                </span>
              </div>
            </div>
            
            <!-- 内容信息 -->
            <div>
              <label class="block text-xs font-medium text-gray-600 mb-1">帖子标题</label>
              <p class="text-xs text-gray-800">{{ selectedTask.postTitle || '无标题' }}</p>
            </div>
            
            <div>
              <label class="block text-xs font-medium text-gray-600 mb-1">帖子内容</label>
              <p class="text-xs text-gray-800 whitespace-pre-wrap bg-gray-50 p-2 rounded border">{{ selectedTask.postContent }}</p>
            </div>
            
            <!-- 媒体文件 -->
            <div v-if="selectedTask.mediaUrls && selectedTask.mediaUrls.length > 0">
              <label class="block text-xs font-medium text-gray-600 mb-1">媒体文件</label>
              <div class="grid grid-cols-3 gap-1">
                <div v-for="(url, index) in selectedTask.mediaUrls" :key="index" class="aspect-square bg-gray-100 rounded overflow-hidden border">
                  <img :src="url" :alt="'媒体 ' + (index + 1)" class="w-full h-full object-cover">
                </div>
              </div>
            </div>
            
            <!-- 时间信息 -->
            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-xs font-medium text-gray-600 mb-1">计划时间</label>
                <p class="text-xs text-gray-800">{{ formatDateTime(selectedTask.scheduleTime) }}</p>
              </div>
              <div v-if="selectedTask.executeTime">
                <label class="block text-xs font-medium text-gray-600 mb-1">执行时间</label>
                <p class="text-xs text-gray-800">{{ formatDateTime(selectedTask.executeTime) }}</p>
              </div>
            </div>
            
            <!-- 错误信息 -->
            <div v-if="selectedTask.errorMessage">
              <label class="block text-xs font-medium text-gray-600 mb-1">错误信息</label>
              <p class="text-xs text-red-600 bg-red-50 p-2 rounded border border-red-200">{{ selectedTask.errorMessage }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { showNotification } from '../../utils/notification.js'
import { socialMediaAPI } from '../../utils/api.js'

export default {
  name: 'TaskManagement',
  emits: ['close'],
  setup(props, { emit }) {
    // 响应式数据
    const tasks = ref([])
    const taskStats = reactive({
      pending: 0,
      running: 0,
      success: 0,
      failed: 0,
      cancelled: 0
    })
    const isLoading = ref(false)
    const isRetrying = ref(false)
    const isCancelling = ref(false)
    const statusFilter = ref('')
    const currentPage = ref(1)
    const totalPages = ref(1)
    const pageSize = 10
    
    // 模态框相关
    const showDetailModal = ref(false)
    const selectedTask = ref(null)

    // 获取任务列表
    const fetchTasks = async () => {
      isLoading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: 20
        }
        
        // 添加状态筛选
        if (statusFilter.value) {
          // 将前端状态映射到后端状态码
          const statusMap = {
            'PENDING': 0,
            'RUNNING': 1,
            'SUCCESS': 2,
            'FAILED': 2,
            'CANCELLED': 3
          }
          params.taskStatus = statusMap[statusFilter.value]
        }
        
        const response = await socialMediaAPI.getScheduledTasks(params)
        if (response.code === 200) {
          tasks.value = response.data.content || []
          totalPages.value = response.data.totalPages || 1
          updateTaskStats()
        } else {
          throw new Error(response.message || '获取任务列表失败')
        }
      } catch (error) {
        console.error('获取任务列表失败:', error)
        showNotification(error.message || '获取任务列表失败', 'error')
        tasks.value = []
      } finally {
        isLoading.value = false
      }
    }

    // 更新任务统计
    const updateTaskStats = () => {
      const stats = {
        pending: 0,
        running: 0,
        success: 0,
        failed: 0,
        cancelled: 0
      }
      
      tasks.value.forEach(task => {
        const status = task.status.toLowerCase()
        if (stats.hasOwnProperty(status)) {
          stats[status]++
        }
      })
      
      Object.assign(taskStats, stats)
    }

    // 重试任务
    const retryTask = async (taskId) => {
      isRetrying.value = true
      try {
        const response = await socialMediaAPI.retryTask(taskId)
        if (response.code === 200) {
          showNotification('任务重试成功', 'success')
          fetchTasks()
        } else {
          throw new Error(response.message || '重试失败')
        }
      } catch (error) {
        console.error('重试任务失败:', error)
        showNotification(error.message || '重试失败', 'error')
      } finally {
        isRetrying.value = false
      }
    }

    // 取消任务
    const cancelTask = async (taskId) => {
      isCancelling.value = true
      try {
        const response = await socialMediaAPI.cancelScheduledTask(taskId)
        if (response.code === 200) {
          showNotification('任务已取消', 'success')
          fetchTasks()
        } else {
          throw new Error(response.message || '取消失败')
        }
      } catch (error) {
        console.error('取消任务失败:', error)
        showNotification(error.message || '取消失败', 'error')
      } finally {
        isCancelling.value = false
      }
    }

    // 查看任务详情
    const viewTaskDetail = (task) => {
      selectedTask.value = task
      showDetailModal.value = true
    }

    // 切换页面
    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
        fetchTasks()
      }
    }

    // 获取状态样式类
    const getStatusClass = (status) => {
      const classes = {
        'PENDING': 'bg-blue-100 text-blue-800',
        'RUNNING': 'bg-yellow-100 text-yellow-800',
        'SUCCESS': 'bg-green-100 text-green-800',
        'FAILED': 'bg-red-100 text-red-800',
        'CANCELLED': 'bg-gray-100 text-gray-800'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }

    // 获取状态图标
    const getStatusIcon = (status) => {
      const icons = {
        'PENDING': 'fas fa-clock',
        'RUNNING': 'fas fa-spinner fa-spin',
        'SUCCESS': 'fas fa-check-circle',
        'FAILED': 'fas fa-times-circle',
        'CANCELLED': 'fas fa-ban'
      }
      return icons[status] || 'fas fa-question-circle'
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const texts = {
        'PENDING': '待执行',
        'RUNNING': '执行中',
        'SUCCESS': '成功',
        'FAILED': '失败',
        'CANCELLED': '已取消'
      }
      return texts[status] || '未知'
    }

    // 获取平台文本
    const getPlatformText = (platformType) => {
      const platforms = {
        1: 'Facebook',
        2: 'Instagram'
      }
      return platforms[platformType] || '未知平台'
    }

    // 格式化日期时间
    const formatDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return '-'
      const date = new Date(dateTimeStr)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }

    // 关闭任务管理
    const closeTaskManagement = () => {
      emit('close')
    }

    // 组件挂载时获取数据
    onMounted(() => {
      fetchTasks()
      
      // 设置定时刷新
      const interval = setInterval(() => {
        fetchTasks()
      }, 30000) // 每30秒刷新一次
      
      // 组件卸载时清除定时器
      return () => {
        clearInterval(interval)
      }
    })

    return {
      tasks,
      taskStats,
      isLoading,
      isRetrying,
      isCancelling,
      statusFilter,
      currentPage,
      totalPages,
      showDetailModal,
      selectedTask,
      fetchTasks,
      retryTask,
      cancelTask,
      viewTaskDetail,
      changePage,
      getStatusClass,
      getStatusIcon,
      getStatusText,
      getPlatformText,
      formatDateTime,
      closeTaskManagement
    }
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>