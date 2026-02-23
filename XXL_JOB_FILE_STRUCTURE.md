# XXL-JOB派单系统文件结构规划

## 📁 建议的目录结构

```
uav-service/
├── pom.xml (修改：添加xxl-job依赖)
├── src/main/
│   ├── java/com/uav/
│   │   ├── common/
│   │   │   └── constant/
│   │   │       └── RedisConstant.java ✅ (已创建)
│   │   │
│   │   └── service/
│   │       ├── config/
│   │       │   └── xxl/
│   │       │       ├── XxlJobConfig.java (NEW)
│   │       │       ├── XxlJobClientConfig.java (NEW)
│   │       │       └── XxlJobClient.java (NEW)
│   │       │
│   │       ├── domain/
│   │       │   ├── MissionJob.java (NEW)
│   │       │   └── XxlJobLog.java (NEW)
│   │       │
│   │       └── mapper/
│   │           ├── MissionJobMapper.java (NEW)
│   │           └── XxlJobLogMapper.java (NEW)
│   │
│   └── resources/
│       ├── application.yml (修改：添加xxl-job配置)
│       └── mapper/
│           ├── MissionJobMapper.xml (NEW)
│           └── XxlJobLogMapper.xml (NEW)
```

## 🎯 第一批文件清单（9个文件）

### 1. XXL-JOB配置类（3个）
```
📂 uav-service/src/main/java/com/uav/service/config/xxl/
├── XxlJobConfig.java          # 执行器配置（参考代驾项目）
├── XxlJobClientConfig.java    # 客户端配置属性
└── XxlJobClient.java           # 任务管理客户端（调用XXL-JOB API）
```

**说明**：
- `XxlJobConfig.java` - 配置执行器，连接调度中心
- `XxlJobClientConfig.java` - 读取配置文件中的client配置
- `XxlJobClient.java` - 封装任务的增删改查操作

### 2. 实体类（2个）
```
📂 uav-service/src/main/java/com/uav/service/domain/
├── MissionJob.java    # 任务调度关联表实体
└── XxlJobLog.java     # XXL-JOB执行日志实体
```

**说明**：
- 与数据库表对应
- 使用MyBatis-Plus注解
- 继承BaseEntity（如果有）

### 3. Mapper接口（2个）
```
📂 uav-service/src/main/java/com/uav/service/mapper/
├── MissionJobMapper.java    # 任务关联Mapper
└── XxlJobLogMapper.java     # 日志Mapper
```

### 4. MyBatis XML映射（2个）
```
📂 uav-service/src/main/resources/mapper/
├── MissionJobMapper.xml    # 任务关联SQL映射
└── XxlJobLogMapper.xml     # 日志SQL映射
```

### 5. 配置文件（2个修改）
```
📂 uav-service/
├── pom.xml                              # 添加xxl-job-core依赖
└── src/main/resources/application.yml  # 添加xxl-job配置
```

---

## 🤔 需要讨论的问题

### 问题1：XXL-JOB配置类的位置
**选项A（推荐）**：
```
uav-service/src/main/java/com/uav/service/config/xxl/
```
- ✅ 优点：与service模块其他配置类在一起，结构清晰
- ✅ 优点：符合Spring Boot配置类的常规位置
- ❌ 缺点：路径稍长

**选项B**：
```
uav-service/src/main/java/com/uav/service/xxl/config/
```
- ✅ 优点：所有xxl相关代码集中在xxl包下
- ❌ 缺点：与其他配置类分离

**你的选择**：A 还是 B？或者其他建议？

---

### 问题2：实体类的位置
**选项A（推荐）**：
```
uav-service/src/main/java/com/uav/service/domain/
├── MissionJob.java
└── XxlJobLog.java
```
- ✅ 优点：与现有实体类（UavMission、PilotAccount等）在一起
- ✅ 优点：符合你当前的项目结构
- ❌ 缺点：domain包会越来越大

**选项B**：
```
uav-service/src/main/java/com/uav/service/xxl/domain/
├── MissionJob.java
└── XxlJobLog.java
```
- ✅ 优点：xxl相关实体独立管理
- ❌ 缺点：与其他实体类分离

**你的选择**：A 还是 B？

---

### 问题3：Mapper的位置
**选项A（推荐）**：
```
uav-service/src/main/java/com/uav/service/mapper/
├── MissionJobMapper.java
└── XxlJobLogMapper.java
```
- ✅ 优点：与现有Mapper在一起
- ✅ 优点：MyBatis扫描配置不需要修改

**选项B**：
```
uav-service/src/main/java/com/uav/service/xxl/mapper/
├── MissionJobMapper.java
└── XxlJobLogMapper.java
```
- ✅ 优点：xxl相关Mapper独立
- ❌ 缺点：需要修改MyBatis扫描路径

**你的选择**：A 还是 B？

---

### 问题4：是否需要单独的xxl模块？
**选项A（推荐）**：集成到uav-service模块
```
uav-service/
├── config/xxl/
├── domain/ (包含MissionJob、XxlJobLog)
├── mapper/ (包含MissionJobMapper、XxlJobLogMapper)
└── ...
```
- ✅ 优点：简单直接，不增加模块复杂度
- ✅ 优点：适合当前项目规模
- ❌ 缺点：如果未来xxl相关代码很多，会显得混乱

**选项B**：创建独立的xxl子包
```
uav-service/src/main/java/com/uav/service/xxl/
├── config/
├── domain/
├── mapper/
├── service/
└── job/
```
- ✅ 优点：xxl相关代码高度内聚
- ✅ 优点：便于后续维护和扩展
- ❌ 缺点：增加了一层包结构

**你的选择**：A 还是 B？

---

## 💡 我的建议

基于你当前的项目结构和代驾项目的经验，我建议：

### 推荐方案：**混合方式**
```
uav-service/
├── src/main/java/com/uav/
│   ├── common/constant/
│   │   └── RedisConstant.java ✅
│   │
│   └── service/
│       ├── config/
│       │   └── xxl/                    # XXL-JOB配置（独立）
│       │       ├── XxlJobConfig.java
│       │       ├── XxlJobClientConfig.java
│       │       └── XxlJobClient.java
│       │
│       ├── domain/                      # 实体类（集成）
│       │   ├── MissionJob.java
│       │   ├── XxlJobLog.java
│       │   └── ... (其他实体)
│       │
│       └── mapper/                      # Mapper（集成）
│           ├── MissionJobMapper.java
│           ├── XxlJobLogMapper.java
│           └── ... (其他Mapper)
```

**理由**：
1. ✅ 配置类独立（`config/xxl/`），便于管理XXL-JOB相关配置
2. ✅ 实体类和Mapper集成到现有结构，保持一致性
3. ✅ 不增加过多的包层级，保持简洁
4. ✅ 符合Spring Boot和MyBatis的最佳实践

---

## ❓ 请确认

请告诉我你的选择：
1. **XXL-JOB配置类位置**：A（config/xxl/）还是 B（xxl/config/）？
2. **实体类位置**：A（domain/）还是 B（xxl/domain/）？
3. **Mapper位置**：A（mapper/）还是 B（xxl/mapper/）？
4. **整体结构**：A（集成）还是 B（独立xxl包）？

或者，你可以直接说：**"按照你的推荐方案来"**，我就按照上面的混合方式创建文件。