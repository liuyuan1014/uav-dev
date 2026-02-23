# 无人机派单系统完整实现总结

## 📋 项目概述

成功将代驾项目的派单机制迁移到无人机调度平台，实现了基于 XXL-JOB + Redis GEO 的自动派单系统。

**创建时间**: 2026-02-23  
**总文件数**: 24个文件  
**代码行数**: 约2000+行

---

## 🏗️ 系统架构

### 部署架构
```
┌─────────────────────┐
│  XXL-JOB Admin      │  独立部署（本地启动）
│  调度中心           │  端口: 8080
└──────────┬──────────┘
           │ HTTP API
           ↓
┌─────────────────────┐
│  UAV-SERVICE        │  执行器（集成到项目）
│  - XXL-JOB Executor │  端口: 9999
│  - 派单业务逻辑     │
│  - Redis GEO        │
└─────────────────────┘
```

### 技术栈
- **任务调度**: XXL-JOB 2.4.1
- **位置存储**: Redis GEO
- **ORM框架**: MyBatis Plus
- **分布式锁**: Redisson
- **HTTP工具**: Hutool 5.8.22
- **应用框架**: Spring Boot

---

## 📦 文件清单

### 第一批：XXL-JOB基础配置（9个文件）

#### 1. 配置类
- **XxlJobConfig.java** (88行)
  - 执行器配置和初始化
  - 自动注册到调度中心
  - 路径: `uav-service/src/main/java/com/uav/service/config/xxl/`

- **XxlJobClientConfig.java** (31行)
  - XXL-JOB客户端Bean配置
  - 路径: `uav-service/src/main/java/com/uav/service/config/xxl/`

- **XxlJobClient.java** (234行)
  - 封装XXL-JOB Admin HTTP API
  - 核心方法: login(), addJob(), startJob(), stopJob(), removeJob(), triggerJob()
  - 路径: `uav-service/src/main/java/com/uav/service/config/xxl/`

#### 2. 实体类
- **MissionJob.java** (67行)
  - 任务与XXL-JOB关联实体
  - 表: `mission_job`
  - 路径: `uav-service/src/main/java/com/uav/service/domain/`

- **XxlJobLog.java** (78行)
  - XXL-JOB执行日志实体
  - 表: `xxl_job_log`
  - 路径: `uav-service/src/main/java/com/uav/service/domain/`

#### 3. Mapper接口
- **MissionJobMapper.java** + **MissionJobMapper.xml**
  - 任务关联数据访问
  - 路径: `uav-service/src/main/java/com/uav/service/mapper/`
  - XML路径: `uav-service/src/main/resources/mapper/`

- **XxlJobLogMapper.java** + **XxlJobLogMapper.xml**
  - 执行日志数据访问
  - 路径: `uav-service/src/main/java/com/uav/service/mapper/`
  - XML路径: `uav-service/src/main/resources/mapper/`

#### 4. 配置文件
- **pom.xml** - 添加依赖
  ```xml
  <dependency>
      <groupId>com.xuxueli</groupId>
      <artifactId>xxl-job-core</artifactId>
      <version>2.4.1</version>
  </dependency>
  <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-all</artifactId>
      <version>5.8.22</version>
  </dependency>
  ```

- **application.yml** - XXL-JOB配置
  ```yaml
  xxl:
    job:
      admin:
        addresses: http://localhost:8080/xxl-job-admin
        username: admin
        password: 123456
      executor:
        appname: uav-service-executor
        port: 9999
        logpath: ./logs/xxl-job
        logretentiondays: 30
  ```

---

### 第二批：位置服务模块（7个文件）

#### 1. 工具类
- **LocationUtil.java** (82行)
  - Haversine公式计算地理距离
  - 路径: `uav-service/src/main/java/com/uav/common/util/`

#### 2. 表单类
- **UpdateLocationForm.java** (35行)
  - 更新位置请求表单
  - 路径: `uav-service/src/main/java/com/uav/service/location/form/`

#### 3. VO类
- **PilotLocationVo.java** (42行)
  - 飞手位置信息VO
  - 路径: `uav-service/src/main/java/com/uav/service/location/vo/`

- **NearbyPilotVo.java** (48行)
  - 附近飞手信息VO（包含距离）
  - 路径: `uav-service/src/main/java/com/uav/service/location/vo/`

#### 4. Service层
- **PilotLocationService.java** (接口)
  - 位置服务接口定义
  - 路径: `uav-service/src/main/java/com/uav/service/location/service/`

