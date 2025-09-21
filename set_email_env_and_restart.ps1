# 设置邮件相关环境变量
Write-Host "正在设置邮件相关环境变量..."
[Environment]::SetEnvironmentVariable('MAIL_PASSWORD', 'ecSf2NmEOOdJNkBT', 'Process')
[Environment]::SetEnvironmentVariable('MAIL_HOST', 'smtp.feishu.cn', 'Process')
[Environment]::SetEnvironmentVariable('MAIL_PORT', '465', 'Process')
[Environment]::SetEnvironmentVariable('MAIL_USERNAME', 'service@shamillaa.com', 'Process')

# 显示设置的环境变量
Write-Host "\n已设置的环境变量："
Write-Host "MAIL_HOST: $env:MAIL_HOST"
Write-Host "MAIL_PORT: $env:MAIL_PORT"
Write-Host "MAIL_USERNAME: $env:MAIL_USERNAME"
Write-Host "MAIL_PASSWORD: ********"  # 不显示实际密码

Write-Host "\n环境变量设置完成，准备重启服务..."

# 停止当前Java服务
Write-Host "\n正在查找并停止正在运行的Java服务..."
$javaProcesses = Get-Process -Name java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    foreach ($process in $javaProcesses) {
        if ($process.CommandLine -like "*Server-1.0.0.jar*") {
            Write-Host "停止Java服务，PID: $($process.Id)"
            Stop-Process -Id $process.Id -Force
            Start-Sleep -Seconds 3  # 等待进程完全停止
        }
    }
}

# 重启Java服务
Write-Host "\n正在重启Java服务..."
cd "C:\mian\Server"
Start-Process -FilePath "java" -ArgumentList "-jar", "target/Server-1.0.0.jar" -PassThru

Write-Host "\n服务重启命令已发送，请稍等几分钟让服务完全启动。"
Write-Host "\n您可以使用check_command_status工具检查服务启动状态。"