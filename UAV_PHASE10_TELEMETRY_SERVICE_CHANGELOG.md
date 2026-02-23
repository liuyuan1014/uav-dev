# UAV Phase 10: Telemetry Service 变更日志

## 概述
本阶段完成了遥测服务（service-telemetry）的微服务化迁移工作，将原有的遥测数据处理功能从单体服务迁移到独立的微服务模块。

## 变更时间
2026-02-24

## 变更内容

### 1. 模块结构创建

#### 1.1 创建service-telemetry模块
- **位置**: `uav-services/service-telemetry/`
- **说明**: 新建独立的遥测服务模块

#### 1.2 Maven配置
- **文件**: [`uav-services/service-telemetry/pom.xml`](uav-services/service-telemetry/pom.xml:1)
- **依赖项**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data MongoDB (MongoDB支持)
  - Spring Boot Starter Data Redis (Redis支持)
  - Spring Kafka (Kafka消费者支持)
  - Nacos Discovery (服务注册)
  - Nacos Config (配置中心)
  - Jackson Databind (JSON序列化)
  - Lombok

### 2. 配置文件

#### 2.1 Bootstrap配置
- **文件**: [`uav-services/service-telemetry/src/main/resources/bootstrap.yml`](uav-services/service-telemetry/src/main/resources/bootstrap.yml:1)
- **配置内容**:
  - 服务名称: `service-telemetry`
  - Nacos配置中心地址
  - 配置文件扩展名: yaml

#### 2.2 Application配置
- **文件**: [`uav-services/service-telemetry/src/main/resources/application.yml`](uav-services/service-telemetry/src/main/resources/application.yml:1)
- **配置内容**:
  - 服务端口: 8086
  - MongoDB配置 (数据库: uav_telemetry)
  - Redis配置
  - Kafka配置 (消费者组: telemetry-persistence-group)
  - Nacos服务注册配置

### 3. 核心代码迁移

#### 3.1 启动类
- **文件**: [`uav-services/service-telemetry/src/main/java/com/uav/telemetry/ServiceTelemetryApplication.java`](uav-services/service-telemetry/src/main/java/com/uav/telemetry/ServiceTelemetryApplication.java:1)
- **包名**: `com.uav.telemetry`
- **注解**:
  - `@SpringBootApplication`
  - `@EnableDiscoveryClient` (启用服务注册)
  - `@EnableMongoRepositories` (启用MongoDB仓库)

#### 3.2 实体类
- **文件**: [`uav-services/service-telemetry/src/main/java/com/uav/telemetry/entity/UavTelemetryEntity.java`](uav-services/service-telemetry/src/main/java/com/uav/telemetry/entity/UavTelemetryEntity.java:1)
- **原始位置**: `uav-service/src/main/java/com/uav/service/telemetry/UavTelemetryEntity.java`
- **包名变更**: `com.uav.service.telemetry` → `com.uav.telemetry.entity`
- **功能**: MongoDB文档实体，存储无人机遥测历史数据
- **特性**:
  - 使用复合索引 (deviceId + timestamp) 实现幂等性
  - 防止同一设备在同一时间戳的数据重复插入

#### 3.3 Repository接口
- **文件**: [`uav-services/service-telemetry/src/main/java/com/uav/telemetry/repository/UavTelemetryRepository.java`](uav-services/service-telemetry/src/main/java/com/uav/telemetry/repository/UavTelemetryRepository.java:1)
- **原始位置**: `uav-service/src/main/java/com/uav/service/telemetry/UavTelemetryRepository.java`
- **包名变更**: `com.uav.service.telemetry` → `com.uav.telemetry.repository`
- **功能**: MongoDB数据访问接口
- **方法**:
  - `findByDeviceId()`: 根据设备ID查询所有数据
  - `findByDeviceIdOrderByTimestampDesc()`: 根据设备ID倒序查询
  - `findByDeviceIdAndTimestampBetweenOrderByTimestampAsc()`: 时间范围查询

#### 3.4 Kafka消费者
- **文件**: [`uav-services/service-telemetry/src/main/java/com/uav/telemetry/consumer/UavTelemetryConsumer.java`](uav-services/service-telemetry/src/main/java/com/uav/telemetry/consumer/UavTelemetryConsumer.java:1)
- **原始位置**: `uav-service/src/main/java/com/uav/service/telemetry/UavTelemetryConsumer.java`
- **包名变更**: `com.uav.service.telemetry` → `com.uav.telemetry.consumer`
- **功能**: 消费Kafka遥测数据并持久化
- **特性**:
  - 监听主题: `uav-telemetry`
  - 消费者组: `telemetry-persistence-group`
  - 双重存储策略:
    - Redis: 存储实时状态 (覆盖写入，天然幂等)
    - MongoDB: 存储历史记录 (唯一索引防重)
  - 幂等性处理: 捕获DuplicateKeyException防止重复数据

