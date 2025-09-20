@echo off
chcp 65001 > nul

REM 简单前端服务启动脚本
REM 专为Trae环境设计

cd web

echo 安装前端依赖...
npm install

if %ERRORLEVEL% NEQ 0 (
    echo 依赖安装失败，请检查错误信息
    pause
    exit /b 1
)

echo 启动前端服务...
npm run dev