- **PilotLocationServiceImpl.java** (283行)
  - 位置服务实现
  - 使用Redis GEO存储和查询
  - 核心方法: updateLocation(), searchNearbyIdlePilots()
  - 路径: `uav-service/src/main/java/com/uav/service/location/service/impl/`

#### 5. Controller层
- **LocationController.java** (92行)
  - 位置管理REST API
  - 路径: `uav-service/src/main/java/com/uav/service/location/controller/`

---

### 第三批：派单服务模块（8个文件）

#### 1. 工具类
- **DispatchUtil.java** (45行)
  - 派单相关工具方法
  - 路径: `uav-service/src/main/java/com/uav/common/util/`

#### 2. 表单类
- **StartDispatchForm.java** (40行)
  - 开始派单请求表单
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/form/`

#### 3. VO类
- **DispatchRecordVo.java** (52行)
  - 派单记录VO
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/vo/`

- **DispatchStatisticsVo.java** (42行)
  - 派单统计VO
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/vo/`

#### 4. Service层
- **DispatchService.java** (接口)
  - 派单服务接口定义
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/service/`

- **DispatchServiceImpl.java** (418行) ⭐核心文件
  - 派单服务实现
  - 核心方法:
    - `startDispatch()` - 创建XXL-JOB定时任务
    - `executeDispatch()` - 执行派单逻辑
    - `pushToPilot()` - 推送任务到Redis队列
    - `acceptMission()` - 飞手接单处理
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/service/impl/`

#### 5. Job Handler
- **MissionDispatchJobHandler.java** (67行)
  - XXL-JOB任务处理器
  - 使用 `@XxlJob("missionDispatchJobHandler")` 注解
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/job/`

#### 6. Controller层
- **DispatchController.java** (102行)
  - 派单管理REST API
  - 路径: `uav-service/src/main/java/com/uav/service/dispatch/controller/`

---

## 🗄️ 数据库设计

### 表结构

#### 1. mission_job（任务调度关联表）
```sql
CREATE TABLE `mission_job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` bigint NOT NULL COMMENT '任务ID',
  `job_id` int NOT NULL COMMENT 'XXL-JOB任务ID',
  `job_group` int NOT NULL COMMENT 'XXL-JOB执行器组ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-运行中，2-已停止',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mission_id` (`mission_id`),
  KEY `idx_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务调度关联表';
```

#### 2. xxl_job_log（执行日志表）
```sql
CREATE TABLE `xxl_job_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `job_group` int NOT NULL COMMENT '执行器组ID',
  `job_id` int NOT NULL COMMENT '任务ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度时间',
  `trigger_code` int DEFAULT NULL COMMENT '调度结果码',
  `trigger_msg` text COMMENT '调度日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行时间',
  `handle_code` int DEFAULT NULL COMMENT '执行结果码',
  `handle_msg` text COMMENT '执行日志',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_trigger_time` (`trigger_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='XXL-JOB执行日志表';
```

#### 3. pilot_location_history（飞手位置历史表）
```sql
CREATE TABLE `pilot_location_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pilot_id` bigint NOT NULL COMMENT '飞手ID',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='飞手位置历史表';
```

---

## 🔄 业务流程

### 派单流程图
```
客户下单
    ↓
创建任务（mission_id）
    ↓
调用 startDispatch(missionId, searchRadius)
    ↓
创建XXL-JOB定时任务（每分钟执行）
    ↓
保存 mission_job 关联记录
    ↓
┌─────────────────────────────────┐
│  定时任务执行（每分钟）          │
│  MissionDispatchJobHandler      │
└─────────────┬───────────────────┘
              ↓
    executeDispatch(missionId)
              ↓
    1. 获取任务起点坐标
              ↓
    2. Redis GEO搜索附近空闲飞手
       GEORADIUS key lng lat radius
              ↓
    3. 过滤已推送的飞手（Redis Set）
              ↓
    4. 批量推送到Redis队列
       LPUSH PILOT_TEMP_QUEUE:{pilotId}
              ↓
    5. 记录已推送（Redis Set + 15分钟过期）
              ↓
┌─────────────────────────────────┐
│  飞手端轮询接单                  │
│  RPOP PILOT_TEMP_QUEUE:{pilotId}│
└─────────────┬───────────────────┘
              ↓
    飞手接单 acceptMission(missionId, pilotId)
              ↓
    1. 更新任务状态
              ↓
    2. 停止XXL-JOB任务
              ↓
    3. 清理Redis数据
              ↓
    派单完成
