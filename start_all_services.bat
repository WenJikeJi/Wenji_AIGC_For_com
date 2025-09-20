@echo off
chcp 65001 > nul

echo ===================================
echo 文姬项目服务启动脚本
echo ===================================
echo.

REM 检查MySQL服务是否运行
echo 正在检查MySQL服务状态...
sc query MYSQL80 > nul
if %ERRORLEVEL% NEQ 0 (
    echo [错误] MySQL服务未运行，请先启动MySQL服务
    goto :end
) else (
    echo [成功] MySQL服务正在运行
)

echo.
echo ===================================
echo 正在启动数据库服务...
echo ===================================
start "数据库服务" cmd /c "cd Data_server && start_server.bat"
timeout /t 3 > nul

echo.
echo ===================================
echo 正在启动Java后端服务...
echo ===================================
start "Java后端服务" cmd /c "cd Server && start_server.bat"
timeout /t 3 > nul

echo.
echo ===================================
echo 正在启动Python数据处理服务...
echo ===================================
start "Python数据处理服务" cmd /c "cd Date_python && start_server.bat"
timeout /t 3 > nul

echo.
echo ===================================
echo 正在启动前端服务...
echo ===================================
start "前端服务" cmd /c "cd web && start_frontend.bat"

echo.
echo ===================================
echo 所有服务启动完成！
echo ===================================
echo.
echo 服务访问地址:
echo - 前端服务: http://localhost:5173
echo - Java后端服务: http://localhost:8080
echo - Python数据处理服务: http://localhost:8000
echo.
echo 按任意键退出...

:end
pause