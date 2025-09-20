// Wenji 社交媒体管理系统 - 数据库初始化脚本
// 使用MySQL作为数据库
const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');

// 使用绝对路径加载Data_server目录下的.env文件
const envFilePath = path.resolve(__dirname, '.env');
console.log('正在加载.env文件路径:', envFilePath);
const result = require('dotenv').config({ path: envFilePath });
if (result.error) {
  console.error('加载.env文件时出错:', result.error);
} else {
  console.log('成功加载.env文件');
}

// 数据库配置，从环境变量读取
const DB_CONFIG = {
  host: process.env.DB_HOST || 'localhost',
  port: process.env.DB_PORT || 3306,
  user: process.env.DB_USERNAME || 'root',
  password: process.env.DB_PASSWORD || '123456',
  database: process.env.DB_NAME || 'wenji_db'
};

console.log('数据库连接配置:', DB_CONFIG);

// 检查并创建日志目录
function ensureLogsDir() {
  const logsDir = './logs';
  if (!fs.existsSync(logsDir)) {
    fs.mkdirSync(logsDir, { recursive: true });
  }
  return logsDir;
}

// 创建用户账户表 - MySQL版本
async function createUsersTable(conn) {
  try {
    // 创建user_account表
    await conn.execute(`
      CREATE TABLE IF NOT EXISTS user_account (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        username VARCHAR(50) NOT NULL,
        account VARCHAR(50) NOT NULL UNIQUE,
        email VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(100) NOT NULL,
        role TINYINT NOT NULL COMMENT '0 = 主账号，1 = 子账号',
        parent_id BIGINT COMMENT '子账号关联主账号ID',
        email_verified TINYINT NOT NULL DEFAULT 0 COMMENT '0 = 未验证，1 = 已验证',
        created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_login_time DATETIME,
        last_login_ip VARCHAR(50),
        last_login_address VARCHAR(100),
        status TINYINT NOT NULL DEFAULT 1 COMMENT '1 = 正常，0 = 禁用',
        rsa_public_key TEXT,
        UNIQUE KEY uk_email_role (email, role),
        UNIQUE KEY uk_account (account),
        INDEX idx_parent_id (parent_id)
      )
    `);
    
    // 检查是否已有超级管理员账户
    const [rows] = await conn.execute(
      'SELECT COUNT(*) as count FROM user_account WHERE role = 0'
    );
    
    // 如果没有超级管理员，创建一个
    if (rows[0].count === 0) {
      // 使用BCrypt加密的密码，示例密码: Admin@2024
      const hashedPassword = '$2b$10$j6wI3QzX5eY7uN8vM9cKpO0iL1k2j3h4g5f6d7s8a9z0x';
      await conn.execute(
        'INSERT INTO user_account (username, account, email, password, role, email_verified, status) VALUES (?, ?, ?, ?, ?, ?, ?)',
        ['超级管理员', 'admin', 'admin@wenji.com', hashedPassword, 0, 1, 1]
      );
      console.log('超级管理员账户已创建');
    }
    
    return '用户账户表创建成功';
  } catch (error) {
    throw new Error(`创建用户账户表失败: ${error.message}`);
  }
}

// 创建操作日志表 - MySQL版本
async function createOperationLogsTable(conn) {
  try {
    // 创建user_operation_log表
    await conn.execute(`
      CREATE TABLE IF NOT EXISTS user_operation_log (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        user_id BIGINT NOT NULL,
        operation VARCHAR(200) NOT NULL,
        operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        operation_ip VARCHAR(50),
        operation_address VARCHAR(100),
        details TEXT,
        encryption_status TINYINT NOT NULL DEFAULT 0,
        INDEX idx_user_id (user_id),
        INDEX idx_operation_time (operation_time),
        FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
      )
    `);
    
    return '操作日志表创建成功';
  } catch (error) {
    throw new Error(`创建操作日志表失败: ${error.message}`);
  }
}

// 创建验证码表 - MySQL版本
async function createVerificationCodeTable(conn) {
  try {
    // 创建verification_code表
    await conn.execute(`
      CREATE TABLE IF NOT EXISTS verification_code (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        email VARCHAR(100) NOT NULL,
        code VARCHAR(6) NOT NULL,
        type TINYINT NOT NULL COMMENT '1 = 注册验证，2 = 密码重置',
        created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        expired_time DATETIME NOT NULL,
        used TINYINT NOT NULL DEFAULT 0,
        request_ip VARCHAR(50) NOT NULL,
        INDEX idx_email (email)
      )
    `);
    
    return '验证码表创建成功';
  } catch (error) {
    throw new Error(`创建验证码表失败: ${error.message}`);
  }
}

