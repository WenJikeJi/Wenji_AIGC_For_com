@echo off
chcp 65001 > nul

REM ===================================
REM 文姬项目服务启动脚本 (简单版本)
REM ===================================
REM 专为在Trae中运行设计
REM 直接显示服务启动过程和日志
REM ===================================

echo 欢迎使用文姬项目Trae版启动脚本

:menu
cls
echo ===================================
echo 文姬项目服务管理菜单

echo 1. 检查环境状态
echo 2. 初始化数据库
echo 3. 启动Java后端服务
echo 4. 启动Python数据处理服务
echo 5. 启动前端服务
echo 6. 检查端口占用
echo 0. 退出

echo 请输入选择: 
set /p choice=

if %choice% == 1 goto check_status
if %choice% == 2 goto init_db
if %choice% == 3 goto start_java
if %choice% == 4 goto start_python
if %choice% == 5 goto start_frontend
if %choice% == 6 goto check_ports
if %choice% == 0 goto exit

echo 无效选择，请重新输入。
pause
goto menu

:check_status
cls
echo 检查MySQL服务状态...
sc query MYSQL80 > nul
if %ERRORLEVEL% EQU 0 (
    echo [成功] MySQL服务正在运行
) else (
    echo [错误] MySQL服务未运行
)

echo.
echo 检查端口占用情况:
netstat -ano | findstr "3000 8080 8000 5173"

echo.
echo 按任意键返回菜单
pause
goto menu

:init_db
cls
echo 正在初始化数据库...
cd Data_server
set SERVER_PORT=3000

echo 安装Node.js依赖...
npm install

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 依赖安装失败
    pause
    cd ..
    goto menu
)

echo 运行数据库初始化脚本...
node init_databases.js

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 数据库初始化失败
) else (
    echo [成功] 数据库初始化完成
)

pause
cd ..
goto menu

:start_java
cls
echo 正在启动Java后端服务...
echo 注意：此服务将在当前终端运行，按Ctrl+C可停止

echo 正在设置环境变量...
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@wenji.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "SERVER_PORT=8080"

cd Server

echo 正在启动Java后端服务...
java -jar target/Server-1.0.0.jar --server.ssl.enabled=false --jwt.secret=wenji_secret_key_1234567890

cd ..
goto menu

:start_python
cls
echo 正在启动Python数据处理服务...
echo 注意：此服务将在当前终端运行，按Ctrl+C可停止

cd Date_python

echo 安装Python依赖...
py -m pip install fastapi uvicorn
py -m pip install -r requirements.txt

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 依赖安装失败
    pause
    cd ..
    goto menu
)

echo 启动Python服务...
py -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload

cd ..
goto menu

:start_frontend
cls
echo 正在启动前端服务...
echo 注意：此服务将在当前终端运行，按Ctrl+C可停止

cd web

echo 安装前端依赖...
npm install

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 依赖安装失败
    pause
    cd ..
    goto menu
)

echo 启动前端服务...
npm run dev

cd ..
goto menu

:check_ports
cls
echo 检查端口占用情况:
netstat -ano | findstr "3000 8080 8000 5173"

echo 按任意键返回菜单
pause
goto menu

:exit
echo 感谢使用文姬项目服务管理脚本
pause