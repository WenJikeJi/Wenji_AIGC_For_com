// 专门用于创建wenji_db数据库的脚本
const mysql = require('mysql2/promise');
const path = require('path');

// 加载.env文件
const dotenv = require('dotenv');
const envFilePath = path.resolve(__dirname, '.env');
const result = dotenv.config({ path: envFilePath });
if (result.error) {
  console.error('加载.env文件时出错:', result.error);
  process.exit(1);
}

// 数据库配置，不指定database参数以便连接后创建数据库
const DB_CONFIG = {
  host: process.env.DB_HOST || 'localhost',
  port: process.env.DB_PORT || 3306,
  user: process.env.DB_USERNAME || 'root',
  password: process.env.DB_PASSWORD || '123456'
};

// 要创建的数据库名
const DB_NAME = process.env.DB_NAME || 'wenji_db';

async function createDatabase() {
  try {
    console.log('开始创建数据库...');
    console.log('数据库配置:', {
      ...DB_CONFIG,
      password: '******' // 隐藏密码
    });
    console.log('要创建的数据库名:', DB_NAME);

    // 连接到MySQL服务器（不指定数据库）
    const conn = await mysql.createConnection(DB_CONFIG);
    console.log('成功连接到MySQL服务器');

    // 创建数据库
    await conn.execute(`
      CREATE DATABASE IF NOT EXISTS ${DB_NAME} 
      CHARACTER SET utf8mb4 
      COLLATE utf8mb4_unicode_ci
    `);
    console.log(`数据库 ${DB_NAME} 创建成功`);

    // 关闭连接
    await conn.end();
    console.log('数据库连接已关闭');
    return true;
  } catch (error) {
    console.error('创建数据库失败:', error);
    return false;
  }
}

// 执行创建数据库操作
createDatabase().then(success => {
  if (success) {
    console.log('数据库创建任务完成');
    process.exit(0);
  } else {
    console.error('数据库创建任务失败');
    process.exit(1);
  }
});