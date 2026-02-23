# XXL-JOB Admin 配置和启动指南

## 📋 概述

本文档说明如何配置和启动 XXL-JOB 调度中心（xxl-job-admin）。

**好消息**：您复制的 xxl-job-admin 项目已经包含了所需的自定义 API，无需额外修改代码！

---

## ✅ 已完成的修改

### 1. 配置文件修改

**文件**: [`xxl-job-admin/src/main/resources/application.properties`](xxl-job-admin/src/main/resources/application.properties:37)

已添加数据库配置说明注释（第37-43行）：
```properties
### xxl-job, datasource
### 重要提示：请根据您的实际数据库配置修改以下参数
### 1. 数据库地址：localhost:3306 改为您的MySQL地址和端口
### 2. 数据库名称：xxl_job（需要先创建此数据库并导入官方SQL脚本）
### 3. 用户名：root 改为您的MySQL用户名
### 4. 密码：root 改为您的MySQL密码
spring.datasource.url=jdbc:mysql://localhost:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 2. 自定义 API 已存在 ✅

**文件**: [`xxl-job-admin/src/main/java/com/xxl/job/admin/controller/JobInfoController.java`](xxl-job-admin/src/main/java/com/xxl/job/admin/controller/JobInfoController.java:181)

您的 xxl-job-admin 项目已经包含了所需的自定义 API（第181-236行），包括：

#### 已有的 API 方法：

1. **添加任务** - `addJob` (第186行)
   ```java
   @RequestMapping("/addJob")
   @ResponseBody
   @PermissionLimit(limit = false)
   public ReturnT<String> addJobInfo(@RequestBody XxlJobInfo jobInfo)
   ```

2. **删除任务** - `removeJob` (第194行)
   ```java
   @RequestMapping("/removeJob")
   @ResponseBody
   @PermissionLimit(limit = false)
   public ReturnT<String> removeJob(@RequestBody XxlJobInfo jobInfo)
   ```

3. **修改任务** - `updateJob` (第202行)
   ```java
   @RequestMapping("/updateJob")
   @ResponseBody
   @PermissionLimit(limit = false)
   public ReturnT<String> updateJob(@RequestBody XxlJobInfo jobInfo)
   ```

4. **停止任务** - `stopJob` (第210行) ⭐
   ```java
   @RequestMapping("/stopJob")
   @ResponseBody
   @PermissionLimit(limit = false)
   public ReturnT<String> pauseJob(@RequestBody XxlJobInfo jobInfo)
   ```

5. **启动任务** - `startJob` (第218行) ⭐
   ```java
   @RequestMapping("/startJob")
   @ResponseBody
   @PermissionLimit(limit = false)
   public ReturnT<String> startJob(@RequestBody XxlJobInfo jobInfo)
   ```

6. **添加并启动任务** - `addAndStartJob` (第226行)
   ```java
   @RequestMapping("/addAndStartJob")
   @ResponseBody
   @PermissionLimit(limit = false)
   public ReturnT<String> addAndStartJob(@RequestBody XxlJobInfo jobInfo)
   ```

**重要说明**：
- 所有自定义 API 都使用了 `@PermissionLimit(limit = false)` 注解
- 这意味着这些 API 可以被外部系统（如 uav-service）直接调用，无需登录验证
- 这正是我们在 [`XxlJobClient.java`](uav-service/src/main/java/com/uav/service/config/xxl/XxlJobClient.java) 中调用的 API

---

## 🚀 部署步骤

### 步骤1：初始化数据库

#### 1.1 创建数据库
```sql
CREATE DATABASE xxl_job DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 1.2 下载并导入官方 SQL 脚本

**下载地址**：
- GitHub: https://github.com/xuxueli/xxl-job/blob/master/doc/db/tables_xxl_job.sql
- 或使用您本地的 xxl-job-master 项目中的 SQL 文件

**导入命令**：
```bash
mysql -u root -p xxl_job < tables_xxl_job.sql
```

或使用 Navicat/MySQL Workbench 等工具导入。

---

### 步骤2：修改配置文件

编辑 [`xxl-job-admin/src/main/resources/application.properties`](xxl-job-admin/src/main/resources/application.properties:37)

**必须修改的配置**：
```properties
# 数据库地址（根据实际情况修改）
spring.datasource.url=jdbc:mysql://localhost:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai

# 数据库用户名（根据实际情况修改）
spring.datasource.username=root

# 数据库密码（根据实际情况修改）
spring.datasource.password=your_password
```

**可选配置**：
```properties
# 服务端口（默认8080）
server.port=8080

# 访问令牌（建议修改）
xxl.job.accessToken=default_token

# 日志保留天数
xxl.job.logretentiondays=30
```

---

### 步骤3：编译项目

在 xxl-job-admin 目录下执行：

```bash
cd D:/uav/uav-dev/xxl-job-admin
mvn clean package -DskipTests
```

编译成功后，会在 `target/` 目录下生成 `xxl-job-admin-2.x.x.jar` 文件。

---

### 步骤4：启动服务

#### 方式一：使用 jar 包启动（推荐）

```bash
cd D:/uav/uav-dev/xxl-job-admin/target
java -jar xxl-job-admin-2.x.x.jar
```

#### 方式二：使用 Maven 启动

```bash
cd D:/uav/uav-dev/xxl-job-admin
mvn spring-boot:run
```

#### 方式三：在 IDEA 中启动

1. 用 IDEA 打开 xxl-job-admin 项目
2. 找到主类 `XxlJobAdminApplication.java`
3. 右键 → Run 'XxlJobAdminApplication'

---

