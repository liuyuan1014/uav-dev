package com.uav.telemetry.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 无人机遥测数据实体
 * 使用复合索引实现幂等性，防止同一设备在同一时间戳的数据重复插入
 */
@Data
@Document(collection = "telemetry_history")
@CompoundIndex(def = "{'deviceId': 1, 'timestamp': 1}", unique = true)
public class UavTelemetryEntity {
    
    @Id
    private String id;
    
    @Indexed
    private String deviceId;
    
    private Double lat;
    private Double lon;
    private Double altitude;
    private Double speed;
    private Integer battery;
    private Long timestamp;
    private Date createTime;
}