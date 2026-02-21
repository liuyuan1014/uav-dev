package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.UavMission;

/**
 * 无人机任务服务接口
 */
public interface UavMissionService extends IService<UavMission> {

    /**
     * 发布任务
     * 
     * @param clientId 客户ID
     * @param startLocation 起点位置
     * @param endLocation 终点位置
     * @return 创建的任务对象
     */
    UavMission publishMission(Long clientId, String startLocation, String endLocation);

    /**
     * 飞手接单
     * 使用 Redisson 分布式锁 + 乐观锁保证并发安全
     * 
     * @param missionId 任务ID
     * @param pilotId 飞手ID
     * @param deviceId 设备ID
     * @return 是否接单成功
     */
    boolean acceptMission(Long missionId, Long pilotId, String deviceId);

    /**
     * 开始执行任务
     * 
     * @param missionId 任务ID
     * @return 是否开始成功
     */
    boolean startMission(Long missionId);
}