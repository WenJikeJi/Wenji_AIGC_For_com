@echo off
REM 生产环境启动脚本

REM 设置编码为UTF-8
chcp 65001

REM 设置环境为生产模式
set "SPRING_PROFILES_ACTIVE=prod"

REM 启动Spring Boot应用，使用生产配置
java -jar -Dfile.encoding=UTF-8 target/Server-1.0.0.jar --spring.profiles.active=prod --server.port=8080

pause