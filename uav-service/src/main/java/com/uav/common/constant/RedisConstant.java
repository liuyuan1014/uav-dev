package com.uav.common.constant;

/**
 * Redis常量定义
 * 用于派单系统的Redis Key管理
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
    
    // ==================== 任务派单相关 ====================
    
    /**
     * 任务已推送飞手记录
     * Key: pilot:mission:repeat:list:{missionId}
     * Type: SET
     * Value: Set<pilotId>
     * 过期时间: 15分钟
     * 说明: 记录某个任务已经推送给哪些飞手，防止重复推送
     */
    public static final String PILOT_MISSION_REPEAT_LIST = "pilot:mission:repeat:list:";
    
    /**
     * 任务已推送记录过期时间(分钟)
     * 15分钟后任务自动取消，记录也应该过期
     */
    public static final int PILOT_MISSION_REPEAT_LIST_EXPIRES_TIME = 15;
    
    /**
     * 飞手临时任务队列
     * Key: pilot:mission:temp:list:{pilotId}
     * Type: LIST
     * Value: List<NewMissionDataVo(JSON)>
     * 过期时间: 1分钟
     * 说明: 存储推送给飞手的新任务信息，飞手端轮询获取
     */
    public static final String PILOT_MISSION_TEMP_LIST = "pilot:mission:temp:list:";
    
    /**
     * 飞手临时任务队列过期时间(分钟)
     * 1分钟未消费自动过期，飞手端每5秒轮询一次
     */
    public static final int PILOT_MISSION_TEMP_LIST_EXPIRES_TIME = 1;
    
    // ==================== 系统配置相关 ====================
    
    /**
     * 附近飞手搜索半径(公里)
     * 默认搜索任务起点5公里范围内的飞手
     */
    public static final double NEARBY_PILOT_RADIUS = 5.0;
    
    /**
     * 任务等待接单超时时间(分钟)
     * 15分钟内无人接单，任务自动取消
     */
    public static final int MISSION_WAIT_TIMEOUT = 15;
    
    // ==================== 分布式锁相关 ====================
    
    /**
     * 任务接单分布式锁
     * Key: lock:mission:accept:{missionId}
     * 说明: 防止多个飞手同时接同一个任务
     */
    public static final String LOCK_MISSION_ACCEPT = "lock:mission:accept:";
    
    /**
     * 分布式锁过期时间(秒)
     */
    public static final int LOCK_EXPIRE_TIME = 10;
    
    // ==================== 缓存相关 ====================
    
    /**
     * 飞手设置缓存
     * Key: cache:pilot:settings:{pilotId}
     * Type: String(JSON)
     * 过期时间: 30分钟
     * 说明: 缓存飞手的接单设置，减少数据库查询
     */
    public static final String CACHE_PILOT_SETTINGS = "cache:pilot:settings:";
    
    /**
     * 飞手设置缓存过期时间(分钟)
     */
    public static final int CACHE_PILOT_SETTINGS_EXPIRES_TIME = 30;
    
    /**
     * 任务信息缓存
     * Key: cache:mission:info:{missionId}
     * Type: String(JSON)
     * 过期时间: 10分钟
     */
    public static final String CACHE_MISSION_INFO = "cache:mission:info:";
    
    /**
     * 任务信息缓存过期时间(分钟)
     */
    public static final int CACHE_MISSION_INFO_EXPIRES_TIME = 10;
}