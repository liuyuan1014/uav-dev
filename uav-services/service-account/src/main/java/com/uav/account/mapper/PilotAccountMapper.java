package com.uav.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.pilot.PilotAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 飞手账户Mapper接口
 */
@Mapper
public interface PilotAccountMapper extends BaseMapper<PilotAccount> {

    /**
     * 增加账户余额
     */
    int addAmount(@Param("pilotId") Long pilotId, @Param("amount") BigDecimal amount);

    /**
     * 减少账户余额
     */
    int reduceAmount(@Param("pilotId") Long pilotId, @Param("amount") BigDecimal amount);

    /**
     * 冻结金额
     */
    int freezeAmount(@Param("pilotId") Long pilotId, @Param("amount") BigDecimal amount);

    /**
     * 解冻金额
     */
    int unfreezeAmount(@Param("pilotId") Long pilotId, @Param("amount") BigDecimal amount);
}