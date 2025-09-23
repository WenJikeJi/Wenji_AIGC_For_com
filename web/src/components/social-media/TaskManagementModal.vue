<template>
  <!-- 弹窗遮罩 -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click.self="$emit('close')">
    <!-- 弹窗主体 -->
    <div class="bg-white rounded-lg shadow-xl relative" style="width: 600px; padding: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 8px;">
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
      
      <!-- 标题与说明 -->
      <div class="mb-6">
        <h2 class="text-lg font-bold text-left mb-2" style="font-size: 18px; color: #333; font-weight: bold;">
          任务管理
        </h2>
        <p class="text-sm text-gray-600" style="font-size: 12px; color: #666;">
          管理您的定时发帖任务
        </p>
      </div>

      <!-- 筛选操作区 -->
      <div class="bg-gray-50 rounded-lg p-4 mb-4 border border-gray-100">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <!-- 状态筛选下拉 -->
            <div class="relative">
              <button
                @click="showStatusDropdown = !showStatusDropdown"
                class="flex items-center justify-between border rounded transition-all duration-200 hover:border-purple-400"
                style="width: 100px; padding: 6px; border: 1px solid #e0e0e0; background: white;"
              >
                <span class="text-sm text-gray-700">{{ selectedStatusText }}</span>
                <i class="fas fa-chevron-down text-xs text-gray-400 ml-2"></i>
              </button>
              
              <!-- 下拉选项 -->
              <div 
                v-if="showStatusDropdown"
                class="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded shadow-lg z-10"
                style="width: 100px;"
              >
                <div 
                  v-for="status in statusOptions"
                  :key="status.value"
                  @click="selectStatus(status.value)"
                  class="px-3 py-2 text-sm cursor-pointer hover:bg-gray-50 transition-colors"
                  style="background: white;"
                >
                  {{ status.label }}
                </div>
              </div>
            </div>
            
            <!-- 平台筛选下拉 -->
            <div class="relative">
              <button
                @click="showPlatformDropdown = !showPlatformDropdown"
                class="flex items-center justify-between border rounded transition-all duration-200 hover:border-purple-400"
                style="width: 100px; padding: 6px; border: 1px solid #e0e0e0; background: white;"
              >
                <span class="text-sm text-gray-700">{{ selectedPlatformText }}</span>
                <i class="fas fa-chevron-down text-xs text-gray-400 ml-2"></i>
              </button>
              
              <!-- 下拉选项 -->
              <div 
                v-if="showPlatformDropdown"
                class="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded shadow-lg z-10"
                style="width: 100px;"
              >
                <div 
                  v-for="platform in platformOptions"
                  :key="platform.value"
                  @click="selectPlatform(platform.value)"
                  class="px-3 py-2 text-sm cursor-pointer hover:bg-gray-50 transition-colors flex items-center"
                  style="background: white;"
                >
                  <i v-if="platform.icon" :class="platform.icon" class="mr-2 text-xs"></i>
                  {{ platform.label }}
                </div>
              </div>
            </div>
            
            <!-- 时间范围筛选下拉 -->
            <div class="relative">
              <button
                @click="showTimeRangeDropdown = !showTimeRangeDropdown"
                class="flex items-center justify-between border rounded transition-all duration-200 hover:border-purple-400"
                style="width: 100px; padding: 6px; border: 1px solid #e0e0e0; background: white;"
              >
                <span class="text-sm text-gray-700">{{ selectedTimeRangeText }}</span>
                <i class="fas fa-chevron-down text-xs text-gray-400 ml-2"></i>
              </button>
              
              <!-- 下拉选项 -->
              <div 
                v-if="showTimeRangeDropdown"
                class="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded shadow-lg z-10"
                style="width: 100px;"
              >
                <div 
                  v-for="timeRange in timeRangeOptions"
                  :key="timeRange.value"
                  @click="selectTimeRange(timeRange.value)"
                  class="px-3 py-2 text-sm cursor-pointer hover:bg-gray-50 transition-colors"
                  style="background: white;"
                >
                  {{ timeRange.label }}
                </div>
              </div>
            </div>
            
            <!-- 搜索框 -->
            <div class="relative">
              <input
                v-model="searchKeyword"
                type="text"
                placeholder="搜索任务..."
                class="border rounded transition-all duration-200 hover:border-purple-400 focus:border-purple-500 focus:outline-none pl-8"
                style="width: 150px; padding: 6px 8px 6px 32px; border: 1px solid #e0e0e0; background: white; font-size: 12px;"
              />
              <i class="fas fa-search absolute left-2 top-1/2 transform -translate-y-1/2 text-gray-400 text-xs"></i>
            </div>
            
            <!-- 刷新按钮 -->
            <button
              @click="refreshTasks"
              :disabled="isRefreshing"
              class="flex items-center justify-center text-gray-600 hover:text-purple-600 transition-all duration-200 bg-white border border-gray-200 rounded"
              style="width: 32px; height: 32px;"
              title="刷新"
            >
              <i 
                class="fas fa-sync-alt text-sm"
                :class="{ 'animate-spin': isRefreshing }"
              ></i>
            </button>
          </div>
          
          <!-- 批量操作区域 -->
          <div class="flex items-center space-x-2">
            <!-- 批量操作按钮 - 仅在有选中任务时显示 -->
            <div v-if="selectedTasks.length > 0" class="flex items-center space-x-2">
              <span class="text-xs text-gray-500">已选择 {{ selectedTasks.length }} 项</span>
              
              <!-- 批量删除 -->
              <button
                @click="batchDeleteTasks"
                class="text-red-600 hover:text-red-800 text-xs px-2 py-1 rounded hover:bg-red-50 transition-all duration-200 bg-white border border-red-200 hover:border-red-300 hover:shadow-sm active:bg-red-100"
                title="批量删除"
              >
                <i class="fas fa-trash mr-1"></i>
                删除
              </button>
              
              <!-- 批量重新执行 - 仅当选中的任务中有失败状态时显示 -->
              <button
                v-if="hasFailedTasksSelected"
                @click="batchRetryTasks"
                class="text-orange-600 hover:text-orange-800 text-xs px-2 py-1 rounded hover:bg-orange-50 transition-all duration-200 bg-white border border-orange-200 hover:border-orange-300 hover:shadow-sm active:bg-orange-100"
                title="批量重新执行"
              >
                <i class="fas fa-redo mr-1"></i>
                重试
              </button>
            </div>
            
            <!-- 关闭按钮 -->
            <button 
              type="button"
              @click="$emit('close')"
              class="font-medium rounded border transition-all duration-200 hover:bg-gray-100 hover:border-gray-300 hover:shadow-sm active:bg-gray-200 bg-white"
              style="padding: 6px 12px; border: 1px solid #d1d5db; color: #374151; border-radius: 6px;"
            >
              关闭
            </button>
          </div>
        </div>
      </div>

      <!-- 统计概览区 -->
      <div class="bg-white rounded-lg p-4 mb-4 border border-gray-100">
        <div class="flex justify-between space-x-3">
          <div 
            v-for="stat in taskStats"
            :key="stat.status"
            class="bg-gray-50 rounded-lg shadow-sm cursor-pointer transition-all duration-200 hover:shadow-md hover:bg-gray-100 border border-gray-100 group relative"
            style="flex: 1; padding: 16px 12px; border-radius: 8px;"
            @click="selectStatus(stat.status)"
          >
            <div class="text-center">
              <!-- 数字 - 放大字号 -->
              <div class="text-3xl font-bold mb-2" style="color: #1f2937; font-weight: 800; font-size: 28px; line-height: 1;">
                {{ stat.count }}
              </div>
              <!-- 说明文字 - 缩小字号 -->
              <div class="text-xs font-medium" style="color: #6b7280; font-size: 10px; font-weight: 500;">
                {{ stat.label }}
              </div>
            </div>
            
            <!-- Hover 时显示的数量明细提示 -->
            <div class="absolute -top-10 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10">
              {{ stat.label }}：{{ stat.count }} 条任务
              <div class="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-800"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 任务列表区 -->
      <div class="bg-white rounded-lg border border-gray-100 min-h-64">
        <!-- 无任务状态 -->
        <div v-if="filteredTasks.length === 0" class="flex flex-col items-center justify-center py-12">
          <div class="text-gray-400 mb-4">
            <i class="fas fa-tasks text-4xl"></i>
          </div>
          <p class="text-center" style="font-size: 14px; color: #666;">
            暂无任务
          </p>
          <button 
            @click="createNewTask"
            class="mt-4 text-blue-600 hover:text-blue-800 text-sm transition-colors"
          >
            <i class="fas fa-plus mr-1"></i>
            创建新任务
          </button>
        </div>
        
        <!-- 任务列表 -->
        <div v-else class="divide-y divide-gray-200">
          <!-- 全选/反选行 -->
          <div class="p-4 bg-gray-50 border-b border-gray-200">
            <div class="flex items-center space-x-3">
              <input
                type="checkbox"
                :checked="isAllSelected"
                :indeterminate="isIndeterminate"
                @change="toggleAllTasks"
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500 hover:border-purple-400 transition-colors"
              />
              <span class="text-sm text-gray-600 font-medium">
                {{ selectedTasks.length > 0 ? `已选择 ${selectedTasks.length} 项` : '全选' }}
              </span>
            </div>
          </div>
          
          <div 
            v-for="task in filteredTasks"
            :key="task.id"
            class="transition-all duration-200 border-b border-gray-100 last:border-b-0 hover:bg-gray-50 hover:shadow-sm"
            :class="{ 'bg-purple-50 border-purple-200': selectedTasks.includes(task.id) }"
            style="padding: 12px 16px;"
          >
            <div class="flex items-start justify-between">
              <!-- 复选框和任务信息 -->
              <div class="flex items-start space-x-3 flex-1">
                <!-- 复选框 -->
                <input
                  type="checkbox"
                  :checked="selectedTasks.includes(task.id)"
                  @change="toggleTaskSelection(task.id)"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500 mt-1 hover:border-purple-400 transition-colors"
                  @click.stop
                />
                
                <!-- 任务信息 -->
                <div class="flex-1 cursor-pointer" @click="toggleTaskDetail(task.id)">
                  <!-- 基本信息行 -->
                  <div class="flex items-center justify-between mb-2">
                    <div class="flex items-center space-x-3">
                      <!-- 任务标题 -->
                      <h4 class="text-sm font-semibold text-gray-900" style="font-size: 14px; font-weight: 600;">
                        {{ task.title }}
                      </h4>
                      
                      <!-- 关联平台 + 分组 -->
                      <span class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded" style="font-size: 11px;">
                        <i :class="getPlatformIcon(task.platform)" class="mr-1"></i>
                        {{ getPlatformName(task.platform) }} · {{ task.account }}
                      </span>
                      
                      <!-- 执行时间 -->
                      <span class="text-xs text-gray-500" style="font-size: 11px;">
                        <i class="fas fa-clock mr-1"></i>
                        {{ formatScheduleTime(task.scheduleTime) }}
                      </span>
                    </div>
                    
                    <!-- 状态标签 -->
                    <span 
                      class="px-2 py-1 text-xs font-medium rounded"
                      :class="getTaskStatusClass(task.status)"
                      style="font-size: 10px; padding: 2px 6px; border-radius: 4px;"
                    >
                      {{ getTaskStatusText(task.status) }}
                    </span>
                  </div>
                  
                  <!-- 详情折叠行 -->
                  <div 
                    v-if="expandedTasks.includes(task.id)"
                    class="mt-2 pt-2 border-t border-gray-100"
                  >
                    <p class="text-xs text-gray-600 leading-relaxed" style="font-size: 12px; line-height: 1.5;">
                      {{ task.content }}
                    </p>
                  </div>
                  
                  <!-- 展开/收起指示器 -->
                  <div class="flex items-center justify-between mt-2">
                    <button 
                      @click.stop="toggleTaskDetail(task.id)"
                      class="text-xs text-purple-600 hover:text-purple-800 transition-colors"
                      style="font-size: 11px;"
                    >
                      <i 
                        class="fas mr-1"
                        :class="expandedTasks.includes(task.id) ? 'fa-chevron-up' : 'fa-chevron-down'"
                      ></i>
                      {{ expandedTasks.includes(task.id) ? '收起详情' : '展开详情' }}
                    </button>
                    
                    <!-- 操作按钮 -->
                    <div class="flex space-x-1">
                      <!-- 立即执行 - 仅待执行状态显示 -->
                      <button 
                        v-if="task.status === 'pending'"
                        @click.stop="executeTask(task.id)"
                        class="text-green-600 hover:text-green-800 text-xs p-1 rounded hover:bg-green-50 transition-colors"
                        style="width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;"
                        title="立即执行"
                      >
                        <i class="fas fa-play" style="font-size: 10px;"></i>
                      </button>
                      
                      <!-- 重新执行 - 仅失败状态显示 -->
                      <button 
                        v-if="task.status === 'failed'"
                        @click.stop="retryTask(task.id)"
                        class="text-orange-600 hover:text-orange-800 text-xs p-1 rounded hover:bg-orange-50 transition-colors"
                        style="width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;"
                        title="重新执行"
                      >
                        <i class="fas fa-redo" style="font-size: 10px;"></i>
                      </button>
                      
                      <!-- 编辑 - 待执行和失败状态显示 -->
                      <button 
                        v-if="task.status === 'pending' || task.status === 'failed'"
                        @click.stop="editTask(task.id)"
                        class="text-blue-600 hover:text-blue-800 text-xs p-1 rounded hover:bg-blue-50 transition-colors"
                        style="width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;"
                        title="编辑任务"
                      >
                        <i class="fas fa-edit" style="font-size: 10px;"></i>
                      </button>
                      
                      <!-- 复制 - 所有状态都可复制 -->
                      <button 
                        @click.stop="copyTask(task.id)"
                        class="text-purple-600 hover:text-purple-800 text-xs p-1 rounded hover:bg-purple-50 transition-colors"
                        style="width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;"
                        title="复制任务"
                      >
                        <i class="fas fa-copy" style="font-size: 10px;"></i>
                      </button>
                      
                      <!-- 查看详情 - 所有状态都可查看 -->
                      <button 
                        @click.stop="viewTaskDetail(task)"
                        class="text-gray-600 hover:text-gray-800 text-xs p-1 rounded hover:bg-gray-50 transition-colors"
                        style="width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;"
                        title="查看详情"
                      >
                        <i class="fas fa-eye" style="font-size: 10px;"></i>
                      </button>
                      
                      <!-- 删除 - 所有状态都可删除 -->
                      <button 
                        @click.stop="deleteTask(task.id)"
                        class="text-red-600 hover:text-red-800 text-xs p-1 rounded hover:bg-red-50 transition-colors"
                        style="width: 24px; height: 24px; display: flex; align-items: center; justify-content: center;"
                        title="删除任务"
                      >
                        <i class="fas fa-trash" style="font-size: 10px;"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 任务详情弹窗 -->
  <TaskDetailModal
    v-if="showTaskDetail"
    :task="selectedTaskForDetail"
    @close="showTaskDetail = false"
    @execute="handleTaskDetailExecute"
    @retry="handleTaskDetailRetry"
    @edit="handleTaskDetailEdit"
    @copy="handleTaskDetailCopy"
    @delete="handleTaskDetailDelete"
  />
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { showNotification } from '@/utils/notification'
import TaskDetailModal from './TaskDetailModal.vue'

