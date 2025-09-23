<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <div class="p-5 border-b border-gray-100">
      <h3 class="font-semibold text-gray-900">Facebook 授权管理</h3>
    </div>
    
    <div class="p-5">
      <!-- 已授权账号列表 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">已授权账号 ({{ authorizedAccounts.length }})</h4>
        <div class="space-y-3">
          <div v-if="authorizedAccounts.length === 0" class="p-4 bg-gray-50 rounded-lg text-center text-gray-500 text-sm">
            暂无已授权的Facebook账号
          </div>
          <div 
            v-for="(account, index) in authorizedAccounts" 
            :key="account.id || index"
            class="p-4 bg-gray-50 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
                  <i class="fab fa-facebook"></i>
                </div>
                <div class="ml-3">
                  <p class="text-sm font-medium text-gray-900">{{ account.name || '未知账号' }}</p>
                  <p class="text-xs text-gray-500">
                    {{ account.connected ? 
                      `已授权，${formatExpiryDate(account.expiryDate)}` : 
                      '等待授权连接' 
                    }}
                  </p>
                  <p class="text-xs text-gray-400 mt-1">
                    Token: {{ account.token ? account.token.substring(0, 20) + '...' : '无' }}
                  </p>
                </div>
              </div>
              
              <div class="flex items-center space-x-2">
                <span 
                  class="px-3 py-1 text-xs font-semibold rounded-full"
                  :class="account.connected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                >
                  {{ account.connected ? '已连接' : '未连接' }}
                </span>
                <button 
                  @click="removeAccount(index)"
                  class="p-2 text-red-500 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                  title="删除此账号"
                >
                  <i class="fas fa-trash text-sm"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Token输入方式 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">授权方式</h4>
        <div class="grid grid-cols-1 gap-4">
          <!-- 手动输入Token（系统Token） -->
          <div class="p-4 border border-gray-200 rounded-lg">
            <div class="flex items-start mb-3">
              <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">
                <i class="fas fa-key"></i>
              </div>
              <h5 class="ml-2 text-sm font-medium text-gray-900">手动输入系统Token</h5>
            </div>
            <p class="text-xs text-gray-500 mb-3">输入您的Facebook系统访问Token以授权系统访问您的Facebook账户。</p>
            
            <div class="space-y-3">
              <!-- Access Token -->
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">Access Token <span class="text-red-500">*</span></label>
                <input 
                  type="text" 
                  v-model="tokenForm.accessToken" 
                  placeholder="输入Facebook系统Token..." 
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500 transition-colors"
                >
              </div>
              
              <!-- Token过期时间 -->
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">Token过期时间（可选）</label>
                <input 
                  type="date" 
                  v-model="tokenForm.expiryDate" 
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500 transition-colors"
                >
                <p class="text-xs text-gray-400 mt-1">如果不填写，系统将无法提前预测Token过期</p>
              </div>
              
              <!-- Facebook主页ID -->
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">Facebook主页ID（可选）</label>
                <input 
                  type="text" 
                  v-model="tokenForm.pageId" 
                  placeholder="输入Facebook主页ID..." 
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500 transition-colors"
                >
                <p class="text-xs text-gray-400 mt-1">如果Token是用户级别的，请指定要管理的Facebook主页ID</p>
              </div>
              
              <!-- 账户名称 -->
              <div>
                <label class="block text-xs font-medium text-gray-700 mb-1">账户名称（可选）</label>
                <input 
                  type="text" 
                  v-model="tokenForm.accountName" 
                  placeholder="为此账户设置一个易识别的名称..." 
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500 transition-colors"
                >
                <p class="text-xs text-gray-400 mt-1">如果不填写，系统将自动获取账户信息</p>
              </div>
              
              <!-- Token验证状态 -->
              <div v-if="tokenValidationStatus" :class="tokenValidationStatus.isValid ? 'bg-green-50 text-green-800 border border-green-200' : 'bg-red-50 text-red-800 border border-red-200'" class="p-3 rounded-lg text-sm">
                {{ tokenValidationStatus.message }}
              </div>
              
              <!-- 操作按钮 -->
              <div class="flex space-x-3">
                <button 
                  class="flex-1 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                  @click="validateToken"
                  :disabled="!tokenForm.accessToken.trim() || isVerifyingToken"
                >
                  {{ isVerifyingToken ? '验证中...' : '验证Token' }}
                </button>
                <button 
                  class="flex-1 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 transition-colors"
                  @click="saveToken"
                  :disabled="!tokenForm.accessToken.trim() || !tokenValidationStatus?.isValid"
                >
                  验证并保存
                </button>
              </div>
            </div>
          </div>
          
          <!-- Facebook登录授权 -->
          <div class="p-4 border border-gray-200 rounded-lg">
            <div class="flex items-start mb-3">
              <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
                <i class="fab fa-facebook-f"></i>
              </div>
              <h5 class="ml-2 text-sm font-medium text-gray-900">Facebook登录授权</h5>
            </div>
            <p class="text-xs text-gray-500 mb-3">通过Facebook账户直接登录授权，系统将自动获取和管理Token。</p>
            <button 
              class="w-full py-2 bg-white border border-blue-600 text-blue-600 text-sm font-medium rounded-lg hover:bg-blue-50 transition-colors"
              @click="loginWithFacebook"
            >
              <i class="fab fa-facebook-f mr-1"></i>
              通过Facebook登录
            </button>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="flex justify-end space-x-3 pt-4 border-t border-gray-100">
        <button 
          class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-200 transition-colors"
          @click="refreshStatus"
        >
          刷新状态
        </button>
        <button 
          class="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors"
          @click="cancel"
        >
          取消
        </button>
      </div>
    </div>
    
    <!-- 删除确认弹窗 -->
    <CustomConfirmModal
      v-if="showUnbindConfirm"
      title="确认删除账号"
      :message="accountToRemove !== null ? '确定要删除账号 ' + (authorizedAccounts[accountToRemove]?.name || '未知账号') + ' 吗？此操作不可撤销。' : '确定要删除此账号吗？'"
      confirmText="删除"
      cancelText="取消"
      @confirm="confirmRemoveAccount"
      @cancel="cancelRemove"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { socialMediaAPI } from '../../utils/api.js';
