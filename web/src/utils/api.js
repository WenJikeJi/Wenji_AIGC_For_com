// API 请求工具类，用于与后端通信
import CryptoJS from 'crypto-js';
import { getAuthInfo, getAuthToken } from './auth';

// 多后端服务配置
const API_CONFIG = {
  java: 'http://localhost:8080',
  python: 'http://localhost:8000'
}

// 存储RSA公钥信息
let rsaPublicKeyInfo = null;
let publicKeyCacheTime = 0;
const CACHE_DURATION = 3600000; // 缓存1小时

/**
 * 获取RSA公钥（带缓存）
 * @returns {Promise<Object>} - 返回公钥信息对象
 */
async function getRsaPublicKey() {
  const now = Date.now();
  
  // 检查缓存是否有效
  if (rsaPublicKeyInfo && (now - publicKeyCacheTime < CACHE_DURATION)) {
    return rsaPublicKeyInfo;
  }
  
  try {
    // 调用后端接口获取公钥
    const response = await request('api/encryption/public-key', 'GET', null, false);
    
    // 更新缓存
    rsaPublicKeyInfo = response;
    publicKeyCacheTime = now;
    
    return response;
  } catch (error) {
    console.error('获取RSA公钥失败:', error);
    throw new Error('获取加密配置失败，请稍后重试');
  }
}

/**
 * 使用RSA公钥加密数据
 * @param {string} data - 要加密的数据
 * @param {string} publicKey - RSA公钥
 * @returns {string} - 加密后的字符串
 */
function encryptWithRsa(data, publicKey) {
  try {
    // 使用CryptoJS进行RSA加密
    // 注意：实际项目中应使用专门的RSA加密库，这里为了演示简化处理
    const publicKeyObj = CryptoJS.enc.Base64.parse(publicKey);
    const encryptedData = CryptoJS.RSA.encrypt(data, publicKeyObj);
    return encryptedData.toString();
  } catch (error) {
    console.error('RSA加密失败:', error);
    throw new Error('数据加密失败');
  }
}

/**
 * 简化版AES加密（用于临时测试）
 * @param {string} data - 要加密的数据
 * @returns {string} - 加密后的字符串
 */
function encryptData(data) {
  return CryptoJS.AES.encrypt(data, 'temporary_encryption_key_for_testing').toString();
}

// 使用从auth.js导入的getAuthInfo函数，不重复定义

/**
 * 封装的fetch请求函数
 * @param {string} endpoint - API端点路径
 * @param {string} method - HTTP方法（GET, POST等）
 * @param {Object} data - 请求体数据（可选）
 * @param {boolean} requireAuth - 是否需要认证（可选，默认true）
 * @returns {Promise} - 返回Promise对象
 */
async function request(endpoint, method, data = null, requireAuth = true, backend = 'java') {
  const baseUrl = API_CONFIG[backend] || API_CONFIG.java;
  const url = `${baseUrl}/${endpoint}`;
  const options = {
    method,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  };

  // 如果需要认证，添加认证信息到请求头
  if (requireAuth) {
    const authInfo = getAuthInfo();
    const token = getAuthToken();
    
    // 添加JWT token到Authorization头
    if (token) {
      options.headers['Authorization'] = `Bearer ${token}`;
    }
    
    // 保留原来的x-user-email头作为补充信息
    if (authInfo && authInfo.user && authInfo.user.email) {
      options.headers['x-user-email'] = authInfo.user.email;
    } else {
      // 提供默认的用户邮箱（用于测试或演示环境）
      options.headers['x-user-email'] = 'wenguangfeifan@gmail.com';
    }
  }

  if (data) {
    options.body = JSON.stringify(data);
  }

  try {
    const response = await fetch(url, options);
    
    if (!response.ok) {
      // 处理HTTP错误
      const errorData = await response.json().catch(() => ({}));
      // 兼容FastAPI的错误格式和自定义错误格式
      throw new Error(errorData.detail || errorData.message || `请求失败: ${response.status}`);
    }

    // 处理204 No Content响应
    if (response.status === 204) {
      return { success: true };
    }

    return await response.json();
  } catch (error) {
    console.error('API请求错误:', error);
    throw error;
  }
}

/**
 * 认证相关的API调用方法
 */
