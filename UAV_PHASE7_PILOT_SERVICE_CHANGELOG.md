# UAV Phase 7 - service-pilot 飞手服务拆分完成

## 变更概述
成功从uav-service拆分出service-pilot（飞手服务），实现飞手信息管理和认证审核功能，集成腾讯云COS文件存储。

## 服务信息
- **服务名称**: service-pilot
- **端口**: 8203
- **Dubbo端口**: 20883
- **数据库**: uav_pilot
- **包名**: com.uav.pilot

## 创建的文件 (13个)

### 1. 配置文件 (2个)
- `pom.xml` - Maven配置，包含COS SDK依赖
- `src/main/resources/application.yml` - 应用配置
- `src/main/resources/bootstrap.yml` - Nacos配置

### 2. 主启动类 (1个)
- `ServicePilotApplication.java` - Spring Boot启动类，@MapperScan("com.uav.pilot.mapper")

### 3. Controller层 (1个)
- `PilotInfoController.java` - 飞手信息控制器
  - GET `/pilot/auth-info/{pilotId}` - 获取认证信息
  - PUT `/pilot/auth-info` - 更新认证信息
  - GET `/pilot/info/{pilotId}` - 获取飞手详情
  - POST `/pilot/certification/audit` - 审核认证

### 4. Service层 (2个)
- `PilotInfoService.java` - 接口
- `PilotInfoServiceImpl.java` - 实现类
  - 集成COS生成签名URL（1小时有效期）
  - 包含临时方法`getCompletedMissionsCountTemp()`（标记@Deprecated，Phase 11替换）

### 5. Mapper层 (3个)
- `UavPilotMapper.java` - 飞手主表Mapper
- `PilotCertificationAuditMapper.java` - 认证审核记录Mapper
- `UavMissionMapper.java` - 临时Mapper（标记@Deprecated，Phase 11移除）

### 6. Configuration层 (2个)
- `TencentCosConfig.java` - 腾讯云COS配置类
  - 配置secretId、secretKey、region、bucketName
  - 创建COSClient Bean
- `CosStorageService.java` - COS存储服务（212行）
  - `uploadFile()` - 文件上传（最大5MB，支持jpg/jpeg/png/pdf）
  - `getSignedUrl()` - 生成签名URL
  - `deleteFile()` - 删除文件

## 核心功能

### 1. 飞手认证管理
- 获取飞手认证信息（身份证、驾驶证、资格证）
- 更新认证信息（上传证件照片）
- 认证审核（通过/拒绝）

### 2. COS文件存储
- 安全的文件上传（带验证）
- 签名URL生成（防止直接访问）
- 文件删除功能

### 3. 飞手信息查询
- 基本信息（姓名、手机、状态）
- 认证状态
- 完成任务数（临时实现）

## 技术特性

### 1. 文件安全
- 使用签名URL（1小时有效期）
- 文件类型验证
- 文件大小限制（5MB）

### 2. 跨服务依赖（临时）
```java
@Deprecated
private Long getCompletedMissionsCountTemp(Long pilotId) {
    // TODO: Phase 11 - 替换为Dubbo RPC调用service-mission
    LambdaQueryWrapper<UavMission> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(UavMission::getPilotId, pilotId);
    wrapper.eq(UavMission::getStatus, 4); // 4-已完成
    return missionMapper.selectCount(wrapper);
}
```

### 3. 依赖版本
- Spring Boot: 3.0.5
- MyBatis-Plus: 3.5.3.1
- Dubbo: 3.2.0
- Nacos: 2.1.2
- Hutool: 5.8.16
- Tencent COS SDK: 5.6.89
- SpringDoc OpenAPI: 2.0.2

## 编译结果
```
[INFO] BUILD SUCCESS
[INFO] Total time: 16.956 s
```

## 待优化项（Phase 11）
1. 移除`UavMissionMapper`临时依赖
2. 将`getCompletedMissionsCountTemp()`替换为Dubbo RPC调用service-mission
3. 创建service-pilot-client模块，定义Dubbo接口

## 数据库表
- `uav_pilot` - 飞手主表
- `pilot_certification_audit` - 认证审核记录表

## 配置说明
需要在application.yml中配置腾讯云COS：
```yaml
tencent:
  cos:
    secret-id: your-secret-id
    secret-key: your-secret-key
    region: ap-guangzhou
    bucket-name: your-bucket-name
    expire-time: 3600  # 签名URL有效期（秒）
```

## 下一步
继续Phase 8 - 拆分service-device（设备服务）