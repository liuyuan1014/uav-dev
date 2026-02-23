package com.uav.service.location.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 飞手位置服务接口
 * 
 * @author Roo
 * @date 2026-02-23
 */
public interface PilotLocationService {

    /**
     * 更新飞手位置
     * 使用Redis GEO存储实时位置
     * 
     * @param form 位置信息
     * @return 是否成功
     */
    Boolean updateLocation(UpdateLocationForm form);

    /**
     * 获取飞手当前位置
     * 
     * @param pilotId 飞手ID
     * @return 位置信息
     */
    PilotLocationVo getLocation(Long pilotId);

    /**
     * 搜索附近的飞手
     * 使用Redis GEORADIUS命令搜索指定半径内的飞手
     * 
     * @param latitude 中心点纬度
     * @param longitude 中心点经度
     * @param radiusKm 搜索半径（千米）
     * @param limit 返回数量限制
     * @return 附近飞手列表
     */
    List<NearbyPilotVo> searchNearbyPilots(BigDecimal latitude, BigDecimal longitude, 
                                           Double radiusKm, Integer limit);

    /**
     * 搜索附近空闲的飞手
     * 只返回状态为空闲的飞手
     * 
     * @param latitude 中心点纬度
     * @param longitude 中心点经度
     * @param radiusKm 搜索半径（千米）
     * @param limit 返回数量限制
     * @return 附近空闲飞手列表
     */
    List<NearbyPilotVo> searchNearbyIdlePilots(BigDecimal latitude, BigDecimal longitude, 
                                               Double radiusKm, Integer limit);

    /**
     * 批量更新飞手位置
     * 
     * @param locations 位置信息列表
     * @return 成功更新的数量
     */
    Integer batchUpdateLocation(List<UpdateLocationForm> locations);

    /**
     * 删除飞手位置
     * 飞手下线时调用
     * 
     * @param pilotId 飞手ID
     * @return 是否成功
     */
    Boolean removeLocation(Long pilotId);

    /**
     * 保存位置历史记录
     * 定期将Redis中的位置数据持久化到MySQL
     * 
     * @param pilotId 飞手ID
     * @param latitude 纬度
     * @param longitude 经度
     */
    void saveLocationHistory(Long pilotId, BigDecimal latitude, BigDecimal longitude);

    /**
     * 计算两个飞手之间的距离
     * 
     * @param pilotId1 飞手1的ID
     * @param pilotId2 飞手2的ID
     * @return 距离（千米）
     */
    BigDecimal calculateDistance(Long pilotId1, Long pilotId2);
}