#### 3.5 配置类
- **文件**: [`uav-services/service-telemetry/src/main/java/com/uav/telemetry/config/TelemetryConfig.java`](uav-services/service-telemetry/src/main/java/com/uav/telemetry/config/TelemetryConfig.java:1)
- **功能**: Redis和ObjectMapper配置
- **Bean配置**:
  - `RedisTemplate<String, Object>`: Redis操作模板
    - Key序列化: StringRedisSerializer
    - Value序列化: GenericJackson2JsonRedisSerializer
  - `ObjectMapper`: JSON序列化工具
    - 禁用日期时间戳序列化

### 4. 父POM更新
- **文件**: [`uav-services/pom.xml`](uav-services/pom.xml:26)
- **变更**: 取消注释service-telemetry模块
- **变更前**: `<!-- <module>service-telemetry</module> -->`
- **变更后**: `<module>service-telemetry</module>`

## 技术架构

### 数据流程
```
Kafka (uav-telemetry) 
    ↓
UavTelemetryConsumer
    ↓
├─→ Redis (实时状态)
│   ├─ uav:status:{deviceId} (最新状态)
│   └─ uav:locations (Geo坐标)
│
└─→ MongoDB (历史记录)
    └─ telemetry_history (带唯一索引)
```

### 幂等性保证
1. **Redis层**: 覆盖写入，天然幂等
2. **MongoDB层**: 复合唯一索引 (deviceId + timestamp)
3. **Kafka层**: 手动确认机制，失败自动重试

### 服务端口分配
- service-location: 8081
- service-mission: 8082
- service-dispatch: 8083
- service-pilot: 8084
- service-device: 8085
- **service-telemetry: 8086** ✨ 新增

## 编译测试

### 测试命令
```bash
cd uav-services/service-telemetry
mvn clean compile
```

### 测试结果
- ✅ 编译成功
- ✅ 5个源文件编译通过
- ✅ 资源文件复制成功
- ⚠️ 存在unchecked警告（泛型类型转换，不影响功能）

## 依赖关系

### 上游依赖
- uav-model (统一模型层)
- Spring Cloud Alibaba (Nacos)
- Spring Boot Data MongoDB
- Spring Boot Data Redis
- Spring Kafka

### 下游服务
- 无（遥测服务为数据消费端，不被其他服务直接调用）

## 数据库要求

### MongoDB
- **数据库名**: `uav_telemetry`
- **集合**: `telemetry_history`
- **索引**: 
  - 复合唯一索引: `{deviceId: 1, timestamp: 1}`
  - 单字段索引: `{deviceId: 1}`

### Redis
- **Key模式**:
  - `uav:status:{deviceId}`: 设备最新状态
  - `uav:locations`: Geo地理位置集合

## 注意事项

1. **MongoDB连接**: 确保MongoDB服务运行在 `localhost:27017`
2. **Redis连接**: 确保Redis服务运行在 `localhost:6379`
3. **Kafka连接**: 确保Kafka服务运行在 `localhost:9092`
4. **Nacos注册**: 确保Nacos服务运行在 `localhost:8848`
5. **数据幂等**: 依赖MongoDB唯一索引，首次启动需确保索引创建成功
6. **消息确认**: 使用手动确认模式，确保数据可靠性

## 后续工作

1. ✅ 完成service-telemetry基础架构
2. ⏳ 添加遥测数据查询API
3. ⏳ 实现数据统计分析功能
4. ⏳ 添加数据清理策略（历史数据归档）
5. ⏳ 性能优化和监控

## 相关文档
- [UAV Phase 8: Device Service](UAV_PHASE8_DEVICE_SERVICE_CHANGELOG.md)
- [UAV Phase 9: Account Service](UAV_PHASE9_ACCOUNT_SERVICE_CHANGELOG.md)
- [UAV微服务架构设计](UAV_MICROSERVICE_ARCHITECTURE_DESIGN.md)

---
**变更完成时间**: 2026-02-24  
**变更人**: Roo  
**状态**: ✅ 已完成