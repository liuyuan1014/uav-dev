@echo off
chcp 65001 >nul
echo ========================================
echo    停止所有 SSH 隧道
echo ========================================
echo.
echo 正在查找并终止所有 SSH 隧道进程...
echo.

REM 查找所有包含端口转发的 SSH 进程并终止
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq ssh.exe" /NH 2^>nul') do (
    echo 终止进程 PID: %%i
    taskkill /PID %%i /F >nul 2>&1
)

echo.
echo ✅ 所有 SSH 隧道已停止
echo.
pause