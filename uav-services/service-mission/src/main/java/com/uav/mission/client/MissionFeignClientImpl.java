package com.uav.mission.client;

import com.uav.client.mission.MissionFeignClient;
import com.uav.mission.service.MissionService;
import com.uav.model.entity.mission.UavMission;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 任务服务Dubbo实现类
 * 暴露任务相关的RPC接口
 */
@Slf4j
@DubboService
public class MissionFeignClientImpl implements MissionFeignClient {

    @Autowired
    private MissionService missionService;

    @Override
    public Long getCompletedMissionsCount(Long pilotId) {
        log.info("Dubbo调用: 获取飞手完成任务数, pilotId={}", pilotId);
        return missionService.getCompletedMissionsCount(pilotId);
    }

    @Override
    public UavMission getMissionById(Long missionId) {
        log.info("Dubbo调用: 获取任务信息, missionId={}", missionId);
        return missionService.getById(missionId);
    }
}