// 响应式数据
const showStatusDropdown = ref(false)
const showPlatformDropdown = ref(false)
const showTimeRangeDropdown = ref(false)
const selectedStatus = ref('all')
const selectedPlatform = ref('all')
const selectedTimeRange = ref('all')
const searchKeyword = ref('')
const isRefreshing = ref(false)
const selectedTasks = ref([]) // 选中的任务ID列表
const expandedTasks = ref([]) // 展开详情的任务ID列表

// 任务详情相关
const showTaskDetail = ref(false)
const selectedTaskForDetail = ref(null)

// 状态选项
const statusOptions = ref([
  { value: 'all', label: '全部状态' },
  { value: 'pending', label: '待执行' },
  { value: 'running', label: '执行中' },
  { value: 'success', label: '成功' },
  { value: 'failed', label: '失败' },
  { value: 'cancelled', label: '已取消' }
])

// 平台选项
const platformOptions = ref([
  { value: 'all', label: '全部平台', icon: null },
  { value: 'facebook', label: 'Facebook', icon: 'fab fa-facebook-f' },
  { value: 'instagram', label: 'Instagram', icon: 'fab fa-instagram' },
  { value: 'tiktok', label: 'TikTok', icon: 'fab fa-tiktok' }
])

// 时间范围选项
const timeRangeOptions = ref([
  { value: 'all', label: '全部时间' },
  { value: 'today', label: '今日' },
  { value: 'week', label: '本周' },
  { value: 'month', label: '本月' },
  { value: 'custom', label: '自定义' }
])

