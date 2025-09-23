<template>
  <div class="min-h-screen bg-gradient-to-br from-purple-50 to-pink-100">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-3">
              <div class="w-8 h-8 bg-purple-600 rounded-lg flex items-center justify-center">
                <span class="text-white font-bold text-sm">文</span>
              </div>
              <span class="text-xl font-bold text-gray-900">文际信息</span>
            </router-link>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/login" class="text-gray-600 hover:text-purple-600 transition-colors">
              返回登录
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <!-- 页面标题 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">联系我们</h1>
        <p class="text-xl text-gray-600 max-w-3xl mx-auto">
          我们随时准备为您提供帮助。无论您有任何问题、建议或合作意向，都欢迎与我们联系。
        </p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
        <!-- 联系信息卡片 -->
        <div class="space-y-8">
          <!-- 公司信息 -->
          <div class="bg-white rounded-xl shadow-lg p-8">
            <div class="flex items-center mb-6">
              <div class="w-12 h-12 bg-purple-600 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-4m-5 0H9m0 0H7m2 0v-5a2 2 0 012-2h2a2 2 0 012 2v5m-6 0V9a2 2 0 012-2h2a2 2 0 012 2v8"></path>
                </svg>
              </div>
              <h2 class="text-2xl font-bold text-gray-900 ml-4">公司信息</h2>
            </div>
            
            <div class="space-y-4">
              <div class="flex items-start">
                <div class="w-6 h-6 text-purple-600 mt-1">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-4m-5 0H9m0 0H7m2 0v-5a2 2 0 012-2h2a2 2 0 012 2v5m-6 0V9a2 2 0 012-2h2a2 2 0 012 2v8"></path>
                  </svg>
                </div>
                <div class="ml-4">
                  <h3 class="font-semibold text-gray-900">公司名称</h3>
                  <p class="text-gray-600">广州文际信息科技有限公司</p>
                </div>
              </div>

              <div class="flex items-start">
                <div class="w-6 h-6 text-purple-600 mt-1">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  </svg>
                </div>
                <div class="ml-4">
                  <h3 class="font-semibold text-gray-900">办公地址</h3>
                  <p class="text-gray-600">广州市天河区珠江新城XXX大厦XX层</p>
                  <p class="text-sm text-gray-500">（具体地址将在正式运营后更新）</p>
                </div>
              </div>

              <div class="flex items-start">
                <div class="w-6 h-6 text-purple-600 mt-1">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                </div>
                <div class="ml-4">
                  <h3 class="font-semibold text-gray-900">营业时间</h3>
                  <p class="text-gray-600">周一至周五：9:00 - 18:00</p>
                  <p class="text-gray-600">周六：10:00 - 16:00</p>
                  <p class="text-gray-600">周日及法定节假日：休息</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 联系方式 -->
          <div class="bg-white rounded-xl shadow-lg p-8">
            <div class="flex items-center mb-6">
              <div class="w-12 h-12 bg-purple-600 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
                </svg>
              </div>
              <h2 class="text-2xl font-bold text-gray-900 ml-4">联系方式</h2>
            </div>

            <div class="space-y-6">
              <div class="flex items-center p-4 bg-purple-50 rounded-lg">
                <div class="w-8 h-8 text-purple-600">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                  </svg>
                </div>
                <div class="ml-4">
                  <h3 class="font-semibold text-gray-900">邮箱地址</h3>
                  <p class="text-purple-600">service@wenji.com</p>
                  <p class="text-sm text-gray-500">（上线后更新为正式邮箱）</p>
                </div>
              </div>

              <div class="flex items-center p-4 bg-purple-50 rounded-lg">
                <div class="w-8 h-8 text-purple-600">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
                  </svg>
                </div>
                <div class="ml-4">
                  <h3 class="font-semibold text-gray-900">客服热线</h3>
                  <p class="text-purple-600">400-XXX-XXXX</p>
                  <p class="text-sm text-gray-500">（上线后更新为正式号码）</p>
                </div>
              </div>

              <div class="flex items-center p-4 bg-purple-50 rounded-lg">
                <div class="w-8 h-8 text-purple-600">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                  </svg>
                </div>
                <div class="ml-4">
                  <h3 class="font-semibold text-gray-900">在线客服</h3>
                  <p class="text-gray-600">工作时间内提供在线咨询服务</p>
                  <p class="text-sm text-gray-500">（功能开发中）</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系表单 -->
        <div class="bg-white rounded-xl shadow-lg p-8">
          <div class="flex items-center mb-6">
            <div class="w-12 h-12 bg-purple-600 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
              </svg>
            </div>
            <h2 class="text-2xl font-bold text-gray-900 ml-4">发送消息</h2>
          </div>

          <form @submit.prevent="submitForm" class="space-y-6">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div>
                <label for="name" class="block text-sm font-medium text-gray-700 mb-2">姓名 *</label>
                <input
                  type="text"
                  id="name"
                  v-model="form.name"
                  required
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  placeholder="请输入您的姓名"
                >
              </div>
              <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-2">邮箱 *</label>
                <input
                  type="email"
                  id="email"
                  v-model="form.email"
                  required
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  placeholder="请输入您的邮箱"
                >
              </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div>
                <label for="phone" class="block text-sm font-medium text-gray-700 mb-2">电话</label>
                <input
                  type="tel"
                  id="phone"
                  v-model="form.phone"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  placeholder="请输入您的电话号码"
                >
              </div>
              <div>
                <label for="company" class="block text-sm font-medium text-gray-700 mb-2">公司</label>
                <input
                  type="text"
                  id="company"
                  v-model="form.company"
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  placeholder="请输入您的公司名称"
                >
              </div>
            </div>

            <div>
              <label for="subject" class="block text-sm font-medium text-gray-700 mb-2">主题 *</label>
              <select
                id="subject"
                v-model="form.subject"
                required
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
              >
                <option value="">请选择咨询主题</option>
                <option value="product">产品咨询</option>
                <option value="technical">技术支持</option>
                <option value="business">商务合作</option>
                <option value="complaint">投诉建议</option>
                <option value="other">其他</option>
              </select>
            </div>

            <div>
              <label for="message" class="block text-sm font-medium text-gray-700 mb-2">消息内容 *</label>
              <textarea
                id="message"
                v-model="form.message"
                required
                rows="6"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all resize-none"
                placeholder="请详细描述您的问题或需求..."
              ></textarea>
            </div>

            <div class="flex items-center">
              <input
                type="checkbox"
                id="privacy"
                v-model="form.agreePrivacy"
                required
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
              >
              <label for="privacy" class="ml-2 text-sm text-gray-600">
                我已阅读并同意
                <router-link to="/privacy" class="text-purple-600 hover:text-purple-800 underline">隐私政策</router-link>
                和
                <router-link to="/terms" class="text-purple-600 hover:text-purple-800 underline">服务条款</router-link>
              </label>
            </div>

            <button
              type="submit"
              :disabled="isSubmitting"
              class="w-full bg-purple-600 text-white py-3 px-6 rounded-lg font-medium hover:bg-purple-700 focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="!isSubmitting">发送消息</span>
              <span v-else class="flex items-center justify-center">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                发送中...
              </span>
            </button>
          </form>

          <!-- 提交成功提示 -->
          <div v-if="showSuccess" class="mt-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <div class="flex items-center">
              <svg class="w-5 h-5 text-green-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
              <p class="text-green-800">消息发送成功！我们会尽快回复您。</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 常见问题 -->
      <div class="mt-16 bg-white rounded-xl shadow-lg p-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-8 text-center">常见问题</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div class="space-y-6">
            <div>
              <h3 class="font-semibold text-gray-900 mb-2">如何开始使用您的服务？</h3>
              <p class="text-gray-600">您可以通过注册账户开始免费试用我们的服务，我们提供详细的入门指南和客户支持。</p>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-2">支持哪些社交媒体平台？</h3>
              <p class="text-gray-600">我们支持主流的社交媒体平台，包括微信、微博、抖音、Facebook、Instagram等。</p>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-2">数据安全如何保障？</h3>
              <p class="text-gray-600">我们采用银行级别的安全措施，包括数据加密、访问控制和定期安全审计。</p>
            </div>
          </div>
          <div class="space-y-6">
            <div>
              <h3 class="font-semibold text-gray-900 mb-2">是否提供API接口？</h3>
              <p class="text-gray-600">是的，我们提供完整的API文档和开发者支持，方便您集成到现有系统中。</p>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-2">如何获得技术支持？</h3>
              <p class="text-gray-600">您可以通过邮箱、电话或在线客服联系我们，我们提供7×24小时技术支持。</p>
            </div>
            <div>
              <h3 class="font-semibold text-gray-900 mb-2">是否支持定制开发？</h3>
              <p class="text-gray-600">我们提供定制开发服务，可以根据您的具体需求提供个性化解决方案。</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { showSuccess, showError } from '../utils/notification.js';

