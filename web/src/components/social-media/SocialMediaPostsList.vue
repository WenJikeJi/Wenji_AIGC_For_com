<template>
  <div class="bg-gray-800 rounded-xl shadow-xl border border-gray-700 overflow-hidden backdrop-blur-sm min-h-[500px]" style="color: #e5e7eb;">
    <!-- 帖子列表头部 -->
    <div class="p-5 border-b border-gray-700 bg-gradient-to-r from-gray-800 to-gray-750">
      <div class="flex justify-between items-start mb-4">
        <h3 class="font-semibold text-gray-100">已发布帖子</h3>
        <button @click="resetFilters" class="px-3 py-1 bg-gray-600 hover:bg-gray-500 text-gray-200 rounded-lg text-sm transition-all">
          重置筛选
        </button>
      </div>
      
      <!-- 筛选器区域 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <!-- 平台多选 -->
        <div class="relative">
          <label class="block text-sm font-medium text-gray-300 mb-2">平台</label>
          <div class="relative">
            <button @click="showPlatformDropdown = !showPlatformDropdown" 
                    class="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-sm text-gray-200 focus:ring-blue-500 focus:border-blue-500 transition-all flex justify-between items-center">
              <span>{{ getPlatformFilterText() }}</span>
              <i class="fas fa-chevron-down transition-transform" :class="{ 'rotate-180': showPlatformDropdown }"></i>
            </button>
            <div v-if="showPlatformDropdown" class="absolute z-10 w-full mt-1 bg-gray-700 border border-gray-600 rounded-lg shadow-lg">
              <div class="p-2 space-y-2">
                <label class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.platforms" value="facebook" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">Facebook</span>
                </label>
                <label class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.platforms" value="instagram" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">Instagram</span>
                </label>
                <label class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.platforms" value="tiktok" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">TikTok</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- 主页多选 -->
        <div class="relative">
          <label class="block text-sm font-medium text-gray-300 mb-2">主页</label>
          <div class="relative">
            <button @click="showHomepageDropdown = !showHomepageDropdown" 
                    class="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-sm text-gray-200 focus:ring-blue-500 focus:border-blue-500 transition-all flex justify-between items-center">
              <span>{{ getHomepageFilterText() }}</span>
              <i class="fas fa-chevron-down transition-transform" :class="{ 'rotate-180': showHomepageDropdown }"></i>
            </button>
            <div v-if="showHomepageDropdown" class="absolute z-10 w-full mt-1 bg-gray-700 border border-gray-600 rounded-lg shadow-lg max-h-48 overflow-y-auto">
              <div class="p-2 space-y-2">
                <div v-if="availableHomepages.length === 0" class="text-gray-400 text-sm p-2">
                  暂无可用主页
                </div>
                <label v-for="homepage in availableHomepages" :key="homepage.id" 
                       class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.homepages" :value="homepage.id" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">{{ homepage.name }}</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- 状态多选 -->
        <div class="relative">
          <label class="block text-sm font-medium text-gray-300 mb-2">状态</label>
          <div class="relative">
            <button @click="showStatusDropdown = !showStatusDropdown" 
                    class="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-sm text-gray-200 focus:ring-blue-500 focus:border-blue-500 transition-all flex justify-between items-center">
              <span>{{ getStatusFilterText() }}</span>
              <i class="fas fa-chevron-down transition-transform" :class="{ 'rotate-180': showStatusDropdown }"></i>
            </button>
            <div v-if="showStatusDropdown" class="absolute z-10 w-full mt-1 bg-gray-700 border border-gray-600 rounded-lg shadow-lg">
              <div class="p-2 space-y-2">
                <label class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.statuses" value="published" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">已发布</span>
                </label>
                <label class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.statuses" value="scheduled" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">已排期</span>
                </label>
                <label class="flex items-center space-x-2 cursor-pointer hover:bg-gray-600 p-1 rounded">
                  <input type="checkbox" v-model="filter.statuses" value="failed" class="text-blue-500 focus:ring-blue-500">
                  <span class="text-gray-200 text-sm">发布失败</span>
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 帖子列表内容 -->
    <div class="divide-y divide-gray-700">
      <div v-for="post in posts" :key="post.id" class="p-5 hover:bg-gray-750 transition-all duration-200 relative group">
        <!-- 特殊装饰元素 -->
        <div class="absolute left-0 top-0 w-1 h-full bg-gradient-to-b from-blue-500 to-purple-600 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
        
        <!-- 帖子头部 -->
        <div class="flex justify-between items-start mb-4">
          <div class="flex items-center space-x-3">
            <!-- 平台图标 -->
            <div class="flex-shrink-0">
              <div 
                class="w-10 h-10 rounded-full flex items-center justify-center shadow-lg"
                :class="{
                  'bg-gradient-to-br from-blue-500 to-blue-600': post.platform === 'facebook',
                  'bg-gradient-to-br from-pink-500 to-purple-600': post.platform === 'instagram',
                  'bg-gradient-to-br from-gray-800 to-black': post.platform === 'tiktok'
                }"
              >
                <i 
                  class="text-white text-lg"
                  :class="{
                    'fab fa-facebook-f': post.platform === 'facebook',
                    'fab fa-instagram': post.platform === 'instagram',
                    'fab fa-tiktok': post.platform === 'tiktok'
                  }"
                ></i>
              </div>
            </div>
            
            <!-- 帖子信息 -->
            <div>
              <div class="flex items-center space-x-2">
                <span 
                  class="font-medium text-gray-100"
                  :class="{
                    'text-blue-400': post.platform === 'facebook',
                    'text-pink-400': post.platform === 'instagram'
                  }"
                >
                  {{ getPlatformName(post.platform) }}
                </span>
                <span 
                  class="px-2 py-1 text-xs rounded-full font-medium"
                  :class="getStatusClass(post.status)"
                >
                  {{ getStatusName(post.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-400 mt-1">
                {{ formatDate(post.publishTime) }}
              </p>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="flex items-center space-x-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
            <!-- 查看评论按钮 -->
            <button 
              class="p-2 text-gray-400 hover:text-blue-400 hover:bg-blue-900/30 rounded-lg transition-all duration-200 transform hover:scale-110"
              @click="showComments(post)"
              title="查看评论"
            >
              <i class="far fa-comment"></i>
            </button>
            
            <!-- 删除按钮 -->
            <button 
              class="p-2 text-gray-400 hover:text-red-400 hover:bg-red-900/30 rounded-lg transition-all duration-200 transform hover:scale-110"
              @click="confirmDelete(post)"
              title="删除帖子"
            >
              <i class="far fa-trash-alt"></i>
            </button>
          </div>
        </div>

        <!-- 帖子内容 -->
        <div class="mb-4 pl-13">
          <p v-if="post.content" class="text-gray-300 whitespace-pre-line leading-relaxed">{{ post.content }}</p>
        </div>

        <!-- 帖子图片 -->
        <div v-if="post.images && post.images.length > 0" class="mb-4 pl-13">
          <div 
            class="grid gap-2 rounded-lg overflow-hidden"
            :class="{
              'grid-cols-1': post.images.length === 1,
              'grid-cols-2': post.images.length === 2,
              'grid-cols-3': post.images.length >= 3
            }"
          >
            <div 
              v-for="(image, index) in post.images.slice(0, 9)" 
              :key="index" 
              class="relative aspect-square group/image"
            >
              <img 
                :src="image" 
                alt="Post image" 
                class="w-full h-full object-cover rounded-lg transition-transform duration-300 group-hover/image:scale-105"
              >
              <!-- 超过9张图片时显示省略提示 -->
              <div v-if="index === 8 && post.images.length > 9" class="absolute inset-0 bg-black bg-opacity-60 rounded-lg flex items-center justify-center backdrop-blur-sm">
                <span class="text-white font-medium text-lg">+{{ post.images.length - 9 }}</span>
              </div>
              <!-- 图片悬停效果 -->
              <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover/image:opacity-100 transition-opacity duration-300 rounded-lg"></div>
            </div>
          </div>
        </div>

        <!-- 帖子统计数据 -->
        <div class="flex items-center text-sm text-gray-300 pl-13 space-x-6">
          <div class="flex items-center space-x-1 hover:text-blue-400 transition-colors cursor-pointer">
            <i class="far fa-thumbs-up"></i>
            <span>{{ post.stats?.likes || post.likes || 0 }}</span>
          </div>
          <div class="flex items-center space-x-1 hover:text-green-400 transition-colors cursor-pointer">
            <i class="far fa-comment"></i>
            <span>{{ post.stats?.comments || post.comments || 0 }}</span>
          </div>
          <div class="flex items-center space-x-1 hover:text-purple-400 transition-colors cursor-pointer">
            <i class="far fa-share-square"></i>
            <span>{{ post.stats?.shares || post.shares || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="posts.length === 0" class="text-center py-16 px-6">
      <!-- 科技风插画 -->
      <div class="relative mb-8 flex justify-center">
        <!-- 主图标容器 -->
        <div class="relative">
          <!-- 主圆形背景 -->
          <div class="w-32 h-32 bg-gradient-to-br from-purple-100 via-blue-50 to-purple-50 rounded-full flex items-center justify-center mx-auto relative overflow-hidden border border-purple-200">
            <!-- 动画装饰环 -->
            <div class="absolute inset-2 border-2 border-purple-300/60 rounded-full animate-spin" style="animation-duration: 8s;"></div>
            <div class="absolute inset-4 border border-blue-300/50 rounded-full animate-pulse"></div>
            
            <!-- 主图标 -->
            <div class="relative z-10 w-16 h-16 bg-gradient-to-br from-purple-500 to-blue-600 rounded-xl flex items-center justify-center shadow-lg">
              <i class="fas fa-rocket text-white text-2xl"></i>
            </div>
          </div>
          
          <!-- 装饰元素 -->
          <div class="absolute -top-3 -right-3 w-6 h-6 bg-gradient-to-r from-purple-400 to-blue-500 rounded-full animate-bounce shadow-lg">
            <div class="w-full h-full bg-white/60 rounded-full animate-pulse"></div>
          </div>
          <div class="absolute -bottom-2 -left-4 w-4 h-4 bg-gradient-to-r from-blue-400 to-purple-500 rounded-full animate-bounce shadow-lg" style="animation-delay: 0.3s;">
            <div class="w-full h-full bg-white/60 rounded-full animate-pulse" style="animation-delay: 0.3s;"></div>
          </div>
          <div class="absolute top-1/2 -right-6 w-3 h-3 bg-gradient-to-r from-purple-300 to-blue-400 rounded-full animate-bounce" style="animation-delay: 0.6s;"></div>
        </div>
      </div>
      
      <!-- 优化后的文案 -->
      <h3 class="text-2xl font-bold text-gray-800 mb-3">
        还未发布任何社媒帖子～
      </h3>
      <p class="text-gray-600 mb-8 max-w-lg mx-auto leading-relaxed text-lg">
        点击「<span class="text-purple-600 font-medium">创建帖子</span>」开始创作，或「<span class="text-blue-600 font-medium">连接平台</span>」授权账号
      </p>
      
      <!-- 分层级按钮设计 -->
      <div class="flex flex-col sm:flex-row justify-center items-center gap-4">
        <!-- 主按钮：创建帖子 -->
        <button 
          class="group px-8 py-3 bg-gradient-to-r from-purple-600 to-blue-600 text-white rounded-xl font-semibold shadow-lg hover:shadow-xl hover:shadow-purple-500/25 transition-all duration-300 transform hover:scale-105 hover:-translate-y-1 relative overflow-hidden"
          @click="$emit('create-post')"
        >
          <!-- 按钮光效 -->
          <div class="absolute inset-0 bg-gradient-to-r from-white/0 via-white/20 to-white/0 -skew-x-12 -translate-x-full group-hover:translate-x-full transition-transform duration-1000"></div>
          <i class="fas fa-plus mr-2 relative z-10"></i>
          <span class="relative z-10">创建帖子</span>
        </button>
        
        <!-- 次级按钮：连接平台 -->
        <button 
          class="group px-8 py-3 border-2 border-gray-300 text-gray-600 rounded-xl font-medium hover:border-purple-400 hover:text-purple-600 hover:bg-purple-50 transition-all duration-300 transform hover:scale-105 hover:-translate-y-1"
          @click="$emit('connect-platform')"
        >
          <i class="fas fa-link mr-2 group-hover:rotate-12 transition-transform duration-300"></i>
          连接平台
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="pagination.total > 0" class="p-5 border-t border-gray-700 bg-gray-800/50 flex justify-between items-center">
      <div class="text-sm text-gray-400">
        显示 {{ (pagination.currentPage - 1) * pagination.pageSize + 1 }} 至 {{ Math.min(pagination.currentPage * pagination.pageSize, pagination.total) }} 条，共 {{ pagination.total }} 条
      </div>
      <div class="flex space-x-2">
        <button 
          class="px-3 py-1 border border-gray-600 bg-gray-700 text-gray-200 rounded-lg text-sm hover:bg-gray-600 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="pagination.currentPage === 1"
          @click="goToPage(pagination.currentPage - 1)"
        >
          上一页
        </button>
        <button 
          class="px-3 py-1 border border-gray-600 bg-gray-700 text-gray-200 rounded-lg text-sm hover:bg-gray-600 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
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
      platforms: [],
      homepages: [],
      statuses: []
    });
    const showPlatformDropdown = ref(false);
    const showHomepageDropdown = ref(false);
    const showStatusDropdown = ref(false);
    const availableHomepages = ref([]);
    const showCommentsModal = ref(false);
    const selectedPost = ref(null);
    const showDeleteConfirm = ref(false);
    const postToDelete = ref(null);
    const loading = ref(false);
    
    // 获取帖子列表
    const fetchPosts = async () => {
      try {
        loading.value = true;
        
        // 构建查询参数
        const params = {
          page: pagination.value.currentPage,
          pageSize: pagination.value.pageSize
        };
        
        // 添加多选筛选条件
        if (filter.value.platforms.length > 0) {
          params.platforms = filter.value.platforms.join(',');
        }
        
        if (filter.value.homepages.length > 0) {
          params.homepages = filter.value.homepages.join(',');
        }
        
        if (filter.value.statuses.length > 0) {
          params.statuses = filter.value.statuses.join(',');
        }
        
        // 调用API获取帖子列表
        const response = await socialMediaAPI.getPosts(params);
        posts.value = response.data?.list || response.posts || [];
        
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
    
    // 获取可用主页列表
    const fetchHomepages = async () => {
      try {
        const response = await socialMediaAPI.getHomepages();
        availableHomepages.value = response.data || [];
      } catch (error) {
        console.error('获取主页列表失败:', error);
        availableHomepages.value = [];
      }
    };
    
    // 获取平台筛选显示文本
    const getPlatformFilterText = () => {
      if (filter.value.platforms.length === 0) {
        return '全部平台';
      } else if (filter.value.platforms.length === 1) {
        const platformNames = {
          'facebook': 'Facebook',
          'instagram': 'Instagram',
          'tiktok': 'TikTok'
        };
        return platformNames[filter.value.platforms[0]] || filter.value.platforms[0];
      } else {
        return `已选择 ${filter.value.platforms.length} 个平台`;
      }
    };
    
    // 获取主页筛选显示文本
    const getHomepageFilterText = () => {
      if (filter.value.homepages.length === 0) {
        return '全部主页';
      } else if (filter.value.homepages.length === 1) {
        const homepage = availableHomepages.value.find(h => h.id === filter.value.homepages[0]);
        return homepage ? homepage.name : '已选择主页';
      } else {
        return `已选择 ${filter.value.homepages.length} 个主页`;
      }
    };
    
    // 获取状态筛选显示文本
    const getStatusFilterText = () => {
      if (filter.value.statuses.length === 0) {
        return '全部状态';
      } else if (filter.value.statuses.length === 1) {
        const statusNames = {
          'published': '已发布',
          'scheduled': '已排期',
          'failed': '发布失败'
        };
        return statusNames[filter.value.statuses[0]] || filter.value.statuses[0];
      } else {
        return `已选择 ${filter.value.statuses.length} 个状态`;
      }
    };
    
    // 重置筛选器
    const resetFilters = () => {
      filter.value.platforms = [];
      filter.value.homepages = [];
      filter.value.statuses = [];
      showPlatformDropdown.value = false;
      showHomepageDropdown.value = false;
      showStatusDropdown.value = false;
      fetchPosts();
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
    

    
    // 监听过滤条件变化
    watch(filter, () => {
      pagination.value.currentPage = 1;
      fetchPosts();
    }, { deep: true });
    
    // 监听刷新触发器
    watch(() => props.refreshTrigger, () => {
      fetchPosts();
    });
    
    // 当组件挂载时获取帖子列表和主页列表
    onMounted(() => {
      fetchHomepages();
      fetchPosts();
    });
    
    return {
      posts,
      pagination,
      filter,
      showPlatformDropdown,
      showHomepageDropdown,
      showStatusDropdown,
      availableHomepages,
      showCommentsModal,
      selectedPost,
      showDeleteConfirm,
      postToDelete,
      loading,
      fetchPosts,
      fetchHomepages,
      getPlatformFilterText,
      getHomepageFilterText,
      getStatusFilterText,
      resetFilters,
      formatDate,
      getPlatformName,
      getPlatformClass,
      getStatusName,
      getStatusClass,
      goToPage,
      showComments,
      confirmDelete,
      deletePost
    };
  }
};
</script>

<style scoped>
/* 帖子列表特定样式 */
.bg-gray-750 {
  background-color: rgba(55, 65, 81, 0.5);
}

.pl-13 {
  padding-left: 3.25rem;
}

/* 自定义渐变背景 */
.bg-gradient-to-r.from-gray-800.to-gray-750 {
  background: linear-gradient(to right, #1f2937, rgba(55, 65, 81, 0.8));
}

/* 悬停效果增强 */
.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}

/* 图片网格特殊效果 */
.group\/image:hover img {
  transform: scale(1.05);
}

/* 状态标签样式增强 */
.bg-green-100 {
  background-color: rgba(34, 197, 94, 0.1);
  color: #22c55e;
  border: 1px solid rgba(34, 197, 94, 0.2);
}

.bg-blue-100 {
  background-color: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.bg-red-100 {
  background-color: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

/* 动画效果 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.group {
  animation: slideIn 0.3s ease-out;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .p-5 {
    padding: 1rem;
  }
  
  .grid-cols-3 {
    grid-template-columns: 1fr 1fr;
  }
  
  .pl-13 {
    padding-left: 1rem;
  }
  
  .space-x-6 > * + * {
    margin-left: 1rem;
  }
}

/* 加载状态样式 */
.loading {
  opacity: 0.6;
  pointer-events: none;
}

/* 特殊装饰线条动画 */
@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.bg-gradient-to-b.from-blue-500.to-purple-600 {
  background: linear-gradient(45deg, #3b82f6, #8b5cf6, #3b82f6);
  background-size: 200% 200%;
  animation: gradientShift 3s ease infinite;
}
</style>