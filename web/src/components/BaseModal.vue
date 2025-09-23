<template>
  <div 
    v-if="show" 
    class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fade-in"
    @click.self="handleBackdropClick"
    :class="{ 'animate-fade-out': isClosing }"
  >
    <div 
      class="bg-gradient-to-br from-slate-800 to-slate-900 rounded-xl shadow-2xl border border-slate-600/50 overflow-hidden transform transition-all duration-300 animate-modal-enter"
      :class="[
        sizeClass,
        { 'animate-modal-exit': isClosing },
        customClass
      ]"
      :style="customStyle"
    >
      <!-- 弹窗头部 -->
      <div 
        v-if="showHeader"
        class="bg-gradient-to-r from-slate-700/80 to-slate-800/80 px-6 py-4 border-b border-slate-600/50 flex justify-between items-center backdrop-blur-sm"
      >
        <div class="flex items-center">
          <!-- 图标 -->
          <div 
            v-if="icon"
            class="w-8 h-8 rounded-lg flex items-center justify-center mr-3"
            :class="iconClass"
          >
            <i :class="icon" class="text-lg"></i>
          </div>
          
          <!-- 标题 -->
          <h3 class="text-lg font-semibold text-white">{{ title }}</h3>
          
          <!-- 状态标签 -->
          <span 
            v-if="status"
            class="ml-3 px-2 py-1 rounded-full text-xs font-medium"
            :class="statusClass"
          >
            {{ status }}
          </span>
        </div>
        
        <!-- 头部操作按钮 -->
        <div class="flex items-center space-x-2">
          <slot name="header-actions"></slot>
          
          <!-- 关闭按钮 -->
          <button 
            v-if="showCloseButton"
            @click="handleClose"
            class="w-8 h-8 rounded-lg bg-slate-600/50 hover:bg-slate-500/50 text-slate-300 hover:text-white transition-all duration-200 flex items-center justify-center group"
            :disabled="isClosing"
          >
            <i class="fas fa-times text-sm group-hover:scale-110 transition-transform"></i>
          </button>
        </div>
      </div>
      
      <!-- 弹窗内容 -->
      <div 
        class="flex-1 overflow-hidden"
        :class="contentClass"
      >
        <slot></slot>
      </div>
      
      <!-- 弹窗底部 -->
      <div 
        v-if="showFooter || $slots.footer"
        class="bg-slate-800/50 px-6 py-4 border-t border-slate-600/50 flex justify-end space-x-3 backdrop-blur-sm"
      >
        <slot name="footer">
          <button 
            v-if="showCancelButton"
            @click="handleCancel"
            class="px-4 py-2 border border-slate-500 rounded-lg text-slate-300 hover:bg-slate-700/50 hover:text-white transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-slate-400 focus:ring-offset-2 focus:ring-offset-slate-800"
            :disabled="isClosing || loading"
          >
            {{ cancelText }}
          </button>
          
          <button 
            v-if="showConfirmButton"
            @click="handleConfirm"
            class="px-4 py-2 rounded-lg text-white font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-800 flex items-center"
            :class="confirmButtonClass"
            :disabled="isClosing || loading || confirmDisabled"
          >
            <i v-if="loading" class="fas fa-spinner fa-spin mr-2"></i>
            <i v-else-if="confirmIcon" :class="confirmIcon" class="mr-2"></i>
            {{ confirmText }}
          </button>
        </slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BaseModal',
  props: {
    // 基础属性
    show: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: 'md',
      validator: value => ['xs', 'sm', 'md', 'lg', 'xl', '2xl', 'full'].includes(value)
    },
    
    // 头部配置
    showHeader: {
      type: Boolean,
      default: true
    },
    icon: {
      type: String,
      default: ''
    },
    iconType: {
      type: String,
      default: 'info',
      validator: value => ['info', 'success', 'warning', 'error', 'primary'].includes(value)
    },
    status: {
      type: String,
      default: ''
    },
    statusType: {
      type: String,
      default: 'info',
      validator: value => ['info', 'success', 'warning', 'error', 'primary'].includes(value)
    },
    showCloseButton: {
      type: Boolean,
      default: true
    },
    
    // 内容配置
    contentClass: {
      type: String,
      default: 'p-6'
    },
    
    // 底部配置
    showFooter: {
      type: Boolean,
      default: false
    },
    showCancelButton: {
      type: Boolean,
      default: false
    },
    showConfirmButton: {
      type: Boolean,
      default: false
    },
    cancelText: {
      type: String,
      default: '取消'
    },
    confirmText: {
      type: String,
      default: '确认'
    },
    confirmType: {
      type: String,
      default: 'primary',
      validator: value => ['primary', 'success', 'warning', 'error'].includes(value)
    },
    confirmIcon: {
      type: String,
      default: ''
    },
    confirmDisabled: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    },
    
    // 行为配置
    closeOnBackdrop: {
      type: Boolean,
      default: true
    },
    closeOnEscape: {
      type: Boolean,
      default: true
    },
    
    // 自定义样式
    customClass: {
      type: String,
      default: ''
    },
    customStyle: {
      type: Object,
      default: () => ({})
    }
  },
  
  data() {
    return {
      isClosing: false
    }
  },
  
  computed: {
    sizeClass() {
      const sizeMap = {
        xs: 'w-full max-w-xs',
        sm: 'w-full max-w-sm',
        md: 'w-full max-w-md',
        lg: 'w-full max-w-lg',
        xl: 'w-full max-w-xl',
        '2xl': 'w-full max-w-2xl',
        full: 'w-full max-w-6xl'
      }
      return `${sizeMap[this.size]} max-h-[90vh] flex flex-col`
    },
    
    iconClass() {
      const typeMap = {
        info: 'bg-blue-500/20 text-blue-400',
        success: 'bg-green-500/20 text-green-400',
        warning: 'bg-yellow-500/20 text-yellow-400',
        error: 'bg-red-500/20 text-red-400',
        primary: 'bg-purple-500/20 text-purple-400'
      }
      return typeMap[this.iconType] || typeMap.info
    },
    
    statusClass() {
      const typeMap = {
        info: 'bg-blue-500/20 text-blue-300 border border-blue-500/30',
        success: 'bg-green-500/20 text-green-300 border border-green-500/30',
        warning: 'bg-yellow-500/20 text-yellow-300 border border-yellow-500/30',
        error: 'bg-red-500/20 text-red-300 border border-red-500/30',
        primary: 'bg-purple-500/20 text-purple-300 border border-purple-500/30'
      }
      return typeMap[this.statusType] || typeMap.info
    },
    
    confirmButtonClass() {
      const typeMap = {
        primary: 'bg-blue-600 hover:bg-blue-700 focus:ring-blue-500',
        success: 'bg-green-600 hover:bg-green-700 focus:ring-green-500',
        warning: 'bg-yellow-600 hover:bg-yellow-700 focus:ring-yellow-500',
        error: 'bg-red-600 hover:bg-red-700 focus:ring-red-500'
      }
      const baseClass = typeMap[this.confirmType] || typeMap.primary
      const disabledClass = this.confirmDisabled || this.loading ? 'opacity-50 cursor-not-allowed' : ''
      return `${baseClass} ${disabledClass}`
    }
  },
  
  watch: {
    show(newVal) {
      if (newVal) {
        this.isClosing = false
        this.$nextTick(() => {
          if (this.closeOnEscape) {
            document.addEventListener('keydown', this.handleEscapeKey)
          }
        })
      } else {
        document.removeEventListener('keydown', this.handleEscapeKey)
      }
    }
  },
  
  beforeUnmount() {
    document.removeEventListener('keydown', this.handleEscapeKey)
  },
  
  methods: {
    handleBackdropClick() {
      if (this.closeOnBackdrop && !this.loading) {
        this.handleClose()
      }
    },
    
    handleEscapeKey(event) {
      if (event.key === 'Escape' && !this.loading) {
        this.handleClose()
      }
    },
    
    handleClose() {
      if (this.loading) return
      
      this.isClosing = true
      setTimeout(() => {
        this.$emit('close')
        this.isClosing = false
      }, 200)
    },
    
    handleCancel() {
      if (this.loading) return
      this.$emit('cancel')
      this.handleClose()
    },
    
    handleConfirm() {
      if (this.loading || this.confirmDisabled) return
      this.$emit('confirm')
    }
  }
}
</script>

