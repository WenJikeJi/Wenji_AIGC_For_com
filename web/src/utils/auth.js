// 用户认证服务

const AUTH_KEY = 'user_auth';
const SESSION_TIMEOUT = 3 * 60 * 60 * 1000; // 3小时，单位毫秒
const MAX_LOGIN_ATTEMPTS = 5; // 最大登录尝试次数
const LOGIN_ATTEMPT_TIMEOUT = 15 * 60 * 1000; // 15分钟锁定时间

let sessionTimeoutId = null;

// 检查登录尝试次数
export const checkLoginAttempts = (email) => {
  const attemptKey = `login_attempts_${email}`;
  const attempts = JSON.parse(localStorage.getItem(attemptKey) || '{"count": 0, "lastAttempt": 0}');
  
  // 如果超过锁定时间，重置尝试次数
  if (Date.now() - attempts.lastAttempt > LOGIN_ATTEMPT_TIMEOUT) {
    attempts.count = 0;
  }
  
  return {
    isLocked: attempts.count >= MAX_LOGIN_ATTEMPTS,
    remainingAttempts: Math.max(0, MAX_LOGIN_ATTEMPTS - attempts.count),
    lockTimeRemaining: Math.max(0, LOGIN_ATTEMPT_TIMEOUT - (Date.now() - attempts.lastAttempt))
  };
};

// 记录登录失败
export const recordLoginFailure = (email) => {
  const attemptKey = `login_attempts_${email}`;
  const attempts = JSON.parse(localStorage.getItem(attemptKey) || '{"count": 0, "lastAttempt": 0}');
  
  attempts.count += 1;
  attempts.lastAttempt = Date.now();
  
  localStorage.setItem(attemptKey, JSON.stringify(attempts));
  
  return {
    isLocked: attempts.count >= MAX_LOGIN_ATTEMPTS,
    remainingAttempts: Math.max(0, MAX_LOGIN_ATTEMPTS - attempts.count)
  };
};

// 清除登录尝试记录
export const clearLoginAttempts = (email) => {
  const attemptKey = `login_attempts_${email}`;
  localStorage.removeItem(attemptKey);
};

// 检测可疑登录活动
export const detectSuspiciousActivity = () => {
  const lastLoginInfo = JSON.parse(localStorage.getItem('last_login_info') || '{}');
  const currentInfo = {
    userAgent: navigator.userAgent,
    language: navigator.language,
    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
    screen: `${screen.width}x${screen.height}`,
    timestamp: Date.now()
  };
  
  let isSuspicious = false;
  const changes = [];
  
  if (lastLoginInfo.userAgent && lastLoginInfo.userAgent !== currentInfo.userAgent) {
    isSuspicious = true;
    changes.push('浏览器或设备');
  }
  
  if (lastLoginInfo.timezone && lastLoginInfo.timezone !== currentInfo.timezone) {
    isSuspicious = true;
    changes.push('时区');
  }
  
  // 保存当前登录信息
  localStorage.setItem('last_login_info', JSON.stringify(currentInfo));
  
  return { isSuspicious, changes };
};

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

// 安全登出
export const logout = () => {
  try {
    // 1. 清除所有认证相关的本地存储
    localStorage.removeItem(AUTH_KEY);
    localStorage.removeItem('userEmail'); // 清除记住的邮箱
    
    // 2. 清除会话存储
    sessionStorage.clear();
    
    // 3. 清除所有cookie（如果有的话）
    document.cookie.split(";").forEach(function(c) { 
      document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
    });
    
    // 4. 清除会话超时计时器
    clearSessionTimeout();
    
    // 5. 清除快捷登录账户（可选，根据安全策略决定）
    // 注意：这里不清除快捷登录账户，因为用户可能希望保留这些信息
    
    // 6. 通知服务器端登出（如果有相关API）
    // TODO: 可以在这里调用服务器端的logout API来使token失效
    
    // 7. 清除可能的缓存数据
    if ('caches' in window) {
      caches.keys().then(names => {
        names.forEach(name => {
          caches.delete(name);
        });
      });
    }
    
    console.log('用户已安全登出');
    
  } catch (error) {
    console.error('登出过程中发生错误:', error);
  } finally {
    // 8. 强制跳转到登录页面
    window.location.hash = '#/login';
    // 可选：刷新页面以确保完全清除状态
    setTimeout(() => {
      window.location.reload();
    }, 100);
  }
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