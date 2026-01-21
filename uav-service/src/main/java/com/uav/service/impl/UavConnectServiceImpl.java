package com.uav.service.impl;

import com.uav.api.dto.UavHeartbeatDTO;
import com.uav.api.service.UavConnectService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UavConnectServiceImpl implements UavConnectService {

    @Override
    public Boolean connect(UavHeartbeatDTO heartbeat) {
        System.out.println("收到设备心跳: " + heartbeat.getDeviceId());
        return true;
    }
}