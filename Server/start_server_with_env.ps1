# 加载.env文件中的环境变量
function Load-EnvFile {
    param(
        [string]$envFilePath
    )
    
    if (Test-Path $envFilePath) {
        Get-Content $envFilePath | ForEach-Object {
            # 跳过注释行和空行
            if ($_ -match '^\s*#') { continue }
            if ($_ -match '^\s*$') { continue }
            
            # 分割键值对
            $keyValue = $_.Split('=', 2)
            if ($keyValue.Length -eq 2) {
                $key = $keyValue[0].Trim()
                $value = $keyValue[1].Trim()
                
                # 设置环境变量（Process作用域确保在当前进程中可用）
                [System.Environment]::SetEnvironmentVariable($key, $value, 'Process')
            }
        }
        Write-Host "Successfully loaded .env file: $envFilePath"
    } else {
        Write-Warning ".env file not found at $envFilePath"
    }
}

# 加载项目根目录的.env文件
Load-EnvFile -envFilePath "c:/Word_Wenji/dev/.env"
# 加载Server目录的.env文件（优先级更高）
Load-EnvFile -envFilePath "c:/Word_Wenji/dev/Server/.env"

# 显示已加载的关键环境变量以进行调试
Write-Host "Loaded environment variables:"
Write-Host "JWT_SECRET: $([Environment]::GetEnvironmentVariable('JWT_SECRET'))"
Write-Host "DB_USERNAME: $([Environment]::GetEnvironmentVariable('DB_USERNAME'))"
Write-Host "DB_PASSWORD: $([Environment]::GetEnvironmentVariable('DB_PASSWORD'))"

# 添加命令行参数直接设置jwt.secret，作为备选方案
$jwtSecret = [Environment]::GetEnvironmentVariable('JWT_SECRET')
if (-not $jwtSecret) {
    $jwtSecret = "DefaultJwtSecretKeyForDebuggingOnly1234567890"
    Write-Host "Warning: Using default JWT secret for debugging"
}

# 启动Spring Boot应用，明确传递JWT配置和其他必要参数
Write-Host "Starting Spring Boot application..."
java -jar target/Server-1.0.0.jar `
    --server.ssl.enabled=false `
    --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration `
    --server.port=8080 `
    --jwt.secret=$jwtSecret

# 等待用户输入以保持窗口打开
Read-Host -Prompt "Press Enter to exit"