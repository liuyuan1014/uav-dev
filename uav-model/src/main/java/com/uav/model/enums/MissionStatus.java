package com.uav.model.enums;

import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
public enum MissionStatus {
    
    /**
     * 待接单
     */
    PENDING(0, "待接单"),
    
    /**
     * 已接单
     */
    ACCEPTED(1, "已接单"),
    
    /**
     * 执行中
     */
    IN_PROGRESS(2, "执行中"),
    
    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED(4, "已取消");
    
    private final Integer code;
    private final String desc;
    
    MissionStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据状态码获取枚举
     */
    public static MissionStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MissionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 根据状态码获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        MissionStatus status = fromCode(code);
        return status != null ? status.getDesc() : "未知状态";
    }
}