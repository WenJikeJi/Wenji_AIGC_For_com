// 用户认证服务

const AUTH_KEY = 'user_auth';
const SESSION_TIMEOUT = 3 * 60 * 60 * 1000; // 3小时，单位毫秒

let sessionTimeoutId = null;

// 保存用户信息、token和登录时间
export const saveAuthInfo = (userInfo, token = null) => {
  const authInfo = {
    user: userInfo,
    token: token,
    loginTime: Date.now()
  };
  localStorage.setItem(AUTH_KEY, JSON.stringify(authInfo));
  startSessionTimeout();
};

// 获取认证token
export const getAuthToken = () => {
  const authInfo = getAuthInfo();
  return authInfo ? authInfo.token : null;
};

// 获取认证信息
export const getAuthInfo = () => {
  const authStr = localStorage.getItem(AUTH_KEY);
  if (!authStr) return null;
  
  try {
    const authInfo = JSON.parse(authStr);
    // 检查是否已过期
    if (Date.now() - authInfo.loginTime > SESSION_TIMEOUT) {
      logout();
      return null;
    }
    // 重新设置超时计时器
    startSessionTimeout();
    return authInfo;
  } catch (error) {
    console.error('解析认证信息失败:', error);
    return null;
  }
};

// 检查是否已登录
export const isLoggedIn = () => {
  return !!getAuthInfo();
};

// 检查登录状态（为了兼容性添加的导出）
export const checkLoginStatus = isLoggedIn;

// 获取当前登录用户信息
export const getCurrentUser = () => {
  const authInfo = getAuthInfo();
  return authInfo ? authInfo.user : null;
};

// 登出
export const logout = () => {
  localStorage.removeItem(AUTH_KEY);
  clearSessionTimeout();
  // 跳转到登录页面
  window.location.hash = '#/login';
};

// 启动会话超时计时器
const startSessionTimeout = () => {
  clearSessionTimeout();
  sessionTimeoutId = setTimeout(() => {
    // 显示登出提示
    if (confirm('您的会话已超过3小时无活动，将自动登出。')) {
      logout();
      // 刷新页面或跳转到登录页
      window.location.reload();
    }
  }, SESSION_TIMEOUT);
};

// 清除会话超时计时器
const clearSessionTimeout = () => {
  if (sessionTimeoutId) {
    clearTimeout(sessionTimeoutId);
    sessionTimeoutId = null;
  }
};

// 重置会话超时（用户活动时调用）
export const resetSessionTimeout = () => {
  if (isLoggedIn()) {
    startSessionTimeout();
  }
};

// 初始化会话管理
export const initSessionManager = () => {
  // 监听用户活动事件，重置会话超时
  const resetTimeoutEvents = ['mousemove', 'keydown', 'scroll', 'click'];
  resetTimeoutEvents.forEach(event => {
    window.addEventListener(event, resetSessionTimeout);
  });
  
  // 检查初始登录状态
  return isLoggedIn();
};