<style scoped>
@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fade-out {
  from { opacity: 1; }
  to { opacity: 0; }
}

@keyframes modal-enter {
  from { 
    opacity: 0;
    transform: scale(0.9) translateY(-30px);
  }
  to { 
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes modal-exit {
  from { 
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to { 
    opacity: 0;
    transform: scale(0.9) translateY(-30px);
  }
}

@keyframes backdrop-blur {
  from { 
    backdrop-filter: blur(0px);
    background-color: rgba(0, 0, 0, 0);
  }
  to { 
    backdrop-filter: blur(8px);
    background-color: rgba(0, 0, 0, 0.5);
  }
}

@keyframes glow {
  0%, 100% { box-shadow: 0 0 20px rgba(139, 92, 246, 0.3); }
  50% { box-shadow: 0 0 30px rgba(139, 92, 246, 0.5); }
}

.animate-fade-in {
  animation: fade-in 0.3s ease-out;
}

.animate-fade-out {
  animation: fade-out 0.2s ease-in;
}

.animate-modal-enter {
  animation: modal-enter 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.animate-modal-exit {
  animation: modal-exit 0.3s ease-in;
}

.animate-backdrop-blur {
  animation: backdrop-blur 0.3s ease-out;
}

/* 现代化模态框样式增强 */
.modal-container {
  backdrop-filter: blur(12px);
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.6));
}

.modal-content {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.9));
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.modal-content:hover {
  box-shadow: 
    0 32px 64px -12px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
}

/* 按钮现代化样式 */
.modal-button {
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.modal-button:hover::before {
  left: 100%;
}

.modal-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.modal-button:active {
  transform: translateY(0);
}

/* 关闭按钮增强 */
.close-button {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg) scale(1.1);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

/* 状态指示器增强 */
.status-indicator {
  position: relative;
  overflow: hidden;
}

.status-indicator::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  border-radius: inherit;
  z-index: -1;
}

/* 加载状态动画 */
@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

/* 响应式优化 */
@media (max-width: 640px) {
  .modal-content {
    margin: 1rem;
    max-height: calc(100vh - 2rem);
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .modal-content {
    background: linear-gradient(135deg, rgba(31, 41, 55, 0.95), rgba(17, 24, 39, 0.9));
    border: 1px solid rgba(75, 85, 99, 0.3);
  }
  
  .close-button {
    background: rgba(75, 85, 99, 0.2);
    border: 1px solid rgba(75, 85, 99, 0.3);
  }
  
  .close-button:hover {
    background: rgba(75, 85, 99, 0.3);
  }
}

/* 高对比度模式支持 */
@media (prefers-contrast: high) {
  .modal-content {
    border: 2px solid #000;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.5);
  }
}

/* 减少动画模式支持 */
@media (prefers-reduced-motion: reduce) {
  .animate-modal-enter,
  .animate-modal-exit,
  .modal-button,
  .close-button {
    animation: none;
    transition: none;
  }
}
</style>