package com.uav.service.telemetry;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 无人机遥测数据消费者
 * 实现幂等性处理，防止数据重复
 * - Redis: 覆盖写入，天然幂等
 * - MongoDB: 通过唯一索引防止重复数据插入
 */
@Component
@Slf4j
public class UavTelemetryConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UavTelemetryRepository uavTelemetryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "uav-telemetry", groupId = "telemetry-persistence-group")
    public void consumeTelemetryData(String message, Acknowledgment ack) {
        try {
            System.out.println("=== 收到Kafka消息 ===");
            System.out.println("原始消息内容: " + message);
            
            // 解析JSON消息
            Map<String, Object> telemetryData = objectMapper.readValue(message, Map.class);

            String deviceId = (String) telemetryData.get("deviceId");
            if (deviceId == null) {
                log.error("Device ID is missing in telemetry data: {}", message);
                // 即使出错也要确认消息，避免阻塞
                ack.acknowledge();
                return;
            }

            System.out.println("解析到的设备ID: " + deviceId);
            
            // 策略A: 存储到Redis作为实时状态 (覆盖写入，天然幂等)
            System.out.println("开始更新Redis状态...");
            updateRedisStatus(deviceId, telemetryData);
            System.out.println("Redis状态更新完成");

            // 策略B: 存储到MongoDB作为历史记录 (带去重写入)
            System.out.println("开始保存到MongoDB...");
            boolean saveSuccess = saveToMongoDB(telemetryData, deviceId);
            System.out.println("MongoDB保存完成，成功: " + saveSuccess);

            // 只有当MongoDB保存成功或遇到重复数据(视为成功)时才确认消息
            if (saveSuccess) {
                ack.acknowledge();
                System.out.println("Kafka消息确认完成");
            } else {
                System.out.println("Kafka消息未确认，等待重试");
            }
            // 如果MongoDB保存失败(非重复异常)，则不确认消息，让Kafka重试

        } catch (Exception e) {
            System.err.println("处理遥测数据时发生异常: " + e.getMessage());
            e.printStackTrace();
            // 出现异常时不确认消息，让Kafka重试
        }
        System.out.println("=== 消息处理完成 ===");
    }

    private void updateRedisStatus(String deviceId, Map<String, Object> telemetryData) {
        try {
            System.out.println("更新Redis状态 - 设备ID: " + deviceId);
            System.out.println("Redis存储的数据: " + telemetryData);
            
            // 存储最新状态到 Redis (覆盖写入)
            String statusKey = "uav:status:" + deviceId;
            redisTemplate.opsForValue().set(statusKey, telemetryData);

            // 使用 Redis Geo 存储坐标
            Object latObj = telemetryData.get("lat");
            Object lonObj = telemetryData.get("lon");
            if (latObj != null && lonObj != null) {
                Double lat = Double.valueOf(latObj.toString());
                Double lon = Double.valueOf(lonObj.toString());
                redisTemplate.opsForGeo().add("uav:locations", new Point(lon, lat), deviceId);
                System.out.println("已将坐标添加到Redis Geo: lat=" + lat + ", lon=" + lon);
            }

            System.out.println("Redis状态更新完成 - 设备ID: " + deviceId);
        } catch (Exception e) {
            System.err.println("更新Redis状态时发生异常 - 设备ID: " + deviceId + ", 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 保存到MongoDB，实现幂等性
     * 捕获DuplicateKeyException表示数据重复，这种情况下视为成功
     *
     * @param telemetryData 遥测数据
     * @param deviceId 设备ID
     * @return 是否成功保存(包括重复数据的情况)
     */
    private boolean saveToMongoDB(Map<String, Object> telemetryData, String deviceId) {
        try {
            System.out.println("准备保存到MongoDB - 设备ID: " + deviceId);
            
            UavTelemetryEntity entity = new UavTelemetryEntity();
            entity.setDeviceId(deviceId);

            // 设置遥测数据
            Object latObj = telemetryData.get("lat");
            Object lonObj = telemetryData.get("lon");
            Object altObj = telemetryData.get("altitude");
            Object spdObj = telemetryData.get("speed");
            Object batObj = telemetryData.get("battery");

            if (latObj != null) entity.setLat(Double.valueOf(latObj.toString()));
            if (lonObj != null) entity.setLon(Double.valueOf(lonObj.toString()));
            if (altObj != null) entity.setAltitude(Double.valueOf(altObj.toString()));
            if (spdObj != null) entity.setSpeed(Double.valueOf(spdObj.toString()));
            if (batObj != null) entity.setBattery(Integer.valueOf(batObj.toString()));

            // 处理时间戳
            Object timestampObj = telemetryData.get("timestamp");
            if (timestampObj instanceof Number) {
                entity.setTimestamp(((Number) timestampObj).longValue());
            } else if (timestampObj instanceof String) {
                entity.setTimestamp(Long.parseLong((String) timestampObj));
            } else if (timestampObj != null) {
                entity.setTimestamp(Long.parseLong(timestampObj.toString()));
            }

            entity.setCreateTime(new Date());
            
            System.out.println("构建的MongoDB实体: " + entity);

            // 保存到MongoDB - 如果违反唯一索引会抛出DuplicateKeyException
            uavTelemetryRepository.save(entity);
            System.out.println("MongoDB保存成功 - 设备ID: " + deviceId);
            return true;

        } catch (DuplicateKeyException e) {
            // 捕获唯一索引冲突异常，说明数据重复，记录警告日志但不抛出异常
            System.out.println("MongoDB重复数据忽略 - 设备ID: " + deviceId + ", 时间戳: " + telemetryData.get("timestamp"));
            // 重复数据视为处理成功，返回true以便确认消息
            return true;
        } catch (Exception e) {
            // 其他异常（如连接超时等）记录错误日志，不确认消息让Kafka重试
            System.err.println("MongoDB保存失败 - 设备ID: " + deviceId + ", 错误: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}