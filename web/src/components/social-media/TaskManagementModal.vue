<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-5xl max-h-[90vh] overflow-hidden flex flex-col">
      <!-- 弹窗标题 -->
      <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h3 class="text-lg font-medium text-gray-900">任务管理</h3>
        <button 
          @click="$emit('close')" 
          class="text-gray-500 hover:text-gray-700 focus:outline-none"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <!-- 筛选操作区 -->
      <div class="px-6 py-4 border-b border-gray-200 flex flex-wrap gap-4 items-center">
        <!-- 状态筛选 -->
        <div class="relative">
          <button 
            @click="showStatusDropdown = !showStatusDropdown"
            class="flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none"
          >
            {{ selectedStatusText }}
            <i class="fas fa-chevron-down ml-2 text-xs"></i>
          </button>
          <div v-if="showStatusDropdown" class="absolute mt-2 w-48 bg-white rounded-md shadow-lg z-10 border border-gray-200">
            <div 
              v-for="option in statusOptions" 
              :key="option.value"
              @click="selectStatus(option.value)"
              class="px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 cursor-pointer"
            >
              {{ option.label }}
            </div>
          </div>
        </div>
        
        <!-- 平台筛选 -->
        <div class="relative">
          <button 
            @click="showPlatformDropdown = !showPlatformDropdown"
            class="flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none"
          >
            {{ selectedPlatformText }}
            <i class="fas fa-chevron-down ml-2 text-xs"></i>
          </button>
          <div v-if="showPlatformDropdown" class="absolute mt-2 w-48 bg-white rounded-md shadow-lg z-10 border border-gray-200">
            <div 
              v-for="option in platformOptions" 
              :key="option.value"
              @click="selectPlatform(option.value)"
              class="px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 cursor-pointer flex items-center"
            >
              <i :class="option.icon + ' mr-2'" v-if="option.icon"></i>
              {{ option.label }}
            </div>
          </div>
        </div>
        
        <!-- 时间范围筛选 -->
        <div class="relative">
          <button 
            @click="showTimeRangeDropdown = !showTimeRangeDropdown"
            class="flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none"
          >
            {{ selectedTimeRangeText }}
            <i class="fas fa-chevron-down ml-2 text-xs"></i>
          </button>
          <div v-if="showTimeRangeDropdown" class="absolute mt-2 w-48 bg-white rounded-md shadow-lg z-10 border border-gray-200">
            <div 
              v-for="option in timeRangeOptions" 
              :key="option.value"
              @click="selectTimeRange(option.value)"
              class="px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 cursor-pointer"
            >
              {{ option.label }}
            </div>
          </div>
        </div>
        
        <!-- 搜索框 -->
        <div class="relative flex-grow max-w-md">
          <input 
            v-model="searchKeyword"
            type="text" 
            placeholder="搜索任务..."
            class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
          <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
        </div>
        
        <!-- 刷新按钮 -->
        <button 
          @click="refreshTasks"
          :disabled="isRefreshing"
          class="flex items-center px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none disabled:opacity-50"
        >
          <i class="fas fa-sync-alt mr-2" :class="{ 'animate-spin': isRefreshing }"></i>
          刷新
        </button>
      </div>
      
      <!-- 批量操作区域 -->
      <div v-if="selectedTasks.length > 0" class="px-6 py-3 bg-gray-50 border-b border-gray-200 flex items-center space-x-4">
        <span class="text-sm text-gray-700">已选择 {{ selectedTasks.length }} 项</span>
        <button 
          @click="batchDeleteTasks"
          class="px-3 py-1 border border-red-300 text-red-600 rounded-md text-sm font-medium hover:bg-red-50 focus:outline-none"
        >
          <i class="fas fa-trash-alt mr-1"></i> 删除
        </button>
        <button 
          @click="batchRetryTasks"
          :disabled="!hasFailedTasksSelected"
          class="px-3 py-1 border border-blue-300 text-blue-600 rounded-md text-sm font-medium hover:bg-blue-50 focus:outline-none disabled:opacity-50"
        >
          <i class="fas fa-redo-alt mr-1"></i> 重试
        </button>
      </div>
      
      <!-- 统计概览区 -->
      <div class="px-6 py-4 border-b border-gray-200">
        <div class="grid grid-cols-4 gap-4">
          <div 
            v-for="stat in taskStats" 
            :key="stat.status"
            class="p-3 rounded-lg border border-gray-200"
          >
            <div class="text-sm text-gray-500">{{ stat.label }}</div>
            <div class="text-xl font-semibold text-gray-900 mt-1">{{ stat.count }}</div>
          </div>
        </div>
      </div>
      
      <!-- 分页组件 -->
      <div class="px-6 py-3 border-b border-gray-200 flex justify-between items-center">
        <div class="text-sm text-gray-500">
          共 {{ totalPages }} 页，第 {{ currentPage }} 页
        </div>
        <div class="flex items-center space-x-2">
          <button 
            @click="currentPage > 1 && currentPage--"
            :disabled="currentPage === 1"
            class="px-3 py-1 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none disabled:opacity-50"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <button 
            v-for="page in visiblePages" 
            :key="page"
            @click="currentPage = page"
            :class="[
              'px-3 py-1 rounded-md text-sm font-medium',
              currentPage === page 
                ? 'bg-blue-600 text-white' 
                : 'border border-gray-300 text-gray-700 hover:bg-gray-50'
            ]"
          >
            {{ page }}
          </button>
          <button 
            @click="currentPage < totalPages && currentPage++"
            :disabled="currentPage === totalPages"
            class="px-3 py-1 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none disabled:opacity-50"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
      
      <!-- 任务列表区 -->
      <div class="flex-1 overflow-y-auto">
        <div v-if="paginatedTasks.length === 0" class="px-6 py-12 text-center">
          <i class="fas fa-tasks text-gray-300 text-5xl mb-4"></i>
          <p class="text-gray-500">暂无任务数据</p>
        </div>
        <table v-else class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <div class="flex items-center">
                  <input 
                    type="checkbox" 
                    :checked="isAllSelected" 
                    :indeterminate="isIndeterminate"
                    @change="toggleAllTasks"
                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  >
                </div>
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                任务ID
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                任务名称
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                发布平台
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                状态
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                计划时间
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作人
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="task in paginatedTasks" :key="task.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <input 
                    type="checkbox" 
                    :checked="selectedTasks.includes(task.id)"
                    @change="toggleTaskSelection(task.id)"
                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  >
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ task.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ task.name }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <i :class="getPlatformIcon(task.platform) + ' mr-2'" v-if="getPlatformIcon(task.platform)"></i>
                  <span class="text-sm text-gray-500">{{ getPlatformName(task.platform) }}</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getTaskStatusClass(task.status)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                  {{ getTaskStatusText(task.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatScheduleTime(task.scheduleTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ task.operator }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button 
                  @click="toggleTaskDetail(task.id)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  {{ expandedTasks.includes(task.id) ? '收起' : '详情' }}
                </button>
                <button 
                  v-if="task.status === 'failed'"
                  @click="batchRetryTasks([task.id])"
                  class="text-green-600 hover:text-green-900"
                >
                  重试
                </button>
              </td>
            </tr>
            <!-- 任务详情展开区域 -->
            <tr v-for="task in paginatedTasks" :key="`detail-${task.id}`" v-if="expandedTasks.includes(task.id)">
              <td colspan="8" class="px-6 py-4 bg-gray-50">
                <div class="border border-gray-200 rounded-lg p-4 bg-white">
                  <h4 class="text-sm font-medium text-gray-900 mb-3">任务详情</h4>
                  <div class="grid grid-cols-2 gap-4">
                    <div>
                      <p class="text-xs text-gray-500">任务内容</p>
                      <p class="text-sm text-gray-900 mt-1">{{ task.content || '无内容' }}</p>
                    </div>
                    <div>
                      <p class="text-xs text-gray-500">执行结果</p>
                      <p class="text-sm text-gray-900 mt-1">{{ task.result || '暂无结果' }}</p>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 底部操作区 -->
      <div class="px-6 py-4 border-t border-gray-200 flex justify-between items-center">
        <div class="flex items-center space-x-3">
          <button 
            @click="createNewTask"
            class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm font-medium hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            <i class="fas fa-plus mr-2"></i>
            创建新任务
          </button>
        </div>
        <button 
          @click="$emit('close')"
          class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
        >
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'

export default {
  name: 'TaskManagementModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    // 响应式数据
    const showStatusDropdown = ref(false)
    const showPlatformDropdown = ref(false)
    const showTimeRangeDropdown = ref(false)
    const searchKeyword = ref('')
    const selectedStatus = ref('all')
    const selectedPlatform = ref('all')
    const selectedTimeRange = ref('all')
    const selectedTasks = ref([])
    const expandedTasks = ref([])
    const isRefreshing = ref(false)
    
    // 分页数据
    const currentPage = ref(1)
    const pageSize = ref(10)
    
    // 模拟任务数据
    const tasks = ref([])
    
    // 选项数据
    const statusOptions = [
      { value: 'all', label: '全部状态' },
      { value: 'pending', label: '待执行' },
      { value: 'running', label: '执行中' },
      { value: 'success', label: '成功' },
      { value: 'failed', label: '失败' }
    ]
    
    const platformOptions = [
      { value: 'all', label: '全部平台', icon: '' },
      { value: 'weibo', label: '微博', icon: 'fab fa-weibo text-red-500' },
      { value: 'wechat', label: '微信', icon: 'fab fa-weixin text-green-500' },
      { value: 'twitter', label: 'Twitter', icon: 'fab fa-twitter text-blue-500' },
      { value: 'facebook', label: 'Facebook', icon: 'fab fa-facebook text-blue-600' }
    ]
    
    const timeRangeOptions = [
      { value: 'all', label: '全部时间' },
      { value: 'today', label: '今天' },
      { value: 'week', label: '本周' },
      { value: 'month', label: '本月' }
    ]
    
    // 计算属性
    const selectedStatusText = computed(() => {
      return statusOptions.find(option => option.value === selectedStatus.value)?.label || '全部状态'
    })
    
    const selectedPlatformText = computed(() => {
      return platformOptions.find(option => option.value === selectedPlatform.value)?.label || '全部平台'
    })
    
    const selectedTimeRangeText = computed(() => {
      return timeRangeOptions.find(option => option.value === selectedTimeRange.value)?.label || '全部时间'
    })
    
    const filteredTasks = computed(() => {
      // 这里应该实现实际的筛选逻辑
      return tasks.value
    })
    
    const totalPages = computed(() => {
      return Math.ceil(filteredTasks.value.length / pageSize.value)
    })
    
    const visiblePages = computed(() => {
      // 这里应该实现实际的分页逻辑
      return [1]
    })
    
    const paginatedTasks = computed(() => {
      // 这里应该实现实际的分页逻辑
      return filteredTasks.value.slice(0, pageSize.value)
    })
    
    const isAllSelected = computed(() => {
      return selectedTasks.value.length === filteredTasks.value.length && filteredTasks.value.length > 0
    })
    
    const isIndeterminate = computed(() => {
      return selectedTasks.value.length > 0 && selectedTasks.value.length < filteredTasks.value.length
    })
    
    const hasFailedTasksSelected = computed(() => {
      // 这里应该实现实际的逻辑
      return false
    })
    
    const taskStats = computed(() => {
      // 这里应该实现实际的统计逻辑
      return [
        { status: 'pending', label: '待执行', count: 0 },
        { status: 'running', label: '执行中', count: 0 },
        { status: 'success', label: '成功', count: 0 },
        { status: 'failed', label: '失败', count: 0 }
      ]
    })
    
    // 方法
    const selectStatus = (status) => {
      selectedStatus.value = status
      showStatusDropdown.value = false
      refreshTasks()
    }
    
    const selectPlatform = (platform) => {
      selectedPlatform.value = platform
      showPlatformDropdown.value = false
      refreshTasks()
    }
    
    const selectTimeRange = (timeRange) => {
      selectedTimeRange.value = timeRange
      showTimeRangeDropdown.value = false
      refreshTasks()
    }
    
    const refreshTasks = async () => {
      isRefreshing.value = true
      try {
        // 这里应该实现实际的数据获取逻辑
        // 暂时使用空数组作为实际数据
        tasks.value = []
      } catch (error) {
        console.error('刷新任务失败:', error)
      } finally {
        isRefreshing.value = false
      }
    }
    
    const toggleTaskSelection = (taskId) => {
      const index = selectedTasks.value.indexOf(taskId)
      if (index > -1) {
        selectedTasks.value.splice(index, 1)
      } else {
        selectedTasks.value.push(taskId)
      }
    }
    
    const toggleAllTasks = () => {
      if (isAllSelected.value) {
        selectedTasks.value = []
      } else {
        selectedTasks.value = filteredTasks.value.map(task => task.id)
      }
    }
    
    const toggleTaskDetail = (taskId) => {
      const index = expandedTasks.value.indexOf(taskId)
      if (index > -1) {
        expandedTasks.value.splice(index, 1)
      } else {
        expandedTasks.value.push(taskId)
      }
    }
    
    const batchDeleteTasks = () => {
      // 这里应该实现实际的批量删除逻辑
      console.log('批量删除任务:', selectedTasks.value)
    }
    
    const batchRetryTasks = () => {
      // 这里应该实现实际的批量重试逻辑
      console.log('批量重试任务:', selectedTasks.value)
    }
    
    const createNewTask = () => {
      // 这里应该实现创建新任务的逻辑
      console.log('创建新任务')
    }
    
    const getPlatformIcon = (platform) => {
      const option = platformOptions.find(opt => opt.value === platform)
      return option?.icon || ''
    }
    
    const getPlatformName = (platform) => {
      const option = platformOptions.find(opt => opt.value === platform)
      return option?.label || platform
    }
    
    const formatScheduleTime = (time) => {
      // 这里应该实现实际的时间格式化逻辑
      return time
    }
    
    const getTaskStatusClass = (status) => {
      // 这里应该实现实际的状态样式逻辑
      return ''
    }
    
    const getTaskStatusText = (status) => {
      const option = statusOptions.find(opt => opt.value === status)
      return option?.label || status
    }
    
    // 初始化
    refreshTasks()
    
    // 点击外部关闭下拉菜单
    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })
    
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })
    
    const handleClickOutside = (event) => {
      const dropdowns = document.querySelectorAll('.relative')
      let isClickInside = false
      
      dropdowns.forEach(dropdown => {
        if (dropdown.contains(event.target)) {
          isClickInside = true
        }
      })
      
      if (!isClickInside) {
        showStatusDropdown.value = false
        showPlatformDropdown.value = false
        showTimeRangeDropdown.value = false
      }
    }
    
    return {
      showStatusDropdown,
      showPlatformDropdown,
      showTimeRangeDropdown,
      searchKeyword,
      selectedStatus,
      selectedPlatform,
      selectedTimeRange,
      selectedTasks,
      expandedTasks,
      isRefreshing,
      currentPage,
      pageSize,
      statusOptions,
      platformOptions,
      timeRangeOptions,
      selectedStatusText,
      selectedPlatformText,
      selectedTimeRangeText,
      filteredTasks,
      totalPages,
      visiblePages,
      paginatedTasks,
      isAllSelected,
      isIndeterminate,
      hasFailedTasksSelected,
      taskStats,
      selectStatus,
      selectPlatform,
      selectTimeRange,
      refreshTasks,
      toggleTaskSelection,
      toggleAllTasks,
      toggleTaskDetail,
      batchDeleteTasks,
      batchRetryTasks,
      createNewTask,
      getPlatformIcon,
      getPlatformName,
      formatScheduleTime,
      getTaskStatusClass,
      getTaskStatusText
    }
  }
}
</script>

<style scoped>
/* 自定义样式 */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>