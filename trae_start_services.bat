@echo off
chcp 65001 > nul

REM ===================================
REM 文姬项目服务启动脚本 (Trae版本)
REM ===================================
REM 此版本专为在Trae终端中运行设计，不创建额外CMD窗口
REM 可以查看详细日志并提供服务管理功能
REM ===================================

echo 欢迎使用文姬项目Trae版启动脚本

echo 1. 检查当前环境状态
REM 检查MySQL服务是否运行
echo 正在检查MySQL服务状态...
sc query MYSQL80 > nul
if %ERRORLEVEL% NEQ 0 (
    echo [错误] MySQL服务未运行，请先启动MySQL服务
    goto :menu
) else (
    echo [成功] MySQL服务正在运行
)

REM 检查端口占用情况
echo.
echo 当前已运行的进程:
Get-Process node,java,python -ErrorAction SilentlyContinue

REM 创建日志目录
if not exist "logs" mkdir logs

:menu
echo.
echo ===================================
echo 文姬项目服务管理菜单

echo 1. 初始化数据库
echo 2. 启动Java后端服务
echo 3. 启动Python数据处理服务
echo 4. 启动前端服务
echo 5. 查看数据库服务日志
echo 6. 查看Java后端服务日志
echo 7. 查看Python服务日志
echo 8. 查看前端服务日志
echo 9. 检查所有服务端口
echo 0. 退出

echo 请选择操作: 
set /p choice=

if %choice% == 1 goto :init_db
if %choice% == 2 goto :start_java
if %choice% == 3 goto :start_python
if %choice% == 4 goto :start_frontend
if %choice% == 5 goto :view_db_log
if %choice% == 6 goto :view_java_log
if %choice% == 7 goto :view_python_log
if %choice% == 8 goto :view_frontend_log
if %choice% == 9 goto :check_ports
if %choice% == 0 goto :exit

echo 无效的选择，请重新输入。
goto :menu

:init_db
cls
echo ===================================
echo 正在初始化数据库...
echo ===================================
cd Data_server
set SERVER_PORT=3000

echo 安装Node.js依赖...
npm install 2> ../logs/db_error.log 1> ../logs/db_output.log

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 依赖安装失败，请查看logs/db_error.log
    cd ..
    goto :menu
)

echo 运行数据库初始化脚本...
node init_databases.js 2> ../logs/db_error.log 1> ../logs/db_output.log

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 数据库初始化失败，请查看logs/db_error.log
) else (
    echo [成功] 数据库初始化完成
)

cd ..
goto :menu

:start_java
cls
echo ===================================
echo 正在启动Java后端服务...
echo ===================================
echo 注意：此服务将在当前终端运行，按Ctrl+C可停止

echo 日志将保存到 logs/java_service.log
cd Server

REM 设置环境变量
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@wenji.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"
set "SERVER_PORT=8080"

REM 启动Java服务并输出日志
java -jar target/Server-1.0.0.jar --server.ssl.enabled=false --jwt.secret=wenji_secret_key_1234567890 2> ../logs/java_service.log 1>&2

cd ..
goto :menu

:start_python
cls
echo ===================================
echo 正在启动Python数据处理服务...
echo ===================================
echo 注意：此服务将在当前终端运行，按Ctrl+C可停止

echo 日志将保存到 logs/python_service.log
cd Date_python

REM 安装依赖
py -m pip install fastapi uvicorn 2> ../logs/python_error.log 1> ../logs/python_output.log
py -m pip install -r requirements.txt 2>> ../logs/python_error.log 1>> ../logs/python_output.log

REM 启动Python服务并输出日志
py -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload 2> ../logs/python_service.log 1>&2

cd ..
goto :menu

:start_frontend
cls
echo ===================================
echo 正在启动前端服务...
echo ===================================
echo 注意：此服务将在当前终端运行，按Ctrl+C可停止

echo 日志将保存到 logs/frontend_service.log
cd web

REM 安装依赖
npm install 2> ../logs/frontend_error.log 1> ../logs/frontend_output.log

REM 启动前端服务并输出日志
npm run dev 2> ../logs/frontend_service.log 1>&2

cd ..
goto :menu

:view_db_log
cls
echo ===================================
echo 数据库服务日志

echo 按任意键查看错误日志...
pause > nul
type logs\db_error.log
echo.
echo 按任意键查看输出日志...
pause > nul
type logs\db_output.log
echo.
echo 按任意键返回菜单...
pause > nul
goto :menu

:view_java_log
cls
echo ===================================
echo Java后端服务日志

echo 按任意键查看日志（最后50行）...
pause > nul
type logs\java_service.log | findstr /v "^$" | findstr /v "^[[:space:]]*$" | more +50
echo.
echo 按任意键返回菜单...
pause > nul
goto :menu

:view_python_log
cls
echo ===================================
echo Python服务日志

echo 按任意键查看错误日志...
pause > nul
type logs\python_error.log
echo.
echo 按任意键查看输出日志...
pause > nul
type logs\python_output.log
echo.
echo 按任意键查看服务日志（最后50行）...
pause > nul
type logs\python_service.log | findstr /v "^$" | findstr /v "^[[:space:]]*$" | more +50
echo.
echo 按任意键返回菜单...
pause > nul
goto :menu

:view_frontend_log
cls
echo ===================================
echo 前端服务日志

echo 按任意键查看错误日志...
pause > nul
type logs\frontend_error.log
echo.
echo 按任意键查看输出日志...
pause > nul
type logs\frontend_output.log
echo.
echo 按任意键查看服务日志（最后50行）...
pause > nul
type logs\frontend_service.log | findstr /v "^$" | findstr /v "^[[:space:]]*$" | more +50
echo.
echo 按任意键返回菜单...
pause > nul
goto :menu

:check_ports
cls
echo ===================================
echo 检查所有服务端口状态

echo 当前端口占用情况:
netstat -ano | findstr "3000 8080 8000 5173"

echo.
echo 当前相关进程:
Get-Process node,java,python -ErrorAction SilentlyContinue

echo.
echo 按任意键返回菜单...
pause > nul
goto :menu

:exit
echo 感谢使用文姬项目服务管理脚本
pause