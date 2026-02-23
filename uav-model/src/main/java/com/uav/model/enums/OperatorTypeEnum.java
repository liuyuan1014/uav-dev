package com.uav.model.enums;

import lombok.Getter;

/**
 * 操作者类型枚举（记录谁发起的取消操作）
 */
@Getter
public enum OperatorTypeEnum {
    
    CLIENT(1, "客户"),
    PILOT(2, "飞手"),
    SYSTEM(3, "系统");
    
    private final Integer code;
    private final String description;
    
    OperatorTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static OperatorTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OperatorTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据code获取描述
     */
    public static String getDescriptionByCode(Integer code) {
        OperatorTypeEnum type = getByCode(code);
        return type != null ? type.getDescription() : "未知操作者";
    }
}