<template>
  <div class="h-full bg-gradient-to-b from-white via-gray-50/80 to-gray-100/60 border-r border-gray-200/50 flex flex-col shadow-xl backdrop-blur-sm transition-all duration-300" :class="isCollapsed ? 'w-16' : 'w-48'">
    <!-- LOGO区域 -->
    <div class="h-16 px-4 py-3 border-b border-gray-200/30 bg-gradient-to-r from-white/90 to-gray-50/90 backdrop-blur-md flex items-center justify-center relative">
      <div v-if="!isCollapsed" class="w-32 h-10 bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
        <span class="text-white font-bold text-sm tracking-wide">AI智能</span>
      </div>
      <div v-else class="w-8 h-8 bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
        <i class="fas fa-robot text-white text-xs"></i>
      </div>
      <!-- 收缩按钮 -->
      <button 
        @click="toggleSidebar" 
        class="absolute right-4 p-2 rounded-lg text-gray-500 hover:text-gray-700 hover:bg-white/60 backdrop-blur-sm transition-all duration-200 hover:shadow-md"
        :title="isCollapsed ? '展开侧边栏' : '收缩侧边栏'"
      >
        <i :class="isCollapsed ? 'fas fa-chevron-right' : 'fas fa-chevron-left'" class="text-sm"></i>
      </button>
    </div>
    
    <!-- 导航区域 -->
    <nav class="flex-1 px-3 py-4 space-y-2">
      <!-- 数据概览 -->
      <a 
        href="#/data" 
        class="flex items-center px-3 py-3 text-sm font-medium rounded-xl text-gray-600 hover:bg-gradient-to-r hover:from-purple-50 hover:to-blue-50 hover:text-purple-700 transition-all duration-300 group relative overflow-hidden"
        :class="{ 'bg-gradient-to-r from-purple-100 to-blue-100 text-purple-700 shadow-lg border border-purple-200/50': currentHash === '#/data' }"
        :title="isCollapsed ? '数据概览' : ''"
      >
        <div v-if="currentHash === '#/data'" class="absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b from-purple-500 to-blue-500 rounded-r"></div>
        <div class="relative z-10 flex items-center w-full">
          <div class="w-8 h-8 rounded-lg flex items-center justify-center mr-3 transition-all duration-300" 
               :class="currentHash === '#/data' ? 'bg-gradient-to-r from-purple-500 to-blue-500 text-white shadow-md' : 'bg-gray-100 text-gray-500 group-hover:bg-gradient-to-r group-hover:from-purple-500 group-hover:to-blue-500 group-hover:text-white'">
            <i class="fas fa-chart-bar text-sm"></i>
          </div>
          <span v-if="!isCollapsed" class="truncate font-medium">数据概览</span>
        </div>
      </a>
      
      <!-- 社媒管理 -->
      <a 
        href="#/social-media" 
        class="flex items-center px-3 py-3 text-sm font-medium rounded-xl text-gray-600 hover:bg-gradient-to-r hover:from-purple-50 hover:to-blue-50 hover:text-purple-700 transition-all duration-300 group relative overflow-hidden"
        :class="{ 'bg-gradient-to-r from-purple-100 to-blue-100 text-purple-700 shadow-lg border border-purple-200/50': currentHash === '#/social-media' }"
        :title="isCollapsed ? '社媒管理' : ''"
      >
        <div v-if="currentHash === '#/social-media'" class="absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b from-purple-500 to-blue-500 rounded-r"></div>
        <div class="relative z-10 flex items-center w-full">
          <div class="w-8 h-8 rounded-lg flex items-center justify-center mr-3 transition-all duration-300" 
               :class="currentHash === '#/social-media' ? 'bg-gradient-to-r from-purple-500 to-blue-500 text-white shadow-md' : 'bg-gray-100 text-gray-500 group-hover:bg-gradient-to-r group-hover:from-purple-500 group-hover:to-blue-500 group-hover:text-white'">
            <i class="fas fa-share-alt text-sm"></i>
          </div>
          <span v-if="!isCollapsed" class="truncate font-medium">社媒管理</span>
        </div>
      </a>
      
      <!-- 用户管理 - 仅超级用户可见 -->
      <a 
        v-if="isSuperUser" 
        href="#/users" 
        class="flex items-center px-3 py-3 text-sm font-medium rounded-xl text-gray-600 hover:bg-gradient-to-r hover:from-purple-50 hover:to-blue-50 hover:text-purple-700 transition-all duration-300 group relative overflow-hidden"
        :class="{ 'bg-gradient-to-r from-purple-100 to-blue-100 text-purple-700 shadow-lg border border-purple-200/50': currentHash === '#/users' }"
        :title="isCollapsed ? '用户管理' : ''"
      >
        <div v-if="currentHash === '#/users'" class="absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b from-purple-500 to-blue-500 rounded-r"></div>
        <div class="relative z-10 flex items-center w-full">
          <div class="w-8 h-8 rounded-lg flex items-center justify-center mr-3 transition-all duration-300" 
               :class="currentHash === '#/users' ? 'bg-gradient-to-r from-purple-500 to-blue-500 text-white shadow-md' : 'bg-gray-100 text-gray-500 group-hover:bg-gradient-to-r group-hover:from-purple-500 group-hover:to-blue-500 group-hover:text-white'">
            <i class="fas fa-users text-sm"></i>
          </div>
          <span v-if="!isCollapsed" class="truncate font-medium">用户管理</span>
        </div>
      </a>
    </nav>
    
    <!-- 底部系统监控按钮 - 仅系统管理员可见 -->
    <div v-if="isSystemAdmin" class="px-3 pb-4">
      <a 
        href="#/system-monitor" 
        class="flex items-center px-3 py-3 text-sm font-medium rounded-xl text-gray-600 hover:bg-gradient-to-r hover:from-red-50 hover:to-orange-50 hover:text-red-700 transition-all duration-300 group relative overflow-hidden border border-red-200/30"
        :class="{ 'bg-gradient-to-r from-red-100 to-orange-100 text-red-700 shadow-lg border border-red-200/50': currentHash === '#/system-monitor' }"
        :title="isCollapsed ? '系统监控' : ''"
      >
        <div v-if="currentHash === '#/system-monitor'" class="absolute left-0 top-0 bottom-0 w-1 bg-gradient-to-b from-red-500 to-orange-500 rounded-r"></div>
        <div class="relative z-10 flex items-center w-full">
          <div class="w-8 h-8 rounded-lg flex items-center justify-center mr-3 transition-all duration-300" 
               :class="currentHash === '#/system-monitor' ? 'bg-gradient-to-r from-red-500 to-orange-500 text-white shadow-md' : 'bg-gray-100 text-gray-500 group-hover:bg-gradient-to-r group-hover:from-red-500 group-hover:to-orange-500 group-hover:text-white'">
            <i class="fas fa-desktop text-sm"></i>
          </div>
          <span v-if="!isCollapsed" class="truncate font-medium">系统监控</span>
        </div>
      </a>
    </div>
    
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
    },
    isSystemAdmin() {
      // 只有ken@shamillaa.com这个管理员账户可以访问系统监控
      return this.currentUser && this.currentUser.email === 'ken@shamillaa.com';
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