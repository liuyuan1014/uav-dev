# UAV项目微服务架构重构Prompt

## 📋 项目背景

当前UAV无人机调度平台存在严重的耦合问题，所有业务逻辑都集中在单一的`uav-service`模块中，导致：
- 代码职责不清晰，维护困难
- 无法独立部署和扩展
- 团队协作效率低
- 测试和调试复杂

参考代驾项目的优秀架构设计，需要将UAV项目重构为清晰的微服务架构。

---

## 🎯 重构目标

将当前的单体`uav-service`模块拆分为多个独立的微服务模块，实现：
1. **高内聚低耦合**：每个服务专注于单一业务领域
2. **独立部署**：各服务可独立开发、测试、部署
3. **清晰的依赖关系**：通过Feign Client实现服务间调用
4. **统一的模型层**：共享实体、VO、Form等数据模型

---

## 📊 当前架构分析

### 现有模块结构
```
uav-dispatch-platform/
├── uav-common/          # 公共工具类
├── uav-api/             # Dubbo API接口定义
├── uav-gateway-iot/     # IoT网关服务
└── uav-service/         # ❌ 单体服务（问题所在）
    ├── controller/      # 所有Controller混在一起
    ├── service/         # 所有Service混在一起
    ├── mapper/          # 所有Mapper混在一起
    ├── domain/          # 所有实体混在一起
    ├── dispatch/        # 调度相关（已有子包结构）
    ├── location/        # 位置相关（已有子包结构）
    ├── pilot/           # 飞手相关（已有子包结构）
    └── telemetry/       # 遥测相关
```

### 核心问题识别

1. **所有业务混在一个模块**
   - 任务管理、飞手管理、设备管理、账单管理等都在`uav-service`中
   - 无法独立部署和扩展

2. **实体类分散**
   - Domain实体直接放在service模块中
   - 缺少统一的model层管理

3. **缺少服务间调用接口**
   - 没有类似代驾项目的`service-client`层
   - 服务间调用直接依赖实现类

4. **Controller职责不清**
   - Controller直接放在根包下
   - 没有按业务领域分类

---

## 🏗️ 目标架构设计（参考代驾项目）

### 代驾项目架构（参考标准）
```
daijia-parent/
├── common/                    # 公共模块
├── model/                     # ✅ 统一模型层
│   └── src/main/java/com/atguigu/daijia/model/
│       ├── entity/           # 实体类（按业务分包）
│       │   ├── customer/
│       │   ├── driver/
│       │   ├── order/
│       │   ├── dispatch/
│       │   └── ...
│       ├── form/             # 表单对象
│       ├── vo/               # 视图对象
│       └── enums/            # 枚举类
├── service/                   # ✅ 微服务层（按业务拆分）
│   ├── service-customer/     # 客户服务
│   ├── service-driver/       # 司机服务
│   ├── service-order/        # 订单服务
│   ├── service-dispatch/     # 调度服务
│   ├── service-map/          # 地图服务
│   ├── service-payment/      # 支付服务
│   └── ...
├── service-client/            # ✅ 服务调用接口层
│   ├── service-customer-client/
│   ├── service-driver-client/
│   ├── service-order-client/
│   └── ...
├── server-gateway/           # 网关服务
└── web/                      # Web应用
```

