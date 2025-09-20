@echo off
chcp 65001 > nul

REM 设置环境变量
set SERVER_PORT=8000

REM 检查是否已安装Python依赖
pip install -r requirements.txt

REM 启动FastAPI服务
python -m uvicorn main:app --host 0.0.0.0 --port %SERVER_PORT% --reload

pause