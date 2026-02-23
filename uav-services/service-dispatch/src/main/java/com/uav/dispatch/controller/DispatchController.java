package com.uav.dispatch.controller;

import com.uav.dispatch.service.DispatchService;
import com.uav.model.form.dispatch.StartDispatchForm;
import com.uav.model.vo.dispatch.DispatchRecordVo;
import com.uav.model.vo.dispatch.DispatchStatisticsVo;
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
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping("/start")
    public Integer startDispatch(@RequestBody StartDispatchForm form) {
        log.info("开始派单: {}", form);
        return dispatchService.startDispatch(form);
    }

    @PostMapping("/stop/{missionId}")
    public Boolean stopDispatch(@PathVariable Long missionId) {
        log.info("停止派单: missionId={}", missionId);
        return dispatchService.stopDispatch(missionId);
    }

    @PostMapping("/execute/{missionId}")
    public Integer executeDispatch(@PathVariable Long missionId) {
        log.info("立即执行派单: missionId={}", missionId);
        return dispatchService.executeDispatch(missionId);
    }

    @PostMapping("/push")
    public Boolean pushToPilot(@RequestParam Long missionId, @RequestParam Long pilotId) {
        log.info("推送任务: missionId={}, pilotId={}", missionId, pilotId);
        return dispatchService.pushToPilot(missionId, pilotId);
    }

    @PostMapping("/batch-push")
    public Integer batchPushToPilots(@RequestParam Long missionId, @RequestBody List<Long> pilotIds) {
        log.info("批量推送任务: missionId={}, pilotIds={}", missionId, pilotIds);
        return dispatchService.batchPushToPilots(missionId, pilotIds);
    }

    @GetMapping("/records/{missionId}")
    public List<DispatchRecordVo> getDispatchRecords(@PathVariable Long missionId) {
        log.info("获取派单记录: missionId={}", missionId);
        return dispatchService.getDispatchRecords(missionId);
    }

    @GetMapping("/statistics/{missionId}")
    public DispatchStatisticsVo getDispatchStatistics(@PathVariable Long missionId) {
        log.info("获取派单统计: missionId={}", missionId);
        return dispatchService.getDispatchStatistics(missionId);
    }

    @PostMapping("/accept")
    public Boolean acceptMission(@RequestParam Long missionId, @RequestParam Long pilotId) {
        log.info("飞手接单: missionId={}, pilotId={}", missionId, pilotId);
        return dispatchService.acceptMission(missionId, pilotId);
    }

    @PostMapping("/reject")
    public Boolean rejectMission(
            @RequestParam Long missionId,
            @RequestParam Long pilotId,
            @RequestParam(required = false) String reason) {
        log.info("飞手拒单: missionId={}, pilotId={}, reason={}", missionId, pilotId, reason);
        return dispatchService.rejectMission(missionId, pilotId, reason);
    }

    @PostMapping("/cleanup/{missionId}")
    public void cleanupDispatchData(@PathVariable Long missionId) {
        log.info("清理派单数据: missionId={}", missionId);
        dispatchService.cleanupDispatchData(missionId);
    }
}