### UAV项目目标架构
```
uav-dispatch-platform/
├── uav-common/               # 公共工具模块（保持不变）
├── uav-model/                # 🆕 统一模型层
│   └── src/main/java/com/uav/model/
│       ├── entity/           # 实体类
│       │   ├── mission/      # 任务相关实体
│       │   ├── pilot/        # 飞手相关实体
│       │   ├── device/       # 设备相关实体
│       │   ├── dispatch/     # 调度相关实体
│       │   ├── location/     # 位置相关实体
│       │   ├── account/      # 账户相关实体
│       │   └── telemetry/    # 遥测相关实体
│       ├── form/             # 表单对象
│       │   ├── mission/
│       │   ├── pilot/
│       │   ├── dispatch/
│       │   └── location/
│       ├── vo/               # 视图对象
│       │   ├── mission/
│       │   ├── pilot/
│       │   ├── dispatch/
│       │   └── location/
│       └── enums/            # 枚举类
│           ├── MissionStatus.java
│           ├── CancelReasonEnum.java
│           └── OperatorTypeEnum.java
├── service/                  # 🆕 微服务层
│   ├── service-mission/      # 任务服务
│   ├── service-pilot/        # 飞手服务
│   ├── service-device/       # 设备服务
│   ├── service-dispatch/     # 调度服务
│   ├── service-location/     # 位置服务
│   ├── service-account/      # 账户服务
│   └── service-telemetry/    # 遥测服务
├── service-client/           # 🆕 服务调用接口层
│   ├── service-mission-client/
│   ├── service-pilot-client/
│   ├── service-device-client/
│   ├── service-dispatch-client/
│   ├── service-location-client/
│   └── service-account-client/
├── uav-api/                  # Dubbo API（保持不变）
├── uav-gateway-iot/          # IoT网关（保持不变）
└── server-gateway/           # 🆕 API网关服务
```

---

## 📦 详细模块拆分方案

### 1. uav-model（统一模型层）

**职责**：管理所有数据模型，包括实体类、VO、Form、枚举等

**目录结构**：
```
uav-model/
├── pom.xml
└── src/main/java/com/uav/model/
    ├── entity/
    │   ├── base/
    │   │   └── BaseEntity.java
    │   ├── mission/
    │   │   ├── UavMission.java
    │   │   ├── MissionBill.java
    │   │   ├── MissionComment.java
    │   │   ├── MissionMonitor.java
    │   │   ├── MissionStatusLog.java
    │   │   └── MissionJob.java
    │   ├── pilot/
    │   │   ├── UavPilot.java
    │   │   ├── PilotAccount.java
    │   │   ├── PilotAccountDetail.java
    │   │   ├── PilotLoginLog.java
    │   │   ├── PilotSettings.java
    │   │   └── PilotCertificationAudit.java
    │   ├── device/
    │   │   └── UavDevice.java
    │   ├── dispatch/
    │   │   └── XxlJobLog.java
    │   └── telemetry/
    │       └── UavTelemetryEntity.java
    ├── form/
    │   ├── mission/
    │   │   ├── PublishMissionForm.java
    │   │   ├── AcceptMissionForm.java
    │   │   └── CompleteMissionForm.java
    │   ├── pilot/
    │   │   └── UpdatePilotAuthInfoForm.java
    │   ├── dispatch/
    │   │   └── StartDispatchForm.java
    │   └── location/
    │       └── UpdateLocationForm.java
    ├── vo/
    │   ├── mission/
    │   │   ├── MissionDetailVo.java
    │   │   └── MissionListVo.java
    │   ├── pilot/
    │   │   ├── PilotInfoVo.java
    │   │   └── PilotAuthInfoVo.java
    │   ├── dispatch/
    │   │   ├── DispatchRecordVo.java
    │   │   └── DispatchStatisticsVo.java
    │   ├── location/
    │   │   ├── PilotLocationVo.java
    │   │   └── NearbyPilotVo.java
    │   └── device/
    │       └── UavStatusVO.java
    └── enums/
        ├── MissionStatus.java
        ├── CancelReasonEnum.java
        ├── OperatorTypeEnum.java
        └── PilotCertificationStatus.java
```

**迁移内容**：
- 从`uav-service/src/main/java/com/uav/service/domain/`迁移所有实体类
- 从`uav-service/src/main/java/com/uav/service/*/form/`迁移所有Form类
- 从`uav-service/src/main/java/com/uav/service/*/vo/`迁移所有VO类
- 从`uav-service/src/main/java/com/uav/model/vo/`迁移VO类

---

### 2. service-mission（任务服务）

**职责**：管理无人机任务的完整生命周期

**核心功能**：
- 任务发布
- 任务接单（含分布式锁）
- 任务执行
- 任务完成
- 任务取消
- 任务查询
- 任务账单管理
- 任务评价管理
- 任务监控

