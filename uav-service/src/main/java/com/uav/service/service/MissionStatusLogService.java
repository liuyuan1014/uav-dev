package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.model.entity.mission.MissionStatusLog;

import java.util.List;

/**
 * 任务状态日志服务接口
 */
public interface MissionStatusLogService extends IService<MissionStatusLog> {

    /**
     * 记录任务状态变更
     */
    boolean logStatusChange(Long missionId, Integer beforeStatus, Integer afterStatus, 
                           Integer operateType, Long operatorId, Integer operatorType, String operateDesc);

    /**
     * 获取任务的所有状态日志
     */
    List<MissionStatusLog> getByMissionId(Long missionId);
}