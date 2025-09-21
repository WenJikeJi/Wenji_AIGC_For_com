@echo off
chcp 65001

:: 简单的服务器重启脚本
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@shamillaa.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "SERVER_PORT=8080"

:: 终止已有Java进程
echo 正在终止已有Java进程...
TaskKill /F /IM java.exe 2>nul

echo 等待2秒...
timeout /t 2 /nobreak >nul

:: 启动服务器
echo 正在启动服务器...
echo 配置: 端口=%SERVER_PORT%, 邮件用户=%MAIL_USERNAME%
java -Dfile.encoding=UTF-8 -jar target\Server-1.0.0.jar ^
--server.port=%SERVER_PORT% ^
--server.ssl.enabled=false ^
--jwt.secret=%JWT_SECRET% ^
--spring.datasource.username=%DB_USERNAME% ^
--spring.datasource.password=%DB_PASSWORD% ^
--spring.mail.host=%MAIL_HOST% ^
--spring.mail.port=%MAIL_PORT% ^
--spring.mail.username=%MAIL_USERNAME% ^
--spring.mail.password=%MAIL_PASSWORD% ^
--spring.mail.properties.mail.smtp.auth=true