package com.uav.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 位置计算工具类
 * 提供地理位置相关的计算功能
 * 
 * @author Roo
 * @date 2026-02-23
 */
public class LocationUtil {

    /**
     * 地球半径（千米）
     */
    private static final double EARTH_RADIUS = 6371.0;

    /**
     * 使用Haversine公式计算两点之间的距离
     * 
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离（千米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 将角度转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 计算差值
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Haversine公式
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 返回距离（千米）
        return EARTH_RADIUS * c;
    }

    /**
     * 计算两点之间的距离（返回BigDecimal，保留2位小数）
     * 
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离（千米）
     */
    public static BigDecimal calculateDistanceDecimal(double lat1, double lon1, double lat2, double lon2) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 验证经纬度是否有效
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @return 是否有效
     */
    public static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    /**
     * 验证经纬度是否有效（BigDecimal版本）
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @return 是否有效
     */
    public static boolean isValidCoordinate(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        return isValidCoordinate(latitude.doubleValue(), longitude.doubleValue());
    }

    /**
     * 判断点是否在指定半径范围内
     * 
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param pointLat 目标点纬度
     * @param pointLon 目标点经度
     * @param radiusKm 半径（千米）
     * @return 是否在范围内
     */
    public static boolean isWithinRadius(double centerLat, double centerLon, 
                                        double pointLat, double pointLon, 
                                        double radiusKm) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }

    /**
     * 格式化距离显示
     * 小于1公里显示米，大于等于1公里显示公里
     * 
     * @param distanceKm 距离（千米）
     * @return 格式化后的距离字符串
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1) {
            int meters = (int) (distanceKm * 1000);
            return meters + "米";
        } else {
            BigDecimal km = BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP);
            return km + "公里";
        }
    }

    /**
     * 计算边界框（用于数据库查询优化）
     * 返回一个矩形区域的四个角的坐标
     * 
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param radiusKm 半径（千米）
     * @return double数组 [minLat, maxLat, minLon, maxLon]
     */
    public static double[] calculateBoundingBox(double centerLat, double centerLon, double radiusKm) {
        // 纬度每度约111公里
        double latDelta = radiusKm / 111.0;
        
        // 经度每度的距离随纬度变化
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat)));

        double minLat = centerLat - latDelta;
        double maxLat = centerLat + latDelta;
        double minLon = centerLon - lonDelta;
        double maxLon = centerLon + lonDelta;

        return new double[]{minLat, maxLat, minLon, maxLon};
    }
}