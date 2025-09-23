/**
 * 显示全局通知
 * @param {Object} options - 通知配置
 * @param {string} options.title - 通知标题
 * @param {string} options.message - 通知消息
 * @param {string} options.type - 通知类型：'success', 'error', 'warning', 'info'
 * @param {number} options.duration - 通知持续时间（毫秒）
 */
export function showNotification(options) {
  const defaultOptions = {
    title: '',
    message: '',
    type: 'info',
    duration: 3000
  };
  
  const mergedOptions = { ...defaultOptions, ...options };
  
  // 触发全局事件
  window.dispatchEvent(new CustomEvent('showNotification', {
    detail: mergedOptions
  }));
}

/**
 * 显示成功通知
 * @param {string} message - 通知消息
 * @param {string} title - 可选的通知标题
 * @param {number} duration - 可选的持续时间
 */
export function showSuccess(message, title = 'Success', duration = 3000) {
  showNotification({
    title,
    message,
    type: 'success',
    duration
  });
}

/**
 * 显示错误通知
 * @param {string} message - 通知消息
 * @param {string} title - 可选的通知标题
 * @param {number} duration - 可选的持续时间
 */
export function showError(message, title = 'Error', duration = 5000) {
  showNotification({
    title,
    message,
    type: 'error',
    duration
  });
}

/**
 * 显示警告通知
 * @param {string} message - 通知消息
 * @param {string} title - 可选的通知标题
 * @param {number} duration - 可选的持续时间
 */
export function showWarning(message, title = 'Warning', duration = 4000) {
  showNotification({
    title,
    message,
    type: 'warning',
    duration
  });
}

/**
 * 显示信息通知
 * @param {string} message - 通知消息
 * @param {string} title - 可选的通知标题
 * @param {number} duration - 可选的持续时间
 */
export function showInfo(message, title = 'Info', duration = 3000) {
  showNotification({
    title,
    message,
    type: 'info',
    duration
  });
}