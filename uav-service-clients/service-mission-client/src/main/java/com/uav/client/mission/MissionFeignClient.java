package com.uav.client.mission;

import com.uav.model.entity.mission.UavMission;

/**
 * 任务服务Dubbo客户端接口
 * 提供任务相关的RPC调用接口
 */
public interface MissionFeignClient {

    /**
     * 获取飞手完成的任务数量
     *
     * @param pilotId 飞手ID
     * @return 完成的任务数量
     */
    Long getCompletedMissionsCount(Long pilotId);

    /**
     * 根据ID获取任务信息
     *
     * @param missionId 任务ID
     * @return 任务信息，不存在返回null
     */
    UavMission getMissionById(Long missionId);
}