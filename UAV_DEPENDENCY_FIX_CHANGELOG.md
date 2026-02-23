# UAV项目依赖修复日志

## 修复时间
2026-02-23

## 问题描述
在`uav-services/pom.xml`中错误地引入了OpenFeign依赖，与项目使用Dubbo进行服务间调用的架构设计不符。

## 修复内容

### 1. 移除错误依赖
从`uav-services/pom.xml`中移除：
```xml
<!-- OpenFeign (服务间调用) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 2. 添加正确依赖
在`uav-services/pom.xml`中添加：
```xml
<!-- Dubbo (服务间调用) -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
</dependency>

<!-- Dubbo Nacos Registry -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-registry-nacos</artifactId>
</dependency>
```

## 技术栈确认
- **服务间调用**: Apache Dubbo 3.2.0
- **服务注册与发现**: Nacos
- **RPC协议**: Dubbo协议
- **序列化**: Hessian2

## 影响范围
- 所有微服务模块（service-location, service-mission, service-dispatch, service-pilot, service-device, service-account, service-telemetry）
- 后续阶段十一将创建Dubbo接口模块（service-client）

## 验证
执行编译测试：
```bash
cd uav-services && mvn clean compile -DskipTests
```

## 备注
项目统一使用Dubbo进行服务间调用，不使用OpenFeign。