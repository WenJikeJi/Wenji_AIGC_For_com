@echo off
title 文记服务启动脚本（UTF-8编码）

:: ================= 1. 强制设置编码（彻底解决乱码）=================
:: 1.1 命令行窗口编码设为UTF-8（65001），避免中文乱码
chcp 65001 >nul 2>&1
:: 1.2 设置Java输出编码为UTF-8（覆盖JVM默认编码）
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8

:: ================= 2. 切换到脚本所在目录（确保路径正确）=================
:: 无论从哪个目录执行脚本，都切换到脚本本身所在的Server根目录
cd /d "%~dp0"
echo [信息] 当前工作目录：%cd%

:: ================= 3. 检查核心依赖（Java + Maven）=================
:: 3.1 检查Java环境（要求17及以上，匹配你的JDK-25）
echo [信息] 正在检查Java环境...
java --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 未找到Java环境！请先安装Java 17+（推荐JDK-25），并配置JAVA_HOME环境变量。
    pause
    exit /b 1
)
echo [成功] Java环境正常：
java --version | findstr /i "version"

:: 3.2 检查Maven（用于构建项目，若已存在Jar包可跳过）
echo.
echo [信息] 正在检查Maven环境...
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [警告] 未找到Maven！若target目录下无Jar包，需先安装Maven并配置环境变量。
    set "MAVEN_AVAILABLE=no"
) else (
    echo [成功] Maven环境正常：
    mvn -v | findstr /i "Apache Maven"
    set "MAVEN_AVAILABLE=yes"
)

:: ================= 4. 构建项目（若未生成Jar包）=================
echo.
echo [信息] 正在检查target目录及Jar包...
:: 4.1 检查target目录是否存在
if not exist "target" (
    echo [信息] 未找到target目录，开始用Maven构建项目...
    if "%MAVEN_AVAILABLE%"=="no" (
        echo [错误] 无Maven环境，无法构建项目！请安装Maven后重试。
        pause
        exit /b 1
    )
    :: 执行Maven构建（跳过测试，加快速度）
    call mvn clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] Maven构建失败！请查看上述构建日志，修复代码或依赖问题后重试。
        pause
        exit /b 1
    )
    echo [成功] Maven构建完成，target目录已生成。
)

:: 4.2 检查Jar包是否存在（适配可能的版本后缀，如SNAPSHOT）
set "JAR_FILE="
for %%f in (target\Server-1.0.0*.jar) do (
    set "JAR_FILE=%%f"
)
if not defined JAR_FILE (
    echo [错误] 未找到Server-1.0.0相关Jar包！target目录下Jar包列表：
    dir "target\*.jar" /b
    echo [提示] 请确认Maven构建配置（pom.xml）中的artifactId和version是否为Server-1.0.0。
    pause
    exit /b 1
)
echo [成功] 找到Jar包：%JAR_FILE%

:: ================= 5. 配置项目参数（与你的原配置一致）=================
echo.
echo [信息] 正在配置项目环境变量...
:: 数据库配置
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
:: JWT密钥（与原脚本一致）
set "JWT_SECRET=wenji_secret_key_1234567890"
:: 邮件配置（飞书邮箱）
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@wenji.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "MAIL_PROPERTIES_MAIL_SMTP_AUTH=true"
:: 服务器端口（可修改）
set "SERVER_PORT=8080"

:: ================= 6. 启动Spring Boot服务=================
echo.
echo [信息] 正在启动文记服务...
echo [配置] 端口：%SERVER_PORT% ^| JWT密钥：%JWT_SECRET% ^| 邮件服务器：%MAIL_HOST%
echo [提示] 服务启动后，可访问 http://localhost:%SERVER_PORT%/swagger-ui/index.html 查看接口
echo.

:: 执行启动命令（传递所有参数，启用UTF-8编码）
java %JAVA_OPTS% -jar %JAR_FILE% ^
--server.port=%SERVER_PORT% ^
--server.ssl.enabled=false ^
--jwt.secret=%JWT_SECRET% ^
--spring.datasource.username=%DB_USERNAME% ^
--spring.datasource.password=%DB_PASSWORD% ^
--spring.mail.host=%MAIL_HOST% ^
--spring.mail.port=%MAIL_PORT% ^
--spring.mail.username=%MAIL_USERNAME% ^
--spring.mail.password=%MAIL_PASSWORD% ^
--spring.mail.properties.mail.smtp.auth=%MAIL_PROPERTIES_MAIL_SMTP_AUTH%

:: ================= 7. 启动结果检查=================
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [错误] 服务启动失败！可能原因：
    echo 1. 端口%SERVER_PORT%已被占用（用 netstat -ano ^| findstr :%SERVER_PORT% 查看占用进程）
    echo 2. 数据库连接失败（检查DB_USERNAME/DB_PASSWORD是否正确，数据库是否启动）
    echo 3. 邮件配置错误（检查MAIL_HOST/MAIL_PASSWORD是否有效）
    pause
    exit /b 1
)

echo [成功] 服务已正常启动！
pause