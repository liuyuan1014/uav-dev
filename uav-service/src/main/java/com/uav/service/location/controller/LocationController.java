package com.uav.service.location.controller;

import com.uav.common.Result;
import com.uav.service.location.form.UpdateLocationForm;
import com.uav.service.location.service.PilotLocationService;
import com.uav.service.location.vo.NearbyPilotVo;
import com.uav.service.location.vo.PilotLocationVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 位置管理控制器
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@RestController
@RequestMapping("/location")
@Tag(name = "位置管理", description = "飞手位置相关接口")
public class LocationController {

    @Autowired
    private PilotLocationService locationService;

    @PostMapping("/update")
    @Operation(summary = "更新位置", description = "飞手上报当前位置")
    public Result<Boolean> updateLocation(@RequestBody UpdateLocationForm form) {
        log.info("更新位置: {}", form);
        Boolean result = locationService.updateLocation(form);
        return Result.ok(result);
    }

    @GetMapping("/get/{pilotId}")
    @Operation(summary = "获取位置", description = "获取指定飞手的当前位置")
    public Result<PilotLocationVo> getLocation(
            @Parameter(description = "飞手ID") @PathVariable Long pilotId) {
        log.info("获取位置: pilotId={}", pilotId);
        PilotLocationVo location = locationService.getLocation(pilotId);
        return Result.ok(location);
    }

    @GetMapping("/nearby")
    @Operation(summary = "搜索附近飞手", description = "根据经纬度搜索附近的飞手")
    public Result<List<NearbyPilotVo>> searchNearby(
            @Parameter(description = "纬度") @RequestParam BigDecimal latitude,
            @Parameter(description = "经度") @RequestParam BigDecimal longitude,
            @Parameter(description = "搜索半径（千米）", example = "5.0") @RequestParam(defaultValue = "5.0") Double radiusKm,
            @Parameter(description = "返回数量限制", example = "20") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("搜索附近飞手: lat={}, lon={}, radius={}km, limit={}", 
            latitude, longitude, radiusKm, limit);
        List<NearbyPilotVo> pilots = locationService.searchNearbyPilots(latitude, longitude, radiusKm, limit);
        return Result.ok(pilots);
    }

    @GetMapping("/nearby/idle")
    @Operation(summary = "搜索附近空闲飞手", description = "根据经纬度搜索附近空闲的飞手")
    public Result<List<NearbyPilotVo>> searchNearbyIdle(
            @Parameter(description = "纬度") @RequestParam BigDecimal latitude,
            @Parameter(description = "经度") @RequestParam BigDecimal longitude,
            @Parameter(description = "搜索半径（千米）", example = "5.0") @RequestParam(defaultValue = "5.0") Double radiusKm,
            @Parameter(description = "返回数量限制", example = "20") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("搜索附近空闲飞手: lat={}, lon={}, radius={}km, limit={}", 
            latitude, longitude, radiusKm, limit);
        List<NearbyPilotVo> pilots = locationService.searchNearbyIdlePilots(latitude, longitude, radiusKm, limit);
        return Result.ok(pilots);
    }

    @PostMapping("/batch-update")
    @Operation(summary = "批量更新位置", description = "批量更新多个飞手的位置")
    public Result<Integer> batchUpdateLocation(@RequestBody List<UpdateLocationForm> locations) {
        log.info("批量更新位置: count={}", locations.size());
        Integer count = locationService.batchUpdateLocation(locations);
        return Result.ok(count);
    }

    @DeleteMapping("/remove/{pilotId}")
    @Operation(summary = "删除位置", description = "删除飞手的位置信息（飞手下线时调用）")
    public Result<Boolean> removeLocation(
            @Parameter(description = "飞手ID") @PathVariable Long pilotId) {
        log.info("删除位置: pilotId={}", pilotId);
        Boolean result = locationService.removeLocation(pilotId);
        return Result.ok(result);
    }

    @GetMapping("/distance")
    @Operation(summary = "计算距离", description = "计算两个飞手之间的距离")
    public Result<BigDecimal> calculateDistance(
            @Parameter(description = "飞手1的ID") @RequestParam Long pilotId1,
            @Parameter(description = "飞手2的ID") @RequestParam Long pilotId2) {
        log.info("计算距离: pilotId1={}, pilotId2={}", pilotId1, pilotId2);
        BigDecimal distance = locationService.calculateDistance(pilotId1, pilotId2);
        return Result.ok(distance);
    }
}