**目录结构**：
```
service-mission/
├── pom.xml
└── src/main/java/com/uav/mission/
    ├── ServiceMissionApplication.java
    ├── controller/
    │   ├── MissionController.java
    │   ├── MissionBillController.java
    │   └── MissionCommentController.java
    ├── service/
    │   ├── MissionService.java
    │   ├── MissionBillService.java
    │   ├── MissionCommentService.java
    │   ├── MissionMonitorService.java
    │   └── MissionStatusLogService.java
    ├── service/impl/
    │   ├── MissionServiceImpl.java
    │   ├── MissionBillServiceImpl.java
    │   └── ...
    ├── mapper/
    │   ├── MissionMapper.java
    │   ├── MissionBillMapper.java
    │   └── ...
    └── config/
        ├── RedissonConfig.java
        └── MyBatisPlusConfig.java
```

**依赖关系**：
```xml
<dependencies>
    <!-- 模型层 -->
    <dependency>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>uav-model</artifactId>
    </dependency>
    
    <!-- 其他服务客户端 -->
    <dependency>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>service-pilot-client</artifactId>
    </dependency>
    <dependency>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>service-device-client</artifactId>
    </dependency>
</dependencies>
```

**迁移内容**：
- `UavMissionController.java` → `MissionController.java`
- `UavMissionServiceImpl.java` → `MissionServiceImpl.java`
- `MissionBillServiceImpl.java`（保持）
- `MissionCommentServiceImpl.java`（保持）
- 相关Mapper和XML文件

---

### 3. service-pilot（飞手服务）

**职责**：管理飞手信息、认证、账户

**核心功能**：
- 飞手注册
- 飞手认证审核
- 飞手信息管理
- 飞手账户管理
- 飞手登录日志
- 飞手设置

**目录结构**：
```
service-pilot/
├── pom.xml
└── src/main/java/com/uav/pilot/
    ├── ServicePilotApplication.java
    ├── controller/
    │   ├── PilotInfoController.java
    │   └── PilotAccountController.java
    ├── service/
    │   ├── PilotInfoService.java
    │   ├── PilotAccountService.java
    │   ├── PilotAccountDetailService.java
    │   ├── PilotLoginLogService.java
    │   └── PilotSettingsService.java
    ├── service/impl/
    │   └── ...
    ├── mapper/
    │   └── ...
    └── config/
```

**迁移内容**：
- `uav-service/src/main/java/com/uav/service/pilot/`下所有文件
- `UavPilotServiceImpl.java`
- `PilotAccountServiceImpl.java`
- 相关Mapper和XML文件

---

### 4. service-device（设备服务）

**职责**：管理无人机设备信息

**核心功能**：
- 设备注册
- 设备状态管理
- 设备查询
- 设备控制

**目录结构**：
```
service-device/
├── pom.xml
└── src/main/java/com/uav/device/
    ├── ServiceDeviceApplication.java
    ├── controller/
    │   ├── DeviceController.java
    │   └── DeviceControlController.java
    ├── service/
    │   ├── DeviceService.java
    │   └── DeviceQueryService.java
    ├── service/impl/
    │   └── ...
    └── mapper/
```

**迁移内容**：
- `UavDeviceController.java` → `DeviceController.java`
- `UavControlController.java` → `DeviceControlController.java`
- `UavDeviceServiceImpl.java` → `DeviceServiceImpl.java`
- `UavQueryServiceImpl.java` → `DeviceQueryServiceImpl.java`

---

### 5. service-dispatch（调度服务）

**职责**：任务调度和分配

**核心功能**：
- 任务调度
- 飞手匹配
- XXL-JOB任务管理
- 调度统计

**目录结构**：
```
service-dispatch/
├── pom.xml
└── src/main/java/com/uav/dispatch/
    ├── ServiceDispatchApplication.java
    ├── controller/
    │   └── DispatchController.java
    ├── service/
    │   └── DispatchService.java
    ├── service/impl/
    │   └── DispatchServiceImpl.java
    ├── mapper/
    │   ├── MissionJobMapper.java
    │   └── XxlJobLogMapper.java
    ├── job/
    │   └── MissionDispatchJobHandler.java
    └── config/
        └── xxl/
            ├── XxlJobConfig.java
            ├── XxlJobClient.java
            └── XxlJobClientConfig.java
```

