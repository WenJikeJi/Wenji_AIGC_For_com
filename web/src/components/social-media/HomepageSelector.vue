<template>
  <div class="relative">
    <!-- 触发区域 -->
    <div 
      @click="toggleDropdown"
      class="w-full px-3 py-2 text-sm border border-gray-300 rounded bg-white cursor-pointer flex justify-between items-center hover:border-blue-400 transition-colors duration-200"
    >
      <span v-if="selectedHomepages.length === 0" class="text-gray-500">选择主页</span>
      <span v-else class="text-gray-900">已选 {{ selectedHomepages.length }} 个主页</span>
      <svg class="w-4 h-4 text-gray-400 transform transition-transform duration-200" :class="{ 'rotate-180': isDropdownOpen }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
      </svg>
    </div>
    
    <!-- 下拉列表 -->
    <div v-if="isDropdownOpen" class="absolute z-20 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg">
      <!-- 搜索框 -->
      <div class="p-2 border-b border-gray-200">
        <input 
          v-model="searchQuery"
          type="text"
          placeholder="搜索主页..."
          class="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:outline-none focus:border-blue-500"
          @click.stop
        >
      </div>
      
      <!-- 主页列表 -->
      <div class="max-h-48 overflow-y-auto p-2">
        <div 
          v-for="homepage in filteredHomepages" 
          :key="homepage.id" 
          @click="toggleHomepageSelection(homepage.id)" 
          class="flex items-center space-x-3 p-2 hover:bg-blue-50 rounded-lg transition-all duration-200 cursor-pointer"
          :class="{ 'bg-blue-50': selectedHomepages.includes(homepage.id) }"
        >
          <!-- 多选框 -->
          <div class="relative">
            <input 
              type="checkbox" 
              :checked="selectedHomepages.includes(homepage.id)" 
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500 focus:ring-2 transition-all duration-200"
              @click.stop
            >
            <!-- 选中动画效果 -->
            <div v-if="selectedHomepages.includes(homepage.id)" class="absolute inset-0 flex items-center justify-center pointer-events-none">
              <svg class="w-3 h-3 text-blue-600 animate-pulse" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path>
              </svg>
            </div>
          </div>
          
          <span class="flex items-center space-x-2 flex-1">
            <!-- 平台图标 -->
            <i v-if="homepage.platform === '1' || homepage.platform === 'Facebook'" class="fab fa-facebook-f w-5 h-5 text-blue-600 flex items-center justify-center"></i>
            <i v-else-if="homepage.platform === '2' || homepage.platform === 'Instagram'" class="fab fa-instagram w-5 h-5 text-purple-600 flex items-center justify-center"></i>
            
            <!-- 主页名称 -->
            <span class="text-sm text-gray-800 font-medium">{{ homepage.name }}</span>
            
            <!-- 平台标识彩色标签 -->
            <span 
              v-if="homepage.platform === '1' || homepage.platform === 'Facebook'" 
              class="px-2 py-1 text-xs bg-blue-500 text-white rounded-full font-medium shadow-sm"
            >
              FB
            </span>
            <span 
              v-else-if="homepage.platform === '2' || homepage.platform === 'Instagram'" 
              class="px-2 py-1 text-xs bg-purple-500 text-white rounded-full font-medium shadow-sm"
            >
              IG
            </span>
          </span>
        </div>
        
        <!-- 无结果提示 -->
        <div v-if="filteredHomepages.length === 0" class="px-4 py-3 text-sm text-gray-500 text-center">
          <i class="fas fa-search mr-2"></i>
          {{ searchQuery ? '未找到匹配的主页' : '没有可用的主页' }}
        </div>
      </div>
      
      <!-- 底部操作区 -->
      <div v-if="filteredHomepages.length > 0" class="p-2 border-t border-gray-200 bg-gray-50">
        <div class="flex justify-between items-center text-xs text-gray-600">
          <span>已选择 {{ selectedHomepages.length }} 个主页</span>
          <button 
            v-if="selectedHomepages.length > 0"
            @click.stop="clearSelection"
            class="text-blue-600 hover:text-blue-800 font-medium transition-colors duration-200"
          >
            清空选择
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

