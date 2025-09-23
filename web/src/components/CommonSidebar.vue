<template>
  <div class="h-full bg-gray-50 border-r border-gray-200 flex flex-col shadow-lg transition-all duration-300" :class="isCollapsed ? 'w-16' : 'w-48'">
    <!-- LOGO区域 -->
    <div class="h-16 px-4 py-3 border-b border-gray-200 bg-white flex items-center justify-center relative">
      <div v-if="!isCollapsed" class="w-32 h-10 bg-gradient-to-r from-purple-600 to-blue-600 rounded-lg flex items-center justify-center shadow-md">
        <span class="text-white font-bold text-sm tracking-wide">LOGO</span>
      </div>
      <div v-else class="w-8 h-8 bg-gradient-to-r from-purple-600 to-blue-600 rounded-lg flex items-center justify-center shadow-md">
        <span class="text-white font-bold text-xs">L</span>
      </div>
      <!-- 收缩按钮 -->
      <button 
        @click="toggleSidebar" 
        class="absolute right-4 p-2 rounded-md text-gray-500 hover:text-gray-700 hover:bg-gray-100 transition-all duration-200"
        :title="isCollapsed ? '展开侧边栏' : '收缩侧边栏'"
      >
        <i :class="isCollapsed ? 'fas fa-chevron-right' : 'fas fa-chevron-left'" class="text-sm"></i>
      </button>
    </div>
    
    <!-- 导航区域 -->
    <nav class="flex-1 px-3 py-3 space-y-1">
      <!-- 数据概览 -->
      <a 
        href="#/data" 
        class="flex items-center px-3 py-2 text-sm font-medium rounded-lg text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-all duration-200 group relative"
        :class="{ 'bg-gradient-to-r from-purple-50 to-blue-50 text-purple-700 shadow-sm border border-purple-100': currentHash === '#/data' }"
        :title="isCollapsed ? '数据概览' : ''"
      >
        <div v-if="currentHash === '#/data'" class="absolute left-0 top-0 bottom-0 w-1 bg-purple-500 rounded-r"></div>
        <i class="fas fa-chart-bar text-gray-500 group-hover:text-gray-700 transition-colors" :class="[{ 'text-purple-600': currentHash === '#/data' }, isCollapsed ? 'mx-auto' : 'mr-3']"></i>
        <span v-if="!isCollapsed" class="truncate">数据概览</span>
      </a>
      
      <!-- 社媒管理 -->
      <a 
        href="#/social-media" 
        class="flex items-center px-3 py-2 text-sm font-medium rounded-lg text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-all duration-200 group"
        :class="{ 'bg-gradient-to-r from-purple-50 to-blue-50 text-purple-700 shadow-sm border border-purple-100': currentHash === '#/social-media' }"
        :title="isCollapsed ? '社媒管理' : ''"
      >
        <i class="fas fa-share-alt text-gray-500 group-hover:text-gray-700 transition-colors" :class="[{ 'text-purple-600': currentHash === '#/social-media' }, isCollapsed ? 'mx-auto' : 'mr-3']"></i>
        <span v-if="!isCollapsed" class="truncate">社媒管理</span>
      </a>
      
      <!-- 用户管理 - 仅超级用户可见 -->
      <a 
        v-if="isSuperUser" 
        href="#/users" 
        class="flex items-center px-3 py-2 text-sm font-medium rounded-lg text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-all duration-200 group"
        :class="{ 'bg-gradient-to-r from-purple-50 to-blue-50 text-purple-700 shadow-sm border border-purple-100': currentHash === '#/users' }"
        :title="isCollapsed ? '用户管理' : ''"
      >
        <i class="fas fa-users text-gray-500 group-hover:text-gray-700 transition-colors" :class="[{ 'text-purple-600': currentHash === '#/users' }, isCollapsed ? 'mx-auto' : 'mr-3']"></i>
        <span v-if="!isCollapsed" class="truncate">用户管理</span>
      </a>
    </nav>
    
    <!-- 底部用户信息区域 - 删除重复的用户信息 -->
  </div>
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
      userAvatar: 'https://picsum.photos/id/1005/200/200',
      currentHash: window.location.hash,
      isCollapsed: false
    };
  },
  computed: {
    userRoleText() {
      if (!this.currentUser) return '用户';
      return this.currentUser.role === 0 ? '超级管理员' : '成员';
    }
  },
  mounted() {
    this.loadUserInfo();
    // 监听hash变化
    window.addEventListener('hashchange', this.updateCurrentHash);
  },
  beforeUnmount() {
    window.removeEventListener('hashchange', this.updateCurrentHash);
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },
    loadUserInfo() {
      this.currentUser = getCurrentUser();
      // 如果用户有头像，使用用户头像
      if (this.currentUser && this.currentUser.avatar) {
        this.userAvatar = this.currentUser.avatar;
      }
    },
    updateCurrentHash() {
      this.currentHash = window.location.hash;
    },
    handleLogout() {
      logoutUser();
      window.location.hash = '#/login';
    },
    // 确保用户名显示为中文姓名
    getUserName() {
      // 检查用户名并返回正确的中文名
      if (this.currentUser && this.currentUser.name) {
        return '李文广';
        return this.currentUser.name;
      }
      if (this.currentUser && this.currentUser.email) {
        return this.currentUser.email.split('@')[0];
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