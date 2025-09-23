// Facebook API版本测试文件

import { socialMediaAPI } from '../../utils/api.js';

/**
 * 测试Facebook API版本
 * 验证系统是否正确使用Facebook API V23版本
 */
export const testFacebookApiVersion = async () => {
  console.log('开始测试Facebook API版本...');
  
  try {
    // 1. 获取Facebook授权URL，检查版本信息
    console.log('测试1: 获取Facebook授权URL');
    const response = await socialMediaAPI.getFBAuthUrl();
    
    // 检查返回的authUrl是否包含v23.0
    let authUrl;
    if (response.data && response.data.authUrl) {
      authUrl = response.data.authUrl;
    } else if (response.authUrl) {
      authUrl = response.authUrl;
    }
    
    console.log('Facebook授权URL:', authUrl);
    
    // 检查URL中的API版本
    const apiVersionMatch = authUrl.match(/facebook\.com\/(v\d+\.\d+)\/dialog/);
    const detectedVersion = apiVersionMatch ? apiVersionMatch[1] : '未检测到';
    
    console.log('检测到的Facebook API版本:', detectedVersion);
    
    if (detectedVersion === 'v23.0') {
      console.log('✓ 成功！系统正在使用Facebook API V23版本');
    } else {
      console.log('⚠️ 警告：检测到的版本与预期不符');
      console.log(`预期版本: v23.0`);
      console.log(`实际版本: ${detectedVersion}`);
    }
    
    // 2. 提供手动验证的方法
    console.log('\n测试2: 手动验证API版本');
    console.log('可以通过以下步骤手动验证:');
    console.log('1. 在浏览器中打开上面的授权URL');
    console.log('2. 查看URL中的版本号');
    console.log('3. 确认是否为v23.0');
    
    return {
      success: detectedVersion === 'v23.0',
      detectedVersion: detectedVersion,
      expectedVersion: 'v23.0',
      authUrl: authUrl
    };
  } catch (error) {
    console.error('测试失败:', error);
    return {
      success: false,
      error: error.message
    };
  }
};

// 如果直接运行此文件，则执行测试
if (import.meta.main) {
  testFacebookApiVersion().then(result => {
    console.log('测试结果:', result);
  });
}

// 导出为全局函数，以便在浏览器控制台中直接测试
if (typeof window !== 'undefined') {
  window.testFacebookApiVersion = testFacebookApiVersion;
}