<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-lg max-w-3xl w-full mx-4 max-h-[90vh] overflow-y-auto">
      <div class="p-6">
        <h2 class="text-2xl font-bold text-gray-800 mb-6">定时发布内容</h2>
        
        <!-- 社交媒体平台选择 -->
        <div class="mb-6">
          <label class="block text-gray-700 font-medium mb-2">选择社交媒体平台</label>
          <div class="flex flex-wrap gap-3">
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
                class="peer absolute opacity-0"
              />
              <label 
                :for="platform.id"
                class="flex items-center px-4 py-3 bg-gray-100 rounded-lg cursor-pointer transition-all peer-checked:bg-blue-100 peer-checked:border-blue-500 border border-transparent"
              >
                <i :class="`fab ${platform.icon} mr-3 text-xl ${platform.color}`"></i>
                <span class="font-medium">{{ platform.name }}</span>
                <span v-if="platform.status !== 'connected'" class="ml-2 text-xs px-2 py-1 bg-red-100 text-red-800 rounded-full">未连接</span>
                <span v-else class="ml-2 text-xs px-2 py-1 bg-green-100 text-green-800 rounded-full">已连接</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 内容输入 -->
        <div class="mb-6">
          <label for="postContent" class="block text-gray-700 font-medium mb-2">发布内容</label>
          <textarea 
            id="postContent"
            v-model="postContent"
            rows="4"
            placeholder="请输入要发布的内容..."
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            maxlength="280"
          ></textarea>
          <div class="flex justify-between items-center mt-1">
            <span class="text-sm text-gray-500">{{ postContent.length }}/280</span>
            <span v-if="postContent.length > 250" class="text-sm text-yellow-600">接近限制</span>
          </div>
        </div>

        <!-- 媒体文件上传 -->
        <div class="mb-6">
          <label class="block text-gray-700 font-medium mb-2">添加图片/视频</label>
          <div class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-blue-500 transition-colors cursor-pointer">
            <i class="fas fa-cloud-upload-alt text-4xl text-gray-400 mb-3"></i>
            <p class="text-gray-500">点击或拖拽文件到此处上传</p>
            <p class="text-gray-400 text-sm mt-1">支持JPG、PNG、GIF、MP4格式，最大10MB</p>
            <input 
              type="file" 
              multiple
              accept="image/*,video/mp4"
              class="hidden"
              @change="handleFileUpload"
              ref="fileInput"
            />
          </div>
          
          <!-- 已上传文件预览 -->
          <div v-if="uploadedFiles.length > 0" class="grid grid-cols-4 gap-3 mt-3">
            <div v-for="(file, index) in uploadedFiles" :key="index" class="relative group">
              <img 
                v-if="file.type.startsWith('image/')"
                :src="file.preview"
                alt="上传预览"
                class="w-full h-20 object-cover rounded-lg border border-gray-200"
              />
              <video 
                v-else-if="file.type.startsWith('video/')"
                :src="file.preview"
                class="w-full h-20 object-cover rounded-lg border border-gray-200"
              ></video>
              <button 
                type="button"
                class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                @click="removeFile(index)"
              >
                <i class="fas fa-times text-xs"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- 发布时间设置 -->
        <div class="mb-6">
          <label class="block text-gray-700 font-medium mb-2">发布时间</label>
          <input 
            type="datetime-local"
            v-model="scheduledDateTime"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            :min="minDateTime"
          />
          <p class="mt-1 text-sm text-gray-500">请选择未来的时间进行发布</p>
        </div>

        <!-- 高级选项（可折叠） -->
        <div class="mb-6">
          <button 
            type="button"
            class="flex items-center text-blue-600 hover:text-blue-800 transition-colors"
            @click="showAdvancedOptions = !showAdvancedOptions"
          >
            <span>高级选项</span>
            <i :class="`fas ml-2 transition-transform ${showAdvancedOptions ? 'fa-chevron-up' : 'fa-chevron-down'}`"></i>
          </button>
          
          <div v-if="showAdvancedOptions" class="mt-4 space-y-4">
            <!-- Facebook特定选项 -->
            <div v-if="selectedPlatforms.includes('facebook')" class="p-4 bg-blue-50 rounded-lg">
              <h4 class="font-medium text-gray-800 mb-2">Facebook设置</h4>
              <div>
                <label class="inline-flex items-center">
                  <input 
                    type="checkbox" 
                    v-model="facebookOptions.commentEnabled"
                    class="text-blue-600 focus:ring-blue-500"
                  />
                  <span class="ml-2">允许评论</span>
                </label>
              </div>
            </div>
            
            <!-- Instagram特定选项 -->
            <div v-if="selectedPlatforms.includes('instagram')" class="p-4 bg-pink-50 rounded-lg">
              <h4 class="font-medium text-gray-800 mb-2">Instagram设置</h4>
              <div>
                <label class="inline-flex items-center">
                  <input 
                    type="checkbox" 
                    v-model="instagramOptions.allowComments"
                    class="text-blue-600 focus:ring-blue-500"
                  />
                  <span class="ml-2">允许评论</span>
                </label>
              </div>
            </div>
            
            <!-- TikTok特定选项 -->
            <div v-if="selectedPlatforms.includes('tiktok')" class="p-4 bg-black bg-opacity-5 rounded-lg">
              <h4 class="font-medium text-gray-800 mb-2">TikTok设置</h4>
              <div>
                <label class="inline-flex items-center">
                  <input 
                    type="checkbox" 
                    v-model="tiktokOptions.allowDuet"
                    class="text-blue-600 focus:ring-blue-500"
                  />
                  <span class="ml-2">允许Duet</span>
                </label>
                <label class="inline-flex items-center ml-4">
                  <input 
                    type="checkbox" 
                    v-model="tiktokOptions.allowStitch"
                    class="text-blue-600 focus:ring-blue-500"
                  />
                  <span class="ml-2">允许Stitch</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- 标签和提及 -->
        <div class="mb-8">
          <label class="block text-gray-700 font-medium mb-2">标签（用空格分隔）</label>
          <input 
            type="text"
            v-model="tags"
            placeholder="例如: #社交媒体 #营销 #创业"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- 固定底部按钮 -->
        <div class="p-6 border-t border-gray-200 flex-shrink-0 bg-white rounded-b-lg">
          <div class="flex flex-wrap gap-3 justify-end">
            <button 
              type="button"
              class="px-6 py-3 bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium rounded-lg transition-colors"
              @click="handleCancel"
            >
              取消
            </button>
            <button 
              type="button"
              class="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors flex items-center"
              @click="handleSubmit"
              :disabled="!isFormValid || isSubmitting"
            >
              <i v-if="isSubmitting" class="fas fa-spinner fa-spin mr-2"></i>
              <span>{{ isSubmitting ? '安排中...' : '安排发布' }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import apiUtil from '../../utils/api.js'
const { socialMediaAPI } = apiUtil

export default {
  name: 'ScheduledPostForm',
  setup() {
    // 表单状态
    const selectedPlatforms = ref([])
    const postContent = ref('')
    const uploadedFiles = ref([])
    const scheduledDateTime = ref('')
    const showAdvancedOptions = ref(false)
    const tags = ref('')
    const isSubmitting = ref(false)
    const fileInput = ref(null)
    
    // 平台特定选项
    const facebookOptions = ref({
      commentEnabled: true
    })
    
    const instagramOptions = ref({
      allowComments: true
    })
    
    const tiktokOptions = ref({
      allowDuet: true,
      allowStitch: true
    })
    
    // 平台数据
    const platforms = ref([
      {
        id: 'facebook',
        name: 'Facebook',
        icon: 'fa-facebook-square',
        color: 'text-blue-600',
        status: 'connected'
      },
      {
        id: 'instagram',
        name: 'Instagram',
        icon: 'fa-instagram',
        color: 'text-pink-600',
        status: 'connected'
      },
      {
        id: 'tiktok',
        name: 'TikTok',
        icon: 'fa-tiktok',
        color: 'text-black',
        status: 'connected'
      }
    ])
    
    // 最小日期时间（当前时间）
    const minDateTime = computed(() => {
      const now = new Date()
      return now.toISOString().slice(0, 16)
    })
    
    // 表单验证
    const isFormValid = computed(() => {
      return selectedPlatforms.value.length > 0 && 
             postContent.value.trim().length > 0 &&
             scheduledDateTime.value
    })
    
    // 处理文件上传
    const handleFileUpload = (event) => {
      const files = Array.from(event.target.files)
      
      files.forEach(file => {
        // 检查文件大小（10MB限制）
        if (file.size > 10 * 1024 * 1024) {
          alert(`文件 ${file.name} 太大，请选择小于10MB的文件`)
          return
        }
        
        // 创建文件预览
        const reader = new FileReader()
        reader.onload = (e) => {
          uploadedFiles.value.push({
            name: file.name,
            type: file.type,
            size: file.size,
            preview: e.target.result
          })
        }
        reader.readAsDataURL(file)
      })
      
      // 清空input以允许再次选择相同文件
      event.target.value = ''
    }
    
    // 移除文件
    const removeFile = (index) => {
      uploadedFiles.value.splice(index, 1)
    }
    
    // 处理取消
    const handleCancel = () => {
      if (confirm('确定要取消安排吗？')) {
        window.location.href = '/#/social-media'
      }
    }
    
    // 处理提交
    const handleSubmit = async () => {
      if (!isFormValid.value) return
      
      isSubmitting.value = true
      
      try {
        // 构建发布数据
        const postData = {
          platforms: selectedPlatforms.value,
          content: postContent.value,
          files: uploadedFiles.value.map(f => ({
            name: f.name,
            type: f.type,
            content: f.preview // 在实际应用中，这里应该是文件上传后的URL
          })),
          scheduledDateTime: scheduledDateTime.value,
          tags: tags.value.split(' ').filter(t => t),
          platformOptions: {
            facebook: facebookOptions.value,
            instagram: instagramOptions.value,
            tiktok: tiktokOptions.value
          }
        }
        
        // 发送发布请求
        const response = await socialMediaAPI.schedulePost(postData)
        
        if (response && response.status === 'success') {
          alert('发布安排成功！')
          window.location.href = '/#/social-media'
        } else {
          alert('安排失败：' + (response.message || '未知错误'))
        }
      } catch (error) {
        console.error('安排失败:', error)
        alert('安排失败，请稍后重试')
      } finally {
        isSubmitting.value = false
      }
    }
    
    // 组件挂载时初始化
    onMounted(() => {
      // 加载平台连接状态
      loadPlatformStatus()
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
      scheduledDateTime,
      showAdvancedOptions,
      tags,
      isSubmitting,
      fileInput,
      facebookOptions,
      instagramOptions,
      tiktokOptions,
      platforms,
      minDateTime,
      isFormValid,
      handleFileUpload,
      removeFile,
      handleCancel,
      handleSubmit
    }
  }
}
</script>

<style scoped>
/* 自定义样式 */
input[type="checkbox"]:peer-checked + label {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

input[type="checkbox"]:peer-checked + label i {
  color: #3b82f6;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .grid-cols-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>