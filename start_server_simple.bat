@echo off
REM Set environment variables
set "DB_USERNAME=root"
set "DB_PASSWORD=Wenguang-1122"
set "JWT_SECRET=wenji_secret_key_1234567890"
set "MAIL_HOST=smtp.feishu.cn"
set "MAIL_PORT=465"
set "MAIL_USERNAME=service@wenji.com"
set "MAIL_PASSWORD=ecSF2NmEODJNkB7"
set "SERVER_PORT=8080"

REM Stop all Java processes
TASKKILL /F /IM java.exe >nul 2>&1

REM Wait for 3 seconds
ping -n 4 127.0.0.1 >nul

REM Start the server with all parameters
java -Dfile.encoding=UTF-8 -jar "C:\mian\Server\target\Server-1.0.0.jar" ^
--server.port=%SERVER_PORT% ^
--server.ssl.enabled=false ^
--jwt.secret=%JWT_SECRET% ^
--spring.datasource.username=%DB_USERNAME% ^
--spring.datasource.password=%DB_PASSWORD% ^
--spring.mail.host=%MAIL_HOST% ^
--spring.mail.port=%MAIL_PORT% ^
--spring.mail.username=%MAIL_USERNAME% ^
--spring.mail.password=%MAIL_PASSWORD%

pause