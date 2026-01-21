package com.uav.api.dto;

import lombok.Data;

@Data
public class UavHeartbeatDTO {
    private String deviceId;
    private Long timestamp;
}