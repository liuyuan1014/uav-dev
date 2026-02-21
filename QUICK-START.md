# 🚀 快速启动指南

本指南帮助你快速启动无人机配送平台的开发环境。

## 📋 前置条件

- ✅ JDK 17+
- ✅ Maven 3.6+
- ✅ SSH 密钥已配置（`C:\Users\ChenSIMI\.ssh\id_rsa`）
- ✅ 网络可访问远程服务器 `101.42.103.115`

## 🎯 启动步骤

### 1️⃣ 启动 SSH 隧道

**双击运行** `start-all-tunnels.bat`

这将建立以下服务的端口映射：
- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Nacos: `localhost:8848`
- Sentinel: `localhost:8080`

⚠️ **重要**: 保持隧道窗口打开，不要关闭！

### 2️⃣ 验证服务连接

打开浏览器访问：
- **Nacos 控制台**: http://localhost:8848/nacos
  - 用户名: `nacos`
  - 密码: `nacos`
- **Sentinel 控制台**: http://localhost:8080

### 3️⃣ 启动网关服务

```bash
cd uav-gateway-iot
mvn spring-boot:run
```

或在 IDEA 中运行 [`UavGatewayApplication`](uav-gateway-iot/src/main/java/com/uav/gateway/UavGatewayApplication.java)

**验证**: 看到 "Netty Server started on port 9999" 表示启动成功

### 4️⃣ 启动业务服务

```bash
cd uav-service
mvn spring-boot:run
```

或在 IDEA 中运行 [`UavServiceApplication`](uav-service/src/main/java/com/uav/service/UavServiceApplication.java)

**验证**: 看到 "Started UavServiceApplication" 表示启动成功

### 5️⃣ 测试 API

使用 Postman 或 curl 测试：

```bash
# 查询设备列表
curl http://localhost:8081/api/uav/devices

# 发布任务
curl -X POST "http://localhost:8081/api/uav/mission/publish?title=测试任务&description=测试描述&reward=100.00"

# 查询任务列表
curl http://localhost:8081/api/uav/mission/list
```

## 🛠️ 常见问题

### Q1: 启动时报 "Connection refused"

**原因**: SSH 隧道未启动或已断开

**解决**: 
1. 检查 `start-all-tunnels.bat` 窗口是否还在运行
2. 如果已关闭，重新运行该脚本

### Q2: Nacos 连接失败

**原因**: Nacos 服务未启动或端口映射失败

**解决**:
1. 访问 http://localhost:8848/nacos 检查 Nacos 是否可访问
2. 检查 SSH 隧道日志是否有错误
3. 重启 SSH 隧道

### Q3: Redis 连接失败

**原因**: Redis 端口映射失败

**解决**:
1. 测试 Redis 连接: `telnet localhost 6379`
2. 检查 [`RedissonConfig`](uav-service/src/main/java/com/uav/service/config/RedissonConfig.java) 配置
3. 重启 SSH 隧道

### Q4: Dubbo 服务调用失败

**原因**: 服务未注册到 Nacos 或网络问题

**解决**:
1. 访问 Nacos 控制台检查服务是否注册
2. 检查 [`bootstrap.yml`](uav-service/src/main/resources/bootstrap.yml) 配置
3. 查看应用日志排查具体错误

## 📚 相关文档

- [SSH 隧道详细配置](README-SSH-TUNNEL.md)
- [任务模块开发文档](uav-service/README-MISSION.md)
- [网关开发文档](uav-gateway-iot/README.md)

## 🔄 停止服务

### 停止应用
在 IDEA 中点击停止按钮，或在命令行按 `Ctrl+C`

### 停止 SSH 隧道
双击运行 `stop-all-tunnels.bat`

## 💡 开发建议

1. **使用 IDEA 启动**: 更方便查看日志和调试
2. **保持隧道运行**: 开发期间始终保持 SSH 隧道窗口打开
3. **监控日志**: 注意观察 Nacos、Sentinel 的日志输出
4. **定期重启**: 如遇到奇怪问题，尝试重启服务和隧道

## 📞 获取帮助

如遇到问题：
1. 查看应用日志
2. 检查 SSH 隧道状态
3. 访问 Nacos 控制台查看服务状态
4. 查阅相关文档

---

**祝开发顺利！** 🎉