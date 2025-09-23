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
      <div class="flex-1 p-6 overflow-auto bg-gray-100">
        <div class="w-full mx-auto space-y-6">
          
          <!-- 顶部操作栏 -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center space-x-2">
                <button class="px-4 py-2 text-sm font-semibold text-white rounded-md transition-all duration-300 transform hover:scale-102 hover:shadow-lg" style="background: linear-gradient(90deg, #6A5ACD, #9370DB);" @click="showInstantPostForm = true">即时发帖</button>
                <button class="px-4 py-2 text-sm font-semibold text-white rounded-md transition-all duration-300 transform hover:scale-102 hover:shadow-lg" style="background: linear-gradient(90deg, #D977F7, #F17CE0);" @click="showScheduledPostForm = true">定时发帖</button>
                <button class="px-4 py-2 text-sm font-semibold text-white rounded-md transition-all duration-300 transform hover:scale-102 hover:shadow-lg" style="background-color: #F39C12;" @click="showAuthManagement = true">管理授权</button>
                <button class="px-4 py-2 text-sm font-semibold text-white rounded-md transition-all duration-300 transform hover:scale-102 hover:shadow-lg" style="background-color: #2ECC71;" @click="handleTaskManagementClick">任务管理</button>
              </div>
              <div class="flex items-center space-x-3">
                <!-- 搜索框 -->
                <div class="relative" style="width: 200px;">
                  <input 
                    type="text" 
                    placeholder="搜索帖子内容..." 
                    class="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:outline-none focus:border-blue-500"
                    style="padding: 4px 8px; border: 1px solid #e0e0e0; border-radius: 4px;"
                    v-model="searchQuery"
                  >
                </div>
                <!-- 重置按钮 -->
                <button 
                  class="px-2 py-1 text-sm text-gray-600 hover:text-gray-800 transition-colors"
                  style="padding: 4px 8px;"
                  @click="resetFilters"
                >
                  重置
                </button>
              </div>
            </div>
            
            <!-- 筛选栏（平台、主页、状态） -->
            <div class="flex space-x-4">
              <!-- 平台筛选 -->
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">平台</label>
                <div class="relative">
                  <div 
                    class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded cursor-pointer bg-gray-50 hover:border-blue-500 transition-colors"
                    style="padding: 6px; border: 1px solid #e0e0e0; background: #f9fafb;"
                    @click="togglePlatformDropdown"
                  >
                    <div class="flex items-center justify-between">
                      <span v-if="selectedPlatforms.length === 0" class="text-gray-500">选择平台</span>
                      <span v-else-if="selectedPlatforms.length === 1" class="text-gray-900">
                        {{ platforms.find(p => p.id === selectedPlatforms[0])?.name }}
                      </span>
                      <span v-else class="text-gray-900">已选 {{ selectedPlatforms.length }} 个平台</span>
                      <i class="fas fa-chevron-down text-xs text-gray-400"></i>
                    </div>
                  </div>
                  <!-- 平台下拉选项 -->
                  <div v-if="showPlatformDropdown" class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg">
                    <div class="p-2">
                      <div class="max-h-32 overflow-y-auto">
                        <label 
                          v-for="platform in platforms" 
                          :key="platform.id" 
                          class="flex items-center p-1 hover:bg-gray-50 cursor-pointer text-sm rounded"
                          style="background-color: transparent;"
                        >
                          <input 
                            type="checkbox" 
                            :value="platform.id" 
                            v-model="selectedPlatforms" 
                            class="mr-2"
                          >
                          <i :class="platform.icon" class="mr-2"></i>
                          {{ platform.name }}
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 主页筛选 -->
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">主页</label>
                <div class="relative">
                  <div 
                    class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded cursor-pointer bg-gray-50 hover:border-blue-500 transition-colors"
                    style="padding: 6px; border: 1px solid #e0e0e0; background: #f9fafb;"
                    :class="{ 'bg-gray-100 cursor-not-allowed': selectedPlatforms.length === 0 }"
                    @click="selectedPlatforms.length > 0 && toggleHomepageDropdown()"
                  >
                    <div class="flex items-center justify-between">
                      <span v-if="selectedHomepages.length === 0" class="text-gray-500">
                        {{ selectedPlatforms.length === 0 ? '请先选择平台' : '选择主页' }}
                      </span>
                      <span v-else-if="selectedHomepages.length === 1" class="text-gray-900">
                        {{ getHomepageName(selectedHomepages[0]) }}
                      </span>
                      <span v-else class="text-gray-900">已选 {{ selectedHomepages.length }} 个主页</span>
                      <i class="fas fa-chevron-down text-xs text-gray-400"></i>
                    </div>
                  </div>
                  <!-- 主页下拉选项 -->
                  <div v-if="showHomepageDropdown && selectedPlatforms.length > 0" class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg">
                    <div class="p-2">
                      <div class="max-h-32 overflow-y-auto">
                        <label 
                          v-for="homepage in filteredHomepages" 
                          :key="homepage.id" 
                          class="flex items-center p-1 hover:bg-gray-50 cursor-pointer text-sm rounded"
                          style="background-color: transparent;"
                        >
                          <input 
                            type="checkbox" 
                            :value="homepage.id" 
                            v-model="selectedHomepages" 
                            class="mr-2"
                          >
                          <i :class="homepage.icon" class="mr-2 text-xs"></i>
                          {{ homepage.name }}
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 状态筛选 -->
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
                <div class="relative">
                  <div 
                    class="w-full px-2 py-1.5 text-sm border border-gray-300 rounded cursor-pointer bg-gray-50 hover:border-blue-500 transition-colors"
                    style="padding: 6px; border: 1px solid #e0e0e0; background: #f9fafb;"
                    @click="toggleStatusDropdown"
                  >
                    <div class="flex items-center justify-between">
                      <span v-if="selectedStatuses.length === 0" class="text-gray-500">选择状态</span>
                      <span v-else-if="selectedStatuses.length === 1" class="text-gray-900">
                        {{ getStatusName(selectedStatuses[0]) }}
                      </span>
                      <span v-else class="text-gray-900">已选 {{ selectedStatuses.length }} 个状态</span>
                      <i class="fas fa-chevron-down text-xs text-gray-400"></i>
                    </div>
                  </div>
                  <!-- 状态下拉选项 -->
                  <div v-if="showStatusDropdown" class="absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-md shadow-lg">
                    <div class="p-2">
                      <div class="max-h-32 overflow-y-auto">
                        <label 
                          v-for="status in postStatuses" 
                          :key="status.id" 
                          class="flex items-center p-1 hover:bg-gray-50 cursor-pointer text-sm rounded"
                          style="background-color: transparent;"
                        >
                          <input 
                            type="checkbox" 
                            :value="status.id" 
                            v-model="selectedStatuses" 
                            class="mr-2"
                          >
                          <span :class="status.color" class="w-2 h-2 rounded-full mr-2"></span>
                          {{ status.name }}
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态区域 -->
          <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
            <div class="text-center py-12">
              <!-- 图标 -->
              <div class="w-16 h-16 mx-auto mb-6 flex items-center justify-center">
                <svg class="w-16 h-16" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <defs>
                    <linearGradient id="iconGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                      <stop offset="0%" style="stop-color:#667eea;stop-opacity:1" />
                      <stop offset="100%" style="stop-color:#764ba2;stop-opacity:1" />
                    </linearGradient>
                  </defs>
                  <circle cx="32" cy="32" r="30" stroke="url(#iconGradient)" stroke-width="2" fill="none"/>
                  <path d="M20 32h24M32 20v24" stroke="url(#iconGradient)" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              
              <!-- 提示文字 -->
              <h4 class="text-base font-medium text-gray-800 mb-2" style="font-size: 16px; color: #333;">
                还未发布任何社媒帖子～
              </h4>
              <p class="text-gray-600 mb-6 text-sm" style="font-size: 12px; color: #666;">
                点击「创建帖子」开始创作，或「连接平台」授权账号
              </p>
              
              <!-- 操作按钮 -->
              <div class="flex justify-center space-x-4">
                <button 
                  class="px-4 py-2 text-white font-medium rounded transition-all duration-300 hover:shadow-lg hover:-translate-y-0.5"
                  style="padding: 8px 16px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 4px; color: #FFF;"
                  @click="showInstantPostForm = true"
                >
                  创建帖子
                </button>
                <button 
                  class="px-4 py-2 text-gray-800 font-medium rounded border border-gray-300 hover:bg-gray-50 transition-all duration-300"
                  style="padding: 8px 16px; border: 1px solid #e0e0e0; color: #333; border-radius: 4px;"
                  @click="showAuthManagement = true"
                >
                  连接平台
                </button>
              </div>
            </div>
          </div>

          <!-- 数据卡片区域 -->
          <div class="grid grid-cols-3 gap-6">
            <!-- 粉丝增长卡片 -->
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4" style="padding: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.05); border-radius: 6px;">
              <div class="flex items-center justify-between mb-3">
                <h3 class="text-sm font-semibold text-gray-800" style="font-size: 14px; color: #333; font-weight: bold;">粉丝增长</h3>
                <span class="text-xs text-green-600 font-medium" style="font-size: 12px; color: #2ecc71;">+12.5%</span>
              </div>
              <div class="mb-2">
                <div class="text-xl font-bold text-gray-800" style="font-size: 20px; color: #333; font-weight: bold;">+1,234</div>
                <div class="text-xs text-gray-600" style="font-size: 12px; color: #666;">本周新增粉丝</div>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-gradient-to-r from-green-400 to-green-600 h-2 rounded-full" style="width: 75%;"></div>
              </div>
            </div>

            <!-- 互动数据卡片 -->
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4" style="padding: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.05); border-radius: 6px;">
              <div class="flex items-center justify-between mb-3">
                <h3 class="text-sm font-semibold text-gray-800" style="font-size: 14px; color: #333; font-weight: bold;">互动数据</h3>
                <span class="text-xs text-blue-600 font-medium" style="font-size: 12px; color: #667eea;">+8.3%</span>
              </div>
              <div class="mb-2">
                <div class="text-xl font-bold text-gray-800" style="font-size: 20px; color: #333; font-weight: bold;">5.8%</div>
                <div class="text-xs text-gray-600" style="font-size: 12px; color: #666;">平均互动率</div>
              </div>
              <div class="space-y-1 text-xs text-gray-600">
                <div class="flex justify-between">
                  <span>点赞数</span>
                  <span>2,456</span>
                </div>
                <div class="flex justify-between">
                  <span>评论数</span>
                  <span>189</span>
                </div>
                <div class="flex justify-between">
                  <span>分享数</span>
                  <span>67</span>
                </div>
              </div>
            </div>

            <!-- 最近活动卡片 -->
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4" style="padding: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.05); border-radius: 6px;">
              <div class="flex items-center justify-between mb-3">
                <h3 class="text-sm font-semibold text-gray-800" style="font-size: 14px; color: #333; font-weight: bold;">最近活动</h3>
              </div>
              <div class="space-y-2">
                <div class="flex items-center space-x-2">
                  <div class="w-2 h-2 bg-green-500 rounded-full"></div>
                  <div class="text-xs text-gray-600" style="font-size: 12px; color: #666;">
                    <div class="font-medium">黄石子营销案例</div>
                    <div class="text-gray-500">+92 新粉丝 • 2小时前</div>
                  </div>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                  <div class="text-xs text-gray-600" style="font-size: 12px; color: #666;">
                    <div class="font-medium">电子产品测评</div>
                    <div class="text-gray-500">@brand_official • 1天前</div>
                  </div>
                </div>
                <div class="flex items-center space-x-2">
                  <div class="w-2 h-2 bg-orange-500 rounded-full"></div>
                  <div class="text-xs text-gray-600" style="font-size: 12px; color: #666;">
                    <div class="font-medium">初级营销案例</div>
                    <div class="text-gray-500">定时发布 • 3小时前</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- 弹窗组件 -->
    <InstantPostForm 
      v-if="showInstantPostForm"
      :platforms="connectedPlatforms"
      @close="showInstantPostForm = false"
      @success="onPostSuccess"
    />
    
    <ScheduledPostForm 
      v-if="showScheduledPostForm"
      :platforms="connectedPlatforms"
      @close="showScheduledPostForm = false"
      @success="onScheduleSuccess"
    />
    
    <FacebookTokenManager 
      v-if="showFacebookTokenManager"
      @close="showFacebookTokenManager = false"
    />
    
    <InstagramTokenManager 
      v-if="showInstagramTokenManager"
      @close="showInstagramTokenManager = false"
    />
    
    <TikTokTokenManager 
      v-if="showTikTokTokenManager"
      @close="showTikTokTokenManager = false"
    />
    
    <!-- 社交媒体授权管理弹窗 -->
    <SocialMediaAuthModal
      v-if="showAuthManagement"
      @close="showAuthManagement = false"
    />

    <!-- 任务管理弹窗 -->
    <TaskManagementModal
      v-if="showTaskManagement"
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
import SocialMediaPostsList from './social-media/SocialMediaPostsList.vue';
import FacebookTokenManager from './social-media/FacebookTokenManager.vue';
import InstagramTokenManager from './social-media/InstagramTokenManager.vue';
import TikTokTokenManager from './social-media/TikTokTokenManager.vue';
import ScheduledPostForm from './social-media/ScheduledPostForm.vue';
import TaskManagement from './social-media/TaskManagement.vue';
import InstantPostForm from './social-media/InstantPostForm.vue';
import SocialMediaAuthModal from './social-media/SocialMediaAuthModal.vue';
import TaskManagementModal from './social-media/TaskManagementModal.vue';

