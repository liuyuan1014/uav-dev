package com.uav.service.query.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.service.query.UavQueryService;
import com.uav.service.telemetry.UavTelemetryEntity;
import com.uav.service.telemetry.UavTelemetryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 无人机查询服务实现类
 */
@Service
@Slf4j
public class UavQueryServiceImpl implements UavQueryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UavTelemetryRepository uavTelemetryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取无人机最新状态
     * @param deviceId 设备ID
     * @return 最新状态VO
     */
    @Override
    public UavStatusVO getLastStatus(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            log.warn("获取无人机最新状态时设备ID为空");
            return null;
        }

        try {
            // 查询Redis中存储的最新状态
            String statusKey = "uav:status:" + deviceId;
            Object statusObj = redisTemplate.opsForValue().get(statusKey);

            if (statusObj == null) {
                log.info("Redis中未找到设备ID: {} 的状态信息", deviceId);
                return null;
            }

            // 将Redis中存储的对象转换为UavStatusVO
            UavStatusVO statusVO = convertToUavStatusVO(statusObj);
            if (statusVO != null) {
                statusVO.setTimeStr(); // 设置格式化时间字符串
            }

            return statusVO;
        } catch (Exception e) {
            log.error("获取设备ID: {} 的最新状态时发生异常", deviceId, e);
            return null;
        }
    }

    /**
     * 获取无人机历史轨迹
     * @param deviceId 设备ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 历史轨迹列表
     */
    @Override
    public List<UavStatusVO> getHistoryTrack(String deviceId, Long startTime, Long endTime) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            log.warn("获取无人机历史轨迹时设备ID为空");
            return Collections.emptyList();
        }

        // 设置默认时间范围（最近1小时）
        if (endTime == null) {
            endTime = System.currentTimeMillis();
        }
        if (startTime == null) {
            startTime = endTime - 3600000L; // 默认查询最近1小时
        }

        // 添加最大查询时间跨度限制，防止查询压力过大
        if (endTime - startTime > 24 * 60 * 60 * 1000L) { // 24小时
            startTime = endTime - 24 * 60 * 60 * 1000L;
            log.warn("查询时间跨度超过24小时，已自动调整起始时间为: {}", startTime);
        }

        try {
            // 查询MongoDB中的历史数据
            List<UavTelemetryEntity> entities = uavTelemetryRepository.findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                    deviceId, startTime, endTime);

            if (entities == null || entities.isEmpty()) {
                log.info("MongoDB中未找到设备ID: {} 的历史轨迹数据", deviceId);
                return Collections.emptyList();
            }

            // 转换为VO列表
            List<UavStatusVO> voList = new ArrayList<>();
            for (UavTelemetryEntity entity : entities) {
                UavStatusVO vo = convertEntityToVO(entity);
                if (vo != null) {
                    vo.setTimeStr(); // 设置格式化时间字符串
                    voList.add(vo);
                }
            }

            return voList;
        } catch (Exception e) {
            log.error("获取设备ID: {} 的历史轨迹时发生异常", deviceId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 将Redis中存储的对象转换为UavStatusVO
     * @param obj Redis中存储的对象（可能是Map或JSON字符串）
     * @return UavStatusVO
     */
    private UavStatusVO convertToUavStatusVO(Object obj) {
        if (obj == null) {
            return null;
        }

        UavStatusVO vo = new UavStatusVO();

        if (obj instanceof Map) {
            // 如果是Map类型，直接提取字段
            Map<?, ?> map = (Map<?, ?>) obj;
            vo.setDeviceId(getStringValue(map.get("deviceId")));
            vo.setLat(getDoubleValue(map.get("lat")));
            vo.setLon(getDoubleValue(map.get("lon")));
            vo.setAltitude(getDoubleValue(map.get("altitude")));
            vo.setSpeed(getDoubleValue(map.get("speed")));
            vo.setBattery(getIntegerValue(map.get("battery")));
            vo.setTimestamp(getLongValue(map.get("timestamp")));
        } else if (obj instanceof String) {
            // 如果是JSON字符串，先解析再提取字段
            try {
                Map<String, Object> map = objectMapper.readValue((String) obj, new TypeReference<Map<String, Object>>() {});
                vo.setDeviceId(getStringValue(map.get("deviceId")));
                vo.setLat(getDoubleValue(map.get("lat")));
                vo.setLon(getDoubleValue(map.get("lon")));
                vo.setAltitude(getDoubleValue(map.get("altitude")));
                vo.setSpeed(getDoubleValue(map.get("speed")));
                vo.setBattery(getIntegerValue(map.get("battery")));
                vo.setTimestamp(getLongValue(map.get("timestamp")));
            } catch (Exception e) {
                log.error("解析Redis中的JSON字符串失败", e);
                return null;
            }
        } else {
            log.warn("Redis中存储的对象类型不支持: {}", obj.getClass().getName());
            return null;
        }

        return vo;
    }

    /**
     * 将Entity转换为VO
     * @param entity 实体对象
     * @return VO对象
     */
    private UavStatusVO convertEntityToVO(UavTelemetryEntity entity) {
        if (entity == null) {
            return null;
        }

        UavStatusVO vo = new UavStatusVO();
        vo.setDeviceId(entity.getDeviceId());
        vo.setLat(entity.getLat());
        vo.setLon(entity.getLon());
        vo.setAltitude(entity.getAltitude());
        vo.setSpeed(entity.getSpeed());
        vo.setBattery(entity.getBattery());
        vo.setTimestamp(entity.getTimestamp());

        return vo;
    }

    /**
     * 安全地获取字符串值
     */
    private String getStringValue(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    /**
     * 安全地获取Double值
     */
    private Double getDoubleValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else {
            try {
                return Double.parseDouble(obj.toString());
            } catch (NumberFormatException e) {
                log.warn("无法将 '{}' 转换为Double类型", obj);
                return null;
            }
        }
    }

    /**
     * 安全地获取Integer值
     */
    private Integer getIntegerValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else {
            try {
                return Integer.parseInt(obj.toString());
            } catch (NumberFormatException e) {
                log.warn("无法将 '{}' 转换为Integer类型", obj);
                return null;
            }
        }
    }

    /**
     * 安全地获取Long值
     */
    private Long getLongValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else {
            try {
                return Long.parseLong(obj.toString());
            } catch (NumberFormatException e) {
                log.warn("无法将 '{}' 转换为Long类型", obj);
                return null;
            }
        }
    }
}