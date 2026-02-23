package com.uav.client.location;

import com.uav.model.form.location.UpdateLocationForm;
import com.uav.model.vo.location.NearbyPilotVo;
import com.uav.model.vo.location.PilotLocationVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 位置服务Dubbo接口
 * 供其他服务通过Dubbo RPC调用
 * 
 * @author Roo
 * @date 2026-02-23
 */
public interface LocationFeignClient {

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
     * 获取飞手当前位置
     * 
     * @param pilotId 飞手ID
     * @return 位置信息
     */
    PilotLocationVo getLocation(Long pilotId);

    /**
     * 计算两个飞手之间的距离
     * 
     * @param pilotId1 飞手1的ID
     * @param pilotId2 飞手2的ID
     * @return 距离（千米）
     */
    BigDecimal calculateDistance(Long pilotId1, Long pilotId2);
}