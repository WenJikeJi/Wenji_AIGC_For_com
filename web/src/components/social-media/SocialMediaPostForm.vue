<template>
  <div>
    <!-- 帖子内容表单 -->
    <form @submit.prevent="submitPost" class="space-y-6">
      <!-- 平台选择 -->
      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700 mb-2">发布平台</label>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
          <!-- Facebook -->
          <label 
            class="flex items-center p-4 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors"
            :class="{ 'border-blue-500 bg-blue-50': selectedPlatforms.facebook }"
            :disabled="!platformStatus.facebook?.connected"
          >
            <input 
              type="checkbox" 
              name="platforms" 
              value="facebook" 
              v-model="selectedPlatforms.facebook"
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              :disabled="!platformStatus.facebook?.connected"
            >
            <span class="ml-3 flex items-center">
              <i class="fab fa-facebook text-blue-600 mr-2"></i>
              <span>Facebook</span>
              <span v-if="!platformStatus.facebook?.connected" class="ml-2 text-xs text-red-500">未连接</span>
            </span>
          </label>

          <!-- Instagram -->
          <label 
            class="flex items-center p-4 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors"
            :class="{ 'border-pink-500 bg-pink-50': selectedPlatforms.instagram }"
            :disabled="!platformStatus.instagram?.connected"
          >
            <input 
              type="checkbox" 
              name="platforms" 
              value="instagram" 
              v-model="selectedPlatforms.instagram"
              class="w-4 h-4 text-pink-600 border-gray-300 rounded focus:ring-pink-500"
              :disabled="!platformStatus.instagram?.connected"
            >
            <span class="ml-3 flex items-center">
              <i class="fab fa-instagram text-pink-600 mr-2"></i>
              <span>Instagram</span>
              <span v-if="!platformStatus.instagram?.connected" class="ml-2 text-xs text-red-500">未连接</span>
            </span>
          </label>

          <!-- TikTok -->
          <label 
            class="flex items-center p-4 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors"
            disabled
          >
            <input 
              type="checkbox" 
              name="platforms" 
              value="tiktok" 
              v-model="selectedPlatforms.tiktok"
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              disabled
            >
            <span class="ml-3 flex items-center">
              <i class="fab fa-tiktok text-black mr-2"></i>
              <span>TikTok</span>
              <span class="ml-2 text-xs text-yellow-500">对接中</span>
            </span>
          </label>
        </div>
      </div>

      <!-- 帖子文案 -->
      <div class="mb-6">
        <label for="postContent" class="block text-sm font-medium text-gray-700 mb-2">帖子文案</label>
        <textarea 
          id="postContent" 
          v-model="postContent"
          placeholder="请输入帖子内容..."
          rows="4"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 resize-none"
        ></textarea>
        <div class="text-right text-xs text-gray-500 mt-1">{{ postContent.length }}/2000</div>
      </div>

      <!-- 图片上传 -->
      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700 mb-2">上传图片 (最多9张)</label>
        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
          <!-- 上传按钮 -->
          <div v-if="uploadedImages.length < 9" class="relative h-24 border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center cursor-pointer hover:bg-gray-50 transition-colors">
            <input 
              type="file" 
              ref="fileInput"
              @change="handleImageUpload"
              accept="image/*"
              multiple
              class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            >
            <div class="text-center">
              <i class="fas fa-plus text-gray-400 text-xl"></i>
              <p class="text-xs text-gray-500 mt-1">点击上传</p>
            </div>
          </div>

          <!-- 已上传图片预览 -->
          <div v-for="(image, index) in uploadedImages" :key="index" class="relative h-24">
            <img 
              :src="image" 
              alt="上传预览" 
              class="w-full h-full object-cover rounded-lg"
            >
            <button 
              type="button"
              class="absolute top-1 right-1 w-6 h-6 bg-black bg-opacity-50 rounded-full flex items-center justify-center text-white hover:bg-opacity-70 transition-colors"
              @click="removeImage(index)"
            >
              <i class="fas fa-times text-xs"></i>
            </button>
          </div>
        </div>
        <p class="text-xs text-gray-500 mt-2">支持JPG、PNG格式，单张图片不超过10MB</p>
      </div>

      <!-- 发布设置 -->
      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700 mb-2">发布设置</label>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- 发布时间 -->
          <div>
            <label class="inline-flex items-center mb-2">
              <input 
                type="checkbox" 
                v-model="isScheduled"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              >
              <span class="ml-2 text-sm text-gray-700">定时发布</span>
            </label>
            <input 
              type="datetime-local" 
              v-model="scheduleTime"
              :disabled="!isScheduled"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
          </div>

          <!-- 隐私设置 -->
          <div>
            <label for="privacy" class="block text-sm font-medium text-gray-700 mb-2">隐私设置</label>
            <select 
              id="privacy" 
              v-model="privacySetting"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="public">公开</option>
              <option value="friends">仅好友可见</option>
              <option value="private">私密</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="flex justify-end space-x-4 pt-4 border-t border-gray-200">
        <button 
          type="button"
          class="px-5 py-2 bg-white border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors"
          @click="$emit('close')"
        >
          取消
        </button>
        <button 
          type="submit"
          class="px-5 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
          :disabled="isSubmitting || !hasSelectedPlatform"
        >
          <span v-if="isSubmitting"><i class="fas fa-spinner fa-spin mr-2"></i>发布中...</span>
          <span v-else>{{ isScheduled ? '定时发布' : '立即发布' }}</span>
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue';
import { socialMediaAPI } from '../../utils/api.js';
import { showNotification } from '../../utils/notification.js';

