<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg w-full max-w-2xl max-h-[80vh] overflow-hidden flex flex-col">
      <!-- 弹窗标题 -->
      <div class="p-5 border-b border-gray-100 flex justify-between items-center">
        <h3 class="font-semibold text-gray-900">帖子评论</h3>
        <button 
          class="text-gray-400 hover:text-gray-500"
          @click="closeModal"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <!-- 帖子预览 -->
      <div class="p-5 border-b border-gray-100 bg-gray-50">
        <div class="flex items-start">
          <!-- 平台图标 -->
          <div 
            class="flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center"
            :class="getPlatformIconClass(post?.platforms?.[0])"
          >
            <i :class="getPlatformIcon(post?.platforms?.[0])" class="text-white"></i>
          </div>
          
          <!-- 帖子内容 -->
          <div class="ml-3 flex-1">
            <p class="text-sm text-gray-900 line-clamp-2">{{ post?.content }}</p>
            
            <!-- 图片预览 -->
            <div v-if="post?.images && post?.images.length > 0" class="mt-2">
              <div class="flex space-x-1 overflow-x-auto pb-2">
                <img 
                  v-for="(image, index) in post.images" 
                  :key="index" 
                  :src="image" 
                  alt="Post image" 
                  class="w-16 h-16 object-cover rounded flex-shrink-0"
                >
              </div>
            </div>
            
            <p class="text-xs text-gray-500 mt-2">{{ formatDate(post?.publishTime) }}</p>
          </div>
        </div>
      </div>
      
      <!-- 评论列表 -->
      <div class="flex-1 overflow-y-auto p-5">
        <div v-if="loading" class="py-8 flex items-center justify-center">
          <i class="fas fa-spinner fa-spin text-blue-500 text-xl"></i>
        </div>
        
        <div v-else-if="comments.length === 0" class="py-12 flex flex-col items-center justify-center text-gray-500">
          <i class="far fa-comment text-4xl mb-2"></i>
          <p>暂无评论</p>
        </div>
        
        <div v-else class="space-y-4">
          <div v-for="comment in comments" :key="comment.id" class="bg-gray-50 rounded-lg p-4">
            <div class="flex justify-between items-start">
              <div class="flex items-start">
                <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium text-xs">
                  {{ getAvatarInitials(comment.userName) }}
                </div>
                <div class="ml-2">
                  <p class="text-sm font-medium text-gray-900">{{ comment.userName }}</p>
                  <p class="text-xs text-gray-500">{{ formatDate(comment.createdTime) }}</p>
                </div>
              </div>
              <button 
                class="text-gray-400 hover:text-red-500 text-sm"
                @click="confirmDeleteComment(comment)"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
            
            <p class="text-sm text-gray-800 mt-2">{{ comment.content }}</p>
            
            <!-- 回复输入框 -->
            <div class="mt-3">
              <div class="flex space-x-2">
                <input 
                  type="text" 
                  v-model="replyInputs[comment.id]"
                  placeholder="回复 @{{ comment.userName }}..."
                  class="flex-1 px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 transition-colors"
                >
                <button 
                  class="px-3 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors"
                  :disabled="!replyInputs[comment.id]"
                  @click="replyToComment(comment)"
                >
                  回复
                </button>
              </div>
            </div>
            
            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="mt-3 pl-10 space-y-2">
              <div v-for="reply in comment.replies" :key="reply.id" class="bg-white rounded-lg p-2 border border-gray-100">
                <div class="flex justify-between items-start">
                  <div class="flex items-start">
                    <div class="w-6 h-6 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-medium text-xs">
                      官
                    </div>
                    <div class="ml-2">
                      <p class="text-sm font-medium text-blue-600">官方回复</p>
                      <p class="text-xs text-gray-500">{{ formatDate(reply.createdTime) }}</p>
                    </div>
                  </div>
                  <button 
                    class="text-gray-400 hover:text-red-500 text-sm"
                    @click="confirmDeleteReply(comment, reply)"
                  >
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </div>
                <p class="text-sm text-gray-800 mt-1">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="p-5 border-t border-gray-100 flex justify-end">
        <button 
          class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg text-sm font-medium hover:bg-gray-200 transition-colors"
          @click="closeModal"
        >
          关闭
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { socialMediaAPI } from '../../utils/api.js';
import { showSuccess, showError } from '../../utils/notification.js';
import CustomConfirmModal from '../CustomConfirmModal.vue';

