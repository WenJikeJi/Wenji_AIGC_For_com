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
  <div v-else-if="currentPage === 'system-monitor'">
    <!-- 系统监控页面，不要求登录，可以直接访问 -->
    <SystemMonitor />
  </div>
  <div v-else-if="currentPage === 'terms'">
    <TermsOfService />
  </div>
  <div v-else-if="currentPage === 'privacy'">
    <PrivacyPolicy />
  </div>
  <div v-else-if="currentPage === 'contact'">
    <ContactUs />
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
import SystemMonitor from './components/SystemMonitor.vue';
import TermsOfService from './components/TermsOfService.vue';
import PrivacyPolicy from './components/PrivacyPolicy.vue';
import ContactUs from './components/ContactUs.vue';
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
    SystemMonitor,
    TermsOfService,
    PrivacyPolicy,
    ContactUs,
    Notification
  },
  data() {
    // 在初始化时就根据URL确定当前页面
    const hash = window.location.hash;
    let initialPage = 'index';
    if (hash === '#/login') {
      initialPage = 'login';
    } else if (hash === '#/data') {
      initialPage = 'data';
    } else if (hash === '#/social-media') {
      initialPage = 'social-media';
    } else if (hash === '#/users') {
      initialPage = 'users';
    } else if (hash === '#/system-monitor') {
      initialPage = 'system-monitor';
    } else if (hash === '#/terms') {
      initialPage = 'terms';
    } else if (hash === '#/privacy') {
      initialPage = 'privacy';
    } else if (hash === '#/contact') {
      initialPage = 'contact';
    }
    
    return {
      currentPage: initialPage,
      isLoggedIn: false,
      currentUser: null,
      isSuperUser: false,
      isSystemAdmin: false
    };
  },
  mounted() {
    // 初始化会话管理
    this.isLoggedIn = initSessionManager();
    this.currentUser = getCurrentUser();
    this.isSuperUser = this.currentUser && this.currentUser.role === 0;
    this.isSystemAdmin = this.currentUser && this.currentUser.email === 'ken@shamillaa.com';
    
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
            // 非主管时跳转到首页
        showError('只有主管才能访问用户管理页面', '无权限');
            window.location.hash = '#/';
          } else {
            // 未登录时跳转到登录页
            window.location.hash = '#/login';
          }
        } else if (hash === '#/system-monitor') {
          if (this.isLoggedIn && this.isSystemAdmin) {
            this.currentPage = 'system-monitor';
          } else if (this.isLoggedIn && !this.isSystemAdmin) {
            // 非系统管理员时跳转到首页
            showError('权限不足，仅限特定管理员访问', '无权限');
            window.location.hash = '#/';
          } else {
            // 未登录时跳转到登录页
            window.location.hash = '#/login';
          }
        } else if (hash === '#/terms') {
          this.currentPage = 'terms';
        } else if (hash === '#/privacy') {
          this.currentPage = 'privacy';
        } else if (hash === '#/contact') {
          this.currentPage = 'contact';
        } else {
          this.currentPage = 'index';
        }
    },
    setupLoginStatusListener() {
      // 模拟登录状态变化的监听
      const checkStatus = () => {
        const loggedIn = checkLoggedIn();
        if (loggedIn !== this.isLoggedIn || true) { // 每次都更新状态以确保准确性
          this.isLoggedIn = loggedIn;
          this.currentUser = getCurrentUser();
          this.isSuperUser = this.currentUser && this.currentUser.role === 0;
          // 优化系统管理员检查，确保能正确识别邮箱
          this.isSystemAdmin = this.currentUser && this.currentUser.email && 
                              this.currentUser.email.toLowerCase() === 'ken@shamillaa.com';
          
          // 如果之前因为未登录被拦截，可以现在检查并重定向
          if (loggedIn && this.currentPage === 'login') {
            window.location.hash = '#/';
          }
          // 当用户是系统管理员且尝试访问系统监控页面时，确保能正确显示
          if (this.isSystemAdmin && window.location.hash === '#/system-monitor') {
            this.currentPage = 'system-monitor';
          }
        }
      };
      
      // 增加检查频率并立即执行一次检查
      checkStatus();
      setInterval(checkStatus, 500);
    }
  },
  beforeUnmount() {
    window.removeEventListener('hashchange', this.updateCurrentPageFromUrl);
  }
}
</script>

<style>
/* 引入浅色主题样式 */
@import './styles/light-theme.css';

/* App.vue的全局样式 */
#app {
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
}

/* 页面切换动画 */
.page-enter-active, .page-leave-active {
  transition: all 0.3s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: var(--radius-sm);
}

::-webkit-scrollbar-thumb {
  background: var(--border-medium);
  border-radius: var(--radius-sm);
  transition: background 0.2s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--border-dark);
}

/* Firefox滚动条样式 */
* {
  scrollbar-width: thin;
  scrollbar-color: var(--border-medium) var(--bg-secondary);
}
</style>