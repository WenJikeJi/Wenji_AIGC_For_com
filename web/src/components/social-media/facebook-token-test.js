// Facebook系统Token功能测试文件

import { socialMediaAPI } from '../../utils/api.js';

/**
 * 测试Facebook系统Token相关功能
 */
export const testFacebookSystemToken = async () => {
  console.log('开始测试Facebook系统Token功能...');
  
  try {
    // 1. 测试Token验证功能
    console.log('测试1: Token验证功能');
    // 注意：这里使用示例Token进行测试，实际使用时需要替换为有效的Token
    const verifyResult = await socialMediaAPI.verifyFBToken({
      token: 'EAALT5h8CpmABPWP8bbwFy2TMG2UvJdep5pMZBGTZAvErRprX2QQZCfCpvvYlO1ZC89SSmBq9Q0iQ2Cl0hUAUT3cQnIU03WJ3T5ZCTBMearwZAmb06c0PktCghfR1ejvSDTTcPMiScpeOqoGSYSGEKB9Pw60HGQZB0ZCZB6AocvX6ZAS7ZAtLEKKxl86ZAqjBqj5pDQZDZD',
      pageId: '' // 可选
    });
    console.log('验证结果:', verifyResult);
    
    // 2. 测试保存系统Token功能
    console.log('测试2: 保存系统Token功能');
    const saveResult = await socialMediaAPI.saveFBSystemToken({
      token: 'EAALT5h8CpmABPWP8bbwFy2TMG2UvJdep5pMZBGTZAvErRprX2QQZCfCpvvYlO1ZC89SSmBq9Q0iQ2Cl0hUAUT3cQnIU03WJ3T5ZCTBMearwZAmb06c0PktCghfR1ejvSDTTcPMiScpeOqoGSYSGEKB9Pw60HGQZB0ZCZB6AocvX6ZAS7ZAtLEKKxl86ZAqjBqj5pDQZDZD',
      expiryDate: '2024-12-31', // 可选的过期日期
      pageId: '', // 可选的主页ID
      accountName: '测试系统Token账户' // 可选的账户名称
    });
    console.log('保存结果:', saveResult);
    
    // 3. 测试获取平台状态
    console.log('测试3: 获取平台状态');
    const platformStatus = await socialMediaAPI.getPlatformStatus();
    console.log('平台状态:', platformStatus);
    
    console.log('Facebook系统Token功能测试完成');
    return {
      success: true,
      message: '所有测试完成'
    };
  } catch (error) {
    console.error('测试失败:', error);
    return {
      success: false,
      message: '测试失败: ' + error.message
    };
  }
};

// 如果直接运行此文件，则执行测试
if (import.meta.main) {
  testFacebookSystemToken().then(result => {
    console.log(result);
  });
}