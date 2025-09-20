@echo off
:: 关闭命令回显，避免冗余输出

:: 设置环境变量（必须用set定义）
set JAVA_HOME=C:\Program Files\Java\jdk-25  :: 替换为你的Java安装路径
set SERVER_PORT=8080
set JWT_SECRET=wenji_secret_key_1234567890
set DB_USERNAME=root
set DB_PASSWORD=Wenguang-1122
set JAR_FILE=target\Server-1.0.0.jar      :: JAR文件路径

:: 切换到脚本所在目录
cd /d "%~dp0"

echo 当前工作目录：%cd%

:: 检查Java是否安装
java --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo 错误：未找到Java环境！请先安装Java并配置环境变量。
    pause
    exit /b 1
)
echo Java环境正常

:: 检查Maven是否安装
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo 警告：未找到Maven！
) else (
    echo Maven环境正常
)

:: 检查JAR文件是否存在
if not exist "%JAR_FILE%" (
    echo 错误：未找到JAR文件 %JAR_FILE%
    echo 正在尝试构建项目...
    where mvn >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo 错误：无Maven环境，无法构建项目！请安装Maven后重试。
        pause
        exit /b 1
    )
    call mvn clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo 错误：Maven构建失败！请查看构建日志。
        pause
        exit /b 1
    )
    if not exist "%JAR_FILE%" (
        echo 错误：构建完成但仍未找到JAR文件！
        dir "target\*.jar" /b
        pause
        exit /b 1
    )
)
echo 找到JAR文件：%JAR_FILE%

:: 启动服务（指定编码避免乱码）
echo 正在启动服务...
java -Dfile.encoding=UTF-8 -jar "%JAR_FILE%" --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD%

:: 启动失败提示
if %errorlevel% neq 0 (
    echo 服务启动失败，请检查日志！
    pause
    exit /b 1
)

echo 服务已正常启动！
pause