package com.uav.model.entity.mission;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.uav.model.entity.base.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 任务监控实体类
 * 对应表：mission_monitor
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mission_monitor")
public class MissionMonitor extends BaseEntity {

    /**
     * 任务ID
     */
    private Long missionId;

    /**
     * 飞手ID
     */
    private Long pilotId;

    /**
     * 无人机ID
     */
    private Long uavId;

    /**
     * 监控状态：0-正常 1-异常
     */
    private Integer status;

    /**
     * 异常类型：1-失联 2-电量低 3-超出范围 4-天气异常 5-其他
     */
    private Integer exceptionType;

    /**
     * 异常描述
     */
    private String exceptionDesc;

    /**
     * 当前经度
     */
    private String longitude;

    /**
     * 当前纬度
     */
    private String latitude;

    /**
     * 当前高度（米）
     */
    private Integer altitude;

    /**
     * 当前速度（米/秒）
     */
    private Integer speed;

    /**
     * 电池电量（百分比）
     */
    private Integer batteryLevel;

    /**
     * 信号强度
     */
    private Integer signalStrength;
}