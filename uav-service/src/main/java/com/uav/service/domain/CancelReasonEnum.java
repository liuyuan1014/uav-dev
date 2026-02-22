package com.uav.service.domain;

import lombok.Getter;

/**
 * 取消原因枚举
 */
@Getter
public enum CancelReasonEnum {
    
    CLIENT_PLAN_CHANGE(1, "客户取消 - 计划变更"),
    PILOT_DEVICE_FAULT(2, "飞手取消 - 设备故障/电量不足"),
    PILOT_WEATHER(3, "飞手取消 - 天气恶劣（禁飞）"),
    SYSTEM_PAYMENT_TIMEOUT(4, "系统取消 - 支付超时/风控拦截"),
    CLIENT_ADDRESS_ERROR(5, "客户取消 - 收货地址错误"),
    PILOT_EMERGENCY(6, "飞手取消 - 身体不适/紧急情况"),
    SYSTEM_TIMEOUT(7, "系统取消 - 超时未接单");
    
    private final Integer code;
    private final String description;
    
    CancelReasonEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static CancelReasonEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CancelReasonEnum reason : values()) {
            if (reason.getCode().equals(code)) {
                return reason;
            }
        }
        return null;
    }
    
    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        CancelReasonEnum reason = getByCode(code);
        return reason != null ? reason.getDescription() : "未知原因";
    }
}