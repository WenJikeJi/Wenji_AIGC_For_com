@echo off

REM 设置编码为UTF-8
chcp 65001

REM 设置数据库连接环境变量
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"

REM 设置JWT密钥
set "JWT_SECRET=wenji_secret_key_1234567890"

REM 设置邮件服务器配置（使用飞书邮箱）
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@wenji.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"

REM 设置服务器端口
set "SERVER_PORT=8080"

REM 直接使用Spring Boot Maven插件运行应用程序
call mvn spring-boot:run -Dspring-boot.run.arguments="--server.ssl.enabled=false,--jwt.secret=wenji_secret_key_1234567890"

pause