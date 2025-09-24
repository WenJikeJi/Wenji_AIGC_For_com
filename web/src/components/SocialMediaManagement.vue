<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 通用侧边栏 -->
    <CommonSidebar 
      title="智能管理平台" 
      currentPage="social-media" 
      :isSuperUser="isSuperUser" 
    />

    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 通用头部 -->
      <CommonHeader 
        title="社交媒体管理" 
        :userInfo="userInfo" 
        :isSuperUser="isSuperUser" 
      />

      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto bg-gradient-to-br from-gray-50 to-blue-50 p-4 lg:p-6 h-full flex flex-col">
        <!-- 欢迎横幅 -->
        <div class="bg-gradient-to-r from-blue-600 via-purple-600 to-indigo-600 rounded-2xl p-4 lg:p-6 mb-4 lg:mb-6 text-white relative overflow-hidden flex-shrink-0">
          <div class="absolute inset-0 bg-black/10"></div>
          <div class="relative z-10">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between space-y-4 sm:space-y-0">
              <div>
                <h1 class="text-2xl font-bold mb-2">社交媒体管理</h1>
                <p class="text-sm" style="color: rgba(255,255,255,0.8);">一站式管理您的社交媒体账号与内容</p>
              </div>
              <div class="flex flex-col sm:flex-row sm:items-center space-y-4 sm:space-y-0 sm:space-x-6">
                <div class="text-center sm:text-right">
                  <div class="text-xs" style="color: rgba(255,255,255,0.7);">最后更新</div>
                  <div class="text-sm font-medium">{{ formatLastUpdateTime() }}</div>
                </div>
                <div class="w-16 h-16 bg-white/10 rounded-full flex items-center justify-center backdrop-blur-sm">
                  <i class="fas fa-share-alt text-2xl"></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="w-full mx-auto space-y-4 flex-1 flex flex-col">
          <!-- 顶部操作栏 -->
          <div class="bg-white rounded-2xl shadow-sm p-4 lg:p-5 flex-shrink-0">
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center space-x-3">
                <button 
                  class="px-4 py-2 text-sm font-medium rounded-lg transition-all duration-200 bg-gradient-to-r from-purple-500 to-indigo-600 text-white shadow-md hover:from-purple-600 hover:to-indigo-700 hover:shadow-lg"
                  @click="showCreatePostForm = true"
                >
                  <i class="fas fa-plus mr-2"></i>
                  创建帖子
                </button>
                <button 
                  class="px-4 py-2 text-sm font-medium rounded-lg transition-all duration-200 bg-gradient-to-r from-blue-500 to-cyan-600 text-white shadow-md hover:from-blue-600 hover:to-cyan-700 hover:shadow-lg"
                  @click="showAuthManagement = true"
                >
                  <i class="fas fa-link mr-2"></i>
                  管理授权
                </button>
                <button 
                  class="px-4 py-2 text-sm font-medium rounded-lg transition-all duration-200 bg-gradient-to-r from-orange-500 to-red-600 text-white shadow-md hover:from-orange-600 hover:to-red-700 hover:shadow-lg"
                  @click="handleTaskManagementClick"
                >
                  <i class="fas fa-tasks mr-2"></i>
                  任务管理
                </button>

              </div>
              <div class="flex items-center space-x-3">
                <!-- 平台筛选 -->
                <div class="relative">
                  <button 
                    @click="showPlatformDropdown = !showPlatformDropdown"
                    class="px-3 py-2 text-sm border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors flex items-center space-x-2 min-w-[120px] justify-between"
                  >
                    <span>{{ getPlatformFilterText() }}</span>
                    <i class="fas fa-chevron-down text-xs transition-transform" :class="{ 'rotate-180': showPlatformDropdown }"></i>
                  </button>
                  <div 
                    v-if="showPlatformDropdown"
                    class="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-20 min-w-[120px]"
                  >
                    <div 
                      v-for="platform in platformOptions"
                      :key="platform.value"
                      @click="togglePlatformFilter(platform.value)"
                      class="px-3 py-2 text-sm cursor-pointer transition-colors flex items-center justify-between"
                      :class="selectedPlatforms.includes(platform.value) ? 'bg-purple-100 text-purple-700 hover:bg-purple-150' : 'hover:bg-purple-50'"
                    >
                      <span>{{ platform.label }}</span>
                      <i v-if="selectedPlatforms.includes(platform.value)" class="fas fa-check text-purple-600"></i>
                    </div>
                  </div>
                </div>

                <!-- 状态筛选 -->
                <div class="relative">
                  <button 
                    @click="showStatusDropdown = !showStatusDropdown"
                    class="px-3 py-2 text-sm border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors flex items-center space-x-2 min-w-[120px] justify-between"
                  >
                    <span>{{ getStatusFilterText() }}</span>
                    <i class="fas fa-chevron-down text-xs transition-transform" :class="{ 'rotate-180': showStatusDropdown }"></i>
                  </button>
                  <div 
                    v-if="showStatusDropdown"
                    class="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-20 min-w-[120px]"
                  >
                    <div 
                      v-for="status in statusOptions"
                      :key="status.value"
                      @click="toggleStatusFilter(status.value)"
                      class="px-3 py-2 text-sm cursor-pointer transition-colors flex items-center justify-between"
                      :class="selectedStatuses.includes(status.value) ? 'bg-purple-100 text-purple-700 hover:bg-purple-150' : 'hover:bg-purple-50'"
                    >
                      <span>{{ status.label }}</span>
                      <i v-if="selectedStatuses.includes(status.value)" class="fas fa-check text-purple-600"></i>
                    </div>
                  </div>
                </div>

                <!-- 搜索框 -->
                <div class="relative" style="width: 200px;">
                  <input 
                    type="text" 
                    placeholder="搜索帖子内容..." 
                    class="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    v-model="searchQuery"
                  />
                </div>
                <!-- 重置按钮 -->
                <button 
                  class="px-3 py-2 text-sm text-gray-600 hover:text-gray-800 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                  @click="resetFilters"
                >
                  重置
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态区域 -->
          <div class="bg-gradient-to-br from-purple-50 to-indigo-50 rounded-lg p-6 text-center border border-purple-100 shadow-sm flex-1 flex flex-col justify-center">
            <div class="flex-1 flex flex-col justify-center items-center">
              <!-- 图标 -->
              <div class="w-14 h-14 mx-auto mb-4 flex items-center justify-center">
                <svg class="w-14 h-14" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <defs>
                    <linearGradient id="iconGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                      <stop offset="0%" style="stop-color:#8b5cf6;stop-opacity:1" />
                      <stop offset="100%" style="stop-color:#6366f1;stop-opacity:1" />
                    </linearGradient>
                  </defs>
                  <circle cx="32" cy="32" r="30" stroke="url(#iconGradient)" stroke-width="2" fill="none"/>
                  <path d="M20 32h24M32 20v24" stroke="url(#iconGradient)" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              
              <!-- 提示文字 -->
              <h4 class="text-lg font-semibold text-gray-800 mb-2">
                还未发布任何社媒帖子～
              </h4>
              <p class="text-gray-600 mb-4 text-sm">
                点击「创建帖子」开始创作，或「连接平台」授权账号
              </p>
              
              <!-- 操作按钮 -->
              <div class="flex justify-center space-x-4">
                <button 
                  class="px-6 py-3 text-white font-medium rounded-lg transition-all duration-300 hover:shadow-lg hover:-translate-y-0.5 bg-gradient-to-r from-purple-500 to-indigo-600 hover:from-purple-600 hover:to-indigo-700"
                  @click="showCreatePostForm = true"
                >
                  创建帖子
                </button>
                <button 
                  class="px-6 py-3 text-gray-700 font-medium rounded-lg border border-gray-200 hover:bg-gray-50 transition-all duration-300 hover:shadow-md bg-white"
                  @click="showAuthManagement = true"
                >
                  连接平台
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- 弹窗组件 -->
    <CreatePostForm 
      v-if="showCreatePostForm"
      :platforms="connectedPlatforms"
      @close="showCreatePostForm = false"
      @success="onPostSuccess"
    />
    
    <SocialMediaAuthModal
      v-if="showAuthManagement"
      @close="showAuthManagement = false"
    />

    <TaskManagementModal
      :visible="showTaskManagement"
      @close="showTaskManagement = false"
    />

  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { getCurrentUser } from '../utils/auth.js';