export default {
  name: 'ContactUs',
  data() {
    return {
      form: {
        name: '',
        email: '',
        phone: '',
        company: '',
        subject: '',
        message: '',
        agreePrivacy: false
      },
      isSubmitting: false,
      showSuccess: false
    };
  },
  methods: {
    async submitForm() {
      this.isSubmitting = true;
      
      try {
        // 模拟表单提交
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 显示成功消息
        this.showSuccess = true;
        showSuccess('消息发送成功！我们会尽快回复您。');
        
        // 重置表单
        this.resetForm();
        
        // 3秒后隐藏成功提示
        setTimeout(() => {
          this.showSuccess = false;
        }, 3000);
        
      } catch (error) {
        console.error('提交表单失败:', error);
        showError('发送失败，请稍后重试');
      } finally {
        this.isSubmitting = false;
      }
    },
    
    resetForm() {
      this.form = {
        name: '',
        email: '',
        phone: '',
        company: '',
        subject: '',
        message: '',
        agreePrivacy: false
      };
    }
  },
  
  mounted() {
    // 设置页面标题
    document.title = '联系我们 - 文际AIGC社交媒体管理平台';
  }
};
</script>

<style scoped>
/* 自定义样式 */
input:focus, select:focus, textarea:focus {
  outline: none;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .grid-cols-1.sm\\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
  
  .px-8 {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .py-12 {
    padding-top: 2rem;
    padding-bottom: 2rem;
  }
  
  h1 {
    font-size: 2rem;
  }
  
  h2 {
    font-size: 1.5rem;
  }
}
</style>