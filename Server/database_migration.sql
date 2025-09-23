-- 数据库迁移脚本：为用户表添加子账户状态管理字段
-- 执行时间：请在系统维护时间执行

USE wenji_db;

-- 1. 为user_account表添加新字段
ALTER TABLE user_account 
ADD COLUMN account_status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '账户状态：NORMAL-正常，STOPPED-停用，INVITING-邀请中，INVALID-无效，INVITE_FAILED-邀请失败',
ADD COLUMN invite_email VARCHAR(100) COMMENT '邀请的邮箱地址',
ADD COLUMN invite_create_time DATETIME COMMENT '邀请创建时间',
ADD COLUMN temp_password VARCHAR(50) COMMENT '临时密码（首次登录用）',
ADD COLUMN updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 2. 为新字段添加索引以提高查询性能
CREATE INDEX idx_account_status ON user_account(account_status);
CREATE INDEX idx_invite_create_time ON user_account(invite_create_time);

-- 3. 更新现有数据：将现有用户的account_status设置为NORMAL
UPDATE user_account 
SET account_status = 'NORMAL', 
    updated_time = NOW() 
WHERE account_status IS NULL;

-- 4. 验证数据迁移结果
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN account_status = 'NORMAL' THEN 1 END) as normal_users,
    COUNT(CASE WHEN account_status IS NULL THEN 1 END) as null_status_users
FROM user_account;

-- 5. 显示表结构确认字段添加成功
DESCRIBE user_account;