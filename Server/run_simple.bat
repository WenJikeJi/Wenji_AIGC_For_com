@echo off

REM 设置编码为UTF-8
chcp 65001

REM 简化版启动脚本 - 直接运行应用程序主类
java -cp .;target/classes;target/dependency/* com.wenji.server.WenjiServerApplication --server.port=8080 --spring.datasource.url=jdbc:mysql://localhost:3306/wenji?useSSL=false --spring.datasource.username=root --spring.datasource.password=Wenguang-1122 --jwt.secret=wenji_secret_key_1234567890

pause