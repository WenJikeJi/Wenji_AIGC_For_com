<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center">
    <!-- 背景遮罩 -->
    <div 
      class="absolute inset-0 bg-black bg-opacity-60 backdrop-blur-sm transition-opacity duration-300"
      @click="closeModal"
    ></div>
    
    <!-- 弹窗内容 -->
    <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 transform transition-all duration-300 scale-100">
      <!-- 弹窗头部 -->
      <div class="bg-gradient-to-r from-purple-600 to-indigo-600 text-white p-6 rounded-t-2xl">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="w-10 h-10 bg-white bg-opacity-20 rounded-lg flex items-center justify-center mr-3">
              <i class="fas fa-key text-white text-lg"></i>
            </div>
            <h3 class="text-xl font-bold">修改密码</h3>
          </div>
          <button 
            @click="closeModal"
            class="w-8 h-8 bg-white bg-opacity-20 rounded-lg flex items-center justify-center hover:bg-opacity-30 transition-all duration-200"
          >
            <i class="fas fa-times text-white"></i>
          </button>
        </div>
      </div>

      <!-- 弹窗内容 -->
      <div class="p-6">
        <!-- 修改方式选择 -->
        <div class="mb-6">
          <div class="flex bg-gray-100 rounded-xl p-1">
            <button
              @click="changeMethod = 'password'"
              :class="[
                'flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all duration-200',
                changeMethod === 'password' 
                  ? 'bg-white text-purple-600 shadow-sm' 
                  : 'text-gray-600 hover:text-gray-800'
              ]"
            >
              <i class="fas fa-lock mr-2"></i>
              旧密码验证
            </button>
            <button
              @click="changeMethod = 'email'"
              :class="[
                'flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all duration-200',
                changeMethod === 'email' 
                  ? 'bg-white text-purple-600 shadow-sm' 
                  : 'text-gray-600 hover:text-gray-800'
              ]"
            >
              <i class="fas fa-envelope mr-2"></i>
              邮箱验证码
            </button>
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-if="errorMessage" class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
          <div class="flex items-center text-red-700">
            <i class="fas fa-exclamation-circle mr-2"></i>
            <span class="text-sm">{{ errorMessage }}</span>
          </div>
        </div>

        <!-- 旧密码验证方式 -->
        <div v-if="changeMethod === 'password'" class="space-y-4">
          <!-- 当前密码 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <i class="fas fa-lock text-gray-500 mr-2"></i>
              当前密码
            </label>
            <input
              v-model="form.oldPassword"
              type="password"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-300 bg-gray-50 hover:bg-white"
              placeholder="请输入当前密码"
            />
          </div>
        </div>

        <!-- 邮箱验证码方式 -->
        <div v-if="changeMethod === 'email'" class="space-y-4">
          <!-- 邮箱地址 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <i class="fas fa-envelope text-gray-500 mr-2"></i>
              邮箱地址
            </label>
            <input
              v-model="form.email"
              type="email"
              readonly
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl bg-gray-100 text-gray-600"
              :placeholder="userEmail || '未设置邮箱'"
            />
          </div>

          <!-- 验证码 -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <i class="fas fa-shield-alt text-gray-500 mr-2"></i>
              邮箱验证码
            </label>
            <div class="flex space-x-3">
              <input
                v-model="form.verificationCode"
                type="text"
                class="flex-1 px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-300 bg-gray-50 hover:bg-white"
                placeholder="请输入验证码"
              />
              <button
                @click="sendVerificationCode"
                :disabled="codeSending || countdown > 0"
                class="px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl hover:from-blue-600 hover:to-blue-700 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed whitespace-nowrap font-medium"
              >
                <i v-if="codeSending" class="fas fa-spinner fa-spin mr-1"></i>
                {{ codeSending ? '发送中' : countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 新密码 -->
        <div class="space-y-4 mt-6">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <i class="fas fa-key text-gray-500 mr-2"></i>
              新密码
            </label>
            <input
              v-model="form.newPassword"
              type="password"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-300 bg-gray-50 hover:bg-white"
              placeholder="密码长度≥8位，需包含大小写字母、数字和特殊字符"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <i class="fas fa-shield-alt text-gray-500 mr-2"></i>
              确认新密码
            </label>
            <input
              v-model="form.confirmPassword"
              type="password"
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-300 bg-gray-50 hover:bg-white"
              placeholder="请再次输入新密码"
            />
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex space-x-3 mt-8">
          <button
            @click="closeModal"
            class="flex-1 py-3 border-2 border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 transition-all duration-300 font-medium"
          >
            取消
          </button>
          <button
            @click="submitPasswordChange"
            :disabled="submitting || !isFormValid"
            class="flex-1 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl hover:from-purple-700 hover:to-indigo-700 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 font-semibold"
          >
            <i v-if="submitting" class="fas fa-spinner fa-spin mr-2"></i>
            {{ submitting ? '修改中...' : '确认修改' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'ChangePasswordModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    userId: {
      type: [String, Number],
      required: true
    },
    userEmail: {
      type: String,
      default: ''
    }
  },
  emits: ['update:visible', 'success'],
  setup(props, { emit }) {
    const changeMethod = ref('password') // 'password' 或 'email'
    const submitting = ref(false)
    const codeSending = ref(false)
    const countdown = ref(0)
    const errorMessage = ref('')

    const form = ref({
      oldPassword: '',
      email: props.userEmail,
      verificationCode: '',
      newPassword: '',
      confirmPassword: ''
    })

    // 监听用户邮箱变化
    watch(() => props.userEmail, (newEmail) => {
      form.value.email = newEmail
    })

    // 表单验证
    const isFormValid = computed(() => {
      const { newPassword, confirmPassword } = form.value
      
      if (!newPassword || !confirmPassword) return false
      if (newPassword !== confirmPassword) return false
      if (newPassword.length < 8) return false
      
      if (changeMethod.value === 'password') {
        return !!form.value.oldPassword
      } else {
        return !!form.value.verificationCode
      }
    })

    // 发送验证码
    const sendVerificationCode = async () => {
      if (!form.value.email) {
        errorMessage.value = '用户未设置邮箱地址'
        return
      }

      try {
        codeSending.value = true
        errorMessage.value = ''
        
        const response = await fetch('/api/auth/send-verification-code', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email: form.value.email,
            type: 'password_reset'
          })
        })

        if (response.ok) {
          // 开始倒计时
          countdown.value = 60
          const timer = setInterval(() => {
            countdown.value--
            if (countdown.value <= 0) {
              clearInterval(timer)
            }
          }, 1000)
        } else {
          const error = await response.json()
          errorMessage.value = error.message || '发送验证码失败'
        }
      } catch (error) {
        errorMessage.value = '网络错误，请稍后重试'
      } finally {
        codeSending.value = false
      }
    }

    // 提交密码修改
    const submitPasswordChange = async () => {
      if (!isFormValid.value) return

      try {
        submitting.value = true
        errorMessage.value = ''

        const requestData = {
          userId: props.userId,
          newPassword: form.value.newPassword,
          confirmPassword: form.value.confirmPassword
        }

        if (changeMethod.value === 'password') {
          requestData.oldPassword = form.value.oldPassword
        } else {
          requestData.email = form.value.email
          requestData.verificationCode = form.value.verificationCode
        }

        const response = await fetch('/api/user/change-password', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestData)
        })

        if (response.ok) {
          emit('success')
          closeModal()
        } else {
          const error = await response.json()
          errorMessage.value = error.message || '密码修改失败'
        }
      } catch (error) {
        errorMessage.value = '网络错误，请稍后重试'
      } finally {
        submitting.value = false
      }
    }

    // 关闭弹窗
    const closeModal = () => {
      // 重置表单
      form.value = {
        oldPassword: '',
        email: props.userEmail,
        verificationCode: '',
        newPassword: '',
        confirmPassword: ''
      }
      errorMessage.value = ''
      changeMethod.value = 'password'
      emit('update:visible', false)
    }

    return {
      changeMethod,
      submitting,
      codeSending,
      countdown,
      errorMessage,
      form,
      isFormValid,
      sendVerificationCode,
      submitPasswordChange,
      closeModal
    }
  }
}
</script>