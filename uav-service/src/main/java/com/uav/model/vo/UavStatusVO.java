package com.uav.model.vo;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 无人机状态视图对象
 * 与UavTelemetryEntity字段对齐，包含格式化的时间字符串
 */
@Data
public class UavStatusVO {
    private String deviceId;
    private Double lat;
    private Double lon;
    private Double altitude;
    private Double speed;
    private Integer battery;
    private Long timestamp;
    private String timeStr;

    /**
     * 根据timestamp字段设置格式化的时间字符串
     */
    public void setTimeStr() {
        if (this.timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.timeStr = sdf.format(new Date(this.timestamp));
        } else {
            this.timeStr = null;
        }
    }
}