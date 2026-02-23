package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.PilotLoginLog;

import java.util.List;

/**
 * 飞手登录日志服务接口
 */
public interface PilotLoginLogService extends IService<PilotLoginLog> {

    /**
     * 记录登录日志
     *
     * @param pilotId 飞手ID
     * @param ipAddress IP地址
     * @param deviceType 设备类型
     * @param deviceModel 设备型号
     * @param osVersion 操作系统版本
     * @param appVersion 应用版本
     * @param status 登录状态（1-成功，2-失败）
     * @param msg 登录消息
     * @return 是否成功
     */
    boolean recordLogin(Long pilotId, String ipAddress, String deviceType, String deviceModel,
                       String osVersion, String appVersion, Integer status, String msg);

    /**
     * 根据飞手ID获取登录日志列表
     *
     * @param pilotId 飞手ID
     * @return 登录日志列表
     */
    List<PilotLoginLog> getByPilotId(Long pilotId);

    /**
     * 获取飞手最近一次登录记录
     *
     * @param pilotId 飞手ID
     * @return 最近登录记录
     */
    PilotLoginLog getLatestByPilotId(Long pilotId);

    /**
     * 获取登录失败记录
     *
     * @param pilotId 飞手ID
     * @return 失败记录列表
     */
    List<PilotLoginLog> getFailedLoginsByPilotId(Long pilotId);
}