**迁移内容**：
- `uav-service/src/main/java/com/uav/service/dispatch/`下所有文件（已有良好结构）

---

### 6. service-location（位置服务）

**职责**：管理飞手位置信息

**核心功能**：
- 位置更新
- 附近飞手查询
- 位置历史记录

**目录结构**：
```
service-location/
├── pom.xml
└── src/main/java/com/uav/location/
    ├── ServiceLocationApplication.java
    ├── controller/
    │   └── LocationController.java
    ├── service/
    │   └── PilotLocationService.java
    ├── service/impl/
    │   └── PilotLocationServiceImpl.java
    └── config/
        └── RedisConfig.java
```

**迁移内容**：
- `uav-service/src/main/java/com/uav/service/location/`下所有文件（已有良好结构）

---

### 7. service-account（账户服务）

**职责**：管理飞手账户和交易

**核心功能**：
- 账户余额管理
- 交易记录
- 提现管理

**目录结构**：
```
service-account/
├── pom.xml
└── src/main/java/com/uav/account/
    ├── ServiceAccountApplication.java
    ├── controller/
    │   └── AccountController.java
    ├── service/
    │   ├── PilotAccountService.java
    │   └── PilotAccountDetailService.java
    └── service/impl/
```

---

### 8. service-telemetry（遥测服务）

**职责**：处理无人机遥测数据

**核心功能**：
- 遥测数据接收（Kafka消费）
- 遥测数据存储（MongoDB）
- 遥测数据查询

**目录结构**：
```
service-telemetry/
├── pom.xml
└── src/main/java/com/uav/telemetry/
    ├── ServiceTelemetryApplication.java
    ├── consumer/
    │   └── UavTelemetryConsumer.java
    ├── repository/
    │   └── UavTelemetryRepository.java
    └── config/
        ├── KafkaConfig.java
        └── MongoConfig.java
```

**迁移内容**：
- `uav-service/src/main/java/com/uav/service/telemetry/`下所有文件

---

### 9. service-client（服务调用接口层）

**职责**：定义服务间调用的Feign接口

**示例：service-mission-client**
```
service-mission-client/
├── pom.xml
└── src/main/java/com/uav/mission/client/
    ├── MissionFeignClient.java
    ├── MissionBillFeignClient.java
    └── fallback/
        ├── MissionFeignClientFallback.java
        └── MissionBillFeignClientFallback.java
```

**MissionFeignClient示例**：
```java
@FeignClient(
    name = "service-mission",
    fallback = MissionFeignClientFallback.class
)
public interface MissionFeignClient {
    
    @GetMapping("/api/mission/{missionId}")
    Result<UavMission> getMissionById(@PathVariable Long missionId);
    
    @PostMapping("/api/mission/publish")
    Result<UavMission> publishMission(@RequestBody PublishMissionForm form);
    
    @PostMapping("/api/mission/accept")
    Result<String> acceptMission(@RequestBody AcceptMissionForm form);
}
```

**其他Client模块**：
- `service-pilot-client`
- `service-device-client`
- `service-dispatch-client`
- `service-location-client`
- `service-account-client`

---

## 🔄 迁移步骤

### 阶段一：创建基础架构（第1-2周）

1. **创建uav-model模块**
   ```bash
   # 创建模块目录
   mkdir -p uav-model/src/main/java/com/uav/model/{entity,form,vo,enums}
   
   # 创建pom.xml
   # 迁移所有实体类、VO、Form、枚举
   ```

2. **更新父POM**
   ```xml
   <modules>
       <module>uav-common</module>
       <module>uav-model</module>  <!-- 新增 -->
       <module>uav-api</module>
       <module>uav-gateway-iot</module>
       <module>service</module>     <!-- 新增 -->
       <module>service-client</module>  <!-- 新增 -->
   </modules>
   ```

3. **创建service父模块**
   ```bash
   mkdir -p service
   # 创建service/pom.xml
   ```

### 阶段二：拆分核心服务（第3-6周）

