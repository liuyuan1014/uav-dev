package com.uav.service.pilot.controller;

import com.uav.common.Result;
import com.uav.service.pilot.service.PilotInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 飞手信息管理接口
 */
@Slf4j
@Tag(name = "飞手信息管理")
@RestController
@RequestMapping("/api/pilot/info")
public class PilotInfoController {

    @Autowired
    private PilotInfoService pilotInfoService;

    /**
     * 获取飞手认证信息
     */
    @Operation(summary = "获取飞手认证信息")
    @GetMapping("/auth/{pilotId}")
    public Result<PilotAuthInfoVo> getPilotAuthInfo(@PathVariable Long pilotId) {
        log.info("获取飞手认证信息，pilotId: {}", pilotId);
        PilotAuthInfoVo vo = pilotInfoService.getPilotAuthInfo(pilotId);
        return Result.ok(vo);
    }

    /**
     * 更新飞手认证信息（飞手提交认证）
     */
    @Operation(summary = "更新飞手认证信息")
    @PostMapping("/auth/update")
    public Result<Boolean> updatePilotAuthInfo(@RequestBody UpdatePilotAuthInfoForm form) {
        log.info("更新飞手认证信息，pilotId: {}", form.getPilotId());
        Boolean result = pilotInfoService.updatePilotAuthInfo(form);
        return Result.ok(result);
    }

    /**
     * 获取飞手基本信息
     */
    @Operation(summary = "获取飞手基本信息")
    @GetMapping("/{pilotId}")
    public Result<PilotInfoVo> getPilotInfo(@PathVariable Long pilotId) {
        log.info("获取飞手基本信息，pilotId: {}", pilotId);
        PilotInfoVo vo = pilotInfoService.getPilotInfo(pilotId);
        return Result.ok(vo);
    }

    /**
     * 审核飞手认证（管理员操作）
     */
    @Operation(summary = "审核飞手认证")
    @PostMapping("/auth/audit")
    public Result<Boolean> auditPilotCertification(
            @RequestParam Long pilotId,
            @RequestParam Integer status,
            @RequestParam(required = false) String rejectReason) {
        log.info("审核飞手认证，pilotId: {}, status: {}, rejectReason: {}", pilotId, status, rejectReason);
        Boolean result = pilotInfoService.auditPilotCertification(pilotId, status, rejectReason);
        return Result.ok(result);
    }
}