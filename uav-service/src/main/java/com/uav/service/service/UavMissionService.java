package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.model.entity.mission.UavMission;

import java.math.BigDecimal;
import java.util.Map;

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

    /**
     * 完成任务
     *
     * @param missionId 任务ID
     * @param actualDistance 实际飞行里程（公里）
     * @return 是否完成成功
     */
    boolean completeMission(Long missionId, BigDecimal actualDistance);

    /**
     * 取消任务
     *
     * @param missionId 任务ID
     * @param cancelType 取消类型（对应 CancelReasonEnum 的 code）
     * @param operatorType 操作者类型（1=客户, 2=飞手, 3=系统）
     * @return 包含退款信息的 Map（refundAmount: 退款金额, refundReason: 退款原因）
     */
    Map<String, Object> cancelMission(Long missionId, Integer cancelType, Integer operatorType);
}