**优先级顺序**：
1. service-mission（最核心）
2. service-pilot
3. service-device
4. service-dispatch（已有良好结构）
5. service-location（已有良好结构）
6. service-account
7. service-telemetry

**每个服务的迁移步骤**：

1. **创建服务模块**
   ```bash
   cd service
   mkdir -p service-mission/src/main/java/com/uav/mission/{controller,service,mapper,config}
   mkdir -p service-mission/src/main/resources/{mapper,}
   ```

2. **配置pom.xml**
   - 添加uav-model依赖
   - 添加Spring Boot、MyBatis-Plus等依赖
   - 添加需要的service-client依赖

3. **迁移代码**
   - 复制相关Controller
   - 复制相关Service和ServiceImpl
   - 复制相关Mapper接口和XML
   - 调整包名和import

4. **配置文件**
   - 创建application.yml
   - 配置数据库连接
   - 配置Nacos注册中心
   - 配置Redis、Kafka等

5. **创建启动类**
   ```java
   @SpringBootApplication
   @MapperScan("com.uav.mission.mapper")
   public class ServiceMissionApplication {
       public static void main(String[] args) {
           SpringApplication.run(ServiceMissionApplication.class, args);
       }
   }
   ```

### 阶段三：创建服务调用接口（第7-8周）

1. **创建service-client父模块**
2. **为每个服务创建对应的client模块**
3. **定义Feign接口**
4. **实现降级逻辑**

### 阶段四：服务间调用改造（第9-10周）

1. **识别服务间依赖**
   - service-mission 依赖 service-pilot（查询飞手信息）
   - service-mission 依赖 service-device（查询设备信息）
   - service-dispatch 依赖 service-mission（任务调度）
   - service-dispatch 依赖 service-location（查询附近飞手）

2. **替换直接调用为Feign调用**
   ```java
   // 修改前
   @Autowired
   private UavPilotService pilotService;
   UavPilot pilot = pilotService.getById(pilotId);
   
   // 修改后
   @Autowired
   private PilotFeignClient pilotFeignClient;
   Result<UavPilot> result = pilotFeignClient.getPilotById(pilotId);
   UavPilot pilot = result.getData();
   ```

### 阶段五：测试和优化（第11-12周）

1. **单元测试**
2. **集成测试**
3. **性能测试**
4. **逐步下线旧的uav-service模块**

---

## 📝 关键配置示例

### 1. uav-model的pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>uav-dispatch-platform</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>uav-model</artifactId>
    <name>uav-model</name>
    <description>UAV统一模型层</description>

    <dependencies>
        <!-- MyBatis-Plus注解支持 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-annotation</artifactId>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Jackson注解 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 2. service-mission的pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>service-mission</artifactId>
    <name>service-mission</name>
    <description>任务服务</description>

    <dependencies>
        <!-- 模型层 -->
        <dependency>
            <groupId>com.uav.dispatch</groupId>
            <artifactId>uav-model</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <!-- 公共模块 -->
        <dependency>
            <groupId>com.uav.dispatch</groupId>
            <artifactId>uav-common</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <!-- 服务客户端 -->
        <dependency>
            <groupId>com.uav.dispatch</groupId>
            <artifactId>service-pilot-client</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.uav.dispatch</groupId>
            <artifactId>service-device-client</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        
        <!-- MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        
        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
        <!-- Redisson -->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
        </dependency>
        
        <!-- Nacos Discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        
        <!-- Nacos Config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        
        <!-- OpenFeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        
        <!-- Sentinel -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 3. service-mission的application.yml
```yaml
server:
  port: 8201

spring:
  application:
    name: service-mission
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/uav_mission?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    database: 0
  
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
      config:
        server-addr: localhost:8848
        namespace: dev
        file-extension: yml

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.uav.model.entity.mission
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# Feign配置
feign:
  sentinel:
    enabled: true
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000

# Redisson配置
redisson:
  address: redis://localhost:6379
  database: 0
```

