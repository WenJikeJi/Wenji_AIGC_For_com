@echo off

REM Set UTF-8 encoding
chcp 65001 >nul

REM Switch to script directory
cd /d "%~dp0"

REM Define database connection variables
set DB_USERNAME=root
set DB_PASSWORD=Wenguang-1122
set JWT_SECRET=wenji_secret_key_1234567890
set SERVER_PORT=8080

REM Check if JAR file exists
if not exist "target\Server-1.0.0.jar" (
    echo [ERROR] JAR file not found. Please build the project first.
    pause
    exit /b 1
)

REM Start the application
java -Dfile.encoding=UTF-8 -jar target/Server-1.0.0.jar --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET%

pause