@echo off

REM 设置编码为UTF-8
chcp 65001

REM 重要提示：此项目需要Java 17版本，请确保已安装

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

echo 正在使用Maven构建并启动Spring Boot应用...
echo 请确保您的环境中已安装Java 17和Maven

echo 开始构建项目...
mvn clean install

if %ERRORLEVEL% NEQ 0 (
    echo [错误] 项目构建失败，请检查错误信息
    pause
    exit /b 1
)

echo [成功] 项目构建完成，开始启动应用...
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080,--spring.datasource.url=jdbc:mysql://localhost:3306/wenji?useSSL=false,--spring.datasource.username=root,--spring.datasource.password=Wenguang-1122,--jwt.secret=wenji_secret_key_1234567890,--server.ssl.enabled=false"

pause