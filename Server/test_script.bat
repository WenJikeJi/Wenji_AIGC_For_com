@echo off
:: 最简单的批处理测试脚本

echo 测试脚本开始运行

echo 当前目录: %cd%

echo 检查Java环境...
java --version
if %ERRORLEVEL% NEQ 0 (
    echo 未找到Java
) else (
    echo Java环境正常
)

echo 测试脚本运行完毕
pause