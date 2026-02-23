# XXL-JOB部署架构方案（修订版）

## 🏗️ 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    XXL-JOB调度中心                           │
│              (独立部署，单独启动)                             │
│                                                              │
│  D:\代驾项目\...\xxl-job-master\xxl-job-admin               │
│  - 管理任务                                                  │
│  - 触发调度                                                  │
│  - 监控日志                                                  │
└──────────────────┬──────────────────────────────────────────┘
                   │ HTTP API
                   │ (任务管理接口)
                   ↓
┌─────────────────────────────────────────────────────────────┐
│                  UAV-SERVICE (执行器)                        │
│                 D:\uav\uav-dev\uav-service                   │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  XXL-JOB执行器配置                                  │    │
│  │  - XxlJobConfig (连接调度中心)                      │    │
│  │  - XxlJobClient (调用调度中心API)                   │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  派单业务逻辑                                        │    │
│  │  - DispatchService (派单服务)                       │    │
│  │  - LocationService (位置服务)                       │    │
│  │  - MissionDispatchJobHandler (任务处理器)           │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  数据持久化                                          │    │
│  │  - MissionJob (任务关联表)                          │    │
│  │  - XxlJobLog (执行日志表)                           │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## 📁 修订后的文件结构

### 1. XXL-JOB调度中心（独立部署）
```
D:\代驾项目\0211.尚硅谷Java项目-乐尚代驾\代码\xxl-job-master\
├── xxl-job-admin/              # 调度中心（独立启动）
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/xxl/job/admin/
│   │   │       └── controller/
│   │   │           └── JobInfoController.java  # 需要修改：添加自定义API
│   │   └── resources/
│   │       └── application.properties          # 需要修改：配置数据库
│   └── pom.xml
│
└── doc/db/
    └── tables_xxl_job.sql      # 调度中心数据库表
```

**需要修改的文件**：
1. `JobInfoController.java` - 添加自定义API接口（参考代驾项目文档）
2. `application.properties` - 配置数据库连接

---

### 2. UAV-SERVICE（执行器）
```
D:\uav\uav-dev\uav-service\
├── pom.xml                     # 添加xxl-job-core依赖
│
├── src/main/
│   ├── java/com/uav/
│   │   ├── common/
│   │   │   └── constant/
│   │   │       └── RedisConstant.java ✅ (已创建)
│   │   │
│   │   └── service/
│   │       ├── config/
│   │       │   └── xxl/
│   │       │       ├── XxlJobConfig.java        (NEW) 执行器配置
│   │       │       ├── XxlJobClientConfig.java  (NEW) 客户端配置
│   │       │       └── XxlJobClient.java        (NEW) 任务管理客户端
│   │       │
│   │       ├── domain/
│   │       │   ├── MissionJob.java              (NEW) 任务关联实体
│   │       │   └── XxlJobLog.java               (NEW) 执行日志实体
│   │       │
│   │       ├── mapper/
│   │       │   ├── MissionJobMapper.java        (NEW)
│   │       │   └── XxlJobLogMapper.java         (NEW)
│   │       │
│   │       ├── dispatch/                        (NEW) 派单模块
│   │       │   ├── service/
│   │       │   │   ├── DispatchService.java
│   │       │   │   └── impl/
│   │       │   │       └── DispatchServiceImpl.java
│   │       │   ├── job/
│   │       │   │   └── MissionDispatchJobHandler.java
│   │       │   └── controller/
│   │       │       └── DispatchController.java
│   │       │
│   │       └── location/                        (NEW) 位置模块
│   │           ├── service/
│   │           │   ├── LocationService.java
│   │           │   └── impl/
│   │           │       └── LocationServiceImpl.java
│   │           └── controller/
│   │               └── LocationController.java
│   │
│   └── resources/
│       ├── application.yml                      # 添加xxl-job配置
│       └── mapper/
│           ├── MissionJobMapper.xml             (NEW)
│           └── XxlJobLogMapper.xml              (NEW)
│
└── 数据库脚本/
    └── create_dispatch_tables.sql ✅ (已创建)
```

---

## 🎯 实施步骤（修订版）

### 阶段1：准备XXL-JOB调度中心（你本地操作）

#### 步骤1.1：初始化调度中心数据库
```sql
-- 在MySQL中创建数据库
CREATE DATABASE xxl_job DEFAULT CHARACTER SET utf8mb4;

-- 执行初始化脚本
-- 路径：D:\代驾项目\...\xxl-job-master\doc\db\tables_xxl_job.sql
```

#### 步骤1.2：修改调度中心配置
文件：`xxl-job-admin/src/main/resources/application.properties`
```properties
# 修改数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/xxl_job?...
spring.datasource.username=root
spring.datasource.password=你的密码
```

