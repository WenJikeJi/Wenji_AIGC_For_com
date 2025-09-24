<template>
  <!-- Token配置弹窗 -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-white rounded-lg shadow-xl relative" style="width: 600px; max-height: 90vh; overflow-y: auto;">
      <!-- 关闭按钮 -->
      <button 
        @click="$emit('close')"
        class="absolute top-4 right-4 w-8 h-8 flex items-center justify-center text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full transition-all duration-200 z-10"
        type="button"
        title="关闭"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
      </button>
      
      <!-- 弹窗内容 -->
      <div class="p-6">
        <!-- 标题区域 -->
        <div class="mb-6">
          <div class="flex items-center mb-2">
            <div class="w-10 h-10 rounded-full flex items-center justify-center mr-3"
                 :class="getPlatformIconBg(platform)">
              <i :class="getPlatformIcon(platform)" class="text-lg text-white"></i>
            </div>
            <h2 class="text-xl font-bold text-gray-900">
              手动配置{{ getPlatformName(platform) }} Token
            </h2>
          </div>
          <p class="text-gray-600 text-sm mb-4">
            输入您的{{ getPlatformName(platform) }}访问令牌以授权系统访问您的账户
          </p>
          

          
          <!-- 使用说明卡片 -->
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
            <div class="flex items-start">
              <div class="flex-shrink-0">
                <i class="fas fa-info-circle text-blue-500 mt-0.5"></i>
              </div>
              <div class="ml-3">
                <h3 class="text-sm font-medium text-blue-900 mb-2">如何获取{{ getPlatformName(platform) }} Token？</h3>
                <div class="text-sm text-blue-800 space-y-1">
                  <div v-if="platform === 'facebook'">
                    <p>1. 访问 <a href="https://developers.facebook.com/tools/explorer/" target="_blank" class="underline hover:text-blue-600">Facebook Graph API Explorer</a></p>
                    <p>2. 选择您的应用程序并生成用户访问令牌</p>
                    <p>3. 确保令牌包含以下权限：pages_manage_posts, pages_read_engagement</p>
                    <p>4. 复制生成的访问令牌并粘贴到下方输入框</p>
                  </div>
                  <div v-else-if="platform === 'instagram'">
                    <p>1. 访问 <a href="https://developers.facebook.com/docs/instagram-basic-display-api/" target="_blank" class="underline hover:text-blue-600">Instagram Basic Display API</a></p>
                    <p>2. 创建应用并获取用户访问令牌</p>
                    <p>3. 确保令牌包含 instagram_basic 权限</p>
                    <p>4. 复制生成的访问令牌并粘贴到下方输入框</p>
                  </div>
                  <div v-else-if="platform === 'tiktok'">
                    <p>1. 访问 <a href="https://developers.tiktok.com/" target="_blank" class="underline hover:text-blue-600">TikTok for Developers</a></p>
                    <p>2. 创建应用并完成OAuth授权流程</p>
                    <p>3. 获取用户访问令牌</p>
                    <p>4. 复制生成的访问令牌并粘贴到下方输入框</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 表单区域 -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Access Token输入 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fas fa-key mr-1"></i>
              Access Token <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <textarea
                v-model="form.accessToken"
                :placeholder="getTokenPlaceholder(platform)"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 resize-none"
                rows="4"
                :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.accessToken }"
              ></textarea>
              <div class="absolute top-3 right-3">
                <button
                  type="button"
                  @click="toggleTokenVisibility"
                  class="text-gray-400 hover:text-gray-600 transition-colors"
                  title="显示/隐藏Token"
                >
                  <i :class="showToken ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
            </div>
            <p v-if="errors.accessToken" class="mt-1 text-sm text-red-600">{{ errors.accessToken }}</p>
            <p class="mt-1 text-xs text-gray-500">
              请确保Token具有必要的权限：{{ getRequiredPermissions(platform) }}
            </p>
          </div>



          <!-- Facebook APP ID（Facebook特有） -->
          <div v-if="platform === 'facebook'">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fab fa-facebook-f mr-1"></i>
              Facebook APP ID <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.appId"
              type="text"
              placeholder="输入Facebook应用ID..."
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.appId }"
            >
            <p v-if="errors.appId" class="mt-1 text-sm text-red-600">{{ errors.appId }}</p>
          </div>

          <!-- Facebook APP 密钥（Facebook特有） -->
          <div v-if="platform === 'facebook'">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              <i class="fab fa-facebook-f mr-1"></i>
              Facebook APP 密钥 <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="form.appSecret"
                :type="showAppSecret ? 'text' : 'password'"
                placeholder="输入Facebook应用密钥..."
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
                :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.appSecret }"
              >
              <div class="absolute top-3 right-3">
                <button
                  type="button"
                  @click="toggleAppSecretVisibility"
                  class="text-gray-400 hover:text-gray-600 transition-colors"
                  title="显示/隐藏密钥"
                >
                  <i :class="showAppSecret ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
            </div>
            <p v-if="errors.appSecret" class="mt-1 text-sm text-red-600">{{ errors.appSecret }}</p>
          </div>



          <!-- 验证状态显示 -->
          <div v-if="validationStatus" class="p-4 rounded-lg border" :class="getValidationStatusClass()">
            <div class="flex items-center">
              <i :class="getValidationStatusIcon()" class="mr-2"></i>
              <span class="font-medium">{{ validationStatus.message }}</span>
            </div>
            <div v-if="validationStatus.details" class="mt-2 text-sm opacity-90">
              {{ validationStatus.details }}
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-end space-x-4 pt-4 border-t border-gray-200">
            <button
              type="button"
              @click="$emit('close')"
              class="px-6 py-2 bg-gray-100 text-gray-700 rounded-lg font-medium hover:bg-gray-200 transition-all duration-200"
              :disabled="isValidating || isSaving"
            >
              取消
            </button>
            <button
              type="button"
              @click="validateToken"
              class="px-6 py-2 bg-blue-100 text-blue-700 border border-blue-300 rounded-lg font-medium hover:bg-blue-200 transition-all duration-200 flex items-center"
              :disabled="!form.accessToken.trim() || isValidating || isSaving"
            >
              <i v-if="isValidating" class="fas fa-spinner fa-spin mr-2"></i>
              <i v-else class="fas fa-check-circle mr-2"></i>
              {{ isValidating ? '验证中...' : '验证Token' }}
            </button>
            <button
              type="submit"
              class="px-6 py-2 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 transition-all duration-200 flex items-center"
              :disabled="!form.accessToken.trim() || isValidating || isSaving"
            >
              <i v-if="isSaving" class="fas fa-spinner fa-spin mr-2"></i>
              <i v-else class="fas fa-save mr-2"></i>
              {{ isSaving ? '保存中...' : '验证并保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { showNotification } from '@/utils/notification'

// Props
const props = defineProps({
  platform: {
    type: String,
    required: true,
    validator: value => ['facebook', 'instagram', 'tiktok'].includes(value)
  }
})

// Emits
const emit = defineEmits(['close', 'success'])

// 响应式数据
const showToken = ref(false)
const showAppSecret = ref(false)
const isValidating = ref(false)
const isSaving = ref(false)
const validationStatus = ref(null)

// 表单数据
const form = reactive({
  accessToken: '',
  appId: '', // Facebook专用
  appSecret: '' // Facebook专用
})

// 表单验证错误
const errors = reactive({
  accessToken: '',
  appId: '',
  appSecret: ''
})

// 计算属性和方法
const getPlatformIcon = (platform) => {
  const icons = {
    facebook: 'fab fa-facebook-f',
    instagram: 'fab fa-instagram',
    tiktok: 'fab fa-tiktok'
  }
  return icons[platform] || 'fas fa-globe'
}

const getPlatformIconBg = (platform) => {
  const backgrounds = {
    facebook: 'bg-blue-600',
    instagram: 'bg-gradient-to-r from-pink-500 to-purple-600',
    tiktok: 'bg-black'
  }
  return backgrounds[platform] || 'bg-gray-600'
}

const getPlatformName = (platform) => {
  const names = {
    facebook: 'Facebook',
    instagram: 'Instagram',
    tiktok: 'TikTok'
  }
  return names[platform] || platform
}

const getTokenPlaceholder = (platform) => {
  const placeholders = {
    facebook: 'EAABwzLixnjYBO...(Facebook长期访问令牌)',
    instagram: 'IGQVJXa1FMd2...(Instagram基本显示API令牌)',
    tiktok: 'act.example...(TikTok访问令牌)'
  }
  return placeholders[platform] || '请输入访问令牌...'
}

const getRequiredPermissions = (platform) => {
  const permissions = {
    facebook: 'pages_manage_posts, pages_read_engagement',
    instagram: 'user_profile, user_media',
    tiktok: 'user.info.basic, video.list'
  }
  return permissions[platform] || '基本权限'
}

const getValidationStatusClass = () => {
  if (!validationStatus.value) return ''
  
  switch (validationStatus.value.type) {
    case 'success':
      return 'bg-green-50 border-green-200 text-green-800'
    case 'error':
      return 'bg-red-50 border-red-200 text-red-800'
    case 'warning':
      return 'bg-yellow-50 border-yellow-200 text-yellow-800'
    default:
      return 'bg-blue-50 border-blue-200 text-blue-800'
  }
}

const getValidationStatusIcon = () => {
  if (!validationStatus.value) return ''
  
  switch (validationStatus.value.type) {
    case 'success':
      return 'fas fa-check-circle text-green-600'
    case 'error':
      return 'fas fa-times-circle text-red-600'
    case 'warning':
      return 'fas fa-exclamation-triangle text-yellow-600'
    default:
      return 'fas fa-info-circle text-blue-600'
  }
}

// 方法
const toggleTokenVisibility = () => {
  showToken.value = !showToken.value
  
  // 切换textarea的type（虽然textarea没有type属性，这里主要是为了UI反馈）
  const textarea = document.querySelector('textarea')
  if (textarea) {
    if (showToken.value) {
      textarea.style.fontFamily = 'monospace'
    } else {
      textarea.style.fontFamily = 'inherit'
    }
  }
}

const toggleAppSecretVisibility = () => {
  showAppSecret.value = !showAppSecret.value
}

const validateForm = () => {
  // 清空之前的错误
  Object.keys(errors).forEach(key => {
    errors[key] = ''
  })

  let isValid = true

  // 验证Access Token
  if (!form.accessToken.trim()) {
    errors.accessToken = 'Access Token不能为空'
    isValid = false
  } else if (form.accessToken.length < 10) {
    errors.accessToken = 'Access Token长度不能少于10个字符'
    isValid = false
  }

  // 验证Facebook APP ID和APP密钥
  if (props.platform === 'facebook') {
    if (!form.appId.trim()) {
      errors.appId = 'Facebook APP ID不能为空'
      isValid = false
    } else if (form.appId.length < 5) {
      errors.appId = 'Facebook APP ID长度不能少于5个字符'
      isValid = false
    }

    if (!form.appSecret.trim()) {
      errors.appSecret = 'Facebook APP 密钥不能为空'
      isValid = false
    } else if (form.appSecret.length < 10) {
      errors.appSecret = 'Facebook APP 密钥长度不能少于10个字符'
      isValid = false
    }
  }



  return isValid
}

const validateToken = async () => {
  if (!validateForm()) return

  isValidating.value = true
  validationStatus.value = null

  try {
    // 添加调试日志
    console.log('验证Token - 表单数据:', {
      accessToken: form.accessToken,
      appId: form.appId,
      appSecret: form.appSecret,
      platform: props.platform
    })

    // 导入socialMediaAPI和request函数
    const { socialMediaAPI, request } = await import('@/utils/api')

    // 设置请求超时
    const timeoutPromise = new Promise((_, reject) => {
      setTimeout(() => reject(new Error('网络错误')), 30000) // 30秒超时
    })

    // 验证结果
    let response
    let errorMessage = ''
    
    if (props.platform === 'facebook') {
      // 对于Facebook，使用专门的verifyFBToken方法
      try {
        const requestData = {
          token: form.accessToken,
          appId: form.appId,
          appSecret: form.appSecret
        }
        
        console.log('发送验证请求数据:', requestData)
        
        response = await Promise.race([
          socialMediaAPI.verifyFBToken(requestData),
          timeoutPromise
        ])
      } catch (fbError) {
        // 处理Facebook特定错误
        console.error('Facebook Token验证错误:', fbError)
        
        if (fbError.message === '网络错误') {
          throw new Error('网络错误')
        } else if (fbError.message.includes('APP ID') || fbError.message.includes('密钥')) {
          // 应用ID或密钥错误
          errorMessage = fbError.message
        } else {
          // 其他Facebook错误
          errorMessage = fbError.message || 'Token验证失败，请检查Token的有效性'
        }
        
        // 抛出格式化后的错误
        throw new Error(errorMessage)
      }
    } else {
      // 对于其他平台，使用通用的验证接口
      response = await Promise.race([
        request(`api/social-media/${props.platform}/verify-token`, 'POST', {
          token: form.accessToken
        }),
        timeoutPromise
      ])
    }

    if (response.valid) {
      validationStatus.value = {
        type: 'success',
        message: 'Token验证成功！',
        details: response.details || `已成功连接到${getPlatformName(props.platform)}账户，Token有效且具有必要权限。`
      }
    } else {
      // 从响应中获取具体错误信息
      const errorDetails = response.details || response.error || 'Token无效或权限不足'
      validationStatus.value = {
        type: 'error',
        message: 'Token验证失败',
        details: errorDetails
      }
    }

    showNotification(validationStatus.value.message, validationStatus.value.type === 'success' ? 'success' : 'error')

  } catch (error) {
    console.error('Token验证失败:', error)
    
    // 处理网络错误
    if (error.message === '网络错误') {
      validationStatus.value = {
        type: 'error',
        message: '网络错误',
        details: '请稍后重试'
      }
      showNotification('网络错误，请稍后重试', 'error')
    } else {
      // 显示具体错误信息
      validationStatus.value = {
        type: 'error',
        message: '验证失败',
        details: error.message || '验证过程中出现错误'
      }
      showNotification(error.message || 'Token验证失败，请重试', 'error')
    }
  } finally {
    isValidating.value = false
  }
}

const handleSubmit = async () => {
  if (!validateForm()) return

  isSaving.value = true

  try {
    // 如果还没有验证过，先验证
    if (!validationStatus.value || validationStatus.value.type !== 'success') {
      await validateToken()
      
      // 如果验证失败，不继续保存
      if (!validationStatus.value || validationStatus.value.type !== 'success') {
        return
      }
    }

    // 导入socialMediaAPI
    const { socialMediaAPI } = await import('@/utils/api')

    // 构造保存的数据
    const tokenData = {
      platform: props.platform,
      accessToken: form.accessToken,
      createdAt: new Date().toISOString()
    }

    // 设置请求超时
    const timeoutPromise = new Promise((_, reject) => {
      setTimeout(() => reject(new Error('网络错误')), 30000) // 30秒超时
    })

    // 根据不同平台调用不同的保存方法
    if (props.platform === 'facebook') {
      // 对于Facebook，调用saveFBToken方法，并传递APPID和APP密钥
      await Promise.race([
        socialMediaAPI.saveFBToken({
          accessToken: form.accessToken,
          appId: form.appId,
          appSecret: form.appSecret
        }),
        timeoutPromise
      ])
    } else {
      // 对于其他平台，使用统一的saveToken方法
      await Promise.race([
        socialMediaAPI.saveToken({
          platform: props.platform,
          token: form.accessToken
        }),
        timeoutPromise
      ])
    }

    // 从FB获取信息成功
    const successMessage = `${getPlatformName(props.platform)} Token配置成功！已成功获取账户信息。`
    validationStatus.value = {
      type: 'success',
      message: successMessage,
      details: ''
    }
    
    showNotification(successMessage, 'success')
    
    // 通知父组件保存成功
    emit('success', tokenData)
    
    // 关闭弹窗
    emit('close')

  } catch (error) {
    console.error('保存Token失败:', error)
    
    // 处理网络错误
    if (error.message === '网络错误') {
      showNotification('网络错误，请稍后重试', 'error')
    } else {
      // 显示具体错误信息
      showNotification(error.message || '保存失败，请重试', 'error')
    }
  } finally {
    isSaving.value = false
  }
}
</script>

<style scoped>
/* 自定义样式 */
textarea {
  font-family: inherit;
  transition: font-family 0.2s ease;
}

/* 渐变背景样式 */
.bg-gradient-to-r {
  background: linear-gradient(to right, var(--tw-gradient-stops));
}

/* 动画效果 */
.transition-all {
  transition: all 0.2s ease;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .fixed > div {
    width: 95% !important;
    margin: 0 auto;
  }
}
</style>