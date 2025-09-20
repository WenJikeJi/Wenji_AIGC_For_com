/**
 * 显示通知提示
 * @param {string} message - 通知消息
 * @param {string} type - 通知类型 (success, error, info, warning)
 * @param {number} duration - 通知显示时长（毫秒）
 */
export function showNotification(message, type = 'info', duration = 3000) {
  // 检查是否已经存在通知容器，如果不存在则创建
  let notificationContainer = document.getElementById('notification-container');
  if (!notificationContainer) {
    notificationContainer = document.createElement('div');
    notificationContainer.id = 'notification-container';
    notificationContainer.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 10px;
    `;
    document.body.appendChild(notificationContainer);
  }

  // 创建通知元素
  const notification = document.createElement('div');
  
  // 设置样式和图标
  let bgColor, textColor, iconClass;
  
  switch (type) {
    case 'success':
      bgColor = '#10b981'; // 绿色
      textColor = '#ffffff';
      iconClass = 'fa-check-circle';
      break;
    case 'error':
      bgColor = '#ef4444'; // 红色
      textColor = '#ffffff';
      iconClass = 'fa-times-circle';
      break;
    case 'warning':
      bgColor = '#f59e0b'; // 黄色
      textColor = '#ffffff';
      iconClass = 'fa-exclamation-triangle';
      break;
    default:
    case 'info':
      bgColor = '#3b82f6'; // 蓝色
      textColor = '#ffffff';
      iconClass = 'fa-info-circle';
      break;
  }

  notification.style.cssText = `
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background-color: ${bgColor};
    color: ${textColor};
    border-radius: 8px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    max-width: 300px;
    opacity: 0;
    transform: translateX(100%);
    transition: opacity 0.3s ease, transform 0.3s ease;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    font-size: 14px;
  `;

  // 创建图标和消息文本
  notification.innerHTML = `
    <i class="fas ${iconClass} mr-3"></i>
    <span>${message}</span>
  `;

  // 添加关闭按钮
  const closeButton = document.createElement('button');
  closeButton.innerHTML = '<i class="fas fa-times"></i>';
  closeButton.style.cssText = `
    background: none;
    border: none;
    color: inherit;
    cursor: pointer;
    margin-left: auto;
    padding: 4px;
    opacity: 0.8;
  `;
  closeButton.addEventListener('mouseenter', () => {
    closeButton.style.opacity = '1';
  });
  closeButton.addEventListener('mouseleave', () => {
    closeButton.style.opacity = '0.8';
  });
  closeButton.addEventListener('click', () => {
    removeNotification(notification);
  });
  
  notification.appendChild(closeButton);

  // 添加到容器
  notificationContainer.appendChild(notification);

  // 触发重排，使动画生效
  void notification.offsetWidth;

  // 显示通知
  notification.style.opacity = '1';
  notification.style.transform = 'translateX(0)';

  // 自动关闭通知
  const timeoutId = setTimeout(() => {
    removeNotification(notification);
  }, duration);

  // 点击通知也可以关闭
  notification.addEventListener('click', () => {
    clearTimeout(timeoutId);
    removeNotification(notification);
  });

  /**
   * 移除通知的动画
   */
  function removeNotification(element) {
    element.style.opacity = '0';
    element.style.transform = 'translateX(100%)';
    
    element.addEventListener('transitionend', () => {
      if (element.parentNode === notificationContainer) {
        notificationContainer.removeChild(element);
      }
      
      // 如果容器为空，移除容器
      if (notificationContainer.children.length === 0) {
        document.body.removeChild(notificationContainer);
      }
    });
  }
}

/**
 * 显示成功通知
 * @param {string} message - 通知消息
 * @param {number} duration - 显示时长
 */
export function showSuccessNotification(message, duration = 3000) {
  showNotification(message, 'success', duration);
}

/**
 * 显示错误通知
 * @param {string} message - 通知消息
 * @param {number} duration - 显示时长
 */
export function showErrorNotification(message, duration = 5000) {
  showNotification(message, 'error', duration);
}

/**
 * 显示警告通知
 * @param {string} message - 通知消息
 * @param {number} duration - 显示时长
 */
export function showWarningNotification(message, duration = 4000) {
  showNotification(message, 'warning', duration);
}

/**
 * 显示信息通知
 * @param {string} message - 通知消息
 * @param {number} duration - 显示时长
 */
export function showInfoNotification(message, duration = 3000) {
  showNotification(message, 'info', duration);
}