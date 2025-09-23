<template>
  <!-- 顶部浮窗下滑通知 -->
  <div class="notification-container">
    <div
      v-for="notification in notifications"
      :key="notification.id"
      :class="['notification-item', notification.type, { 'notification-closing': notification.closing }]"
      :style="{ animationDelay: notification.delay + 'ms' }"
      :data-notification-id="notification.id"
    >
      <!-- 左侧图标 -->
      <div class="notification-icon">
        <i v-if="notification.type === 'success'" class="fas fa-check-circle"></i>
        <i v-else-if="notification.type === 'error'" class="fas fa-exclamation-circle"></i>
        <i v-else-if="notification.type === 'warning'" class="fas fa-exclamation-triangle"></i>
        <i v-else class="fas fa-info-circle"></i>
      </div>
      
      <!-- 内容区域 -->
      <div class="notification-content">
        <div class="notification-title" v-if="notification.title">{{ notification.title }}</div>
        <div class="notification-message">{{ notification.message }}</div>
      </div>
      
      <!-- 右侧关闭按钮 -->
      <button class="notification-close" @click="removeNotification(notification.id)">
        ×
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Notification',
  data() {
    return {
      notifications: []
    };
  },
  created() {
    // 监听全局通知事件
    window.addEventListener('showNotification', this.handleShowNotification);
  },
  beforeUnmount() {
    window.removeEventListener('showNotification', this.handleShowNotification);
  },
  methods: {
    handleShowNotification(event) {
      const notification = {
        id: Date.now(),
        title: event.detail.title || '',
        message: event.detail.message || '',
        type: event.detail.type || 'info',
        duration: event.detail.duration || 3000,
        delay: this.notifications.length * 100
      };
      
      // 将新通知添加到数组开头，确保最新通知在最上方
      this.notifications.unshift(notification);
      
      // 设置自动关闭定时器
      setTimeout(() => {
        this.removeNotification(notification.id);
      }, notification.duration + notification.delay);
    },
    
    removeNotification(id) {
      const index = this.notifications.findIndex(n => n.id === id);
      if (index !== -1) {
        // 添加关闭动画标记
        this.notifications[index].closing = true;
        
        // 等待动画完成后移除
        setTimeout(() => {
          const currentIndex = this.notifications.findIndex(n => n.id === id);
          if (currentIndex !== -1) {
            this.notifications.splice(currentIndex, 1);
          }
        }, 300);
      }
    }
  }
};
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  pointer-events: none;
  max-width: 800px;
  width: 100%;
}

.notification-item {
  display: flex;
  align-items: center;
  background: rgba(59, 130, 246, 0.25);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 8px;
  box-shadow: 0 8px 25px -5px rgba(59, 130, 246, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  margin: 4px 0;
  padding: 6px 10px;
  pointer-events: auto;
  animation: slideDown 0.4s ease-out forwards;
  border-left: 4px solid #1e40af;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  position: relative;
  min-height: 28px;
}

.notification-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 35px -5px rgba(0, 0, 0, 0.15), 0 15px 15px -5px rgba(0, 0, 0, 0.06);
}

@keyframes slideDown {
  from {
    transform: translateY(-100px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(0);
    opacity: 1;
    max-height: 60px;
  }
  to {
    transform: translateY(-100px);
    opacity: 0;
    max-height: 0;
  }
}

.notification-closing {
  animation: slideUp 0.3s ease-in forwards !important;
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  font-size: 8px;
  margin-right: 8px;
  flex-shrink: 0;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
}

.notification-content {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notification-title {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 0;
  color: #ffffff;
  white-space: nowrap;
}

.notification-message {
  color: #ffffff;
  font-size: 13px;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-close {
  background: transparent;
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 0;
  border-radius: 0;
  transition: all 0.2s ease;
  margin-left: 8px;
  flex-shrink: 0;
  font-size: 16px;
  font-weight: bold;
  width: auto;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 1;
}

.notification-close:hover {
  color: #e0e7ff;
  background: transparent;
  border: none;
}

/* Success notifications */
.notification-item.success {
  background: rgba(34, 197, 94, 0.25);
  border: 1px solid rgba(34, 197, 94, 0.3);
  border-left: 4px solid #16a34a;
  box-shadow: 0 8px 25px -5px rgba(34, 197, 94, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
}

.notification-item.success .notification-icon {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
}

.notification-item.success .notification-title {
  color: #ffffff;
  white-space: nowrap;
}

.notification-item.success .notification-message {
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-item.success .notification-close {
  color: #ffffff;
  background: transparent;
  border: none;
}

.notification-item.success .notification-close:hover {
  color: #bbf7d0;
  background: transparent;
  border: none;
}

/* Error notifications */
.notification-item.error {
  background: rgba(220, 38, 38, 0.25);
  border: 1px solid rgba(220, 38, 38, 0.3);
  border-left: 4px solid #dc2626;
  box-shadow: 0 8px 25px -5px rgba(220, 38, 38, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
}

.notification-item.error .notification-icon {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
}

.notification-item.error .notification-title {
  color: #ffffff;
  white-space: nowrap;
}

.notification-item.error .notification-close {
  color: #ffffff;
  background: transparent;
  border: none;
}

.notification-item.error .notification-message {
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-item.error .notification-close:hover {
  color: #fecaca;
  background: transparent;
  border: none;
}

/* Warning notifications */
.notification-item.warning {
  background: rgba(220, 38, 38, 0.25);
  border: 1px solid rgba(220, 38, 38, 0.3);
  border-left: 4px solid #dc2626;
  box-shadow: 0 8px 25px -5px rgba(220, 38, 38, 0.3), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
}

.notification-item.warning .notification-icon {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
}

.notification-item.warning .notification-title {
  color: #ffffff;
  white-space: nowrap;
}

.notification-item.warning .notification-message {
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-item.warning .notification-close {
  color: #ffffff;
  background: transparent;
  border: none;
}

.notification-item.warning .notification-close:hover {
  color: #fecaca;
  background: transparent;
  border: none;
}

.notification-item.info {
  /* info类型使用默认的蓝色渐变背景，无需重复定义 */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .notification-container {
    top: 10px;
    left: 50%;
    transform: translateX(-50%);
    padding: 0 10px;
    max-width: 90%;
  }
  
  .notification-item {
    padding: 12px 16px;
    margin-bottom: 8px;
  }
  
  .notification-icon {
    width: 20px;
    height: 20px;
    font-size: 14px;
    margin-right: 10px;
  }
  
  .notification-title {
    font-size: 13px;
  }
  
  .notification-message {
    font-size: 13px;
  }
}
</style>