// 模拟任务数据
const tasks = ref([
  {
    id: 'task_1',
    title: '科技前沿资讯分享',
    content: '今日科技热点：AI技术在医疗领域的最新突破，为患者带来更精准的诊断和治疗方案...',
    platform: 'facebook',
    account: '科技前沿',
    scheduleTime: '2024-01-16 10:00:00',
    status: 'pending',
    createdAt: '2024-01-15 15:30:00'
  },
  {
    id: 'task_2',
    title: '产品发布预告',
    content: '即将发布的新产品功能预告，敬请期待！#产品更新 #科技创新',
    platform: 'instagram',
    account: 'TechDaily',
    scheduleTime: '2024-01-16 14:30:00',
    status: 'pending',
    createdAt: '2024-01-15 16:00:00'
  },
  {
    id: 'task_3',
    title: '周末技术分享',
    content: '本周技术总结和下周展望，包含最新的开发工具和框架介绍...',
    platform: 'tiktok',
    account: 'TechTrends',
    scheduleTime: '2024-01-15 09:00:00',
    status: 'success',
    createdAt: '2024-01-14 18:00:00'
  },
  {
    id: 'task_4',
    title: '行业动态报告',
    content: '本月的发展报告，涵盖市场趋势、技术发展和投资机会分析...',
    platform: 'facebook',
    account: '创新科技',
    scheduleTime: '2024-01-15 16:00:00',
    status: 'failed',
    createdAt: '2024-01-15 10:00:00'
  },
  {
    id: 'task_5',
    title: '用户反馈收集',
    content: '收集用户的新功能反馈，帮助我们持续改进产品体验...',
    platform: 'instagram',
    account: 'Innovation Hub',
    scheduleTime: '2024-01-16 20:00:00',
    status: 'running',
    createdAt: '2024-01-15 14:00:00'
  },
  {
    id: 4,
    title: '用户调研问卷',
    content: '为了提供更好的服务，我们诚邀您参与本次用户调研，完成问卷即可获得精美礼品一份！',
    platform: 'facebook',
    account: 'Global Page',
    scheduleTime: new Date().getTime() - 18000000, // 5小时前
    status: 'failed',
    errorMessage: 'API调用失败，请检查网络连接',
    createTime: new Date().getTime() - 259200000,
    updateTime: new Date().getTime() - 18000000
  },
  {
    id: 5,
    title: '企业动态',
    content: '我司已完成roudine融资，估值达10亿美元，Further加大研发投入，推出更多创新产品。',
    platform: 'twitter',
    account: 'Official',
    scheduleTime: new Date().getTime() - 36000000, // 10小时前
    status: 'success',
    createTime: new Date().getTime() - 324000000,
    updateTime: new Date().getTime() - 36000000
  },
  {
    id: 6,
    title: '员工招聘启事',
    content: '我们正在寻找优秀的前端开发工程师加入我们的团队，欢迎投递简历！',
    platform: 'instagram',
    account: 'Careers',
    scheduleTime: new Date().getTime() - 86400000, // 1天前
    status: 'cancelled',
    createTime: new Date().getTime() - 432000000,
    updateTime: new Date().getTime() - 75600000
  }
])

