@echo off
REM Simple service starter with ANSI encoding

REM Set environment variables
set SERVER_PORT=8080
set JWT_SECRET=wenji_secret_key_1234567890
set DB_USERNAME=root
set DB_PASSWORD=Wenguang-1122

REM Change to script directory
cd /d "%~dp0"

echo Current directory: %cd%

echo Checking Java environment...
java --version
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java not found!
    pause
    exit /b 1
)

echo Checking if target directory exists...
if not exist "target" (
    echo Error: Target directory not found! Please build the project first.
    echo Use command: mvn clean package -DskipTests
    pause
    exit /b 1
)

echo Looking for JAR files in target directory...
dir "target\*.jar"

set JAR_FOUND=0
for %%f in (target\*.jar) do (
    set "JAR_FILE=%%f"
    set JAR_FOUND=1
)

if %JAR_FOUND% EQU 0 (
    echo Error: No JAR files found in target directory!
    echo Please build the project first.
    pause
    exit /b 1
)

echo Found JAR file: %JAR_FILE%

echo Starting Java service...
java -Dfile.encoding=UTF-8 -jar "%JAR_FILE%" --server.port=%SERVER_PORT% --server.ssl.enabled=false --jwt.secret=%JWT_SECRET% --spring.datasource.username=%DB_USERNAME% --spring.datasource.password=%DB_PASSWORD%

if %errorlevel% neq 0 (
    echo Service failed to start!
    pause
    exit /b 1
)

echo Service started successfully!