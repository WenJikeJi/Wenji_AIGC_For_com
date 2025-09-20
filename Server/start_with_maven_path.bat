@echo off
chcp 65001 > nul

REM 设置Maven路径为系统上已安装的Maven 3.9.11
SET MAVEN_HOME=C:\Program Files\Apache-maven-3.9.11
SET MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd

REM 设置项目环境变量
SET SERVER_PORT=8080
SET JWT_SECRET=Wenji2024ServerAuthenticationKey
SET SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/wenji_server?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
SET SPRING_DATASOURCE_USERNAME=root
SET SPRING_DATASOURCE_PASSWORD=123456

REM 切换到脚本所在目录
CD /D %~dp0

REM 检查Maven命令是否存在
IF NOT EXIST "%MAVEN_CMD%" (
    ECHO 错误: 未找到Maven命令 "%MAVEN_CMD%"
    PAUSE
    EXIT /B 1
)

REM 检查Java环境
java -version > nul
IF %ERRORLEVEL% NEQ 0 (
    ECHO 错误: 未找到Java环境，请确保已安装Java并配置了环境变量
    PAUSE
    EXIT /B 1
) ELSE (
    ECHO Java环境检查通过
)

REM 使用完整路径的Maven构建项目
ECHO 开始使用Maven构建项目...
"%MAVEN_CMD%" clean package -Dmaven.test.skip=true
IF %ERRORLEVEL% NEQ 0 (
    ECHO 错误: Maven构建失败
    PAUSE
    EXIT /B 1
)

REM 查找生成的JAR文件
SET JAR_FILE=
FOR /F "delims=" %%i IN ('DIR /B /A-D "target\*.jar"') DO (
    SET JAR_FILE=target\%%i
    GOTO :FOUND_JAR
)
:FOUND_JAR

IF NOT DEFINED JAR_FILE (
    ECHO 错误: 未找到构建生成的JAR文件
    PAUSE
    EXIT /B 1
)

REM 启动Java服务
ECHO 找到JAR文件: %JAR_FILE%
ECHO 开始启动Java服务，端口: %SERVER_PORT%
java -Dfile.encoding=UTF-8 -jar "%JAR_FILE%" --server.port=%SERVER_PORT% --jwt.secret=%JWT_SECRET% --spring.datasource.url=%SPRING_DATASOURCE_URL% --spring.datasource.username=%SPRING_DATASOURCE_USERNAME% --spring.datasource.password=%SPRING_DATASOURCE_PASSWORD%

IF %ERRORLEVEL% NEQ 0 (
    ECHO 错误: Java服务启动失败
    PAUSE
    EXIT /B 1
)

PAUSE