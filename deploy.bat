@echo off
REM 项目部署脚本
chcp 65001

echo ========================================
echo           文记 AIGC 项目部署
echo ========================================

REM 检查环境变量文件
if not exist ".env.production" (
    echo 错误: 未找到 .env.production 文件
    echo 请复制 .env.production 文件并配置生产环境参数
    pause
    exit /b 1
)

echo 1. 构建后端应用...
cd Server
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 后端构建失败
    pause
    exit /b 1
)
cd ..

echo 2. 构建前端应用...
cd web
call npm install
call npm run build
if %errorlevel% neq 0 (
    echo 前端构建失败
    pause
    exit /b 1
)
cd ..

echo 3. 初始化数据库...
cd Data_server
call npm install
node init_databases.js
if %errorlevel% neq 0 (
    echo 数据库初始化失败
    pause
    exit /b 1
)
cd ..

echo 4. 生成RSA密钥...
node generate_rsa_keys.js
if %errorlevel% neq 0 (
    echo RSA密钥生成失败
    pause
    exit /b 1
)

echo ========================================
echo           部署完成
echo ========================================
echo 请按以下顺序启动服务:
echo 1. 启动Python日志服务: cd Date_python && start_server.bat
echo 2. 启动后端服务: cd Server && start_server.bat
echo 3. 启动前端服务: cd web && npm run preview
echo ========================================

pause