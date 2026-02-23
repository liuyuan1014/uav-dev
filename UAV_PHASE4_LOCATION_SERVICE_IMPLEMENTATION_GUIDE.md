# service-location 位置服务实施指南

## 📋 概述

**目标**: 将 location 相关代码从 uav-service 拆分为独立的 service-location 微服务

**优先级**: 高（无外部服务依赖，最简单）

**预计时间**: 2-3小时

---

## 📂 文件清单

### 需要迁移的文件 (3个)

1. **Controller** (1个)
   - `uav-service/src/main/java/com/uav/service/location/controller/LocationController.java`

2. **Service** (2个)
   - `uav-service/src/main/java/com/uav/service/location/service/PilotLocationService.java`
   - `uav-service/src/main/java/com/uav/service/location/service/impl/PilotLocationServiceImpl.java`

### 需要临时复制的文件 (1个)

- `uav-service/src/main/java/com/uav/service/mapper/UavPilotMapper.java`
  - **原因**: PilotLocationServiceImpl 需要查询飞手信息
  - **标记**: `@Deprecated // TODO: 阶段十一改为Feign调用`

### 需要创建的文件

1. **pom.xml** - Maven配置
2. **ServiceLocationApplication.java** - 启动类
3. **application.yml** - 应用配置
4. **bootstrap.yml** - Nacos配置

---

## 🔧 实施步骤

### 步骤1: 创建模块结构

#### 1.1 创建目录
```bash
mkdir -p uav-services/service-location/src/main/java/com/uav/location
mkdir -p uav-services/service-location/src/main/resources
```

#### 1.2 创建 pom.xml

**路径**: `uav-services/service-location/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>uav-services</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>service-location</artifactId>
    <name>service-location</name>
    <description>UAV位置服务</description>

    <dependencies>
        <!-- 无需额外依赖，父模块已包含所有必要依赖 -->
    </dependencies>

    <build>
        <finalName>service-location</finalName>
    </build>
</project>
```

---

### 步骤2: 创建启动类

**路径**: `uav-services/service-location/src/main/java/com/uav/location/ServiceLocationApplication.java`

```java
package com.uav.location;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 位置服务启动类
 * 
 * @author Roo
 * @date 2026-02-23
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.uav.location.mapper")
public class ServiceLocationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceLocationApplication.class, args);
    }
}
```

---

### 步骤3: 迁移 Controller

**源文件**: `uav-service/src/main/java/com/uav/service/location/controller/LocationController.java`

**目标**: `uav-services/service-location/src/main/java/com/uav/location/controller/LocationController.java`

**修改内容**:
```java
// 修改包名
package com.uav.location.controller;  // 原: com.uav.service.location.controller

// 修改import
import com.uav.location.service.PilotLocationService;  // 原: com.uav.service.location.service
import com.uav.model.form.location.UpdateLocationForm;  // 使用uav-model
import com.uav.model.vo.location.PilotLocationVo;      // 使用uav-model
import com.uav.model.vo.location.NearbyPilotVo;        // 使用uav-model

// 其他代码保持不变
```

---

### 步骤4: 迁移 Service 接口

**源文件**: `uav-service/src/main/java/com/uav/service/location/service/PilotLocationService.java`

**目标**: `uav-services/service-location/src/main/java/com/uav/location/service/PilotLocationService.java`

**修改内容**:
```java
// 修改包名
package com.uav.location.service;  // 原: com.uav.service.location.service

// 修改import
import com.uav.model.form.location.UpdateLocationForm;  // 使用uav-model
import com.uav.model.vo.location.PilotLocationVo;      // 使用uav-model
import com.uav.model.vo.location.NearbyPilotVo;        // 使用uav-model

// 其他代码保持不变
```

---

### 步骤5: 迁移 Service 实现类

**源文件**: `uav-service/src/main/java/com/uav/service/location/service/impl/PilotLocationServiceImpl.java`

**目标**: `uav-services/service-location/src/main/java/com/uav/location/service/impl/PilotLocationServiceImpl.java`

**修改内容**:
```java
// 修改包名
package com.uav.location.service.impl;  // 原: com.uav.service.location.service.impl

// 修改import
import com.uav.location.service.PilotLocationService;  // 原: com.uav.service.location.service
import com.uav.location.mapper.UavPilotMapper;         // 原: com.uav.service.mapper
import com.uav.model.form.location.UpdateLocationForm; // 使用uav-model
import com.uav.model.vo.location.PilotLocationVo;     // 使用uav-model
import com.uav.model.vo.location.NearbyPilotVo;       // 使用uav-model
import com.uav.model.entity.pilot.UavPilot;           // 使用uav-model

// 其他代码保持不变
```

---

### 步骤6: 临时复制 UavPilotMapper

**源文件**: `uav-service/src/main/java/com/uav/service/mapper/UavPilotMapper.java`

**目标**: `uav-services/service-location/src/main/java/com/uav/location/mapper/UavPilotMapper.java`

