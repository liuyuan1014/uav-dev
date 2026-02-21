@echo off
chcp 65001 >nul
echo ========================================
echo    启动所有服务 SSH 隧道
echo ========================================
echo.
echo 正在建立 SSH 隧道，映射以下服务：
echo   - MySQL:       localhost:3306  →  远程:3306
echo   - Redis:       localhost:6379  →  远程:6379
echo   - Nacos HTTP:  localhost:8848  →  远程:8848
echo   - Nacos gRPC:  localhost:9848  →  远程:9848
echo.
echo 注意：Sentinel (8080) 未映射，避免与网关端口冲突
echo.
echo 服务器: ubuntu@101.42.103.115
echo.
echo ⚠️  请保持此窗口打开，关闭窗口将断开所有隧道
echo.

ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" ^
    -L 3306:localhost:3306 ^
    -L 6379:localhost:6379 ^
    -L 8848:localhost:8848 ^
    -L 9848:localhost:9848 ^
    -o ServerAliveInterval=60 ^
    -o ServerAliveCountMax=3 ^
    ubuntu@101.42.103.115 ^
    -N

echo.
echo ❌ SSH 隧道已断开
pause