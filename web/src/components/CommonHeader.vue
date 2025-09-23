<template>
  <header class="bg-gradient-to-r from-purple-600 via-blue-600 to-indigo-700 shadow-xl border-b border-blue-200/30 backdrop-blur-sm">
    <div class="flex items-center justify-between h-16 px-4 lg:px-6">
      <div class="flex items-center h-full">
        <button class="lg:hidden text-white/80 hover:text-white mr-4 p-2 rounded-lg hover:bg-white/10 transition-all duration-200">
          <i class="fas fa-bars text-xl" />
        </button>
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center border border-white/30">
            <i class="fas fa-robot text-white text-sm"></i>
          </div>
          <h2 class="text-base font-semibold text-white">{{ title }}</h2>
        </div>
      </div>
      
      <div class="flex items-center space-x-4">
        <!-- Search Input -->
        <div class="relative" v-if="showSearch">
          <input 
            :type="searchType"
            :placeholder="searchPlaceholder" 
            class="w-full lg:w-64 pl-10 pr-4 py-2.5 text-sm bg-white/20 backdrop-blur-md border border-white/30 rounded-xl focus:ring-2 focus:ring-white/50 focus:outline-none focus:border-white/50 transition-all text-white placeholder-white/60 hover:bg-white/25"
            style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
            :value="searchValue"
            @input="$emit('search-input', $event.target.value)"
          >
          <div class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 bg-white/10 rounded-lg flex items-center justify-center">
            <i class="fas fa-search text-white/60 text-xs" />
          </div>
        </div>
        
        <!-- Notification Bell -->
        <div class="relative">
          <button class="relative text-white/80 hover:text-white transition-colors p-2 rounded-xl hover:bg-white/10 transition-all duration-200">
            <i class="fas fa-bell text-lg" />
            <span class="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-r from-red-500 to-pink-500 text-white text-xs rounded-full flex items-center justify-center shadow-lg animate-pulse">{{ notificationCount }}</span>
          </button>
        </div>
        
        <!-- Custom Action Buttons Slot -->
        <slot name="actions"></slot>
        
        <!-- User Info and Logout -->
        <div class="flex items-center space-x-3 border-l border-white/20 pl-4">
          <div class="flex items-center space-x-3">
            <div class="h-10 w-10 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center border border-white/30 hover:bg-white/30 transition-all duration-200">
              <i class="fas fa-user text-white text-sm"></i>
            </div>
            <div class="hidden md:block">
              <p class="text-sm font-medium text-white">{{ getUserName() }}</p>
              <p class="text-xs text-white/70">{{ userRoleText }}</p>
            </div>
          </div>
          
          <!-- Logout Button -->
          <button 
            @click="handleLogout" 
            class="flex items-center px-4 py-2 text-white/80 hover:text-white hover:bg-white/10 transition-all duration-200 rounded-xl text-sm border border-white/20 hover:border-white/40 hover:shadow-lg" 
            title="退出登录"
          >
            <i class="fas fa-sign-out-alt mr-2"></i>
            <span class="hidden md:inline">退出</span>
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { getCurrentUser, logout as logoutUser } from '../utils/auth.js';

export default {
  name: 'CommonHeader',
  props: {
    title: {
      type: String,
      required: true
    },
    showSearch: {
      type: Boolean,
      default: true
    },
    searchType: {
      type: String,
      default: 'text'
    },
    searchPlaceholder: {
      type: String,
      default: '搜索...'
    },
    searchValue: {
      type: String,
      default: ''
    },
    notificationCount: {
      type: Number,
      default: 3
    },
    isSuperUser: {
      type: Boolean,
      default: false
    }
  },
  emits: ['search-input'],
  data() {
    return {
      currentUser: null
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
  },
  methods: {
    loadUserInfo() {
      this.currentUser = getCurrentUser();
    },
    handleLogout() {
      logoutUser();
      window.location.hash = '#/login';
    },
    getUserName() {
      if (this.currentUser && this.currentUser.name) {
        return '李文广';
      }
      if (this.currentUser && this.currentUser.email) {
        return this.currentUser.email.split('@')[0];
      }
      return '用户';
    }
  }
};
</script>