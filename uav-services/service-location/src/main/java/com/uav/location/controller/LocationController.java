package com.uav.location.controller;

import com.uav.location.service.PilotLocationService;
import com.uav.model.form.location.UpdateLocationForm;
import com.uav.model.vo.location.NearbyPilotVo;
import com.uav.model.vo.location.PilotLocationVo;
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
public class LocationController {

    @Autowired
    private PilotLocationService locationService;

    @PostMapping("/update")
    public Boolean updateLocation(@RequestBody UpdateLocationForm form) {
        log.info("更新位置: {}", form);
        return locationService.updateLocation(form);
    }

    @GetMapping("/get/{pilotId}")
    public PilotLocationVo getLocation(@PathVariable Long pilotId) {
        log.info("获取位置: pilotId={}", pilotId);
        return locationService.getLocation(pilotId);
    }

    @GetMapping("/nearby")
    public List<NearbyPilotVo> searchNearby(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "20") Integer limit) {
        log.info("搜索附近飞手: lat={}, lon={}, radius={}km, limit={}",
            latitude, longitude, radiusKm, limit);
        return locationService.searchNearbyPilots(latitude, longitude, radiusKm, limit);
    }

    @GetMapping("/nearby/idle")
    public List<NearbyPilotVo> searchNearbyIdle(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "20") Integer limit) {
        log.info("搜索附近空闲飞手: lat={}, lon={}, radius={}km, limit={}",
            latitude, longitude, radiusKm, limit);
        return locationService.searchNearbyIdlePilots(latitude, longitude, radiusKm, limit);
    }

    @PostMapping("/batch-update")
    public Integer batchUpdateLocation(@RequestBody List<UpdateLocationForm> locations) {
        log.info("批量更新位置: count={}", locations.size());
        return locationService.batchUpdateLocation(locations);
    }

    @DeleteMapping("/remove/{pilotId}")
    public Boolean removeLocation(@PathVariable Long pilotId) {
        log.info("删除位置: pilotId={}", pilotId);
        return locationService.removeLocation(pilotId);
    }

    @GetMapping("/distance")
    public BigDecimal calculateDistance(
            @RequestParam Long pilotId1,
            @RequestParam Long pilotId2) {
        log.info("计算距离: pilotId1={}, pilotId2={}", pilotId1, pilotId2);
        return locationService.calculateDistance(pilotId1, pilotId2);
    }
}