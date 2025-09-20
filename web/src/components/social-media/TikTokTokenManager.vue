<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
    <div class="p-5 border-b border-gray-100">
      <h3 class="font-semibold text-gray-900">TikTok 授权管理</h3>
    </div>
    
    <div class="p-5">
      <!-- 当前授权状态 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">当前授权状态</h4>
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <div class="flex items-center">
            <div class="w-10 h-10 rounded-full bg-black flex items-center justify-center text-white">
              <i class="fab fa-tiktok"></i>
            </div>
            <div class="ml-3">
              <p class="text-sm font-medium text-gray-900">TikTok账号</p>
              <p class="text-xs text-gray-500">功能开发中，敬请期待</p>
            </div>
          </div>
          
          <span class="px-3 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800">
            对接中
          </span>
        </div>
      </div>
      
      <!-- TikTok授权信息 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">开发进度</h4>
        <div class="p-4 bg-blue-50 rounded-lg text-sm text-blue-700">
          <p class="mb-2">TikTok功能正在开发中，以下是当前进度：</p>
          <ul class="list-disc pl-5 space-y-1">
            <li>API接口设计 - 90%</li>
            <li>授权机制开发 - 50%</li>
            <li>内容发布功能 - 30%</li>
            <li>数据统计集成 - 20%</li>
          </ul>
          <p class="mt-3 font-medium">预计上线时间：2023年8月</p>
        </div>
      </div>
      
      <!-- 通知设置 -->
      <div class="mb-6">
        <h4 class="text-sm font-medium text-gray-700 mb-3">上线通知</h4>
        <div class="p-4 border border-gray-200 rounded-lg">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-900">TikTok功能上线时通知我</p>
              <p class="text-xs text-gray-500">开启后，我们会在功能上线时通过站内信通知您</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" :checked="notifyWhenAvailable" @change="toggleNotify" class="sr-only peer">
              <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
            </label>
          </div>
        </div>
      </div>
      
      <!-- 联系支持 -->
      <div class="pt-4 border-t border-gray-100">
        <p class="text-sm text-gray-500 mb-3">如有TikTok相关需求或问题，可联系客服获取帮助</p>
        <button class="px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors w-full">
          联系客服
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { socialMediaAPI } from '../../utils/api.js';
import { showSuccess, showError } from '../../utils/notification.js';

export default {
  name: 'TikTokTokenManager',
  setup() {
    const notifyWhenAvailable = ref(false);
    
    // 获取通知设置
    const fetchNotificationSetting = async () => {
      try {
        const response = await socialMediaAPI.getTikTokNotifySetting();
        notifyWhenAvailable.value = response.enabled || false;
      } catch (error) {
        console.error('获取通知设置失败:', error);
        notifyWhenAvailable.value = false;
      }
    };
    
    // 切换通知设置
    const toggleNotify = async () => {
      try {
        await socialMediaAPI.setTikTokNotifySetting({
          enabled: notifyWhenAvailable.value
        });
        showSuccess(notifyWhenAvailable.value ? '通知设置已开启' : '通知设置已关闭');
      } catch (error) {
        showError('设置失败: ' + error.message);
        // 恢复之前的状态
        notifyWhenAvailable.value = !notifyWhenAvailable.value;
      }
    };
    
    // 组件挂载时获取设置
    onMounted(() => {
      fetchNotificationSetting();
    });
    
    return {
      notifyWhenAvailable,
      toggleNotify
    };
  }
};
</script>