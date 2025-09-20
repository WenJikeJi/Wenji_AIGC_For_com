@echo off
REM ==============================================
REM  Simple startup script for Spring Boot project
REM ==============================================

REM Set environment variables
set MAVEN_HOME=C:\Program Files\Apache-maven-3.9.11
REM Try to detect Java automatically
for /f "tokens=2* skip=2" %%a in ('reg query "HKLM\Software\JavaSoft\Java Development Kit" /v CurrentVersion') do set JDKVER=%%b
for /f "tokens=2* skip=2" %%a in ('reg query "HKLM\Software\JavaSoft\Java Development Kit\%JDKVER%" /v JavaHome') do set JAVA_HOME=%%b
set PROJECT_DIR=C:\mian\Server
set PORT=8080
set JAR_NAME=Server-1.0.0.jar

REM Check Maven installation
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Error: Maven not found at %MAVEN_HOME%
    echo Please set correct MAVEN_HOME
    pause
    exit /b 1
)

REM Check Java installation
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Error: Java not found at %JAVA_HOME%
    echo Please set correct JAVA_HOME
    pause
    exit /b 1
)

REM Build project with Maven
echo Building project...
cd "%PROJECT_DIR%"
call "%MAVEN_HOME%\bin\mvn.cmd" clean package -Dmaven.test.skip=true

REM Check if build succeeded
if %errorlevel% neq 0 (
    echo Error: Maven build failed
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "%PROJECT_DIR%\target\%JAR_NAME%" (
    echo Error: JAR file not found at target\%JAR_NAME%
    pause
    exit /b 1
)

REM Start the application
echo Starting application on port %PORT%...
"%JAVA_HOME%\bin\java.exe" -Dfile.encoding=UTF-8 -Dserver.port=%PORT% -jar "%PROJECT_DIR%\target\%JAR_NAME%"

REM Check if startup succeeded
if %errorlevel% neq 0 (
    echo Error: Application startup failed
    pause
    exit /b 1
)