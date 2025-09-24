<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full mx-4 max-h-[85vh] flex flex-col">
      <!-- 固定的装饰性顶部 -->
      <div class="relative bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-600 rounded-t-2xl p-8 overflow-hidden flex-shrink-0">
        <!-- 背景装饰元素 -->
        <div class="absolute inset-0 opacity-20">
          <div class="absolute top-0 left-0 w-32 h-32 bg-white rounded-full -translate-x-16 -translate-y-16"></div>
          <div class="absolute top-0 right-0 w-24 h-24 bg-white rounded-full translate-x-12 -translate-y-12"></div>
          <div class="absolute bottom-0 left-1/3 w-20 h-20 bg-white rounded-full translate-y-10"></div>
        </div>
        
        <!-- 标题内容 -->
        <div class="relative z-10 flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <div class="w-12 h-12 bg-white bg-opacity-20 rounded-xl flex items-center justify-center backdrop-blur-sm">
              <i class="fas fa-edit text-white text-xl"></i>
            </div>
            <div>
              <h2 class="text-2xl font-bold text-white">创建帖子</h2>
              <p class="text-purple-100 text-sm">发布内容到社交媒体平台</p>
            </div>
          </div>
          
          <!-- 关闭按钮 -->
          <button 
            @click="handleCancel"
            class="w-10 h-10 bg-white bg-opacity-20 rounded-full flex items-center justify-center hover:bg-opacity-30 transition-all duration-200 backdrop-blur-sm"
          >
            <i class="fas fa-times text-white"></i>
          </button>
        </div>
      </div>
      
      <!-- 可滚动的内容区域 -->
      <div class="flex-1 overflow-y-auto p-8 space-y-6">
        
        <!-- 社交媒体平台选择 -->
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-4">选择社交媒体平台</h3>
          <div class="flex flex-wrap gap-4">
            <div 
              v-for="platform in platforms" 
              :key="platform.id"
              class="relative"
            >
              <input 
                type="checkbox" 
                :id="platform.id"
                :value="platform.id"
                v-model="selectedPlatforms"
                @change="onPlatformChange"
                class="hidden"
                :disabled="platform.status !== 'connected'"
              />
              <label 
                :for="platform.id"
                class="flex flex-col items-center p-4 rounded-xl border cursor-pointer transition-all duration-200 min-w-[100px]"
                :class="{
                  'border-gray-200 hover:border-gray-300 hover:bg-gray-50': !selectedPlatforms.includes(platform.id),
                  'border-blue-500 bg-blue-50': selectedPlatforms.includes(platform.id),
                  'opacity-50 cursor-not-allowed': platform.status !== 'connected'
                }"
              >
                <i :class="`fab ${platform.icon} text-2xl mb-2`" 
                   :style="{ color: platform.id === 'facebook' ? '#1877f2' : platform.id === 'instagram' ? '#E4405F' : platform.id === 'tiktok' ? '#000000' : '#1DA1F2' }"></i>
                <span class="text-sm font-medium text-gray-700">{{ platform.name }}</span>
                <span 
                  class="text-xs mt-1 px-2 py-1 rounded-full"
                  :class="{
                    'bg-green-100 text-green-800': platform.status === 'connected',
                    'bg-red-100 text-red-800': platform.status === 'disconnected'
                  }"
                >
                  {{ platform.status === 'connected' ? '已连接' : '未连接' }}
                </span>
              </label>
            </div>
          </div>
        </div>

        <!-- 主页选择 -->
        <div v-if="selectedPlatforms.length > 0">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">选择主页</h3>
          <HomepageSelector
            :homepages="availableHomepages"
            :selectedHomepages="selectedHomepages"
            :selectedPlatforms="selectedPlatforms"
            @update:selectedHomepages="selectedHomepages = $event"
          />
          <p class="text-sm text-gray-500 mt-3">
            请选择要发布到的主页。只显示已选择平台对应的主页。
          </p>
        </div>

        <!-- 帖子内容输入 -->
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-4">帖子内容</h3>
          <textarea 
            v-model="postContent"
            class="w-full h-32 px-4 py-3 rounded-xl border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 resize-none"
            placeholder="输入帖子内容..."
          ></textarea>
          <div class="mt-2 text-right text-sm text-gray-500">
            {{ postContent.length }} 字符
          </div>
        </div>
        
        <!-- 媒体文件上传区域 -->
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-4">上传媒体文件</h3>
          <div 
            class="border-2 border-dashed border-gray-200 rounded-xl p-8 text-center hover:border-blue-400 transition-all duration-200 cursor-pointer relative"
            @click="fileInput.click()"
            @dragover.prevent="handleDragOver"
            @dragenter.prevent="handleDragEnter"
            @dragleave.prevent="handleDragEnd"
            @drop.prevent="handleDrop"
          >
            <input 
              ref="fileInput"
              type="file"
              accept="image/*,video/*"
              multiple
              class="hidden"
              @change="handleFileUpload"
            />
            <i class="fas fa-cloud-upload-alt text-3xl text-gray-400 mb-3"></i>
            <p class="text-gray-600 mb-1">拖拽文件到此处或点击上传</p>
            <p class="text-gray-400 text-sm">支持 JPG, PNG, GIF, MP4 (最大 10MB)</p>
          </div>
          
          <!-- 已上传文件预览 -->
          <div v-if="uploadedFiles.length > 0" class="mt-4">
            <p class="text-sm text-gray-600 mb-3">已上传文件</p>
            <div class="flex flex-wrap gap-3">
              <div 
                v-for="(file, index) in uploadedFiles" 
                :key="index"
                class="relative w-24 h-24 rounded-xl overflow-hidden border border-gray-200 group"
              >
                <img 
                  v-if="file.type.startsWith('image/')"
                  :src="URL.createObjectURL(file)"
                  class="w-full h-full object-cover"
                  alt="预览图片"
                />
                <video 
                  v-else-if="file.type.startsWith('video/')"
                  :src="URL.createObjectURL(file)"
                  class="w-full h-full object-cover"
                  alt="预览视频"
                  controls
                  muted
                ></video>
                <button 
                  @click="removeFile(index)"
                  class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-50 flex items-center justify-center transition-all duration-200 opacity-0 group-hover:opacity-100"
                >
                  <i class="fas fa-trash text-white"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 发布时间选择 -->
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-4">发布时间</h3>
          <div class="flex items-center space-x-4">
            <div 
              class="flex-1 py-3 px-4 rounded-xl border cursor-pointer transition-all duration-200"
              :class="publishTime === 'immediate' ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'"
              @click="publishTime = 'immediate'"
            >
              <div class="flex items-center">
                <div 
                  class="w-5 h-5 rounded-full border flex items-center justify-center mr-3"
                  :class="publishTime === 'immediate' ? 'border-blue-500 bg-blue-500' : 'border-gray-300'"
                >
                  <i 
                    v-if="publishTime === 'immediate'"
                    class="fas fa-check text-white text-xs"
                  ></i>
                </div>
                <span class="text-gray-700">立即发布</span>
              </div>
            </div>
            
            <div 
              class="flex-1 py-3 px-4 rounded-xl border cursor-pointer transition-all duration-200"
              :class="publishTime === 'scheduled' ? 'border-blue-500 bg-blue-50' : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'"
              @click="publishTime = 'scheduled'"
            >
              <div class="flex items-center">
                <div 
                  class="w-5 h-5 rounded-full border flex items-center justify-center mr-3"
                  :class="publishTime === 'scheduled' ? 'border-blue-500 bg-blue-500' : 'border-gray-300'"
                >
                  <i 
                    v-if="publishTime === 'scheduled'"
                    class="fas fa-check text-white text-xs"
                  ></i>
                </div>
                <span class="text-gray-700">排期发布</span>
              </div>
            </div>
          </div>
          
          <!-- 排期日期时间选择 -->
          <div 
            v-if="publishTime === 'scheduled'"
            class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4"
          >
            <CustomDatePicker 
              v-model="scheduledDate"
              :min-date="minDate"
              class="w-full"
            />
            <CustomTimePicker 
              v-model="scheduledTime"
              class="w-full"
            />
          </div>
        </div>
        
        <!-- 标签输入区域 -->
        <div>
          <h3 class="text-lg font-semibold text-gray-800 mb-4">标签（可选）</h3>
          <div class="flex flex-wrap gap-2">
            <span 
              v-for="(tag, index) in tags"
              :key="index"
              class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800"
            >
              {{ tag }}
              <button 
                @click="tags.splice(index, 1)"
                class="ml-2 text-blue-600 hover:text-blue-800"
              >
                <i class="fas fa-times text-xs"></i>
              </button>
            </span>
            <input 
              type="text"
              v-model="newTag"
              @keyup.enter="addTag"
              placeholder="添加标签..."
              class="px-3 py-1 rounded-full text-sm border border-gray-200 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>
      </div>
      
      <!-- 底部操作按钮 -->
      <div class="border-t border-gray-100 p-8 bg-white rounded-b-2xl flex justify-end space-x-3">
        <button 
          @click="handleCancel"
          class="px-8 py-3 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-all duration-200 font-medium"
        >
          取消
        </button>
        <button 
          @click="handleSaveDraft"
          :disabled="!isFormValid || isSavingDraft"
          class="px-8 py-3 border border-blue-500 bg-white text-blue-600 rounded-xl hover:bg-blue-50 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 font-medium"
        >
          <i v-if="isSavingDraft" class="fas fa-spinner fa-spin mr-2"></i>
          <span>{{ isSavingDraft ? '保存中...' : '保存草稿' }}</span>
        </button>
        <button 
          @click="handleSubmit"
          :disabled="!isFormValid || isSubmitting"
          class="px-8 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-sm hover:shadow-md font-medium"
        >
          <i v-if="isSubmitting" class="fas fa-spinner fa-spin mr-2"></i>
          <span>{{ isSubmitting ? (publishTime === 'immediate' ? '发布中...' : '安排中...') : (publishTime === 'immediate' ? '立即发布' : '安排发布') }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import apiUtil from '../../utils/api.js'
import CustomDatePicker from '@/components/common/CustomDatePicker.vue'
import CustomTimePicker from '@/components/common/CustomTimePicker.vue'
import HomepageSelector from './HomepageSelector.vue'

const socialMediaAPI = apiUtil.socialMedia

// 导出组件
export default {
  name: 'CreatePostForm',
  components: {
    CustomDatePicker,
    CustomTimePicker,
    HomepageSelector
  },
  emits: ['close', 'success'],
  setup(props, { emit }) {
    // 表单状态变量
    const selectedPlatforms = ref([])
    const selectedHomepages = ref([])
    const availableHomepages = ref([])
    const postContent = ref('')
    const uploadedFiles = ref([])
    const publishTime = ref('immediate')
    const scheduledDate = ref('')
    const scheduledTime = ref('')
    const isSubmitting = ref(false)
    const isSavingDraft = ref(false)
    const fileInput = ref(null)
    const initialFormState = ref({})
    const hasFormChanged = ref(false)
    const tags = ref([])
    const newTag = ref('')
    const facebookOptions = ref({})
    const instagramOptions = ref({})
    const tiktokOptions = ref({})
    
    // 平台数据
    const platforms = ref([
      { id: 'facebook', name: 'Facebook', icon: 'fa-facebook-f', status: 'disconnected' },
      { id: 'instagram', name: 'Instagram', icon: 'fa-instagram', status: 'disconnected' },
      { id: 'tiktok', name: 'TikTok', icon: 'fa-tiktok', status: 'disconnected' },
      { id: 'twitter', name: 'Twitter', icon: 'fa-twitter', status: 'disconnected' }
    ])

    // 平台选择变化处理
    const onPlatformChange = () => {
      // 当平台选择变化时，清空不相关的主页选择
      if (selectedPlatforms.value.length === 0) {
        selectedHomepages.value = []
      } else {
        // 过滤掉不属于当前选中平台的主页
        const platformMap = {
          'facebook': '1',
          'instagram': '2',
          'tiktok': '3',
          'twitter': '4'
        }
        const selectedPlatformIds = selectedPlatforms.value.map(p => platformMap[p])
        selectedHomepages.value = selectedHomepages.value.filter(homepageId => {
          const homepage = availableHomepages.value.find(h => h.id === homepageId)
          return homepage && selectedPlatformIds.includes(homepage.platform.toString())
        })
      }
      
      // 获取对应平台的主页
      fetchHomepages()
      checkFormChanges()
    }

    // 获取主页列表
    const fetchHomepages = async () => {
      if (selectedPlatforms.value.length === 0) {
        availableHomepages.value = []
        return
      }

      try {
        // 平台ID映射
        const platformMap = {
          'facebook': '1',
          'instagram': '2',
          'tiktok': '3',
          'twitter': '4'
        }
        
        const platformIds = selectedPlatforms.value.map(p => platformMap[p]).filter(Boolean)
        
        if (platformIds.length === 0) {
          availableHomepages.value = []
          return
        }

        // 调用API获取主页列表
        const response = await socialMediaAPI.getHomepages(platformIds)
        
        if (response && response.status === 'success') {
          availableHomepages.value = response.data || []
        } else {
          availableHomepages.value = []
        }
      } catch (error) {
        console.error('获取主页列表失败:', error)
        availableHomepages.value = []
      }
    }
    
    // 最小日期（今天）
    const minDate = computed(() => {
      const today = new Date()
      return today.toISOString().split('T')[0]
    })
    
    // 格式化排期日期时间
    const formatScheduledDateTime = computed(() => {
      if (!scheduledDate.value || !scheduledTime.value) return ''
      return `${scheduledDate.value} ${scheduledTime.value}`
    })
    
    // 排期日期时间对象
    const scheduledDateTime = computed(() => {
      if (!scheduledDate.value || !scheduledTime.value) return null
      const [year, month, day] = scheduledDate.value.split('-')
      const [hours, minutes] = scheduledTime.value.split(':')
      return new Date(year, month - 1, day, hours, minutes)
    })
    
    // 检查表单是否有内容
    const hasContent = computed(() => {
      return postContent.value.trim().length > 0 || uploadedFiles.length > 0
    })
    
    // 表单验证
    const isFormValid = computed(() => {
      return (
        selectedPlatforms.value.length > 0 &&
        selectedHomepages.value.length > 0 &&
        hasContent.value &&
        (!isScheduled.value || (scheduledDate.value && scheduledTime.value))
      )
    })
    
    // 检查是否是排期发布
    const isScheduled = computed(() => {
      return publishTime.value === 'scheduled'
    })
    
    // 添加标签
    const addTag = () => {
      if (newTag.value.trim() && !tags.value.includes(newTag.value.trim())) {
        tags.value.push(newTag.value.trim())
        newTag.value = ''
      }
    }
    
    // 处理文件上传
    const handleFileUpload = (event) => {
      const files = Array.from(event.target.files)
      uploadedFiles.value = [...uploadedFiles.value, ...files]
      checkFormChanges()
    }
    
    // 移除文件
    const removeFile = (index) => {
      uploadedFiles.value.splice(index, 1)
      checkFormChanges()
    }
    
    // 拖拽事件处理
    const handleDragOver = (event) => {
      event.currentTarget.classList.add('border-purple-500')
    }
    
    const handleDragEnter = (event) => {
      event.currentTarget.classList.add('border-purple-500')
    }
    
    const handleDragEnd = (event) => {
      event.currentTarget.classList.remove('border-purple-500')
    }
    
    const handleDrop = (event) => {
      event.currentTarget.classList.remove('border-purple-500')
      const files = Array.from(event.dataTransfer.files)
      uploadedFiles.value = [...uploadedFiles.value, ...files]
      checkFormChanges()
    }
    
    // 检查表单是否有变动
    const checkFormChanges = () => {
      const currentFormState = {
        selectedPlatforms: [...selectedPlatforms.value],
        postContent: postContent.value.trim(),
        uploadedFiles: [...uploadedFiles.value],
        publishTime: publishTime.value,
        scheduledDate: scheduledDate.value,
        scheduledTime: scheduledTime.value
      }
      
      // 简单比较表单状态是否有变动
      hasFormChanged.value = JSON.stringify(currentFormState) !== JSON.stringify(initialFormState.value)
    }
    
    // 处理取消
    const handleCancel = () => {
      // 如果表单有变动，提示用户是否保存草稿
      if (hasFormChanged.value && hasContent.value) {
        const confirmSave = confirm('表单内容已更改，是否保存草稿？')
        if (confirmSave) {
          handleSaveDraft(true)
          return
        }
      }
      emit('close')
    }
    
    // 处理保存草稿
    const handleSaveDraft = async (isAutoSave = false) => {
      if (!isFormValid.value || isSavingDraft.value) return
      
      isSavingDraft.value = true
      
      try {
        const draftData = {
          platforms: selectedPlatforms.value,
          content: postContent.value,
          files: uploadedFiles.value.map(file => ({
            name: file.name,
            type: file.type,
            size: file.size
          })),
          publishTime: publishTime.value,
          scheduledDate: scheduledDate.value,
          scheduledTime: scheduledTime.value,
          tags: tags.value,
          options: {
            facebook: facebookOptions.value,
            instagram: instagramOptions.value,
            tiktok: tiktokOptions.value
          }
        }
        
        // 保存到本地存储作为草稿
        localStorage.setItem('socialMediaDraft', JSON.stringify({
          ...draftData,
          timestamp: new Date().toISOString()
        }))
        
        if (!isAutoSave) {
          // 显示保存成功的临时消息
          const successMessage = document.createElement('div')
          successMessage.className = 'fixed top-4 right-4 bg-green-500 text-white px-4 py-2 rounded-lg shadow-lg z-50 transform transition-all duration-500 opacity-0 translate-y-[-20px]'
          successMessage.textContent = '草稿保存成功！'
          document.body.appendChild(successMessage)
          
          // 淡入效果
          setTimeout(() => {
            successMessage.style.opacity = '1'
            successMessage.style.transform = 'translateY(0)'
          }, 10)
          
          // 3秒后淡出并移除
          setTimeout(() => {
            successMessage.style.opacity = '0'
            successMessage.style.transform = 'translateY(-20px)'
            setTimeout(() => {
              document.body.removeChild(successMessage)
            }, 500)
          }, 3000)
        }
        
        // 重置表单变动状态
        checkFormChanges()
        
        // 如果是自动保存，则不关闭弹窗
        if (isAutoSave) {
          return
        }
        
        emit('success', { type: 'draft', data: draftData })
      } catch (error) {
        console.error('保存草稿失败:', error)
        alert('保存草稿失败，请稍后重试')
      } finally {
        isSavingDraft.value = false
      }
    }
    
    // 处理提交
    const handleSubmit = async () => {
      if (!isFormValid.value || isSubmitting.value) return
      
      isSubmitting.value = true
      
      try {
        // 构建帖子数据
        const postData = {
          platforms: selectedPlatforms.value,
          content: postContent.value,
          publishTime: publishTime.value,
          scheduledDateTime: scheduledDateTime.value ? scheduledDateTime.value.toISOString() : null,
          tags: tags.value,
          options: {
            facebook: facebookOptions.value,
            instagram: instagramOptions.value,
            tiktok: tiktokOptions.value
          }
        }
        
        // 这里应该是实际的API调用
        // const response = await socialMediaAPI.publishPost(postData)
        
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        // 模拟成功响应
        const response = {
          status: 'success',
          data: {
            id: `post_${Date.now()}`,
            ...postData,
            createdAt: new Date().toISOString()
          }
        }
        
        // 清除本地草稿
        localStorage.removeItem('socialMediaDraft')
        
        emit('success', { type: 'publish', data: response.data })
      } catch (error) {
        console.error('发布帖子失败:', error)
        alert('发布帖子失败，请稍后重试')
      } finally {
        isSubmitting.value = false
      }
    }
    
    // 组件挂载时初始化
    onMounted(() => {
      // 加载平台连接状态
      loadPlatformStatus()
      
      // 设置默认时间为当前时间+1小时
      const now = new Date()
      now.setHours(now.getHours() + 1)
      scheduledDate.value = now.toISOString().split('T')[0]
      scheduledTime.value = now.toTimeString().slice(0, 5)
      
      // 更新初始表单状态，包含默认设置的时间
      initialFormState.value = {
        selectedPlatforms: [...selectedPlatforms.value],
        postContent: postContent.value.trim(),
        uploadedFiles: [...uploadedFiles.value],
        publishTime: publishTime.value,
        scheduledDate: scheduledDate.value,
        scheduledTime: scheduledTime.value
      }
    })
    
    // 加载平台连接状态
    const loadPlatformStatus = async () => {
      try {
        const response = await socialMediaAPI.getConnectedPlatforms()
        if (response && response.status === 'success') {
          // 更新平台状态
          platforms.value.forEach(platform => {
            const connected = response.data.find(p => p.id === platform.id)
            platform.status = connected ? 'connected' : 'disconnected'
          })
        }
      } catch (error) {
        console.error('加载平台状态失败:', error)
      }
    }
    
    return {
      selectedPlatforms,
      postContent,
      uploadedFiles,
      publishTime,
      scheduledDate,
      scheduledTime,
      isSubmitting,
      isSavingDraft,
      fileInput,
      initialFormState,
      hasFormChanged,
      hasContent,
      platforms,
      minDate,
      formatScheduledDateTime,
      scheduledDateTime,
      isFormValid,
      isScheduled,
      tags,
      newTag,
      facebookOptions,
      instagramOptions,
      tiktokOptions,
      handleFileUpload,
      removeFile,
      handleDragOver,
      handleDragEnter,
      handleDragEnd,
      handleDrop,
      checkFormChanges,
      handleCancel,
      handleSaveDraft,
      handleSubmit,
      addTag
    }
  }
}
</script>

<style scoped>
/* 复选框样式优化 */
input[type="checkbox"]:checked + label {
  border-color: #7c3aed;
  background-color: #f5f3ff;
}

/* 拖拽排序样式 */
.dragging {
  opacity: 0.5;
  border: 2px dashed #7c3aed;
}

/* 日期时间选择器样式优化 */
.custom-date-picker,
.custom-time-picker {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  transition: all 0.2s ease-in-out;
}

.custom-date-picker:focus,
.custom-time-picker:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 2px rgba(124, 58, 237, 0.25);
}

/* 响应式设计调整 */
@media (max-width: 640px) {
  .max-w-4xl {
    max-width: 95vw;
  }
  
  .p-6 {
    padding: 1rem;
  }
  
  .gap-4 {
    gap: 1rem;
  }
  
  .grid-cols-1.md\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
}

/* 日期时间选择器的全局样式覆盖 */
:deep(.date-picker),
:deep(.time-picker) {
  /* 自定义日期时间选择器的样式 */
  --date-picker-primary-color: #7c3aed;
  --date-picker-background: #ffffff;
  --date-picker-text: #1f2937;
  --date-picker-border: #d1d5db;
  --date-picker-hover: #f9fafb;
}

/* 自定义下拉菜单样式 */
:deep(.dropdown-menu) {
  border-radius: 0.5rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

/* 深度阴影效果 */
:deep(.shadow-depth) {
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
}

/* 淡入效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  animation: fadeIn 0.3s ease-out;
}
</style>