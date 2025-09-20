@echo off

REM 简化版启动脚本 - 直接启动后端服务
set "JAVA_HOME=C:\Program Files\Java\jre1.8.0_461"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM 设置服务器配置
set "SERVER_PORT=8080"
set "DB_URL=jdbc:mysql://localhost:3306/wenji?useSSL=false"
set "DB_USER=root"
set "DB_PASS=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"

REM 确保使用UTF-8编码
chcp 65001 >nul

REM 构建并启动应用
cd /d "%~dp0"
echo 正在构建项目...
mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 项目构建失败
    pause
    exit /b 1
)

echo [成功] 项目构建完成，正在启动服务...
echo 服务器端口: %SERVER_PORT%

java -Dfile.encoding=UTF-8 -jar target/Server-1.0.0.jar --server.port=%SERVER_PORT% --spring.datasource.url=%DB_URL% --spring.datasource.username=%DB_USER% --spring.datasource.password=%DB_PASS% --jwt.secret=%JWT_SECRET%

pause