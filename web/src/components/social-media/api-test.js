// 简单的API测试文件
import { socialMediaAPI } from '@/utils/api.js';

// 测试函数
export async function testSocialMediaAPI() {
  try {
    console.log('开始测试社交媒体API...');
    
    // 测试获取平台状态
    console.log('测试getPlatformStatus方法...');
    const platformStatus = await socialMediaAPI.getPlatformStatus();
    console.log('平台状态:', platformStatus);
    
    // 测试获取主页列表
    console.log('\n测试getHomepages方法...');
    const homepages = await socialMediaAPI.getHomepages();
    console.log('主页列表数量:', homepages.length);
    
    // 测试Facebook相关API
    console.log('\n测试Facebook相关方法...');
    const fbAuthUrl = await socialMediaAPI.getFBAuthUrl();
    console.log('Facebook授权URL:', fbAuthUrl);
    
    // 其他API方法信息
    console.log('\nAPI方法列表:');
    console.log('- 获取已发布帖子: socialMediaAPI.getPosts()');
    console.log('- 发布新帖子: socialMediaAPI.publishPost(postData)');
    console.log('- 创建定时任务: socialMediaAPI.schedulePost(postData)');
    console.log('- 获取定时任务: socialMediaAPI.getScheduledTasks()');
    console.log('- 取消定时任务: socialMediaAPI.cancelScheduledTask(taskId)');
    console.log('- 重试失败任务: socialMediaAPI.retryTask(taskId)');
    console.log('- 删除帖子: socialMediaAPI.deletePost(postId)');
    
    return {
      success: true,
      message: 'API测试完成'
    };
  } catch (error) {
    console.error('API测试失败:', error);
    return {
      success: false,
      message: error.message
    };
  }
}

// 如果直接运行此文件，则执行测试
if (typeof window !== 'undefined') {
  // 在浏览器环境中，可以通过控制台手动调用 testSocialMediaAPI()
  window.testSocialMediaAPI = testSocialMediaAPI;
  console.log('API测试函数已加载，请在控制台输入 testSocialMediaAPI() 执行测试');
}