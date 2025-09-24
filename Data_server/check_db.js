const mysql = require('mysql2/promise');

async function checkDatabase() {
    try {
        // 连接到MySQL
        const dbConfig = {
            host: process.env.DB_HOST || 'localhost',
            user: process.env.DB_USERNAME || 'root',
            password: process.env.DB_PASSWORD || 'Wenguang-1122',
            database: process.env.DB_NAME || 'wenji_db'
        };
        
        const connection = await mysql.createConnection(dbConfig);

        console.log('连接到wenji_db成功');

        // 专门查询ken@shamillaa.com用户的详细信息
        console.log('\n=== 查询ken@shamillaa.com用户详细信息 ===');
        const [userRows] = await connection.execute(
            'SELECT id, username, account, email, role, status, email_verified FROM user_account WHERE email = ?',
            ['ken@shamillaa.com']
        );
        
        if (userRows.length > 0) {
            console.log('找到用户:', userRows[0]);
            console.log('用户账号(account):', userRows[0].account);
            console.log('用户邮箱(email):', userRows[0].email);
            console.log('用户名(username):', userRows[0].username);
            console.log('角色(role):', userRows[0].role);
            console.log('状态(status):', userRows[0].status);
        } else {
            console.log('未找到该用户');
        }

        // 查询所有用户账户的account字段
        console.log('\n=== 查询所有用户的account字段 ===');
        const [allUsers] = await connection.execute(
            'SELECT id, username, account, email FROM user_account LIMIT 10'
        );
        console.log('所有用户账户信息:');
        allUsers.forEach(user => {
            console.log(`ID: ${user.id}, Account: ${user.account}, Email: ${user.email}, Username: ${user.username}`);
        });

        // 查询验证码记录
        console.log('\n=== 查询ken@shamillaa.com的验证码记录 ===');
        const [codeRows] = await connection.execute(
            'SELECT * FROM verification_code WHERE email = ? ORDER BY created_time DESC LIMIT 5',
            ['ken@shamillaa.com']
        );
        console.log('验证码记录:', codeRows);

        await connection.end();
    } catch (error) {
        console.error('数据库操作失败:', error);
    }
}

checkDatabase();