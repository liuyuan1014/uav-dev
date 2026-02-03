# IDEA JDK 17 运行配置说明

## 问题说明
JDK 17 的模块化系统限制了 Hessian 序列化对 JDK 内部类的反射访问，导致 `InaccessibleObjectException` 错误。

## 解决方案：在 IDEA 中配置 JVM 参数

### 方法一：修改运行配置（推荐）

1. **打开运行配置**
   - 点击 IDEA 顶部工具栏的运行配置下拉菜单
   - 选择 "Edit Configurations..."

2. **配置 uav-gateway-iot**
   - 找到 `UavGatewayApplication` 的运行配置
   - **重要：点击右侧的 "Modify" 按钮**（在 "Code Coverage" 旁边）
   - 在展开的选项中找到 "VM options" 或 "Add VM options"
   - 点击后会出现一个输入框，在其中添加以下参数：

```
--add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-opens java.base/java.lang.constant=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.util.concurrent.atomic=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/sun.nio.cs=ALL-UNNAMED --add-opens java.base/sun.security.action=ALL-UNNAMED --add-opens java.base/sun.util.calendar=ALL-UNNAMED --add-opens java.base/sun.reflect.annotation=ALL-UNNAMED --add-opens java.base/sun.invoke.util=ALL-UNNAMED
```

3. **配置 uav-service**
   - 找到 `UavServiceApplication` 的运行配置
   - 在 "VM options" 字段中添加相同的参数

4. **应用并保存**
   - 点击 "Apply" 然后 "OK"

### 方法二：使用 .idea/runConfigurations（团队共享）

如果您想让团队成员也能使用这些配置，可以创建共享的运行配置文件。
           
### 验证配置

重新启动两个服务后，再次测试：
1. 使用 NetAssist 模拟无人机发送登录包
2. 使用 Apifox 向无人机发送指令
3. 应该不再出现 `InaccessibleObjectException` 错误

## 注意事项

- 这些参数必须在每个需要使用 Dubbo 的服务中配置
- 如果创建新的运行配置，记得添加这些参数
- Maven 打包后运行 jar 文件时，pom.xml 中的配置会自动生效

## 替代方案：使用环境变量

您也可以设置环境变量 `JAVA_TOOL_OPTIONS`：

```bash
set JAVA_TOOL_OPTIONS=--add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED ...
```

但这会影响所有 Java 应用程序，不推荐。