package com.uav.pilot.controller;

import com.uav.model.vo.pilot.PilotAuthInfoVo;
import com.uav.model.vo.pilot.PilotInfoVo;
import com.uav.model.form.pilot.UpdatePilotAuthInfoForm;
import com.uav.pilot.service.PilotInfoService;
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
    public PilotAuthInfoVo getPilotAuthInfo(@PathVariable Long pilotId) {
        log.info("获取飞手认证信息，pilotId: {}", pilotId);
        return pilotInfoService.getPilotAuthInfo(pilotId);
    }

    /**
     * 更新飞手认证信息（飞手提交认证）
     */
    @Operation(summary = "更新飞手认证信息")
    @PostMapping("/auth/update")
    public Boolean updatePilotAuthInfo(@RequestBody UpdatePilotAuthInfoForm form) {
        log.info("更新飞手认证信息，pilotId: {}", form.getPilotId());
        return pilotInfoService.updatePilotAuthInfo(form);
    }

    /**
     * 获取飞手基本信息
     */
    @Operation(summary = "获取飞手基本信息")
    @GetMapping("/{pilotId}")
    public PilotInfoVo getPilotInfo(@PathVariable Long pilotId) {
        log.info("获取飞手基本信息，pilotId: {}", pilotId);
        return pilotInfoService.getPilotInfo(pilotId);
    }

    /**
     * 审核飞手认证（管理员操作）
     */
    @Operation(summary = "审核飞手认证")
    @PostMapping("/auth/audit")
    public Boolean auditPilotCertification(
            @RequestParam Long pilotId,
            @RequestParam Integer status,
            @RequestParam(required = false) String rejectReason) {
        log.info("审核飞手认证，pilotId: {}, status: {}, rejectReason: {}", pilotId, status, rejectReason);
        return pilotInfoService.auditPilotCertification(pilotId, status, rejectReason);
    }
}