import { showSuccess, showError } from '../../utils/notification.js';
import CustomConfirmModal from '../CustomConfirmModal.vue';

export default {
  name: 'FacebookTokenManager',
  components: {
    CustomConfirmModal
  },
  setup() {
    // Token表单数据
    const tokenForm = ref({
      accessToken: '',
      expiryDate: '',
      pageId: '',
      accountName: ''
    });
    
    const authorizedAccounts = ref([]);
    const showUnbindConfirm = ref(false);
    const accountToRemove = ref(null);
    const tokenValidationStatus = ref(null);
    const isVerifyingToken = ref(false);
    
    // 获取当前授权状态
    const fetchStatus = async () => {
      try {
        const response = await socialMediaAPI.getPlatformStatus();
        if (response.facebook) {
          // 如果返回的是单个账号，转换为数组格式
          if (Array.isArray(response.facebook)) {
            authorizedAccounts.value = response.facebook;
          } else {
            authorizedAccounts.value = response.facebook.connected ? [response.facebook] : [];
          }
        }
      } catch (error) {
        console.error('获取授权状态失败:', error);
      }
    };
    
    // 格式化过期日期
    const formatExpiryDate = (dateString) => {
      if (!dateString) return '永不过期';
      const date = new Date(dateString);
      return `有效期至 ${date.toLocaleDateString('zh-CN')}`;
    };
    
    // 验证Token
    const validateToken = async () => {
      if (!tokenForm.value.accessToken.trim()) {
        showError('请输入Token');
        return;
      }
      
      isVerifyingToken.value = true;
      tokenValidationStatus.value = null;
      
      try {
        // 调用API验证Token
        const response = await socialMediaAPI.verifyFBToken({
          token: tokenForm.value.accessToken,
          pageId: tokenForm.value.pageId
        });
        
        if (response.isValid) {
          tokenValidationStatus.value = {
            isValid: true,
            message: 'Token验证成功！\n已成功连接到Facebook账户，Token有效且具有必要权限。'
          };
        } else {
          tokenValidationStatus.value = {
            isValid: false,
            message: 'Token验证失败：' + (response.error || '无效的Token或缺少必要权限')
          };
        }
      } catch (error) {
        tokenValidationStatus.value = {
          isValid: false,
          message: '验证失败：' + error.message
        };
      } finally {
        isVerifyingToken.value = false;
      }
    };
    
    // 保存Token
    const saveToken = async () => {
      try {
        await socialMediaAPI.saveFBSystemToken({
          token: tokenForm.value.accessToken,
          expiryDate: tokenForm.value.expiryDate,
          pageId: tokenForm.value.pageId,
          accountName: tokenForm.value.accountName
        });
        
        showSuccess('系统Token保存成功!');
        
        // 重置表单
        tokenForm.value = {
          accessToken: '',
          expiryDate: '',
          pageId: '',
          accountName: ''
        };
        tokenValidationStatus.value = null;
        
        await fetchStatus();
      } catch (error) {
        showError('Token保存失败: ' + error.message);
      }
    };
    
    // 通过Facebook登录
    const loginWithFacebook = async () => {
      try {
        const response = await socialMediaAPI.getFBAuthUrl();
        // 打开Facebook授权页面
        window.open(response.data.authUrl, '_blank');
        
        // 授权完成后，服务器可能会通过其他方式（如回调）通知前端
        // 这里我们可以设置一个定时器，定期检查授权状态
        const checkInterval = setInterval(async () => {
          await fetchStatus();
          if (authorizedAccounts.value.length > 0) {
            clearInterval(checkInterval);
            showSuccess('Facebook授权成功!');
          }
        }, 3000);
        
        // 5分钟后自动停止检查
        setTimeout(() => {
          clearInterval(checkInterval);
        }, 5 * 60 * 1000);
      } catch (error) {
        showError('获取Facebook授权URL失败: ' + error.message);
      }
    };
    
    // 删除账号
    const removeAccount = (index) => {
      accountToRemove.value = index;
      showUnbindConfirm.value = true;
    };
    
    // 确认删除账号
    const confirmRemoveAccount = async () => {
      try {
        const account = authorizedAccounts.value[accountToRemove.value];
        // 调用API删除特定账号
        await socialMediaAPI.removeFBAccount(account.id || account.token);
        
        // 从本地数组中移除
        authorizedAccounts.value.splice(accountToRemove.value, 1);
        
        showSuccess('账号删除成功!');
        showUnbindConfirm.value = false;
        accountToRemove.value = null;
      } catch (error) {
        showError('删除账号失败: ' + error.message);
      }
    };
    
    // 取消删除
    const cancelRemove = () => {
      showUnbindConfirm.value = false;
      accountToRemove.value = null;
    };
    
    // 解绑Facebook账号
    const unbindFacebook = async () => {
      try {
        await socialMediaAPI.unbindFB();
        showSuccess('Facebook账号解绑成功!');
        await fetchStatus();
        showUnbindConfirm.value = false;
      } catch (error) {
        showError('解绑失败: ' + error.message);
      }
    };
    
    // 刷新状态
    const refreshStatus = async () => {
      await fetchStatus();
      showSuccess('状态已刷新');
    };
    
    // 组件挂载时获取当前状态
    onMounted(() => {
      fetchStatus();
    });
    
    // 取消操作，通过emit通知父组件关闭弹窗
    const emit = defineEmits(['cancel']);
    
    const cancel = () => {
      emit('cancel');
    };

    return {
      tokenInput,
      authorizedAccounts,
      showUnbindConfirm,
      accountToRemove,
      fetchStatus,
      formatExpiryDate,
      saveToken,
      loginWithFacebook,
      removeAccount,
      confirmRemoveAccount,
      cancelRemove,
      refreshStatus,
      cancel
    };
  }
};
</script>