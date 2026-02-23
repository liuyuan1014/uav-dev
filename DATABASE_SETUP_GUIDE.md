# 数据库设置指南

## 📊 数据库架构说明

本项目使用**两个独立的数据库**：

```
┌─────────────────────────────────────────────────────────┐
│                    MySQL 数据库服务器                      │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────────┐    ┌─────────────────────┐   │
│  │   xxl_job 数据库      │    │  uav_dev 数据库      │   │
│  │  (XXL-JOB调度中心)    │    │  (UAV业务系统)       │   │
│  ├──────────────────────┤    ├─────────────────────┤   │
│  │ • xxl_job_group      │    │ • uav_mission       │   │
│  │ • xxl_job_info       │    │ • uav_pilot         │   │
│  │ • xxl_job_log        │    │ • uav_device        │   │
│  │ • xxl_job_logglue    │    │ • mission_job ⭐    │   │
│  │ • xxl_job_registry   │    │ • xxl_job_log ⭐    │   │
│  │ • xxl_job_user       │    │ • pilot_location... │   │
│  │ • xxl_job_lock       │    │ • ...               │   │
│  └──────────────────────┘    └─────────────────────┘   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🗄️ 数据库1：xxl_job（XXL-JOB调度中心）

### 用途
- XXL-JOB Admin 管理后台使用
- 存储任务调度配置、执行日志等

### 创建方式

#### 步骤1：创建数据库
```sql
CREATE DATABASE xxl_job DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 步骤2：导入官方SQL脚本

**下载地址**：
- GitHub: https://github.com/xuxueli/xxl-job/blob/master/doc/db/tables_xxl_job.sql
- 或使用本地 xxl-job-master 项目中的 SQL 文件

**导入命令**：
```bash
mysql -u root -p xxl_job < tables_xxl_job.sql
```

#### 步骤3：验证
```sql
USE xxl_job;
SHOW TABLES;
```

应该看到以下表：
- xxl_job_group
- xxl_job_info
- xxl_job_log
- xxl_job_logglue
- xxl_job_registry
- xxl_job_user
- xxl_job_lock

### 配置文件
[`xxl-job-admin/src/main/resources/application.properties`](xxl-job-admin/src/main/resources/application.properties:38)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 🗄️ 数据库2：uav_dev（UAV业务系统）⭐

### 用途
- UAV-SERVICE 业务系统使用
- 存储无人机、飞手、任务等业务数据
- **包含派单系统的3张表**

### 创建方式

#### 步骤1：创建数据库（如果还没有）
```sql
CREATE DATABASE uav_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 步骤2：执行 create_dispatch_tables.sql

**文件位置**：[`create_dispatch_tables.sql`](create_dispatch_tables.sql)

**执行命令**：
```bash
mysql -u root -p uav_dev < create_dispatch_tables.sql
```

或在 MySQL 客户端中：
```sql
USE uav_dev;
SOURCE D:/uav/uav-dev/create_dispatch_tables.sql;
```

#### 步骤3：验证
```sql
USE uav_dev;
SHOW TABLES;
```

应该看到新增的3张表：
- **mission_job** - 任务调度关联表
- **xxl_job_log** - XXL-JOB执行日志表
- **pilot_location_history** - 飞手位置历史表

### 配置文件
[`uav-service/src/main/resources/application.yml`](uav-service/src/main/resources/application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/uav_dev?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

---

## 📋 create_dispatch_tables.sql 详解

### 表1：mission_job（任务调度关联表）

**用途**：关联无人机任务与XXL-JOB调度任务

**字段说明**：
```sql
CREATE TABLE `mission_job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mission_id` bigint NOT NULL COMMENT '任务ID（关联uav_mission表）',
  `job_id` int NOT NULL COMMENT 'XXL-JOB任务ID',
  `job_group` int NOT NULL COMMENT 'XXL-JOB执行器组ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-运行中，2-已停止',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mission_id` (`mission_id`),
  KEY `idx_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**对应实体类**：[`MissionJob.java`](uav-service/src/main/java/com/uav/service/domain/MissionJob.java)

**使用场景**：
- 创建派单任务时，保存任务ID和XXL-JOB任务ID的关联
- 停止派单时，通过mission_id查找对应的job_id

---

### 表2：xxl_job_log（执行日志表）

**用途**：记录派单任务的执行日志