**修改内容**:
```java
package com.uav.location.mapper;  // 修改包名

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.pilot.UavPilot;  // 使用uav-model
import org.apache.ibatis.annotations.Mapper;

/**
 * 飞手Mapper（临时）
 * 
 * @Deprecated TODO: 阶段十一改为Feign调用 service-pilot
 */
@Deprecated
@Mapper
public interface UavPilotMapper extends BaseMapper<UavPilot> {
    // 保持原有方法
}
```

---

### 步骤7: 创建配置文件

#### 7.1 application.yml

**路径**: `uav-services/service-location/src/main/resources/application.yml`

```yaml
server:
  port: 8205

spring:
  application:
    name: service-location
  
  # Redis配置（位置服务的核心依赖）
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    database: 0
    password: ${REDIS_PASSWORD:}
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
  
  # 数据源配置（临时，用于查询飞手信息）
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/uav_pilot?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:your_password}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  # Nacos服务注册
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_ADDR:101.42.103.115:8848}
        namespace: ${NACOS_NAMESPACE:dev}
        group: DEFAULT_GROUP
        service: ${spring.application.name}

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.uav.model.entity.pilot
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 日志配置
logging:
  level:
    com.uav.location: INFO
    com.uav.location.mapper: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n"

# Actuator配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

#### 7.2 bootstrap.yml

**路径**: `uav-services/service-location/src/main/resources/bootstrap.yml`

```yaml
spring:
  application:
    name: service-location
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_ADDR:101.42.103.115:8848}
        namespace: ${NACOS_NAMESPACE:dev}
        file-extension: yml
        group: DEFAULT_GROUP
        refresh-enabled: true
```

---

### 步骤8: 更新父模块 pom.xml

**文件**: `uav-services/pom.xml`

**添加模块**:
```xml
<modules>
    <module>service-location</module>
    <!-- 其他模块 -->
</modules>
```

---

## 🧪 验证步骤

### 1. 编译验证
```bash
cd uav-services/service-location
mvn clean compile
```

**预期结果**: BUILD SUCCESS

### 2. 启动验证
```bash
mvn spring-boot:run
```

**检查项**:
- ✅ 服务成功启动
- ✅ 端口 8205 监听正常
- ✅ Nacos 注册成功
- ✅ Redis 连接正常

### 3. API 测试

#### 3.1 更新位置
```bash
curl -X POST http://localhost:8205/location/update \
  -H "Content-Type: application/json" \
  -d '{
    "pilotId": 1,
    "latitude": 23.1291,
    "longitude": 113.2644,
    "accuracy": 10.0
  }'
```

#### 3.2 获取位置
```bash
curl http://localhost:8205/location/get/1
```

#### 3.3 搜索附近飞手
```bash
curl "http://localhost:8205/location/nearby?latitude=23.1291&longitude=113.2644&radiusKm=5.0&limit=10"
```

---

## ⚠️ 注意事项

### 1. 包名变更规则
```
com.uav.service.location.* → com.uav.location.*
```

### 2. Import 语句调整
- ✅ 使用 `com.uav.model.*` 引用实体、VO、Form
- ✅ 使用 `com.uav.common.*` 引用公共类
- ✅ 使用 `com.uav.location.*` 引用本服务的类

### 3. 临时依赖说明
- `UavPilotMapper` 是临时复制的
- 标记为 `@Deprecated`
- 在阶段十一会改为 Feign 调用 service-pilot

### 4. Redis Key 管理
使用 `RedisConstant` 中定义的常量:
- `PILOT_GEO_KEY` - 飞手位置GEO数据
- `PILOT_LOCATION_DETAIL_KEY` - 飞手位置详细信息

### 5. 数据库连接
- 临时连接 `uav_pilot` 数据库
- 仅用于查询飞手基本信息
- 后续改为 Feign 调用

---

## 📊 依赖关系

### 当前依赖
```
service-location
  ├── uav-model (统一模型层)
  ├── uav-common (公共工具)
  ├── Redis (位置存储)
  └── MySQL (临时，查询飞手信息)
```

### 最终依赖（阶段十一后）
```
service-location
  ├── uav-model (统一模型层)
  ├── uav-common (公共工具)
  ├── Redis (位置存储)
  └── service-pilot-client (Feign调用)
```

---

## 🎯 完成标准

- [x] 模块可以独立编译
- [x] 服务可以独立启动
- [x] Nacos 注册成功
- [x] Redis 连接正常
- [x] 所有 API 接口正常工作
- [x] 位置更新功能正常
- [x] 附近飞手搜索正常
- [x] 距离计算正常

---

## 📝 后续工作

1. **阶段五**: 拆分 service-mission
2. **阶段六**: 拆分 service-dispatch
3. **阶段十一**: 创建 service-location-client
4. **阶段十二**: 将 UavPilotMapper 改为 Feign 调用

---

**文档版本**: v1.0  
**创建时间**: 2026-02-23  
**预计完成时间**: 2-3小时