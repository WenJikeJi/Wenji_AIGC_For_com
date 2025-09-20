<template>
  <aside class="hidden lg:flex lg:flex-col w-56 bg-white border-r border-blue-100 shadow-md transition-all duration-300 hover:shadow-lg">
    <!-- 标题区域 -->
    <div class="p-3 bg-gradient-to-r from-blue-600 to-blue-800 text-white shadow-md flex items-center justify-center">
      <i class="fas fa-chart-pie text-cyan-300 text-lg mr-2"></i>
      <h1 class="text-lg font-bold tracking-tight">{{ title }}</h1>
    </div>
    
    <!-- 导航区域 -->
    <nav class="flex-1 flex flex-col p-2 overflow-y-auto scrollbar-thin scrollbar-thumb-gray-200 scrollbar-track-gray-100">
      <!-- 数据概览 -->
      <a href="#/data" class="group flex items-center px-3 py-2 rounded-lg transition-all duration-300 mb-1 {{ currentPage === 'data' ? 'bg-blue-50 text-blue-600 font-medium border-l-4 border-blue-500 shadow-sm' : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900' }}">
        <div class="w-7 h-7 flex-shrink-0 flex items-center justify-center rounded-md transition-colors duration-300 {{ currentPage === 'data' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500 group-hover:bg-gray-200 group-hover:text-gray-700' }}">
          <i class="fas fa-chart-line text-sm"></i>
        </div>
        <span class="ml-3 transition-transform duration-300 group-hover:translate-x-0.5">数据概览</span>
      </a>
      
      <!-- 社媒管理 -->
      <a href="#/social-media" class="group flex items-center px-3 py-2 rounded-lg transition-all duration-300 mb-1 {{ currentPage === 'social-media' ? 'bg-blue-50 text-blue-600 font-medium border-l-4 border-blue-500 shadow-sm' : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900' }}">
        <div class="w-7 h-7 flex-shrink-0 flex items-center justify-center rounded-md transition-colors duration-300 {{ currentPage === 'social-media' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500 group-hover:bg-gray-200 group-hover:text-gray-700' }}">
          <i class="fas fa-share-alt text-sm"></i>
        </div>
        <span class="ml-3 transition-transform duration-300 group-hover:translate-x-0.5">社媒管理</span>
      </a>
      
      <!-- 分隔空间 - 将用户管理推到最底部 -->
      <div class="flex-grow"></div>
      
      <!-- 分割线 - 明确分隔导航和用户管理 -->
      <div class="border-t border-blue-100 my-2"></div>
      
      <!-- 用户管理 - 明确放在最底部 -->
      <a v-if="isSuperUser" href="#/users" class="group flex items-center px-3 py-2 rounded-lg transition-all duration-300 {{ currentPage === 'users' ? 'bg-blue-50 text-blue-600 font-medium border-l-4 border-blue-500 shadow-sm' : 'text-gray-700 hover:bg-gray-50 hover:text-gray-900' }}">
        <div class="w-7 h-7 flex-shrink-0 flex items-center justify-center rounded-md transition-colors duration-300 {{ currentPage === 'users' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500 group-hover:bg-gray-200 group-hover:text-gray-700' }}">
          <i class="fas fa-users-cog text-sm"></i>
        </div>
        <span class="ml-3 transition-transform duration-300 group-hover:translate-x-0.5">用户管理</span>
      </a>
    </nav>
    
    <!-- 底部区域 - 优化用户信息展示 -->
    <div class="p-3 border-t border-blue-100 bg-blue-50">
      <!-- 当前用户信息 -->
      <div class="relative bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-4 border border-gray-200 shadow-md transition-all duration-300 hover:shadow-lg hover:border-blue-300">
        <!-- 头像 -->
        <div class="flex justify-center mb-3">
          <div class="relative">
            <img :src="userAvatar" alt="用户头像" class="w-16 h-16 rounded-full border-4 border-blue-300 shadow-md transition-transform duration-300 hover:scale-105">
            <!-- 状态指示器使用蓝色 -->
            <span class="absolute bottom-0 right-0 w-5 h-5 bg-green-500 rounded-full border-2 border-white shadow-sm"></span>
          </div>
        </div>
        
        <!-- 用户信息 -->
        <div class="text-center">
          <div class="inline-block bg-blue-100 text-blue-700 text-xs px-3 py-1 rounded-full mb-2">
            {{ isSuperUser ? '超级管理员' : '普通用户' }}
          </div>
          <!-- 确保用户名显示正确 -->
          <p class="text-sm font-bold text-gray-800 mb-1">{{ getUserName() }}</p>
          <p class="text-xs text-gray-500 mb-4 truncate">{{ currentUser?.email || 'admin@example.com' }}</p>
          
          <!-- 快捷操作 -->
          <div class="flex justify-center space-x-3">
            <button class="py-1.5 px-4 text-xs bg-gray-100 text-gray-700 rounded-lg border border-gray-200 hover:bg-gray-200 transition-colors duration-200 flex items-center justify-center shadow-sm">
              <i class="fas fa-user-cog mr-1 text-gray-500"></i>
              <span>设置</span>
            </button>
            <button class="py-1.5 px-4 text-xs bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors duration-200 flex items-center justify-center shadow-sm">
              <i class="fas fa-sign-out-alt mr-1"></i>
              <span>退出</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
import { getCurrentUser, logout as logoutUser } from '../utils/auth.js';

export default {
  name: 'CommonSidebar',
  props: {
    title: {
      type: String,
      default: 'AI智能管家'
    },
    currentPage: {
      type: String,
      default: ''
    },
    isSuperUser: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      currentUser: null,
      userAvatar: 'https://picsum.photos/id/1005/200/200'
    };
  },
  mounted() {
    this.loadUserInfo();
  },
  methods: {
    loadUserInfo() {
      this.currentUser = getCurrentUser();
      // 如果用户有头像，使用用户头像
      if (this.currentUser && this.currentUser.avatar) {
        this.userAvatar = this.currentUser.avatar;
      }
    },
    logout() {
      logoutUser();
    },
    // 确保用户名显示为中文姓名
    getUserName() {
      // 检查用户名并返回正确的中文名
      if (this.currentUser && this.currentUser.name) {
        // 如果用户名是wenguangfeifan，返回李文广
        if (this.currentUser.name.toLowerCase() === 'wenguangfeifan') {
          return '李文广';
        }
        return this.currentUser.name;
      }
      return '管理员';
    }
  },
  watch: {
    // 监听用户状态变化，更新用户信息
    isSuperUser() {
      this.loadUserInfo();
    }
  }
};
</script>