<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click.self="close">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md overflow-hidden transform transition-all">
      <!-- 弹窗头部 -->
      <div class="bg-blue-50 px-6 py-4 border-b border-gray-200">
        <h3 class="text-lg font-medium text-gray-900">{{ title }}</h3>
      </div>
      
      <!-- 弹窗内容 -->
      <div class="px-6 py-5">
        <p class="text-gray-700">{{ message }}</p>
      </div>
      
      <!-- 弹窗底部按钮 -->
      <div class="px-6 py-3 bg-gray-50 border-t border-gray-200 flex justify-end space-x-3">
        <button 
          @click="cancel"
          class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          取消
        </button>
        <button 
          @click="confirm"
          class="px-4 py-2 bg-blue-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          确认
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CustomConfirmModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '确认操作'
    },
    message: {
      type: String,
      default: '您确定要执行此操作吗？'
    }
  },
  methods: {
    confirm() {
      this.$emit('confirm');
    },
    cancel() {
      this.$emit('cancel');
    },
    close() {
      this.$emit('cancel');
    }
  }
};
</script>

<style scoped>
/* 添加动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

div[class*="fixed inset-0"] {
  animation: fadeIn 0.2s ease-out;
}
</style>