# UAV Model 文件迁移指南

## 📋 文件迁移映射表

### 实体类（Entity）迁移

#### 任务相关实体
| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/domain/BaseEntity.java` | `uav-model/src/main/java/com/uav/model/entity/base/BaseEntity.java` | `com.uav.model.entity.base` |
| `uav-service/src/main/java/com/uav/service/domain/UavMission.java` | `uav-model/src/main/java/com/uav/model/entity/mission/UavMission.java` | `com.uav.model.entity.mission` |
| `uav-service/src/main/java/com/uav/service/domain/MissionBill.java` | `uav-model/src/main/java/com/uav/model/entity/mission/MissionBill.java` | `com.uav.model.entity.mission` |
| `uav-service/src/main/java/com/uav/service/domain/MissionComment.java` | `uav-model/src/main/java/com/uav/model/entity/mission/MissionComment.java` | `com.uav.model.entity.mission` |
| `uav-service/src/main/java/com/uav/service/domain/MissionMonitor.java` | `uav-model/src/main/java/com/uav/model/entity/mission/MissionMonitor.java` | `com.uav.model.entity.mission` |
| `uav-service/src/main/java/com/uav/service/domain/MissionStatusLog.java` | `uav-model/src/main/java/com/uav/model/entity/mission/MissionStatusLog.java` | `com.uav.model.entity.mission` |
| `uav-service/src/main/java/com/uav/service/domain/MissionJob.java` | `uav-model/src/main/java/com/uav/model/entity/mission/MissionJob.java` | `com.uav.model.entity.mission` |

#### 飞手相关实体
| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/domain/UavPilot.java` | `uav-model/src/main/java/com/uav/model/entity/pilot/UavPilot.java` | `com.uav.model.entity.pilot` |
| `uav-service/src/main/java/com/uav/service/domain/PilotAccount.java` | `uav-model/src/main/java/com/uav/model/entity/pilot/PilotAccount.java` | `com.uav.model.entity.pilot` |
| `uav-service/src/main/java/com/uav/service/domain/PilotAccountDetail.java` | `uav-model/src/main/java/com/uav/model/entity/pilot/PilotAccountDetail.java` | `com.uav.model.entity.pilot` |
| `uav-service/src/main/java/com/uav/service/domain/PilotLoginLog.java` | `uav-model/src/main/java/com/uav/model/entity/pilot/PilotLoginLog.java` | `com.uav.model.entity.pilot` |
| `uav-service/src/main/java/com/uav/service/domain/PilotSettings.java` | `uav-model/src/main/java/com/uav/model/entity/pilot/PilotSettings.java` | `com.uav.model.entity.pilot` |
| `uav-service/src/main/java/com/uav/service/pilot/domain/PilotCertificationAudit.java` | `uav-model/src/main/java/com/uav/model/entity/pilot/PilotCertificationAudit.java` | `com.uav.model.entity.pilot` |

#### 设备相关实体
| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/domain/UavDevice.java` | `uav-model/src/main/java/com/uav/model/entity/device/UavDevice.java` | `com.uav.model.entity.device` |

#### 调度相关实体
| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/domain/XxlJobLog.java` | `uav-model/src/main/java/com/uav/model/entity/dispatch/XxlJobLog.java` | `com.uav.model.entity.dispatch` |

#### 遥测相关实体
| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/telemetry/UavTelemetryEntity.java` | `uav-model/src/main/java/com/uav/model/entity/telemetry/UavTelemetryEntity.java` | `com.uav.model.entity.telemetry` |

---

### 枚举类（Enum）迁移

| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/domain/MissionStatus.java` | `uav-model/src/main/java/com/uav/model/enums/MissionStatus.java` | `com.uav.model.enums` |
| `uav-service/src/main/java/com/uav/service/domain/CancelReasonEnum.java` | `uav-model/src/main/java/com/uav/model/enums/CancelReasonEnum.java` | `com.uav.model.enums` |
| `uav-service/src/main/java/com/uav/service/domain/OperatorTypeEnum.java` | `uav-model/src/main/java/com/uav/model/enums/OperatorTypeEnum.java` | `com.uav.model.enums` |
| `uav-service/src/main/java/com/uav/service/pilot/domain/PilotCertificationStatus.java` | `uav-model/src/main/java/com/uav/model/enums/PilotCertificationStatus.java` | `com.uav.model.enums` |

