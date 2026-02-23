package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.MissionMonitor;
import com.uav.service.mapper.MissionMonitorMapper;
import com.uav.service.service.MissionMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 任务监控服务实现类
 */
@Slf4j
@Service
public class MissionMonitorServiceImpl extends ServiceImpl<MissionMonitorMapper, MissionMonitor> implements MissionMonitorService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMonitor(Long missionId, Integer status, BigDecimal longitude, BigDecimal latitude,
                                BigDecimal altitude, BigDecimal speed, Integer batteryLevel, Integer signalStrength) {
        MissionMonitor monitor = new MissionMonitor();
        monitor.setMissionId(missionId);
        monitor.setStatus(status);
        monitor.setLongitude(longitude);
        monitor.setLatitude(latitude);
        monitor.setAltitude(altitude);
        monitor.setSpeed(speed);
        monitor.setBatteryLevel(batteryLevel);
        monitor.setSignalStrength(signalStrength);
        return this.save(monitor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordException(Long missionId, Integer exceptionType, String exceptionDesc) {
        MissionMonitor monitor = new MissionMonitor();
        monitor.setMissionId(missionId);
        monitor.setExceptionType(exceptionType);
        monitor.setExceptionDesc(exceptionDesc);
        return this.save(monitor);
    }

    @Override
    public List<MissionMonitor> getByMissionId(Long missionId) {
        LambdaQueryWrapper<MissionMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionMonitor::getMissionId, missionId)
                .orderByDesc(MissionMonitor::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public MissionMonitor getLatestByMissionId(Long missionId) {
        LambdaQueryWrapper<MissionMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionMonitor::getMissionId, missionId)
                .orderByDesc(MissionMonitor::getCreateTime)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<MissionMonitor> getExceptionsByMissionId(Long missionId) {
        LambdaQueryWrapper<MissionMonitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionMonitor::getMissionId, missionId)
                .isNotNull(MissionMonitor::getExceptionType)
                .orderByDesc(MissionMonitor::getCreateTime);
        return this.list(wrapper);
    }
}