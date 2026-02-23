package com.uav.service.dispatch.service;

import java.util.List;

/**
 * 派单服务接口
 * 
 * @author Roo
 * @date 2026-02-23
 */
public interface DispatchService {

    /**
     * 开始派单
     * 创建XXL-JOB定时任务，每分钟执行一次派单逻辑
     * 
     * @param form 派单参数
     * @return 任务ID
     */
    Integer startDispatch(StartDispatchForm form);

    /**
     * 停止派单
     * 停止并删除XXL-JOB定时任务
     * 
     * @param missionId 任务ID
     * @return 是否成功
     */
    Boolean stopDispatch(Long missionId);

    /**
     * 执行一次派单
     * 由XXL-JOB定时任务调用
     * 
     * @param missionId 任务ID
     * @return 派单数量
     */
    Integer executeDispatch(Long missionId);

    /**
     * 推送任务到飞手
     * 将任务信息推送到Redis临时队列
     *
     * @param missionId 任务ID
     * @param pilotId 飞手ID
     * @return 是否成功
     */
    Boolean pushToPilot(Long missionId, Long pilotId);

    /**
     * 批量推送任务到飞手
     * 
     * @param missionId 任务ID
     * @param pilotIds 飞手ID列表
     * @return 成功推送的数量
     */
    Integer batchPushToPilots(Long missionId, List<Long> pilotIds);

    /**
     * 获取派单记录
     * 
     * @param missionId 任务ID
     * @return 派单记录列表
     */
    List<DispatchRecordVo> getDispatchRecords(Long missionId);

    /**
     * 获取派单统计
     * 
     * @param missionId 任务ID
     * @return 派单统计信息
     */
    DispatchStatisticsVo getDispatchStatistics(Long missionId);

    /**
     * 检查任务是否超时
     * 如果超时，自动停止派单
     * 
     * @param missionId 任务ID
     * @return 是否超时
     */
    Boolean checkAndHandleTimeout(Long missionId);

    /**
     * 飞手接单
     * 飞手接单后，停止派单任务
     * 
     * @param missionId 任务ID
     * @param pilotId 飞手ID
     * @return 是否成功
     */
    Boolean acceptMission(Long missionId, Long pilotId);

    /**
     * 飞手拒单
     * 
     * @param missionId 任务ID
     * @param pilotId 飞手ID
     * @param reason 拒绝原因
     * @return 是否成功
     */
    Boolean rejectMission(Long missionId, Long pilotId, String reason);

    /**
     * 清理派单数据
     * 清理Redis中的临时数据
     * 
     * @param missionId 任务ID
     */
    void cleanupDispatchData(Long missionId);
}