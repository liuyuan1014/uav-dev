package com.uav.gateway.producer;

import com.uav.gateway.protocol.UavPacket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UavTelemetryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Value("${kafka.uav.telemetry.topic:uav-telemetry}")
    private String telemetryTopic;

    public UavTelemetryProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // 发送遥测数据
    public void sendTelemetry(UavPacket packet) {
        // 从packet body中解析deviceId
        String deviceId = parseDeviceIdFromJson(packet.getBody());
        
        // 如果解析失败或ID为空，使用默认值
        String finalDeviceId = !StringUtils.hasText(deviceId) ? "UNKNOWN_DEVICE" : deviceId;

        try {
            // 异步发送消息到Kafka
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(telemetryTopic, finalDeviceId, packet.getBody());

            // 添加回调处理发送结果
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    // 为什么要传 DeviceId 进去？Kafka 保证：同一个 Key 的消息，永远会去同一个分区，并且是有序的。同一台无人机的轨迹数据，是严格按照时间顺序处理的
                    System.out.println("Kafka推送成功: " + finalDeviceId + ", partition: " + 
                                     result.getRecordMetadata().partition() + 
                                     ", offset: " + result.getRecordMetadata().offset());
                } else {
                    System.err.println("Kafka推送失败: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Kafka推送异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从JSON字符串中解析deviceId
     * @param jsonBody JSON字符串
     * @return deviceId，如果解析失败则返回null
     */
    private String parseDeviceIdFromJson(String jsonBody) {
        try {
            // 使用正则表达式解析JSON中的deviceId字段
            Pattern pattern = Pattern.compile("\"deviceId\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(jsonBody);
            
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("解析JSON失败: " + e.getMessage());
        }
        return null;
    }
}