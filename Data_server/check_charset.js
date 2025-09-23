const mysql = require('mysql2/promise');
require('dotenv').config();

const config = {
  host: 'localhost',
  user: 'root',
  password: process.env.DB_PASSWORD || 'Wenguang-1122',
  charset: 'utf8mb4'
};

async function checkCharset() {
  let connection;
  
  try {
    connection = await mysql.createConnection(config);
    console.log('连接MySQL成功\n');

    // 检查数据库字符集
    console.log('===== 检查数据库字符集 =====');
    const databases = ['wenji_db', 'wenji_users', 'wenji_comments', 'wenji_social_auth', 'wenji_operation_logs'];
    
    for (const dbName of databases) {
      try {
        const [rows] = await connection.query(`SHOW CREATE DATABASE ${dbName}`);
        if (rows.length > 0) {
          console.log(`数据库 ${dbName}:`);
          console.log(rows[0]['Create Database']);
          console.log('');
        }
      } catch (error) {
        console.log(`数据库 ${dbName} 不存在或无法访问`);
      }
    }

    // 检查主数据库的表字符集
    console.log('===== 检查 wenji_db 数据库表字符集 =====');
    try {
      await connection.query('USE wenji_db');
      const [tables] = await connection.query('SHOW TABLES');
      
      for (const table of tables) {
        const tableName = Object.values(table)[0];
        const [createTable] = await connection.query(`SHOW CREATE TABLE ${tableName}`);
        console.log(`表 ${tableName}:`);
        console.log(createTable[0]['Create Table']);
        console.log('');
      }
    } catch (error) {
      console.log('无法检查 wenji_db 表字符集:', error.message);
    }

    // 检查服务器字符集设置
    console.log('===== 检查MySQL服务器字符集设置 =====');
    const [charsetVars] = await connection.query(`
      SHOW VARIABLES WHERE Variable_name LIKE 'character_set_%' 
      OR Variable_name LIKE 'collation_%'
    `);
    
    charsetVars.forEach(row => {
      console.log(`${row.Variable_name}: ${row.Value}`);
    });

  } catch (error) {
    console.error('检查字符集失败:', error.message);
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

checkCharset();