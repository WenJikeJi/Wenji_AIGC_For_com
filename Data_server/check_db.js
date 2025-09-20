const mysql = require('mysql2/promise');

async function checkDatabase() {
    try {
        // 先连接到MySQL但不指定数据库
        const connection = await mysql.createConnection({
            host: 'localhost',
            user: 'root',
            password: 'Wenguang-1122'
        });
        
        console.log('连接MySQL成功');
        
        // 检查所有可用的数据库
        console.log('\n===== 可用数据库列表 =====');
        const [databases] = await connection.query('SHOW DATABASES');
        console.log(databases.map(db => db.Database));
        
        // 查找可能的文姬项目数据库
        const possibleDatabases = databases.map(db => db.Database)
            .filter(db => db.includes('wenji') || db.includes('Wenji'));
        
        console.log('\n===== 可能的文姬项目数据库 =====');
        console.log(possibleDatabases);
        
        // 如果有找到可能的数据库，则连接第一个
        if (possibleDatabases.length > 0) {
            const dbName = possibleDatabases[0];
            console.log(`\n===== 连接到数据库: ${dbName} =====`);
            
            // 选择数据库
            await connection.query(`USE ${dbName}`);
            
            // 检查系统加密配置表是否存在
            try {
                const [tables] = await connection.query('SHOW TABLES');
                console.log('\n===== 数据库中的表 =====');
                console.log(tables.map(table => Object.values(table)[0]));
                
                // 检查系统加密配置
                if (tables.some(table => Object.values(table)[0] === 'system_encryption_config')) {
                    console.log('\n===== 系统加密配置 =====');
                    const [systemConfigs] = await connection.query('SELECT * FROM system_encryption_config WHERE status = 1');
                    console.log(systemConfigs);
                }
                
                // 检查用户账户表
                if (tables.some(table => Object.values(table)[0] === 'user_account')) {
                    console.log('\n===== 用户账户 =====');
                    const [users] = await connection.query('SELECT id, account, email, role, status FROM user_account');
                    console.log(users);
                    
                    // 检查用户表结构
                    const [userColumns] = await connection.query('SHOW COLUMNS FROM user_account');
                    console.log('\n===== 用户表字段 =====');
                    console.log(userColumns.map(col => col.Field));
                }
            } catch (tableError) {
                console.error('检查表结构时出错:', tableError);
            }
        }
        
        await connection.end();
    } catch (error) {
        console.error('数据库检查出错:', error);
    }
}

checkDatabase();