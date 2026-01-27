# 无人机数据链路测试说明

## 测试目的
验证从无人机发送JSON数据到网关，通过Kafka，最终存储到Redis和MongoDB的完整链路。

## 测试前准备
1. 确保MongoDB服务正在运行（152.136.128.11:27017）
2. 确保Redis服务正在运行（101.42.103.115:6379）
3. 确保Kafka服务正在运行（101.42.103.115:9092）
4. 启动uav-service服务
5. 启动uav-gateway-iot服务

## 测试类说明

### 1. UavDataSimulationTest
- 交互式测试，手动输入JSON数据
- 适合单条数据测试和调试

使用方法：
```bash
# 在IDE中运行或使用Maven
mvn test-compile exec:java -Dexec.mainClass="com.uav.gateway.UavDataSimulationTest" -pl uav-gateway-iot
```

### 2. BatchUavDataTest
- 批量发送预生成的测试数据
- 适合批量数据测试和性能验证

使用方法：
```bash
# 在IDE中运行或使用Maven
mvn test-compile exec:java -Dexec.mainClass="com.uav.gateway.BatchUavDataTest" -pl uav-gateway-iot
```

## 测试步骤

1. 启动uav-service（业务服务）
2. 启动uav-gateway-iot（网关服务）
3. 运行测试类发送数据
4. 观察控制台日志输出
5. 验证Redis和MongoDB中的数据

## 验证方法

### 验证Redis存储
- 检查Redis中的`uav:status:{deviceId}`键值对
- 检查Redis中的`uav:locations`地理信息

### 验证MongoDB存储
- 检查MongoDB中的`telemetry_history`集合
- 确认数据包含正确的设备ID、经纬度、高度等信息

## 示例JSON格式
```json
{
  "deviceId": "TEST_UAV_001",
  "lat": 39.9042,
  "lon": 116.4074,
  "altitude": 100.0,
  "speed": 10.5,
  "battery": 85,
  "timestamp": 1678886400000
}
```

## 日志观察点
1. 网关日志：确认收到消息及消息内容
2. Kafka日志：确认消息发送和接收
3. 业务服务日志：确认数据存储到Redis和MongoDB