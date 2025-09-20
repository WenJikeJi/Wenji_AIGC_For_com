<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <div class="p-5 border-b border-gray-100">
      <h3 class="font-semibold text-gray-900">Instagram 授权管理</h3>
    </div>
    
    <div class="p-5">
      <!-- 当前授权状态 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">当前授权状态</h4>
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <div class="flex items-center">
            <div class="w-10 h-10 rounded-full bg-pink-100 flex items-center justify-center text-pink-600">
              <i class="fab fa-instagram"></i>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-gray-900">{{ currentStatus.name || '未连接' }}</p>
              <p class="text-xs text-gray-500">
                {{ currentStatus.connected ? 
                  `已授权，${formatExpiryDate(currentStatus.expiryDate)}` : 
                  '等待授权连接' 
                }}
              </p>
              <p v-if="!facebookConnected" class="text-xs text-red-500 mt-1">
                <i class="fas fa-exclamation-circle mr-1"></i>
                需要先完成Facebook账号授权
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
      
      <!-- Instagram授权信息 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">授权说明</h4>
        <div class="p-4 bg-blue-50 rounded-lg text-sm text-blue-700">
          <p class="mb-2">Instagram授权通过Facebook Business Integration完成，步骤如下：</p>
          <ol class="list-decimal pl-5 space-y-1">
            <li>确保您已经完成了Facebook账号的授权</li>
            <li>系统将自动尝试获取您的Instagram Business Account</li>
            <li>如果需要额外权限，请通过Facebook重新授权</li>
          </ol>
        </div>
      </div>
      
      <!-- 授权操作 -->
      <div v-if="facebookConnected" class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">Instagram Pages</h4>
        <div class="space-y-3">
          <div v-if="pages.length > 0" class="space-y-2">
            <div 
              v-for="page in pages" 
              :key="page.id" 
              class="p-4 border border-gray-200 rounded-lg flex justify-between items-center"
            >
              <div class="flex items-center">
                <div v-if="page.profilePictureUrl" class="w-10 h-10 rounded-full overflow-hidden mr-3">
                  <img :src="page.profilePictureUrl" alt="Page profile" class="w-full h-full object-cover">
                </div>
                <div v-else class="w-10 h-10 rounded-full bg-pink-100 flex items-center justify-center text-pink-600 mr-3">
                  <i class="fab fa-instagram"></i>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ page.name }}</p>
                  <p class="text-xs text-gray-500">Page ID: {{ page.id }}</p>
                </div>
              </div>
              <button 
                class="px-3 py-1 bg-pink-600 text-white text-sm rounded-lg hover:bg-pink-700 transition-colors"
                @click="selectPage(page.id)"
                :disabled="currentStatus.connected && currentStatus.id === page.id"
              >
                {{ currentStatus.connected && currentStatus.id === page.id ? '已选择' : '选择' }}
              </button>
            </div>
          </div>
          <div v-else class="p-4 bg-gray-50 rounded-lg text-sm text-gray-500 text-center">
            <p>未找到关联的Instagram Business Pages</p>
            <p class="mt-1">请确保您的Facebook账号已关联Instagram Business Account</p>
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
import { ref, onMounted, computed } from 'vue';
import { socialMediaAPI } from '../../utils/api.js';
import { showSuccess, showError } from '../../utils/notification.js';
import CustomConfirmModal from '../CustomConfirmModal.vue';

export default {
  name: 'InstagramTokenManager',
  components: {
    CustomConfirmModal
  },
  props: {
    facebookStatus: {
      type: Object,
      default: () => ({ connected: false })
    }
  },
  setup(props) {
    const currentStatus = ref({ 
      connected: false,
      name: '',
      id: '',
      expiryDate: null
    });
    const pages = ref([]);
    const showUnbindConfirm = ref(false);
    
    // 计算Facebook是否已连接
    const facebookConnected = computed(() => {
      return props.facebookStatus?.connected || false;
    });
    
    // 获取当前授权状态
    const fetchStatus = async () => {
      try {
        const response = await socialMediaAPI.getPlatformStatus();
        if (response.instagram) {
          currentStatus.value = response.instagram;
        }
        
        // 获取Instagram Pages列表
        if (facebookConnected.value) {
          await fetchInstagramPages();
        }
      } catch (error) {
        console.error('获取授权状态失败:', error);
      }
    };
    
    // 获取Instagram Pages
    const fetchInstagramPages = async () => {
      try {
        const response = await socialMediaAPI.getInstagramPages();
        pages.value = response.pages || [];
      } catch (error) {
        console.error('获取Instagram Pages失败:', error);
        pages.value = [];
      }
    };
    
    // 格式化过期日期
    const formatExpiryDate = (dateString) => {
      if (!dateString) return '永不过期';
      const date = new Date(dateString);
      return `有效期至 ${date.toLocaleDateString('zh-CN')}`;
    };
    
    // 选择Instagram Page
    const selectPage = async (pageId) => {
      try {
        await socialMediaAPI.selectInstagramPage(pageId);
        showSuccess('Instagram账号授权成功!');
        await fetchStatus();
      } catch (error) {
        showError('Instagram账号授权失败: ' + error.message);
      }
    };
    
    // 确认解绑
    const confirmUnbind = () => {
      showUnbindConfirm.value = true;
    };
    
    // 解绑Instagram账号
    const unbindInstagram = async () => {
      try {
        await socialMediaAPI.unbindInstagram();
        showSuccess('Instagram账号解绑成功!');
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
    
    return {
      currentStatus,
      pages,
      showUnbindConfirm,
      facebookConnected,
      fetchStatus,
      formatExpiryDate,
      selectPage,
      confirmUnbind,
      unbindInstagram,
      refreshStatus
    };
  }
};
</script>