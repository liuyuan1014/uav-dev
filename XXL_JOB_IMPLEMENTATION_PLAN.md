# XXL-JOB派单系统实施计划

## 📋 实施概览

**目标**：为无人机项目实现基于XXL-JOB的智能派单系统  
**技术栈**：XXL-JOB 2.4.1 + Redis GEO + Spring Boot  
**预计时间**：10-14天  
**实施模式**：一步到位，直接生产级实现

---

## 🎯 阶段1：基础设施搭建（1-2天）

### 1.1 部署XXL-JOB调度中心
- [ ] 下载xxl-job-admin项目
- [ ] 初始化调度中心数据库
- [ ] 修改调度中心配置
- [ ] 启动调度中心服务
- [ ] 验证管理界面访问

### 1.2 创建数据库表
```sql
-- mission_job.sql
-- xxl_job_log.sql
-- pilot_location_history.sql (可选)
```

### 1.3 配置Maven依赖
```xml
<!-- uav-service/pom.xml -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.4.1</version>
</dependency>
```

### 1.4 配置application.yml
```yaml
xxl:
  job:
    admin:
      addresses: http://localhost:8080/xxl-job-admin
    executor:
      appname: uav-dispatch-executor
      port: 9999
    client:
      jobGroupId: 1
      addAndStartUrl: ${xxl.job.admin.addresses}/jobinfo/addAndStartJob
      stopJobUrl: ${xxl.job.admin.addresses}/jobinfo/stopJob
```

---

## 🎯 阶段2：位置服务开发（2-3天）

### 2.1 实体类和VO
- [ ] `UpdatePilotLocationForm.java` - 更新飞手位置表单
- [ ] `SearchNearByPilotForm.java` - 搜索附近飞手表单
- [ ] `NearByPilotVo.java` - 附近飞手VO

### 2.2 LocationService接口
```java
public interface LocationService {
    Boolean updatePilotLocation(UpdatePilotLocationForm form);
    Boolean removePilotLocation(Long pilotId);
    List<NearByPilotVo> searchNearByPilot(SearchNearByPilotForm form);
}
```

### 2.3 Redis GEO集成
- [ ] 实现飞手位置添加到GEO
- [ ] 实现GEORADIUS搜索
- [ ] 实现位置删除
- [ ] 添加Redis常量定义

### 2.4 Controller接口
- [ ] `LocationController.updatePilotLocation()`
- [ ] `LocationController.removePilotLocation()`
- [ ] `LocationController.searchNearByPilot()`

### 2.5 单元测试
- [ ] 位置更新测试
- [ ] 附近飞手搜索测试
- [ ] 边界条件测试

---

## 🎯 阶段3：派单服务开发（3-4天）

### 3.1 XXL-JOB配置类
- [ ] `XxlJobConfig.java` - 执行器配置
- [ ] `XxlJobClientConfig.java` - 客户端配置
- [ ] `XxlJobClient.java` - 任务管理客户端

### 3.2 实体类和Mapper
- [ ] `MissionJob.java` - 任务调度关联实体
- [ ] `XxlJobLog.java` - 调度日志实体
- [ ] `MissionJobMapper.java` - Mapper接口
- [ ] `XxlJobLogMapper.java` - Mapper接口
- [ ] `MissionJobMapper.xml` - MyBatis映射
- [ ] `XxlJobLogMapper.xml` - MyBatis映射

### 3.3 派单服务
- [ ] `DispatchService.java` - 派单服务接口
- [ ] `DispatchServiceImpl.java` - 派单服务实现
  - `addAndStartTask()` - 创建并启动任务
  - `executeTask()` - 执行派单逻辑
  - `stopTask()` - 停止任务

### 3.4 任务处理器
- [ ] `MissionDispatchJobHandler.java`
  - `@XxlJob("newMissionTaskHandler")`
  - 任务执行入口
  - 异常处理和日志记录

### 3.5 智能匹配算法
- [ ] 实现飞手过滤逻辑
  - 基础条件过滤
  - 无人机状态检查
  - 任务类型匹配
- [ ] 实现优先级排序
  - 综合评分计算
  - 距离权重
  - 评分权重
  - 接单率权重

### 3.6 Redis队列管理
- [ ] 实现去重SET管理
- [ ] 实现临时队列管理
- [ ] 设置合理的过期时间

### 3.7 Controller接口
- [ ] `DispatchController.addAndStartTask()`
- [ ] `DispatchController.findNewMissionQueueData()`
- [ ] `DispatchController.clearNewMissionQueueData()`

---

## 🎯 阶段4：集成测试（2-3天）

### 4.1 单元测试
- [ ] LocationService测试
- [ ] DispatchService测试
- [ ] XxlJobClient测试

### 4.2 集成测试
- [ ] 完整派单流程测试
- [ ] 任务调度测试
- [ ] Redis数据验证

### 4.3 并发测试
- [ ] 多飞手同时接单测试
- [ ] 高并发下单测试
- [ ] 分布式锁测试

### 4.4 异常场景测试
- [ ] 任务超时测试
- [ ] Redis连接失败测试
- [ ] XXL-JOB调度失败测试
- [ ] 数据库异常测试

### 4.5 性能测试
- [ ] Redis GEO查询性能
- [ ] 任务执行耗时
- [ ] 系统吞吐量

---

## 🎯 阶段5：优化与上线（1-2天）

### 5.1 代码优化
- [ ] 代码重构和优化
- [ ] 添加详细注释
- [ ] 统一异常处理
- [ ] 日志优化

### 5.2 文档完善
- [ ] API文档（Swagger）
- [ ] 部署文档
- [ ] 运维手册
- [ ] 故障排查指南

