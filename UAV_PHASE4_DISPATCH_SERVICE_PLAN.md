# 阶段四实施计划 - 拆分 service-dispatch 调度服务

## 📋 任务概述

**目标**: 将 `uav-service` 中的 dispatch 相关代码拆分为独立的 `service-dispatch` 微服务

**预计工作量**: 中等（dispatch 模块已有良好的子包结构）

---

## 🔍 现状分析

### 需要迁移的文件

#### 1. Controller层 (1个文件)
- `uav-service/src/main/java/com/uav/service/dispatch/controller/DispatchController.java`

#### 2. Service层 (2个文件)
- `uav-service/src/main/java/com/uav/service/dispatch/service/DispatchService.java`
- `uav-service/src/main/java/com/uav/service/dispatch/service/impl/DispatchServiceImpl.java`

#### 3. Job层 (1个文件)
- `uav-service/src/main/java/com/uav/service/dispatch/job/MissionDispatchJobHandler.java`

#### 4. Config层 (3个文件)
- `uav-service/src/main/java/com/uav/service/config/xxl/XxlJobConfig.java`
- `uav-service/src/main/java/com/uav/service/config/xxl/XxlJobClient.java`
- `uav-service/src/main/java/com/uav/service/config/xxl/XxlJobClientConfig.java`

#### 5. Mapper层 (2个文件)
- `uav-service/src/main/java/com/uav/service/mapper/MissionJobMapper.java`
- `uav-service/src/main/java/com/uav/service/mapper/XxlJobLogMapper.java`

#### 6. Mapper XML (2个文件)
- `uav-service/src/main/resources/mapper/MissionJobMapper.xml`
- `uav-service/src/main/resources/mapper/XxlJobLogMapper.xml`

### 依赖关系分析

**外部服务依赖**:
```
service-dispatch 依赖:
  → PilotLocationService (位置服务) - 查询附近飞手
  → UavMissionMapper (任务Mapper) - 查询任务信息
```

**解决方案**:
1. **临时方案**: 在 service-dispatch 中保留对 location 和 mission 相关代码的直接依赖
2. **最终方案**: 在阶段十一创建 service-client 后，改为通过 Feign 调用

---

## 📝 实施步骤

### 步骤1: 创建 service-dispatch 模块结构

#### 1.1 创建 pom.xml
```xml
位置: uav-services/service-dispatch/pom.xml
父模块: uav-services
依赖:
  - uav-model
  - uav-common
  - xxl-job-core
  - mybatis-plus
  - redis
  - 其他Spring Boot依赖
```

#### 1.2 创建目录结构
```
service-dispatch/
├── pom.xml
└── src/main/
    ├── java/com/uav/dispatch/
    │   ├── ServiceDispatchApplication.java  (启动类)
    │   ├── controller/
    │   │   └── DispatchController.java
    │   ├── service/
    │   │   ├── DispatchService.java
    │   │   └── impl/
    │   │       └── DispatchServiceImpl.java
    │   ├── job/
    │   │   └── MissionDispatchJobHandler.java
    │   ├── mapper/
    │   │   ├── MissionJobMapper.java
    │   │   ├── XxlJobLogMapper.java
    │   │   └── (临时) UavMissionMapper.java
    │   │   └── (临时) PilotLocationService.java
    │   └── config/
    │       └── xxl/
    │           ├── XxlJobConfig.java
    │           ├── XxlJobClient.java
    │           └── XxlJobClientConfig.java
    └── resources/
        ├── mapper/
        │   ├── MissionJobMapper.xml
        │   └── XxlJobLogMapper.xml
        ├── application.yml
        └── bootstrap.yml
```

### 步骤2: 迁移代码文件

#### 2.1 迁移 Controller
- 源: `com.uav.service.dispatch.controller.DispatchController`
- 目标: `com.uav.dispatch.controller.DispatchController`
- 修改: 包名、import语句

#### 2.2 迁移 Service
- 源: `com.uav.service.dispatch.service.*`
- 目标: `com.uav.dispatch.service.*`
- 修改: 包名、import语句

#### 2.3 迁移 Job Handler
- 源: `com.uav.service.dispatch.job.MissionDispatchJobHandler`
- 目标: `com.uav.dispatch.job.MissionDispatchJobHandler`
- 修改: 包名、import语句

#### 2.4 迁移 XXL-JOB 配置
- 源: `com.uav.service.config.xxl.*`
- 目标: `com.uav.dispatch.config.xxl.*`
- 修改: 包名

#### 2.5 迁移 Mapper
- 源: `com.uav.service.mapper.{MissionJobMapper, XxlJobLogMapper}`
- 目标: `com.uav.dispatch.mapper.*`
- 修改: 包名

