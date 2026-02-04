package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.UavDevice;
import com.uav.service.mapper.UavDeviceMapper;
import com.uav.service.service.UavDeviceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 无人机设备服务实现类
 */
@Service
public class UavDeviceServiceImpl extends ServiceImpl<UavDeviceMapper, UavDevice> implements UavDeviceService {

    @Override
    public boolean bindDevice(Long pilotId, String serialNumber) {
        // 1. 查询设备
        LambdaQueryWrapper<UavDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UavDevice::getSerialNumber, serialNumber);
        UavDevice device = this.getOne(queryWrapper);
        
        // 临时逻辑：如果设备不存在，自动创建并绑定（方便测试）
        if (device == null) {
            device = new UavDevice();
            device.setSerialNumber(serialNumber);
            device.setPilotId(pilotId);  // 创建时直接绑定飞手
            device.setModel("DJI M300"); // 默认型号
            device.setType(1); // 默认多旋翼
            device.setLoadCapacity(new BigDecimal("5.0")); // 默认5kg载重
            device.setAuthCode(UUID.randomUUID().toString().replace("-", "")); // 生成32位鉴权码
            device.setStatus(0); // 默认离线
            this.save(device);
            return true;  // 创建成功直接返回
        }
        
        // 2. 检查是否已被绑定
        if (device.getPilotId() != null) {
            throw new RuntimeException("该设备已被其他飞手绑定");
        }
        
        // 3. 执行绑定
        device.setPilotId(pilotId);
        device.setAuthCode(UUID.randomUUID().toString().replace("-", "")); // 生成32位鉴权码
        device.setStatus(0); // 设置为离线状态
        
        // 4. 更新数据库
        return this.updateById(device);
    }
}