<template>
  <!-- 弹窗遮罩 -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click.self="$emit('close')">
    <!-- 弹窗主体 -->
    <div class="bg-white rounded-lg shadow-xl relative" style="width: 500px; padding: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border-radius: 8px;">
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
        <h2 class="text-lg font-bold text-left" style="font-size: 18px; color: #333; font-weight: bold;">
          定时发帖
        </h2>
      </div>

      <form @submit.prevent="submitScheduledPost" class="space-y-6">
        <!-- 平台选择区 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-3">
            发布平台 <span class="text-red-500">*</span>
          </label>
          <div class="flex space-x-3">
            <!-- Facebook选项 -->
            <div 
              class="cursor-pointer rounded-lg transition-all duration-200 hover:shadow-lg hover:scale-105"
              style="width: 200px; padding: 12px;"
              :class="[
                selectedPlatforms.includes('1')
                  ? 'text-white border-2 border-white'
                  : 'bg-gray-100 text-gray-800 hover:bg-gray-200'
              ]"
              :style="selectedPlatforms.includes('1') ? 'background: linear-gradient(to right, #2563eb, #9333ea);' : ''"
              @click="togglePlatform('1')"
            >
              <div class="flex items-center justify-center">
                <i class="fab fa-facebook-f mr-2"></i>
                <span class="font-medium">Facebook</span>
              </div>
            </div>
            
            <!-- Instagram选项 -->
            <div 
              class="cursor-pointer rounded-lg transition-all duration-200 hover:shadow-lg hover:scale-105"
              style="width: 200px; padding: 12px;"
              :class="[
                selectedPlatforms.includes('2')
                  ? 'text-white border-2 border-white'
                  : 'bg-gray-100 text-gray-800 hover:bg-gray-200'
              ]"
              :style="selectedPlatforms.includes('2') ? 'background: linear-gradient(to right, #2563eb, #9333ea);' : ''"
              @click="togglePlatform('2')"
            >
              <div class="flex items-center justify-center">
                <i class="fab fa-instagram mr-2"></i>
                <span class="font-medium">Instagram</span>
              </div>
            </div>
          </div>
          <p v-if="platformError" class="mt-2 text-sm text-red-500">
            {{ platformError }}
          </p>
        </div>

        <!-- 选择发布主页 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            发布主页 <span class="text-red-500">*</span>
          </label>
          <HomepageSelector
            :homepages="homepages"
            :selected-homepages="selectedHomepages"
            :selected-platforms="selectedPlatforms"
            @update:selected-homepages="selectedHomepages = $event"
            @toggle-homepage="handleHomepageToggle"
          />
          <p v-if="homepageError" class="mt-2 text-sm text-red-500">{{ homepageError }}</p>
        </div>

        <!-- 定时设置区 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-3">
            定时设置 <span class="text-red-500">*</span>
          </label>
          
          <!-- 发布日期和发布时间下拉框 -->
          <div class="grid grid-cols-2 gap-3 mb-4">
            <!-- 发布日期 -->
            <div>
              <select 
                v-model="scheduleDate"
                class="w-full text-sm rounded bg-white focus:outline-none transition-colors"
                style="padding: 6px; border: 1px solid #e0e0e0; background: white;"
                @focus="$event.target.style.borderColor = '#667eea'"
                @blur="$event.target.style.borderColor = '#e0e0e0'"
              >
                <option value="" disabled>选择日期</option>
                <option 
                  v-for="date in availableDates" 
                  :key="date.value" 
                  :value="date.value"
                >
                  {{ date.label }}
                </option>
              </select>
            </div>
            
            <!-- 发布时间 -->
            <div>
              <select 
                v-model="scheduleTime"
                class="w-full text-sm rounded bg-white focus:outline-none transition-colors"
                style="padding: 6px; border: 1px solid #e0e0e0; background: white;"
                @focus="$event.target.style.borderColor = '#667eea'"
                @blur="$event.target.style.borderColor = '#e0e0e0'"
              >
                <option value="" disabled>选择时间</option>
                <option 
                  v-for="time in availableTimes" 
                  :key="time.value" 
                  :value="time.value"
                >
                  {{ time.label }}
                </option>
              </select>
            </div>
          </div>
          
          <!-- 快捷选择按钮 -->
          <div class="mb-4">
            <div class="flex flex-wrap gap-2">
              <button
                v-for="preset in timePresets"
                :key="preset.label"
                type="button"
                @click="selectTimePreset(preset)"
                class="text-sm rounded transition-all duration-200"
                style="padding: 6px;"
                :class="[
                  selectedPreset === preset.label 
                    ? 'text-white'
                    : 'text-gray-800 hover:bg-gray-200'
                ]"
                :style="selectedPreset === preset.label ? 'background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);' : 'background: #f5f7fa;'"
              >
                {{ preset.label }}
              </button>
            </div>
          </div>
          
          <p v-if="scheduleError" class="text-sm text-red-500">
            {{ scheduleError }}
          </p>
        </div>

        <!-- 帖子内容输入框 -->
        <div>
          <label for="postContent" class="block text-sm font-medium text-gray-700 mb-2">
            帖子内容 <span class="text-red-500">*</span>
          </label>
          <textarea 
            id="postContent"
            v-model="postContent"
            placeholder="请输入帖子内容..."
            rows="4"
            maxlength="2000"
            class="w-full text-sm rounded bg-white focus:outline-none resize-none"
            style="padding: 6px; border: 1px solid #e0e0e0; background: white; height: 100px; color: #999;"
            @focus="$event.target.style.borderColor = '#667eea'; $event.target.style.color = '#333';"
            @blur="$event.target.style.borderColor = '#e0e0e0';"
          ></textarea>
          <p v-if="contentError" class="mt-1 text-sm text-red-500">{{ contentError }}</p>
        </div>

        <!-- 媒体文件区域 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            媒体文件
          </label>
          <MediaUploader 
            v-model="uploadedFiles"
            :max-files="10"
            :max-size="100 * 1024 * 1024"
            :accepted-types="['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'video/mp4', 'video/webm', 'video/mov']"
            @error="handleUploadError"
          />
          <p v-if="fileError" class="mt-2 text-sm text-red-500">{{ fileError }}</p>
        </div>

        <!-- 底部操作按钮 -->
        <div class="flex justify-end space-x-4 pt-4">
          <button 
            type="button"
            @click="$emit('close')"
            class="font-medium rounded border transition-all duration-300 hover:bg-gray-50"
            style="width: 80px; padding: 8px 16px; border: 1px solid #e0e0e0; color: #333; border-radius: 4px;"
          >
            取消
          </button>
          <button 
            type="submit"
            :disabled="isSubmitting"
            class="text-white font-medium rounded transition-all duration-300 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
            style="width: 160px; background: linear-gradient(90deg, #D977F7, #F17CE0); border-radius: 4px; color: #FFF; padding: 8px 16px;"
          >
            <i v-if="isSubmitting" class="fas fa-spinner fa-spin mr-2"></i>
            <i v-else class="fas fa-clock mr-2"></i>
            {{ isSubmitting ? '创建中...' : '创建定时任务' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { showNotification } from '@/utils/notification'
import HomepageSelector from './HomepageSelector.vue'
import MediaUploader from '../common/MediaUploader.vue'

// 定义 emits
const emit = defineEmits(['close', 'success'])

// 响应式数据
const selectedPlatforms = ref([])
const postContent = ref('')
const uploadedFiles = ref([])
const isSubmitting = ref(false)
const isDragOver = ref(false)
const scheduleDate = ref('')
const scheduleTime = ref('')
const selectedPreset = ref('')
const selectedHomepages = ref([]);
const isHomepageDropdownOpen = ref(false);

const homepages = ref([
  { id: 'h1', name: '科技前沿主页 1', platform: 'Facebook' },
  { id: 'h2', name: '科技前沿主页 2', platform: 'Facebook' },
  { id: 'h3', name: 'Tech Instagram', platform: 'Instagram' },
  { id: 'h4', name: 'Instagram科技', platform: 'Instagram' },
  { id: 'h5', name: 'Twitter财经', platform: 'Twitter' },
]);

const filteredHomepages = computed(() => {
  // The platform IDs in selectedPlatforms are '1' for Facebook and '2' for Instagram.
  // We need to map these to the platform names used in the homepages data.
  const platformMap = {
    '1': 'Facebook',
    '2': 'Instagram'
  };
  const enabledPlatforms = selectedPlatforms.value.map(p => platformMap[p]);
  return homepages.value.filter(h => enabledPlatforms.includes(h.platform));
});

// 错误信息
const platformError = ref('')
const contentError = ref('')
const fileError = ref('')
const scheduleError = ref('')
const homepageError = ref('');

// New helper to get platform name for display
function getPlatformName(platform) {
    const names = {
        Facebook: 'FB',
        Instagram: 'INS',
        Twitter: 'TW'
    };
    return names[platform] || platform;
}

// New helper for platform colors
function getPlatformClass(platform) {
    const classes = {
        Facebook: 'bg-blue-600 text-white',
        Instagram: 'bg-pink-500 text-white',
        Twitter: 'bg-blue-400 text-white'
    };
    return classes[platform] || 'bg-gray-400 text-white';
}

// 可用日期选项
const availableDates = computed(() => {
  const dates = []
  const today = new Date()
  
  for (let i = 0; i < 30; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const value = `${year}-${month}-${day}`
    
    let label = `${month}月${day}日`
    if (i === 0) label += ' (今天)'
    else if (i === 1) label += ' (明天)'
    
    dates.push({ value, label })
  }
  
  return dates
})

// 可用时间选项
const availableTimes = computed(() => {
  const times = []
  
  for (let hour = 0; hour < 24; hour++) {
    for (let minute = 0; minute < 60; minute += 30) {
      const hourStr = String(hour).padStart(2, '0')
      const minuteStr = String(minute).padStart(2, '0')
      const value = `${hourStr}:${minuteStr}`
      const label = `${hourStr}:${minuteStr}`
      
      times.push({ value, label })
    }
  }
  
  return times
})

// 快捷时间预设
const timePresets = [
  { label: '1小时后', hours: 1 },
  { label: '2小时后', hours: 2 },
  { label: '明天上午9点', preset: 'tomorrow_9am' },
  { label: '明天下午2点', preset: 'tomorrow_2pm' },
  { label: '明天晚上8点', preset: 'tomorrow_8pm' }
]

// 切换平台选择
const togglePlatform = (platformId) => {
  const index = selectedPlatforms.value.indexOf(platformId);
  if (index > -1) {
    selectedPlatforms.value.splice(index, 1);
  } else {
    selectedPlatforms.value.push(platformId);
  }
  platformError.value = '';

  // Filter out selected homepages that are not on the currently selected platforms
  selectedHomepages.value = selectedHomepages.value.filter(homepageId => {
    const homepage = homepages.value.find(h => h.id === homepageId);
    const platformMap = { '1': 'Facebook', '2': 'Instagram' };
    const enabledPlatforms = selectedPlatforms.value.map(p => platformMap[p]);
    return homepage && enabledPlatforms.includes(homepage.platform);
  });
};

// 方法
const handleHomepageToggle = (homepageId) => {
  // 可以在这里添加额外的逻辑，比如日志记录等
  console.log('Homepage toggled:', homepageId)
}

function toggleHomepageDropdown() {
  isHomepageDropdownOpen.value = !isHomepageDropdownOpen.value;
}

function toggleHomepageSelection(homepageId) {
    const index = selectedHomepages.value.indexOf(homepageId);
    if (index > -1) {
        selectedHomepages.value.splice(index, 1);
    } else {
        selectedHomepages.value.push(homepageId);
    }
}

// 选择时间预设
const selectTimePreset = (preset) => {
  selectedPreset.value = preset.label
  const now = new Date()
  
  if (preset.hours) {
    // 基于小时的预设
    const targetTime = new Date(now.getTime() + preset.hours * 60 * 60 * 1000)
    
    const year = targetTime.getFullYear()
    const month = String(targetTime.getMonth() + 1).padStart(2, '0')
    const day = String(targetTime.getDate()).padStart(2, '0')
    scheduleDate.value = `${year}-${month}-${day}`
    
    const hours = String(targetTime.getHours()).padStart(2, '0')
    const minutes = String(targetTime.getMinutes()).padStart(2, '0')
    scheduleTime.value = `${hours}:${minutes}`
  } else if (preset.preset) {
    // 特定时间预设
    const tomorrow = new Date(now)
    tomorrow.setDate(now.getDate() + 1)
    
    const year = tomorrow.getFullYear()
    const month = String(tomorrow.getMonth() + 1).padStart(2, '0')
    const day = String(tomorrow.getDate()).padStart(2, '0')
    scheduleDate.value = `${year}-${month}-${day}`
    
    switch (preset.preset) {
      case 'tomorrow_9am':
        scheduleTime.value = '09:00'
        break
      case 'tomorrow_2pm':
        scheduleTime.value = '14:00'
        break
      case 'tomorrow_8pm':
        scheduleTime.value = '20:00'
        break
    }
  }
}

// 处理上传错误
const handleUploadError = (error) => {
  fileError.value = error
}

// 文件上传处理
const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  processFiles(files)
}

// 拖拽文件处理
const handleFileDrop = (event) => {
  isDragOver.value = false
  const files = Array.from(event.dataTransfer.files)
  processFiles(files)
}

// 处理文件
const processFiles = (files) => {
  fileError.value = ''
  
  files.forEach(file => {
    // 检查文件大小（10MB限制）
    if (file.size > 10 * 1024 * 1024) {
      fileError.value = '文件大小不能超过10MB'
      return
    }

    // 检查文件类型
    if (!file.type.match(/^(image|video)\//)) {
      fileError.value = '只支持图片和视频文件'
      return
    }

    // 创建预览
    const reader = new FileReader()
    reader.onload = (e) => {
      uploadedFiles.value.push({
        file: file,
        name: file.name,
        size: file.size,
        type: file.type,
        preview: e.target.result
      })
    }
    reader.readAsDataURL(file)
  })
}

// 移除文件
const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

// 验证定时时间
const validateScheduleTime = () => {
  if (!scheduleDate.value || !scheduleTime.value) {
    scheduleError.value = '请选择发布日期和时间'
    return false
  }

  const scheduleDateTime = new Date(`${scheduleDate.value}T${scheduleTime.value}`)
  const now = new Date()

  if (scheduleDateTime <= now) {
    scheduleError.value = '发布时间必须晚于当前时间'
    return false
  }

  scheduleError.value = ''
  return true
}

// 表单验证
const validateForm = () => {
  let isValid = true

  // 重置错误信息
  platformError.value = ''
  contentError.value = ''
  homepageError.value = ''

  // 验证平台选择
  if (selectedPlatforms.value.length === 0) {
    platformError.value = '请选择至少一个发布平台'
    isValid = false
  }

  // 验证主页选择
  if (selectedHomepages.value.length === 0) {
    homepageError.value = '请至少选择一个发布主页'
    isValid = false
  }

  // 验证内容
  if (!postContent.value.trim()) {
    contentError.value = '请输入帖子内容'
    isValid = false
  }

  // 验证定时时间
  if (!validateScheduleTime()) {
    isValid = false
  }

  return isValid
}

// 提交定时任务
const submitScheduledPost = async () => {
  if (!validateForm()) {
    return
  }

  isSubmitting.value = true

  try {
    // 准备媒体URL（这里需要先上传文件获取URL）
    const mediaUrls = []
    for (const fileItem of uploadedFiles.value) {
      // 这里应该调用文件上传接口获取URL
      // 暂时使用本地预览URL作为示例
      mediaUrls.push(fileItem.preview)
    }

    const scheduleDateTime = `${scheduleDate.value} ${scheduleTime.value}:00`

    const requestData = {
      homepageIds: selectedHomepages.value,
      platformTypes: selectedPlatforms.value.map(Number),
      postContent: postContent.value.trim(),
      mediaUrls: mediaUrls,
      scheduleTime: scheduleDateTime
    }

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    showNotification('定时任务创建成功！', 'success')
    emit('success', requestData)
    emit('close')
  } catch (error) {
    console.error('创建定时任务失败:', error)
    showNotification(error.message || '创建失败，请重试', 'error')
  } finally {
    isSubmitting.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  // 设置默认时间为1小时后
  const now = new Date()
  const defaultTime = new Date(now.getTime() + 60 * 60 * 1000)
  
  // 确保正确的日期格式 (YYYY-MM-DD)
  const year = defaultTime.getFullYear()
  const month = String(defaultTime.getMonth() + 1).padStart(2, '0')
  const day = String(defaultTime.getDate()).padStart(2, '0')
  scheduleDate.value = `${year}-${month}-${day}`
  
  // 确保正确的时间格式 (HH:MM)
  const hours = String(defaultTime.getHours()).padStart(2, '0')
  const minutes = String(defaultTime.getMinutes()).padStart(2, '0')
  scheduleTime.value = `${hours}:${minutes}`
})
</script>

<style scoped>
/* 自定义样式 */
.aspect-square {
  aspect-ratio: 1 / 1;
}

/* 下拉框选项悬停效果 */
select option:hover {
  background-color: #f5f7fa;
}

/* 确保下拉框在展开时有正确的样式 */
select:focus {
  border-color: #667eea !important;
}
</style>