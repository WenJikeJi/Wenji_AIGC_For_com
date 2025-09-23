const mysql = require('mysql2/promise');
require('dotenv').config();

// MySQL连接配置
const dbConfig = {
  host: 'localhost',
  user: 'root',
  password: process.env.DB_PASSWORD || 'root',
  multipleStatements: true
};

// 创建社交媒体发帖数据库
exports.createSocialMediaPostDatabase = async function() {
  try {
    const connection = await mysql.createConnection(dbConfig);
    
    // 创建数据库
    await connection.execute('CREATE DATABASE IF NOT EXISTS wenji_social_media');
    await connection.query('USE wenji_social_media');
    
    // 创建主页表（存储Facebook/Instagram主页信息）
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS social_homepage (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        homepage_id VARCHAR(100) NOT NULL COMMENT '主页ID（FB/INS平台的主页ID）',
        homepage_name VARCHAR(200) NOT NULL COMMENT '主页名称',
        platform_type TINYINT NOT NULL COMMENT '平台类型（1-Facebook/2-Instagram）',
        access_token TEXT COMMENT '访问令牌',
        refresh_token TEXT COMMENT '刷新令牌',
        token_expires_at DATETIME COMMENT '令牌过期时间',
        user_id BIGINT NOT NULL COMMENT '关联用户ID',
        status TINYINT DEFAULT 1 COMMENT '状态（0-禁用/1-启用）',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY unique_homepage_platform (homepage_id, platform_type),
        INDEX idx_user_id (user_id),
        INDEX idx_platform_type (platform_type),
        INDEX idx_status (status)
      )
    `);
    
    // 创建发帖任务表
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS social_post_task (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        task_no VARCHAR(50) NOT NULL COMMENT '任务编号（如TASK_20250924001）',
        homepage_ids VARCHAR(200) NOT NULL COMMENT '关联主页ID（多个用逗号分隔，如123,456）',
        platform_types VARCHAR(50) NOT NULL COMMENT '发送平台（1-FB/2-INS/3-同时，如1,2）',
        post_title VARCHAR(200) COMMENT '帖子标题（INS可选）',
        post_content TEXT NOT NULL COMMENT '帖子内容',
        media_urls TEXT COMMENT '图片/视频URL（JSON数组）',
        schedule_time DATETIME NOT NULL COMMENT '定时执行时间',
        task_status TINYINT DEFAULT 0 COMMENT '0-待执行/1-已执行/2-执行失败/3-已取消',
        execute_result TEXT COMMENT '执行结果（失败时存错误信息）',
        retry_count TINYINT DEFAULT 0 COMMENT '重试次数',
        max_retry_count TINYINT DEFAULT 3 COMMENT '最大重试次数',
        operator_id BIGINT NOT NULL COMMENT '操作人ID（关联system_user.id）',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY unique_task_no (task_no),
        INDEX idx_homepage_ids (homepage_ids),
        INDEX idx_platform_types (platform_types),
        INDEX idx_schedule_time (schedule_time),
        INDEX idx_task_status (task_status),
        INDEX idx_operator_id (operator_id)
      )
    `);
    
    // 创建帖子表
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS social_post (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        post_id VARCHAR(100) NOT NULL COMMENT '平台帖子ID（FB的post_id或INS的media_id）',
        homepage_id VARCHAR(100) NOT NULL COMMENT '主页ID',
        platform_type TINYINT NOT NULL COMMENT '平台类型（1-Facebook/2-Instagram）',
        post_title VARCHAR(200) COMMENT '帖子标题',
        post_content TEXT NOT NULL COMMENT '帖子内容',
        media_urls TEXT COMMENT '媒体URL（JSON数组）',
        publish_time DATETIME NOT NULL COMMENT '发布时间',
        post_status TINYINT DEFAULT 1 COMMENT '帖子状态（0-删除/1-正常/2-隐藏）',
        task_id BIGINT COMMENT '关联任务ID（social_post_task.id，即时发帖为空）',
        publish_source TINYINT DEFAULT 0 COMMENT '发布来源（0-即时发帖/1-定时任务）',
        likes_count INT DEFAULT 0 COMMENT '点赞数',
        comments_count INT DEFAULT 0 COMMENT '评论数',
        shares_count INT DEFAULT 0 COMMENT '分享数',
        operator_id BIGINT NOT NULL COMMENT '操作人ID',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY unique_post_platform (post_id, platform_type),
        INDEX idx_homepage_id (homepage_id),
        INDEX idx_platform_type (platform_type),
        INDEX idx_publish_time (publish_time),
        INDEX idx_post_status (post_status),
        INDEX idx_task_id (task_id),
        INDEX idx_publish_source (publish_source),
        INDEX idx_operator_id (operator_id)
      )
    `);
    
    // 创建帖子统计表（用于存储定期更新的统计数据）
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS social_post_stats (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        post_id BIGINT NOT NULL COMMENT '关联social_post.id',
        platform_post_id VARCHAR(100) NOT NULL COMMENT '平台帖子ID',
        platform_type TINYINT NOT NULL COMMENT '平台类型',
        likes_count INT DEFAULT 0 COMMENT '点赞数',
        comments_count INT DEFAULT 0 COMMENT '评论数',
        shares_count INT DEFAULT 0 COMMENT '分享数',
        reach_count INT DEFAULT 0 COMMENT '触达数',
        impressions_count INT DEFAULT 0 COMMENT '展示数',
        stats_date DATE NOT NULL COMMENT '统计日期',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY unique_post_stats_date (post_id, stats_date),
        INDEX idx_platform_post_id (platform_post_id),
        INDEX idx_platform_type (platform_type),
        INDEX idx_stats_date (stats_date)
      )
    `);
    
    // 插入示例主页数据
    const sampleHomepages = [
      {
        homepage_id: 'FB_PAGE_123456',
        homepage_name: '测试Facebook主页',
        platform_type: 1,
        user_id: 1,
        status: 1
      },
      {
        homepage_id: 'INS_ACCOUNT_789012',
        homepage_name: '测试Instagram账号',
        platform_type: 2,
        user_id: 1,
        status: 1
      }
    ];
    
    // 插入示例数据
    for (const homepage of sampleHomepages) {
      await connection.execute(
        'INSERT IGNORE INTO social_homepage (homepage_id, homepage_name, platform_type, user_id, status) VALUES (?, ?, ?, ?, ?)',
        [homepage.homepage_id, homepage.homepage_name, homepage.platform_type, homepage.user_id, homepage.status]
      );
    }
    
    await connection.end();
    console.log('社交媒体发帖数据库创建成功');
    return true;
  } catch (error) {
    console.error('创建社交媒体发帖数据库失败:', error);
    throw error;
  }
};

// 初始化社交媒体发帖数据库
async function initializeSocialMediaPostDatabase() {
  console.log('开始初始化社交媒体发帖数据库...');
  
  try {
    await exports.createSocialMediaPostDatabase();
    console.log('社交媒体发帖数据库初始化完成');
  } catch (error) {
    console.error('初始化失败:', error);
    process.exit(1);
  }
}

// 如果直接运行此文件，则初始化数据库
if (require.main === module) {
  initializeSocialMediaPostDatabase();
}

// 导出函数供其他模块使用
module.exports.initializeSocialMediaPostDatabase = initializeSocialMediaPostDatabase;