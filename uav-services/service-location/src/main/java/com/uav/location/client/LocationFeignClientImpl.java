package com.uav.location.client;

import com.uav.client.location.LocationFeignClient;
import com.uav.location.service.PilotLocationService;
import com.uav.model.vo.location.NearbyPilotVo;
import com.uav.model.vo.location.PilotLocationVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * 位置服务Dubbo实现
 * 暴露给其他服务通过Dubbo RPC调用
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@DubboService
public class LocationFeignClientImpl implements LocationFeignClient {

    @Autowired
    private PilotLocationService pilotLocationService;

    @Override
    public List<NearbyPilotVo> searchNearbyIdlePilots(BigDecimal latitude, BigDecimal longitude, 
                                                      Double radiusKm, Integer limit) {
        log.info("Dubbo调用 - 搜索附近空闲飞手: lat={}, lon={}, radius={}, limit={}", 
            latitude, longitude, radiusKm, limit);
        return pilotLocationService.searchNearbyIdlePilots(latitude, longitude, radiusKm, limit);
    }

    @Override
    public List<NearbyPilotVo> searchNearbyPilots(BigDecimal latitude, BigDecimal longitude, 
                                                  Double radiusKm, Integer limit) {
        log.info("Dubbo调用 - 搜索附近飞手: lat={}, lon={}, radius={}, limit={}", 
            latitude, longitude, radiusKm, limit);
        return pilotLocationService.searchNearbyPilots(latitude, longitude, radiusKm, limit);
    }

    @Override
    public PilotLocationVo getLocation(Long pilotId) {
        log.info("Dubbo调用 - 获取飞手位置: pilotId={}", pilotId);
        return pilotLocationService.getLocation(pilotId);
    }

    @Override
    public BigDecimal calculateDistance(Long pilotId1, Long pilotId2) {
        log.info("Dubbo调用 - 计算飞手距离: pilotId1={}, pilotId2={}", pilotId1, pilotId2);
        return pilotLocationService.calculateDistance(pilotId1, pilotId2);
    }
}