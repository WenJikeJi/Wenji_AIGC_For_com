require('dotenv').config();
const mysql = require('mysql2/promise');

async function queryRecentLogs() {
  try {
    const connection = await mysql.createConnection({
      host: 'localhost',
      user: 'root',
      password: process.env.DB_PASSWORD || 'root',
      database: 'wenji_operation_logs'
    });

    const [rows] = await connection.execute(
      'SELECT * FROM operation_logs ORDER BY created_at DESC LIMIT 10'
    );

    console.log('最近10条操作日志:');
    console.log('='.repeat(80));
    
    if (rows.length === 0) {
      console.log('暂无日志记录，插入一些示例数据...');
      
      // 插入示例日志数据
      const sampleLogs = [
        {
          user_id: 1,
          operation: '用户登录',
          details: '登录成功，账号: admin@wenji.com',
          ip_address: '192.168.1.100',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        },
        {
          user_id: 1,
          operation: '修改用户信息',
          details: '修改了用户名称',
          ip_address: '192.168.1.100',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        },
        {
          user_id: 2,
          operation: '创建子账号',
          details: '创建子账号: editor@wenji.com',
          ip_address: '192.168.1.101',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        },
        {
          user_id: 1,
          operation: '发布社媒内容',
          details: '发布到Facebook: 新产品发布公告',
          ip_address: '192.168.1.100',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        },
        {
          user_id: 2,
          operation: '用户登录',
          details: '登录成功，账号: editor@wenji.com',
          ip_address: '192.168.1.102',
          user_agent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        },
        {
          user_id: 1,
          operation: '数据导出',
          details: '导出社媒数据报表',
          ip_address: '192.168.1.100',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        },
        {
          user_id: 3,
          operation: '用户注册',
          details: '新用户注册: viewer@wenji.com',
          ip_address: '192.168.1.103',
          user_agent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15'
        },
        {
          user_id: 1,
          operation: '系统设置',
          details: '修改系统配置参数',
          ip_address: '192.168.1.100',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        },
        {
          user_id: 2,
          operation: '内容审核',
          details: '审核通过3条待发布内容',
          ip_address: '192.168.1.102',
          user_agent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        },
        {
          user_id: 1,
          operation: '用户退出',
          details: '用户安全退出系统',
          ip_address: '192.168.1.100',
          user_agent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
      ];

      for (const log of sampleLogs) {
        await connection.execute(
          'INSERT INTO operation_logs (user_id, operation, details, ip_address, user_agent) VALUES (?, ?, ?, ?, ?)',
          [log.user_id, log.operation, log.details, log.ip_address, log.user_agent]
        );
      }

      console.log('示例数据插入完成，重新查询...\n');
      
      // 重新查询
      const [newRows] = await connection.execute(
        'SELECT * FROM operation_logs ORDER BY created_at DESC LIMIT 10'
      );
      
      newRows.forEach((log, index) => {
        console.log(`${index + 1}. [${log.created_at}] 用户ID: ${log.user_id}`);
        console.log(`   操作: ${log.operation}`);
        console.log(`   IP地址: ${log.ip_address || '未知'}`);
        console.log(`   用户代理: ${(log.user_agent || '未知').substring(0, 50)}...`);
        console.log(`   详情: ${log.details || '无'}`);
        console.log('-'.repeat(60));
      });
    } else {
      rows.forEach((log, index) => {
        console.log(`${index + 1}. [${log.created_at}] 用户ID: ${log.user_id}`);
        console.log(`   操作: ${log.operation}`);
        console.log(`   IP地址: ${log.ip_address || '未知'}`);
        console.log(`   用户代理: ${log.user_agent || '未知'}`);
        console.log(`   详情: ${log.details || '无'}`);
        console.log('-'.repeat(60));
      });
    }

    await connection.end();
  } catch (error) {
    console.error('查询失败:', error.message);
    
    // 如果数据库不存在，尝试查看所有数据库
    if (error.message.includes('Unknown database')) {
      console.log('\n尝试查看所有数据库...');
      try {
        const connection = await mysql.createConnection({
          host: 'localhost',
          user: 'root',
          password: process.env.DB_PASSWORD || 'root'
        });
        
        const [databases] = await connection.execute('SHOW DATABASES');
        console.log('可用数据库:');
        databases.forEach(db => console.log(`- ${db.Database}`));
        
        await connection.end();
      } catch (err) {
        console.error('无法连接数据库:', err.message);
      }
    }
  }
}

queryRecentLogs();