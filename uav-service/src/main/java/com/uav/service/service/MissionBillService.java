package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.MissionBill;

/**
 * 任务账单服务接口
 */
public interface MissionBillService extends IService<MissionBill> {

    /**
     * 根据任务ID获取账单
     */
    MissionBill getByMissionId(Long missionId);

    /**
     * 创建任务账单
     */
    boolean createBill(MissionBill bill);

    /**
     * 更新支付状态
     */
    boolean updatePayStatus(Long missionId, Integer payStatus);
}