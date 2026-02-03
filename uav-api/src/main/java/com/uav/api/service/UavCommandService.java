package com.uav.api.service;

/**
 * 无人机指令服务接口
 * 用于向无人机下发控制指令
 */
public interface UavCommandService {

    /**
     * 向指定设备发送指令
     *
     * @param deviceId 设备ID
     * @param jsonCmd  JSON格式的指令
     * @return 是否发送成功
     */
    boolean sendCommand(String deviceId, String jsonCmd);

    /**
     * 查询设备是否在线
     *
     * @param deviceId 设备ID
     * @return 是否在线
     */
    boolean isOnline(String deviceId);
}