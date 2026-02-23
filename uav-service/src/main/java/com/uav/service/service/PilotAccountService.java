package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * 飞手账户服务接口
 */
public interface PilotAccountService extends IService<PilotAccount> {

    /**
     * 根据飞手ID获取账户信息
     */
    PilotAccount getByPilotId(Long pilotId);

    /**
     * 增加账户余额
     */
    boolean addAmount(Long pilotId, BigDecimal amount);

    /**
     * 减少账户余额
     */
    boolean reduceAmount(Long pilotId, BigDecimal amount);

    /**
     * 冻结金额
     */
    boolean freezeAmount(Long pilotId, BigDecimal amount);

    /**
     * 解冻金额
     */
    boolean unfreezeAmount(Long pilotId, BigDecimal amount);

    /**
     * 初始化飞手账户
     */
    boolean initAccount(Long pilotId);
}