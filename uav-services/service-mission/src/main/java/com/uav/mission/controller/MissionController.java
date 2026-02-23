package com.uav.mission.controller;

import com.uav.mission.service.MissionService;
import com.uav.model.entity.mission.UavMission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mission")
public class MissionController {

    @Autowired
    private MissionService missionService;

    /**
     * 发布任务
     */
    @PostMapping("/publish")
    public UavMission publishMission(
            @RequestParam Long clientId,
            @RequestParam String startLocation,
            @RequestParam String endLocation) {
        
        if (clientId == null) {
            throw new RuntimeException("客户ID不能为空");
        }
        if (startLocation == null || startLocation.trim().isEmpty()) {
            throw new RuntimeException("起点位置不能为空");
        }
        if (endLocation == null || endLocation.trim().isEmpty()) {
            throw new RuntimeException("终点位置不能为空");
        }
        
        UavMission mission = missionService.publishMission(clientId, startLocation, endLocation);
        log.info("任务发布成功: missionId={}, missionNo={}", mission.getId(), mission.getMissionNo());
        return mission;
    }

    /**
     * 飞手接单
     */
    @PostMapping("/accept")
    public Boolean acceptMission(
            @RequestParam Long missionId,
            @RequestParam Long pilotId,
            @RequestParam String deviceId) {
        
        if (missionId == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        if (pilotId == null) {
            throw new RuntimeException("飞手ID不能为空");
        }
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new RuntimeException("设备ID不能为空");
        }
        
        boolean success = missionService.acceptMission(missionId, pilotId, deviceId);
        log.info("飞手接单成功: missionId={}, pilotId={}, deviceId={}", missionId, pilotId, deviceId);
        return success;
    }

    /**
     * 开始执行任务
     */
    @PostMapping("/start")
    public Boolean startMission(@RequestParam Long missionId) {
        if (missionId == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        
        boolean success = missionService.startMission(missionId);
        log.info("任务开始执行: missionId={}", missionId);
        return success;
    }

    /**
     * 完成任务
     */
    @PostMapping("/complete")
    public Boolean completeMission(
            @RequestParam Long missionId,
            @RequestParam BigDecimal actualDistance) {
        
        if (missionId == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        if (actualDistance == null || actualDistance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("实际飞行里程必须大于0");
        }
        
        boolean success = missionService.completeMission(missionId, actualDistance);
        log.info("任务完成成功: missionId={}, actualDistance={}", missionId, actualDistance);
        return success;
    }

    /**
     * 取消任务
     */
    @PostMapping("/cancel")
    public Map<String, Object> cancelMission(
            @RequestParam Long missionId,
            @RequestParam Integer cancelType,
            @RequestParam Integer operatorType) {
        
        if (missionId == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        if (cancelType == null || cancelType < 1 || cancelType > 7) {
            throw new RuntimeException("取消类型无效，必须在1-7之间");
        }
        if (operatorType == null || operatorType < 1 || operatorType > 3) {
            throw new RuntimeException("操作者类型无效，必须在1-3之间");
        }
        
        Map<String, Object> result = missionService.cancelMission(missionId, cancelType, operatorType);
        log.info("任务取消成功: missionId={}, cancelType={}, operatorType={}, refundAmount={}",
                missionId, cancelType, operatorType, result.get("refundAmount"));
        return result;
    }

    /**
     * 查询任务详情
     */
    @GetMapping("/{missionId}")
    public UavMission getMissionDetail(@PathVariable Long missionId) {
        if (missionId == null) {
            throw new RuntimeException("任务ID不能为空");
        }
        
        UavMission mission = missionService.getById(missionId);
        if (mission == null) {
            throw new RuntimeException("任务不存在");
        }
        
        return mission;
    }
}