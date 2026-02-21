# SSH 隧道配置指南

## 📋 概述

本文档说明如何使用 SSH 隧道将远程服务器的端口映射到本地，以便在本地开发环境中访问远程服务。

## 🎯 映射的服务

| 服务 | 远程端口 | 本地端口 | 说明 |
|------|---------|---------|------|
| MySQL | 3306 | 3306 | 数据库服务 |
| Redis | 6379 | 6379 | 缓存和分布式锁 |
| Nacos HTTP | 8848 | 8848 | 配置中心和服务注册（HTTP API） |
| Nacos gRPC | 9848 | 9848 | 配置中心和服务注册（gRPC） |
| Sentinel | 8080 | 8080 | 流量控制和熔断降级 |

## 🚀 快速开始

### 0. 检查端口占用（重要！）

在启动隧道前，建议先检查端口是否被占用：

```batch
check-and-kill-ports.bat
```

这个脚本会：
- 检查所有需要的端口（3306, 6379, 8848, 9848, 8080）
- 显示占用端口的进程
- 提供选项终止占用端口的进程

### 1. 启动所有隧道

双击运行 `start-all-tunnels.bat`，或在命令行执行：

```batch
start-all-tunnels.bat
```

**重要提示：**
- ⚠️ 启动后请保持窗口打开，关闭窗口将断开所有隧道
- ✅ 看到 "Welcome to Ubuntu" 等信息表示连接成功
- ❌ 如果出现连接错误，请检查网络和 SSH 密钥配置

### 2. 验证隧道状态

打开新的命令行窗口，测试各服务连接：

```batch
# 测试 MySQL
telnet localhost 3306

# 测试 Redis
telnet localhost 6379

# 测试 Nacos
curl http://localhost:8848/nacos

# 测试 Sentinel
curl http://localhost:8080
```

### 3. 停止所有隧道

双击运行 `stop-all-tunnels.bat`，或在命令行执行：

```batch
stop-all-tunnels.bat
```

## ⚙️ 配置说明

### SSH 连接信息

- **服务器地址**: `101.42.103.115`
- **用户名**: `ubuntu`
- **认证方式**: SSH 密钥
- **密钥路径**: `C:\Users\ChenSIMI\.ssh\id_rsa`

### 保活机制

脚本配置了以下参数确保连接稳定：

```
ServerAliveInterval=60    # 每 60 秒发送心跳包
ServerAliveCountMax=3     # 最多 3 次心跳失败后断开
```

## 📝 应用配置

### bootstrap.yml 配置

确保 `uav-service/src/main/resources/bootstrap.yml` 和 `uav-gateway-iot/src/main/resources/bootstrap.yml` 中的服务地址使用 `localhost`：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # 使用 localhost
      config:
        server-addr: localhost:8848  # 使用 localhost
    sentinel:
      transport:
        dashboard: localhost:8080    # 使用 localhost
```

### Nacos 配置中心

在 Nacos 配置中心的配置文件中，确保以下服务地址使用 `localhost`：

```yaml
# Redis 配置
spring:
  redis:
    host: localhost
    port: 6379

# MySQL 配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/uav_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
```

## 🔧 故障排查

### 问题 1: 连接被拒绝

**症状**: `Connection refused` 或 `Could not connect to server`

**解决方案**:
1. 检查 SSH 隧道是否正在运行
2. 验证 SSH 密钥权限是否正确
3. 确认远程服务器防火墙规则

### 问题 2: 端口已被占用

**症状**: `bind: Address already in use`

**解决方案**:
1. 运行 `stop-all-tunnels.bat` 停止现有隧道
2. 检查是否有其他程序占用相同端口：
   ```batch
   netstat -ano | findstr "3306"
   netstat -ano | findstr "6379"
   netstat -ano | findstr "8848"
   netstat -ano | findstr "8080"
   ```
3. 如有冲突，终止占用端口的进程或修改配置使用其他端口

### 问题 3: 隧道意外断开

**症状**: 隧道窗口自动关闭或显示断开消息

**解决方案**:
1. 检查网络连接是否稳定
2. 验证 SSH 密钥是否有效
3. 查看服务器日志确认是否有安全策略限制
4. 重新运行 `start-all-tunnels.bat`

### 问题 4: 无法访问 Nacos 控制台

**症状**: 浏览器访问 `http://localhost:8848/nacos` 失败

**解决方案**:
1. 确认 SSH 隧道正在运行
2. 检查远程 Nacos 服务是否正常：
   ```batch
   ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" ubuntu@101.42.103.115 "systemctl status nacos"
   ```
3. 验证本地防火墙是否阻止了 8848 端口

## 📚 高级用法

### 单独启动某个服务的隧道

如果只需要某个服务，可以手动执行：

```batch
# 仅启动 Redis 隧道
ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" -L 6379:localhost:6379 ubuntu@101.42.103.115 -N

# 仅启动 MySQL 隧道
ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" -L 3306:localhost:3306 ubuntu@101.42.103.115 -N

# 仅启动 Nacos 隧道
ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" -L 8848:localhost:8848 ubuntu@101.42.103.115 -N
```

### 后台运行隧道

如果希望隧道在后台运行（不显示窗口），可以使用：

```batch
start /B ssh -i "C:\Users\ChenSIMI\.ssh\id_rsa" ^
    -L 3306:localhost:3306 ^
    -L 6379:localhost:6379 ^
    -L 8848:localhost:8848 ^
    -L 8080:localhost:8080 ^
    ubuntu@101.42.103.115 -N
```

### 查看当前运行的隧道

```batch
tasklist | findstr ssh.exe
```

## 🔐 安全建议

1. **保护 SSH 密钥**: 确保 `id_rsa` 文件权限正确，不要分享给他人
2. **使用强密码**: 如果 SSH 密钥有密码保护，请使用强密码
3. **定期更新**: 定期更新 SSH 密钥和服务器安全补丁
4. **限制访问**: 仅在需要时启动隧道，不使用时及时关闭
5. **监控日志**: 定期检查服务器 SSH 登录日志

## 📞 支持

如遇到问题，请检查：
1. SSH 密钥配置是否正确
2. 网络连接是否稳定
3. 远程服务器服务是否正常运行
4. 本地防火墙设置

## 📄 相关文件

- `start-all-tunnels.bat` - 启动所有隧道的脚本（包含 MySQL, Redis, Nacos HTTP/gRPC, Sentinel）
- `stop-all-tunnels.bat` - 停止所有隧道的脚本
- `check-and-kill-ports.bat` - 检查并清理端口占用的脚本
- `start-redis-tunnel.bat` - 仅启动 Redis 隧道（已废弃，建议使用 start-all-tunnels.bat）
- `stop-redis-tunnel.bat` - 仅停止 Redis 隧道（已废弃，建议使用 stop-all-tunnels.bat）

## 🔄 更新日志

- **2026-02-21 v2**: 添加 Nacos gRPC 端口（9848）映射，创建端口检查脚本
- **2026-02-21 v1**: 创建完整的多服务隧道脚本，支持 MySQL、Redis、Nacos、Sentinel
- **初始版本**: 仅支持 Redis 单一服务隧道