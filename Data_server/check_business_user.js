const mysql = require('mysql2/promise');

async function checkBusinessUser() {
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

        // 查询business@shamillaa.com用户的详细信息
        console.log('\n=== 查询business@shamillaa.com用户详细信息 ===');
        const [userRows] = await connection.execute(
            'SELECT id, username, account, email, role, status, email_verified, password FROM user_account WHERE email = ? OR account = ?',
            ['business@shamillaa.com', 'business@shamillaa.com']
        );
        
        if (userRows.length > 0) {
            console.log('找到用户:', userRows[0]);
            console.log('用户ID:', userRows[0].id);
            console.log('用户账号(account):', userRows[0].account);
            console.log('用户邮箱(email):', userRows[0].email);
            console.log('用户名(username):', userRows[0].username);
            console.log('角色(role):', userRows[0].role);
            console.log('状态(status):', userRows[0].status);
            console.log('邮箱验证状态:', userRows[0].email_verified);
            console.log('密码哈希:', userRows[0].password ? '已设置' : '未设置');
        } else {
            console.log('未找到business@shamillaa.com用户');
            
            // 查询所有用户，看看是否有类似的邮箱
            console.log('\n=== 查询所有用户邮箱 ===');
            const [allUsers] = await connection.execute(
                'SELECT id, username, account, email FROM user_account'
            );
            console.log('数据库中的所有用户:');
            allUsers.forEach(user => {
                console.log(`ID: ${user.id}, Account: ${user.account}, Email: ${user.email}, Username: ${user.username}`);
            });
        }

        await connection.end();
    } catch (error) {
        console.error('数据库操作失败:', error);
    }
}

checkBusinessUser();