// 计算属性 - 选中的状态文本
const selectedStatusText = computed(() => {
  const status = statusOptions.value.find(option => option.value === selectedStatus.value)
  return status ? status.label : '全部状态'
})

// 计算属性 - 选中的平台文本
const selectedPlatformText = computed(() => {
  const platform = platformOptions.value.find(option => option.value === selectedPlatform.value)
  return platform ? platform.label : '全部平台'
})

// 计算属性 - 选中的时间范围文本
const selectedTimeRangeText = computed(() => {
  const timeRange = timeRangeOptions.value.find(option => option.value === selectedTimeRange.value)
  return timeRange ? timeRange.label : '全部时间'
})

// 计算属性 - 任务统计
const taskStats = computed(() => {
  const stats = []
  
  // 全部任务数量
  stats.push({
    status: 'all',
    label: '全部任务',
    count: tasks.value.length
  })
  
  // 各状态任务数量
  statusOptions.value.slice(1).forEach(option => {
    const count = tasks.value.filter(task => task.status === option.value).length
    stats.push({
      status: option.value,
      label: option.label,
      count
    })
  })
  
  return stats
})

// 计算属性 - 筛选后的任务列表
const filteredTasks = computed(() => {
  let filtered = [...tasks.value]
  
  // 状态筛选
  if (selectedStatus.value !== 'all') {
    filtered = filtered.filter(task => task.status === selectedStatus.value)
  }
  
  // 平台筛选
  if (selectedPlatform.value !== 'all') {
    filtered = filtered.filter(task => task.platform === selectedPlatform.value)
  }
  
  // 时间范围筛选
  if (selectedTimeRange.value !== 'all') {
    const now = new Date().getTime()
    const oneDay = 86400000
    const oneWeek = 7 * oneDay
    const oneMonth = 30 * oneDay
    const oneQuarter = 90 * oneDay
    const oneYear = 365 * oneDay
    
    switch (selectedTimeRange.value) {
      case 'today':
        filtered = filtered.filter(task => Math.abs(task.scheduleTime - now) <= oneDay)
        break
      case 'week':
        filtered = filtered.filter(task => Math.abs(task.scheduleTime - now) <= oneWeek)
        break
      case 'month':
        filtered = filtered.filter(task => Math.abs(task.scheduleTime - now) <= oneMonth)
        break
      case 'quarter':
        filtered = filtered.filter(task => Math.abs(task.scheduleTime - now) <= oneQuarter)
        break
      case 'year':
        filtered = filtered.filter(task => Math.abs(task.scheduleTime - now) <= oneYear)
        break
    }
  }
  
  // 关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    filtered = filtered.filter(task => 
      task.title.toLowerCase().includes(keyword) ||
      task.content.toLowerCase().includes(keyword) ||
      task.account.toLowerCase().includes(keyword)
    )
  }
  
  // 按计划时间倒序排序
  return filtered.sort((a, b) => b.scheduleTime - a.scheduleTime)
})

