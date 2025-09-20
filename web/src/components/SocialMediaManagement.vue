<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 公共侧边栏 -->
    <CommonSidebar 
      title="社媒管理系统" 
      currentPage="social-media" 
      :isSuperUser="isSuperUser" 
    />

    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部导航栏 -->
      <header class="bg-white shadow-sm border-b border-gray-200">
        <div class="flex items-center justify-between h-16 px-4 lg:px-6">
          <div class="flex items-center">
            <button class="lg:hidden text-gray-500 hover:text-gray-600 mr-4">
              <i class="fas fa-bars text-xl"></i>
            </button>
            <h2 class="text-lg font-semibold text-gray-900">社媒管理</h2>
          </div>
          
          <div class="flex items-center space-x-4">
            <div class="relative">
              <input 
                type="text" 
                placeholder="搜索..." 
                class="w-full lg:w-64 pl-10 pr-4 py-2 text-sm bg-gray-100 border-0 rounded-full focus:ring-2 focus:ring-blue-500 focus:outline-none transition-colors"
              >
              <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
            
            <div class="relative">
              <button class="relative text-gray-500 hover:text-gray-600">
                <i class="fas fa-bell text-xl"></i>
                <span class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">3</span>
              </button>
            </div>
            
            <!-- Facebook登录按钮 -->
            <button 
              class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
              @click="showFacebookTokenManager = true"
            >
              <i class="fab fa-facebook-f mr-1"></i>FB授权
            </button>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto bg-gray-50 p-4 lg:p-6">
        <!-- 欢迎信息 -->
        <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">您好，{{ userInfo?.name || '管理员' }}</h1>
            <p class="text-gray-500 mt-1">这是您的社交媒体管理中心</p>
          </div>
          <div class="mt-4 md:mt-0 flex space-x-3">
            <button 
              class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
              @click="showPostForm = true"
            >
              <i class="fas fa-plus mr-2"></i>新建帖子
            </button>
          </div>
        </div>

        <!-- 平台连接状态 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <!-- Facebook -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-medium text-gray-500">Facebook</h3>
              <span class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                <i class="fab fa-facebook text-blue-600"></i>
              </span>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="text-lg font-bold text-gray-900">{{ platformStatus.facebook?.name || '未连接' }}</p>
                <p 
                  class="text-xs mt-1 flex items-center"
                  :class="platformStatus.facebook?.connected ? 'text-green-600' : 'text-red-600'"
                >
                  <i 
                    class="mr-1"
                    :class="platformStatus.facebook?.connected ? 'fas fa-check-circle' : 'fas fa-times-circle'"
                  ></i> 
                  {{ platformStatus.facebook?.connected ? '已连接' : '未连接' }}
                </p>
                <p v-if="platformStatus.facebook?.connected" class="text-xs text-gray-600 mt-1">
                  粉丝: {{ platformStatus.facebook?.followers || 0 }} | 评论: {{ platformStatus.facebook?.comments || 0 }}
                </p>
              </div>
              <button 
                class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors"
                @click="showFacebookTokenManager = true"
              >
                管理
              </button>
            </div>
          </div>

          <!-- Instagram -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-medium text-gray-500">Instagram</h3>
              <span class="w-10 h-10 rounded-full bg-pink-100 flex items-center justify-center">
                <i class="fab fa-instagram text-pink-600"></i>
              </span>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="text-lg font-bold text-gray-900">{{ platformStatus.instagram?.name || '未连接' }}</p>
                <p 
                  class="text-xs mt-1 flex items-center"
                  :class="platformStatus.instagram?.connected ? 'text-green-600' : 'text-red-600'"
                >
                  <i 
                    class="mr-1"
                    :class="platformStatus.instagram?.connected ? 'fas fa-check-circle' : 'fas fa-times-circle'"
                  ></i> 
                  {{ platformStatus.instagram?.connected ? '已连接' : '未连接' }}
                </p>
                <p v-if="platformStatus.instagram?.connected" class="text-xs text-gray-600 mt-1">
                  粉丝: {{ platformStatus.instagram?.followers || 0 }} | 评论: {{ platformStatus.instagram?.comments || 0 }}
                </p>
              </div>
              <button 
                class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors"
                @click="showInstagramTokenManager = true"
                :disabled="!platformStatus.facebook?.connected"
              >
                管理
              </button>
            </div>
          </div>

          <!-- TikTok -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-medium text-gray-500">TikTok</h3>
              <span class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                <i class="fab fa-tiktok text-black"></i>
              </span>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="text-lg font-bold text-gray-900">未连接</p>
                <p class="text-xs text-yellow-600 mt-1 flex items-center">
                  <i class="fas fa-clock mr-1"></i> 对接中
                </p>
              </div>
              <button 
                class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors"
                @click="showTikTokTokenManager = true"
              >
                管理
              </button>
            </div>
          </div>
        </div>

        <!-- 帖子列表 -->
        <div class="mb-6">
          <SocialMediaPostsList />
        </div>

        <!-- 数据分析 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <!-- 互动数据 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-6">
              <h3 class="font-semibold text-gray-900">互动数据概览</h3>
              <div class="flex space-x-2">
                <button class="px-3 py-1 text-xs bg-blue-600 text-white rounded-full hover:bg-blue-700 transition-colors">本周</button>
                <button class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors">本月</button>
                <button class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors">全年</button>
              </div>
            </div>
            <!-- 图表占位 -->
            <div class="h-64 w-full flex items-center justify-center bg-gray-50 rounded-lg">
              <div class="text-center">
                <i class="fas fa-chart-bar text-4xl text-gray-300 mb-2"></i>
                <p class="text-gray-500 text-sm">互动数据分析图表</p>
              </div>
            </div>
          </div>

          <!-- 平台表现对比 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-6">
              <h3 class="font-semibold text-gray-900">平台表现对比</h3>
              <button class="text-gray-400 hover:text-gray-500">
                <i class="fas fa-ellipsis-v"></i>
              </button>
            </div>
            <!-- 图表占位 -->
            <div class="h-64 w-full flex items-center justify-center bg-gray-50 rounded-lg">
              <div class="text-center">
                <i class="fas fa-chart-pie text-4xl text-gray-300 mb-2"></i>
                <p class="text-gray-500 text-sm">平台表现对比图</p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <!-- 新建帖子表单模态框 -->
  <div v-if="showPostForm" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg w-full max-w-3xl max-h-[90vh] overflow-auto">
      <div class="p-5 border-b border-gray-100 flex justify-between items-center">
        <h3 class="font-semibold text-gray-900">新建帖子</h3>
        <button 
          class="text-gray-400 hover:text-gray-500"
          @click="showPostForm = false"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="p-5">
        <SocialMediaPostForm 
          :platformStatus="platformStatus"
          @postPublished="onPostPublished"
        />
      </div>
    </div>
  </div>

  <!-- Facebook Token管理模态框 -->
  <div v-if="showFacebookTokenManager" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-auto">
      <div class="p-5 border-b border-gray-100 flex justify-between items-center">
        <h3 class="font-semibold text-gray-900">Facebook 授权管理</h3>
        <button 
          class="text-gray-400 hover:text-gray-500"
          @click="showFacebookTokenManager = false"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="p-5">
        <FacebookTokenManager @cancel="showFacebookTokenManager = false" />
      </div>
    </div>
  </div>
  
  <!-- Instagram Token管理模态框 -->
  <div v-if="showInstagramTokenManager" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-auto">
      <div class="p-5 border-b border-gray-100 flex justify-between items-center">
        <h3 class="font-semibold text-gray-900">Instagram 授权管理</h3>
        <button 
          class="text-gray-400 hover:text-gray-500"
          @click="showInstagramTokenManager = false"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="p-5">
        <InstagramTokenManager :facebookStatus="platformStatus.facebook" />
      </div>
    </div>
  </div>
  
  <!-- TikTok Token管理模态框 -->
  <div v-if="showTikTokTokenManager" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg w-full max-w-2xl max-h-[90vh] overflow-auto">
      <div class="p-5 border-b border-gray-100 flex justify-between items-center">
        <h3 class="font-semibold text-gray-900">TikTok 授权管理</h3>
        <button 
          class="text-gray-400 hover:text-gray-500"
          @click="showTikTokTokenManager = false"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="p-5">
        <TikTokTokenManager />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { getCurrentUser } from '../utils/auth.js';
