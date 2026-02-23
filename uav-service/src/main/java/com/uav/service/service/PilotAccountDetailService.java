package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 飞手账户明细服务接口
 */
public interface PilotAccountDetailService extends IService<PilotAccountDetail> {

    /**
     * 创建账户明细记录
     *
     * @param pilotId 飞手ID
     * @param tradeType 交易类型（1-收入，2-支出）
     * @param tradeAmount 交易金额
     * @param content 交易内容描述
     * @param missionId 关联任务ID（可选）
     * @return 是否成功
     */
    boolean createDetail(Long pilotId, Integer tradeType, BigDecimal tradeAmount, String content, Long missionId);

    /**
     * 根据飞手ID获取账户明细列表
     *
     * @param pilotId 飞手ID
     * @return 账户明细列表
     */
    List<PilotAccountDetail> getByPilotId(Long pilotId);

    /**
     * 根据任务ID获取账户明细
     *
     * @param missionId 任务ID
     * @return 账户明细
     */
    PilotAccountDetail getByMissionId(Long missionId);

    /**
     * 计算飞手总收入
     *
     * @param pilotId 飞手ID
     * @return 总收入金额
     */
    BigDecimal calculateTotalIncome(Long pilotId);

    /**
     * 计算飞手总支出
     *
     * @param pilotId 飞手ID
     * @return 总支出金额
     */
    BigDecimal calculateTotalExpense(Long pilotId);
}