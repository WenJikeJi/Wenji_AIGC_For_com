@echo off
chcp 65001 >nul

:: 设置环境变量
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@shamillaa.com"
set "MAIL_PASSWORD=ecSf2NmEOOdJNkBT"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"
set "SERVER_PORT=8080"

:: 显示设置的环境变量
echo 已设置的环境变量：
echo DB_USERNAME: %DB_USERNAME%
echo JWT_SECRET: %JWT_SECRET%
echo MAIL_HOST: %MAIL_HOST%
echo MAIL_PORT: %MAIL_PORT%
echo MAIL_USERNAME: %MAIL_USERNAME%
echo MAIL_PASSWORD: ********
echo SERVER_PORT: %SERVER_PORT%

echo.
echo 正在停止所有Java进程...
taskkill /F /IM java.exe >nul 2>&1

echo 等待3秒让进程完全停止...
ping -n 4 127.0.0.1 >nul

echo.
echo 正在启动Wenji Server...
echo 启动命令: java -Dfile.encoding=UTF-8 -jar "C:\mian\Server\target\Server-1.0.0.jar" --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD% --spring.mail.host=%MAIL_HOST% --spring.mail.port=%MAIL_PORT% --spring.mail.username=%MAIL_USERNAME% --spring.mail.password=%MAIL_PASSWORD% --spring.mail.properties.mail.smtp.auth=%MAIL_PROPERTIES_MAIL_SMTP_AUTH%

echo.
echo 服务启动日志：
echo ===============================================
:: 启动服务并输出日志
java -Dfile.encoding=UTF-8 -jar "C:\mian\Server\target\Server-1.0.0.jar" --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD% --spring.mail.host=%MAIL_HOST% --spring.mail.port=%MAIL_PORT% --spring.mail.username=%MAIL_USERNAME% --spring.mail.password=%MAIL_PASSWORD% --spring.mail.properties.mail.smtp.auth=%MAIL_PROPERTIES_MAIL_SMTP_AUTH%

echo ===============================================
echo.
echo 如果服务启动失败，请查看上面的错误信息。
pause