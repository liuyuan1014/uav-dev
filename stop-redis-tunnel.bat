@echo off
chcp 65001 >nul
echo ========================================
echo    停止 Redis SSH 隧道
echo ========================================
echo.
echo 正在查找 SSH 隧道进程...

for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq ssh.exe" /FO LIST ^| findstr /C:"PID:"') do (
    echo 找到 SSH 进程: PID %%a
    taskkill /PID %%a /F
)

echo.
echo ✅ SSH 隧道已停止
pause