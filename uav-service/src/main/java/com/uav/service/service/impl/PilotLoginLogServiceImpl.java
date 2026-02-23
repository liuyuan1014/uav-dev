package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.PilotLoginLog;
import com.uav.service.mapper.PilotLoginLogMapper;
import com.uav.service.service.PilotLoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 飞手登录日志服务实现类
 */
@Slf4j
@Service
public class PilotLoginLogServiceImpl extends ServiceImpl<PilotLoginLogMapper, PilotLoginLog> implements PilotLoginLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordLogin(Long pilotId, String ipAddress, String deviceType, String deviceModel,
                              String osVersion, String appVersion, Integer status, String msg) {
        PilotLoginLog loginLog = new PilotLoginLog();
        loginLog.setPilotId(pilotId);
        loginLog.setIpAddress(ipAddress);
        loginLog.setDeviceType(deviceType);
        loginLog.setDeviceModel(deviceModel);
        loginLog.setOsVersion(osVersion);
        loginLog.setAppVersion(appVersion);
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        return this.save(loginLog);
    }

    @Override
    public List<PilotLoginLog> getByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotLoginLog::getPilotId, pilotId)
                .orderByDesc(PilotLoginLog::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public PilotLoginLog getLatestByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotLoginLog::getPilotId, pilotId)
                .orderByDesc(PilotLoginLog::getCreateTime)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<PilotLoginLog> getFailedLoginsByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotLoginLog::getPilotId, pilotId)
                .eq(PilotLoginLog::getStatus, 2) // 2-失败
                .orderByDesc(PilotLoginLog::getCreateTime);
        return this.list(wrapper);
    }
}