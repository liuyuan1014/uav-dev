package com.uav.model.enums;

/**
 * 飞手认证状态枚举
 */
public enum PilotCertificationStatus {
    
    /**
     * 未认证
     */
    UNCERTIFIED(0, "未认证"),
    
    /**
     * 审核中
     */
    UNDER_REVIEW(1, "审核中"),
    
    /**
     * 认证通过
     */
    CERTIFIED(2, "认证通过"),
    
    /**
     * 认证失败
     */
    REJECTED(-1, "认证失败");
    
    private final Integer code;
    private final String description;
    
    PilotCertificationStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PilotCertificationStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return null;
    }
    
    /**
     * 根据code获取枚举
     */
    public static PilotCertificationStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PilotCertificationStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}