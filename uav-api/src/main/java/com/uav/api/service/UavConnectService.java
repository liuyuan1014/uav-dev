package com.uav.api.service;

import com.uav.api.dto.UavHeartbeatDTO;

public interface UavConnectService {
    Boolean connect(UavHeartbeatDTO heartbeat);
}