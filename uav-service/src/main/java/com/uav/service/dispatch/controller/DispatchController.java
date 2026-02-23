package com.uav.service.dispatch.controller;

import com.uav.common.Result;
import com.uav.service.dispatch.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 派单管理控制器
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@RestController
@RequestMapping("/dispatch")
@Tag(name = "派单管理", description = "任务派单相关接口")
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping("/start")
    @Operation(summary = "开始派单", description = "创建定时任务，开始自动派单")
    public Result<Integer> startDispatch(@RequestBody StartDispatchForm form) {
        log.info("开始派单: {}", form);
        Integer jobId = dispatchService.startDispatch(form);
        return Result.ok(jobId);
    }

    @PostMapping("/stop/{missionId}")
    @Operation(summary = "停止派单", description = "停止任务的自动派单")
    public Result<Boolean> stopDispatch(
            @Parameter(description = "任务ID") @PathVariable Long missionId) {
        log.info("停止派单: missionId={}", missionId);
        Boolean result = dispatchService.stopDispatch(missionId);
        return Result.ok(result);
    }

    @PostMapping("/execute/{missionId}")
    @Operation(summary = "立即执行派单", description = "手动触发一次派单")
    public Result<Integer> executeDispatch(
            @Parameter(description = "任务ID") @PathVariable Long missionId) {
        log.info("立即执行派单: missionId={}", missionId);
        Integer count = dispatchService.executeDispatch(missionId);
        return Result.ok(count);
    }

    @PostMapping("/push")
    @Operation(summary = "推送任务到飞手", description = "手动推送任务到指定飞手")
    public Result<Boolean> pushToPilot(
            @Parameter(description = "任务ID") @RequestParam Long missionId,
            @Parameter(description = "飞手ID") @RequestParam Long pilotId) {
        log.info("推送任务: missionId={}, pilotId={}", missionId, pilotId);
        Boolean result = dispatchService.pushToPilot(missionId, pilotId);
        return Result.ok(result);
    }

    @PostMapping("/batch-push")
    @Operation(summary = "批量推送任务", description = "批量推送任务到多个飞手")
    public Result<Integer> batchPushToPilots(
            @Parameter(description = "任务ID") @RequestParam Long missionId,
            @Parameter(description = "飞手ID列表") @RequestBody List<Long> pilotIds) {
        log.info("批量推送任务: missionId={}, pilotIds={}", missionId, pilotIds);
        Integer count = dispatchService.batchPushToPilots(missionId, pilotIds);
        return Result.ok(count);
    }

    @GetMapping("/records/{missionId}")
    @Operation(summary = "获取派单记录", description = "查询任务的派单记录")
    public Result<List<DispatchRecordVo>> getDispatchRecords(
            @Parameter(description = "任务ID") @PathVariable Long missionId) {
        log.info("获取派单记录: missionId={}", missionId);
        List<DispatchRecordVo> records = dispatchService.getDispatchRecords(missionId);
        return Result.ok(records);
    }

    @GetMapping("/statistics/{missionId}")
    @Operation(summary = "获取派单统计", description = "查询任务的派单统计信息")
    public Result<DispatchStatisticsVo> getDispatchStatistics(
            @Parameter(description = "任务ID") @PathVariable Long missionId) {
        log.info("获取派单统计: missionId={}", missionId);
        DispatchStatisticsVo statistics = dispatchService.getDispatchStatistics(missionId);
        return Result.ok(statistics);
    }

    @PostMapping("/accept")
    @Operation(summary = "飞手接单", description = "飞手接受任务")
    public Result<Boolean> acceptMission(
            @Parameter(description = "任务ID") @RequestParam Long missionId,
            @Parameter(description = "飞手ID") @RequestParam Long pilotId) {
        log.info("飞手接单: missionId={}, pilotId={}", missionId, pilotId);
        Boolean result = dispatchService.acceptMission(missionId, pilotId);
        return Result.ok(result);
    }

    @PostMapping("/reject")
    @Operation(summary = "飞手拒单", description = "飞手拒绝任务")
    public Result<Boolean> rejectMission(
            @Parameter(description = "任务ID") @RequestParam Long missionId,
            @Parameter(description = "飞手ID") @RequestParam Long pilotId,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        log.info("飞手拒单: missionId={}, pilotId={}, reason={}", missionId, pilotId, reason);
        Boolean result = dispatchService.rejectMission(missionId, pilotId, reason);
        return Result.ok(result);
    }

    @PostMapping("/cleanup/{missionId}")
    @Operation(summary = "清理派单数据", description = "清理任务的派单临时数据")
    public Result<Void> cleanupDispatchData(
            @Parameter(description = "任务ID") @PathVariable Long missionId) {
        log.info("清理派单数据: missionId={}", missionId);
        dispatchService.cleanupDispatchData(missionId);
        return Result.ok(null);
    }
}