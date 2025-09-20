Write-Host "正在启动Wenji服务器..."

# 直接使用命令行参数启动服务，避免环境变量设置问题
java -jar target/Server-1.0.0.jar `
  --server.ssl.enabled=false `
  --server.port=8080 `
  --spring.datasource.url="jdbc:mysql://localhost:3306/wenji_db?useSSL=false&serverTimezone=UTC" `
  --spring.datasource.username="root" `
  --spring.datasource.password="Wenguang-1122" `
  --jwt.secret="wenji_secret_key_1234567890"

Read-Host "按Enter键退出"