@echo off
REM 设置编码为UTF-8
chcp 65001

REM 设置数据库连接环境变量
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"

REM 设置JWT密钥
set "JWT_SECRET=wenjiSecretKey1234567890"

REM 设置邮件服务器配置（使用飞书邮箱）
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@shamillaa.com"
set "MAIL_PASSWORD=ecSf2NmEOOdJNkBT"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"

REM 设置服务器端口
set "SERVER_PORT=8080"

REM 启动Spring Boot应用，明确禁用SSL并传递JWT密钥，强制使用UTF-8编码
java -Dfile.encoding=UTF-8 -jar target/Server-1.0.0.jar --server.ssl.enabled=false --jwt.secret=wenjisecretkeyfor256bitshs256algorithmsecuritycompliance2024mustbelongenough