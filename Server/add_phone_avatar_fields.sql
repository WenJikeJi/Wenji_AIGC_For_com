-- 数据库迁移脚本：为用户表添加phone和avatar字段
-- 执行时间：请在系统维护时间执行

USE wenji_db;

-- 1. 为user_account表添加phone和avatar字段
ALTER TABLE user_account 
ADD COLUMN phone VARCHAR(20) COMMENT '手机号码',
ADD COLUMN avatar VARCHAR(500) COMMENT '用户头像URL';

-- 2. 为新字段添加索引以提高查询性能
CREATE INDEX idx_phone ON user_account(phone);

-- 3. 验证字段添加结果
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN phone IS NOT NULL THEN 1 END) as users_with_phone,
    COUNT(CASE WHEN avatar IS NOT NULL THEN 1 END) as users_with_avatar
FROM user_account;

-- 4. 显示表结构确认字段添加成功
DESCRIBE user_account;