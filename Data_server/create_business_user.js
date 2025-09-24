const mysql = require('mysql2/promise');
const bcrypt = require('bcrypt');

async function createBusinessUser() {
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

        // 检查用户是否已存在
        const [existingUsers] = await connection.execute(
            'SELECT id FROM user_account WHERE email = ? OR account = ?',
            ['business@shamillaa.com', 'business@shamillaa.com']
        );
        
        if (existingUsers.length > 0) {
            console.log('用户business@shamillaa.com已存在，无需创建');
            await connection.end();
            return;
        }

        // 加密密码 Wenguang-1122
        const password = 'Wenguang-1122';
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);
        
        console.log('密码加密完成');

        // 插入新用户
        const [result] = await connection.execute(`
            INSERT INTO user_account (
                username, 
                account, 
                email, 
                password, 
                role, 
                email_verified, 
                status, 
                created_time, 
                updated_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        `, [
            'Business User',           // username
            'business@shamillaa.com', // account
            'business@shamillaa.com', // email
            hashedPassword,           // password (BCrypt加密)
            0,                        // role (0=主账号)
            1,                        // email_verified (1=已验证)
            1                         // status (1=激活)
        ]);

        console.log('用户创建成功，用户ID:', result.insertId);

        // 设置parentId为自身ID（主账号特性）
        await connection.execute(
            'UPDATE user_account SET parent_id = ? WHERE id = ?',
            [result.insertId, result.insertId]
        );

        console.log('已设置parentId为自身ID:', result.insertId);

        // 验证创建的用户
        const [newUser] = await connection.execute(
            'SELECT id, username, account, email, role, status, email_verified, parent_id FROM user_account WHERE id = ?',
            [result.insertId]
        );

        console.log('\n=== 创建的用户信息 ===');
        console.log('用户ID:', newUser[0].id);
        console.log('用户名:', newUser[0].username);
        console.log('账号:', newUser[0].account);
        console.log('邮箱:', newUser[0].email);
        console.log('角色:', newUser[0].role);
        console.log('状态:', newUser[0].status);
        console.log('邮箱验证:', newUser[0].email_verified);
        console.log('父账号ID:', newUser[0].parent_id);

        await connection.end();
        console.log('\n用户business@shamillaa.com创建完成！');
        console.log('登录信息:');
        console.log('邮箱: business@shamillaa.com');
        console.log('密码: Wenguang-1122');

    } catch (error) {
        console.error('创建用户失败:', error);
    }
}

createBusinessUser();