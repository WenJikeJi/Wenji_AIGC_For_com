/**
 * Facebook SDK 工具模块
 * 用于初始化和管理Facebook JavaScript SDK
 */

// Facebook SDK 配置
const FB_SDK_CONFIG = {
  appId: import.meta.env.VITE_FACEBOOK_APP_ID || '{your-app-id}',
  version: 'v23.0'
};

// 状态标志
let isInitialized = false;
let initPromise = null;

/**
 * 初始化Facebook SDK
 * @returns {Promise} 初始化Promise
 */
export const initFacebookSDK = () => {
  // 如果已经初始化或正在初始化，返回已有Promise
  if (initPromise) {
    return initPromise;
  }
  
  initPromise = new Promise((resolve, reject) => {
    // 检查Facebook SDK是否已经加载
    if (window.FB) {
      // 如果已经加载但未初始化，直接初始化
      if (!isInitialized) {
        initializeFB(window.FB, resolve, reject);
      } else {
        resolve(window.FB);
      }
      return;
    }
    
    // 创建全局回调函数
    window.fbAsyncInit = function() {
      initializeFB(window.FB, resolve, reject);
    };
    
    // 动态加载Facebook SDK脚本
    loadFBSDKScript().catch(reject);
  });
  
  return initPromise;
};

/**
 * 初始化FB对象
 * @param {Object} FB - Facebook SDK对象
 * @param {Function} resolve - Promise解析函数
 * @param {Function} reject - Promise拒绝函数
 */
function initializeFB(FB, resolve, reject) {
  FB.init({
    appId: FB_SDK_CONFIG.appId,
    cookie: true,  // 启用cookie以允许服务器访问会话
    xfbml: true,   // 解析XFBML标签
    version: FB_SDK_CONFIG.version  // 使用指定的API版本
  });
  
  // 跟踪页面访问
  FB.AppEvents.logPageView();
  
  isInitialized = true;
  resolve(FB);
}

/**
 * 加载Facebook SDK脚本
 * @returns {Promise} 加载Promise
 */
function loadFBSDKScript() {
  return new Promise((resolve, reject) => {
    const id = 'facebook-jssdk';
    const d = document;
    const s = 'script';
    
    // 检查脚本是否已加载
    if (d.getElementById(id)) {
      resolve();
      return;
    }
    
    // 创建并插入脚本元素
    const js = d.createElement(s);
    js.id = id;
    js.src = `https://connect.facebook.net/en_US/sdk.js`;
    js.onload = resolve;
    js.onerror = reject;
    
    const fjs = d.getElementsByTagName(s)[0];
    fjs.parentNode.insertBefore(js, fjs);
  });
}

/**
 * 检查用户的Facebook登录状态
 * 使用FB.getLoginStatus获取用户登录状态，支持三种状态：
 * - connected: 用户已登录Facebook并已授权应用
 * - not_authorized: 用户已登录Facebook但未授权应用
 * - unknown: 用户未登录Facebook或无法确定状态
 * 
 * @param {boolean} forceRefresh - 是否强制刷新状态，默认为true
 * @returns {Promise<Object>} 登录状态结果对象，包含完整的authResponse信息
 */
export const checkLoginStatus = async (forceRefresh = true) => {
  const FB = await initFacebookSDK();
  
  return new Promise((resolve, reject) => {
    FB.getLoginStatus((response) => {
      // 响应格式参考：
      // {
      //   status: 'connected|not_authorized|unknown',
      //   authResponse: {
      //     accessToken: '...',
      //     expiresIn: '...',
      //     signedRequest: '...',
      //     userID: '...'
      //   }
      // }
      
      try {
        if (!response) {
          throw new Error('获取登录状态失败：空响应');
        }
        
        // 标准化返回结果，确保包含完整信息
        const result = {
          status: response.status, // 原始状态值
          isLoggedIn: response.status === 'connected',
          isAuthorized: response.status === 'connected'
        };
        
        // 如果用户已连接，包含完整的authResponse信息
        if (response.status === 'connected' && response.authResponse) {
          result.authResponse = {
            accessToken: response.authResponse.accessToken,
            userId: response.authResponse.userID,
            expiresIn: response.authResponse.expiresIn,
            signedRequest: response.authResponse.signedRequest,
            // 计算过期时间戳
            expiresAt: response.authResponse.expiresIn ? 
              Date.now() + (parseInt(response.authResponse.expiresIn) * 1000) : null
          };
          
          // 保持向后兼容性的字段
          result.accessToken = response.authResponse.accessToken;
          result.userId = response.authResponse.userID;
        }
        
        // 根据不同状态添加描述信息
        switch(response.status) {
          case 'connected':
            result.description = '用户已登录Facebook并已授权应用';
            break;
          case 'not_authorized':
            result.description = '用户已登录Facebook，但尚未授权应用';
            break;
          case 'unknown':
            result.description = '用户未登录Facebook，无法确定是否已授权应用';
            break;
          default:
            result.description = `未知状态: ${response.status}`;
        }
        
        resolve(result);
      } catch (error) {
        reject(new Error(`获取Facebook登录状态失败: ${error.message}`));
      }
    }, forceRefresh);
  });
};

