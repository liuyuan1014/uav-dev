package com.uav.gateway;

import com.uav.api.dto.UavHeartbeatDTO;
import com.uav.api.service.UavConnectService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DubboTestRunner implements CommandLineRunner {

    @DubboReference
    private UavConnectService uavConnectService;

    @Override
    public void run(String... args) throws Exception {
        UavHeartbeatDTO heartbeat = new UavHeartbeatDTO();
        heartbeat.setDeviceId("UAV-TEST-001");
        heartbeat.setTimestamp(System.currentTimeMillis());

        Boolean result = uavConnectService.connect(heartbeat);
        System.out.println("调用结果: " + result);
    }
}