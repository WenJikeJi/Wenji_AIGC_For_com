<template>
  <!-- 根据页面路径显示不同的组件 -->
  <div v-if="currentPage === 'login'">
    <LoginPage />
  </div>
  <div v-else-if="currentPage === 'data' && isLoggedIn">
    <DataPage />
  </div>
  <div v-else-if="currentPage === 'social-media' && isLoggedIn">
    <SocialMediaManagement />
  </div>
  <div v-else-if="currentPage === 'users' && isLoggedIn && isSuperUser">
    <UserManagement />
  </div>
  <div v-else>
    <!-- 首页，显示IndexPage组件 -->
    <IndexPage />
  </div>
  
  <!-- 全局通知组件 -->
  <Notification />
</template>

<script>
import LoginPage from './components/LoginPage.vue';
import IndexPage from './components/index.vue';
import DataPage from './components/data.vue';
import SocialMediaManagement from './components/SocialMediaManagement.vue';
import UserManagement from './components/UserManagement.vue';
import Notification from './components/Notification.vue';
import { initSessionManager, isLoggedIn as checkLoggedIn, getCurrentUser } from './utils/auth.js';
import { showError } from './utils/notification.js';

export default {
  name: 'App',
  components: {
    LoginPage,
    IndexPage,
    DataPage,
    SocialMediaManagement,
    UserManagement,
    Notification
  },
  data() {
    return {
      currentPage: 'index',
      isLoggedIn: false,
      currentUser: null,
      isSuperUser: false
    };
  },
  mounted() {
    // 初始化会话管理
    this.isLoggedIn = initSessionManager();
    this.currentUser = getCurrentUser();
    this.isSuperUser = this.currentUser && this.currentUser.role === 0;
    
    // 监听URL变化，实现简单的页面切换
    this.updateCurrentPageFromUrl();
    window.addEventListener('hashchange', this.updateCurrentPageFromUrl);
    
    // 初始化时检查是否需要保存重定向URL
    this.saveRedirectUrl();
    
    // 监听登录状态变化
    this.setupLoginStatusListener();
  },
  methods: {
    // 保存重定向URL
    saveRedirectUrl() {
      const currentHash = window.location.hash;
      // 只有当不是登录页时才保存当前URL
      if (currentHash !== '#/login') {
        localStorage.setItem('redirectUrl', currentHash);
      }
    },
    
    updateCurrentPageFromUrl() {
      const hash = window.location.hash;
      if (hash === '#/login') {
        this.currentPage = 'login';
      } else if (hash === '#/data') {
          if (this.isLoggedIn) {
            this.currentPage = 'data';
          } else {
            // 保存当前URL以便登录后返回
            this.saveRedirectUrl();
            // 未登录时跳转到登录页
            window.location.hash = '#/login';
          }
        } else if (hash === '#/social-media') {
          if (this.isLoggedIn) {
            this.currentPage = 'social-media';
          } else {
            // 保存当前URL以便登录后返回
            this.saveRedirectUrl();
            // 未登录时跳转到登录页
            window.location.hash = '#/login';
          }
        } else if (hash === '#/users') {
          if (this.isLoggedIn && this.isSuperUser) {
            this.currentPage = 'users';
          } else if (this.isLoggedIn && !this.isSuperUser) {
            // 非超级用户时跳转到首页
            showError('只有超级用户才能访问用户管理页面', '无权限');
            window.location.hash = '#/';
          } else {
            // 未登录时跳转到登录页
            window.location.hash = '#/login';
          }
        } else {
          this.currentPage = 'index';
        }
    },
    setupLoginStatusListener() {
      // 模拟登录状态变化的监听
      const checkStatus = () => {
        const loggedIn = checkLoggedIn();
        if (loggedIn !== this.isLoggedIn) {
          this.isLoggedIn = loggedIn;
          this.currentUser = getCurrentUser();
          this.isSuperUser = this.currentUser && this.currentUser.role === 0;
          // 如果之前因为未登录被拦截，可以现在检查并重定向
          if (loggedIn && this.currentPage === 'login') {
            window.location.hash = '#/';
          }
        }
      };
      
      // 定时检查登录状态
      setInterval(checkStatus, 1000);
    }
  },
  beforeUnmount() {
    window.removeEventListener('hashchange', this.updateCurrentPageFromUrl);
  }
}
</script>

<style>
/* App.vue的全局样式 */
* {
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  font-family: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>