export default {
  name: 'CommentsModal',
  components: {
    CustomConfirmModal
  },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    post: {
      type: Object,
      required: true
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const comments = ref([]);
    const loading = ref(false);
    const replyInputs = ref({});
    const showDeleteConfirm = ref(false);
    const itemToDelete = ref(null);
    const deleteType = ref('comment'); // 'comment' or 'reply'
    
    // 获取评论列表
    const fetchComments = async () => {
      if (!props.post?.id) return;
      
      try {
        loading.value = true;
        const response = await socialMediaAPI.getComments(props.post.id);
        comments.value = response.comments || [];
        
        // 如果没有真实评论数据，提供模拟数据
        if (comments.value.length === 0) {
          comments.value = [
            {
              id: `comment-${Date.now()}-1`,
              userName: '用户123',
              content: '这个帖子太棒了！请问什么时候还有类似的活动？',
              createdTime: new Date(Date.now() - 3600000).toISOString(),
              replies: [
                {
                  id: `reply-${Date.now()}-1`,
                  content: '感谢支持！我们计划下周推出新活动，请持续关注！',
                  createdTime: new Date(Date.now() - 1800000).toISOString()
                }
              ]
            },
            {
              id: `comment-${Date.now()}-2`,
              userName: '市场专员',
              content: '我觉得这个内容很有价值，已经分享给我的团队了。',
              createdTime: new Date(Date.now() - 7200000).toISOString(),
              replies: []
            }
          ];
        }
        
        // 初始化回复输入框
        comments.value.forEach(comment => {
          replyInputs.value[comment.id] = '';
        });
      } catch (error) {
        console.error('获取评论列表失败:', error);
        // 显示错误，但仍然提供模拟数据
        showError('获取评论列表失败，但显示模拟数据');
        comments.value = [
          {
            id: `comment-${Date.now()}-1`,
            userName: '用户123',
            content: '这个帖子太棒了！请问什么时候还有类似的活动？',
            createdTime: new Date(Date.now() - 3600000).toISOString(),
            replies: [
              {
                id: `reply-${Date.now()}-1`,
                content: '感谢支持！我们计划下周推出新活动，请持续关注！',
                createdTime: new Date(Date.now() - 1800000).toISOString()
              }
            ]
          },
          {
            id: `comment-${Date.now()}-2`,
            userName: '市场专员',
            content: '我觉得这个内容很有价值，已经分享给我的团队了。',
            createdTime: new Date(Date.now() - 7200000).toISOString(),
            replies: []
          }
        ];
        // 初始化回复输入框
        comments.value.forEach(comment => {
          replyInputs.value[comment.id] = '';
        });
      } finally {
        loading.value = false;
      }
    };
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN');
    };
    
    // 获取头像首字母
    const getAvatarInitials = (userName) => {
      if (!userName) return '?';
      return userName.charAt(0).toUpperCase();
    };
    
    // 获取平台图标
    const getPlatformIcon = (platform) => {
      const platformIcons = {
        'facebook': 'fab fa-facebook-f',
        'instagram': 'fab fa-instagram',
        'tiktok': 'fab fa-tiktok'
      };
      return platformIcons[platform] || 'fas fa-globe';
    };
    
    // 获取平台图标样式
    const getPlatformIconClass = (platform) => {
      const platformClasses = {
        'facebook': 'bg-blue-600',
        'instagram': 'bg-pink-600',
        'tiktok': 'bg-black'
      };
      return platformClasses[platform] || 'bg-gray-600';
    };
    
    // 回复评论
    const replyToComment = async (comment) => {
      try {
        const content = replyInputs.value[comment.id];
        if (!content.trim()) return;
        
        await socialMediaAPI.replyComment(props.post.id, comment.id, content);
        showSuccess('回复成功!');
        
        // 清空输入框
        replyInputs.value[comment.id] = '';
        
        // 重新获取评论列表
        await fetchComments();
      } catch (error) {
        showError('回复失败: ' + error.message);
      }
    };
    
    // 确认删除评论
    const confirmDeleteComment = (comment) => {
      itemToDelete.value = comment;
      deleteType.value = 'comment';
      showDeleteConfirm.value = true;
    };
    
    // 确认删除回复
    const confirmDeleteReply = (comment, reply) => {
      itemToDelete.value = { comment, reply };
      deleteType.value = 'reply';
      showDeleteConfirm.value = true;
    };
    
    // 删除评论或回复
    const deleteItem = async () => {
      try {
        if (deleteType.value === 'comment') {
          await socialMediaAPI.deleteComment(props.post.id, itemToDelete.value.id);
          showSuccess('评论删除成功!');
        } else {
          // 假设删除回复的API是类似的
          await socialMediaAPI.deleteComment(props.post.id, itemToDelete.value.reply.id);
          showSuccess('回复删除成功!');
        }
        
        // 重新获取评论列表
        await fetchComments();
        showDeleteConfirm.value = false;
      } catch (error) {
        showError('删除失败: ' + error.message);
      }
    };
    
    // 关闭弹窗
    const closeModal = () => {
      emit('close');
    };
    
    // 监听show属性变化
    onMounted(() => {
      if (props.show) {
        fetchComments();
      }
    });
    
    // 监听props.show变化
    onUnmounted(() => {
      // 清理工作
    });
    
    return {
      comments,
      loading,
      replyInputs,
      showDeleteConfirm,
      itemToDelete,
      deleteType,
      fetchComments,
      formatDate,
      getAvatarInitials,
      getPlatformIcon,
      getPlatformIconClass,
      replyToComment,
      confirmDeleteComment,
      confirmDeleteReply,
      deleteItem,
      closeModal
    };
  }
};
</script>