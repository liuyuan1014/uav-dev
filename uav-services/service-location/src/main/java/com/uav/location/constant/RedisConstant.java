package com.uav.location.constant;

/**
 * Redis常量定义
 * 用于位置服务的Redis Key管理
 *
 * @author Roo
 * @date 2026-02-23
 */
public class RedisConstant {
    
    // ==================== 飞手位置相关 ====================
    
    /**
     * 飞手位置GEO集合
     * Key: pilot:geo:location
     * Type: GEO
     * Value: Point(longitude, latitude) -> pilotId
     * 说明: 存储所有开启接单服务的飞手实时位置
     */
    public static final String PILOT_GEO_LOCATION = "pilot:geo:location";
    
    /**
     * 飞手位置GEO集合 (别名)
     */
    public static final String PILOT_GEO_KEY = "pilot:geo:location";
    
    /**
     * 飞手位置详细信息
     * Key: pilot:location:detail:{pilotId}
     * Type: HASH
     * 说明: 存储飞手位置的详细信息（精度、方向角等）
     */
    public static final String PILOT_LOCATION_DETAIL_KEY = "pilot:location:detail:";
    
    // ==================== 系统配置相关 ====================
    
    /**
     * 附近飞手搜索半径(公里)
     * 默认搜索任务起点5公里范围内的飞手
     */
    public static final double NEARBY_PILOT_RADIUS = 5.0;
}