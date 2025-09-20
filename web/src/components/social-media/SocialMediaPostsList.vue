<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden" style="color: #333;">
    <!-- 帖子列表头部 -->
    <div class="p-5 border-b border-gray-100 flex justify-between items-center">
      <h3 class="font-semibold text-gray-900">已发布帖子</h3>
      <div class="flex items-center space-x-2">
        <select v-model="filter.platform" class="px-3 py-1 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500">
          <option value="">全部平台</option>
          <option value="facebook">Facebook</option>
          <option value="instagram">Instagram</option>
        </select>
        <select v-model="filter.status" class="px-3 py-1 border border-gray-300 rounded-lg text-sm focus:ring-blue-500 focus:border-blue-500">
          <option value="">全部状态</option>
          <option value="published">已发布</option>
          <option value="scheduled">已排期</option>
          <option value="failed">发布失败</option>
        </select>
      </div>
    </div>

    <!-- 帖子列表内容 -->
    <div class="divide-y divide-gray-100">
      <div v-for="post in posts" :key="post.id" class="p-5 hover:bg-gray-50 transition-colors">
        <!-- 帖子头部 -->
        <div class="flex justify-between items-start mb-4">
          <div class="flex items-center">
            <!-- 平台图标 -->
            <div 
              class="w-8 h-8 rounded-full flex items-center justify-center mr-3"
              :class="{
                'bg-blue-100 text-blue-600': post.platform === 'facebook',
                'bg-pink-100 text-pink-600': post.platform === 'instagram'
              }"
            >
              <i :class="post.platform === 'facebook' ? 'fab fa-facebook-f' : 'fab fa-instagram'"></i>
            </div>
            <!-- 平台名称和发布时间 -->
            <div>
              <div class="flex items-center">
                <span 
                  class="font-medium text-gray-900"
                  :class="{
                    'text-blue-600': post.platform === 'facebook',
                    'text-pink-600': post.platform === 'instagram'
                  }"
                >
                  {{ getPlatformName(post.platform) }}
                </span>
                <!-- 帖子状态标签 -->
                <span 
                  class="ml-2 text-xs px-2 py-0.5 rounded-full"
                  :class="getStatusClass(post.status)"
                >
                  {{ getStatusName(post.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mt-1">
                {{ formatDate(post.publishTime) }}
              </p>
            </div>
          </div>
          <!-- 操作按钮 -->
          <div class="flex space-x-2">
            <!-- 查看评论按钮 -->
            <button 
              class="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
              @click="showComments(post)"
              title="查看评论"
            >
              <i class="far fa-comment"></i>
            </button>
            <!-- 删除按钮 -->
            <button 
              class="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors"
              @click="confirmDelete(post)"
              title="删除帖子"
            >
              <i class="far fa-trash-alt"></i>
            </button>
          </div>
        </div>

        <!-- 帖子内容 -->
        <div class="mb-4">
          <p v-if="post.content" class="text-gray-700 whitespace-pre-line">{{ post.content }}</p>
        </div>

        <!-- 图片网格 -->
        <div v-if="post.images && post.images.length > 0" class="mb-4">
          <div 
            class="grid gap-2"
            :class="{
              'grid-cols-1': post.images.length === 1,
              'grid-cols-2': post.images.length === 2,
              'grid-cols-2 md:grid-cols-3': post.images.length >= 3 && post.images.length <= 4,
              'grid-cols-3': post.images.length >= 5 && post.images.length <= 9
            }"
          >
            <div 
              v-for="(image, index) in post.images.slice(0, 9)" 
              :key="index" 
              class="relative aspect-square"
            >
              <img 
                :src="image" 
                alt="Post image" 
                class="w-full h-full object-cover rounded-lg"
              >
              <!-- 超过9张图片时显示省略提示 -->
              <div v-if="index === 8 && post.images.length > 9" class="absolute inset-0 bg-black bg-opacity-50 rounded-lg flex items-center justify-center">
                <span class="text-white font-medium">+{{ post.images.length - 9 }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 帖子统计数据 -->
        <div class="flex items-center text-sm text-gray-700">
          <div class="flex items-center mr-4">
            <i class="far fa-thumbs-up mr-1"></i>
            <span>{{ post.stats?.likes || post.likes || 0 }}</span>
          </div>
          <div class="flex items-center mr-4">
            <i class="far fa-comment mr-1"></i>
            <span>{{ post.stats?.comments || post.comments || 0 }}</span>
          </div>
          <div class="flex items-center">
            <i class="far fa-share-square mr-1"></i>
            <span>{{ post.stats?.shares || post.shares || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="posts.length === 0" class="p-10 text-center">
      <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
        <i class="far fa-file-image text-gray-400 text-2xl"></i>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-1">暂无帖子</h3>
      <p class="text-gray-500">您还没有发布任何社交媒体帖子</p>
    </div>

    <!-- 分页 -->
    <div v-if="pagination.total > 0" class="p-5 border-t border-gray-100 flex justify-between items-center">
      <div class="text-sm text-gray-500">
        显示 {{ (pagination.currentPage - 1) * pagination.pageSize + 1 }} 至 {{ Math.min(pagination.currentPage * pagination.pageSize, pagination.total) }} 条，共 {{ pagination.total }} 条
      </div>
      <div class="flex space-x-2">
        <button 
          class="px-3 py-1 border border-gray-300 rounded-lg text-sm hover:bg-gray-50 transition-colors"
          :disabled="pagination.currentPage === 1"
          @click="goToPage(pagination.currentPage - 1)"
        >
          上一页
        </button>
        <button 
          class="px-3 py-1 border border-gray-300 rounded-lg text-sm hover:bg-gray-50 transition-colors"
          :disabled="pagination.currentPage === pagination.totalPages"
          @click="goToPage(pagination.currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue';
import { socialMediaAPI } from '../../utils/api.js';
import { showSuccess, showError } from '../../utils/notification.js';
import CommentsModal from './CommentsModal.vue';
import CustomConfirmModal from '../CustomConfirmModal.vue';

export default {
  name: 'SocialMediaPostsList',
  components: {
    CommentsModal,
    CustomConfirmModal
  },
  props: {
    refreshTrigger: {
      type: Number,
      default: 0
    }
  },
  setup(props) {
    const posts = ref([]);
    const pagination = ref({
      currentPage: 1,
      pageSize: 10,
      total: 0,
      totalPages: 1
    });
    const filter = ref({
      platform: '',
      status: ''
    });
    const showCommentsModal = ref(false);
    const selectedPost = ref(null);
    const showDeleteConfirm = ref(false);
    const postToDelete = ref(null);
    const loading = ref(false);
    
    // 获取帖子列表
    const fetchPosts = async () => {
      try {
        loading.value = true;
        let response;
        
        // 根据过滤条件调用不同的API
        if (filter.value.platform === 'facebook' && filter.value.status === 'published') {
          // 获取Facebook已发布的真实帖子
          response = await socialMediaAPI.getFacebookRealPosts();
          posts.value = response.data || [];
        } else if (filter.value.platform === 'instagram' && filter.value.status === 'published') {
          // 获取Instagram已发布的真实帖子
          response = await socialMediaAPI.getInstagramRealPosts();
          posts.value = response.data || [];
        } else {
          // 使用统一的API获取帖子列表（支持待发布等状态）
          const params = {
            page: pagination.value.currentPage,
            pageSize: pagination.value.pageSize,
            platform: filter.value.platform
          };
          
          if (filter.value.status) {
            params.status = filter.value.status;
          }
          
          response = await socialMediaAPI.getPosts(params);
          posts.value = response.data?.list || response.posts || [];
        }
        
        // 确保每个帖子都有评论、点赞和分享数量
        posts.value = posts.value.map(post => ({
          ...post,
          comments: post.stats?.comments || post.comments || 0,
          likes: post.stats?.likes || post.likes || 0,
          shares: post.stats?.shares || post.shares || 0
        }));
        
        pagination.value = {
          ...pagination.value,
          total: response.total || posts.value.length,
          totalPages: Math.ceil((response.total || posts.value.length) / pagination.value.pageSize)
        };
      } catch (error) {
        showError('获取帖子列表失败: ' + error.message);
        // 不使用模拟数据，显示真实的空状态
        posts.value = [];
        pagination.value = {
          ...pagination.value,
          total: 0,
          totalPages: 1
        };
      } finally {
        loading.value = false;
      }
    };
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      const now = new Date();
      const diffTime = Math.abs(now - date);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays === 0) {
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      } else if (diffDays === 1) {
        return `昨天 ${date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`;
      } else if (diffDays < 7) {
        return `${diffDays}天前`;
      } else {
        return date.toLocaleDateString('zh-CN');
      }
    };
    
    // 获取平台显示名称
    const getPlatformName = (platform) => {
      const platformNames = {
        'facebook': 'Facebook',
        'instagram': 'Instagram',
        'tiktok': 'TikTok'
      };
      return platformNames[platform] || platform;
    };
    
    // 获取平台样式类
    const getPlatformClass = (platform) => {
      const platformClasses = {
        'facebook': 'bg-blue-100 text-blue-800',
        'instagram': 'bg-pink-100 text-pink-800',
        'tiktok': 'bg-black text-white'
      };
      return platformClasses[platform] || 'bg-gray-100 text-gray-800';
    };
    
    // 获取状态显示名称
    const getStatusName = (status) => {
      const statusNames = {
        'published': '已发布',
        'scheduled': '待发布',
        'failed': '发布失败'
      };
      return statusNames[status] || status;
    };
    
    // 获取状态样式类
    const getStatusClass = (status) => {
      const statusClasses = {
        'published': 'bg-green-100 text-green-800',
        'scheduled': 'bg-blue-100 text-blue-800',
        'failed': 'bg-red-100 text-red-800'
      };
      return statusClasses[status] || 'bg-gray-100 text-gray-800';
    };
    
    // 切换页码
    const goToPage = (page) => {
      if (page >= 1 && page <= pagination.value.totalPages) {
        pagination.value.currentPage = page;
        fetchPosts();
      }
    };
    
    // 显示评论弹窗
    const showComments = (post) => {
      selectedPost.value = post;
      showCommentsModal.value = true;
    };
    
    // 确认删除帖子
    const confirmDelete = (post) => {
      postToDelete.value = post;
      showDeleteConfirm.value = true;
    };
    
    // 删除帖子
    const deletePost = async () => {
      try {
        await socialMediaAPI.deletePost(postToDelete.value.id);
        showSuccess('帖子删除成功!');
        fetchPosts();
        showDeleteConfirm.value = false;
      } catch (error) {
        showError('帖子删除失败: ' + error.message);
      }
    };
    
    // 重置过滤条件
    const resetFilters = () => {
      filter.value.platform = '';
      filter.value.status = '';
      fetchPosts();
    };
    
    // 监听过滤条件变化
    watch(filter, () => {
      pagination.value.currentPage = 1;
      fetchPosts();
    }, { deep: true });
    
    // 监听刷新触发器
    watch(() => props.refreshTrigger, () => {
      fetchPosts();
    });
    
    // 当组件挂载时获取帖子列表
    onMounted(() => {
      fetchPosts();
    });
    
    return {
      posts,
      pagination,
      filter,
      showCommentsModal,
      selectedPost,
      showDeleteConfirm,
      postToDelete,
      loading,
      fetchPosts,
      formatDate,
      getPlatformName,
      getPlatformClass,
      getStatusName,
      getStatusClass,
      goToPage,
      showComments,
      confirmDelete,
      deletePost,
      resetFilters
    };
  }
};
</script>

<style scoped>
/* 帖子列表特定样式 */

/* 响应式调整 */
@media (max-width: 640px) {
  .p-5 {
    padding: 1rem;
  }
  
  .grid-cols-3 {
    grid-template-columns: 1fr 1fr;
  }
}

/* 加载状态样式 */
.loading {
  opacity: 0.6;
  pointer-events: none;
}
</style>