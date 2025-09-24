// 修复 init_databases.js 的脚本
// 使用方法: 将此脚本上传到服务器上的 Data_server 目录，然后执行 node fix_init_databases.js

const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'init_databases.js');

// 读取原始文件内容
fs.readFile(filePath, 'utf8', (err, data) => {
  if (err) {
    console.error('读取文件失败:', err);
    return;
  }

  // 检查是否已经包含 mysql2 导入
  if (!data.includes('const mysql = require(\'mysql2/promise\');')) {
    // 添加 mysql2 导入语句
    const updatedContent = 'const path = require(\'path\');\n' +
                          'const fs = require(\'fs\');\n' +
                          'const mysql = require(\'mysql2/promise\');\n' +
                          'require(\'dotenv\').config();\n\n' +
                          data.substring(data.indexOf('// MySQL连接配置'));
    
    // 修正环境变量名不一致问题
    const fixedContent = updatedContent
      .replace('process.env.DB_USERNAME', 'process.env.DB_USER || process.env.DB_USERNAME')
      .replace('password: process.env.DB_PASSWORD || \'root\'', 'password: process.env.DB_PASSWORD || \'\'');
    
    // 写回文件
    fs.writeFile(filePath, fixedContent, 'utf8', (err) => {
      if (err) {
        console.error('写入文件失败:', err);
      } else {
        console.log('修复成功! 文件已更新:');
        console.log('1. 添加了 mysql2/promise 模块的导入');
        console.log('2. 修正了环境变量名不一致的问题 (DB_USER 和 DB_USERNAME)');
        console.log('3. 调整了密码默认值以适应无密码情况');
        console.log('\n现在您可以尝试重新运行: node init_databases.js');
      }
    });
  } else {
    console.log('文件已经包含 mysql2 导入，无需修复。');
  }
});