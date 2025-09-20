@echo off
REM Python FastAPI startup batch file using ENGLISH only to avoid encoding issues

echo Installing Python dependencies...
"C:\Program Files\python\python.exe" -m pip install fastapi uvicorn
"C:\Program Files\python\python.exe" -m pip install -r requirements.txt

echo Starting FastAPI service...
"C:\Program Files\python\python.exe" -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
echo Service started successfully. Press any key to exit...
pause > nul