### 步骤5：验证启动

#### 5.1 检查启动日志

看到以下日志表示启动成功：
```
xxl-job remoting server start success, nettype = class com.xxl.job.admin.core.thread.JobTriggerPoolHelper, port = 8080
```

#### 5.2 访问管理后台

浏览器访问：http://localhost:8080/xxl-job-admin

**默认账号**：
- 用户名：`admin`
- 密码：`123456`

#### 5.3 测试自定义 API

使用 Postman 或 curl 测试：

**测试 startJob API**：
```bash
curl -X POST http://localhost:8080/xxl-job-admin/jobinfo/startJob \
  -H "Content-Type: application/json" \
  -d '{"id": 1}'
```

**测试 stopJob API**：
```bash
curl -X POST http://localhost:8080/xxl-job-admin/jobinfo/stopJob \
  -H "Content-Type: application/json" \
  -d '{"id": 1}'
```

---

## 🔗 与 UAV-SERVICE 的集成

### 配置说明

UAV-SERVICE 的配置文件 [`uav-service/src/main/resources/application.yml`](uav-service/src/main/resources/application.yml:89) 中已配置：

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

### 工作流程

1. **UAV-SERVICE 启动**
   - 执行器自动注册到 XXL-JOB Admin
   - 可在管理后台看到 "uav-service-executor" 执行器

2. **创建派单任务**
   - 调用 [`DispatchService.startDispatch()`](uav-service/src/main/java/com/uav/service/dispatch/service/impl/DispatchServiceImpl.java:89)
   - 通过 [`XxlJobClient`](uav-service/src/main/java/com/uav/service/config/xxl/XxlJobClient.java) 调用 XXL-JOB Admin API
   - 创建定时任务（每分钟执行一次）

3. **任务执行**
   - XXL-JOB Admin 定时触发任务
   - 调用 UAV-SERVICE 的 [`MissionDispatchJobHandler`](uav-service/src/main/java/com/uav/service/dispatch/job/MissionDispatchJobHandler.java:35)
   - 执行派单逻辑

4. **停止任务**
   - 飞手接单后调用 [`DispatchService.acceptMission()`](uav-service/src/main/java/com/uav/service/dispatch/service/impl/DispatchServiceImpl.java:197)
   - 通过 [`XxlJobClient`](uav-service/src/main/java/com/uav/service/config/xxl/XxlJobClient.java) 停止任务

---

## 📊 管理后台功能

### 执行器管理
- 查看已注册的执行器
- 查看执行器状态和地址

### 任务管理
- 查看所有任务
- 手动触发任务
- 查看任务执行日志
- 启动/停止任务

### 调度日志
- 查看任务调度记录
- 查看执行结果
- 查看错误信息

---

## ⚠️ 常见问题

### Q1: 启动失败，提示数据库连接错误
**A**: 检查以下几点：
1. MySQL 服务是否启动
2. 数据库 xxl_job 是否已创建
3. 用户名密码是否正确
4. 数据库地址和端口是否正确

### Q2: UAV-SERVICE 无法连接到 XXL-JOB Admin
**A**: 检查以下几点：
1. XXL-JOB Admin 是否已启动
2. 端口 8080 是否被占用
3. UAV-SERVICE 配置的地址是否正确
4. 防火墙是否阻止了连接

### Q3: 执行器注册失败
**A**: 检查以下几点：
1. UAV-SERVICE 是否已启动
2. 执行器端口 9999 是否被占用
3. XXL-JOB Admin 是否能访问 UAV-SERVICE 的地址

### Q4: 任务创建失败
**A**: 检查以下几点：
1. 执行器是否已注册成功
2. JobHandler 名称是否正确（missionDispatchJobHandler）
3. 查看 XXL-JOB Admin 日志

### Q5: 自定义 API 调用失败
**A**: 检查以下几点：
1. API 地址是否正确
2. 请求方法是否为 POST
3. Content-Type 是否为 application/json
4. 请求体格式是否正确

---

## 🔧 高级配置

### 修改端口

如果 8080 端口被占用，可以修改：
```properties
server.port=8081
```

同时需要修改 UAV-SERVICE 的配置：
```yaml
xxl:
  job:
    admin:
      addresses: http://localhost:8081/xxl-job-admin
```

### 配置邮件告警

编辑 [`application.properties`](xxl-job-admin/src/main/resources/application.properties:43)：
```properties
spring.mail.host=smtp.qq.com
spring.mail.port=25
spring.mail.username=your_email@qq.com
spring.mail.from=your_email@qq.com
spring.mail.password=your_password
```

### 修改访问令牌

编辑 [`application.properties`](xxl-job-admin/src/main/resources/application.properties:55)：
```properties
xxl.job.accessToken=your_custom_token
```

同时需要修改 UAV-SERVICE 的配置（如果需要）。

---

## 📝 总结

### 已完成的工作
✅ 配置文件已添加详细注释  
✅ 自定义 API 已存在（无需修改代码）  
✅ 创建了完整的部署指南

### 您需要做的
1. ⬜ 创建 xxl_job 数据库
2. ⬜ 导入官方 SQL 脚本
3. ⬜ 修改数据库配置（用户名、密码）
4. ⬜ 编译项目
5. ⬜ 启动 XXL-JOB Admin
6. ⬜ 验证管理后台可访问
7. ⬜ 启动 UAV-SERVICE
8. ⬜ 测试派单功能

### 下一步
完成上述步骤后，整个派单系统就可以正常工作了！

---

**文档版本**: v1.0  
**最后更新**: 2026-02-23  
**维护人员**: Roo AI Assistant