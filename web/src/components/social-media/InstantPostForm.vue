<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
      <div class="p-6">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-semibold text-gray-800">立即发布</h2>
          <button 
            @click="closeForm"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <i class="fas fa-times text-lg"></i>
          </button>
        </div>

        <form @submit.prevent="submitPost" class="space-y-6">
          <!-- 平台选择 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              发布平台 <span class="text-red-500">*</span>
            </label>
            <div class="flex space-x-4">
              <button
                type="button"
                @click="togglePlatform('1')"
                :class="[
                  'flex items-center px-4 py-2 rounded border transition-all duration-300',
                  selectedPlatforms.includes('1') 
                    ? 'bg-blue-50 border-blue-500 text-blue-700' 
                    : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'
                ]"
              >
                <i class="fab fa-facebook-f mr-2"></i>
                Facebook
              </button>
              <button
                type="button"
                @click="togglePlatform('2')"
                :class="[
                  'flex items-center px-4 py-2 rounded border transition-all duration-300',
                  selectedPlatforms.includes('2') 
                    ? 'bg-pink-50 border-pink-500 text-pink-700' 
                    : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'
                ]"
              >
                <i class="fab fa-instagram mr-2"></i>
                Instagram
              </button>
            </div>
            <p v-if="platformError" class="mt-1 text-sm text-red-500">{{ platformError }}</p>
          </div>

          <!-- 主页选择 -->
          <HomepageSelector
            v-model="selectedHomepages"
            :platforms="selectedPlatforms"
            :error="homepageError"
            @update:modelValue="handleHomepageToggle"
          />

          <!-- 帖子标题输入框 -->
          <div>
            <label for="postTitle" class="block text-sm font-medium text-gray-700 mb-2">
              帖子标题
            </label>
            <input 
              id="postTitle"
              v-model="postTitle"
              type="text"
              placeholder="请输入帖子标题..."
              maxlength="100"
              class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded bg-white focus:outline-none focus:border-blue-500"
              style="padding: 6px; border: 1px solid #e0e0e0; background: white;"
            >
            <p class="mt-1 text-xs text-gray-500" style="font-size: 12px; color: #999;">
              Facebook 必填，Instagram 可选
            </p>
            <p v-if="titleError" class="mt-1 text-sm text-red-500">{{ titleError }}</p>
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
              class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded bg-white focus:outline-none focus:border-blue-500 resize-none"
              style="padding: 6px; border: 1px solid #e0e0e0; background: white; height: 100px; color: #999;"
            ></textarea>
            <p v-if="contentError" class="mt-1 text-sm text-red-500">{{ contentError }}</p>
          </div>

          <!-- 媒体文件区域 -->
          <MediaUploader
            v-model="uploadedFiles"
            @error="handleUploadError"
            :max-files="10"
            :max-size="100"
            :accept="['image/*', 'video/*']"
          />

          <!-- 底部操作按钮 -->
          <div class="flex justify-end space-x-4 pt-4">
            <button 
              type="button"
              @click="closeForm"
              class="font-medium rounded border transition-all duration-300 hover:bg-gray-50"
              style="width: 80px; padding: 8px 16px; border: 1px solid #e0e0e0; color: #333; border-radius: 4px;"
            >
              取消
            </button>
            <button 
              type="submit"
              :disabled="isSubmitting"
              class="text-white font-medium rounded transition-all duration-300 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
              style="width: 120px; background: linear-gradient(90deg, #D977F7, #F17CE0); border-radius: 4px; color: #FFF; padding: 8px 16px;"
            >
              <i v-if="isSubmitting" class="fas fa-spinner fa-spin mr-2"></i>
              <i v-else class="fas fa-rocket mr-2"></i>
              {{ isSubmitting ? '发布中...' : '立即发布' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { showNotification } from '@/utils/notification'
import { socialMediaAPI } from '@/utils/api.js'
import HomepageSelector from './HomepageSelector.vue'
import MediaUploader from '../common/MediaUploader.vue'

export default {
  name: 'InstantPostForm',
  components: {
    HomepageSelector,
    MediaUploader
  },
  emits: ['close', 'success'],
  setup(props, { emit }) {
    // 响应式数据
    const selectedHomepages = ref([])
    const selectedPlatforms = ref([])
    const postTitle = ref('')
    const postContent = ref('')
    const uploadedFiles = ref([])
    const availableHomepages = ref([])
    const isSubmitting = ref(false)
    const isDragOver = ref(false)
    const showHomepageDropdown = ref(false)

    // 错误信息
    const homepageError = ref('')
    const platformError = ref('')
    const titleError = ref('')
    const contentError = ref('')
    const fileError = ref('')

    // 移除模拟数据，将从API获取真实数据

    // 计算属性：过滤主页选项（基于选中的平台）
    const filteredHomepages = computed(() => {
      return availableHomepages.value.filter(homepage => 
        selectedPlatforms.value.includes(homepage.platform.toString())
      )
    })

    // 获取可用主页列表
    const fetchHomepages = async () => {
      try {
        // 调用后端API获取所有可用的社交媒体主页
        const response = await socialMediaAPI.getHomepages()
        // 格式化返回的数据，确保平台ID为字符串格式
        availableHomepages.value = response.map(homepage => ({
          ...homepage,
          platform: homepage.platform.toString()
        }))
      } catch (error) {
        console.error('获取主页列表失败:', error)
        showNotification('获取主页列表失败', 'error')
      }
    }

    // 获取平台名称
    const getPlatformName = (platformId) => {
      if (platformId === '1') return 'Facebook'
      if (platformId === '2') return 'Instagram'
      return '未知平台'
    }

    // 切换平台选择
    const togglePlatform = (platformId) => {
      const index = selectedPlatforms.value.indexOf(platformId)
      if (index > -1) {
        selectedPlatforms.value.splice(index, 1)
      } else {
        selectedPlatforms.value.push(platformId)
      }
      platformError.value = ''
      
      // 当平台选择改变时，过滤掉不属于所选平台的主页
      selectedHomepages.value = selectedHomepages.value.filter(homepageId => {
        const homepage = availableHomepages.value.find(h => h.id === homepageId)
        return homepage && selectedPlatforms.value.includes(homepage.platform.toString())
      })
    }

    // 主页选择处理
    const handleHomepageToggle = (homepageId) => {
      console.log('Homepage toggled:', homepageId)
    }

    // 处理上传错误
    const handleUploadError = (error) => {
      fileError.value = error
    }

    // 表单验证
    const validateForm = () => {
      let isValid = true

      // 重置错误信息
      homepageError.value = ''
      platformError.value = ''
      titleError.value = ''
      contentError.value = ''

      // 验证平台选择
      if (selectedPlatforms.value.length === 0) {
        platformError.value = '请选择至少一个发布平台'
        isValid = false
      }

      // 验证主页选择
      if (selectedHomepages.value.length === 0) {
        homepageError.value = '请选择至少一个发布主页'
        isValid = false
      }

      // 验证标题（Facebook必填）
      if (selectedPlatforms.value.includes('1') && !postTitle.value.trim()) {
        titleError.value = 'Facebook发布时标题为必填项'
        isValid = false
      }

      // 验证内容
      if (!postContent.value.trim()) {
        contentError.value = '请输入帖子内容'
        isValid = false
      }

      return isValid
    }

    // 提交表单
    const submitPost = async () => {
      if (!validateForm()) {
        return
      }

      isSubmitting.value = true

      try {
        // 准备媒体URL
        const mediaUrls = []
        for (const fileItem of uploadedFiles.value) {
          mediaUrls.push(fileItem.preview)
        }

        const requestData = {
          homepageIds: selectedHomepages.value,
          platformTypes: selectedPlatforms.value.map(Number),
          postTitle: postTitle.value.trim(),
          postContent: postContent.value.trim(),
          mediaUrls: mediaUrls
        }

        // 调用后端API发布帖子
        await socialMediaAPI.publishPost(requestData)
        
        showNotification('帖子发布成功！', 'success')
        emit('success', requestData)
        emit('close')
      } catch (error) {
        console.error('发布失败:', error)
        showNotification(error.message || '发布失败，请重试', 'error')
      } finally {
        isSubmitting.value = false
      }
    }

    // 组件挂载时获取数据
    onMounted(() => {
      fetchHomepages()
    })

    // 关闭表单
    const closeForm = () => {
      emit('close')
    }

    return {
      selectedHomepages,
      selectedPlatforms,
      postTitle,
      postContent,
      uploadedFiles,
      availableHomepages,
      filteredHomepages,
      isSubmitting,
      isDragOver,
      showHomepageDropdown,
      homepageError,
      platformError,
      titleError,
      contentError,
      fileError,
      togglePlatform,
      getPlatformName,
      handleHomepageToggle,
      handleUploadError,
      submitPost,
      closeForm
    }
  }
}
</script>

<style scoped>
/* 自定义样式 */
.aspect-square {
  aspect-ratio: 1 / 1;
}
.homepage-dropdown-list {
  max-height: 200px;
  overflow-y: auto;
}

.homepage-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.homepage-item:hover {
  background-color: #f5f5f5;
}

.homepage-item input[type="checkbox"] {
  margin-right: 10px;
}

.platform-badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}
</style>