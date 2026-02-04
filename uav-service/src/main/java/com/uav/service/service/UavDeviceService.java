package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.UavDevice;

/**
 * 无人机设备服务接口
 */
public interface UavDeviceService extends IService<UavDevice> {

    /**
     * 绑定设备到飞手
     *
     * @param pilotId 飞手ID
     * @param serialNumber 设备序列号
     * @return 是否绑定成功
     */
    boolean bindDevice(Long pilotId, String serialNumber);
}