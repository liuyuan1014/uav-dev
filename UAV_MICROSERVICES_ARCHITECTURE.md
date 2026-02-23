# UAV无人机调度平台 - 微服务架构文档

## 项目概述
UAV无人机调度平台已成功从单体应用重构为微服务架构，采用Spring Cloud Alibaba技术栈，实现了服务的独立部署、水平扩展和高可用性。

## 架构设计

### 整体架构图
```
┌─────────────────────────────────────────────────────────────┐
│                        客户端层                              │
│                  (Web/Mobile/IoT设备)                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      网关层 (Gateway)                        │
│                   uav-gateway-iot (8888)                     │
│              - Netty TCP服务器 (9999)                        │
│              - Dubbo服务消费者                               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   服务注册中心 (Nacos)                       │
│                      localhost:8848                          │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      微服务层 (Services)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Location     │  │ Mission      │  │ Dispatch     │      │
│  │ 位置服务      │  │ 任务服务      │  │ 调度服务      │      │
│  │ :8201/20881  │  │ :8202/20882  │  │ :8203/20883  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Pilot        │  │ Device       │  │ Account      │      │
│  │ 飞手服务      │  │ 设备服务      │  │ 账户服务      │      │
│  │ :8204/20884  │  │ :8205/20885  │  │ :8206/20886  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐                                           │
│  │ Telemetry    │                                           │
│  │ 遥测服务      │                                           │
│  │ :8207/20887  │                                           │
│  └──────────────┘                                           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      数据层 (Data)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ MySQL        │  │ Redis        │  │ MongoDB      │      │
│  │ 关系型数据库   │  │ 缓存/位置     │  │ 遥测数据      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐                        │
│  │ RocketMQ     │  │ XXL-Job      │                        │
│  │ 消息队列      │  │ 任务调度      │                        │
│  └──────────────┘  └──────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

## 模块结构

### 1. 核心模块

#### uav-model (统一模型层)
- **职责**: 定义所有领域实体、VO、Form等数据模型
- **依赖**: 无外部依赖，被所有服务依赖
- **包结构**:
  - `com.uav.model.entity.*` - 领域实体
  - `com.uav.model.vo.*` - 视图对象
  - `com.uav.model.form.*` - 表单对象

#### uav-service-clients (服务客户端)
- **职责**: 定义服务间RPC调用接口
- **子模块**:
  - `service-location-client` - 位置服务客户端
  - `service-mission-client` - 任务服务客户端
  - `service-dispatch-client` - 调度服务客户端
  - `service-pilot-client` - 飞手服务客户端
  - `service-device-client` - 设备服务客户端
  - `service-account-client` - 账户服务客户端
  - `service-telemetry-client` - 遥测服务客户端

### 2. 微服务模块

#### service-location (位置服务)
- **端口**: HTTP 8201, Dubbo 20881
- **职责**: 
  - 飞手位置管理
  - 附近飞手搜索
  - 在线状态管理
- **数据表**: `uav_pilot` (飞手基础信息)
- **技术栈**: Redis GEO, Redisson分布式锁

#### service-mission (任务服务)
- **端口**: HTTP 8202, Dubbo 20882
- **职责**:
  - 任务CRUD
  - 任务状态管理
  - 任务统计
- **数据表**: `uav_mission`, `mission_status_log`, `mission_monitor`, `mission_comment`, `mission_bill`
- **技术栈**: MyBatis-Plus, 乐观锁

#### service-dispatch (调度服务)
- **端口**: HTTP 8203, Dubbo 20883
- **职责**:
  - 智能派单
  - 派单记录管理
  - 派单统计
- **数据表**: `mission_job`, `xxl_job_log`
- **技术栈**: XXL-Job, Dubbo RPC

#### service-pilot (飞手服务)
- **端口**: HTTP 8204, Dubbo 20884
- **职责**:
  - 飞手信息管理
  - 认证审核
  - 飞手设置
- **数据表**: `uav_pilot`, `pilot_certification_audit`, `pilot_settings`, `pilot_login_log`
- **技术栈**: MyBatis-Plus

#### service-device (设备服务)
- **端口**: HTTP 8205, Dubbo 20885
- **职责**:
  - 设备管理
  - 设备控制
  - 设备查询
- **数据表**: `uav_device`
- **技术栈**: MyBatis-Plus, Dubbo RPC

#### service-account (账户服务)
- **端口**: HTTP 8206, Dubbo 20886
- **职责**:
  - 飞手账户管理
  - 账户明细
  - 余额管理
- **数据表**: `pilot_account`, `pilot_account_detail`
- **技术栈**: MyBatis-Plus, 分布式事务

#### service-telemetry (遥测服务)
- **端口**: HTTP 8207, Dubbo 20887
- **职责**:
  - 遥测数据采集
  - 遥测数据存储
  - 遥测数据查询
- **数据存储**: MongoDB
- **技术栈**: Spring Data MongoDB, RocketMQ

### 3. 网关模块

#### uav-gateway-iot
- **端口**: HTTP 8888, TCP 9999
- **职责**:
  - IoT设备接入
  - 协议转换
  - 消息路由
- **技术栈**: Netty, Dubbo, RocketMQ

## 服务间通信

### Dubbo RPC调用链路

#### 1. 调度服务 → 位置服务
```java
// service-dispatch调用service-location
@DubboReference
private LocationFeignClient locationFeignClient;