// 计算属性 - 是否全选
const isAllSelected = computed(() => {
  return filteredTasks.value.length > 0 && selectedTasks.value.length === filteredTasks.value.length
})

// 计算属性 - 部分选中（不确定状态）
const isIndeterminate = computed(() => {
  return selectedTasks.value.length > 0 && selectedTasks.value.length < filteredTasks.value.length
})

// 计算属性 - 选中的任务中是否有失败状态
const hasFailedTasksSelected = computed(() => {
  return selectedTasks.value.some(taskId => {
    const task = tasks.value.find(t => t.id === taskId)
    return task && task.status === 'failed'
  })
})

// 方法 - 选择状态
const selectStatus = (status) => {
  selectedStatus.value = status
  showStatusDropdown.value = false
}

// 方法 - 选择平台
const selectPlatform = (platform) => {
  selectedPlatform.value = platform
  showPlatformDropdown.value = false
}

// 方法 - 选择时间范围
const selectTimeRange = (timeRange) => {
  selectedTimeRange.value = timeRange
  showTimeRangeDropdown.value = false
}

// 方法 - 刷新任务列表
const refreshTasks = async () => {
  isRefreshing.value = true
  
  try {
    // 模拟网络请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟刷新数据（实际应用中应从API获取）
    // 这里只是重新排序，实际应该调用API
    tasks.value.sort((a, b) => b.scheduleTime - a.scheduleTime)
    
    showNotification('任务列表已刷新', 'success')
  } catch (error) {
    console.error('刷新任务列表失败:', error)
    showNotification('刷新失败，请重试', 'error')
  } finally {
    isRefreshing.value = false
  }
}

