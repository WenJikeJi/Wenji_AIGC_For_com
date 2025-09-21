@echo off
setlocal enabledelayedexpansion

:: 设置Java编码为UTF-8
set JAVA_OPTS=-Dfile.encoding=UTF-8

:: 切换到Server目录
cd /d c:\mian\Server

:: 设置环境变量（使用正确的邮件授权码）
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@shamillaa.com"
set "MAIL_PASSWORD=ecSf2NmEOOdJNkBT"

:: 启动Spring Boot服务
java %JAVA_OPTS% -jar target\Server-1.0.0.jar --spring.mail.host=%MAIL_HOST% --spring.mail.port=%MAIL_PORT% --spring.mail.username=%MAIL_USERNAME% --spring.mail.password=%MAIL_PASSWORD% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD% --jwt.secret=%JWT_SECRET%