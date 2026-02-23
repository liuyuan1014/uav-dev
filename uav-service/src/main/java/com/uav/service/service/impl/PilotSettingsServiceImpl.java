package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.mapper.PilotSettingsMapper;
import com.uav.service.service.PilotSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 飞手设置服务实现类
 */
@Slf4j
@Service
public class PilotSettingsServiceImpl extends ServiceImpl<PilotSettingsMapper, PilotSettings> implements PilotSettingsService {

    @Override
    public PilotSettings getByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotSettings::getPilotId, pilotId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceStatus(Long pilotId, Integer status) {
        LambdaUpdateWrapper<PilotSettings> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PilotSettings::getPilotId, pilotId)
                .set(PilotSettings::getServiceStatus, status);
        return this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initSettings(Long pilotId) {
        PilotSettings settings = new PilotSettings();
        settings.setPilotId(pilotId);
        settings.setServiceStatus(0); // 默认停止服务
        settings.setMissionDistance(new BigDecimal("50")); // 默认50公里
        settings.setAcceptDistance(new BigDecimal("5")); // 默认5公里
        settings.setIsAutoAccept(0); // 默认不自动接单
        settings.setMaxFlightHeight(500); // 默认最大飞行高度500米
        settings.setMaxFlightDistance(new BigDecimal("10")); // 默认最大飞行距离10公里
        settings.setAcceptNightMission(0); // 默认不接受夜间任务
        return this.save(settings);
    }
}