### 5.3 监控配置
- [ ] 添加关键指标监控
- [ ] 配置告警规则
- [ ] 日志收集配置

### 5.4 上线准备
- [ ] 生产环境配置
- [ ] 数据库迁移脚本
- [ ] 灰度发布计划
- [ ] 回滚方案

---

## 📁 文件清单

### 新增文件（约30个）

#### 配置类（3个）
```
uav-service/src/main/java/com/uav/service/xxl/
├── config/
│   ├── XxlJobConfig.java
│   └── XxlJobClientConfig.java
└── client/
    └── XxlJobClient.java
```

#### 实体类（2个）
```
uav-service/src/main/java/com/uav/service/domain/
├── MissionJob.java
└── XxlJobLog.java
```

#### Mapper（4个）
```
uav-service/src/main/java/com/uav/service/mapper/
├── MissionJobMapper.java
└── XxlJobLogMapper.java

uav-service/src/main/resources/mapper/
├── MissionJobMapper.xml
└── XxlJobLogMapper.xml
```

#### 位置服务（7个）
```
uav-service/src/main/java/com/uav/service/location/
├── controller/
│   └── LocationController.java
├── service/
│   ├── LocationService.java
│   └── impl/
│       └── LocationServiceImpl.java
├── form/
│   ├── UpdatePilotLocationForm.java
│   └── SearchNearByPilotForm.java
└── vo/
    └── NearByPilotVo.java
```

#### 派单服务（8个）
```
uav-service/src/main/java/com/uav/service/dispatch/
├── controller/
│   └── DispatchController.java
├── service/
│   ├── DispatchService.java
│   └── impl/
│       └── DispatchServiceImpl.java
├── job/
│   └── MissionDispatchJobHandler.java
├── form/
│   └── NewMissionTaskForm.java
└── vo/
    └── NewMissionDataVo.java
```

#### 常量类（1个）
```
uav-service/src/main/java/com/uav/common/constant/
└── RedisConstant.java
```

#### 数据库脚本（3个）
```
sql/
├── create_mission_job.sql
├── create_xxl_job_log.sql
└── create_pilot_location_history.sql
```

#### 测试类（3个）
```
uav-service/src/test/java/com/uav/service/
├── LocationServiceTest.java
├── DispatchServiceTest.java
└── MissionDispatchIntegrationTest.java
```

---

## 🔑 关键技术点

### 1. Redis GEO使用
```java
// 添加位置
Point point = new Point(longitude, latitude);
redisTemplate.opsForGeo().add("pilot:geo:location", point, pilotId);

// 搜索附近
Circle circle = new Circle(point, new Distance(5, DistanceUnit.KILOMETERS));
GeoResults results = redisTemplate.opsForGeo().radius("pilot:geo:location", circle, args);
```

### 2. XXL-JOB任务管理
```java
// 创建并启动任务
Long jobId = xxlJobClient.addAndStart(
    "newMissionTaskHandler",  // JobHandler名称
    JSONObject.toJSONString(taskVo),  // 参数
    "0 0/1 * * * ?",  // CRON表达式
    "新任务派单"  // 描述
);

// 停止任务
xxlJobClient.stopJob(jobId);
```

### 3. 任务处理器
```java
@XxlJob("newMissionTaskHandler")
public void newMissionTaskHandler() {
    Long jobId = XxlJobHelper.getJobId();
    String param = XxlJobHelper.getJobParam();
    
    try {
        dispatchService.executeTask(jobId);
    } catch (Exception e) {
        XxlJobHelper.handleFail(e.getMessage());
    }
}
```

### 4. 智能匹配算法
```java
// 综合评分
double score = (1 - distance/maxDistance) * 0.4 
             + (rating/5.0) * 0.3 
             + acceptRate * 0.3;

// 排序
pilots.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
```

---

## ⚠️ 注意事项

### 1. Redis配置
- 确保Redis版本 >= 3.2（支持GEO）
- 配置合理的内存限制
- 设置合适的过期策略

### 2. XXL-JOB配置
- 调度中心和执行器时钟同步
- 配置合理的线程池大小
- 设置任务超时时间

### 3. 数据一致性
- 使用分布式锁防止重复派单
- 使用Redis SET防止重复推送
- 使用数据库事务保证一致性

### 4. 性能优化
- Redis连接池配置
- 批量操作优化
- 异步处理非关键逻辑

### 5. 异常处理
- 完善的异常捕获和日志
- 任务失败重试机制
- 降级方案

---

## 📊 验收标准

### 功能验收
- [ ] 客户下单后自动创建派单任务
- [ ] 任务每1分钟执行一次搜索附近飞手
- [ ] 符合条件的飞手收到新任务推送
- [ ] 飞手接单后任务自动停止
- [ ] 15分钟无人接单任务自动取消

### 性能验收
- [ ] Redis GEO查询 < 100ms (P95)
- [ ] 任务执行耗时 < 500ms (P95)
- [ ] 支持1000+并发下单
- [ ] 系统可用性 > 99.9%

### 质量验收
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试通过
- [ ] 代码审查通过
- [ ] 文档完整

---

## 🚀 下一步行动

现在我将切换到Code模式，开始实施这个计划。实施顺序：

1. **首先**：创建数据库表和基础配置
2. **然后**：实现位置服务（Redis GEO）
3. **接着**：实现派单服务（XXL-JOB）
4. **最后**：集成测试和优化

准备好了吗？我现在就开始实施！

---

**文档版本**：v1.0  
**创建时间**：2026-02-23  
**预计完成**：2026-03-08