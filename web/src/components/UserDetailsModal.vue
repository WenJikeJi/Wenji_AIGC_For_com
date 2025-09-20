<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg w-full max-w-4xl max-h-[90vh] flex flex-col">
      <!-- 弹窗头部 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-900">用户详情</h3>
        <button @click="handleClose" class="text-gray-400 hover:text-gray-500">
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <!-- 弹窗内容 -->
      <div class="flex-1 overflow-y-auto p-6">
        <!-- 账户基本信息 -->
        <div class="mb-8">
          <h4 class="text-md font-medium text-gray-700 mb-4">账户基本信息</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">用户名</p>
              <p class="font-medium text-gray-900">{{ user?.username || '未设置' }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">邮箱</p>
              <p class="font-medium text-gray-900">{{ user?.email }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">角色</p>
              <p class="font-medium">
                <span v-if="user?.role === 0" class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-700 shadow-sm">
                  超级用户
                </span>
                <span v-else class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-700 shadow-sm">
                  普通用户
                </span>
              </p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">状态</p>
              <p class="font-medium">
                <span v-if="user?.status === 1" class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-700 shadow-sm">
                  活跃
                </span>
                <span v-else-if="user?.status === 0" class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-700 shadow-sm">
                  禁用
                </span>
                <span v-else class="px-3 py-1.5 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-700 shadow-sm">
                  待审核
                </span>
              </p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">创建时间</p>
              <p class="font-medium text-gray-900">{{ formatDate(user?.created_at) }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">最后登录</p>
              <div class="flex items-center">
                <p class="font-medium text-gray-900">{{ formatDate(user?.last_login_at) }}</p>
                <span v-if="isRecentLogin(user?.last_login_at)" class="ml-2 px-1.5 py-0.5 text-xs bg-green-100 text-green-800 rounded">
                  刚刚
                </span>
              </div>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">最常用IP</p>
              <div class="flex items-center">
                <p class="font-medium text-indigo-600">{{ user?.mostUsedIp || user?.last_login_ip || '未知' }}</p>
                <span v-if="user?.mostUsedIp && user?.last_login_ip && user?.mostUsedIp !== user?.last_login_ip" class="ml-1 px-1 py-0.5 text-xs bg-indigo-100 text-indigo-800 rounded">
                  最常用
                </span>
              </div>
            </div>
            <div v-if="user?.mostUsedIp && user?.last_login_ip && user?.mostUsedIp !== user?.last_login_ip" class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">最后登录IP</p>
              <p class="font-medium">{{ user?.last_login_ip || '未知' }}</p>
            </div>
          </div>
        </div>

        <!-- 重置密码确认 -->
        <div v-if="showChangePasswordForm" class="mb-8 p-4 bg-blue-50 rounded-lg">
          <h4 class="text-md font-medium text-gray-700 mb-4">重置密码</h4>
          <div class="space-y-4">
            <div v-if="passwordChangeError" class="text-red-500 text-sm mb-2">{{ passwordChangeError }}</div>
            <div class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
              <p class="text-sm text-gray-700">
                <i class="fas fa-info-circle text-yellow-500 mr-2"></i>
                点击确认后，系统将为用户生成一个新的随机密码，并发送到用户的邮箱地址。
              </p>
            </div>
            <div class="flex space-x-3">
              <button @click="changePassword" class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors">
                确认重置
              </button>
              <button @click="cancelChangePassword" class="px-4 py-2 border border-gray-300 rounded text-gray-700 hover:bg-gray-50 transition-colors">
                取消
              </button>
            </div>
          </div>
        </div>
        
        <!-- 操作日志 -->
        <div>
          <h4 class="text-md font-medium text-gray-700 mb-4">操作日志</h4>
          
          <!-- 搜索框 -->
          <div class="mb-4 flex flex-col md:flex-row gap-3">
            <div class="flex-1">
              <input
                type="text"
                placeholder="搜索操作类型..."
                v-model="localSearch.operationType"
                @input="handleSearch"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
              />
            </div>
            <div class="flex-1">
              <input
                type="text"
                placeholder="搜索操作描述..."
                v-model="localSearch.description"
                @input="handleSearch"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
              />
            </div>
            <button @click="handleResetSearch" class="px-4 py-2 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors whitespace-nowrap">
              重置
            </button>
          </div>
          
          <!-- 日志表格 -->
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作时间
                  </th>
                  <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作类型
                  </th>
                  <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作描述
                  </th>
                  <th scope="col" class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    操作IP
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="log in userLogs" :key="log.id">
                  <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-900 font-medium">
                    {{ formatDate(log.created_at) }}
                  </td>
                  <td class="px-4 py-3 whitespace-nowrap text-sm">
                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                      {{ log.operation_type }}
                    </span>
                  </td>
                  <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-900">
                    {{ log.description }}
                  </td>
                  <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-900">
                    {{ log.ip_address || '未知' }}
                  </td>
                </tr>
                <tr v-if="userLogs.length === 0">
                  <td colspan="4" class="px-4 py-6 text-center text-sm text-gray-500">
                    暂无操作日志
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- 分页控件 -->
          <div class="mt-4 flex flex-col sm:flex-row items-center justify-between">
            <div class="flex items-center mb-2 sm:mb-0">
              <span class="text-sm text-gray-700 mr-2">每页显示：</span>
              <select 
                v-model="localPageSize" 
                @change="handlePageSizeChange"
                class="px-2 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="10">10条</option>
                <option value="20">20条</option>
                <option value="50">50条</option>
              </select>
            </div>
            
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <button 
                @click="handlePrevPage"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">上一页</span>
                <i class="fas fa-chevron-left"></i>
              </button>
              
              <button 
                v-for="page in visiblePages"
                :key="page"
                @click="handlePageChange(page)"
                :class="['relative inline-flex items-center px-4 py-2 border text-sm font-medium', currentPage === page ? 'bg-blue-50 border-blue-300 text-blue-700' : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50']"
              >
                {{ page }}
              </button>
              
              <button 
                @click="handleNextPage"
                :disabled="currentPage === totalPages"
                class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              >
                <span class="sr-only">下一页</span>
                <i class="fas fa-chevron-right"></i>
              </button>
            </nav>
          </div>
        </div>
      </div>
      
      <!-- 弹窗底部 -->
      <div class="flex justify-end p-6 border-t border-gray-200 space-x-3">
        <button v-if="!showChangePasswordForm" @click="showChangePasswordForm = true" class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition-colors">
          修改密码
        </button>
        <button 
          v-if="!showChangePasswordForm && user?.role !== 0" 
          @click="toggleUserStatus"
          :class="['px-4 py-2 rounded transition-colors', user?.status === 1 ? 'bg-red-600 text-white hover:bg-red-700' : 'bg-green-600 text-white hover:bg-green-700']"
        >
          {{ user?.status === 1 ? '停用账户' : '启用账户' }}
        </button>
        <button @click="handleClose" class="px-4 py-2 border border-gray-300 rounded text-gray-700 hover:bg-gray-50 transition-colors">
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { userAPI } from '../utils/api.js';
import { showSuccess, showError } from '../utils/notification.js';

export default {
  name: 'UserDetailsModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    user: {
      type: Object,
      default: null
    },
    userLogs: {
      type: Array,
      default: () => []
    },
    // 分页相关属性
    currentPage: {
      type: Number,
      default: 1
    },
    pageSize: {
      type: Number,
      default: 10
    },
    totalItems: {
      type: Number,
      default: 0
    },
    totalPages: {
      type: Number,
      default: 1
    },
    // 搜索相关属性
    searchParams: {
      type: Object,
      default: () => ({
        operationType: '',
        description: ''
      })
    }
  },
  data() {
    return {
      showChangePasswordForm: false,
      passwordChangeError: '',
      // 本地搜索参数副本，避免直接修改props
      localSearch: {
        operationType: '',
        description: ''
      },
      // 本地每页显示条数副本
      localPageSize: 10
    };
  },
  computed: {
    // 计算可见页码
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
  watch: {
    show(newVal) {
      if (!newVal) {
        // 当弹窗关闭时，重置表单状态
        this.resetFormState();
      }
    },
    // 监听搜索参数变化
    searchParams: {
      handler(newVal) {
        this.localSearch = { ...newVal };
      },
      deep: true,
      immediate: true
    },
    // 监听每页显示条数变化
    pageSize(newVal) {
      this.localPageSize = newVal;
    }
  },
  methods: {
    // 判断是否为最近登录（24小时内）
    isRecentLogin(loginTime) {
      if (!loginTime) return false;
      const now = new Date();
      const loginDate = new Date(loginTime);
      const diffHours = (now - loginDate) / (1000 * 60 * 60);
      return diffHours < 24;
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
    handleClose() {
      this.$emit('close');
    },
    resetFormState() {
      this.showChangePasswordForm = false;
      this.passwordChangeError = '';
    },
    cancelChangePassword() {
      this.showChangePasswordForm = false;
      this.passwordChangeError = '';
    },
    async changePassword() {
      if (!this.user || !this.user.id) {
        showError('用户信息无效', '操作失败');
        return;
      }

      try {
        // 调用API重置密码（系统会生成随机密码并发送给用户）
        await userAPI.resetUserPassword(this.user.id);
        showSuccess('密码重置成功，新密码已发送到用户邮箱', '操作成功');
        this.cancelChangePassword();
        // 触发用户更新事件，通知父组件
        this.$emit('userUpdated', { ...this.user });
      } catch (error) {
        console.error('重置密码失败:', error);
        showError('重置密码失败，请稍后重试', '操作失败');
      }
    },
    async toggleUserStatus() {
      if (!this.user || !this.user.id) {
        showError('用户信息无效', '操作失败');
        return;
      }

      // 超级用户不能被停用
      if (this.user.role === 0) {
        showError('超级账户不能被停用', '操作失败');
        return;
      }

      const newStatus = this.user.status === 1 ? 0 : 1;
      const actionText = newStatus === 1 ? '启用' : '停用';

      try {
        // 调用API更新用户状态
        await userAPI.updateUserStatus(this.user.id, newStatus);
        showSuccess(`用户${actionText}成功`, '操作成功');
        
        // 更新本地用户状态
        const updatedUser = { ...this.user, status: newStatus };
        this.$emit('userUpdated', updatedUser);
      } catch (error) {
        console.error(`更新用户状态失败:`, error);
        showError(`用户${actionText}失败，请稍后重试`, '操作失败');
      }
    },
    // 处理搜索
    handleSearch() {
      this.$emit('search', this.localSearch);
    },
    // 重置搜索条件
    handleResetSearch() {
      this.localSearch = {
        operationType: '',
        description: ''
      };
      this.$emit('reset-search');
    },
    // 切换页码
    handlePageChange(page) {
      this.$emit('page-change', page);
    },
    // 上一页
    handlePrevPage() {
      if (this.currentPage > 1) {
        this.$emit('page-change', this.currentPage - 1);
      }
    },
    // 下一页
    handleNextPage() {
      if (this.currentPage < this.totalPages) {
        this.$emit('page-change', this.currentPage + 1);
      }
    },
    // 更改每页显示条数
    handlePageSizeChange() {
      this.$emit('page-size-change', this.localPageSize);
    }
  }
};
</script>

<style scoped>
/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>