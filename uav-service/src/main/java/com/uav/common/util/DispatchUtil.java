package com.uav.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 派单工具类
 * 
 * @author Roo
 * @date 2026-02-23
 */
public class DispatchUtil {

    /**
     * 默认搜索半径（千米）
     */
    public static final Double DEFAULT_SEARCH_RADIUS = 5.0;

    /**
     * 最大搜索半径（千米）
     */
    public static final Double MAX_SEARCH_RADIUS = 50.0;

    /**
     * 默认派单数量
     */
    public static final Integer DEFAULT_DISPATCH_COUNT = 20;

    /**
     * 任务超时时间（分钟）
     */
    public static final Integer MISSION_TIMEOUT_MINUTES = 15;

    /**
     * 派单任务执行频率（Cron表达式：每1分钟执行一次）
     */
    public static final String DISPATCH_JOB_CRON = "0 */1 * * * ?";

    /**
     * 生成派单任务描述
     * 
     * @param missionId 任务ID
     * @return 任务描述
     */
    public static String generateJobDesc(Long missionId) {
        return "任务派单-" + missionId;
    }

    /**
     * 生成派单任务Handler名称
     * 
     * @return Handler名称
     */
    public static String getDispatchJobHandler() {
        return "missionDispatchJobHandler";
    }

    /**
     * 生成派单任务参数（JSON格式）
     * 
     * @param missionId 任务ID
     * @return JSON参数
     */
    public static String generateJobParam(Long missionId) {
        return String.format("{\"missionId\":%d}", missionId);
    }

    /**
     * 计算任务超时时间
     * 
     * @param createTime 创建时间
     * @return 超时时间
     */
    public static LocalDateTime calculateTimeout(LocalDateTime createTime) {
        return createTime.plusMinutes(MISSION_TIMEOUT_MINUTES);
    }

    /**
     * 判断任务是否超时
     * 
     * @param createTime 创建时间
     * @return 是否超时
     */
    public static boolean isTimeout(LocalDateTime createTime) {
        LocalDateTime timeout = calculateTimeout(createTime);
        return LocalDateTime.now().isAfter(timeout);
    }

    /**
     * 格式化时间
     * 
     * @param dateTime 时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * 计算派单优先级
     * 根据任务创建时间、紧急程度等因素计算
     * 
     * @param createTime 创建时间
     * @param isUrgent 是否紧急
     * @return 优先级分数（越高越优先）
     */
    public static int calculatePriority(LocalDateTime createTime, boolean isUrgent) {
        int priority = 0;
        
        // 紧急任务加50分
        if (isUrgent) {
            priority += 50;
        }
        
        // 等待时间越长，优先级越高（每分钟加1分）
        long waitMinutes = java.time.Duration.between(createTime, LocalDateTime.now()).toMinutes();
        priority += (int) Math.min(waitMinutes, 100);
        
        return priority;
    }

    /**
     * 验证搜索半径是否合法
     * 
     * @param radiusKm 半径（千米）
     * @return 是否合法
     */
    public static boolean isValidRadius(Double radiusKm) {
        return radiusKm != null && radiusKm > 0 && radiusKm <= MAX_SEARCH_RADIUS;
    }

    /**
     * 获取有效的搜索半径
     * 如果输入无效，返回默认值
     * 
     * @param radiusKm 半径（千米）
     * @return 有效的半径
     */
    public static Double getValidRadius(Double radiusKm) {
        if (isValidRadius(radiusKm)) {
            return radiusKm;
        }
        return DEFAULT_SEARCH_RADIUS;
    }
}