/**
 * 用于页面加载时检查用户登录状态的快捷方法
 * 通常在应用初始化时调用，可用于决定是否需要显示登录界面
 * 
 * @param {Function} callback - 状态变化回调函数，接收statusChangeCallback格式的参数
 * @returns {Promise<void>}
 */
export const checkLoginStatusOnLoad = async (callback) => {
  try {
    const status = await checkLoginStatus(true);
    if (typeof callback === 'function') {
      callback(status);
    }
  } catch (error) {
    console.error('检查Facebook登录状态失败:', error);
    if (typeof callback === 'function') {
      callback({
        status: 'unknown',
        isLoggedIn: false,
        error: error.message
      });
    }
  }
};

/**
 * 执行Facebook登录流程
 * @param {Array} scope - 权限范围数组
 * @returns {Promise<Object>} 登录结果
 */
export const loginWithFacebook = async (scope = ['public_profile', 'email', 'pages_show_list', 'pages_read_engagement', 'pages_manage_posts', 'pages_manage_metadata']) => {
  const FB = await initFacebookSDK();
  
  return new Promise((resolve, reject) => {
    FB.login((response) => {
      if (response.status === 'connected') {
        // 登录成功
        resolve({
          isLoggedIn: true,
          accessToken: response.authResponse.accessToken,
          userId: response.authResponse.userID
        });
      } else {
        // 登录失败或用户取消
        reject(new Error('Facebook登录失败或被取消'));
      }
    }, {
      scope: scope.join(','),
      return_scopes: true
    });
  });
};

/**
 * 执行Facebook登出
 * @returns {Promise<void>}
 */
export const logoutFromFacebook = async () => {
  const FB = await initFacebookSDK();
  
  return new Promise((resolve, reject) => {
    FB.logout((response) => {
      if (response.authResponse) {
        reject(new Error('Facebook登出失败'));
      } else {
        resolve();
      }
    });
  });
};

/**
 * 获取用户信息
 * @returns {Promise<Object>} 用户信息
 */
export const getUserProfile = async () => {
  const FB = await initFacebookSDK();
  
  return new Promise((resolve, reject) => {
    FB.api('/me', { fields: 'id,name,email,picture' }, (response) => {
      if (response && !response.error) {
        resolve(response);
      } else {
        reject(new Error(response?.error?.message || '获取用户信息失败'));
      }
    });
  });
};

/**
 * 获取用户管理的Facebook页面列表
 * @returns {Promise<Array>} 页面列表
 */
export const getUserPages = async () => {
  const FB = await initFacebookSDK();
  
  return new Promise((resolve, reject) => {
    FB.api('/me/accounts', { fields: 'id,name,access_token,category,perms' }, (response) => {
      if (response && !response.error && response.data) {
        resolve(response.data);
      } else {
        reject(new Error(response?.error?.message || '获取页面列表失败'));
      }
    });
  });
};

/**
 * 调用Facebook API
 * @param {string} path - API路径
 * @param {Object} params - API参数
 * @returns {Promise<Object>} API响应
 */
export const callFacebookAPI = async (path, params = {}) => {
  const FB = await initFacebookSDK();
  
  return new Promise((resolve, reject) => {
    FB.api(path, params, (response) => {
      if (response && !response.error) {
        resolve(response);
      } else {
        reject(new Error(response?.error?.message || 'Facebook API调用失败'));
      }
    });
  });
};

// 导出模块
export default {
  init: initFacebookSDK,
  checkLoginStatus,
  login: loginWithFacebook,
  logout: logoutFromFacebook,
  getUserProfile,
  getUserPages,
  api: callFacebookAPI,
  get isInitialized() { return isInitialized; }
};