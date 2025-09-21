@echo off
chcp 65001

echo 正在终止已有Java进程...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

cd /d c:\mian\Server

java -Dfile.encoding=UTF-8 -jar target\Server-1.0.0.jar --server.port=8080 --server.ssl.enabled=false --jwt.secret=wenji_secret_key_1234567890 --spring.datasource.username=root --spring.datasource.password=Wenguang-1122 --spring.mail.host=smtp.feishu.cn --spring.mail.port=465 --spring.mail.username=service@shamillaa.com --spring.mail.password=ecSF2NmEODJNkB7 --spring.mail.properties.mail.smtp.auth=true