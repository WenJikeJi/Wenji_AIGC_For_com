// 快捷登录管理工具

const QUICK_LOGIN_KEY = 'quick_login_accounts';

// 保存登录账户到快捷登录列表
export const saveQuickLoginAccount = (email, username) => {
  try {
    const accounts = getQuickLoginAccounts();
    
    // 检查是否已存在该邮箱
    const existingIndex = accounts.findIndex(account => account.email === email);
    
    if (existingIndex !== -1) {
      // 如果已存在，更新用户名和最后登录时间
      accounts[existingIndex] = {
        ...accounts[existingIndex],
        username: username || accounts[existingIndex].username,
        lastLoginTime: Date.now()
      };
    } else {
      // 如果不存在，添加新账户
      accounts.unshift({
        email,
        username: username || email.split('@')[0],
        lastLoginTime: Date.now(),
        id: Date.now().toString()
      });
    }
    
    // 限制最多保存10个账户
    if (accounts.length > 10) {
      accounts.splice(10);
    }
    
    localStorage.setItem(QUICK_LOGIN_KEY, JSON.stringify(accounts));
    return true;
  } catch (error) {
    console.error('保存快捷登录账户失败:', error);
    return false;
  }
};

// 获取快捷登录账户列表
export const getQuickLoginAccounts = () => {
  try {
    const accountsStr = localStorage.getItem(QUICK_LOGIN_KEY);
    if (!accountsStr) return [];
    
    const accounts = JSON.parse(accountsStr);
    // 按最后登录时间排序，最近的在前面
    return accounts.sort((a, b) => b.lastLoginTime - a.lastLoginTime);
  } catch (error) {
    console.error('获取快捷登录账户失败:', error);
    return [];
  }
};

// 删除快捷登录账户
export const removeQuickLoginAccount = (email) => {
  try {
    const accounts = getQuickLoginAccounts();
    const filteredAccounts = accounts.filter(account => account.email !== email);
    localStorage.setItem(QUICK_LOGIN_KEY, JSON.stringify(filteredAccounts));
    return true;
  } catch (error) {
    console.error('删除快捷登录账户失败:', error);
    return false;
  }
};

// 清空所有快捷登录账户
export const clearQuickLoginAccounts = () => {
  try {
    localStorage.removeItem(QUICK_LOGIN_KEY);
    return true;
  } catch (error) {
    console.error('清空快捷登录账户失败:', error);
    return false;
  }
};

// 格式化显示时间
export const formatLastLoginTime = (timestamp) => {
  if (!timestamp) return '未知';
  
  const now = Date.now();
  const diff = now - timestamp;
  
  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000));
    return `${minutes}分钟前`;
  }
  
  // 小于1天
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000));
    return `${hours}小时前`;
  }
  
  // 小于7天
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000));
    return `${days}天前`;
  }
  
  // 超过7天显示具体日期
  const date = new Date(timestamp);
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  });
};