// 方法 - 切换全选状态
const toggleAllTasks = () => {
  if (isAllSelected.value) {
    selectedTasks.value = []
  } else {
    selectedTasks.value = filteredTasks.value.map(task => task.id)
  }
}

// 方法 - 切换单个任务的选择状态
const toggleTaskSelection = (taskId) => {
  const index = selectedTasks.value.indexOf(taskId)
  if (index > -1) {
    selectedTasks.value.splice(index, 1)
  } else {
    selectedTasks.value.push(taskId)
  }
}

// 方法 - 切换任务详情的展开/收起状态
const toggleTaskDetail = (taskId) => {
  const index = expandedTasks.value.indexOf(taskId)
  if (index > -1) {
    expandedTasks.value.splice(index, 1)
  } else {
    expandedTasks.value.push(taskId)
  }
}

// 方法 - 立即执行任务
const executeTask = async (taskId) => {
  try {
    showNotification('任务正在执行中...', 'info')
    
    // 更新任务状态为执行中
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'running'
    }
    
    // 模拟执行过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 随机设置执行结果（80%成功率）
    if (task) {
      const success = Math.random() > 0.2
      task.status = success ? 'success' : 'failed'
      task.updateTime = new Date().getTime()
    }
    
    showNotification('任务执行完成', 'success')
  } catch (error) {
    console.error('执行任务失败:', error)
    showNotification('执行失败，请重试', 'error')
  }
}