export default {
  name: 'SocialMediaPostForm',
  props: {
    platformStatus: {
      type: Object,
      required: true
    }
  },
  emits: ['postPublished', 'close'],
  setup(props, { emit }) {
    // 响应式数据
    const postContent = ref('');
    const uploadedImages = ref([]);
    const selectedPlatforms = ref({
      facebook: false,
      instagram: false,
      tiktok: false
    });
    const isScheduled = ref(false);
    const scheduleTime = ref('');
    const privacySetting = ref('public');
    const isSubmitting = ref(false);
    const fileInput = ref(null);

    // 计算属性
    const hasSelectedPlatform = computed(() => {
      return Object.values(selectedPlatforms.value).some(platform => platform);
    });

    // 监听Facebook选择状态，自动选择Instagram
    watch(
      () => selectedPlatforms.value.facebook,
      (newValue) => {
        if (newValue && props.platformStatus.instagram?.connected && !selectedPlatforms.value.instagram) {
          showNotification('已自动选择Instagram同步发布', 'info');
          selectedPlatforms.value.instagram = true;
        }
      }
    );

    // 处理图片上传
    const handleImageUpload = (event) => {
      const files = event.target.files;
      if (!files.length) return;

      const remainingSlots = 9 - uploadedImages.value.length;
      const filesToProcess = Math.min(files.length, remainingSlots);

      for (let i = 0; i < filesToProcess; i++) {
        const file = files[i];
        
        // 检查文件大小
        if (file.size > 10 * 1024 * 1024) {
          showNotification('图片大小不能超过10MB', 'error');
          continue;
        }

        // 检查文件类型
        if (!file.type.startsWith('image/')) {
          showNotification('请上传图片文件', 'error');
          continue;
        }

        // 创建预览URL
        const reader = new FileReader();
        reader.onload = (e) => {
          uploadedImages.value.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }

      // 清空input，允许重复选择相同文件
      if (fileInput.value) {
        fileInput.value.value = '';
      }
    };

    // 移除图片
    const removeImage = (index) => {
      uploadedImages.value.splice(index, 1);
    };

    // 提交帖子
    const submitPost = async () => {
      if (!hasSelectedPlatform.value) {
        showNotification('请至少选择一个发布平台', 'error');
        return;
      }

      if (!postContent.value.trim() && uploadedImages.value.length === 0) {
        showNotification('请输入帖子内容或上传图片', 'error');
        return;
      }

      isSubmitting.value = true;

      try {
        // 准备发布数据
        const postData = {
          content: postContent.value,
          images: uploadedImages.value,
          platforms: Object.entries(selectedPlatforms.value)
            .filter(([_, isSelected]) => isSelected)
            .map(([platform]) => platform),
          isScheduled: isScheduled.value,
          scheduleTime: isScheduled.value ? scheduleTime.value : null,
          privacy: privacySetting.value
        };

        // 调用API发布帖子
        await socialMediaAPI.publishPost(postData);

        showNotification('帖子发布成功！', 'success');
        emit('postPublished');
        
        // 重置表单
        resetForm();
      } catch (error) {
        console.error('发布帖子失败:', error);
        showNotification('发布帖子失败，请稍后重试', 'error');
      } finally {
        isSubmitting.value = false;
      }
    };

    // 重置表单
    const resetForm = () => {
      postContent.value = '';
      uploadedImages.value = [];
      selectedPlatforms.value = {
        facebook: false,
        instagram: false,
        tiktok: false
      };
      isScheduled.value = false;
      scheduleTime.value = '';
      privacySetting.value = 'public';
    };

    // 初始化默认时间（当前时间加5分钟）
    const initializeDefaultTime = () => {
      const now = new Date();
      now.setMinutes(now.getMinutes() + 5);
      const formattedTime = now.toISOString().slice(0, 16);
      scheduleTime.value = formattedTime;
    };

    // 初始化
    initializeDefaultTime();

    return {
      postContent,
      uploadedImages,
      selectedPlatforms,
      isScheduled,
      scheduleTime,
      privacySetting,
      isSubmitting,
      fileInput,
      hasSelectedPlatform,
      handleImageUpload,
      removeImage,
      submitPost
    };
  }
};
</script>

<style scoped>
/* 帖子表单特定样式 */

/* 响应式调整 */
@media (max-width: 768px) {
  .grid-cols-2 {
    grid-template-columns: 1fr;
  }
}
</style>