#### 步骤1.3：添加自定义API（参考代驾项目文档第955-959行）
文件：`xxl-job-admin/.../JobInfoController.java`
```java
// 在文件末尾添加自定义方法
@RequestMapping("/addAndStartJob")
@ResponseBody
@PermissionLimit(limit = false)
public ReturnT<String> addAndStartJob(@RequestBody XxlJobInfo jobInfo) {
    ReturnT<String> result = xxlJobService.add(jobInfo);
    int id = Integer.valueOf(result.getContent());
    xxlJobService.start(id);
    JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, jobInfo.getExecutorParam(), "");
    return result;
}

@RequestMapping("/stopJob")
@ResponseBody
@PermissionLimit(limit = false)
public ReturnT<String> stopJob(@RequestBody XxlJobInfo jobInfo) {
    return xxlJobService.stop(jobInfo.getId());
}
```

#### 步骤1.4：启动调度中心
```bash
cd D:\代驾项目\...\xxl-job-master\xxl-job-admin
mvn spring-boot:run
```
访问：http://localhost:8080/xxl-job-admin  
默认账号：admin/123456

---

### 阶段2：配置UAV-SERVICE执行器（我来实现）

#### 第一批文件（9个）- XXL-JOB基础配置

**在uav-service中创建**：

1. **XXL-JOB配置类（3个）**
   ```
   uav-service/src/main/java/com/uav/service/config/xxl/
   ├── XxlJobConfig.java          # 执行器配置
   ├── XxlJobClientConfig.java    # 客户端配置
   └── XxlJobClient.java           # 任务管理客户端
   ```

2. **实体类和Mapper（4个）**
   ```
   uav-service/src/main/java/com/uav/service/
   ├── domain/
   │   ├── MissionJob.java
   │   └── XxlJobLog.java
   └── mapper/
       ├── MissionJobMapper.java
       └── XxlJobLogMapper.java
   ```

3. **MyBatis XML（2个）**
   ```
   uav-service/src/main/resources/mapper/
   ├── MissionJobMapper.xml
   └── XxlJobLogMapper.xml
   ```

4. **配置文件修改（2个）**
   - `pom.xml` - 添加xxl-job-core依赖
   - `application.yml` - 添加xxl-job配置

---

### 阶段3：实现派单业务（我来实现）

#### 第二批文件 - 位置服务
#### 第三批文件 - 派单服务

---

## 🔄 工作流程

### 1. 客户下单流程
```
客户下单
  ↓
UavMissionService.createMission()
  ↓
DispatchService.addAndStartTask()
  ↓
XxlJobClient.addAndStart() → HTTP请求 → XXL-JOB调度中心
  ↓
调度中心创建任务并启动
  ↓
每1分钟触发一次 → MissionDispatchJobHandler.execute()
  ↓
搜索附近飞手 → 推送到Redis队列
```

### 2. 飞手接单流程
```
飞手轮询Redis队列
  ↓
获取新任务信息
  ↓
飞手接单
  ↓
DispatchService.stopTask()
  ↓
XxlJobClient.stopJob() → HTTP请求 → XXL-JOB调度中心
  ↓
调度中心停止任务
```

---

## ✅ 优势分析

### 1. 架构解耦
- ✅ XXL-JOB调度中心独立部署，可以服务多个项目
- ✅ UAV-SERVICE只作为执行器，职责单一
- ✅ 便于横向扩展（可以部署多个执行器实例）

### 2. 运维友好
- ✅ 调度中心有独立的管理界面
- ✅ 可以在界面上手动管理任务
- ✅ 日志集中管理，便于排查问题

### 3. 技术成熟
- ✅ 完全参考代驾项目的成熟方案
- ✅ XXL-JOB经过大量生产环境验证
- ✅ 社区活跃，文档完善

---

## 📊 部署清单

### 你需要做的（调度中心）
- [ ] 创建xxl_job数据库
- [ ] 执行初始化SQL脚本
- [ ] 修改application.properties配置
- [ ] 在JobInfoController添加自定义API
- [ ] 启动xxl-job-admin
- [ ] 访问管理界面验证

### 我需要做的（执行器）
- [ ] 在uav-service添加xxl-job-core依赖
- [ ] 创建XXL-JOB配置类（3个）
- [ ] 创建实体类和Mapper（4个）
- [ ] 创建MyBatis XML（2个）
- [ ] 修改application.yml配置
- [ ] 实现位置服务
- [ ] 实现派单服务
- [ ] 实现任务处理器

---

## 🎯 下一步行动

**现在的分工**：

1. **你先准备调度中心**（预计30分钟）
   - 初始化数据库
   - 修改配置
   - 添加自定义API
   - 启动调度中心

2. **我开始实现执行器**（同时进行）
   - 创建第一批9个文件
   - 配置执行器连接调度中心
   - 实现基础的任务管理功能

3. **联调测试**
   - 验证执行器能否连接调度中心
   - 测试任务创建和执行
   - 验证日志记录

**你觉得这个方案怎么样？如果同意，我现在就开始创建uav-service中的第一批文件！**