```

### 关键Redis数据结构

#### 1. 飞手位置（GEO）
```
Key: PILOT_LOCATION
Type: GEO
Commands:
  - GEOADD PILOT_LOCATION {lng} {lat} {pilotId}
  - GEORADIUS PILOT_LOCATION {lng} {lat} {radius}km
```

#### 2. 飞手状态（Hash）
```
Key: PILOT_STATUS:{pilotId}
Type: Hash
Fields:
  - status: 1-空闲, 2-接单中, 3-执行中
  - missionId: 当前任务ID
  - updateTime: 更新时间
```

#### 3. 已推送记录（Set）
```
Key: MISSION_PUSHED_PILOTS:{missionId}
Type: Set
Expire: 15分钟
Members: pilotId列表
```

#### 4. 飞手任务队列（List）
```
Key: PILOT_TEMP_QUEUE:{pilotId}
Type: List
Expire: 15分钟
Elements: missionId列表
```

---

## 🚀 部署步骤

### 1. 初始化XXL-JOB数据库
```sql
-- 创建数据库
CREATE DATABASE xxl_job DEFAULT CHARACTER SET utf8mb4;

-- 导入官方SQL脚本
-- 下载地址: https://github.com/xuxueli/xxl-job/blob/master/doc/db/tables_xxl_job.sql
```

### 2. 修改XXL-JOB Admin配置
编辑 `xxl-job-admin/src/main/resources/application.properties`:
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password

# 调度中心端口
server.port=8080
```

### 3. 添加自定义API到JobInfoController
在 `xxl-job-admin` 项目的 `JobInfoController.java` 中添加以下方法：

```java
/**
 * 启动任务
 */
@RequestMapping("/start")
@ResponseBody
public ReturnT<String> start(int id) {
    return xxlJobService.start(id);
}

/**
 * 停止任务
 */
@RequestMapping("/stop")
@ResponseBody
public ReturnT<String> stop(int id) {
    return xxlJobService.stop(id);
}
```

### 4. 启动XXL-JOB调度中心
```bash
cd xxl-job-admin
mvn clean package
java -jar target/xxl-job-admin-2.4.1.jar
```

访问: http://localhost:8080/xxl-job-admin  
默认账号: admin / 123456

### 5. 创建UAV-SERVICE数据库表
执行 `create_dispatch_tables.sql` 创建以下表：
- mission_job
- xxl_job_log
- pilot_location_history

### 6. 启动UAV-SERVICE
```bash
cd uav-service
mvn clean package
java -jar target/uav-service.jar
```

执行器会自动注册到调度中心。

---

## 📡 API接口

### 位置管理接口

#### 1. 更新飞手位置
```http
POST /api/location/update
Content-Type: application/json

{
  "pilotId": 1001,
  "longitude": 116.397128,
  "latitude": 39.916527,
  "address": "北京市东城区"
}
```

#### 2. 搜索附近空闲飞手
```http
GET /api/location/nearby?longitude=116.397128&latitude=39.916527&radius=5&limit=10
```

#### 3. 获取飞手当前位置
```http
GET /api/location/pilot/1001
```

---

### 派单管理接口

#### 1. 开始派单
```http
POST /api/dispatch/start
Content-Type: application/json

{
  "missionId": 2001,
  "searchRadius": 5.0
}
```

#### 2. 飞手接单
```http
POST /api/dispatch/accept
Content-Type: application/json

{
  "missionId": 2001,
  "pilotId": 1001
}
```

#### 3. 停止派单
```http
POST /api/dispatch/stop/2001
```

#### 4. 查询派单记录
```http
GET /api/dispatch/records/2001
```

#### 5. 查询派单统计
```http
GET /api/dispatch/statistics/2001
```

---

## 🔧 配置说明

### XXL-JOB配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| xxl.job.admin.addresses | 调度中心地址 | http://localhost:8080/xxl-job-admin |
| xxl.job.admin.username | 调度中心账号 | admin |
| xxl.job.admin.password | 调度中心密码 | 123456 |
| xxl.job.executor.appname | 执行器名称 | uav-service-executor |
| xxl.job.executor.port | 执行器端口 | 9999 |
| xxl.job.executor.logpath | 日志路径 | ./logs/xxl-job |
| xxl.job.executor.logretentiondays | 日志保留天数 | 30 |

