package com.uav.service.location.service.impl;

import com.uav.common.constant.RedisConstant;
import com.uav.common.util.LocationUtil;
import com.uav.service.domain.UavPilot;
import com.uav.service.location.form.UpdateLocationForm;
import com.uav.service.location.service.PilotLocationService;
import com.uav.service.location.vo.NearbyPilotVo;
import com.uav.service.location.vo.PilotLocationVo;
import com.uav.service.mapper.UavPilotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 飞手位置服务实现
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@Service
public class PilotLocationServiceImpl implements PilotLocationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UavPilotMapper pilotMapper;

    @Override
    public Boolean updateLocation(UpdateLocationForm form) {
        try {
            // 验证经纬度
            if (!LocationUtil.isValidCoordinate(form.getLatitude(), form.getLongitude())) {
                log.error("无效的经纬度: lat={}, lon={}", form.getLatitude(), form.getLongitude());
                return false;
            }

            // 使用Redis GEO存储位置
            Point point = new Point(form.getLongitude().doubleValue(), form.getLatitude().doubleValue());
            Long result = redisTemplate.opsForGeo().add(
                RedisConstant.PILOT_GEO_KEY,
                point,
                form.getPilotId().toString()
            );

            // 存储额外信息（精度、方向角等）
            String detailKey = RedisConstant.PILOT_LOCATION_DETAIL_KEY + form.getPilotId();
            redisTemplate.opsForHash().put(detailKey, "latitude", form.getLatitude().toString());
            redisTemplate.opsForHash().put(detailKey, "longitude", form.getLongitude().toString());
            if (form.getAccuracy() != null) {
                redisTemplate.opsForHash().put(detailKey, "accuracy", form.getAccuracy().toString());
            }
            if (form.getBearing() != null) {
                redisTemplate.opsForHash().put(detailKey, "bearing", form.getBearing().toString());
            }
            redisTemplate.opsForHash().put(detailKey, "updateTime", LocalDateTime.now().toString());

            log.info("更新飞手位置成功: pilotId={}, lat={}, lon={}", 
                form.getPilotId(), form.getLatitude(), form.getLongitude());
            return true;
        } catch (Exception e) {
            log.error("更新飞手位置失败", e);
            return false;
        }
    }

    @Override
    public PilotLocationVo getLocation(Long pilotId) {
        try {
            // 从Redis GEO获取位置
            List<Point> positions = redisTemplate.opsForGeo().position(
                RedisConstant.PILOT_GEO_KEY,
                pilotId.toString()
            );

            if (positions == null || positions.isEmpty() || positions.get(0) == null) {
                return null;
            }

            Point point = positions.get(0);
            PilotLocationVo vo = new PilotLocationVo();
            vo.setPilotId(pilotId);
            vo.setLongitude(BigDecimal.valueOf(point.getX()));
            vo.setLatitude(BigDecimal.valueOf(point.getY()));

            // 获取详细信息
            String detailKey = RedisConstant.PILOT_LOCATION_DETAIL_KEY + pilotId;
            String accuracy = (String) redisTemplate.opsForHash().get(detailKey, "accuracy");
            String bearing = (String) redisTemplate.opsForHash().get(detailKey, "bearing");
            String updateTime = (String) redisTemplate.opsForHash().get(detailKey, "updateTime");

            if (accuracy != null) {
                vo.setAccuracy(new BigDecimal(accuracy));
            }
            if (bearing != null) {
                vo.setBearing(new BigDecimal(bearing));
            }
            if (updateTime != null) {
                vo.setUpdateTime(LocalDateTime.parse(updateTime));
            }

            // 获取飞手信息
            UavPilot pilot = pilotMapper.selectById(pilotId);
            if (pilot != null) {
                vo.setPilotName(pilot.getName());
                vo.setOnline(pilot.getStatus() != 0);
            }

            return vo;
        } catch (Exception e) {
            log.error("获取飞手位置失败: pilotId={}", pilotId, e);
            return null;
        }
    }

    @Override
    public List<NearbyPilotVo> searchNearbyPilots(BigDecimal latitude, BigDecimal longitude, 
                                                  Double radiusKm, Integer limit) {
        try {
            // 使用Redis GEORADIUS搜索附近飞手
            Point center = new Point(longitude.doubleValue(), latitude.doubleValue());
            Distance radius = new Distance(radiusKm, Metrics.KILOMETERS);
            
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();
            
            if (limit != null && limit > 0) {
                args.limit(limit);
            }

            GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo()
                .radius(RedisConstant.PILOT_GEO_KEY, center, radius, args);

            if (results == null) {
                return new ArrayList<>();
            }

            // 转换为VO
            return results.getContent().stream()
                .map(result -> {
                    String pilotIdStr = result.getContent().getName().toString();
                    Long pilotId = Long.parseLong(pilotIdStr);
                    
                    NearbyPilotVo vo = new NearbyPilotVo();
                    vo.setPilotId(pilotId);
                    
                    // 设置距离
                    Distance distance = result.getDistance();
                    vo.setDistance(BigDecimal.valueOf(distance.getValue()));
                    vo.setDistanceDesc(LocationUtil.formatDistance(distance.getValue()));
                    
                    // 设置位置
                    Point point = result.getContent().getPoint();
                    vo.setLongitude(BigDecimal.valueOf(point.getX()));
                    vo.setLatitude(BigDecimal.valueOf(point.getY()));
                    
                    // 获取飞手详细信息
                    UavPilot pilot = pilotMapper.selectById(pilotId);
                    if (pilot != null) {
                        vo.setPilotName(pilot.getName());
                        vo.setPhone(pilot.getPhone());
                        vo.setStatus(pilot.getStatus());
                        vo.setStatusDesc(getStatusDesc(pilot.getStatus()));
                        vo.setRating(pilot.getRating());
                        vo.setCompletedMissions(pilot.getCompletedMissions());
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("搜索附近飞手失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<NearbyPilotVo> searchNearbyIdlePilots(BigDecimal latitude, BigDecimal longitude, 
                                                      Double radiusKm, Integer limit) {
        // 先搜索所有附近飞手，然后过滤出空闲状态的
        List<NearbyPilotVo> allPilots = searchNearbyPilots(latitude, longitude, radiusKm, null);
        
        return allPilots.stream()
            .filter(pilot -> pilot.getStatus() != null && pilot.getStatus() == 1) // 1-空闲
            .limit(limit != null ? limit : Integer.MAX_VALUE)
            .collect(Collectors.toList());
    }

    @Override
    public Integer batchUpdateLocation(List<UpdateLocationForm> locations) {
        int successCount = 0;
        for (UpdateLocationForm form : locations) {
            if (updateLocation(form)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public Boolean removeLocation(Long pilotId) {
        try {
            // 从Redis GEO中删除
            Long result = redisTemplate.opsForGeo().remove(
                RedisConstant.PILOT_GEO_KEY,
                pilotId.toString()
            );

            // 删除详细信息
            String detailKey = RedisConstant.PILOT_LOCATION_DETAIL_KEY + pilotId;
            redisTemplate.delete(detailKey);

            log.info("删除飞手位置成功: pilotId={}", pilotId);
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("删除飞手位置失败: pilotId={}", pilotId, e);
            return false;
        }
    }

    @Override
    public void saveLocationHistory(Long pilotId, BigDecimal latitude, BigDecimal longitude) {
        // TODO: 实现位置历史记录保存到MySQL
        // 可以使用定时任务批量保存，避免频繁写入数据库
        log.debug("保存位置历史: pilotId={}, lat={}, lon={}", pilotId, latitude, longitude);
    }

    @Override
    public BigDecimal calculateDistance(Long pilotId1, Long pilotId2) {
        try {
            PilotLocationVo loc1 = getLocation(pilotId1);
            PilotLocationVo loc2 = getLocation(pilotId2);

            if (loc1 == null || loc2 == null) {
                return null;
            }

            return LocationUtil.calculateDistanceDecimal(
                loc1.getLatitude().doubleValue(),
                loc1.getLongitude().doubleValue(),
                loc2.getLatitude().doubleValue(),
                loc2.getLongitude().doubleValue()
            );
        } catch (Exception e) {
            log.error("计算距离失败: pilotId1={}, pilotId2={}", pilotId1, pilotId2, e);
            return null;
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0: return "离线";
            case 1: return "空闲";
            case 2: return "接单中";
            case 3: return "执行中";
            default: return "未知";
        }
    }
}