@echo off
chcp 65001 > nul

echo 正在启动前端服务...

REM 使用cmd执行npm命令，避免PowerShell执行策略限制
cmd /c "cd %~dp0 && npm install && npm run dev"

pause