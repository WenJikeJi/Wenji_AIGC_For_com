<template>
  <header class="bg-gradient-to-r from-purple-600 via-blue-600 to-blue-700 shadow-lg border-b border-blue-200/30">
    <div class="flex items-center justify-between h-16 px-4 lg:px-6">
      <div class="flex items-center h-full">
        <button class="lg:hidden text-white/80 hover:text-white mr-4">
          <i class="fas fa-bars text-xl" />
        </button>
        <h2 class="text-base font-semibold text-white">{{ title }}</h2>
      </div>
      
      <div class="flex items-center space-x-4">
        <!-- Search Input -->
        <div class="relative" v-if="showSearch">
          <input 
            :type="searchType"
            :placeholder="searchPlaceholder" 
            class="w-full lg:w-64 pl-10 pr-4 py-2 text-sm bg-white/20 backdrop-blur-sm border border-white/30 rounded-full focus:ring-2 focus:ring-white/50 focus:outline-none focus:border-white/50 transition-all text-white placeholder-white/60"
            style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
            :value="searchValue"
            @input="$emit('search-input', $event.target.value)"
          >
          <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-white/60" />
        </div>
        
        <!-- Notification Bell -->
        <div class="relative">
          <button class="relative text-white/80 hover:text-white transition-colors">
            <i class="fas fa-bell text-xl" />
            <span class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center shadow-lg">{{ notificationCount }}</span>
          </button>
        </div>
        
        <!-- Custom Action Buttons Slot -->
        <slot name="actions"></slot>
        
        <!-- User Info and Logout -->
        <div class="flex items-center space-x-3 border-l border-white/20 pl-4">
          <div class="flex items-center space-x-2">
            <div class="h-8 w-8 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center border border-white/30">
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
            class="flex items-center px-3 py-2 text-white/80 hover:text-white hover:bg-white/10 transition-colors rounded-lg text-sm border border-white/20 hover:border-white/40" 
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