import { socialMediaAPI } from '../utils/api.js';
import CommonSidebar from './CommonSidebar.vue';
import SocialMediaPostForm from './social-media/SocialMediaPostForm.vue';
import SocialMediaPostsList from './social-media/SocialMediaPostsList.vue';
import FacebookTokenManager from './social-media/FacebookTokenManager.vue';
import InstagramTokenManager from './social-media/InstagramTokenManager.vue';
import TikTokTokenManager from './social-media/TikTokTokenManager.vue';

export default {
  name: 'SocialMediaManagement',
  components: {
    CommonSidebar,
    SocialMediaPostForm,
    SocialMediaPostsList,
    FacebookTokenManager,
    InstagramTokenManager,
    TikTokTokenManager
  },
  data() {
    return {
      userInfo: null,
      isSuperUser: false,
      showPostForm: false,
      showFacebookTokenManager: false,
      showInstagramTokenManager: false,
      showTikTokTokenManager: false,
      platformStatus: {
        facebook: {
          connected: false,
          name: '',
          expiryDate: null
        },
        instagram: {
          connected: false,
          name: '',
          expiryDate: null
        },
        tiktok: {
          connected: false,
          name: '',
          expiryDate: null
        }
      }
    };
  },
  mounted() {
    this.loadUserInfo();
    this.fetchPlatformStatus();
    
    // 监听Webhook数据更新
    this.setupWebhookListener();
  },
  methods: {
    loadUserInfo() {
      const user = getCurrentUser();
      if (user) {
        this.userInfo = user;
        this.isSuperUser = user.role === 0;
      }
    },
    
    // 获取平台连接状态
    async fetchPlatformStatus() {
      try {
        const response = await socialMediaAPI.getPlatformStatus();
        this.platformStatus = response;
      } catch (error) {
        console.error('获取平台状态失败:', error);
        // API端点可能未实现，使用默认值
        // 不抛出错误，保持应用正常运行
      }
    },
    
    // 帖子发布成功后的回调
    onPostPublished() {
      this.showPostForm = false;
      // 可以添加通知或刷新列表
    },
    
    // 设置Webhook监听器
    setupWebhookListener() {
      // 这里应该实现与服务器的实时连接
      // 例如使用EventSource、WebSocket等
      // 为了演示，我们可以模拟定期刷新数据
      setInterval(() => {
        this.fetchPlatformStatus();
        // 这里可以触发其他组件的数据刷新
      }, 30000); // 每30秒刷新一次
    }
  }
};
</script>

<style scoped>
/* 社媒管理页面特定样式 */
</style>