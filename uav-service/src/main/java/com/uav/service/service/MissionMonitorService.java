package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.MissionMonitor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 任务监控服务接口
 */
public interface MissionMonitorService extends IService<MissionMonitor> {

    /**
     * 创建任务监控记录
     *
     * @param missionId 任务ID
     * @param status 状态
     * @param longitude 经度
     * @param latitude 纬度
     * @param altitude 海拔高度
     * @param speed 速度
     * @param batteryLevel 电池电量
     * @param signalStrength 信号强度
     * @return 是否成功
     */
    boolean createMonitor(Long missionId, Integer status, BigDecimal longitude, BigDecimal latitude,
                         BigDecimal altitude, BigDecimal speed, Integer batteryLevel, Integer signalStrength);

    /**
     * 记录异常情况
     *
     * @param missionId 任务ID
     * @param exceptionType 异常类型
     * @param exceptionDesc 异常描述
     * @return 是否成功
     */
    boolean recordException(Long missionId, Integer exceptionType, String exceptionDesc);

    /**
     * 根据任务ID获取监控记录列表
     *
     * @param missionId 任务ID
     * @return 监控记录列表
     */
    List<MissionMonitor> getByMissionId(Long missionId);

    /**
     * 获取任务最新监控记录
     *
     * @param missionId 任务ID
     * @return 最新监控记录
     */
    MissionMonitor getLatestByMissionId(Long missionId);

    /**
     * 获取任务异常记录
     *
     * @param missionId 任务ID
     * @return 异常记录列表
     */
    List<MissionMonitor> getExceptionsByMissionId(Long missionId);
}