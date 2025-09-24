const mysql = require('mysql2/promise');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

// MySQL连接配置
const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USERNAME || 'root',
  password: process.env.DB_PASSWORD || 'root',
  multipleStatements: true
};

// 创建用户数据库
exports.createUsersDatabase = async function() {
  try {
    const connection = await mysql.createConnection(dbConfig);
    
    // 创建数据库
    await connection.execute('CREATE DATABASE IF NOT EXISTS wenji_users');
    await connection.query('USE wenji_users');
    
    // 创建用户表
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        password_hash TEXT NOT NULL,
        role VARCHAR(50) DEFAULT 'user',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      )
    `);
    
    // 创建索引
    try {
      await connection.execute('CREATE INDEX idx_users_email ON users(email)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    try {
      await connection.execute('CREATE INDEX idx_users_username ON users(username)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    
    // 插入示例用户
    const sampleUsers = [
      {
        username: 'admin',
        email: 'admin@example.com',
        password_hash: '$2b$10$example_hash_for_admin',
        role: 'admin'
      },
      {
        username: 'user1',
        email: 'user1@example.com',
        password_hash: '$2b$10$example_hash_for_user1',
        role: 'user'
      }
    ];
    
    // 插入示例数据
    for (const user of sampleUsers) {
      await connection.execute(
        'INSERT IGNORE INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)',
        [user.username, user.email, user.password_hash, user.role]
      );
    }
    
    await connection.end();
    console.log('用户数据库创建成功');
    return true;
  } catch (error) {
    console.error('创建用户数据库失败:', error);
    throw error;
  }
};
// 创建评论数据库
exports.createCommentsDatabase = async function() {
  try {
    const connection = await mysql.createConnection(dbConfig);
    
    // 创建数据库
    await connection.execute('CREATE DATABASE IF NOT EXISTS wenji_comments');
    await connection.query('USE wenji_comments');
    
    // 创建评论表
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS comments (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        content TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      )
    `);
    
    // 创建索引
    try {
      await connection.execute('CREATE INDEX idx_comments_user_id ON comments(user_id)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    try {
      await connection.execute('CREATE INDEX idx_comments_created_at ON comments(created_at)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    
    // 插入示例评论
    const sampleComments = [
      {
        user_id: 1,
        content: '这是管理员的第一条评论'
      },
      {
        user_id: 2,
        content: '这是用户的评论内容'
      },
      {
        user_id: 1,
        content: '管理员的另一条评论'
      }
    ];
    
    // 插入示例数据
    for (const comment of sampleComments) {
      await connection.execute(
        'INSERT INTO comments (user_id, content) VALUES (?, ?)',
        [comment.user_id, comment.content]
      );
    }
    
    await connection.end();
    console.log('评论数据库创建成功');
    return true;
  } catch (error) {
    console.error('创建评论数据库失败:', error);
    throw error;
  }
};

// 创建社交媒体授权数据库
exports.createSocialMediaAuthDatabase = async function() {
  try {
    const connection = await mysql.createConnection(dbConfig);
    
    // 创建数据库
    await connection.execute('CREATE DATABASE IF NOT EXISTS wenji_social_auth');
    await connection.query('USE wenji_social_auth');
    
    // 创建社交媒体授权表
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS social_auth (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        provider VARCHAR(50) NOT NULL,
        provider_id VARCHAR(255) NOT NULL,
        access_token TEXT,
        refresh_token TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE KEY unique_provider_user (provider, user_id)
      )
    `);
    
    // 创建索引
    try {
      await connection.execute('CREATE INDEX idx_social_auth_user_id ON social_auth(user_id)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    try {
      await connection.execute('CREATE INDEX idx_social_auth_provider ON social_auth(provider)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    
    await connection.end();
    console.log('社交媒体授权数据库创建成功');
    return true;
  } catch (error) {
    console.error('创建社交媒体授权数据库失败:', error);
    throw error;
  }
};

// 创建用户操作日志数据库
exports.createUserOperationLogsDatabase = async function() {
  try {
    const connection = await mysql.createConnection(dbConfig);
    
    // 创建数据库
    await connection.execute('CREATE DATABASE IF NOT EXISTS wenji_operation_logs');
    await connection.query('USE wenji_operation_logs');
    
    // 创建用户操作日志表
    await connection.execute(`
      CREATE TABLE IF NOT EXISTS operation_logs (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        operation VARCHAR(100) NOT NULL,
        details TEXT,
        ip_address VARCHAR(45),
        user_agent TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);
    
    // 创建索引
    try {
      await connection.execute('CREATE INDEX idx_operation_logs_user_id ON operation_logs(user_id)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    try {
      await connection.execute('CREATE INDEX idx_operation_logs_created_at ON operation_logs(created_at)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    try {
      await connection.execute('CREATE INDEX idx_operation_logs_operation ON operation_logs(operation)');
    } catch (err) {
      if (err.code !== 'ER_DUP_KEYNAME') throw err;
    }
    
    await connection.end();
    console.log('用户操作日志数据库创建成功');
    return true;
  } catch (error) {
    console.error('创建用户操作日志数据库失败:', error);
    throw error;
  }
};

// 初始化所有数据库
async function initializeAllDatabases() {
  console.log('开始初始化数据库...');
  
  try {
    await exports.createUsersDatabase();
    await exports.createCommentsDatabase();
    await exports.createSocialMediaAuthDatabase();
    await exports.createUserOperationLogsDatabase();
    
    // 初始化社交媒体发帖数据库
    const { createSocialMediaPostDatabase } = require('./create_social_media_database');
    await createSocialMediaPostDatabase();
    
    console.log('所有数据库初始化完成');
  } catch (error) {
    console.error('初始化失败:', error);
    process.exit(1);
  }
}

// 如果直接运行此文件，则初始化数据库
if (require.main === module) {
  initializeAllDatabases();
}

// 导出函数供其他模块使用
module.exports.initializeAllDatabases = initializeAllDatabases;