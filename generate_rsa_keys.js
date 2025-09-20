const crypto = require('crypto');
const mysql = require('mysql2/promise');

// 数据库配置
const dbConfig = {
    host: 'localhost',
    user: 'root',
    password: 'Wenguang-1122',
    database: 'wenji_db'
};

// 生成RSA密钥对
function generateRSAKeyPair() {
    console.log('🔑 正在生成RSA密钥对...');
    
    const { publicKey, privateKey } = crypto.generateKeyPairSync('rsa', {
        modulusLength: 2048,
        publicKeyEncoding: {
            type: 'spki',
            format: 'pem'
        },
        privateKeyEncoding: {
            type: 'pkcs8',
            format: 'pem'
        }
    });
    
    // 提取公钥的base64部分（去掉头尾标识）
    const publicKeyBase64 = publicKey
        .replace('-----BEGIN PUBLIC KEY-----', '')
        .replace('-----END PUBLIC KEY-----', '')
        .replace(/\n/g, '');
    
    console.log('✅ RSA密钥对生成成功');
    console.log('公钥长度:', publicKeyBase64.length);
    console.log('私钥长度:', privateKey.length);
    
    return {
        publicKey: publicKeyBase64,
        privateKey: privateKey
    };
}

// 插入密钥对到数据库
async function insertRSAKeys() {
    let connection;
    
    try {
        console.log('📊 连接数据库...');
        connection = await mysql.createConnection(dbConfig);
        
        // 生成密钥对
        const { publicKey, privateKey } = generateRSAKeyPair();
        
        // 插入到数据库
        console.log('💾 插入RSA密钥对到数据库...');
        const insertQuery = `
            INSERT INTO system_encryption_config 
            (rsa_private_key, rsa_public_key, key_version, last_rotate_time, status) 
            VALUES (?, ?, ?, NOW(), 1)
        `;
        
        const [result] = await connection.execute(insertQuery, [
            privateKey,
            publicKey,
            'v1.0'
        ]);
        
        console.log('✅ RSA密钥对插入成功');
        console.log('插入ID:', result.insertId);
        
        // 验证插入结果
        const [rows] = await connection.execute(
            'SELECT id, key_version, status, created_time FROM system_encryption_config WHERE id = ?',
            [result.insertId]
        );
        
        if (rows.length > 0) {
            console.log('✅ 验证成功，密钥配置已保存:');
            console.log('- ID:', rows[0].id);
            console.log('- 版本:', rows[0].key_version);
            console.log('- 状态:', rows[0].status);
            console.log('- 创建时间:', rows[0].created_time);
        }
        
        return result.insertId;
        
    } catch (error) {
        console.error('❌ 操作失败:', error.message);
        throw error;
    } finally {
        if (connection) {
            await connection.end();
            console.log('📊 数据库连接已关闭');
        }
    }
}

// 测试公钥获取接口
async function testPublicKeyAPI() {
    try {
        console.log('🧪 测试公钥获取接口...');
        
        // 等待一下让服务器刷新
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        const axios = require('axios');
        const response = await axios.get('http://localhost:8080/api/encryption/public-key');
        
        if (response.data.success) {
            console.log('✅ 公钥获取接口测试成功');
            console.log('公钥内容:', response.data.data.publicKey.substring(0, 100) + '...');
            return response.data.data.publicKey;
        } else {
            throw new Error('API返回失败: ' + response.data.message);
        }
    } catch (error) {
        console.error('❌ 公钥获取接口测试失败:', error.message);
        if (error.response) {
            console.error('错误详情:', error.response.data);
        }
        throw error;
    }
}

// 主函数
async function main() {
    console.log('🚀 开始生成和配置RSA密钥对...\n');
    
    try {
        // 1. 生成并插入RSA密钥对
        const keyId = await insertRSAKeys();
        
        console.log('\n⏳ 等待2秒让服务器刷新配置...');
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 2. 测试公钥获取接口
        const publicKey = await testPublicKeyAPI();
        
        console.log('\n🎉 RSA密钥配置完成！现在可以正常使用加密功能了。');
        console.log('\n📋 接下来的步骤:');
        console.log('1. 使用公钥加密密码');
        console.log('2. 发送邮箱验证码');
        console.log('3. 进行用户注册');
        console.log('4. 用户登录获取JWT');
        
    } catch (error) {
        console.error('\n❌ 配置失败:', error.message);
        process.exit(1);
    }
}

// 如果直接运行此脚本
if (require.main === module) {
    main();
}

module.exports = {
    generateRSAKeyPair,
    insertRSAKeys,
    testPublicKeyAPI
};