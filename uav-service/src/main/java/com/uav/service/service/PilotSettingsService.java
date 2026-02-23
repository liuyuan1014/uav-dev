package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.PilotSettings;

/**
 * 飞手设置服务接口
 */
public interface PilotSettingsService extends IService<PilotSettings> {

    /**
     * 根据飞手ID获取设置信息
     */
    PilotSettings getByPilotId(Long pilotId);

    /**
     * 更新服务状态
     */
    boolean updateServiceStatus(Long pilotId, Integer status);

    /**
     * 初始化飞手设置
     */
    boolean initSettings(Long pilotId);
}