**字段说明**：
```sql
CREATE TABLE `xxl_job_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_group` int NOT NULL COMMENT '执行器组ID',
  `job_id` int NOT NULL COMMENT '任务ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '任务handler名称',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '任务参数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度时间',
  `trigger_code` int DEFAULT NULL COMMENT '调度结果码：200-成功，500-失败',
  `trigger_msg` text COMMENT '调度日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行时间',
  `handle_code` int DEFAULT NULL COMMENT '执行结果码：200-成功，500-失败',
  `handle_msg` text COMMENT '执行日志',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_trigger_time` (`trigger_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**对应实体类**：[`XxlJobLog.java`](uav-service/src/main/java/com/uav/service/domain/XxlJobLog.java)

**使用场景**：
- 记录每次派单任务的执行情况
- 用于问题排查和统计分析

---

### 表3：pilot_location_history（飞手位置历史表）

**用途**：记录飞手的位置变化历史

**字段说明**：
```sql
CREATE TABLE `pilot_location_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pilot_id` bigint NOT NULL COMMENT '飞手ID',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
  `address` varchar(255) DEFAULT NULL COMMENT '地址描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pilot_id` (`pilot_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_pilot_time` (`pilot_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**使用场景**：
- 保存飞手位置更新历史
- 用于轨迹回放和数据分析

---

## 🔗 数据库关系说明

### 跨数据库关联

虽然使用两个独立的数据库，但它们通过**应用层**进行关联：

```
UAV-SERVICE (uav_dev数据库)
    ↓
创建派单任务
    ↓
调用 XxlJobClient.addJob()
    ↓
XXL-JOB Admin (xxl_job数据库)
    ↓
返回 job_id
    ↓
保存到 mission_job 表 (uav_dev数据库)
```

### 为什么使用两个数据库？

1. **职责分离**
   - xxl_job：专门用于任务调度
   - uav_dev：专门用于业务数据

2. **独立部署**
   - XXL-JOB Admin 可以独立部署和升级
   - 不影响业务数据库

3. **数据隔离**
   - 调度数据和业务数据分开存储
   - 便于备份和维护

---

## ✅ 完整部署检查清单

### 数据库部分

- [ ] 创建 xxl_job 数据库
- [ ] 导入 XXL-JOB 官方 SQL 脚本
- [ ] 验证 xxl_job 数据库表已创建
- [ ] 创建 uav_dev 数据库（如果还没有）
- [ ] 执行 create_dispatch_tables.sql
- [ ] 验证 uav_dev 数据库新增3张表

### 配置部分

- [ ] 修改 xxl-job-admin 的数据库配置（指向 xxl_job）
- [ ] 修改 uav-service 的数据库配置（指向 uav_dev）
- [ ] 确认两个配置文件的数据库地址、用户名、密码正确

### 启动验证

- [ ] 启动 XXL-JOB Admin
- [ ] 访问管理后台 http://localhost:8080/xxl-job-admin
- [ ] 启动 UAV-SERVICE
- [ ] 验证执行器已注册
- [ ] 测试派单功能

---

## 🔧 常见问题

### Q1: 为什么 uav_dev 数据库中有 xxl_job_log 表？
**A**: 这是为了在 UAV-SERVICE 中记录任务执行日志，方便业务系统查询和统计。这个表和 xxl_job 数据库中的 xxl_job_log 表是独立的。

### Q2: mission_job 表的 job_id 和 xxl_job 数据库有什么关系？
**A**: mission_job.job_id 存储的是 XXL-JOB Admin 返回的任务ID，用于后续启动、停止任务时调用 XXL-JOB API。

### Q3: 可以使用同一个数据库吗？
**A**: 技术上可以，但不推荐。使用两个独立数据库可以更好地分离职责，便于维护和扩展。

### Q4: 如何清理测试数据？
**A**: 
```sql
-- 清理 uav_dev 数据库的派单相关表
USE uav_dev;
TRUNCATE TABLE mission_job;
TRUNCATE TABLE xxl_job_log;
TRUNCATE TABLE pilot_location_history;

-- 清理 xxl_job 数据库的任务数据（谨慎操作）
USE xxl_job;
DELETE FROM xxl_job_info WHERE job_group = 2; -- 假设执行器组ID是2
```

---

## 📚 相关文档

- [`create_dispatch_tables.sql`](create_dispatch_tables.sql) - 数据库建表脚本
- [`XXL_JOB_ADMIN_SETUP_GUIDE.md`](XXL_JOB_ADMIN_SETUP_GUIDE.md) - XXL-JOB Admin 配置指南
- [`XXL_JOB_COMPLETE_SUMMARY.md`](XXL_JOB_COMPLETE_SUMMARY.md) - 派单系统完整总结

---

**文档版本**: v1.0  
**最后更新**: 2026-02-23  
**维护人员**: Roo AI Assistant