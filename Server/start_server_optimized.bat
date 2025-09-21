@echo off
chcp 65001 >nul 2>&1

:: 设置环境变量和日志文件
set "LOG_FILE=%~dp0server_log.txt"
set "JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

echo =======================================================
echo                Wenji服务启动脚本（优化版）
echo =======================================================
echo 启动时间: %date% %time% >> "%LOG_FILE%"

echo [信息] 当前工作目录: %cd%
echo [信息] 当前工作目录: %cd% >> "%LOG_FILE%"

echo [信息] 正在检查Java环境...
echo [信息] 正在检查Java环境... >> "%LOG_FILE%"
java --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
echo [错误] 未找到Java环境！
echo [错误] 未找到Java环境！ >> "%LOG_FILE%"
pause
exit /b 1
)

echo [成功] Java环境正常
echo [成功] Java环境正常 >> "%LOG_FILE%"
java --version | findstr /i "version" >> "%LOG_FILE%"

echo [信息] 正在检查JAR包...
echo [信息] 正在检查JAR包... >> "%LOG_FILE%"
set "JAR_FILE="
for %%f in (target\Server-1.0.0*.jar) do (
set "JAR_FILE=%%f"
)
if not defined JAR_FILE (
echo [错误] 未找到Server-1.0.0相关JAR包！
echo [错误] 未找到Server-1.0.0相关JAR包！ >> "%LOG_FILE%"
dir "target\*.jar" /b >> "%LOG_FILE%"
pause
exit /b 1
)

echo [成功] 找到JAR包: %JAR_FILE%
echo [成功] 找到JAR包: %JAR_FILE% >> "%LOG_FILE%"

echo [信息] 正在配置环境变量...
echo [信息] 正在配置环境变量... >> "%LOG_FILE%"
:: 数据库配置
echo [配置] 数据库用户: root >> "%LOG_FILE%"
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
:: JWT密钥
set "JWT_SECRET=wenji_secret_key_1234567890"
:: 邮件配置 - 已统一为service@shamillaa.com
echo [配置] 邮件服务器: smtp.feishu.cn >> "%LOG_FILE%"
echo [配置] 邮件用户名: service@shamillaa.com >> "%LOG_FILE%"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@shamillaa.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"
:: 服务器端口
set "SERVER_PORT=8080"

:: 终止已有Java进程（可选）
echo [信息] 检查并终止已有Java进程...
echo [信息] 检查并终止已有Java进程... >> "%LOG_FILE%"
TaskList | findstr /i "java.exe"
if %ERRORLEVEL% EQU 0 (
echo [警告] 检测到运行中的Java进程，正在终止...
echo [警告] 检测到运行中的Java进程，正在终止... >> "%LOG_FILE%"
TaskKill /F /IM java.exe
)

echo [信息] 等待2秒...
echo [信息] 等待2秒... >> "%LOG_FILE%"
timeout /t 2 /nobreak >nul

echo [信息] 正在启动Wenji服务...
echo [信息] 正在启动Wenji服务... >> "%LOG_FILE%"
echo [配置] 端口: %SERVER_PORT%
echo [配置] 端口: %SERVER_PORT% >> "%LOG_FILE%"
echo [提示] 服务日志已保存到: %LOG_FILE%
echo [提示] 服务启动后，可访问 http://localhost:%SERVER_PORT%/swagger-ui/index.html 查看接口

echo [信息] 启动命令: java %JAVA_OPTS% -jar %JAR_FILE% --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=****** --spring.mail.host=%MAIL_HOST% --spring.mail.port=%MAIL_PORT% --spring.mail.username=%MAIL_USERNAME% --spring.mail.password=****** --spring.mail.properties.mail.smtp.auth=%MAIL_PROPERTIES_MAIL_SMTP_AUTH% >> "%LOG_FILE%"

echo. >> "%LOG_FILE%"
echo ************************ 服务输出日志开始 ************************ >> "%LOG_FILE%"

:: 启动服务并将输出重定向到日志文件
java %JAVA_OPTS% -jar %JAR_FILE% ^
--server.port=%SERVER_PORT% ^
--server.ssl.enabled=false ^
--jwt.secret=%JWT_SECRET% ^
--spring.datasource.username=%DB_USERNAME% ^
--spring.datasource.password=%DB_PASSWORD% ^
--spring.mail.host=%MAIL_HOST% ^
--spring.mail.port=%MAIL_PORT% ^
--spring.mail.username=%MAIL_USERNAME% ^
--spring.mail.password=%MAIL_PASSWORD% ^
--spring.mail.properties.mail.smtp.auth=%MAIL_PROPERTIES_MAIL_SMTP_AUTH% 2>&1 | tee -a "%LOG_FILE%"