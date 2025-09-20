<template>
  <div class="notification-container" v-if="notifications.length > 0">
    <div
      v-for="notification in notifications"
      :key="notification.id"
      :class="['notification-item', notification.type]"
      :style="{ animationDelay: `${notification.delay}ms` }"
    >
      <div class="notification-icon">
        <i v-if="notification.type === 'success'" class="fas fa-check-circle"></i>
        <i v-else-if="notification.type === 'error'" class="fas fa-exclamation-circle"></i>
        <i v-else-if="notification.type === 'warning'" class="fas fa-exclamation-triangle"></i>
        <i v-else class="fas fa-info-circle"></i>
      </div>
      <div class="notification-content">
        <div class="notification-title">{{ notification.title }}</div>
        <div class="notification-message">{{ notification.message }}</div>
      </div>
      <button class="notification-close" @click="removeNotification(notification.id)">
        <i class="fas fa-times"></i>
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
      
      this.notifications.push(notification);
      
      // 设置自动关闭定时器
      setTimeout(() => {
        this.removeNotification(notification.id);
      }, notification.duration + notification.delay);
    },
    
    removeNotification(id) {
      const index = this.notifications.findIndex(n => n.id === id);
      if (index !== -1) {
        this.notifications[index].closing = true;
        // 等待动画完成后移除
        setTimeout(() => {
          this.notifications.splice(index, 1);
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
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  border-left: 4px solid;
  max-width: 400px;
  animation: slideIn 0.3s ease-out forwards;
  transition: transform 0.3s ease-out, opacity 0.3s ease-out;
}

.notification-item.closing {
  transform: translateX(100%);
  opacity: 0;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.notification-item.success {
  border-left-color: #10b981;
  background: linear-gradient(135deg, #f0fdf4 0%, #ffffff 100%);
}

.notification-item.error {
  border-left-color: #ef4444;
  background: linear-gradient(135deg, #fef2f2 0%, #ffffff 100%);
}

.notification-item.warning {
  border-left-color: #f59e0b;
  background: linear-gradient(135deg, #fef3c7 0%, #ffffff 100%);
}

.notification-item.info {
  border-left-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff 0%, #ffffff 100%);
}

.notification-icon {
  font-size: 20px;
  margin-top: 1px;
  flex-shrink: 0;
}

.notification-item.success .notification-icon {
  color: #10b981;
}

.notification-item.error .notification-icon {
  color: #ef4444;
}

.notification-item.warning .notification-icon {
  color: #f59e0b;
}

.notification-item.info .notification-icon {
  color: #3b82f6;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
  font-size: 14px;
}

.notification-message {
  color: #4b5563;
  font-size: 13px;
  line-height: 1.4;
}

.notification-close {
  background: none;
  border: none;
  font-size: 16px;
  color: #9ca3af;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.notification-close:hover {
  background: #f3f4f6;
  color: #374151;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .notification-container {
    top: 10px;
    right: 10px;
    left: 10px;
  }
  
  .notification-item {
    max-width: none;
  }
}
</style>