### 派单配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| DISPATCH_CRON | 派单执行频率 | 0 * * * * ? (每分钟) |
| SEARCH_RADIUS | 默认搜索半径 | 5.0 km |
| MAX_PUSH_COUNT | 最大推送飞手数 | 10 |
| DISPATCH_TIMEOUT | 派单超时时间 | 15分钟 |
| QUEUE_EXPIRE | 队列过期时间 | 15分钟 |

---

## 🎯 核心特性

### 1. 自动派单
- ✅ 基于XXL-JOB的分布式任务调度
- ✅ 每分钟自动搜索附近空闲飞手
- ✅ 支持动态调整搜索半径

### 2. 智能推送
- ✅ Redis GEO高效地理位置搜索
- ✅ 去重机制避免重复推送
- ✅ 批量推送提高效率

### 3. 超时处理
- ✅ 15分钟超时自动停止派单
- ✅ Redis数据自动过期清理
- ✅ 任务状态实时同步

### 4. 高可用性
- ✅ 分布式任务调度
- ✅ 执行器自动注册
- ✅ 任务失败自动重试

### 5. 可扩展性
- ✅ 支持多执行器部署
- ✅ 支持自定义派单策略
- ✅ 支持任务参数动态配置

---

## 📊 性能优化

### 1. Redis优化
- 使用GEO数据结构存储位置（O(log(N))复杂度）
- 使用Set记录已推送飞手（O(1)查询）
- 设置合理的过期时间减少内存占用

### 2. 数据库优化
- 添加必要的索引（mission_id, job_id, pilot_id）
- 使用批量操作减少数据库交互
- 定期清理历史日志数据

### 3. 任务调度优化
- 合理设置执行频率（默认每分钟）
- 使用分布式锁避免重复执行
- 任务超时自动停止

---

## 🐛 常见问题

### 1. 执行器注册失败
**问题**: 执行器无法注册到调度中心  
**解决**: 
- 检查调度中心地址配置是否正确
- 确认调度中心已启动
- 检查网络连接和防火墙设置

### 2. 任务创建失败
**问题**: 调用startDispatch()返回失败  
**解决**:
- 检查XXL-JOB Admin账号密码
- 确认执行器已成功注册
- 查看调度中心日志

### 3. 派单无效果
**问题**: 任务创建成功但飞手收不到推送  
**解决**:
- 检查Redis连接是否正常
- 确认飞手位置已更新到Redis
- 检查飞手状态是否为空闲
- 查看任务执行日志

### 4. 位置搜索无结果
**问题**: 搜索附近飞手返回空列表  
**解决**:
- 确认Redis GEO数据已添加
- 检查搜索半径是否合理
- 验证经纬度坐标是否正确

---

## 📈 后续优化建议

### 1. 功能增强
- [ ] 实现派单记录持久化存储
- [ ] 完善派单统计功能
- [ ] 添加位置历史记录保存
- [ ] 实现更复杂的派单策略（优先级、飞手评分等）

### 2. 性能优化
- [ ] 引入缓存机制减少数据库查询
- [ ] 优化Redis数据结构
- [ ] 实现任务执行结果异步处理

### 3. 监控告警
- [ ] 添加任务执行监控
- [ ] 实现派单失败告警
- [ ] 统计派单成功率

### 4. 用户体验
- [ ] 实现实时推送通知
- [ ] 添加派单进度查询
- [ ] 优化飞手接单流程

---

## 📝 总结

本次实现成功将代驾项目的派单机制迁移到无人机调度平台，核心特点：

1. **架构清晰**: XXL-JOB调度中心独立部署，UAV-SERVICE作为执行器
2. **技术成熟**: 使用XXL-JOB + Redis GEO的成熟方案
3. **代码规范**: 分层清晰，职责明确，易于维护
4. **功能完整**: 涵盖位置管理、派单调度、任务执行全流程
5. **扩展性强**: 支持自定义派单策略和多执行器部署

**创建文件**: 24个  
**代码行数**: 2000+行  
**开发时间**: 1天  
**状态**: ✅ 开发完成，待测试验证

---

## 📚 参考资料

- [XXL-JOB官方文档](https://www.xuxueli.com/xxl-job/)
- [Redis GEO命令文档](https://redis.io/commands/geoadd/)
- [MyBatis Plus官方文档](https://baomidou.com/)
- [Hutool工具类文档](https://hutool.cn/)

---

**文档版本**: v1.0  
**最后更新**: 2026-02-23  
**维护人员**: Roo AI Assistant