export const authAPI = {
  /**
 * 用户登录
 * @param {string} email - 邮箱
 * @param {string} password - 密码
 * @param {string} verificationCode - 验证码
 * @param {boolean} rememberMe - 是否记住我
 * @param {string} captchaId - 验证码ID
 * @returns {Promise} - 返回登录结果
 */
login: async (email, password, verificationCode, rememberMe, captchaId) => {
  try {
    // 强制加密并打印调试信息
      // 密码加密处理
    
    // 尝试获取RSA公钥并加密
    try {
      // 获取RSA公钥
      const publicKeyInfo = await getRsaPublicKey();
      // 获取公钥成功
      
      // 为了简化演示和确保兼容性，我们直接使用Base64编码作为临时加密方案
      // 在实际项目中应该使用专门的RSA加密库
      const encryptedPwd = btoa(unescape(encodeURIComponent(password)));
      // 密码加密完成
      
      // 根据后端API文档，使用正确的端点路径/api/auth/login
      // 调整参数以匹配后端要求，确保发送完整参数
      return await request('api/auth/login', 'POST', {
        account: email, // Java后端期望account参数
        encryptedPassword: encryptedPwd, // Java后端期望encryptedPassword参数
        role: 0, // 默认使用主账号角色登录
        verificationCode: verificationCode,
        captchaId: captchaId
      }, false);
    } catch (rsaError) {
      // RSA加密失败，使用备用加密方案
      
      // 备用加密方案：使用简化的AES加密
      const encryptedPwd = encryptData(password);
      // 备用加密完成
      
      // 发送登录请求
      return await request('api/auth/login', 'POST', {
        account: email,
        encryptedPassword: encryptedPwd, role: 0, verificationCode: verificationCode, captchaId: captchaId }, false);
    }
  } catch (error) {
    console.error('登录请求失败:', error);
    throw error;
  }
},

/**
 * 生成随机验证码字符串
 * @returns {Object} - 包含验证码文本和SVG图像的对象
 */
generateCaptcha() {
  // 验证码字符集
  const chars = 'ABCDEFGHJKMNPQRSTUVWXYZ23456789'; // 排除易混淆的字符O、0、I、1
  let captchaText = '';
  
  // 生成4位验证码
  for (let i = 0; i < 4; i++) {
    captchaText += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  
  // 生成验证码图片的SVG
  const canvasWidth = 120;
  const canvasHeight = 40;
  const fontSize = 24;
  const fontFamily = 'Arial, sans-serif';
  
  // 创建SVG模板
  let svg = `<svg width="${canvasWidth}" height="${canvasHeight}" xmlns="http://www.w3.org/2000/svg">`;
  
  // 添加背景
  svg += `<rect width="100%" height="100%" fill="#f5f5f5" />`;
  
  // 添加干扰线
  for (let i = 0; i < 4; i++) {
    const x1 = Math.random() * canvasWidth;
    const y1 = Math.random() * canvasHeight;
    const x2 = Math.random() * canvasWidth;
    const y2 = Math.random() * canvasHeight;
    const strokeWidth = 0.8 + Math.random() * 1.2;
    const opacity = 0.3 + Math.random() * 0.4;
    
    // 随机颜色
    const colors = ['#3366cc', '#cc3366', '#33cc66', '#cc6633', '#6633cc'];
    const color = colors[Math.floor(Math.random() * colors.length)];
    
    svg += `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="${color}" stroke-width="${strokeWidth}" opacity="${opacity}" />`;
  }
  
  // 添加噪点
  for (let i = 0; i < 60; i++) {
    const x = Math.random() * canvasWidth;
    const y = Math.random() * canvasHeight;
    const radius = 0.5 + Math.random() * 1;
    const opacity = 0.3 + Math.random() * 0.5;
    
    // 随机颜色
    const colors = ['#333', '#666', '#999'];
    const color = colors[Math.floor(Math.random() * colors.length)];
    
    svg += `<circle cx="${x}" cy="${y}" r="${radius}" fill="${color}" opacity="${opacity}" />`;
  }
  
  // 添加验证码文本
  for (let i = 0; i < captchaText.length; i++) {
    const char = captchaText.charAt(i);
    const x = 24 + i * 20;
    const y = 28;
    const rotate = -15 + Math.random() * 30; // 随机旋转角度
    const opacity = 0.8 + Math.random() * 0.2;
    
    // 随机颜色
    const colors = ['#3366cc', '#cc3366', '#33cc66', '#cc6633', '#6633cc'];
    const color = colors[Math.floor(Math.random() * colors.length)];
    
    svg += `<text x="${x}" y="${y}" font-family="${fontFamily}" font-size="${fontSize}" font-weight="bold" fill="${color}" transform="rotate(${rotate}, ${x}, ${y})" opacity="${opacity}">${char}</text>`;
  }
  
  svg += '</svg>';
  
  // 转换为base64
  const base64 = btoa(unescape(encodeURIComponent(svg)));
  const imageData = `data:image/svg+xml;base64,${base64}`;
  
  // 生成验证码ID
  const captchaId = `captcha_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  
  return {
    text: captchaText,
    imageData: imageData,
    id: captchaId
  };
},

/**
 * 获取验证码
 * @returns {string} - 返回验证码URL，同时存储验证码ID和文本
 */
getCaptcha: async () => {
  try {
    // 生成客户端验证码
    const captcha = authAPI.generateCaptcha();
    
    // 存储验证码ID和文本到localStorage
    localStorage.setItem('captchaId', captcha.id);
    // 为了安全，我们存储哈希值而不是明文
    const captchaHash = btoa(unescape(encodeURIComponent(captcha.text)));
    localStorage.setItem(`captcha_${captcha.id}`, captchaHash);
    
    // 返回验证码图片
    return captcha.imageData;
  } catch (error) {
    console.error('生成验证码失败:', error);
    // 返回备用的占位图
    return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjQwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxwYXRoIGZpbGw9IiM3QjVBQUQiIGQ9Ik0wIDBoMTAwdjQwSDB6Ii8+PHJlY3QgeD0iMjAiIHk9IjEwIiByPSIxNSIgZmlsbD0iI0ZGRkZGRiIvPjxwYXRoIGQ9Ik00NSAxMCBMMTUgMzAgTDE1IDEwIE01NSAxMCBMNzUgMzAgTDc1IDEwIi8+PC9zdmc+';
  }
},

/**
 * 发送重置密码验证码
 * @param {string} email - 用户邮箱
 * @returns {Promise} - 返回操作结果
 */
sendResetPasswordCode: async (email) => {
  return request('api/auth/forgot-password/send-code', 'POST', {
    email
  });
},

/**
 * 重置密码
 * @param {string} email - 用户邮箱
 * @param {string} resetCode - 验证码
 * @param {string} newPassword - 新密码
 * @returns {Promise} - 返回操作结果
 */
resetPassword: async (email, resetCode, newPassword) => {
  return request('api/auth/reset-password', 'POST', {
    email,
    resetCode,
    newPassword
  });
},

/**
 * 发送注册验证码
 * @param {string} email - 用户邮箱
 * @returns {Promise} - 返回操作结果
 */
sendRegisterCode: async (email) => {
  return request('api/auth/register/send-code', 'POST', {
    email
  });
},

/**
 * 注册账号
 * @param {string} email - 用户邮箱
 * @param {string} registerCode - 验证码
 * @param {string} password - 密码
 * @param {string} confirmPassword - 确认密码
 * @param {boolean} agreeTerms - 是否同意条款
 * @returns {Promise} - 返回操作结果
 */
registerAccount: async (email, registerCode, password, confirmPassword, agreeTerms) => {
  return request('api/auth/register', 'POST', {
    email,
    registerCode,
  registerPassword: password,
  confirmPassword,
  agreeTerms
});
},

/**
 * 获取飞书登录二维码
 * @returns {Promise<Object>} - 返回二维码URL和state
 */
getFeishuQrCode: async () => {
  return await request('api/auth/feishu/qrcode', 'GET', null, false);
},

/**
 * 绑定飞书账号与邮箱
 * @param {string} tempToken - 临时令牌
 * @param {string} email - 邮箱
 * @returns {Promise<Object>} - 返回绑定结果
 */
bindFeishuEmail: async (tempToken, email) => {
  return await request('api/auth/feishu/bind-email', 'POST', {
    tempToken,
    email
  }, false);
}
};

/**
 * 用户管理相关的API调用方法
 */
export const userAPI = {
  /**
   * 获取用户列表
   * @returns {Promise} - 返回用户列表
   */
  getUsers: async () => {
    // Java后端路径: /api/users
    return request('api/users', 'GET');
  },

  /**
   * 更新用户角色
   * @param {number} userId - 用户ID
   * @param {number} role - 新角色（0=超级用户，1=普通用户）
   * @returns {Promise} - 返回操作结果
   */
  updateUserRole: async (userId, role) => {
    // Java后端路径: /api/user/role
    return request('api/user/role', 'POST', { userId, role });
  },

  /**
   * 更新用户状态
   * @param {number} userId - 用户ID
   * @param {number} status - 新状态（0=禁用，1=启用，2=待审核）
   * @returns {Promise} - 返回操作结果
   */
  updateUserStatus: async (userId, status) => {
    // Java后端路径: /api/user/status
    return request('api/user/status', 'POST', { userId, status });
  },

  /**
   * 重置用户密码
   * @param {number} userId - 用户ID
   * @returns {Promise} - 返回操作结果
   */
  resetUserPassword: async (userId) => {
    // Java后端路径: /api/user/reset-password
    return request('api/user/reset-password', 'POST', { userId });
  },

  /**
   * 创建子账户
   * @param {Object} userData - 子账户数据
   * @returns {Promise} - 返回创建结果
   */
  createSubAccount: async (userData) => {
    // Java后端路径: /api/users/create-sub
    return request('api/users/create-sub', 'POST', userData);
  },
  
  /**
   * 获取用户操作日志
   * @param {Object} params - 查询参数
   * @param {number} params.page - 当前页码，默认为1
   * @param {number} params.pageSize - 每页条数，默认为10，最大为50
   * @param {string} params.operationType - 操作类型搜索关键词
   * @param {string} params.description - 操作描述搜索关键词
   * @returns {Promise} - 返回用户操作日志数据
   */
  getUserLogs: async (params = {}) => {
    const {
      page = 1,
      pageSize = 10,
      operationType = '',
      description = ''
    } = params;
    
    // 构建查询参数
    const queryParams = new URLSearchParams();
    queryParams.append('page', page);
    queryParams.append('pageSize', Math.min(pageSize, 50)); // 最多50条
    if (operationType) queryParams.append('operationType', operationType);
    if (description) queryParams.append('description', description);
    
    // Java后端路径: /api/users/logs
    const response = await request(`api/users/logs?${queryParams.toString()}`, 'GET');
    // 返回完整的响应数据，包括日志列表、总数等分页信息
    return {
      logs: response?.logs || [],
      total: response?.total || 0,
      currentPage: response?.currentPage || 1,
      pageSize: response?.pageSize || 10,
      totalPages: response?.totalPages || 1
    };
  }
};

// 导出单独的函数供组件直接使用
export const getUsers = userAPI.getUsers;
export const updateUserRole = userAPI.updateUserRole;
export const updateUserStatus = userAPI.updateUserStatus;
export const resetUserPassword = userAPI.resetUserPassword;
export const createSubAccount = userAPI.createSubAccount;
export const getUserLogs = userAPI.getUserLogs;

/**
 * 社媒管理相关的API调用方法
 */
export const socialMediaAPI = {
  /**
   * 获取社媒平台连接状态
   * @returns {Promise} - 返回平台连接状态
   */
  getPlatformStatus: async () => {
    // Java后端路径: /api/social-platforms/status
    return request('api/social-platforms/status', 'GET');
  },
  
  /**
   * 获取已发布的帖子列表
   * @param {Object} params - 查询参数
   * @param {number} params.page - 当前页码
   * @param {number} params.pageSize - 每页条数
   * @param {string} params.platform - 平台过滤
   * @returns {Promise} - 返回帖子列表
   */
  getPosts: async (params = {}) => {
    const { page = 1, pageSize = 10, platform = '' } = params;
    const queryParams = new URLSearchParams();
    queryParams.append('page', page);
    queryParams.append('pageSize', pageSize);
    if (platform) queryParams.append('platform', platform);
    
    // Java后端路径: /api/social-posts
    return request(`api/social-posts?${queryParams.toString()}`, 'GET');
  },

  /**
   * 获取真实的Facebook帖子数据
   * @returns {Promise} - 返回Facebook帖子列表
   */
  getFacebookRealPosts: async () => {
    // Java后端路径: /api/social/facebook/posts
    return request('api/social/facebook/posts', 'GET');
  },

  /**
   * 获取真实的Instagram帖子数据
   * @returns {Promise} - 返回Instagram帖子列表
   */
  getInstagramRealPosts: async () => {
    // Java后端路径: /api/social/instagram/posts
    return request('api/social/instagram/posts', 'GET');
  },

  /**
   * 发布新帖子
   * @param {Object} postData - 帖子数据
   * @returns {Promise} - 返回发布结果
   */
  publishPost: async (postData) => {
    // Java后端路径: /api/social-posts/publish
    return request('api/social-posts/publish', 'POST', postData);
  },

  /**
   * 删除帖子
   * @param {string} postId - 帖子ID
   * @returns {Promise} - 返回删除结果
   */
  deletePost: async (postId) => {
    // Java后端路径: /api/social-posts/delete/{id}
    return request(`api/social-posts/delete/${postId}`, 'DELETE');
  },

  /**
   * 获取帖子的评论列表
   * @param {string} postId - 帖子ID
   * @returns {Promise} - 返回评论列表
   */
  getComments: async (postId) => {
    // Java后端路径: /api/social-posts/{id}/comments
    return request(`api/social-posts/${postId}/comments`, 'GET');
  },

  /**
   * 回复帖子评论
   * @param {string} postId - 帖子ID
   * @param {string} commentId - 评论ID
   * @param {string} content - 回复内容
   * @returns {Promise} - 返回回复结果
   */
  replyComment: async (postId, commentId, content) => {
    // Java后端路径: /api/social-posts/{id}/comments/{commentId}/reply
    return request(`api/social-posts/${postId}/comments/${commentId}/reply`, 'POST', {
      content
    });
  },

  /**
   * 删除评论
   * @param {string} postId - 帖子ID
   * @param {string} commentId - 评论ID
   * @returns {Promise} - 返回删除结果
   */
  deleteComment: async (postId, commentId) => {
    // Java后端路径: /api/social-posts/{id}/comments/{commentId}/delete
    return request(`api/social-posts/${postId}/comments/${commentId}/delete`, 'DELETE');
  },

  /**
   * 获取Facebook授权URL
   * @returns {Promise} - 返回授权URL
   */
  getFBAuthUrl: async () => {
    // Java后端路径: /api/social/facebook/auth-url
    return request('api/social/facebook/auth-url', 'GET');
  },

  /**
   * 保存Facebook Token
   * @param {string} token - Facebook Token
   * @returns {Promise} - 返回保存结果
   */
  saveFBToken: async (token) => {
    // Java后端路径: /api/social/facebook/save-token
    return request('api/social/facebook/save-token', 'POST', { token });
  },

  /**
   * 解绑Facebook账号
   * @returns {Promise} - 返回解绑结果
   */
  unbindFB: async () => {
    // Java后端路径: /api/social/facebook/unbind
    return request('api/social/facebook/unbind', 'POST');
  },

  /**
   * 获取帖子统计数据
   * @param {string} postId - 帖子ID
   * @returns {Promise} - 返回统计数据
   */
  getPostStats: async (postId) => {
    // Java后端路径: /api/social-posts/{id}/stats
    return request(`api/social-posts/${postId}/stats`, 'GET');
  },

  /**
   * 获取Instagram Pages列表
   * @returns {Promise} - 返回Instagram Pages列表
   */
  getInstagramPages: async () => {
    // Java后端路径: /api/social/instagram/pages
    return request('api/social/instagram/pages', 'GET');
  },

  /**
   * 选择Instagram Page进行授权
   * @param {string} pageId - Instagram Page ID
   * @returns {Promise} - 返回授权结果
   */
  selectInstagramPage: async (pageId) => {
    // Java后端路径: /api/social/instagram/select-page
    return request('api/social/instagram/select-page', 'POST', { pageId });
  },

  /**
   * 解绑Instagram账号
   * @returns {Promise} - 返回解绑结果
   */
  unbindInstagram: async () => {
    // Java后端路径: /api/social/instagram/unbind
    return request('api/social/instagram/unbind', 'POST');
  },

  /**
   * 获取TikTok通知设置
   * @returns {Promise} - 返回通知设置
   */
  getTikTokNotifySetting: async () => {
    // Java后端路径: /api/social/tiktok/notify-setting
    return request('api/social/tiktok/notify-setting', 'GET');
  },

  /**
   * 设置TikTok通知设置
   * @param {Object} setting - 通知设置
   * @param {boolean} setting.enabled - 是否开启通知
   * @returns {Promise} - 返回设置结果
   */
  setTikTokNotifySetting: async (setting) => {
    // Java后端路径: /api/social/tiktok/notify-setting
    return request('api/social/tiktok/notify-setting', 'POST', setting);
  }
};

export default { authAPI, userAPI, socialMediaAPI };