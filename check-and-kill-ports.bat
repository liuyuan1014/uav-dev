@echo off
chcp 65001 >nul
echo ========================================
echo    检查并清理端口占用
echo ========================================
echo.
echo 正在检查以下端口的占用情况：
echo   - 3306 (MySQL)
echo   - 6379 (Redis)
echo   - 8848 (Nacos HTTP)
echo   - 9848 (Nacos gRPC)
echo   - 8080 (Sentinel)
echo.

set "PORTS=3306 6379 8848 9848 8080"

for %%p in (%PORTS%) do (
    echo.
    echo ========================================
    echo 检查端口 %%p
    echo ========================================
    netstat -ano | findstr ":%%p " | findstr "LISTENING"
    if errorlevel 1 (
        echo ✅ 端口 %%p 未被占用
    ) else (
        echo ⚠️  端口 %%p 已被占用
        echo.
        set /p "KILL=是否终止占用端口 %%p 的进程？(Y/N): "
        if /i "!KILL!"=="Y" (
            for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p " ^| findstr "LISTENING"') do (
                echo 正在终止进程 PID: %%a
                taskkill /PID %%a /F
            )
        )
    )
)

echo.
echo ========================================
echo 检查完成
echo ========================================
echo.
pause