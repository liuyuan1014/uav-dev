package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 飞手账户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "飞手账户")
@TableName("pilot_account")
public class PilotAccount extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "飞手ID")
    @TableField("pilot_id")
    private Long pilotId;

    @Schema(description = "账户总金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "锁定金额")
    @TableField("lock_amount")
    private BigDecimal lockAmount;

    @Schema(description = "可用金额")
    @TableField("available_amount")
    private BigDecimal availableAmount;

    @Schema(description = "总收入")
    @TableField("total_income_amount")
    private BigDecimal totalIncomeAmount;

    @Schema(description = "总支出")
    @TableField("total_pay_amount")
    private BigDecimal totalPayAmount;

}