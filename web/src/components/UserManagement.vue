<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 公共侧边栏 -->
    <CommonSidebar 
      title="用户管理系统" 
      currentPage="users" 
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
            <h2 class="text-lg font-semibold text-gray-900">用户管理</h2>
          </div>
          
          <div class="flex items-center space-x-4">
            <div class="relative">
              <!-- 只有超级用户可以使用搜索功能 -->
              <input 
                v-if="isSuperUser"
                type="text" 
                placeholder="搜索用户..." 
                class="w-full lg:w-64 pl-10 pr-4 py-2 text-sm bg-gray-100 border-0 rounded-full focus:ring-2 focus:ring-blue-500 focus:outline-none transition-colors"
                v-model="searchTerm"
                @input="filterUsers"
              >
              <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
            
            <div class="relative">
              <button class="relative text-gray-500 hover:text-gray-600">
                <i class="fas fa-bell text-xl"></i>
                <span class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">{{ notifications.length }}</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto bg-gray-50 p-4 lg:p-6">
        <!-- 顶部操作区 -->
        <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">用户列表</h1>
            <p class="text-gray-500 mt-1">管理所有系统用户，包括账号状态和角色设置</p>
          </div>
          <div class="mt-4 md:mt-0">
            <!-- 只有超级用户可以创建子账户 -->
          <button v-if="isSuperUser" class="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors shadow-sm" @click="showCreateSubAccountModal = true">
            <i class="fas fa-plus mr-2"></i>创建子账户
          </button>
          </div>
        </div>
        
        <!-- 创建子账户模态框 -->
        <div v-if="showCreateSubAccountModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div class="bg-white rounded-lg w-full max-w-md p-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">创建子账户</h3>
            
            <!-- 表单错误提示 -->
            <div v-if="subAccountFormErrors.length > 0" class="mb-4">
              <div v-for="error in subAccountFormErrors" :key="error" class="text-red-500 text-sm mb-1">
                {{ error }}
              </div>
            </div>
            
            <form @submit.prevent="handleCreateSubAccount">
              <div class="mb-4">
                <label for="username" class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                <input
                  id="username"
                  type="text"
                  v-model="subAccountForm.username"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="请输入用户名"
                  required
                />
              </div>
              
              <div class="mb-4">
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
                <input
                  id="email"
                  type="email"
                  v-model="subAccountForm.email"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
                  placeholder="请输入邮箱"
                  required
                />
              </div>
              
              <div class="mb-4">
                <label for="password" class="block text-sm font-medium text-gray-700 mb-1">初始密码</label>
                <div class="relative">
                  <input
                    id="password"
                    :type="showPassword ? 'text' : 'password'"
                    v-model="subAccountForm.password"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
                    placeholder="请输入初始密码"
                    required
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    @click="togglePasswordVisibility('password')"
                  >
                    <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                  </button>
                </div>
              </div>
              
              <div class="mb-4">
                <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
                <div class="relative">
                  <input
                    id="confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    v-model="subAccountForm.confirmPassword"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
                    placeholder="请再次输入密码"
                    required
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    @click="togglePasswordVisibility('confirmPassword')"
                  >
                    <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                  </button>
                </div>
              </div>
              
              <div class="mb-4">
                <label for="role" class="block text-sm font-medium text-gray-700 mb-1">角色</label>
                <select
                  id="role"
                  v-model="subAccountForm.role"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  required
                >
                  <option value="admin">管理员</option>
                  <option value="editor">编辑</option>
                  <option value="viewer">查看者</option>
                </select>
              </div>
              
              <div class="flex justify-end space-x-3">
                <button 
                  type="button"
                  @click="closeCreateSubAccountModal"
                  class="px-4 py-2 border border-gray-300 rounded text-gray-700 hover:bg-gray-50 transition-colors"
                >
                  取消
                </button>
                <button 
                  type="submit"
                  class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
                >
                  确认创建
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- 统计卡片 - 只有超级用户可以查看 -->
        <div v-if="isSuperUser" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100 transform hover:-translate-y-2 transition-all duration-300">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-500">总用户数</h3>
              <span class="p-1.5 bg-blue-100 text-blue-600 rounded-lg">
                <i class="fas fa-users"></i>
              </span>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ users.length }}</p>
            <p class="text-xs text-green-500 mt-2"><i class="fas fa-arrow-up"></i> 较上月增长 12%</p>
          </div>
          <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100 transform hover:-translate-y-2 transition-all duration-300">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-500">超级用户</h3>
              <span class="p-1.5 bg-purple-100 text-purple-600 rounded-lg">
                <i class="fas fa-user-shield"></i>
              </span>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ superUsersCount }}</p>
            <p class="text-xs text-gray-500 mt-2">占总用户 {{ superUsersPercentage }}%</p>
          </div>
          <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100 transform hover:-translate-y-2 transition-all duration-300">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-500">活跃用户</h3>
              <span class="p-1.5 bg-green-100 text-green-600 rounded-lg">
                <i class="fas fa-user-check"></i>
              </span>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ activeUsersCount }}</p>
            <p class="text-xs text-green-500 mt-2"><i class="fas fa-arrow-up"></i> 较上月增长 8%</p>
          </div>
          <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100 transform hover:-translate-y-2 transition-all duration-300">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-500">禁用用户</h3>
              <span class="p-1.5 bg-red-100 text-red-600 rounded-lg">
                <i class="fas fa-user-slash"></i>
              </span>
            </div>
            <p class="text-3xl font-bold text-gray-900">{{ disabledUsersCount }}</p>
            <p class="text-xs text-red-500 mt-2"><i class="fas fa-arrow-down"></i> 较上月减少 3%</p>
          </div>
        </div>

        <!-- 用户列表表格 -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    用户信息
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    角色
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    状态
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    注册时间
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    最后登录
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    IP地址
                  </th>
                  <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <!-- 超级用户可以看到所有用户，普通用户只能看到自己 -->
                <tr v-for="user in isSuperUser ? filteredUsers : filteredUsers.filter(u => u.id === currentUser?.id)" :key="user.id">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="flex-shrink-0 h-10 w-10">
                        <img class="h-10 w-10 rounded-full" :src="getUserAvatar(user.email)" alt="">
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">{{ user.name || '未设置' }}</div>
                        <div class="text-sm text-gray-500">{{ user.email }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span v-if="user.role === 0" class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-700 shadow-sm">
                      超级用户
                    </span>
                    <span v-else class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-700 shadow-sm">
                      普通用户
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span v-if="user.status === 1" class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-700 shadow-sm">
                      活跃
                    </span>
                    <span v-else-if="user.status === 0" class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-700 shadow-sm">
                      禁用
                    </span>
                    <span v-else class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-700 shadow-sm">
                      待审核
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(user.created_at) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm">
                    <div class="flex items-center">
                      <span class="text-gray-700 font-medium">{{ formatDate(user.lastLoginTime) }}</span>
                      <span v-if="isRecentLogin(user.lastLoginTime)" class="ml-2 px-1.5 py-0.5 text-xs bg-green-100 text-green-800 rounded">
                        刚刚
                      </span>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm">
                    <div class="flex flex-col space-y-1">
                      <div class="flex items-center">
                        <span class="text-indigo-600 font-medium">{{ user.mostUsedIp || user.lastLoginIp || '未知' }}</span>
                        <span v-if="user.mostUsedIp && user.lastLoginIp && user.mostUsedIp !== user.lastLoginIp" class="ml-1 px-1 py-0.5 text-xs bg-indigo-100 text-indigo-800 rounded">
                          最常用
                        </span>
                      </div>
                      <div v-if="user.mostUsedIp && user.lastLoginIp && user.mostUsedIp !== user.lastLoginIp" class="text-xs text-gray-500">
                        上次登录: {{ user.lastLoginIp }}
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <!-- 只有超级用户可以进行用户角色和状态的管理操作 -->
                    <button v-if="isSuperUser && (user.role !== 0 || canRemoveLastSuperUser)" @click="changeUserRole(user)" class="text-indigo-600 hover:text-indigo-900 mr-4">
                      {{ user.role === 0 ? '降级' : '升级' }}
                    </button>
                    <button v-if="isSuperUser && user.role !== 0" @click="toggleUserStatus(user)" class="text-blue-600 hover:text-blue-900 mr-4">
                      {{ user.status === 1 ? '禁用' : '启用' }}
                    </button>
                    <!-- 所有用户都可以查看自己的详情 -->
                    <button @click="editUser(user)" class="text-green-600 hover:text-green-900">
                      编辑
                    </button>
                  </td>
                </tr>
                <!-- 如果普通用户没有找到自己的记录，显示空状态 -->
                <tr v-if="!isSuperUser && filteredUsers.filter(u => u.id === currentUser?.id).length === 0">
                  <td colspan="7" class="px-6 py-10 text-center text-sm text-gray-500">
                    暂无用户数据
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- 分页 -->
          <div class="px-6 py-3 bg-gray-50 border-t border-gray-200 sm:px-6 flex items-center justify-between">
            <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
              <div>
                <p class="text-sm text-gray-700">
                  显示 <span class="font-medium">{{ Math.min((currentPage - 1) * pageSize + 1, filteredUsers.length) }}</span> 到 <span class="font-medium">{{ Math.min(currentPage * pageSize, filteredUsers.length) }}</span> 条，共 <span class="font-medium">{{ filteredUsers.length }}</span> 条结果
                </p>
              </div>
              <div>
                <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                  <button @click="prevPage" :disabled="currentPage === 1" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                    <span class="sr-only">上一页</span>
                    <i class="fas fa-chevron-left"></i>
                  </button>
                  <button v-for="page in visiblePages" :key="page" @click="goToPage(page)" :class="['relative inline-flex items-center px-4 py-2 border text-sm font-medium', currentPage === page ? 'bg-blue-50 border-blue-300 text-blue-700' : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50']">
                    {{ page }}
                  </button>
                  <button @click="nextPage" :disabled="currentPage === totalPages" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                    <span class="sr-only">下一页</span>
                    <i class="fas fa-chevron-right"></i>
                  </button>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
    <!-- 自定义确认弹窗 -->
    <CustomConfirmModal
      v-if="showConfirmModal"
      :show="showConfirmModal"
      :title="confirmModalTitle"
      :message="confirmModalMessage"
      @confirm="handleConfirm"
      @cancel="handleCancel"
      @close="showConfirmModal = false"
    />
    
    <!-- 用户详情弹窗 -->
    <UserDetailsModal
      :show="showUserDetailsModal"
      :user="selectedUser"
      :userLogs="userLogs"
      :currentPage="userLogsPage"
      :pageSize="userLogsPageSize"
      :totalItems="userLogsTotal"
      :totalPages="userLogsTotalPages"
      :searchParams="userLogsSearch"
      @close="closeUserDetailsModal"
      @userUpdated="handleUserUpdated"
      @search="searchUserLogs"
      @resetSearch="resetUserLogsSearch"
      @pageChange="changeUserLogsPage"
      @pageSizeChange="changeUserLogsPageSize"
    />
  </div>
</template>

<script>
import { getCurrentUser, logout as logoutUser } from '../utils/auth.js';
import { userAPI } from '../utils/api.js';
import { showSuccess, showError } from '../utils/notification.js';
import CommonSidebar from './CommonSidebar.vue';
import CustomConfirmModal from './CustomConfirmModal.vue';
import UserDetailsModal from './UserDetailsModal.vue';

export default {
  name: 'UserManagement',
  components: {
    CommonSidebar,
    CustomConfirmModal,
    UserDetailsModal
  },
  data() {
      return {
        currentUser: null,
        users: [],
        filteredUsers: [],
        searchTerm: '',
        currentPage: 1,
        pageSize: 10,
        totalPages: 1,
        notifications: [],
        isLoading: false,
        isSuperUser: false,
        // 子账户创建表单
        showCreateSubAccountModal: false,
        // 自定义确认弹窗
        showConfirmModal: false,
        confirmModalTitle: '',
        confirmModalMessage: '',
        confirmModalCallback: null,
        // 编辑用户相关
        showUserDetailsModal: false,
        selectedUser: null,
        userLogs: [],
        // 操作日志分页和搜索
        userLogsPage: 1,
        userLogsPageSize: 10,
        userLogsTotal: 0,
        userLogsTotalPages: 1,
        userLogsSearch: {
          operationType: '',
          description: ''
        },
        subAccountForm: {
          username: '',
          email: '',
          password: '',
          confirmPassword: '',
          role: 'editor' // 默认角色设置为编辑
        },
        subAccountFormErrors: [],
        showPassword: false,
        showConfirmPassword: false
      };
  },
  computed: {
    superUsersCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.role === 0).length;
    },
    superUsersPercentage() {
      if (!Array.isArray(this.users) || this.users.length === 0) {
        return 0;
      }
      return Math.round((this.superUsersCount / this.users.length) * 100);
    },
    activeUsersCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.status === 1).length;
    },
    disabledUsersCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.status === 0).length;
    },
    canRemoveLastSuperUser() {
      return this.superUsersCount > 1;
    },
    visiblePages() {
      const total = this.totalPages;
      const current = this.currentPage;
      const maxVisible = 5;
      
      if (total <= maxVisible) {
        return Array.from({ length: total }, (_, i) => i + 1);
      }
      
      let start = Math.max(1, current - Math.floor(maxVisible / 2));
      let end = Math.min(total, start + maxVisible - 1);
      
      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }
      
      return Array.from({ length: end - start + 1 }, (_, i) => i + start);
    }
  },
  async mounted() {
    // 初始化用户信息
    this.currentUser = getCurrentUser();
    this.isSuperUser = this.currentUser && this.currentUser.role === 0;
    
    // 检查是否已登录
    if (!this.currentUser) {
      showError('请先登录', '未登录');
      window.location.hash = '#/';
      return;
    }
    
    // 加载用户列表
    await this.loadUsers();
  },
  methods: {
      logout() {
        logoutUser();
      },
      
      // 判断是否为最近登录（24小时内）
      isRecentLogin(loginTime) {
        if (!loginTime) return false;
        const now = new Date();
        const loginDate = new Date(loginTime);
        const diffHours = (now - loginDate) / (1000 * 60 * 60);
        return diffHours < 24;
      },
      
      // 显示确认弹窗
      showConfirm(title, message, callback) {
        this.confirmModalTitle = title;
        this.confirmModalMessage = message;
        this.confirmModalCallback = callback;
        this.showConfirmModal = true;
      },
    
    // 处理弹窗确认
    handleConfirm() {
      if (this.confirmModalCallback) {
        this.confirmModalCallback();
      }
      this.showConfirmModal = false;
    },
    
    // 处理弹窗取消
    handleCancel() {
      this.showConfirmModal = false;
    },
    
    async loadUsers() {
      try {
        this.isLoading = true;
        const response = await userAPI.getUsers();
        // 确保正确提取响应中的users数组
        this.users = Array.isArray(response?.users) ? response.users : [];
        
        this.filterUsers();
      } catch (error) {
        console.error('加载用户列表失败:', error);
        showError('加载用户列表失败，请稍后再试', '加载失败');
        // 即使出错，也要确保this.users是数组
        this.users = [];
        this.filterUsers();
      } finally {
        this.isLoading = false;
      }
    },
    
    filterUsers() {
      const searchTerm = this.searchTerm.toLowerCase();
      // 添加类型检查，确保this.users是数组
      if (!Array.isArray(this.users)) {
        this.filteredUsers = [];
        this.totalPages = 1;
        this.currentPage = 1;
        return;
      }
      this.filteredUsers = this.users.filter(user => 
        (user.username && user.username.toLowerCase().includes(searchTerm)) || 
        (user.email && user.email.toLowerCase().includes(searchTerm))
      );
      this.totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
      this.currentPage = 1;
    },
    
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    
    goToPage(page) {
      this.currentPage = page;
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    getUserAvatar(email) {
      // 使用Gravatar或其他服务生成头像
      const hash = email ? this.md5(email.trim().toLowerCase()) : '';
      return `https://picsum.photos/id/${hash ? parseInt(hash.substring(0, 2), 16) % 100 : 1005}/200/200`;
    },
    
    md5(str) {
      // 简单的md5实现，仅用于演示
      // 实际项目中应该使用专门的库
      let hash = 0;
      if (str.length === 0) return hash.toString();
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // 转换为32位整数
      }
      return Math.abs(hash).toString(16);
    },
    
    async changeUserRole(user) {
      const newRole = user.role === 0 ? 1 : 0;
      const actionText = newRole === 0 ? '升级为超级用户' : '降级为普通用户';
      
      // 防止删除最后一个超级用户
      if (user.role === 0 && this.superUsersCount <= 1) {
        showError('不能删除最后一个超级用户！', '操作失败');
        return;
      }
      
      // 使用自定义确认弹窗
      this.showConfirm(
        '确认角色更改',
        `确定要将用户 ${user.email} ${actionText}吗？`,
        async () => {
          try {
            await userAPI.updateUserRole(user.id, newRole);
            // 直接更新本地数据，避免重新加载
            user.role = newRole;
            showSuccess(`用户 ${user.email} ${actionText}成功！`, '操作成功');
          } catch (error) {
            console.error('更新用户角色失败:', error);
            showError('更新用户角色失败，请稍后再试', '操作失败');
          }
        }
      );
    },
    
    async toggleUserStatus(user) {
      const newStatus = user.status === 1 ? 0 : 1;
      const actionText = newStatus === 1 ? '启用' : '禁用';
      
      // 超级用户不能被停用
      if (user.role === 0) {
        showError('超级账户不能被停用', '操作失败');
        return;
      }
      
      // 使用自定义确认弹窗
      this.showConfirm(
        '确认状态更改',
        `确定要${actionText}用户 ${user.email}吗？`,
        async () => {
          try {
            await userAPI.updateUserStatus(user.id, newStatus);
            // 直接更新本地数据，避免重新加载
            user.status = newStatus;
            showSuccess(`用户 ${user.email} ${actionText}成功！`, '操作成功');
          } catch (error) {
            console.error('更新用户状态失败:', error);
            showError('更新用户状态失败，请稍后再试', '操作失败');
          }
        }
      );
    },
    
    // 编辑用户（查看详情和操作日志）
    editUser(user) {
      this.selectedUser = user;
      // 重置操作日志的分页和搜索条件
      this.userLogsPage = 1;
      this.userLogsPageSize = 10;
      this.userLogsSearch = {
        operationType: '',
        description: ''
      };
      // 加载用户操作日志
      this.loadUserLogs(user.id);
      this.showUserDetailsModal = true;
    },
    
    // 关闭用户详情弹窗
    closeUserDetailsModal() {
      this.showUserDetailsModal = false;
      this.selectedUser = null;
      this.userLogs = [];
    },
    
    // 加载用户操作日志
    async loadUserLogs(userId) {
      try {
        // 检查当前登录用户是否是超级用户
        const canAccess = this.isSuperUser || (userId === this.currentUser?.id);
        
        if (canAccess) {
          // 构建查询参数
          const params = {
            page: this.userLogsPage,
            pageSize: this.userLogsPageSize,
            operationType: this.userLogsSearch.operationType,
            description: this.userLogsSearch.description,
            userId: userId // 添加用户ID参数，用于筛选特定用户的日志
          };
          
          const response = await userAPI.getUserLogs(params);
          this.userLogs = Array.isArray(response?.logs) ? response.logs : [];
          this.userLogsTotal = response?.total || 0;
          this.userLogsTotalPages = response?.totalPages || 1;
        } else {
          // 如果没有权限查看，显示空列表
          this.userLogs = [];
          this.userLogsTotal = 0;
          this.userLogsTotalPages = 1;
        }
      } catch (error) {
        console.error('加载用户操作日志失败:', error);
        this.userLogs = [];
        this.userLogsTotal = 0;
        this.userLogsTotalPages = 1;
      }
    },
    
    // 搜索操作日志
    searchUserLogs() {
      this.userLogsPage = 1;
      if (this.selectedUser) {
        this.loadUserLogs(this.selectedUser.id);
      }
    },
    
    // 重置搜索条件
    resetUserLogsSearch() {
      this.userLogsSearch = {
        operationType: '',
        description: ''
      };
      this.searchUserLogs();
    },
    
    // 切换操作日志页码
    changeUserLogsPage(page) {
      this.userLogsPage = page;
      if (this.selectedUser) {
        this.loadUserLogs(this.selectedUser.id);
      }
    },
    
    // 更改每页显示条数
    changeUserLogsPageSize(pageSize) {
      this.userLogsPageSize = Math.min(pageSize, 50); // 最多50条
      this.userLogsPage = 1;
      if (this.selectedUser) {
        this.loadUserLogs(this.selectedUser.id);
      }
    },
    
    // 关闭创建子账户模态框
    closeCreateSubAccountModal() {
      this.showCreateSubAccountModal = false;
      this.resetSubAccountForm();
    },
    
    // 重置子账户表单
    resetSubAccountForm() {
      this.subAccountForm = {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      };
      this.formErrors = {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      };
    },
    
    // 验证表单
    validateForm() {
      this.subAccountFormErrors = [];
      
      if (!this.subAccountForm.username) {
        this.subAccountFormErrors.push('用户名不能为空');
      }
      
      if (!this.subAccountForm.email) {
        this.subAccountFormErrors.push('邮箱不能为空');
      } else if (!this.isValidEmail(this.subAccountForm.email)) {
        this.subAccountFormErrors.push('请输入有效的邮箱地址');
      }
      
      if (!this.subAccountForm.password) {
        this.subAccountFormErrors.push('密码不能为空');
      } else if (this.subAccountForm.password.length < 6) {
        this.subAccountFormErrors.push('密码长度不能少于6个字符');
      }
      
      if (this.subAccountForm.password !== this.subAccountForm.confirmPassword) {
        this.subAccountFormErrors.push('两次输入的密码不一致');
      }
      
      if (!this.subAccountForm.role) {
        this.subAccountFormErrors.push('请选择角色');
      }
      
      return this.subAccountFormErrors.length === 0;
    },
    
    // 处理创建子账户
    handleCreateSubAccount() {
      if (this.validateForm()) {
        // 准备创建子账户的数据
        const subAccountData = {
          username: this.subAccountForm.username,
          email: this.subAccountForm.email,
          password: this.subAccountForm.password,
          role: this.subAccountForm.role
        };
        
        // 调用API创建子账户
        userAPI.createSubAccount(subAccountData).then(response => {
          showSuccess('子账户创建成功');
          this.closeCreateSubAccountModal();
          this.resetSubAccountForm();
          this.loadUsers(); // 重新加载用户列表
        }).catch(error => {
          console.error('创建子账户失败:', error);
          showError('创建子账户失败，请稍后重试', '错误');
        });
      }
    },
    
    // 显示确认弹窗
    showConfirm(title, message, callback) {
      this.confirmModalTitle = title;
      this.confirmModalMessage = message;
      this.confirmModalCallback = callback;
      this.showConfirmModal = true;
    },
    
    // 处理确认操作
    handleConfirm() {
      this.showConfirmModal = false;
      if (this.confirmModalCallback && typeof this.confirmModalCallback === 'function') {
        this.confirmModalCallback();
      }
      this.confirmModalCallback = null;
    },
    
    // 处理取消操作
    handleCancel() {
      this.showConfirmModal = false;
      this.confirmModalCallback = null;
    }
  }
}
</script>

<style scoped>
/* 用户管理页面特定样式 */
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .grid-cols-1 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}
</style>