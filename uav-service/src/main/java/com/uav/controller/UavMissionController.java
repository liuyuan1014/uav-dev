package com.uav.controller;

import com.uav.common.Result;
import com.uav.model.entity.mission.UavMission;
import com.uav.service.service.UavMissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

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
     * 完成任务
     *
     * @param missionId 任务ID
     * @param actualDistance 实际飞行里程（公里）
     * @return 完成结果
     */
    @PostMapping("/complete")
    public Result<String> completeMission(
            @RequestParam Long missionId,
            @RequestParam BigDecimal actualDistance) {
        
        try {
            // 参数校验
            if (missionId == null) {
                return Result.error("任务ID不能为空");
            }
            if (actualDistance == null || actualDistance.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("实际飞行里程必须大于0");
            }
            
            // 完成任务
            boolean success = uavMissionService.completeMission(missionId, actualDistance);
            
            if (success) {
                log.info("任务完成成功: missionId={}, actualDistance={}", missionId, actualDistance);
                return Result.success("任务完成成功");
            } else {
                return Result.error("任务完成失败");
            }
            
        } catch (RuntimeException e) {
            // 业务异常（如任务不存在、状态不正确等）
            log.warn("任务完成失败: missionId={}, error={}", missionId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("任务完成失败: missionId={}", missionId, e);
            return Result.error("任务完成失败: " + e.getMessage());
        }
    }

    /**
     * 取消任务
     *
     * @param missionId 任务ID
     * @param cancelType 取消类型（1-7，对应 CancelReasonEnum）
     * @param operatorType 操作者类型（1=客户, 2=飞手, 3=系统）
     * @return 取消结果（包含退款信息）
     */
    @PostMapping("/cancel")
    public Result<Map<String, Object>> cancelMission(
            @RequestParam Long missionId,
            @RequestParam Integer cancelType,
            @RequestParam Integer operatorType) {
        
        try {
            // 参数校验
            if (missionId == null) {
                return Result.error("任务ID不能为空");
            }
            if (cancelType == null || cancelType < 1 || cancelType > 7) {
                return Result.error("取消类型无效，必须在1-7之间");
            }
            if (operatorType == null || operatorType < 1 || operatorType > 3) {
                return Result.error("操作者类型无效，必须在1-3之间");
            }
            
            // 取消任务
            Map<String, Object> result = uavMissionService.cancelMission(missionId, cancelType, operatorType);
            
            log.info("任务取消成功: missionId={}, cancelType={}, operatorType={}, refundAmount={}",
                    missionId, cancelType, operatorType, result.get("refundAmount"));
            return Result.success(result);
            
        } catch (RuntimeException e) {
            // 业务异常（如任务不存在、状态不正确等）
            log.warn("任务取消失败: missionId={}, error={}", missionId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("任务取消失败: missionId={}", missionId, e);
            return Result.error("任务取消失败: " + e.getMessage());
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