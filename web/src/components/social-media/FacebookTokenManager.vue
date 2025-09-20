<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <div class="p-5 border-b border-gray-100">
      <h3 class="font-semibold text-gray-900">Facebook 授权管理</h3>
    </div>
    
    <div class="p-5">
      <!-- 当前授权状态 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">当前授权状态</h4>
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <div class="flex items-center">
            <div class="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
              <i class="fab fa-facebook"></i>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-gray-900">{{ currentStatus.name || '未连接' }}</p>
              <p class="text-xs text-gray-500">
                {{ currentStatus.connected ? 
                  `已授权，${formatExpiryDate(currentStatus.expiryDate)}` : 
                  '等待授权连接' 
                }}
              </p>
            </div>
          </div>
          
          <span 
            class="px-3 py-1 text-xs font-semibold rounded-full"
            :class="currentStatus.connected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
          >
            {{ currentStatus.connected ? '已连接' : '未连接' }}
          </span>
        </div>
      </div>
      
      <!-- Token输入方式 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">授权方式</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- 手动输入Token -->
          <div class="p-4 border border-gray-200 rounded-lg">
            <div class="flex items-start mb-3">
              <div class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600">
                <i class="fas fa-key"></i>
              </div>
              <h5 class="ml-2 text-sm font-medium text-gray-900">手动输入Token</h5>
            </div>
            <p class="text-xs text-gray-500 mb-3">输入您的Facebook访问Token以授权系统访问您的Facebook账户。</p>
            <div class="space-y-3">
              <input 
                type="text" 
                v-model="tokenInput" 
                placeholder="输入Facebook Token..." 
                class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500 transition-colors"
              >
              <button 
                class="w-full py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
                @click="saveToken"
                :disabled="!tokenInput.trim()"
              >
                保存Token
              </button>
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
        <button 
          class="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors"
          @click="confirmUnbind"
          :disabled="!currentStatus.connected"
        >
          解绑账户
        </button>
      </div>
    </div>
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
    const tokenInput = ref('');
    const currentStatus = ref({
      connected: false,
      name: '',
      expiryDate: null
    });
    const showUnbindConfirm = ref(false);
    
    // 获取当前授权状态
    const fetchStatus = async () => {
      try {
        const response = await socialMediaAPI.getPlatformStatus();
        if (response.facebook) {
          currentStatus.value = response.facebook;
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
    
    // 保存Token
    const saveToken = async () => {
      try {
        await socialMediaAPI.saveFBToken(tokenInput.value);
        showSuccess('Token保存成功!');
        tokenInput.value = '';
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
        window.open(response.authUrl, '_blank');
        
        // 授权完成后，服务器可能会通过其他方式（如回调）通知前端
        // 这里我们可以设置一个定时器，定期检查授权状态
        const checkInterval = setInterval(async () => {
          await fetchStatus();
          if (currentStatus.value.connected) {
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
    
    // 确认解绑
    const confirmUnbind = () => {
      showUnbindConfirm.value = true;
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
      currentStatus,
      showUnbindConfirm,
      fetchStatus,
      formatExpiryDate,
      saveToken,
      loginWithFacebook,
      confirmUnbind,
      unbindFacebook,
      refreshStatus,
      cancel
    };
  }
};
</script>