---

### Form类迁移

| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/pilot/form/UpdatePilotAuthInfoForm.java` | `uav-model/src/main/java/com/uav/model/form/pilot/UpdatePilotAuthInfoForm.java` | `com.uav.model.form.pilot` |
| `uav-service/src/main/java/com/uav/service/dispatch/form/StartDispatchForm.java` | `uav-model/src/main/java/com/uav/model/form/dispatch/StartDispatchForm.java` | `com.uav.model.form.dispatch` |
| `uav-service/src/main/java/com/uav/service/location/form/UpdateLocationForm.java` | `uav-model/src/main/java/com/uav/model/form/location/UpdateLocationForm.java` | `com.uav.model.form.location` |

---

### VO类迁移

| 源文件路径 | 目标路径 | 新包名 |
|-----------|---------|--------|
| `uav-service/src/main/java/com/uav/service/pilot/vo/PilotInfoVo.java` | `uav-model/src/main/java/com/uav/model/vo/pilot/PilotInfoVo.java` | `com.uav.model.vo.pilot` |
| `uav-service/src/main/java/com/uav/service/pilot/vo/PilotAuthInfoVo.java` | `uav-model/src/main/java/com/uav/model/vo/pilot/PilotAuthInfoVo.java` | `com.uav.model.vo.pilot` |
| `uav-service/src/main/java/com/uav/service/dispatch/vo/DispatchRecordVo.java` | `uav-model/src/main/java/com/uav/model/vo/dispatch/DispatchRecordVo.java` | `com.uav.model.vo.dispatch` |
| `uav-service/src/main/java/com/uav/service/dispatch/vo/DispatchStatisticsVo.java` | `uav-model/src/main/java/com/uav/model/vo/dispatch/DispatchStatisticsVo.java` | `com.uav.model.vo.dispatch` |
| `uav-service/src/main/java/com/uav/service/location/vo/PilotLocationVo.java` | `uav-model/src/main/java/com/uav/model/vo/location/PilotLocationVo.java` | `com.uav.model.vo.location` |
| `uav-service/src/main/java/com/uav/service/location/vo/NearbyPilotVo.java` | `uav-model/src/main/java/com/uav/model/vo/location/NearbyPilotVo.java` | `com.uav.model.vo.location` |
| `uav-service/src/main/java/com/uav/model/vo/UavStatusVO.java` | `uav-model/src/main/java/com/uav/model/vo/device/UavStatusVO.java` | `com.uav.model.vo.device` |

---

## 🔧 使用IDE进行迁移的详细步骤

### 方法一：逐个文件迁移（推荐新手）

#### 1. 迁移实体类示例（以UavPilot为例）

**步骤：**
1. 在IDE中打开 `uav-service/src/main/java/com/uav/service/domain/UavPilot.java`
2. 右键点击文件 → 选择 **Refactor** → **Move**
3. 在弹出的对话框中，选择目标包：`com.uav.model.entity.pilot`
4. 如果目标包不存在，IDE会提示创建，点击确认
5. IDE会自动：
   - 移动文件到新位置
   - 更新文件的package声明
   - 更新所有引用该类的import语句
6. 点击 **Refactor** 确认

**注意事项：**
- 确保uav-model模块已经在项目中正确配置
- 如果IDE提示找不到目标包，需要先手动创建目录结构

#### 2. 批量迁移同类文件

**步骤：**
1. 在IDE的Project视图中，选中多个需要迁移的文件（按住Ctrl/Cmd多选）
2. 右键 → **Refactor** → **Move**
3. 选择目标包（例如：`com.uav.model.entity.mission`）
4. 确认迁移

**适用场景：**
- 迁移所有任务相关实体到 `entity/mission/`
- 迁移所有飞手相关实体到 `entity/pilot/`
- 迁移所有枚举到 `enums/`

---

### 方法二：使用文件系统+IDE重构（推荐熟练用户）

#### 步骤1：创建目标目录结构
在uav-model模块下创建以下目录：
```
uav-model/src/main/java/com/uav/model/
├── entity/
│   ├── base/          ✅ 已创建
│   ├── mission/       ✅ 已创建
│   ├── pilot/         ⬅️ 需要创建
│   ├── device/        ⬅️ 需要创建
│   ├── dispatch/      ⬅️ 需要创建
│   └── telemetry/     ⬅️ 需要创建
├── form/
│   ├── pilot/         ⬅️ 需要创建
│   ├── dispatch/      ⬅️ 需要创建
│   └── location/      ⬅️ 需要创建
├── vo/
│   ├── pilot/         ⬅️ 需要创建
│   ├── dispatch/      ⬅️ 需要创建
│   ├── location/      ⬅️ 需要创建
│   └── device/        ⬅️ 需要创建
└── enums/             ✅ 已创建
```

#### 步骤2：复制文件
1. 使用文件管理器复制文件到对应目录
2. 或在IDE中拖拽文件到目标目录

#### 步骤3：批量修改包名
1. 在IDE中选中uav-model模块
2. 使用 **Find and Replace in Path** (Ctrl+Shift+R / Cmd+Shift+R)
3. 查找：`package com.uav.service.domain`
4. 替换为对应的新包名（根据文件所在目录）：
   - `package com.uav.model.entity.mission` (任务实体)
   - `package com.uav.model.entity.pilot` (飞手实体)
   - `package com.uav.model.entity.device` (设备实体)
   - `package com.uav.model.enums` (枚举)

#### 步骤4：批量修改import语句
1. 在整个项目范围内使用 **Find and Replace in Path**
2. 查找：`import com.uav.service.domain.`
3. 替换为：`import com.uav.model.entity.` 或 `import com.uav.model.enums.`
4. 需要根据具体类型调整

---

## ✅ 迁移检查清单

### 迁移前检查
- [ ] 确保uav-model模块的pom.xml已创建
- [ ] 确保根pom.xml已添加uav-model模块
- [ ] 备份当前代码（Git commit）

### 迁移中检查
- [ ] 每个文件的package声明已更新
- [ ] 文件已移动到正确的目录
- [ ] 继承BaseEntity的类import路径已更新

### 迁移后检查
- [ ] 运行 `mvn clean compile` 确保编译通过
- [ ] 检查IDE中是否有红色错误标记
- [ ] 检查所有import语句是否正确
- [ ] 提交Git commit记录迁移

---

## 🚨 常见问题

### Q1: IDE提示找不到目标包
**解决方案：**
1. 手动在文件系统中创建目录结构
2. 在IDE中右键项目 → **Reload from Disk** 或 **Synchronize**
3. 重新尝试Move操作

### Q2: 迁移后编译报错
**解决方案：**
1. 检查package声明是否正确
2. 检查import语句是否更新
3. 运行 `mvn clean install` 重新构建
4. 在IDE中 **Invalidate Caches and Restart**

### Q3: 循环依赖问题
**解决方案：**
- uav-model不应该依赖任何业务模块
- 只依赖基础框架（MyBatis-Plus、Lombok等）
- 如果出现循环依赖，检查pom.xml配置

---

## 📝 迁移后的下一步

完成uav-model迁移后：
1. 更新uav-service的pom.xml，添加uav-model依赖
2. 删除uav-service中已迁移的文件（确认无误后）
3. 运行完整的测试套件
4. 继续进行阶段三：创建service父模块

---

## 💡 提示

- **建议分批迁移**：先迁移实体类，再迁移枚举，最后迁移Form和VO
- **及时提交**：每完成一批迁移就提交一次Git
- **保持旧代码**：在确认新架构完全正常前，不要删除旧文件