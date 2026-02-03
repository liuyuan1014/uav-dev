package com.uav.gateway.controller;

import com.uav.api.service.UavCommandService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关测试控制器
 * 用于测试指令下发功能
 */
@RestController
@RequestMapping("/api/gateway/cmd")
public class GatewayTestController {

    @DubboReference
    private UavCommandService uavCommandService;

    /**
     * 发送指令到指定设备
     *
     * @param deviceId 设备ID
     * @param cmd      指令内容(JSON格式)
     * @return 发送结果
     */
    @PostMapping("/send")
    public Map<String, Object> sendCommand(@RequestParam String deviceId, @RequestParam String cmd) {
        try {
            boolean result = uavCommandService.sendCommand(deviceId, cmd);
            Map<String, Object> response = new HashMap<>();
            if (result) {
                response.put("success", true);
                response.put("message", "指令发送成功到设备: " + deviceId + ", 指令内容: " + cmd);
                response.put("data", Map.of("deviceId", deviceId, "command", cmd, "sent", true));
            } else {
                response.put("success", false);
                response.put("message", "指令发送失败到设备: " + deviceId + ", 设备可能不在线");
                response.put("data", Map.of("deviceId", deviceId, "command", cmd, "sent", false));
            }
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "指令发送异常: " + e.getMessage());
            response.put("error", e.getMessage());
            return response;
        }
    }

    /**
     * 查询设备是否在线
     *
     * @param deviceId 设备ID
     * @return 设备在线状态
     */
    @GetMapping("/online")
    public Map<String, Object> isOnline(@RequestParam String deviceId) {
        try {
            boolean online = uavCommandService.isOnline(deviceId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "设备 " + deviceId + " 在线状态: " + online);
            response.put("data", Map.of("deviceId", deviceId, "online", online));
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查询设备在线状态异常: " + e.getMessage());
            response.put("error", e.getMessage());
            return response;
        }
    }
}