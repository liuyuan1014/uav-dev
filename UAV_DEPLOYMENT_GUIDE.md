# UAV无人机调度平台 - 部署指南

## 环境要求

### 基础环境
- **JDK**: 17+
- **Maven**: 3.8+
- **Node.js**: 14+ (如有前端)
- **Git**: 2.0+

### 中间件
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **MongoDB**: 4.4+
- **Nacos**: 2.2.0+
- **RocketMQ**: 5.1.0+
- **XXL-Job Admin**: 2.4.0+

## 快速开始

### 1. 环境准备

#### 启动Nacos
```bash
# Windows
cd nacos/bin
startup.cmd -m standalone

# Linux/Mac
cd nacos/bin
sh startup.sh -m standalone
```
访问: http://localhost:8848/nacos (用户名/密码: nacos/nacos)

#### 启动MySQL
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE uav_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入表结构
mysql -u root -p uav_platform < migration_tables.sql
mysql -u root -p uav_platform < create_dispatch_tables.sql
mysql -u root -p uav_platform < add_pilot_certification_fields.sql
```

#### 启动Redis
```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server
```

#### 启动MongoDB
```bash
# Windows
mongod --dbpath D:\data\mongodb

# Linux/Mac
mongod --dbpath /data/mongodb
```

#### 启动RocketMQ
```bash
# Windows
cd rocketmq/bin
start mqnamesrv.cmd
start mqbroker.cmd -n localhost:9876

# Linux/Mac
cd rocketmq/bin
sh mqnamesrv &
sh mqbroker -n localhost:9876 &
```

#### 启动XXL-Job Admin
```bash
cd xxl-job-admin
# 修改application.properties配置数据库连接
java -jar xxl-job-admin-2.4.0.jar
```
访问: http://localhost:8080/xxl-job-admin (用户名/密码: admin/123456)

### 2. 编译项目

```bash
# 进入项目根目录
cd uav-dev

# 编译uav-model
cd uav-model
mvn clean install -DskipTests

# 编译uav-service-clients
cd ../uav-service-clients
mvn clean install -DskipTests

# 编译所有服务
cd ../uav-services
mvn clean package -DskipTests
```

### 3. 启动服务

#### 方式一：IDEA启动（推荐开发环境）
1. 导入项目到IDEA
2. 等待Maven依赖下载完成
3. 按顺序启动各个服务的Application类：
   - ServiceLocationApplication (8201)
   - ServiceMissionApplication (8202)
   - ServiceDispatchApplication (8203)
   - ServicePilotApplication (8204)
   - ServiceDeviceApplication (8205)
   - ServiceAccountApplication (8206)
   - ServiceTelemetryApplication (8207)
   - UavGatewayApplication (8888)

#### 方式二：命令行启动
```bash
# 启动service-location
cd uav-services/service-location
java -jar target/service-location-1.0.0-SNAPSHOT.jar

# 启动service-mission
cd ../service-mission
java -jar target/service-mission-1.0.0-SNAPSHOT.jar

# 启动service-dispatch
cd ../service-dispatch
java -jar target/service-dispatch-1.0.0-SNAPSHOT.jar

# 启动service-pilot
cd ../service-pilot
java -jar target/service-pilot-1.0.0-SNAPSHOT.jar

# 启动service-device
cd ../service-device
java -jar target/service-device-1.0.0-SNAPSHOT.jar

# 启动service-account
cd ../service-account
java -jar target/service-account-1.0.0-SNAPSHOT.jar

# 启动service-telemetry
cd ../service-telemetry
java -jar target/service-telemetry-1.0.0-SNAPSHOT.jar

# 启动gateway
cd ../../uav-gateway-iot
java -jar target/uav-gateway-iot-1.0.0-SNAPSHOT.jar
```

#### 方式三：Docker启动（推荐生产环境）
```bash
# 构建镜像
docker-compose build

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f service-location
```

### 4. 验证部署

#### 检查服务注册
访问Nacos控制台: http://localhost:8848/nacos
确认所有服务都已注册成功

#### 检查服务健康
```bash
# service-location
curl http://localhost:8201/actuator/health

# service-mission
curl http://localhost:8202/actuator/health

# service-dispatch
curl http://localhost:8203/actuator/health

