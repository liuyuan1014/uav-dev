package com.uav.controller;

import com.uav.api.service.UavCommandService;
import com.uav.common.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 无人机控制指令 Controller
 * 提供 Web API 用于向无人机下发控制指令
 */
@RestController
@RequestMapping("/api/uav/control")
public class UavControlController {

    @DubboReference(group = "gateway")
    private UavCommandService uavCommandService;

    /**
     * 向指定设备发送控制指令
     *
     * @param deviceId 设备ID（无人机序列号）
     * @param action   控制动作（如：TAKEOFF, LAND, RETURN, HOVER 等）
     * @return 发送结果
     */
    @PostMapping("/send")
    public Result<String> sendCommand(
            @RequestParam String deviceId,
            @RequestParam String action) {
        
        // 参数校验
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return Result.error("设备ID不能为空");
        }
        
        if (action == null || action.trim().isEmpty()) {
            return Result.error("控制动作不能为空");
        }
        
        // 检查设备是否在线
        boolean isOnline = uavCommandService.isOnline(deviceId);
        if (!isOnline) {
            return Result.error("设备 " + deviceId + " 不在线，无法发送指令");
        }
        
        // 构建指令 JSON
        Map<String, Object> commandMap = new HashMap<>();
        commandMap.put("action", action);
        commandMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        commandMap.put("deviceId", deviceId);
        
        // 简单的 JSON 序列化（生产环境建议使用 Jackson 或 Gson）
        String commandJson = String.format(
            "{\"action\":\"%s\",\"timestamp\":\"%s\",\"deviceId\":\"%s\"}",
            action,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            deviceId
        );
        
        // 通过 Dubbo 调用 Gateway 服务发送指令
        boolean success = uavCommandService.sendCommand(deviceId, commandJson);
        
        if (success) {
            return Result.success("指令发送成功", commandJson);
        } else {
            return Result.error("指令发送失败");
        }
    }

    /**
     * 查询设备在线状态
     *
     * @param deviceId 设备ID
     * @return 在线状态
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getDeviceStatus(@RequestParam String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return Result.error("设备ID不能为空");
        }
        
        boolean isOnline = uavCommandService.isOnline(deviceId);
        
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("deviceId", deviceId);
        statusMap.put("online", isOnline);
        statusMap.put("checkTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return Result.success(statusMap);
    }

    /**
     * 批量发送指令（可选功能）
     *
     * @param deviceIds 设备ID列表（逗号分隔）
     * @param action    控制动作
     * @return 发送结果
     */
    @PostMapping("/batch-send")
    public Result<Map<String, Object>> batchSendCommand(
            @RequestParam String deviceIds,
            @RequestParam String action) {
        
        if (deviceIds == null || deviceIds.trim().isEmpty()) {
            return Result.error("设备ID列表不能为空");
        }
        
        if (action == null || action.trim().isEmpty()) {
            return Result.error("控制动作不能为空");
        }
        
        String[] deviceIdArray = deviceIds.split(",");
        int successCount = 0;
        int failCount = 0;
        
        for (String deviceId : deviceIdArray) {
            deviceId = deviceId.trim();
            if (deviceId.isEmpty()) {
                continue;
            }
            
            // 检查设备是否在线
            if (!uavCommandService.isOnline(deviceId)) {
                failCount++;
                continue;
            }
            
            // 构建指令 JSON
            String commandJson = String.format(
                "{\"action\":\"%s\",\"timestamp\":\"%s\",\"deviceId\":\"%s\"}",
                action,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                deviceId
            );
            
            // 发送指令
            boolean success = uavCommandService.sendCommand(deviceId, commandJson);
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", deviceIdArray.length);
        resultMap.put("success", successCount);
        resultMap.put("fail", failCount);
        
        return Result.success("批量指令发送完成", resultMap);
    }
}