// 方法 - 重新执行任务
const retryTask = async (taskId) => {
  try {
    showNotification('任务正在重新执行...', 'info')
    
    // 更新任务状态为执行中
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'running'
    }
    
    // 模拟执行过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 随机设置执行结果（70%成功率）
    if (task) {
      const success = Math.random() > 0.3
      task.status = success ? 'success' : 'failed'
      task.updateTime = new Date().getTime()
      if (!success) {
        task.errorMessage = '重新执行失败，请稍后再试'
      }
    }
    
    showNotification('任务重新执行完成', 'success')
  } catch (error) {
    console.error('重新执行任务失败:', error)
    showNotification('重新执行失败，请重试', 'error')
  }
}

// 方法 - 编辑任务
const editTask = (taskId) => {
  // 在实际应用中，这里应该打开编辑弹窗或跳转到编辑页面
  showNotification('编辑功能待实现', 'info')
}

// 方法 - 复制任务
const copyTask = async (taskId) => {
  try {
    const originalTask = tasks.value.find(t => t.id === taskId)
    if (!originalTask) return
    
    // 创建新任务（深拷贝）
    const newTask = {
      ...JSON.parse(JSON.stringify(originalTask)),
      id: Date.now(), // 使用时间戳作为临时ID
      status: 'pending',
      scheduleTime: new Date().getTime() + 86400000, // 默认安排在明天
      createTime: new Date().getTime(),
      updateTime: new Date().getTime(),
      title: `复制: ${originalTask.title}`
    }
    
    // 添加到任务列表开头
    tasks.value.unshift(newTask)
    
    showNotification('任务已复制', 'success')
  } catch (error) {
    console.error('复制任务失败:', error)
    showNotification('复制失败，请重试', 'error')
  }
}

// 方法 - 删除任务
const deleteTask = async (taskId) => {
  if (!confirm('确定要删除这个任务吗？此操作不可撤销。')) {
    return
  }
  
  try {
    const index = tasks.value.findIndex(t => t.id === taskId)
    if (index > -1) {
      tasks.value.splice(index, 1)
    }
    
    // 从选中列表中移除
    const selectedIndex = selectedTasks.value.indexOf(taskId)
    if (selectedIndex > -1) {
      selectedTasks.value.splice(selectedIndex, 1)
    }
    
    // 从展开列表中移除
    const expandedIndex = expandedTasks.value.indexOf(taskId)
    if (expandedIndex > -1) {
      expandedTasks.value.splice(expandedIndex, 1)
    }
    
    showNotification('任务已成功删除', 'success')
  } catch (error) {
    console.error('删除任务失败:', error)
    showNotification('删除失败，请重试', 'error')
  }
}

// 方法 - 查看任务详情
const viewTaskDetail = (task) => {
  selectedTaskForDetail.value = task
  showTaskDetail.value = true
}

// 方法 - 创建新任务
const createNewTask = () => {
  // 在实际应用中，这里应该打开创建弹窗或跳转到创建页面
  showNotification('创建新任务功能待实现', 'info')
}

// 方法 - 批量删除任务
const batchDeleteTasks = async () => {
  if (selectedTasks.value.length === 0) return
  
  if (!confirm(`确定要删除选中的 ${selectedTasks.value.length} 个任务吗？此操作不可撤销。`)) {
    return
  }
  
  try {
    // 过滤掉选中的任务
    tasks.value = tasks.value.filter(task => !selectedTasks.value.includes(task.id))
    
    const deletedCount = selectedTasks.value.length
    selectedTasks.value = []
    
    showNotification(`已成功删除 ${deletedCount} 个任务`, 'success')
  } catch (error) {
    console.error('批量删除任务失败:', error)
    showNotification('批量删除失败，请重试', 'error')
  }
}

