# UAV项目 - 阶段八：service-device设备服务拆分

## 变更时间
2026-02-24 00:00

## 变更概述
成功拆分service-device（设备服务），包含设备管理和设备控制两大功能模块。

## 创建的文件

### 1. 配置文件 (2个)
- `uav-services/service-device/pom.xml` - Maven配置
- `uav-services/service-device/src/main/resources/application.yml` - 应用配置（端口8204）
- `uav-services/service-device/src/main/resources/bootstrap.yml` - Nacos配置

### 2. Java文件 (6个)
- `ServiceDeviceApplication.java` - 启动类
- `UavDeviceMapper.java` - 设备Mapper
- `UavDeviceService.java` - 设备服务接口
- `UavDeviceServiceImpl.java` - 设备服务实现
- `UavDeviceController.java` - 设备管理Controller
- `UavControlController.java` - 设备控制Controller

### 3. uav-model新增
- `uav-model/src/main/java/com/uav/model/vo/Result.java` - 统一响应结果类

## 服务配置

### 端口配置
- **HTTP端口**: 8204
- **Dubbo端口**: 20884
- **数据库**: uav_device

### 核心功能

#### 1. 设备管理 (UavDeviceController)
- `POST /device/bind` - 绑定设备到飞手
  - 支持自动创建设备（测试用）
  - 生成32位鉴权码
  - 检查设备绑定状态

#### 2. 设备控制 (UavControlController)
- `POST /control/send` - 发送控制指令
  - 检查设备在线状态
  - 通过Dubbo调用Gateway服务
  - 支持TAKEOFF、LAND、RETURN、HOVER等指令
- `GET /control/status` - 查询设备在线状态
- `POST /control/batch-send` - 批量发送指令

## 技术特点

### 1. Dubbo集成
- 使用`@DubboReference(group = "gateway")`调用Gateway服务
- 实现设备控制指令下发

### 2. 设备管理
- 自动创建设备功能（方便测试）
- UUID生成鉴权码
- 设备绑定状态检查

### 3. 统一响应
- 使用Result类统一响应格式
- 支持ok()和fail()方法

## 依赖关系

### Maven依赖
```xml
<dependencies>
    <dependency>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>uav-model</artifactId>
    </dependency>
    <dependency>
        <groupId>com.uav.dispatch</groupId>
        <artifactId>uav-api</artifactId>
    </dependency>
    <!-- Spring Boot, MyBatis-Plus, Dubbo, Nacos等 -->
</dependencies>
```

### 跨服务调用
- **调用Gateway**: 通过Dubbo调用`UavCommandService`发送设备控制指令

## 编译验证
```bash
cd uav-services/service-device
mvn clean compile
```
**结果**: ✅ BUILD SUCCESS (5.609s)

## 包结构
```
com.uav.device
├── ServiceDeviceApplication.java
├── controller/
│   ├── UavDeviceController.java      # 设备管理
│   └── UavControlController.java     # 设备控制
├── service/
│   ├── UavDeviceService.java
│   └── impl/
│       └── UavDeviceServiceImpl.java
└── mapper/
    └── UavDeviceMapper.java
```

## 数据库表
- `uav_device` - 设备信息表

## 重要修复

### 1. Result类迁移
- 将`Result.java`从uav-service迁移到uav-model
- 路径: `com.uav.model.vo.Result`
- 新增`fail()`方法作为`error()`的别名

### 2. Import路径修正
- UavDevice: `com.uav.model.entity.device.UavDevice`
- Result: `com.uav.model.vo.Result`

### 3. uav-model重新安装
```bash
cd uav-model && mvn clean install -DskipTests
```

## API端点

### 设备管理
- `POST /device/bind?pilotId={id}&serialNumber={sn}` - 绑定设备

### 设备控制
- `POST /control/send?deviceId={id}&action={action}` - 发送指令
- `GET /control/status?deviceId={id}` - 查询状态
- `POST /control/batch-send?deviceIds={ids}&action={action}` - 批量指令

## 下一步
- 阶段九：拆分service-account（账户服务）