# 快速修复包名脚本指南

## 🚀 使用IDEA批量修复（最快方法）

### 步骤1：打开全局替换
按 `Ctrl+Shift+R` (Windows) 或 `Cmd+Shift+R` (Mac)

### 步骤2：按顺序执行以下替换操作

#### 替换1：修复Entity - Pilot实体（6个文件）
```
路径: uav-model/src/main/java/com/uav/model/entity/pilot
查找: package com.uav.service.domain;
替换为: package com.uav.model.entity.pilot;
```
点击 "Replace All"

#### 替换2：修复Entity - Pilot特殊情况（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/entity/pilot
查找: package com.uav.service.pilot.domain;
替换为: package com.uav.model.entity.pilot;
```
点击 "Replace All"

#### 替换3：修复Entity - Device设备（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/entity/device
查找: package com.uav.service.domain;
替换为: package com.uav.model.entity.device;
```
点击 "Replace All"

#### 替换4：修复Entity - Dispatch调度（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/entity/dispatch
查找: package com.uav.service.domain;
替换为: package com.uav.model.entity.dispatch;
```
点击 "Replace All"

#### 替换5：修复Entity - Telemetry遥测（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/entity/telemetry
查找: package com.uav.service.telemetry;
替换为: package com.uav.model.entity.telemetry;
```
点击 "Replace All"

#### 替换6：修复Enum枚举（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/enums
查找: package com.uav.service.pilot.domain;
替换为: package com.uav.model.enums;
```
点击 "Replace All"

#### 替换7：修复Form - Pilot表单（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/form/pilot
查找: package com.uav.service.pilot.form;
替换为: package com.uav.model.form.pilot;
```
点击 "Replace All"

#### 替换8：修复Form - Dispatch表单（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/form/dispatch
查找: package com.uav.service.dispatch.form;
替换为: package com.uav.model.form.dispatch;
```
点击 "Replace All"

#### 替换9：修复Form - Location表单（1个文件）
```
路径: uav-model/src/main/java/com/uav/model/form/location
查找: package com.uav.service.location.form;
替换为: package com.uav.model.form.location;
```
点击 "Replace All"

#### 替换10：修复VO - Pilot视图（2个文件）
```
路径: uav-model/src/main/java/com/uav/model/vo/pilot
查找: package com.uav.service.pilot.vo;
替换为: package com.uav.model.vo.pilot;
```
点击 "Replace All"

#### 替换11：修复VO - Dispatch视图（2个文件）
```
路径: uav-model/src/main/java/com/uav/model/vo/dispatch
查找: package com.uav.service.dispatch.vo;
替换为: package com.uav.model.vo.dispatch;
```
点击 "Replace All"

#### 替换12：修复VO - Location视图（2个文件）
```
路径: uav-model/src/main/java/com/uav/model/vo/location
查找: package com.uav.service.location.vo;
替换为: package com.uav.model.vo.location;
```
点击 "Replace All"

### 步骤3：验证修复结果
```bash
cd D:/uav/uav-dev
mvn clean compile -pl uav-model
```

如果编译成功，说明包名已全部修复！

---

## 📋 修复检查清单

完成上述替换后，请检查：

- [ ] 所有19个文件的包名已更新
- [ ] uav-model模块编译通过
- [ ] 没有红色错误标记
- [ ] 准备好进行下一步：更新uav-service依赖

---

## ⚡ 预计时间

- 执行12次替换操作：约3-5分钟
- 编译验证：约1-2分钟
- **总计：5-7分钟**

---

## 🎯 完成后的状态

✅ uav-model模块100%完成
✅ 所有文件位置正确
✅ 所有包名正确
✅ 编译通过
⏭️ 准备进入阶段三：创建service父模块