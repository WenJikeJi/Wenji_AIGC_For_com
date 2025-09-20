@echo off
:: Simple Java Service Starter

:: Set environment variables
set SERVER_PORT=8080
set JWT_SECRET=wenji_secret_key_1234567890
set DB_USERNAME=root
set DB_PASSWORD=Wenguang-1122

:: Change to script directory
cd /d "%~dp0"

echo Current directory: %cd%

echo Checking Java environment...
java --version
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java not found!
    pause
    exit /b 1
)

echo Looking for JAR file in target directory...
if not exist "target\Server-1.0.0.jar" (
    echo Error: JAR file not found! Building project...
    call mvn clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo Error: Build failed!
        pause
        exit /b 1
    )
)

echo Starting Java service...
java -Dfile.encoding=UTF-8 -jar target\Server-1.0.0.jar --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD%

if %errorlevel% neq 0 (
    echo Service failed to start!
    pause
    exit /b 1
)

echo Service started successfully!