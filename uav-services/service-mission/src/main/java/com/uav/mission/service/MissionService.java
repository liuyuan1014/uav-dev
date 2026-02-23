package com.uav.mission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.model.entity.mission.UavMission;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 任务服务接口
 */
public interface MissionService extends IService<UavMission> {

    /**
     * 发布任务
     */
    UavMission publishMission(Long clientId, String startLocation, String endLocation);

    /**
     * 飞手接单（使用Redisson分布式锁 + 乐观锁）
     */
    boolean acceptMission(Long missionId, Long pilotId, String deviceId);

    /**
     * 开始执行任务
     */
    boolean startMission(Long missionId);

    /**
     * 完成任务
     */
    boolean completeMission(Long missionId, BigDecimal actualDistance);

    /**
     * 取消任务
     */
    Map<String, Object> cancelMission(Long missionId, Integer cancelType, Integer operatorType);

    /**
     * 查询飞手完成的任务数
     *
     * @param pilotId 飞手ID
     * @return 完成的任务数量
     */
    Long getCompletedMissionsCount(Long pilotId);
}