package com.uav.device.controller;

import com.uav.device.service.UavDeviceService;
import com.uav.model.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 无人机设备控制器
 */
@RestController
@RequestMapping("/device")
public class UavDeviceController {

    @Autowired
    private UavDeviceService uavDeviceService;

    /**
     * 绑定设备到飞手
     * 
     * @param pilotId 飞手ID
     * @param serialNumber 设备序列号
     * @return 绑定结果
     */
    @PostMapping("/bind")
    public Result<Boolean> bindDevice(@RequestParam Long pilotId, 
                                      @RequestParam String serialNumber) {
        try {
            boolean success = uavDeviceService.bindDevice(pilotId, serialNumber);
            if (success) {
                return Result.ok(true);
            } else {
                return Result.fail("设备绑定失败");
            }
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("系统异常：" + e.getMessage());
        }
    }
}