List<NearbyPilotVo> pilots = locationFeignClient.searchNearbyIdlePilots(
    longitude, latitude, radius, limit
);
```

#### 2. 飞手服务 → 任务服务
```java
// service-pilot调用service-mission
@DubboReference
private MissionFeignClient missionFeignClient;

Integer count = missionFeignClient.getCompletedMissionsCount(pilotId);
```

#### 3. 调度服务 → 任务服务
```java
// service-dispatch调用service-mission
@DubboReference
private MissionFeignClient missionFeignClient;

UavMission mission = missionFeignClient.getMissionById(missionId);
```

## 技术栈

### 核心框架
- **Spring Boot**: 2.7.x
- **Spring Cloud Alibaba**: 2021.0.x
- **Dubbo**: 3.2.x
- **Nacos**: 2.2.x

### 数据存储
- **MySQL**: 8.0+ (关系型数据)
- **Redis**: 6.0+ (缓存、位置数据)
- **MongoDB**: 4.4+ (遥测数据)

### 中间件
- **RocketMQ**: 5.1.x (消息队列)
- **XXL-Job**: 2.4.x (分布式任务调度)
- **Redisson**: 3.x (分布式锁)

### 工具库
- **MyBatis-Plus**: 3.5.x (ORM框架)
- **Hutool**: 5.8.x (工具类库)
- **Lombok**: 1.18.x (代码简化)

## 配置说明

### Nacos配置中心
- **地址**: localhost:8848
- **命名空间**: public
- **配置格式**: YAML

### Dubbo配置
- **注册中心**: Nacos
- **协议**: dubbo
- **端口范围**: 20881-20887
- **超时时间**: 3000ms

### 数据库配置
- **连接池**: HikariCP
- **最大连接数**: 20
- **最小空闲连接**: 5

## 部署架构

### 开发环境
- 所有服务单实例部署
- 使用本地Nacos、MySQL、Redis
- 日志级别: DEBUG

### 生产环境
- 每个服务至少2个实例
- 使用集群版Nacos、MySQL、Redis
- 日志级别: INFO
- 启用健康检查和熔断降级

## 监控与运维

### 健康检查
- Spring Boot Actuator
- Nacos健康检查
- 自定义健康指标

### 日志管理
- 统一日志格式
- 按服务分类存储
- 支持日志聚合查询

### 性能监控
- JVM监控
- 接口性能监控
- 数据库连接池监控

## 安全性

### 服务间认证
- Dubbo Token认证
- 服务白名单

### 数据安全
- 敏感数据加密
- SQL注入防护
- XSS攻击防护

## 扩展性

### 水平扩展
- 无状态服务设计
- 支持动态扩缩容
- 负载均衡

### 垂直扩展
- 服务拆分粒度合理
- 支持按需拆分
- 数据库分库分表

## 总结

本微服务架构实现了：
✅ 服务独立部署和扩展
✅ 服务间解耦，边界清晰
✅ 高可用和容错能力
✅ 统一的技术栈和规范
✅ 完善的监控和运维体系