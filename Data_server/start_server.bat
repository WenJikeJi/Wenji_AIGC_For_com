@echo off
chcp 65001 > nul

REM 设置环境变量
set SERVER_PORT=3000

REM 检查是否已安装Node.js依赖
echo 正在安装Node.js依赖...
call npm install

REM 启动数据服务器
echo 正在启动数据服务器...
node init_databases.js

pause