// 方法 - 批量重新执行任务
const batchRetryTasks = async () => {
  const failedTasks = selectedTasks.value.filter(taskId => {
    const task = tasks.value.find(t => t.id === taskId)
    return task && task.status === 'failed'
  })
  
  if (failedTasks.length === 0) return
  
  try {
    showNotification(`正在重新执行 ${failedTasks.length} 个失败任务...`, 'info')
    
    // 更新任务状态为执行中
    failedTasks.forEach(taskId => {
      const task = tasks.value.find(t => t.id === taskId)
      if (task) {
        task.status = 'running'
      }
    })
    
    // 模拟批量重新执行过程
    await new Promise(resolve => setTimeout(resolve, 3000))
    
    let successCount = 0
    let failCount = 0
    
    // 随机设置重新执行结果
    failedTasks.forEach(taskId => {
      const task = tasks.value.find(t => t.id === taskId)
      if (task) {
        const success = Math.random() > 0.3 // 70%成功率
        task.status = success ? 'success' : 'failed'
        if (success) {
          successCount++
        } else {
          failCount++
        }
      }
    })
    
    selectedTasks.value = []
    
    if (failCount === 0) {
      showNotification(`批量重新执行完成！${successCount} 个任务执行成功`, 'success')
    } else {
      showNotification(`批量重新执行完成！${successCount} 个成功，${failCount} 个失败`, 'warning')
    }
  } catch (error) {
    console.error('批量重新执行失败:', error)
    showNotification('批量重新执行失败，请重试', 'error')
  }
}

// 方法 - 处理任务详情弹窗中的执行操作
const handleTaskDetailExecute = (taskId) => {
  showTaskDetail.value = false
  executeTask(taskId)
}

// 方法 - 处理任务详情弹窗中的重新执行操作
const handleTaskDetailRetry = (taskId) => {
  showTaskDetail.value = false
  retryTask(taskId)
}

// 方法 - 处理任务详情弹窗中的编辑操作
const handleTaskDetailEdit = (taskId) => {
  showTaskDetail.value = false
  editTask(taskId)
}

// 方法 - 处理任务详情弹窗中的复制操作
const handleTaskDetailCopy = (taskId) => {
  showTaskDetail.value = false
  copyTask(taskId)
}

// 方法 - 处理任务详情弹窗中的删除操作
const handleTaskDetailDelete = (taskId) => {
  showTaskDetail.value = false
  deleteTask(taskId)
}

// 方法 - 获取平台图标
const getPlatformIcon = (platform) => {
  const platformInfo = platformOptions.value.find(p => p.value === platform)
  return platformInfo && platformInfo.icon ? platformInfo.icon : ''
}

// 方法 - 获取平台名称
const getPlatformName = (platform) => {
  const platformInfo = platformOptions.value.find(p => p.value === platform)
  return platformInfo ? platformInfo.label : platform
}

// 方法 - 获取任务状态样式类
const getTaskStatusClass = (status) => {
  const statusClasses = {
    pending: 'bg-yellow-100 text-yellow-800',
    running: 'bg-blue-100 text-blue-800',
    success: 'bg-green-100 text-green-800',
    failed: 'bg-red-100 text-red-800',
    cancelled: 'bg-gray-100 text-gray-800'
  }
  
  return statusClasses[status] || 'bg-gray-100 text-gray-800'
}

// 方法 - 获取任务状态文本
const getTaskStatusText = (status) => {
  const statusText = {
    pending: '待执行',
    running: '执行中',
    success: '成功',
    failed: '失败',
    cancelled: '已取消'
  }
  
  return statusText[status] || '未知'
}

// 方法 - 格式化计划时间
const formatScheduleTime = (timestamp) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = date - now
  
  // 如果是未来时间且小于1小时，显示"xx分钟后"
  if (diff > 0 && diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return `${minutes}分钟后`
  }
  
  // 如果是未来时间且小于24小时，显示"xx小时后"
  if (diff > 0 && diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小时后`
  }
  
  // 如果是今天，只显示时间
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  // 如果是昨天，显示"昨天 HH:mm"
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (date.toDateString() === yesterday.toDateString()) {
    return `昨天 ${date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
  }
  
  // 如果是今年，显示"MM-dd HH:mm"
  if (date.getFullYear() === now.getFullYear()) {
    return `${date.getMonth() + 1}-${date.getDate()} ${date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
  }
  
  // 其他情况，显示完整日期时间
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

// 方法 - 点击外部关闭下拉框
const handleClickOutside = (event) => {
  // 检查点击目标是否在下拉框外部
  if (!event.target.closest('.relative')) {
    showStatusDropdown.value = false
    showPlatformDropdown.value = false
    showTimeRangeDropdown.value = false
  }
}

// 组件挂载时添加事件监听
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 旋转动画 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

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
</style>