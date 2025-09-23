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
      
      <!-- Token输入区域 -->
      <div class="mb-6">
        <div class="p-4 border border-gray-200 rounded-lg">
          <h5 class="text-sm font-medium text-gray-900 mb-3">输入Facebook Token</h5>
          
          <div class="space-y-3">
            <!-- Access Token -->
            <div>
              <label class="block text-xs font-medium text-gray-700 mb-1">Access Token <span class="text-red-500">*</span></label>
              <input 
                type="text" 
                v-model="tokenInput" 
                placeholder="输入Facebook Token..." 
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500 transition-colors"
              >
            </div>
            
            <!-- Token验证状态 -->
            <div v-if="tokenValidationStatus" :class="tokenValidationStatus.isValid ? 'bg-green-50 text-green-800 border border-green-200' : 'bg-red-50 text-red-800 border border-red-200'" class="p-3 rounded-lg text-sm">
              {{ tokenValidationStatus.message }}
            </div>
            
            <!-- 操作按钮 -->
            <button 
              class="w-full py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
              @click="saveToken"
              :disabled="!tokenInput.trim() || isVerifyingToken"
            >
              {{ isVerifyingToken ? '验证并保存中...' : '验证并保存Token' }}
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
    // Token输入数据
    const tokenInput = ref('');
    
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
    
    // 验证并保存Token
    const saveToken = async () => {
      if (!tokenInput.value.trim()) {
        showError('请输入Token');
        return;
      }
      
      isVerifyingToken.value = true;
      tokenValidationStatus.value = null;
      
      try {
        // 调用API验证Token
        const verifyResponse = await socialMediaAPI.verifyFBToken({
          token: tokenInput.value,
          pageId: null
        });
        
        if (verifyResponse.isValid) {
          // 验证成功后保存Token
          await socialMediaAPI.saveFBSystemToken({
            token: tokenInput.value,
            expiryDate: '',
            pageId: '',
            accountName: ''
          });
          
          showSuccess('Token保存成功!');
          
          // 重置输入
          tokenInput.value = '';
          
          // 刷新授权状态
          await fetchStatus();
        } else {
          tokenValidationStatus.value = {
            isValid: false,
            message: 'Token验证失败：' + (verifyResponse.error || '无效的Token或缺少必要权限')
          };
        }
      } catch (error) {
        tokenValidationStatus.value = {
          isValid: false,
          message: '操作失败：' + error.message
        };
      } finally {
        isVerifyingToken.value = false;
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
      removeAccount,
      confirmRemoveAccount,
      cancelRemove,
      refreshStatus,
      cancel
    };
  }
};
</script>