-- 创建测试用户脚本
-- 密码: 12345678 (BCrypt加密后的值)

USE wenji_db;

-- 插入测试用户
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
) VALUES (
    '测试用户', 
    'testuser', 
    'test@example.com', 
    '$2a$10$N.zmdr9k7uOsaLQJnkKCUOgBz2tBjZWZKt5V5JhKzKzKzKzKzKzKzO', 
    0, 
    1, 
    1, 
    NOW(), 
    NOW()
);

-- 插入管理员用户
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
) VALUES (
    '管理员', 
    'admin', 
    'admin@example.com', 
    '$2a$10$N.zmdr9k7uOsaLQJnkKCUOgBz2tBjZWZKt5V5JhKzKzKzKzKzKzKzO', 
    0, 
    1, 
    1, 
    NOW(), 
    NOW()
);

-- 查看插入的用户
SELECT id, username, account, email, role, status FROM user_account;