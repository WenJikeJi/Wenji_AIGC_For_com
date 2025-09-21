# 设置环境变量
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "Wenguang-1122"
$env:JWT_SECRET = "wenji_secret_key_1234567890"
$env:MAIL_HOST = "smtp.feishu.cn"
$env:MAIL_PORT = "465"
$env:MAIL_USERNAME = "service@shamillaa.com"
$env:MAIL_PASSWORD = "ecSf2NmEOOdJNkBT"

Write-Host "环境变量设置成功"

# 切换到Server目录
Set-Location -Path "c:\mian\Server"

# 启动Spring Boot服务
java -Dfile.encoding=UTF-8 -jar target\Server-1.0.0.jar --spring.mail.host=$env:MAIL_HOST --spring.mail.port=$env:MAIL_PORT --spring.mail.username=$env:MAIL_USERNAME --spring.mail.password=$env:MAIL_PASSWORD --spring.datasource.username=$env:DB_USERNAME --spring.datasource.password=$env:DB_PASSWORD --jwt.secret=$env:JWT_SECRET