export default {
  name: 'SocialMediaManagement',
  components: {
    CommonSidebar,
    CommonHeader,
    SocialMediaPostsList,
    FacebookTokenManager,
    InstagramTokenManager,
    TikTokTokenManager,
    ScheduledPostForm,
    TaskManagement,
    InstantPostForm,
    SocialMediaAuthModal,
    TaskManagementModal
  },
  data() {
    return {
      userInfo: null,
      isSuperUser: false,
      showPostForm: false,
      showInstantPostForm: false,
      showScheduledPostForm: false,
      showTaskManagement: false,
      showFacebookTokenManager: false,
      showInstagramTokenManager: false,
      showTikTokTokenManager: false,
      activeTab: 'facebook',
      activePlatform: 'facebook',
      searchQuery: '', // 新增搜索查询
      showAuthManagement: false, // 新增授权管理弹窗控制
      selectedPlatforms: ['facebook'], // 改为数组支持多选
      selectedHomepages: [], // 新增主页多选
      selectedStatuses: [], // 新增状态多选
      showHomepages: false, // 控制主页选择区域显示
      showPlatformDropdown: false, // 控制平台下拉显示
      showHomepageDropdown: false, // 控制主页下拉显示
      showStatusDropdown: false, // 控制状态下拉显示
      platformSearchQuery: '', // 平台搜索查询
      homepageSearchQuery: '', // 主页搜索查询
      platforms: [
        { id: 'facebook', name: 'Facebook', icon: 'fab fa-facebook-f' },
        { id: 'instagram', name: 'Instagram', icon: 'fab fa-instagram' },
        { id: 'tiktok', name: 'TikTok', icon: 'fab fa-tiktok' }
      ],
      // 模拟主页数据
      homepages: [
        { id: 'homepage1', name: '科技前沿主页 1', platform: 'facebook', icon: 'fas fa-home' },
        { id: 'homepage2', name: '科技前沿主页 2', platform: 'facebook', icon: 'fas fa-home' },
        { id: 'homepage3', name: 'Tech Instagram', platform: 'instagram', icon: 'fas fa-home' },
        { id: 'homepage4', name: 'TikTok科技', platform: 'tiktok', icon: 'fas fa-home' }
      ],
      // 帖子状态选项
      postStatuses: [
        { id: 'published', name: '已发布', color: 'bg-green-500' },
        { id: 'draft', name: '草稿', color: 'bg-gray-500' },
        { id: 'scheduled', name: '定时发布', color: 'bg-blue-500' },
        { id: 'failed', name: '发布失败', color: 'bg-red-500' }
      ]
    };
  },
  computed: {
    // 过滤平台选项
    filteredPlatforms() {
      if (!this.platformSearchQuery) return this.platforms;
      return this.platforms.filter(platform => 
        platform.name.toLowerCase().includes(this.platformSearchQuery.toLowerCase())
      );
    },
    // 过滤主页选项（基于选中的平台）
    filteredHomepages() {
      let homepages = this.homepages.filter(homepage => 
        this.selectedPlatforms.includes(homepage.platform)
      );
      if (this.homepageSearchQuery) {
        homepages = homepages.filter(homepage => 
          homepage.name.toLowerCase().includes(this.homepageSearchQuery.toLowerCase())
        );
      }
      return homepages;
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
    
    // 任务管理按钮点击处理
    handleTaskManagementClick() {
      this.showTaskManagement = true
    },
    
    // 新增：切换平台选择
    togglePlatform(platformId) {
      const index = this.selectedPlatforms.indexOf(platformId);
      if (index > -1) {
        // 如果已选中，则取消选择（但至少保留一个）
        if (this.selectedPlatforms.length > 1) {
          this.selectedPlatforms.splice(index, 1);
        }
      } else {
        // 如果未选中，则添加到选择列表
        this.selectedPlatforms.push(platformId);
      }
      // 更新活跃平台为第一个选中的平台
      this.activePlatform = this.selectedPlatforms[0];
    },
    
    // 新增：切换主页选择区域显示
    toggleHomepages() {
      this.showHomepages = !this.showHomepages;
    },
    
    // 新增：切换平台下拉显示
    togglePlatformDropdown() {
      this.showPlatformDropdown = !this.showPlatformDropdown;
      this.showHomepageDropdown = false;
      this.showStatusDropdown = false;
    },
    
    // 新增：切换主页下拉显示
    toggleHomepageDropdown() {
      this.showHomepageDropdown = !this.showHomepageDropdown;
      this.showPlatformDropdown = false;
      this.showStatusDropdown = false;
    },
    
    // 新增：切换状态下拉显示
    toggleStatusDropdown() {
      this.showStatusDropdown = !this.showStatusDropdown;
      this.showPlatformDropdown = false;
      this.showHomepageDropdown = false;
    },
    
    // 新增：获取主页名称
    getHomepageName(homepageId) {
      const homepage = this.homepages.find(h => h.id === homepageId);
      return homepage ? homepage.name : homepageId;
    },
    
    // 新增：获取状态名称
    getStatusName(statusId) {
      const status = this.postStatuses.find(s => s.id === statusId);
      return status ? status.name : statusId;
    },
    
    // 新增：重置筛选条件
    resetFilters() {
      this.searchQuery = '';
      this.selectedPlatforms = [];
      this.selectedHomepages = [];
      this.selectedStatuses = [];
      this.showPlatformDropdown = false;
      this.showHomepageDropdown = false;
      this.showStatusDropdown = false;
    },
    
    // 新增：更新平台筛选
    updatePlatformFilter(event) {
      const value = event.target.value;
      if (value && !this.selectedPlatforms.includes(value)) {
        this.selectedPlatforms.push(value);
      }
    },
    onPostSuccess() {
      showNotification('发布成功！', 'success');
      this.showInstantPostForm = false;
    },
    
    onScheduleSuccess() {
      showNotification('定时任务创建成功！', 'success');
      this.showScheduledPostForm = false;
    }
  }
};
</script>

<style scoped>
/* 品牌渐变色彩定义 */
.active-tab {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
}

.active-tab-instagram {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
  transform: translateY(-1px);
}

.active-tab-tiktok {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
  transform: translateY(-1px);
}

.inactive-tab {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #e9ecef;
}

.inactive-tab:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 平台标签页交互效果 */
.platform-tab {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  font-size: 13px;
  padding: 8px 12px;
}

.platform-tab::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s;
}