### 4. service-mission-client的Feign接口
```java
package com.uav.mission.client;

import com.uav.common.Result;
import com.uav.model.entity.mission.UavMission;
import com.uav.model.form.mission.PublishMissionForm;
import com.uav.model.form.mission.AcceptMissionForm;
import com.uav.mission.client.fallback.MissionFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "service-mission",
    fallback = MissionFeignClientFallback.class
)
public interface MissionFeignClient {
    
    /**
     * 根据ID查询任务
     */
    @GetMapping("/api/mission/{missionId}")
    Result<UavMission> getMissionById(@PathVariable("missionId") Long missionId);
    
    /**
     * 发布任务
     */
    @PostMapping("/api/mission/publish")
    Result<UavMission> publishMission(@RequestBody PublishMissionForm form);
    
    /**
     * 接受任务
     */
    @PostMapping("/api/mission/accept")
    Result<String> acceptMission(@RequestBody AcceptMissionForm form);
    
    /**
     * 开始任务
     */
    @PostMapping("/api/mission/start")
    Result<String> startMission(@RequestParam("missionId") Long missionId);
    
    /**
     * 完成任务
     */
    @PostMapping("/api/mission/complete")
    Result<String> completeMission(
        @RequestParam("missionId") Long missionId,
        @RequestParam("actualDistance") BigDecimal actualDistance
    );
}
```

---

## ⚠️ 注意事项

### 1. 数据库拆分
- 每个服务使用独立的数据库schema
- 例如：`uav_mission`、`uav_pilot`、`uav_device`等
- 避免跨库join，通过服务调用获取关联数据

### 2. 事务处理
- 单服务内使用`@Transactional`
- 跨服务事务使用Seata分布式事务（如需要）
- 优先使用最终一致性方案

### 3. 配置管理
- 使用Nacos Config统一管理配置
- 敏感信息加密存储
- 区分dev、test、prod环境

### 4. 服务治理
- 使用Sentinel进行流量控制和熔断降级
- 配置合理的超时时间
- 实现Feign降级逻辑

### 5. 日志追踪
- 使用SkyWalking或Zipkin进行链路追踪
- 统一日志格式
- 添加traceId便于问题排查

### 6. 版本兼容
- 服务接口保持向后兼容
- 使用版本号管理API
- 灰度发布新版本

---

## 🎯 重构收益

### 1. 架构层面
- ✅ 清晰的服务边界和职责划分
- ✅ 独立部署和扩展能力
- ✅ 降低服务间耦合度
- ✅ 提高系统可维护性

### 2. 开发层面
- ✅ 团队可并行开发不同服务
- ✅ 代码结构清晰，易于理解
- ✅ 减少代码冲突
- ✅ 提高开发效率

### 3. 运维层面
- ✅ 服务可独立部署和回滚
- ✅ 故障隔离，不影响其他服务
- ✅ 可针对性能瓶颈服务扩容
- ✅ 便于监控和问题定位

### 4. 业务层面
- ✅ 快速响应业务需求变化
- ✅ 支持灰度发布和A/B测试
- ✅ 提高系统稳定性和可用性

---

## 📚 参考资料

1. **代驾项目架构**
   - 路径：`D:\代驾项目\0211.尚硅谷Java项目-乐尚代驾\代码\daijia-parent`
   - 重点参考：模块划分、service-client设计、model层组织

2. **Spring Cloud Alibaba官方文档**
   - Nacos服务注册与配置
   - Sentinel流量控制
   - Seata分布式事务

3. **MyBatis-Plus官方文档**
   - 代码生成器使用
   - 乐观锁配置

4. **Redisson官方文档**
   - 分布式锁使用

---

## 🚀 开始重构

请按照以上方案，逐步进行重构。建议：

1. **先创建uav-model模块**，迁移所有数据模型
2. **选择一个核心服务开始**（推荐service-mission）
3. **完成一个服务后再开始下一个**，避免同时改动过多
4. **保持旧代码可运行**，新旧并存，逐步切换
5. **充分测试每个阶段**，确保功能正常

重构是一个渐进的过程，不要急于求成。每完成一个服务的拆分，都是向目标架构迈进的一大步！

---

**祝重构顺利！如有问题，随时沟通调整方案。**