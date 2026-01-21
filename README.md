# 工业园区无人机任务调度平台

## 项目概述
这是一个基于微服务架构的工业园区无人机任务调度平台，采用Spring Cloud + Dubbo的技术栈，具有高并发、低延迟的特性，专门用于处理工业园区内的无人机调度任务。

## 技术栈
- **JDK**: 17
- **Spring Boot**: 3.0.5
- **Spring Cloud**: 2022.0.2
- **Spring Cloud Alibaba**: 2022.0.0.0-RC2
- **Dubbo**: 3.2.0
- **MyBatis Plus**: 3.5.3.1
- **MySQL**: 8.0.30
- **Redisson**: 3.23.3
- **Netty**: 4.1.90.Final
- **Kafka**: 与Spring Boot集成

## 项目模块结构

### 1. uav-dispatch-platform (父工程)
- 统一管理所有依赖版本
- 定义基础配置和插件

### 2. uav-common (通用工具模块)
- 提供通用工具类和基础组件
- 包含Lombok、FastJSON、Joda-Time等基础依赖

### 3. uav-api (接口定义模块)
- 定义Dubbo RPC接口和DTO
- 仅依赖uav-common，不包含Web或数据库相关依赖

### 4. uav-gateway-iot (设备接入网关)
- 基于Netty处理无人机TCP长连接
- 依赖Netty、Kafka和uav-api
- 将无人机数据直接推送到Kafka

### 5. uav-service (业务服务聚合)
- 业务逻辑的核心处理层
- 依赖MyBatis Plus、MySQL、Redisson、Kafka和uav-api
- 实现任务调度、路径规划等核心业务

## 版本依赖管理
所有版本号在父工程的properties中统一定义：
- spring-boot.version: 3.0.5
- spring-cloud.version: 2022.0.2
- dubbo.version: 3.2.0
- netty.version: 4.1.90.Final
- mybatis-plus.version: 3.5.3.1
- mysql.version: 8.0.30
- redisson.version: 3.23.3
- fastjson.version: 2.0.41
- kafka.version: 3.0.5

## 模块依赖关系
```
uav-dispatch-platform (父工程)
├── uav-common (工具模块)
├── uav-api (接口定义模块)
│   └── uav-common
├── uav-gateway-iot (设备接入网关)
│   ├── uav-api
│   └── spring-boot-starter
├── uav-service (业务服务聚合)
│   ├── uav-api
│   ├── spring-boot-starter-web
│   ├── mybatis-plus-boot-starter
│   ├── mysql-connector-java
│   ├── redisson-spring-boot-starter
│   └── spring-kafka
```

## 开发说明
1. 所有模块均使用JDK 17编译
2. RPC调用使用Dubbo协议
3. 数据传输格式使用JSON
4. 消息队列使用Kafka进行异步处理
5. 缓存使用Redisson
6. 数据库使用MySQL