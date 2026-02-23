package com.uav.model.entity.mission;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.uav.model.entity.base.BaseEntity;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 任务账单实体类
 * 对应表：mission_bill
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mission_bill")
public class MissionBill extends BaseEntity {

    /**
     * 任务ID
     */
    private Long missionId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 飞手ID
     */
    private Long pilotId;

    /**
     * 支付方式：1-微信 2-支付宝 3-现金
     */
    private Integer payWay;

    /**
     * 任务金额
     */
    private BigDecimal missionAmount;

    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 飞手收入
     */
    private BigDecimal pilotIncome;

    /**
     * 平台收入
     */
    private BigDecimal platformIncome;

    /**
     * 支付状态：0-未支付 1-已支付 2-已退款
     */
    private Integer payStatus;

    /**
     * 备注
     */
    private String remark;
}