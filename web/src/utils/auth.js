// 用户认证服务

const AUTH_KEY = 'user_auth';
const SESSION_TIMEOUT = 4 * 60 * 60 * 1000; // 4小时，与后端JWT过期时间保持一致
const MAX_LOGIN_ATTEMPTS = 5; // 最大登录尝试次数
const LOGIN_ATTEMPT_TIMEOUT = 15 * 60 * 1000; // 15分钟锁定时间

let sessionTimeoutId = null;

// JWT令牌解析和验证工具函数
const parseJWT = (token) => {
  try {
    if (!token) return null;
    
    // JWT由三部分组成：header.payload.signature
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    
    // 解码payload部分
    const payload = JSON.parse(atob(parts[1]));
    return payload;
  } catch (error) {
    console.error('JWT解析失败:', error);
    return null;
  }
};

// 检查JWT令牌是否过期
export const isJWTExpired = (token) => {
  try {
    const payload = parseJWT(token);
    if (!payload || !payload.exp) return true;
    
    // exp字段是Unix时间戳（秒），需要转换为毫秒
    const expirationTime = payload.exp * 1000;
    const currentTime = Date.now();
    
    console.log('JWT过期检查:', {
      expirationTime: new Date(expirationTime).toISOString(),
      currentTime: new Date(currentTime).toISOString(),
      isExpired: currentTime >= expirationTime
    });
    
    return currentTime >= expirationTime;
  } catch (error) {
    console.error('JWT过期检查失败:', error);
    return true; // 出错时认为已过期
  }
};

// 获取JWT令牌剩余有效时间（毫秒）
const getJWTRemainingTime = (token) => {
  try {
    const payload = parseJWT(token);
    if (!payload || !payload.exp) return 0;
    
    const expirationTime = payload.exp * 1000;
    const currentTime = Date.now();
    
    return Math.max(0, expirationTime - currentTime);
  } catch (error) {
    console.error('获取JWT剩余时间失败:', error);
    return 0;
  }
};

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

// 保存认证信息
export const saveAuthInfo = (authInfo) => {
  try {
    const authData = {
      ...authInfo,
      loginTime: Date.now()
    };
    localStorage.setItem(AUTH_KEY, JSON.stringify(authData));
    
    // 保存刷新令牌到单独的存储（可选，增强安全性）
    if (authInfo.refreshToken) {
      localStorage.setItem('refresh_token', authInfo.refreshToken);
    }
    
    // 启动会话超时管理
    startSessionTimeout();
    console.log('认证信息保存成功');
  } catch (error) {
    console.error('保存认证信息失败:', error);
  }
};

// 获取刷新令牌
export const getRefreshToken = () => {
  try {
    // 优先从认证信息中获取
    const authInfo = JSON.parse(localStorage.getItem(AUTH_KEY) || '{}');
    if (authInfo.refreshToken) {
      return authInfo.refreshToken;
    }
    
    // 备选：从单独存储中获取
    return localStorage.getItem('refresh_token');
  } catch (error) {
    console.error('获取刷新令牌失败:', error);
    return null;
  }
};

// 刷新JWT令牌
export const refreshJWTToken = async () => {
  try {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      throw new Error('刷新令牌不存在');
    }
    
    console.log('开始刷新JWT令牌...');
    
    // 调用后端刷新令牌接口
    const response = await fetch('/api/auth/refresh-token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        refreshToken: refreshToken
      })
    });
    
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || '刷新令牌失败');
    }
    
    const data = await response.json();
    console.log('JWT令牌刷新成功:', data);
    
    // 更新本地存储的认证信息
    const currentAuthInfo = JSON.parse(localStorage.getItem(AUTH_KEY) || '{}');
    const newAuthInfo = {
      ...currentAuthInfo,
      token: data.token,
      refreshToken: data.refreshToken,
      loginTime: Date.now() // 更新登录时间
    };
    
    saveAuthInfo(newAuthInfo);
    
    return {
      success: true,
      token: data.token,
      refreshToken: data.refreshToken
    };
  } catch (error) {
    console.error('刷新JWT令牌失败:', error);
    
    // 刷新失败，清除认证信息并跳转到登录页面
    logout();
    
    return {
      success: false,
      error: error.message
    };
  }
};

// 自动刷新令牌（在令牌即将过期时调用）
export const autoRefreshToken = async () => {
  const authInfo = getAuthInfo();
  if (!authInfo || !authInfo.token) {
    return false;
  }
  
  const remainingTime = getJWTRemainingTime(authInfo.token);
  
  // 如果剩余时间少于10分钟，尝试刷新
  if (remainingTime < 10 * 60 * 1000 && remainingTime > 0) {
    console.log('JWT令牌即将过期，尝试自动刷新...');
    const result = await refreshJWTToken();
    return result.success;
  }
  
  return true; // 令牌仍然有效
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
    
    // 首先检查JWT令牌是否过期
    if (authInfo.token && isJWTExpired(authInfo.token)) {
      console.warn('JWT令牌已过期，自动清除认证信息');
      logout();
      return null;
    }
    
    // 检查本地会话是否已过期
    if (Date.now() - authInfo.loginTime > SESSION_TIMEOUT) {
      console.warn('本地会话已过期，自动清除认证信息');
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
    if (confirm('您的会话已超过4小时无活动，将自动登出。')) {
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

// 检查JWT令牌状态并提供用户友好的提示
export const checkJWTStatus = () => {
  const authInfo = getAuthInfo();
  if (!authInfo || !authInfo.token) {
    return { status: 'no_token', message: '未登录' };
  }
  
  const token = authInfo.token;
  const remainingTime = getJWTRemainingTime(token);
  
  if (remainingTime <= 0) {
    return { status: 'expired', message: 'JWT令牌已过期，请重新登录' };
  }
  
  // 如果剩余时间少于30分钟，提醒用户
  if (remainingTime < 30 * 60 * 1000) {
    const minutes = Math.floor(remainingTime / (60 * 1000));
    return { 
      status: 'expiring_soon', 
      message: `JWT令牌将在${minutes}分钟后过期，建议重新登录`,
      remainingMinutes: minutes
    };
  }
  
  return { 
    status: 'valid', 
    message: 'JWT令牌有效',
    remainingTime: remainingTime
  };
};

// 导出JWT相关工具函数供其他模块使用
export const jwtUtils = {
  parseJWT,
  isJWTExpired,
  getJWTRemainingTime,
  checkJWTStatus,
  refreshJWTToken,
  autoRefreshToken
};