.platform-tab:hover::before {
  left: 100%;
}

/* 账户卡片样式优化 */
.account-card {
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.account-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.account-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

/* 状态标签优化 */
.status-badge {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
  font-size: 11px;
  padding: 2px 6px;
}

/* 快捷操作按钮优化 */
.instant-post-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  border-radius: 8px;
  padding: 12px 16px;
}

.instant-post-btn:hover {
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.scheduled-post-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
  border-radius: 8px;
  padding: 12px 16px;
}

.scheduled-post-btn:hover {
  box-shadow: 0 8px 25px rgba(240, 147, 251, 0.4);
  transform: translateY(-2px);
}

.task-management-btn {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 12px 16px;
}

.task-management-btn:hover {
  background: #e9ecef;
  box-shadow: 0 4px 15px rgba(73, 80, 87, 0.2);
  transform: translateY(-2px);
}

/* 筛选栏样式优化 */
.filter-bar {
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.filter-multiselect {
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  transition: all 0.3s ease;
  min-height: 38px;
}

.filter-multiselect:hover {
  border-color: #adb5bd;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.filter-multiselect:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

/* 下拉菜单样式 */
.filter-multiselect + div {
  border-radius: 6px;
  border: 1px solid #dee2e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 空状态样式优化 */
.empty-state {
  color: #6c757d;
}

.empty-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 48px;
  height: 48px;
}

.create-post-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
}

.create-post-btn:hover {
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
}

.connect-platform-btn {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
  border-radius: 6px;
  padding: 6px 14px;
  font-size: 14px;
}

.connect-platform-btn:hover {
  background: #667eea;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transform: translateY(-1px);
}

/* 全局交互动效优化 */
.hover\:scale-102:hover {
  transform: scale(1.02);
}

.hover\:scale-105:hover {
  transform: scale(1.05);
}

/* 滚动条样式优化 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: #f8f9fa;
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .platform-tab {
    padding: 6px 10px;
    font-size: 12px;
  }
  
  .account-card {
    margin-bottom: 0.75rem;
  }
  
  .filter-bar {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  
  .instant-post-btn,
  .scheduled-post-btn,
  .task-management-btn {
    padding: 10px 12px;
    font-size: 13px;
  }
}

/* 动画效果增强 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.account-card,
.platform-tab,
.instant-post-btn,
.scheduled-post-btn,
.task-management-btn {
  animation: fadeInUp 0.4s ease-out;
}

/* 焦点状态优化 */
.platform-tab:focus,
.filter-multiselect:focus,
.create-post-btn:focus,
.connect-platform-btn:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

/* 加载状态优化 */
.loading {
  position: relative;
  overflow: hidden;
}

.loading::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

/* 多选标签选中状态 */
.platform-tab .fa-check {
  margin-left: 4px;
  animation: checkmark 0.3s ease-in-out;
}

@keyframes checkmark {
  0% {
    opacity: 0;
    transform: scale(0.5);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 下拉菜单项悬停效果 */
.filter-multiselect + div label:hover {
  background-color: #f8f9fa;
  border-radius: 4px;
}

/* 搜索框样式 */
.filter-multiselect + div input[type="text"] {
  border-radius: 4px;
  border: 1px solid #dee2e6;
  transition: border-color 0.2s ease;
}

.filter-multiselect + div input[type="text"]:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}
</style>