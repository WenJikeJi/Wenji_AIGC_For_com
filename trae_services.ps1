# 文姬项目服务管理脚本 (PowerShell版本)
# 专为在Trae中运行设计，不创建额外窗口

function Show-Menu {
    Clear-Host
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host " 文姬项目服务管理菜单"
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host "1. 检查当前环境状态"
    Write-Host "2. 初始化数据库"
    Write-Host "3. 启动Java后端服务"
    Write-Host "4. 启动Python数据处理服务"
    Write-Host "5. 启动前端服务"
    Write-Host "6. 检查服务端口"
    Write-Host "7. 查看服务进程"
    Write-Host "0. 退出"
    Write-Host "=====================================" -ForegroundColor Green
}

function Check-MySQL {
    Write-Host "正在检查MySQL服务状态..."
    try {
        $service = Get-Service -Name "MYSQL80" -ErrorAction Stop
        if ($service.Status -eq "Running") {
            Write-Host "[成功] MySQL服务正在运行" -ForegroundColor Green
            return $true
        } else {
            Write-Host "[错误] MySQL服务未运行，请先启动MySQL服务" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "[错误] 未找到MySQL服务" -ForegroundColor Red
        return $false
    }
}

function Check-Ports {
    Write-Host "\n检查端口占用情况:"
    try {
        netstat -ano | findstr "3000 8080 8000 5173"
    } catch {
        Write-Host "无法检查端口状态" -ForegroundColor Yellow
    }
}

function Check-Processes {
    Write-Host "\n检查相关进程:"
    try {
        Get-Process node,java,python -ErrorAction SilentlyContinue
    } catch {
        Write-Host "无法检查进程状态" -ForegroundColor Yellow
    }
}

function Init-Database {
    if (-not (Check-MySQL)) {
        return
    }
    
    Write-Host "\n====================================="
    Write-Host "正在初始化数据库..."
    Write-Host "====================================="
    
    Set-Location -Path ".\Data_server"
    $env:SERVER_PORT = "3000"
    
    Write-Host "安装Node.js依赖..."
    npm install
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[错误] 依赖安装失败" -ForegroundColor Red
        Set-Location -Path ".."
        return
    }
    
    Write-Host "运行数据库初始化脚本..."
    node init_databases.js
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[错误] 数据库初始化失败" -ForegroundColor Red
    } else {
        Write-Host "[成功] 数据库初始化完成" -ForegroundColor Green
    }
    
    Set-Location -Path ".."
}

function Start-JavaService {
    if (-not (Check-MySQL)) {
        return
    }
    
    Write-Host "\n====================================="
    Write-Host "正在启动Java后端服务..."
    Write-Host "====================================="
    Write-Host "注意：此服务将在当前终端运行，按Ctrl+C可停止"
    
    Set-Location -Path ".\Server"
    
    # 设置环境变量
    $env:DB_USERNAME = "root"
    $env:DB_PASSWORD = "Wenguang-1122"
    $env:JWT_SECRET = "wenji_secret_key_1234567890"
    $env:MAIL_HOST = "smtp.feishu.cn"
    $env:MAIL_PORT = "465"
    $env:MAIL_USERNAME = "service@wenji.com"
    $env:MAIL_PASSWORD = "ecSF2NmEODJNkB7"
    $env:MAIL_PROPERTIES_MAIL_SMTP_AUTH = "true"
    $env:SERVER_PORT = "8080"
    
    # 启动Java服务
    java -jar target/Server-1.0.0.jar --server.ssl.enabled=false --jwt.secret=wenji_secret_key_1234567890
    
    Set-Location -Path ".."
}

function Start-PythonService {
    Write-Host "\n====================================="
    Write-Host "正在启动Python数据处理服务..."
    Write-Host "====================================="
    Write-Host "注意：此服务将在当前终端运行，按Ctrl+C可停止"
    
    Set-Location -Path ".\Date_python"
    
    # 安装依赖
    Write-Host "安装Python依赖..."
    py -m pip install fastapi uvicorn
    py -m pip install -r requirements.txt
    
    # 启动Python服务
    py -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
    
    Set-Location -Path ".."
}

function Start-FrontendService {
    Write-Host "\n====================================="
    Write-Host "正在启动前端服务..."
    Write-Host "====================================="
    Write-Host "注意：此服务将在当前终端运行，按Ctrl+C可停止"
    
    Set-Location -Path ".\web"
    
    # 安装依赖
    Write-Host "安装前端依赖..."
    npm install
    
    # 启动前端服务
    npm run dev
    
    Set-Location -Path ".."
}

# 主程序
Write-Host "欢迎使用文姬项目Trae版服务管理脚本" -ForegroundColor Green

# 创建logs目录
if (-not (Test-Path -Path ".\logs")) {
    New-Item -ItemType Directory -Path ".\logs" | Out-Null
}

while ($true) {
    Show-Menu
    $choice = Read-Host "请选择操作"
    
    switch ($choice) {
        "1" {
            Check-MySQL
            Check-Ports
            Check-Processes
            Read-Host "按Enter键返回菜单"
        }
        "2" {
            Init-Database
            Read-Host "按Enter键返回菜单"
        }
        "3" {
            Start-JavaService
            Read-Host "按Enter键返回菜单"
        }
        "4" {
            Start-PythonService
            Read-Host "按Enter键返回菜单"
        }
        "5" {
            Start-FrontendService
            Read-Host "按Enter键返回菜单"
        }
        "6" {
            Check-Ports
            Read-Host "按Enter键返回菜单"
        }
        "7" {
            Check-Processes
            Read-Host "按Enter键返回菜单"
        }
        "0" {
            Write-Host "感谢使用文姬项目服务管理脚本" -ForegroundColor Green
            break
        }
        default {
            Write-Host "无效的选择，请重新输入" -ForegroundColor Red
            Read-Host "按Enter键继续"
        }
    }
    
    if ($choice -eq "0") {
        break
    }
}