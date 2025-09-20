@echo off
REM 简化版Python服务启动脚本

REM 设置服务器端口
SET SERVER_PORT=8000

REM 安装必要的依赖包
pip install fastapi==0.110.0 python-dotenv==1.0.1 uvicorn pyjwt

REM 运行简化版服务
python fixed_simple_main.py

REM 防止窗口关闭
pause