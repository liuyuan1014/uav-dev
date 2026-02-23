package com.uav.dispatch.constant;

/**
 * Redis常量类 - 调度服务专用
 * 
 * @author Roo
 * @date 2026-02-23
 */
public class RedisConstant {
    
    /**
     * 飞手临时任务队列Key前缀
     * 完整格式: pilot:temp:queue:{pilotId}
     */
    public static final String PILOT_TEMP_QUEUE_KEY = "pilot:temp:queue:";
    
    /**
     * 任务已派单飞手集合Key前缀
     * 完整格式: mission:dispatched:{missionId}
     */
    public static final String MISSION_DISPATCHED_KEY = "mission:dispatched:";
    
    /**
     * 任务派单锁Key前缀
     * 完整格式: mission:dispatch:lock:{missionId}
     */
    public static final String MISSION_DISPATCH_LOCK_KEY = "mission:dispatch:lock:";
}