// Props
const props = defineProps({
  homepages: {
    type: Array,
    default: () => []
  },
  selectedHomepages: {
    type: Array,
    default: () => []
  },
  selectedPlatforms: {
    type: Array,
    default: () => []
  }
})

// Emits
const emit = defineEmits(['update:selectedHomepages', 'toggle-homepage'])

// 响应式数据
const isDropdownOpen = ref(false)
const searchQuery = ref('')

// 计算属性
const filteredHomepages = computed(() => {
  let filtered = props.homepages

  // 根据选中的平台过滤
  if (props.selectedPlatforms.length > 0) {
    const platformMap = {
      '1': 'Facebook',
      '2': 'Instagram'
    }
    const enabledPlatforms = props.selectedPlatforms.map(p => platformMap[p] || p)
    filtered = filtered.filter(h => 
      enabledPlatforms.includes(h.platform) || 
      enabledPlatforms.includes(h.platform.toString())
    )
  }

  // 根据搜索关键词过滤
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim()
    filtered = filtered.filter(h => 
      h.name.toLowerCase().includes(query) ||
      h.platform.toLowerCase().includes(query)
    )
  }

  return filtered
})

// 方法
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
  if (isDropdownOpen.value) {
    searchQuery.value = ''
  }
}

const toggleHomepageSelection = (homepageId) => {
  const newSelection = [...props.selectedHomepages]
  const index = newSelection.indexOf(homepageId)
  
  if (index > -1) {
    newSelection.splice(index, 1)
  } else {
    newSelection.push(homepageId)
  }
  
  emit('update:selectedHomepages', newSelection)
  emit('toggle-homepage', homepageId)
}

const clearSelection = () => {
  emit('update:selectedHomepages', [])
}

// 监听点击外部关闭下拉框
const handleClickOutside = (event) => {
  if (!event.target.closest('.relative')) {
    isDropdownOpen.value = false
  }
}

// 组件挂载时添加全局点击监听
import { onMounted, onUnmounted } from 'vue'

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 自定义滚动条样式 */
.max-h-48::-webkit-scrollbar {
  width: 6px;
}

.max-h-48::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.max-h-48::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.max-h-48::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 选中状态的动画效果 */
.animate-pulse {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .6;
  }
}

/* 悬停效果增强 */
.hover\:bg-blue-50:hover {
  background-color: #eff6ff;
  transform: translateX(3px);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

/* 平台标签的微动效和脉冲效果 */
.rounded-full {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.rounded-full:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Facebook标签特殊效果 */
.bg-blue-500:hover {
  background: linear-gradient(45deg, #3b82f6, #1d4ed8);
  animation: shimmer 2s infinite;
}

/* Instagram标签特殊效果 */
.bg-purple-500:hover {
  background: linear-gradient(45deg, #8b5cf6, #7c3aed);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

/* 选中项的弹性动画 */
.bg-blue-50 {
  animation: selectBounce 0.3s ease-out;
}

@keyframes selectBounce {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

/* 下拉框展开动画 */
.absolute {
  animation: dropdownSlide 0.2s ease-out;
}

@keyframes dropdownSlide {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 搜索框聚焦效果 */
input:focus {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  transform: scale(1.01);
}

/* 清空按钮悬停效果 */
button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 图标旋转动画 */
.transform.transition-transform {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 复选框选中动画 */
input[type="checkbox"]:checked {
  animation: checkboxPop 0.2s ease-out;
}

@keyframes checkboxPop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

/* 平台图标悬停效果 */
.fab {
  transition: all 0.2s ease;
}

.fab:hover {
  transform: rotate(5deg) scale(1.1);
}

/* 无结果提示的淡入效果 */
.text-center {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>