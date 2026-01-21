package com.uav.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UavHeartbeatDTO implements Serializable {
    // 加上版本号（防止序列化报错）
    private  static final long serialVersionUID = 1L;

    private String deviceId;
    private Long timestamp;
}