#### 2.6 迁移 Mapper XML
- 源: `uav-service/src/main/resources/mapper/{MissionJobMapper, XxlJobLogMapper}.xml`
- 目标: `service-dispatch/src/main/resources/mapper/`
- 修改: namespace

### 步骤3: 处理临时依赖

由于 `DispatchServiceImpl` 依赖了其他服务的代码，需要临时处理：

#### 3.1 临时复制 PilotLocationService
```java
// 临时方案：复制接口和实现
// 位置: com.uav.dispatch.location.service.PilotLocationService
// 标记: @Deprecated // TODO: 阶段十一改为Feign调用
```

#### 3.2 临时复制 UavMissionMapper
```java
// 临时方案：复制Mapper接口
// 位置: com.uav.dispatch.mapper.UavMissionMapper
// 标记: @Deprecated // TODO: 阶段十一改为Feign调用
```

### 步骤4: 创建配置文件

#### 4.1 application.yml
```yaml
server:
  port: 8204

spring:
  application:
    name: service-dispatch
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/uav_dispatch?useUnicode=true&characterEncoding=utf-8
    username: root
    password: ${DB_PASSWORD}
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
  
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_ADDR:101.42.103.115:8848}
        namespace: ${NACOS_NAMESPACE:dev}

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.uav.model.entity

xxl:
  job:
    admin:
      addresses: http://localhost:8080/xxl-job-admin
    executor:
      appname: service-dispatch-executor
      port: 9999
```

#### 4.2 bootstrap.yml
```yaml
spring:
  application:
    name: service-dispatch
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_ADDR:101.42.103.115:8848}
        namespace: ${NACOS_NAMESPACE:dev}
        file-extension: yml
```

### 步骤5: 创建启动类

```java
package com.uav.dispatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.uav.dispatch.mapper")
public class ServiceDispatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDispatchApplication.class, args);
    }
}
```

### 步骤6: 更新父模块 pom.xml

在 `uav-services/pom.xml` 中添加:
```xml
<modules>
    <module>service-dispatch</module>
</modules>
```

### 步骤7: 编译验证

```bash
mvn clean compile -pl uav-services/service-dispatch
```

### 步骤8: 数据库准备

创建独立数据库 `uav_dispatch`:
```sql
CREATE DATABASE IF NOT EXISTS uav_dispatch 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

-- 迁移表结构
USE uav_dispatch;
-- mission_job 表
-- xxl_job_log 表
```

---

## ⚠️ 注意事项

### 1. 包名变更
所有代码的包名需要从 `com.uav.service.dispatch` 改为 `com.uav.dispatch`

### 2. Import 语句调整
- `com.uav.service.dispatch.*` → `com.uav.dispatch.*`
- `com.uav.service.config.xxl.*` → `com.uav.dispatch.config.xxl.*`
- `com.uav.service.mapper.*` → `com.uav.dispatch.mapper.*`
- Form 和 VO 已在 uav-model 中，使用 `com.uav.model.*`

### 3. 临时依赖处理
- PilotLocationService: 临时复制，标记 @Deprecated
- UavMissionMapper: 临时复制，标记 @Deprecated
- 在阶段十一改为 Feign 调用

### 4. XXL-JOB 配置
- 需要在 XXL-JOB Admin 中注册新的执行器
- 执行器名称: `service-dispatch-executor`
- 端口: 9999

### 5. Redis Key 前缀
保持与原来一致，使用 `RedisConstant` 中定义的常量

---

## ✅ 验收标准

1. ✅ service-dispatch 模块可以独立编译
2. ✅ service-dispatch 可以独立启动
3. ✅ 所有 API 接口可以正常访问
4. ✅ XXL-JOB 任务可以正常创建和执行
5. ✅ 派单功能正常工作
6. ✅ Redis 数据读写正常
7. ✅ 数据库操作正常

---

## 📊 预期成果

完成后的模块结构:
```
uav-services/
└── service-dispatch/
    ├── pom.xml
    └── src/main/
        ├── java/com/uav/dispatch/
        │   ├── ServiceDispatchApplication.java
        │   ├── controller/ (1个)
        │   ├── service/ (2个)
        │   ├── job/ (1个)
        │   ├── mapper/ (2个 + 2个临时)
        │   └── config/ (3个)
        └── resources/
            ├── mapper/ (2个XML)
            ├── application.yml
            └── bootstrap.yml
```

---

## 🔄 后续工作

完成 service-dispatch 拆分后:
1. 继续阶段五: 拆分 service-location
2. 在阶段十一: 创建 service-dispatch-client
3. 在阶段十二: 将临时依赖改为 Feign 调用

---

**计划制定时间**: 2026-02-23  
**预计完成时间**: 2026-02-23