# service-pilot
curl http://localhost:8204/actuator/health

# service-device
curl http://localhost:8205/actuator/health

# service-account
curl http://localhost:8206/actuator/health

# service-telemetry
curl http://localhost:8207/actuator/health
```

## 配置说明

### 数据库配置
修改各服务的`application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/uav_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
```

### MongoDB配置
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/uav_telemetry
```

### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
```

### Dubbo配置
```yaml
dubbo:
  registry:
    address: nacos://localhost:8848
  protocol:
    name: dubbo
    port: 20881  # 每个服务不同
```

## 生产环境部署

### 1. 服务器规划
```
┌─────────────────────────────────────────────┐
│ 负载均衡层 (Nginx/SLB)                       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 网关层 (2台)                                 │
│ uav-gateway-iot × 2                         │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 微服务层 (每个服务2-3台)                     │
│ service-location × 2                        │
│ service-mission × 2                         │
│ service-dispatch × 2                        │
│ service-pilot × 2                           │
│ service-device × 2                          │
│ service-account × 2                         │
│ service-telemetry × 2                       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ 数据层 (集群部署)                            │
│ MySQL主从 × 2                               │
│ Redis Cluster × 3                           │
│ MongoDB ReplicaSet × 3                      │
│ RocketMQ Cluster × 3                        │
└─────────────────────────────────────────────┘
```

### 2. JVM参数配置
```bash
java -Xms2g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/logs/heapdump.hprof \
     -Dspring.profiles.active=prod \
     -jar service-xxx.jar
```

### 3. 日志配置
```yaml
logging:
  level:
    root: INFO
    com.uav: INFO
  file:
    name: /logs/service-xxx.log
    max-size: 100MB
    max-history: 30
```

### 4. 监控配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## 常见问题

### 1. 服务启动失败
**问题**: 服务无法启动，报端口占用
**解决**: 
```bash
# Windows
netstat -ano | findstr "8201"
taskkill /PID <PID> /F

# Linux/Mac
lsof -i:8201
kill -9 <PID>
```

### 2. Nacos连接失败
**问题**: 服务无法注册到Nacos
**解决**: 
- 检查Nacos是否启动
- 检查网络连接
- 检查配置文件中的Nacos地址

### 3. Dubbo调用超时
**问题**: 服务间调用超时
**解决**:
```yaml
dubbo:
  consumer:
    timeout: 5000  # 增加超时时间
    retries: 2     # 设置重试次数
```

### 4. 数据库连接池耗尽
**问题**: 数据库连接池满
**解决**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30  # 增加连接池大小
      minimum-idle: 10
```

## 性能优化

### 1. 数据库优化
- 添加合适的索引
- 使用连接池
- 开启查询缓存
- 读写分离

### 2. Redis优化
- 使用连接池
- 设置合理的过期时间
- 使用Pipeline批量操作
- 使用Redis Cluster

### 3. 服务优化
- 启用Dubbo异步调用
- 使用本地缓存
- 合理设置线程池大小
- 启用压缩传输

### 4. JVM优化
- 选择合适的GC算法
- 调整堆内存大小
- 设置合理的GC参数
- 监控GC日志

## 备份与恢复

### 数据库备份
```bash
# 备份
mysqldump -u root -p uav_platform > backup_$(date +%Y%m%d).sql

# 恢复
mysql -u root -p uav_platform < backup_20260223.sql
```

### Redis备份
```bash
# 备份
redis-cli SAVE
cp /var/lib/redis/dump.rdb /backup/

# 恢复
cp /backup/dump.rdb /var/lib/redis/
redis-cli SHUTDOWN
redis-server
```

### MongoDB备份
```bash
# 备份
mongodump --db uav_telemetry --out /backup/

# 恢复
mongorestore --db uav_telemetry /backup/uav_telemetry/
```

## 安全建议

1. **修改默认密码**: 所有中间件的默认密码
2. **启用防火墙**: 只开放必要的端口
3. **使用HTTPS**: 生产环境启用SSL
4. **定期更新**: 及时更新依赖版本
5. **日志审计**: 记录关键操作日志
6. **限流降级**: 配置Sentinel规则

## 联系支持

如遇到问题，请联系技术支持团队。