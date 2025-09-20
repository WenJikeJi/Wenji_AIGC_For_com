@echo off
REM Setting encoding to UTF-8
chcp 65001

REM Setting database connection environment variables
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"

REM Setting JWT secret key
set "JWT_SECRET=wenji_secret_key_1234567890"

REM Setting mail server configuration
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@wenji.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"

REM Setting server port
set "SERVER_PORT=8080"

REM Starting Spring Boot application, explicitly disable SSL and pass JWT secret, force UTF-8 encoding
"C:\Program Files\Java\jre1.8.0_461\bin\java" -Dfile.encoding=UTF-8 -jar target/Server-1.0.0.jar --server.ssl.enabled=false --jwt.secret=wenji_secret_key_1234567890

pause