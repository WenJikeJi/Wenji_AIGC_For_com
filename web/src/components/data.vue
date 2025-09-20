<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 公共侧边栏 -->
    <CommonSidebar 
      title="社交媒体数据大盘" 
      currentPage="data" 
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
            <h2 class="text-lg font-semibold text-gray-900">社交媒体数据大盘</h2>
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
            
            <div class="hidden md:flex items-center space-x-2">
              <span class="text-sm text-gray-500">今日数据</span>
              <span class="px-2 py-1 text-xs bg-green-100 text-green-800 rounded-full">+12.5%</span>
            </div>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto bg-gray-50 p-4 lg:p-6">
        <!-- 欢迎信息 -->
        <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">您好，{{ userInfo?.name || '管理员' }}</h1>
            <p class="text-gray-500 mt-1">这是您的社交媒体数据概览</p>
            <!-- 当前维护主页显示 -->
            <div class="mt-2 inline-block px-3 py-1 bg-yellow-100 text-yellow-800 text-sm rounded-full">
              <i class="fas fa-tools mr-1"></i>当前正在维护：社交媒体数据大盘
            </div>
          </div>
          <div class="mt-4 md:mt-0 flex space-x-3">
            <button class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors">
              <i class="fas fa-download mr-2"></i>导出报告
            </button>
            <button class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
              <i class="fas fa-refresh mr-2"></i>刷新数据
            </button>
          </div>
        </div>

        <!-- 数据指标卡片 - 社交媒体总览 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <!-- 总粉丝量卡片 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-medium text-gray-500">总粉丝量</h3>
              <span class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                <i class="fas fa-users text-blue-600"></i>
              </span>
            </div>
            <div class="flex items-end justify-between">
              <div>
                <p class="text-2xl font-bold text-gray-900">44,030</p>
                <p class="text-xs text-green-600 mt-1 flex items-center">
                  <i class="fas fa-arrow-up mr-1"></i> 12.5%
                  <span class="text-gray-400 ml-1">vs 上月</span>
                </p>
              </div>
              <div class="h-10 w-24">
                <div class="h-full w-full" style="background: linear-gradient(180deg, #3B82F6 0%, #93C5FD 100%); border-radius: 4px;"></div>
              </div>
            </div>
          </div>

          <!-- 总互动量卡片 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-medium text-gray-500">总互动量</h3>
              <span class="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center">
                <i class="fas fa-heart text-purple-600"></i>
              </span>
            </div>
            <div class="flex items-end justify-between">
              <div>
                <p class="text-2xl font-bold text-gray-900">19,342</p>
                <p class="text-xs text-green-600 mt-1 flex items-center">
                  <i class="fas fa-arrow-up mr-1"></i> 8.2%
                  <span class="text-gray-400 ml-1">vs 上月</span>
                </p>
              </div>
              <div class="h-10 w-24">
                <div class="h-full w-full" style="background: linear-gradient(180deg, #8B5CF6 0%, #C4B5FD 100%); border-radius: 4px;"></div>
              </div>
            </div>
          </div>

          <!-- 总评论数卡片 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-medium text-gray-500">总评论数</h3>
              <span class="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
                <i class="fas fa-comment text-green-600"></i>
              </span>
            </div>
            <div class="flex items-end justify-between">
              <div>
                <p class="text-2xl font-bold text-gray-900">2,076</p>
                <p class="text-xs text-green-600 mt-1 flex items-center">
                  <i class="fas fa-arrow-up mr-1"></i> 15.8%
                  <span class="text-gray-400 ml-1">vs 上月</span>
                </p>
              </div>
              <div class="h-10 w-24">
                <div class="h-full w-full" style="background: linear-gradient(180deg, #10B981 0%, #6EE7B7 100%); border-radius: 4px;"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 图表区域 - 社交媒体趋势 -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
          <!-- 粉丝增长趋势图表 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 lg:col-span-2 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-6">
              <h3 class="font-semibold text-gray-900">社交媒体粉丝增长趋势</h3>
              <div class="flex space-x-2">
                <button class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors">本周</button>
                <button class="px-3 py-1 text-xs bg-blue-600 text-white rounded-full hover:bg-blue-700 transition-colors">本月</button>
                <button class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors">全年</button>
              </div>
            </div>
            <!-- 图表占位 -->
            <div class="h-72 w-full flex items-center justify-center bg-gray-50 rounded-lg">
              <div class="text-center">
                <i class="fas fa-chart-line text-4xl text-gray-300 mb-2"></i>
                <p class="text-gray-500 text-sm">粉丝增长趋势图表</p>
              </div>
            </div>
          </div>

          <!-- 平台分布饼图 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-6">
              <h3 class="font-semibold text-gray-900">粉丝平台分布</h3>
              <button class="text-gray-400 hover:text-gray-500">
                <i class="fas fa-ellipsis-v"></i>
              </button>
            </div>
            <!-- 图表占位 -->
            <div class="h-72 w-full flex items-center justify-center bg-gray-50 rounded-lg">
              <div class="text-center">
                <i class="fas fa-chart-pie text-4xl text-gray-300 mb-2"></i>
                <p class="text-gray-500 text-sm">平台分布饼图</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 社交媒体数据概览 -->
        <div class="grid grid-cols-1 gap-6">
          <!-- 社交媒体数据统计 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-6">
              <h3 class="font-semibold text-gray-900">社交媒体数据统计</h3>
              <div class="flex space-x-2">
                <button class="px-3 py-1 text-xs bg-blue-600 text-white rounded-full hover:bg-blue-700 transition-colors">今日</button>
                <button class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors">本周</button>
                <button class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 transition-colors">本月</button>
              </div>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <!-- Facebook统计 -->
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="flex items-center mb-3">
                  <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center mr-2">
                    <i class="fab fa-facebook text-blue-600 text-sm"></i>
                  </div>
                  <h4 class="text-sm font-medium text-gray-900">Facebook</h4>
                </div>
                <div class="space-y-2">
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">粉丝量</span>
                    <span class="text-sm font-semibold text-gray-900">15,240</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">评论数</span>
                    <span class="text-sm font-semibold text-gray-900">836</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">点赞数</span>
                    <span class="text-sm font-semibold text-gray-900">4,521</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">分享数</span>
                    <span class="text-sm font-semibold text-gray-900">1,204</span>
                  </div>
                </div>
              </div>
              
              <!-- Instagram统计 -->
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="flex items-center mb-3">
                  <div class="w-8 h-8 rounded-full bg-pink-100 flex items-center justify-center mr-2">
                    <i class="fab fa-instagram text-pink-600 text-sm"></i>
                  </div>
                  <h4 class="text-sm font-medium text-gray-900">Instagram</h4>
                </div>
                <div class="space-y-2">
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">粉丝量</span>
                    <span class="text-sm font-semibold text-gray-900">28,790</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">评论数</span>
                    <span class="text-sm font-semibold text-gray-900">1,240</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">点赞数</span>
                    <span class="text-sm font-semibold text-gray-900">12,645</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">分享数</span>
                    <span class="text-sm font-semibold text-gray-900">986</span>
                  </div>
                </div>
              </div>
              
              <!-- TikTok统计 -->
              <div class="bg-gray-50 rounded-lg p-4">
                <div class="flex items-center mb-3">
                  <div class="w-8 h-8 rounded-full bg-black flex items-center justify-center mr-2">
                    <i class="fab fa-tiktok text-white text-sm"></i>
                  </div>
                  <h4 class="text-sm font-medium text-gray-900">TikTok</h4>
                </div>
                <div class="space-y-2">
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">粉丝量</span>
                    <span class="text-sm font-semibold text-gray-900">25,860</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">评论数</span>
                    <span class="text-sm font-semibold text-gray-900">1,845</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">点赞数</span>
                    <span class="text-sm font-semibold text-gray-900">36,210</span>
                  </div>
                  <div class="flex justify-between items-center">
                    <span class="text-xs text-gray-500">分享数</span>
                    <span class="text-sm font-semibold text-gray-900">7,820</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 帖子表现排行 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-5 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-semibold text-gray-900">帖子表现排行</h3>
              <button class="text-blue-600 hover:text-blue-700 text-sm font-medium">查看全部</button>
            </div>
            
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">帖子内容</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">平台</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">发布日期</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">点赞数</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">评论数</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">分享数</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 line-clamp-1">新品发布，限时优惠活动开始啦！</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="text-xs bg-pink-100 text-pink-800 rounded-full px-2 py-1 flex items-center">
                        <i class="fab fa-instagram mr-1"></i> Instagram
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">2023-07-10</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">2,135</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">246</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">182</td>
                  </tr>
                  <tr>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 line-clamp-1">公司获得行业大奖，感谢所有用户的支持！</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="text-xs bg-blue-100 text-blue-800 rounded-full px-2 py-1 flex items-center">
                        <i class="fab fa-facebook mr-1"></i> Facebook
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">2023-07-08</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">1,890</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">178</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">312</td>
                  </tr>
                  <tr>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 line-clamp-1">产品使用教程，详细步骤分享</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="text-xs bg-pink-100 text-pink-800 rounded-full px-2 py-1 flex items-center">
                        <i class="fab fa-instagram mr-1"></i> Instagram
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">2023-07-05</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">1,560</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">143</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">98</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { getCurrentUser, logout as logoutUser } from '../utils/auth.js';
import { showSuccess, showError } from '../utils/notification.js';
import CommonSidebar from './CommonSidebar.vue';

export default {
  name: 'DataPage',
  components: {
    CommonSidebar
  },
  data() {
    return {
      userInfo: null,
      isSuperUser: false,
      notifications: [
        {
          id: 1,
          type: 'user',
          icon: 'user-plus',
          color: 'blue',
          message: '张三 注册了新账户',
          time: '10分钟前'
        },
        {
          id: 2,
          type: 'report',
          icon: 'check-circle',
          color: 'green',
          message: '市场分析报告已 <span class="font-medium text-green-600">完成</span>',
          time: '1小时前'
        },
        {
          id: 3,
          type: 'alert',
          icon: 'exclamation-triangle',
          color: 'amber',
          message: '服务器负载过高，请注意监控',
          time: '2小时前'
        }
      ]
    };
  },
  mounted() {
    this.loadUserInfo();
  },
  methods: {
    loadUserInfo() {
      const user = getCurrentUser();
      if (user) {
        this.userInfo = user;
        this.isSuperUser = user.role === 0;
      }
    },
    logout() {
        logoutUser();
      }
  }
};
</script>

<style scoped>
/* 添加特定于数据页面的样式 */
</style>