import { socialMediaAPI } from '../utils/api.js';
import { showNotification } from '../utils/notification.js';
import CommonSidebar from './CommonSidebar.vue';
import CommonHeader from './CommonHeader.vue';
import CreatePostForm from './social-media/CreatePostForm.vue';
import SocialMediaAuthModal from './social-media/SocialMediaAuthModal.vue';
import TaskManagementModal from './social-media/TaskManagementModal.vue';

export default {
  name: 'SocialMediaManagement',
  components: {
    CommonSidebar,
    CommonHeader,
    CreatePostForm,
    SocialMediaAuthModal,
    TaskManagementModal
  },
  data() {
    return {
      userInfo: null,
      isSuperUser: false,
      showPostForm: false,
      showCreatePostForm: false,
      showTaskManagement: false,
      showAuthManagement: false,
      activeTab: 'instant',
      searchQuery: '',
      // 筛选相关数据
      showPlatformDropdown: false,
      showStatusDropdown: false,
      selectedPlatforms: [],
      selectedStatuses: [],
      platformOptions: [
        { value: 'facebook', label: 'Facebook' },
        { value: 'instagram', label: 'Instagram' },
        { value: 'tiktok', label: 'TikTok' },
        { value: 'twitter', label: 'Twitter' }
      ],
      statusOptions: [
        { value: 'published', label: '已发布' },
        { value: 'scheduled', label: '已排期' },
        { value: 'failed', label: '发布失败' },
        { value: 'draft', label: '草稿' }
      ]
    };
  },
  computed: {
    connectedPlatforms() {
      // 模拟已连接平台数据
      return [
        { id: 'facebook', name: 'Facebook', icon: 'fab fa-facebook-f' },
        { id: 'instagram', name: 'Instagram', icon: 'fab fa-instagram' },
        { id: 'tiktok', name: 'TikTok', icon: 'fab fa-tiktok' }
      ];
    }
  },
  async mounted() {
    await this.initializeComponent();
  },
  methods: {
    async initializeComponent() {
      try {
        this.userInfo = await getCurrentUser();
        this.isSuperUser = this.userInfo?.role === 0;
      } catch (error) {
        console.error('初始化组件失败:', error);
        showNotification('初始化失败', 'error');
      }
    },
    
    formatLastUpdateTime() {
      // 模拟格式化最后更新时间
      return new Date().toLocaleString();
    },
    
    // 任务管理按钮点击处理
    handleTaskManagementClick() {
      this.showTaskManagement = true;
    },
    
    // 重置筛选条件
    resetFilters() {
      this.searchQuery = '';
      this.selectedPlatforms = [];
      this.selectedStatuses = [];
      this.showPlatformDropdown = false;
      this.showStatusDropdown = false;
    },

    // 获取平台筛选显示文本
    getPlatformFilterText() {
      if (this.selectedPlatforms.length === 0) {
        return '全部平台';
      } else if (this.selectedPlatforms.length === 1) {
        const platform = this.platformOptions.find(p => p.value === this.selectedPlatforms[0]);
        return platform ? platform.label : '已选择平台';
      } else {
        return `已选择 ${this.selectedPlatforms.length} 个平台`;
      }
    },

    // 获取状态筛选显示文本
    getStatusFilterText() {
      if (this.selectedStatuses.length === 0) {
        return '全部状态';
      } else if (this.selectedStatuses.length === 1) {
        const status = this.statusOptions.find(s => s.value === this.selectedStatuses[0]);
        return status ? status.label : '已选择状态';
      } else {
        return `已选择 ${this.selectedStatuses.length} 个状态`;
      }
    },

    // 切换平台筛选
    togglePlatformFilter(platform) {
      const index = this.selectedPlatforms.indexOf(platform);
      if (index > -1) {
        this.selectedPlatforms.splice(index, 1);
      } else {
        this.selectedPlatforms.push(platform);
      }
    },

    // 切换状态筛选
    toggleStatusFilter(status) {
      const index = this.selectedStatuses.indexOf(status);
      if (index > -1) {
        this.selectedStatuses.splice(index, 1);
      } else {
        this.selectedStatuses.push(status);
      }
    },
    
    onPostSuccess() {
      this.showCreatePostForm = false;
      // 可以在这里添加成功提示或刷新数据的逻辑
    }
  }
};
</script>

<style scoped>
.social-media-management {
  padding: 20px;
}

.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.filters-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.filter-dropdown {
  position: relative;
}

.filter-button {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-button:hover {
  background: #e9ecef;
  border-color: #dee2e6;
}

.filter-button.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  margin-top: 4px;
}

.dropdown-item {
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-item:hover {
  background: #f8f9fa;
}

.dropdown-item:first-child {
  border-radius: 8px 8px 0 0;
}

.dropdown-item:last-child {
  border-radius: 0 0 8px 8px;
}

.posts-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-state svg {
  margin: 0 auto 16px;
  opacity: 0.5;
}

@media (max-width: 768px) {
  .social-media-management {
    padding: 16px;
  }
  
  .header-section {
    padding: 20px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-dropdown {
    width: 100%;
  }
}
</style>