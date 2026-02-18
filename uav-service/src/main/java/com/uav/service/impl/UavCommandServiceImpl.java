package com.uav.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uav.api.service.UavCommandService;
import com.uav.service.domain.UavDevice;
import com.uav.service.mapper.UavDeviceMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 无人机指令服务实现（Service端）
 * 提供设备验证等业务逻辑
 */
@DubboService(group = "service")
@Service
public class UavCommandServiceImpl implements UavCommandService {

    @Autowired
    private UavDeviceMapper uavDeviceMapper;

    @Override
    public boolean sendCommand(String deviceId, String jsonCmd) {
        // Service端不实现指令下发，由Gateway端实现
        throw new UnsupportedOperationException("Service端不支持指令下发操作");
    }

    @Override
    public boolean isOnline(String deviceId) {
        // Service端不实现在线查询，由Gateway端实现
        throw new UnsupportedOperationException("Service端不支持在线查询操作");
    }

    @Override
    public boolean validateLogin(String deviceId, String authCode) {
        // 1. 根据设备ID（serial_number）查询设备
        LambdaQueryWrapper<UavDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UavDevice::getSerialNumber, deviceId);
        UavDevice device = uavDeviceMapper.selectOne(queryWrapper);
        
        // 2. 设备不存在
        if (device == null) {
            System.out.println("设备验证失败: 设备不存在 - " + deviceId);
            return false;
        }
        
        // 3. 验证authCode
        if (authCode == null || !authCode.equals(device.getAuthCode())) {
            System.out.println("设备验证失败: 鉴权码不匹配 - " + deviceId);
            return false;
        }
        
        System.out.println("设备验证成功: " + deviceId);
        return true;
    }
}