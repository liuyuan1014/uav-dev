package com.uav.service.query;

import com.uav.model.vo.UavStatusVO;

import java.util.List;

/**
 * 无人机查询服务接口
 */
public interface UavQueryService {
    
    /**
     * 获取无人机最新状态
     * @param deviceId 设备ID
     * @return 最新状态VO
     */
    UavStatusVO getLastStatus(String deviceId);
    
    /**
     * 获取无人机历史轨迹
     * @param deviceId 设备ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 历史轨迹列表
     */
    List<UavStatusVO> getHistoryTrack(String deviceId, Long startTime, Long endTime);
}