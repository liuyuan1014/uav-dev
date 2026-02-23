package com.uav.model.entity.pilot;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.uav.model.entity.base.BaseEntity;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 飞手账户明细实体类
 * 对应表：pilot_account_detail
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pilot_account_detail")
public class PilotAccountDetail extends BaseEntity {

    /**
     * 飞手ID
     */
    private Long pilotId;

    /**
     * 交易类型：1-收入 2-支出 3-冻结 4-解冻
     */
    private Integer tradeType;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易后余额
     */
    private BigDecimal balance;

    /**
     * 关联任务ID
     */
    private Long missionId;

    /**
     * 交易内容描述
     */
    private String content;

    /**
     * 交易编号
     */
    private String tradeNo;
}