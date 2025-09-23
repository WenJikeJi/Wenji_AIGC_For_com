<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 通用侧边栏 -->
    <CommonSidebar 
      title="智能管理平台" 
      currentPage="data" 
      :isSuperUser="isSuperUser" 
    />

    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 通用标题栏 -->
      <CommonHeader 
        title="数据概览"
        searchPlaceholder="搜索数据..."
        :isSuperUser="isSuperUser"
      />

      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto bg-gradient-to-br from-gray-50 to-blue-50 p-4 lg:p-6">
        <!-- 欢迎横幅 -->
        <div class="bg-gradient-to-r from-blue-600 via-purple-600 to-indigo-600 rounded-2xl p-6 lg:p-8 mb-6 lg:mb-8 text-white relative overflow-hidden">
          <div class="absolute inset-0 bg-black/10"></div>
          <div class="relative z-10">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between space-y-4 sm:space-y-0">
              <div>
                <h1 class="text-2xl font-bold mb-2">数据概览</h1>
                <p class="text-sm" style="color: rgba(255,255,255,0.8);">实时监控您的社交媒体表现</p>
              </div>
              <div class="flex flex-col sm:flex-row sm:items-center space-y-4 sm:space-y-0 sm:space-x-6">
                <div class="text-center sm:text-right">
                  <div class="text-xs" style="color: rgba(255,255,255,0.7);">
                    <i class="fas fa-clock mr-1"></i>
                    最后更新：2023年7月10日 14:30
                  </div>
                </div>
                <div class="hidden lg:block">
                  <div class="w-24 h-24 bg-white/20 rounded-full flex items-center justify-center">
                    <i class="fas fa-chart-line text-4xl text-white"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 装饰性元素 -->
          <div class="absolute -top-4 -right-4 w-32 h-32 bg-white/10 rounded-full"></div>
          <div class="absolute -bottom-8 -left-8 w-40 h-40 bg-white/5 rounded-full"></div>
        </div>

        <!-- 核心指标卡片 -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 lg:gap-6 mb-6 lg:mb-8">
          <!-- 总粉丝数卡片 -->
          <div class="bg-gradient-to-b from-white to-gray-50 rounded-2xl p-6 shadow-sm hover:shadow-lg hover:transform hover:-translate-y-0.5 transition-all duration-300 group">
            <div class="flex items-center justify-between mb-4">
              <div class="p-3 bg-blue-50 rounded-xl group-hover:bg-blue-100 transition-colors">
                <i class="fas fa-users text-2xl text-blue-600"></i>
              </div>
              <span class="text-xs font-medium text-white bg-green-600 px-2 py-1 rounded" style="background: #22c55e; color: white; padding: 2px 6px; border-radius: 4px; font-size: 12px;">
                +12.5%
              </span>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">总粉丝数</h3>
            <p class="text-xl font-bold text-gray-900 mb-2">44,030</p>
            <p class="text-xs text-gray-500">相比上月增长</p>
          </div>

          <!-- 总互动数卡片 -->
          <div class="bg-gradient-to-b from-white to-gray-50 rounded-2xl p-6 shadow-sm hover:shadow-lg hover:transform hover:-translate-y-0.5 transition-all duration-300 group">
            <div class="flex items-center justify-between mb-4">
              <div class="p-3 bg-purple-50 rounded-xl group-hover:bg-purple-100 transition-colors">
                <i class="fas fa-heart text-2xl text-purple-600"></i>
              </div>
              <span class="text-xs font-medium text-white bg-green-600 px-2 py-1 rounded" style="background: #22c55e; color: white; padding: 2px 6px; border-radius: 4px; font-size: 12px;">
                +8.3%
              </span>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">总互动数</h3>
            <p class="text-xl font-bold text-gray-900 mb-2">18,456</p>
            <p class="text-xs text-gray-500">相比上月增长</p>
          </div>

          <!-- 总发布数卡片 -->
          <div class="bg-gradient-to-b from-white to-gray-50 rounded-2xl p-6 shadow-sm hover:shadow-lg hover:transform hover:-translate-y-0.5 transition-all duration-300 group">
            <div class="flex items-center justify-between mb-4">
              <div class="p-3 bg-green-50 rounded-xl group-hover:bg-green-100 transition-colors">
                <i class="fas fa-edit text-2xl text-green-600"></i>
              </div>
              <span class="text-xs font-medium text-white bg-green-600 px-2 py-1 rounded" style="background: #22c55e; color: white; padding: 2px 6px; border-radius: 4px; font-size: 12px;">
                +15.7%
              </span>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">总发布数</h3>
            <p class="text-xl font-bold text-gray-900 mb-2">156</p>
            <p class="text-xs text-gray-500">相比上月增长</p>
          </div>

          <!-- 平均参与率卡片 -->
          <div class="bg-gradient-to-b from-white to-gray-50 rounded-2xl p-6 shadow-sm hover:shadow-lg hover:transform hover:-translate-y-0.5 transition-all duration-300 group">
            <div class="flex items-center justify-between mb-4">
              <div class="p-3 bg-orange-50 rounded-xl group-hover:bg-orange-100 transition-colors">
                <i class="fas fa-chart-pie text-2xl text-orange-600"></i>
              </div>
              <span class="text-xs font-medium text-white bg-green-600 px-2 py-1 rounded" style="background: #22c55e; color: white; padding: 2px 6px; border-radius: 4px; font-size: 12px;">
                +3.4%
              </span>
            </div>
            <h3 class="text-gray-600 text-sm font-medium mb-1">平均参与率</h3>
            <p class="text-xl font-bold text-gray-900 mb-2">4.7%</p>
            <p class="text-xs text-gray-500">相比上月增长</p>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="grid grid-cols-1 xl:grid-cols-2 gap-4 lg:gap-6 mb-6 lg:mb-8">
          <!-- 粉丝增长趋势图表 -->
          <div class="bg-white rounded-2xl p-6 shadow-sm">
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg font-semibold text-gray-900">粉丝增长趋势</h3>
              <div class="flex space-x-2">
                <button class="px-3 py-1 text-xs font-medium text-blue-600 bg-blue-50 rounded-full hover:bg-blue-100 transition-colors">7天</button>
                <button class="px-3 py-1 text-xs font-medium text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors">30天</button>
                <button class="px-3 py-1 text-xs font-medium text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors">90天</button>
              </div>
            </div>
            <div class="h-64 bg-gradient-to-t from-blue-50 to-transparent rounded-xl p-4">
              <!-- 骨架屏组件 -->
              <div class="animate-pulse">
                <div class="flex justify-between items-end h-48 space-x-2">
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-20"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-32"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-40"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-28"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-36"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-44"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded w-8 h-24"></div>
                </div>
                <div class="mt-4 space-y-2">
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded h-3 w-3/4"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded h-3 w-1/2"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 平台分布饼图 -->
          <div class="bg-white rounded-2xl p-6 shadow-sm">
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg font-semibold text-gray-900">平台分布</h3>
              <button class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-50 rounded-lg transition-colors">
                <i class="fas fa-ellipsis-h"></i>
              </button>
            </div>
            <div class="h-64 bg-gradient-to-br from-purple-50 to-pink-50 rounded-xl p-4">
              <!-- 骨架屏组件 - 饼图样式 -->
              <div class="animate-pulse flex items-center justify-center h-full">
                <div class="relative">
                  <!-- 饼图骨架 -->
                  <div class="w-32 h-32 rounded-full bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer"></div>
                  <!-- 中心圆 -->
                  <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-16 h-16 rounded-full bg-white"></div>
                </div>
                <div class="ml-8 space-y-3">
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded h-4 w-24"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded h-4 w-20"></div>
                  <div class="bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-[length:200%_100%] animate-shimmer rounded h-4 w-16"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 社交媒体数据概览 -->
        <div class="bg-white rounded-2xl p-4 lg:p-6 shadow-sm mb-6 lg:mb-8">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-6 space-y-4 sm:space-y-0">
            <h3 class="text-lg lg:text-xl font-bold text-gray-900">社交媒体数据概览</h3>
            <div class="flex bg-gray-100 rounded-xl p-1 self-start sm:self-auto">
              <button class="px-3 lg:px-4 py-2 text-xs lg:text-sm font-medium text-white bg-blue-600 rounded-lg shadow-sm transition-all">今日</button>
              <button class="px-3 lg:px-4 py-2 text-xs lg:text-sm font-medium text-gray-600 hover:text-blue-600 hover:bg-white rounded-lg transition-all">本周</button>
              <button class="px-3 lg:px-4 py-2 text-xs lg:text-sm font-medium text-gray-600 hover:text-blue-600 hover:bg-white rounded-lg transition-all">本月</button>
            </div>
          </div>

          <!-- 平台数据卡片 -->
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 lg:gap-6">
            <!-- Facebook 卡片 -->
            <div class="bg-gradient-to-r from-blue-600 to-blue-700 rounded-xl p-6 hover:shadow-lg transition-all duration-300 group text-white">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center space-x-3">
                  <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center shadow-lg">
                    <i class="fab fa-facebook-f text-white text-xl"></i>
                  </div>
                  <div>
                    <h4 class="font-semibold text-white">Facebook</h4>
                    <p class="text-sm text-blue-100">社交平台</p>
                  </div>
                </div>
                <span class="text-xs font-medium text-white bg-white/20 px-2 py-1 rounded-full">+5.2%</span>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">15,240</p>
                  <p class="text-xs text-blue-100 mt-1">粉丝</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">4,521</p>
                  <p class="text-xs text-blue-100 mt-1">点赞</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">836</p>
                  <p class="text-xs text-blue-100 mt-1">评论</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">1,204</p>
                  <p class="text-xs text-blue-100 mt-1">分享</p>
                </div>
              </div>
            </div>

            <!-- Instagram 卡片 -->
            <div class="bg-gradient-to-r from-pink-600 to-purple-600 rounded-xl p-6 hover:shadow-lg transition-all duration-300 group text-white">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center space-x-3">
                  <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center shadow-lg">
                    <i class="fab fa-instagram text-white text-xl"></i>
                  </div>
                  <div>
                    <h4 class="font-semibold text-white">Instagram</h4>
                    <p class="text-sm text-pink-100">图片分享</p>
                  </div>
                </div>
                <span class="text-xs font-medium text-white bg-white/20 px-2 py-1 rounded-full">+8.7%</span>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">28,790</p>
                  <p class="text-xs text-pink-100 mt-1">粉丝</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">12,645</p>
                  <p class="text-xs text-pink-100 mt-1">点赞</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">1,240</p>
                  <p class="text-xs text-pink-100 mt-1">评论</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">986</p>
                  <p class="text-xs text-pink-100 mt-1">分享</p>
                </div>
              </div>
            </div>

            <!-- TikTok 卡片 -->
            <div class="bg-gradient-to-r from-black to-gray-800 rounded-xl p-6 hover:shadow-lg transition-all duration-300 group text-white">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center space-x-3">
                  <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center shadow-lg">
                    <i class="fab fa-tiktok text-white text-xl"></i>
                  </div>
                  <div>
                    <h4 class="font-semibold text-white">TikTok</h4>
                    <p class="text-sm text-gray-300">短视频</p>
                  </div>
                </div>
                <span class="text-xs font-medium text-white bg-white/20 px-2 py-1 rounded-full">+12.3%</span>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">25,860</p>
                  <p class="text-xs text-gray-300 mt-1">粉丝</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">36,210</p>
                  <p class="text-xs text-gray-300 mt-1">点赞</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">1,845</p>
                  <p class="text-xs text-gray-300 mt-1">评论</p>
                </div>
                <div class="text-center">
                  <p class="text-2xl font-bold text-white">7,820</p>
                  <p class="text-xs text-gray-300 mt-1">分享</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 gap-4">
          
        <!-- 帖子表现排名 -->
        <div class="bg-white rounded-2xl p-6 shadow-sm">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-xl font-bold text-gray-900">帖子表现排名</h3>
            <button class="text-blue-600 hover:text-blue-700 text-sm font-weight-500 flex items-center transition-colors">
              查看全部 <i class="fas fa-arrow-right ml-1"></i>
            </button>
          </div>          
          <div class="overflow-x-auto -mx-6 px-6 lg:mx-0 lg:px-0">
            <table class="w-full">
              <thead>
                <tr class="border-b border-gray-200">
                  <th class="text-left py-3 px-4 font-semibold text-gray-600 text-sm">内容</th>
                  <th class="text-center py-3 px-4 font-semibold text-gray-600 text-sm">平台</th>
                  <th class="text-center py-3 px-4 font-semibold text-gray-600 text-sm hidden sm:table-cell">发布时间</th>
                  <th class="text-center py-3 px-4 font-semibold text-gray-600 text-sm">点赞数</th>
                  <th class="text-center py-3 px-4 font-semibold text-gray-600 text-sm hidden md:table-cell">评论数</th>
                  <th class="text-center py-3 px-4 font-semibold text-gray-600 text-sm hidden md:table-cell">分享数</th>
                </tr>
              </thead>
              <tbody>
                <tr class="border-b border-gray-100 hover:bg-blue-50 transition-colors">
                  <td class="py-4 px-4">
                    <div class="flex items-center space-x-3">
                      <div class="w-10 h-10 bg-pink-100 rounded-lg flex items-center justify-center">
                        <i class="fas fa-image text-pink-600"></i>
                      </div>
                      <div>
                        <p class="font-medium text-gray-900 text-sm">新产品发布，限时优惠现在开始！</p>
                        <p class="text-xs text-gray-500 mt-1">营销 · 产品</p>
                      </div>
                    </div>
                  </td>
                  <td class="text-center py-4 px-4">
                    <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-pink-100 text-pink-800">
                      <i class="fab fa-instagram mr-1"></i> Instagram
                    </span>
                  </td>
                  <td class="text-center py-4 px-4 text-sm text-gray-600 hidden sm:table-cell">2023-07-10</td>
                  <td class="text-center py-4 px-4">
                    <span class="font-semibold text-gray-900">2,135</span>
                  </td>
                  <td class="text-center py-4 px-4 hidden md:table-cell">
                    <span class="font-semibold text-gray-900">246</span>
                  </td>
                  <td class="text-center py-4 px-4 hidden md:table-cell">
                    <span class="font-semibold text-gray-900">182</span>
                  </td>
                </tr>
                <tr class="bg-gray-50/50 border-b border-gray-100 hover:bg-blue-50 transition-colors">
                  <td class="py-4 px-4">
                    <div class="flex items-center space-x-3">
                      <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                        <i class="fas fa-trophy text-blue-600"></i>
                      </div>
                      <div>
                        <p class="font-medium text-gray-900 text-sm">公司获得行业奖项，感谢所有用户支持！</p>
                        <p class="text-xs text-gray-500 mt-1">企业 · 成就</p>
                      </div>
                    </div>
                  </td>
                  <td class="text-center py-4 px-4">
                    <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      <i class="fab fa-facebook-f mr-1"></i> Facebook
                    </span>
                  </td>
                  <td class="text-center py-4 px-4 text-sm text-gray-600 hidden sm:table-cell">2023-07-08</td>
                  <td class="text-center py-4 px-4">
                    <span class="font-semibold text-gray-900">1,890</span>
                  </td>
                  <td class="text-center py-4 px-4 hidden md:table-cell">
                    <span class="font-semibold text-gray-900">178</span>
                  </td>
                  <td class="text-center py-4 px-4 hidden md:table-cell">
                    <span class="font-semibold text-gray-900">312</span>
                  </td>
                </tr>
                <tr class="hover:bg-blue-50 transition-colors">
                  <td class="py-4 px-4">
                    <div class="flex items-center space-x-3">
                      <div class="w-10 h-10 bg-pink-100 rounded-lg flex items-center justify-center">
                        <i class="fas fa-book text-pink-600"></i>
                      </div>
                      <div>
                        <p class="font-medium text-gray-900 text-sm">产品教程，详细分步指南</p>
                        <p class="text-xs text-gray-500 mt-1">教育 · 教程</p>
                      </div>
                    </div>
                  </td>
                  <td class="text-center py-4 px-4">
                    <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-pink-100 text-pink-800">
                      <i class="fab fa-instagram mr-1"></i> Instagram
                    </span>
                  </td>
                  <td class="text-center py-4 px-4 text-sm text-gray-600 hidden sm:table-cell">2023-07-05</td>
                  <td class="text-center py-4 px-4">
                    <span class="font-semibold text-gray-900">1,560</span>
                  </td>
                  <td class="text-center py-4 px-4 hidden md:table-cell">
                    <span class="font-semibold text-gray-900">143</span>
                  </td>
                  <td class="text-center py-4 px-4 hidden md:table-cell">
                    <span class="font-semibold text-gray-900">98</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
         </div>

        <!-- 底部操作区域 -->

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
import CommonHeader from './CommonHeader.vue';

export default {
  name: 'DataOverview',
  components: {
    CommonSidebar,
    CommonHeader
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
          message: 'John Doe registered a new account',
          time: '10 minutes ago'
        },
        {
          id: 2,
          type: 'report',
          icon: 'check-circle',
          color: 'green',
          message: 'Market analysis report has been <span class="font-medium text-green-600">completed</span>',
          time: '1 hour ago'
        },
        {
          id: 3,
          type: 'alert',
          icon: 'exclamation-triangle',
          color: 'amber',
          message: 'Server load is high, please monitor',
          time: '2 hours ago'
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
/* Data page specific styles */
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.animate-shimmer {
  animation: shimmer 1.5s ease-in-out infinite;
}
</style>