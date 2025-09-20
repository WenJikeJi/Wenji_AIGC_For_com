@echo off
REM Python FastAPI startup batch file using ENGLISH only to avoid encoding issues

echo Installing Python dependencies...
py -m pip install fastapi uvicorn
py -m pip install -r requirements.txt

echo Starting FastAPI service...
py -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
echo Service started successfully. Press any key to exit...
pause > nul