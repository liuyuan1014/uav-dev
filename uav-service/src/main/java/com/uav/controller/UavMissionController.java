package com.uav.controller;

import com.uav.common.Result;
import com.uav.service.domain.UavMission;
import com.uav.service.service.UavMissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 无人机任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/uav/mission")
public class UavMissionController {

    @Autowired
    private UavMissionService uavMissionService;

    /**
     * 发布任务
     * 
     * @param clientId 客户ID
     * @param startLocation 起点位置
     * @param endLocation 终点位置
     * @return 创建的任务信息
     */
    @PostMapping("/publish")
    public Result<UavMission> publishMission(
            @RequestParam Long clientId,
            @RequestParam String startLocation,
            @RequestParam String endLocation) {
        
        try {
            // 参数校验
            if (clientId == null) {
                return Result.error("客户ID不能为空");
            }
            if (startLocation == null || startLocation.trim().isEmpty()) {
                return Result.error("起点位置不能为空");
            }
            if (endLocation == null || endLocation.trim().isEmpty()) {
                return Result.error("终点位置不能为空");
            }
            
            // 发布任务
            UavMission mission = uavMissionService.publishMission(clientId, startLocation, endLocation);
            
            log.info("任务发布成功: missionId={}, missionNo={}", mission.getId(), mission.getMissionNo());
            return Result.success(mission);
            
        } catch (Exception e) {
            log.error("任务发布失败: clientId={}", clientId, e);
            return Result.error("任务发布失败: " + e.getMessage());
        }
    }

    /**
     * 飞手接单
     * 使用 Redisson 分布式锁 + 乐观锁保证并发安全
     * 
     * @param missionId 任务ID
     * @param pilotId 飞手ID
     * @param deviceId 设备ID
     * @return 接单结果
     */
    @PostMapping("/accept")
    public Result<String> acceptMission(
            @RequestParam Long missionId,
            @RequestParam Long pilotId,
            @RequestParam String deviceId) {
        
        try {
            // 参数校验
            if (missionId == null) {
                return Result.error("任务ID不能为空");
            }
            if (pilotId == null) {
                return Result.error("飞手ID不能为空");
            }
            if (deviceId == null || deviceId.trim().isEmpty()) {
                return Result.error("设备ID不能为空");
            }
            
            // 飞手接单
            boolean success = uavMissionService.acceptMission(missionId, pilotId, deviceId);
            
            if (success) {
                log.info("飞手接单成功: missionId={}, pilotId={}, deviceId={}", 
                        missionId, pilotId, deviceId);
                return Result.success("接单成功");
            } else {
                return Result.error("接单失败");
            }
            
        } catch (RuntimeException e) {
            // 业务异常（如任务已被接单、状态不正确等）
            log.warn("飞手接单失败: missionId={}, pilotId={}, error={}", 
                    missionId, pilotId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("飞手接单失败: missionId={}, pilotId={}", missionId, pilotId, e);
            return Result.error("接单失败: " + e.getMessage());
        }
    }

    /**
     * 开始执行任务
     * 
     * @param missionId 任务ID
     * @return 执行结果
     */
    @PostMapping("/start")
    public Result<String> startMission(@RequestParam Long missionId) {
        
        try {
            // 参数校验
            if (missionId == null) {
                return Result.error("任务ID不能为空");
            }
            
            // 开始执行任务
            boolean success = uavMissionService.startMission(missionId);
            
            if (success) {
                log.info("任务开始执行: missionId={}", missionId);
                return Result.success("任务开始执行");
            } else {
                return Result.error("任务开始执行失败");
            }
            
        } catch (RuntimeException e) {
            // 业务异常（如任务不存在、状态不正确等）
            log.warn("任务开始执行失败: missionId={}, error={}", missionId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("任务开始执行失败: missionId={}", missionId, e);
            return Result.error("任务开始执行失败: " + e.getMessage());
        }
    }

    /**
     * 查询任务详情
     * 
     * @param missionId 任务ID
     * @return 任务详情
     */
    @GetMapping("/{missionId}")
    public Result<UavMission> getMissionDetail(@PathVariable Long missionId) {
        
        try {
            if (missionId == null) {
                return Result.error("任务ID不能为空");
            }
            
            UavMission mission = uavMissionService.getById(missionId);
            
            if (mission == null) {
                return Result.error("任务不存在");
            }
            
            return Result.success(mission);
            
        } catch (Exception e) {
            log.error("查询任务详情失败: missionId={}", missionId, e);
            return Result.error("查询任务详情失败: " + e.getMessage());
        }
    }
}