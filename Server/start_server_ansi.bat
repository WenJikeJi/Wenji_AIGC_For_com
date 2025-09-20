@echo off

:: 设置编码为UTF-8
chcp 65001 >nul

:: 切换到脚本所在目录
cd /d "%~dp0"

:: 设置Java编码参数
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8

:: 检查Java环境
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo 未找到Java环境，请先安装Java 17或更高版本。
    pause
    exit /b 1
)

:: 设置服务器配置
set SERVER_PORT=8080
set DB_USERNAME=root
set DB_PASSWORD=Wenguang-1122
set JWT_SECRET=wenji_secret_key_1234567890

:: 检查JAR文件是否存在
if not exist "target\Server-1.0.0.jar" (
    echo 未找到target\Server-1.0.0.jar文件！
    echo 请先使用Maven构建项目：mvn clean package -DskipTests
    pause
    exit /b 1
)

:: 启动服务
java %JAVA_OPTS% -jar target\Server-1.0.0.jar --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD%

pause