// 创建系统加密配置表 - MySQL版本
async function createSystemEncryptionConfigTable(conn) {
  try {
    // 创建system_encryption_config表，添加config_key字段并设置默认值
    await conn.execute(`
      CREATE TABLE IF NOT EXISTS system_encryption_config (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        config_key VARCHAR(255) NOT NULL DEFAULT 'default_encryption_key',
        config_value TEXT NULL DEFAULT NULL,
        rsa_private_key TEXT NOT NULL COMMENT '全局RSA私钥',
        rsa_public_key TEXT NOT NULL COMMENT '全局RSA公钥',
        key_version VARCHAR(20) NOT NULL COMMENT '密钥版本（如v1.0.0）',
        last_rotate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次轮换时间',
        status TINYINT NOT NULL DEFAULT 1 COMMENT '1 = 启用，0 = 禁用',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX idx_key_version (key_version),
        INDEX idx_status (status)
      )
    `);
    
    // 检查是否已有特定config_key的加密配置
    const [existingRows] = await conn.execute(
      'SELECT id, status FROM system_encryption_config WHERE config_key = ?',
      ['system_encryption_key']
    );
    
    // 生成示例RSA密钥对（实际环境中应该使用安全的密钥生成方式）
    const examplePrivateKey = `-----BEGIN RSA PRIVATE KEY-----
MIIEpQIBAAKCAQEAwG9yCg4aT2k8Fd2P5Fz9Pw4JjJ6P8Xh8QJcQ7Y4zJZk3X6t2
...[示例私钥内容]...
-----END RSA PRIVATE KEY-----`;
    
    const examplePublicKey = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwG9yCg4aT2k8Fd2P5Fz9
...[示例公钥内容]...
-----END PUBLIC KEY-----`;
    
    if (existingRows.length > 0) {
      // 如果记录存在，更新它
      const existingRecord = existingRows[0];
      await conn.execute(
        'UPDATE system_encryption_config SET rsa_private_key = ?, rsa_public_key = ?, key_version = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE config_key = ?',
        [examplePrivateKey, examplePublicKey, 'v1.0.0', 1, 'system_encryption_key']
      );
      console.log('默认加密配置已更新');
    } else {
      // 如果记录不存在，创建一个新的
      await conn.execute(
        'INSERT INTO system_encryption_config (config_key, rsa_private_key, rsa_public_key, key_version, status) VALUES (?, ?, ?, ?, ?)',
        ['system_encryption_key', examplePrivateKey, examplePublicKey, 'v1.0.0', 1]
      );
      console.log('默认加密配置已创建');
    }
    
    return '系统加密配置表创建并初始化成功';
  } catch (error) {
    throw new Error(`创建系统加密配置表失败: ${error.message}`);
  }
}

// 创建社交媒体评论数据库
// 注意：此函数使用CommonJS模块系统
exports.createCommentsDatabase = function() {
  return new Promise((resolve, reject) => {
    const db = new sqlite3.Database(DB_PATHS.COMMENTS, (err) => {
      if (err) {
        reject(err);
        return;
      }
      
      // 创建comments表
      db.run(`
        CREATE TABLE IF NOT EXISTS comments (
          id TEXT PRIMARY KEY,
          post_id TEXT NOT NULL,
          user_id INTEGER NOT NULL,
          content TEXT NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          status TEXT DEFAULT 'approved',
          reply_to_id TEXT,
          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
          FOREIGN KEY (reply_to_id) REFERENCES comments(id) ON DELETE CASCADE
        )
      `, (err) => {
        if (err) {
          reject(err);
          return;
        }
        
        // 创建索引
        db.run('CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments(post_id)', (err) => {
          if (err) {
            reject(err);
            return;
          }
          
          db.run('CREATE INDEX IF NOT EXISTS idx_comments_reply_to_id ON comments(reply_to_id)', (err) => {
            if (err) {
              reject(err);
              return;
            }
            
            // 插入一些示例评论
            const sampleComments = [
              {
                id: 'fb_c1',
                post_id: 'fb_1',
                user_id: 1,
                content: '这是一条测试评论',
                status: 'approved'
              },
              {
                id: 'fb_c2',
                post_id: 'fb_1',
                user_id: 1,
                content: '这是另一条测试评论',
                status: 'approved'
              },
              {
                id: 'ig_c1',
                post_id: 'ig_1',
                user_id: 1,
                content: 'Instagram测试评论',
                status: 'approved'
              }
            ];
            
            // 开始事务插入示例数据
            db.serialize(() => {
              db.run('BEGIN TRANSACTION');
              
              const stmt = db.prepare('INSERT INTO comments (id, post_id, user_id, content, status) VALUES (?, ?, ?, ?, ?)');
              
              sampleComments.forEach(comment => {
                stmt.run(
                  comment.id, 
                  comment.post_id, 
                  comment.user_id, 
                  comment.content,
                  comment.status
                );
              });
              
              stmt.finalize();
              db.run('COMMIT');
              db.close();
              resolve('Comments database created with sample data');
            });
          });
        });
      });
    });
  });
}

// 创建社交媒体授权数据库
exports.createSocialMediaAuthDatabase = function() {
  return new Promise((resolve, reject) => {
    const db = new sqlite3.Database(DB_PATHS.AUTH, (err) => {
      if (err) {
        reject(err);
        return;
      }
      
      // 创建social_media_auth表
      db.run(`
        CREATE TABLE IF NOT EXISTS social_media_auth (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          user_id INTEGER NOT NULL,
          platform TEXT NOT NULL,
          access_token TEXT NOT NULL,
          refresh_token TEXT,
          expires_at TIMESTAMP,
          user_info TEXT,
          connected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        )
      `, (err) => {
        if (err) {
          reject(err);
          return;
        }
        
        // 创建索引
        db.run('CREATE INDEX IF NOT EXISTS idx_social_media_auth_user_id_platform ON social_media_auth(user_id, platform)', (err) => {
          if (err) {
            reject(err);
            return;
          }
          
          db.close();
          resolve('Social media auth database created');
        });
      });
    });
  });
}

// 创建用户操作日志数据库
exports.createUserOperationLogsDatabase = function() {
  return new Promise((resolve, reject) => {
    const db = new sqlite3.Database(DB_PATHS.LOGS, (err) => {
      if (err) {
        reject(err);
        return;
      }
      
      // 创建user_operation_logs表（参考SQLite版本的SQL文件）
      db.run(`
        CREATE TABLE IF NOT EXISTS user_operation_logs (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          user_id INTEGER NOT NULL,
          operation_type TEXT NOT NULL,
          description TEXT,
          ip_address TEXT,
          user_agent TEXT,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
      `, (err) => {
        if (err) {
          reject(err);
          return;
        }
        
        // 创建索引
        db.run('CREATE INDEX IF NOT EXISTS idx_user_operation_logs_user_id ON user_operation_logs(user_id)', (err) => {
          if (err) {
            reject(err);
            return;
          }
          
          db.run('CREATE INDEX IF NOT EXISTS idx_user_operation_logs_created_at ON user_operation_logs(created_at)', (err) => {
            if (err) {
              reject(err);
              return;
            }
            
            // 插入示例数据
            const sampleLogs = [
              {
                user_id: 1,
                operation_type: '登录',
                description: '用户登录系统',
                ip_address: '192.168.1.1'
              },
              {
                user_id: 1,
                operation_type: '角色变更',
                description: '提升为超级管理员',
                ip_address: '192.168.1.1'
              },
              {
                user_id: 1,
                operation_type: '内容管理',
                description: '发布了一篇文章',
                ip_address: '192.168.1.1'
              }
            ];
            
            // 开始事务插入示例数据
            db.serialize(() => {
              db.run('BEGIN TRANSACTION');
              
              const stmt = db.prepare('INSERT INTO user_operation_logs (user_id, operation_type, description, ip_address) VALUES (?, ?, ?, ?)');
              
              sampleLogs.forEach(log => {
                stmt.run(
                  log.user_id, 
                  log.operation_type, 
                  log.description,
                  log.ip_address
                );
              });
              
              stmt.finalize();
              db.run('COMMIT');
              db.close();
              resolve('User operation logs database created with sample data');
            });
          });
        });
      });
    });
  });
}

// 初始化所有数据库 - MySQL版本
async function initializeAllDatabases() {
  try {
    console.log('开始初始化数据库...');
    
    // 确保日志目录存在
    ensureLogsDir();
    
    // 连接到MySQL数据库
    const conn = await mysql.createConnection(DB_CONFIG);
    
    console.log('成功连接到MySQL数据库');
    
    // 初始化各个表
    const results = [];
    
    results.push(await createUsersTable(conn));
    console.log('用户账户表初始化完成');
    
    results.push(await createOperationLogsTable(conn));
    console.log('操作日志表初始化完成');
    
    results.push(await createVerificationCodeTable(conn));
    console.log('验证码表初始化完成');
    
    results.push(await createSystemEncryptionConfigTable(conn));
    console.log('系统加密配置表初始化完成');
    
    // 关闭连接
    await conn.end();
    
    console.log('所有数据库表初始化成功！');
    return results;
  } catch (error) {
    console.error('数据库初始化失败:', error);
    throw error;
  }
}

// 如果直接运行此脚本，则初始化所有数据库
if (require.main === module) {
  initializeAllDatabases().catch(err => {
    console.error('初始化失败:', err);
    process.exit(1);
  });
}

// 导出函数供其他模块使用
module.exports.initializeAllDatabases = initializeAllDatabases;