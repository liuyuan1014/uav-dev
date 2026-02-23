# UAV项目架构重构 - 阶段九：service-account账户服务

## 变更时间
2026-02-24

## 变更概述
成功拆分service-account（账户服务），负责飞手账户管理、余额操作、账户明细记录等功能。

## 服务信息
- **服务名称**: service-account
- **端口**: 8206
- **Dubbo端口**: 20886
- **包名**: com.uav.account
- **数据库**: uav_account

## 创建的文件

### 1. 配置文件 (2个)
- `uav-services/service-account/pom.xml` - Maven配置
- `uav-services/service-account/src/main/resources/application.yml` - 应用配置
- `uav-services/service-account/src/main/resources/bootstrap.yml` - 启动配置

### 2. 启动类 (1个)
- `ServiceAccountApplication.java` - Spring Boot启动类

### 3. Mapper层 (3个)
- `PilotAccountMapper.java` - 飞手账户Mapper接口
- `PilotAccountDetailMapper.java` - 账户明细Mapper接口
- `PilotAccountMapper.xml` - MyBatis XML映射文件

### 4. Service层 (4个)
- `PilotAccountService.java` - 账户服务接口
- `PilotAccountServiceImpl.java` - 账户服务实现
- `PilotAccountDetailService.java` - 账户明细服务接口
- `PilotAccountDetailServiceImpl.java` - 账户明细服务实现

## 核心功能

### 1. 账户管理 (PilotAccountService)
```java
- getByPilotId(Long pilotId) - 根据飞手ID获取账户
- addAmount(Long pilotId, BigDecimal amount) - 增加余额
- reduceAmount(Long pilotId, BigDecimal amount) - 减少余额
- freezeAmount(Long pilotId, BigDecimal amount) - 冻结金额
- unfreezeAmount(Long pilotId, BigDecimal amount) - 解冻金额
- initAccount(Long pilotId) - 初始化账户
```

### 2. 账户明细 (PilotAccountDetailService)
```java
- createDetail() - 创建账户明细记录
- getByPilotId(Long pilotId) - 获取飞手账户明细列表
- getByMissionId(Long missionId) - 根据任务ID获取明细
- calculateTotalIncome(Long pilotId) - 计算总收入
- calculateTotalExpense(Long pilotId) - 计算总支出
```

## 技术特点

### 1. 事务管理
- 所有金额操作使用`@Transactional`保证原子性
- 金额验证：必须大于0
- 余额检查：减少/冻结前验证可用余额

### 2. 数据库操作
- 使用MyBatis-Plus的BaseMapper
- 自定义SQL实现复杂的金额更新操作
- 乐观锁机制防止并发问题

### 3. 业务逻辑
- 账户初始化：所有金额字段初始化为0
- 明细记录：每次操作记录当前余额快照
- 收支统计：支持按交易类型统计总额

## 遇到的问题及解决

### 问题1: 实体类包路径错误
**现象**: 编译时找不到`com.uav.model.entity.account.PilotAccount`
**原因**: 账户实体类实际在`com.uav.model.entity.pilot`包下
**解决**: 批量修改所有import语句，使用正确的包路径

## 配置说明

### application.yml关键配置
```yaml
server:
  port: 8206

spring:
  application:
    name: service-account
  datasource:
    url: jdbc:mysql://localhost:3306/uav_account

dubbo:
  protocol:
    port: 20886
  scan:
    base-packages: com.uav.account.service

mybatis-plus:
  type-aliases-package: com.uav.model.entity.account
```

## 编译结果
✅ 编译成功 (BUILD SUCCESS, 5.367s)

## 文件统计
- Java文件: 7个
- 配置文件: 3个
- 总计: 10个文件

## 下一步计划
- 阶段十：拆分service-telemetry遥测服务
- 阶段十一：创建所有service-client模块
- 阶段十二：服务间调用改造与测试