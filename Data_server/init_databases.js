const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const fs = require('fs');

// 确保数据库目录存在
const dbDir = path.join(__dirname, 'databases');
if (!fs.existsSync(dbDir)) {
  fs.mkdirSync(dbDir, { recursive: true });
}

// 创建用户数据库
exports.createUsersDatabase = function() {
  return new Promise((resolve, reject) => {
    const dbPath = path.join(dbDir, 'users.db');
    const db = new sqlite3.Database(dbPath);
    
    db.serialize(() => {
      // 创建用户表
      db.run(`
        CREATE TABLE IF NOT EXISTS users (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          username TEXT UNIQUE NOT NULL,
          email TEXT UNIQUE NOT NULL,
          password_hash TEXT NOT NULL,
          role TEXT DEFAULT 'user',
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
      `, (err) => {
        if (err) {
          reject(err);
          return;
        }
        
        // 创建索引
        db.run('CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)', (err) => {
          if (err) {
            reject(err);
            return;
          }
          
          db.run('CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)', (err) => {
            if (err) {
              reject(err);
              return;
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
            
            // 开始事务插入示例数据
            db.serialize(() => {
              db.run('BEGIN TRANSACTION');
              
              const stmt = db.prepare('INSERT OR IGNORE INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)');
              
              sampleUsers.forEach(user => {
                stmt.run(
                  user.username, 
                  user.email, 
                  user.password_hash,
                  user.role
                );
              });
              
              stmt.finalize();
              db.run('COMMIT');
              db.close();
              resolve('Users database created with sample data');
            });
          });
        });
      });
    });
  });
};

// 创建评论数据库
exports.createCommentsDatabase = function() {
  return new Promise((resolve, reject) => {
    const dbPath = path.join(dbDir, 'comments.db');
    const db = new sqlite3.Database(dbPath);
    
    db.serialize(() => {
      // 创建评论表
      db.run(`
        CREATE TABLE IF NOT EXISTS comments (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          user_id INTEGER NOT NULL,
          content TEXT NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        )
      `, (err) => {
        if (err) {
          reject(err);
          return;
        }
        
        // 创建索引
        db.run('CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id)', (err) => {
          if (err) {
            reject(err);
            return;
          }
          
          db.run('CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments(created_at)', (err) => {
            if (err) {
              reject(err);
              return;
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
            
            // 开始事务插入示例数据
            db.serialize(() => {
              db.run('BEGIN TRANSACTION');
              
              const stmt = db.prepare('INSERT INTO comments (user_id, content) VALUES (?, ?)');
              
              sampleComments.forEach(comment => {
                stmt.run(comment.user_id, comment.content);
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
};

// 创建社交媒体授权数据库 - 已废弃，使用MySQL
exports.createSocialMediaAuthDatabase = function() {
  return new Promise((resolve, reject) => {
    // 已废弃：使用MySQL替代SQLite
    resolve('Social media auth database creation skipped - using MySQL instead');
  });
};

// 创建用户操作日志数据库 - 已废弃，使用MySQL
exports.createUserOperationLogsDatabase = function() {
  return new Promise((resolve, reject) => {
    // 已废弃：使用MySQL替代SQLite
    resolve('User operation logs database creation skipped - using MySQL instead');
  });
};

// 初始化所有数据库
function initializeAllDatabases() {
  console.log('开始初始化数据库...');
  
  // 使用MySQL，跳过SQLite数据库创建
  Promise.all([
    exports.createUsersDatabase(),
    exports.createCommentsDatabase(),
    exports.createSocialMediaAuthDatabase(),
    exports.createUserOperationLogsDatabase()
  ]).then(results => {
    console.log('所有数据库初始化完成:');
    results.forEach(result => console.log('- ' + result));
  }).catch(err => {
    console.error('初始化失败:', err);
    process.exit(1);
  });
}

// 如果直接运行此文件，则初始化数据库
if (require.main === module) {
  initializeAllDatabases();
}

// 导出函数供其他模块使用
module.exports.initializeAllDatabases = initializeAllDatabases;