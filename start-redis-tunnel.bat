@echo off
chcp 65001 >nul
echo ========================================
echo    Redis SSH 隧道启动脚本
echo ========================================
echo.
echo 正在建立 SSH 隧道...
echo 服务器: ubuntu@101.42.103.115
echo 本地端口: 6379 → 远程端口: 6379
echo.
echo ⚠️  请保持此窗口打开，关闭窗口将断开隧道
echo.

ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" ^
    -L 6379:localhost:6379 ^
    -o ServerAliveInterval=60 ^
    -o ServerAliveCountMax=3 ^
    ubuntu@101.42.103.115 ^
    -N

echo.
echo ❌ SSH 隧道已断开
pause