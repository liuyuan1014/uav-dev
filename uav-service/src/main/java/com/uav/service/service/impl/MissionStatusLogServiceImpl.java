package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.MissionStatusLog;
import com.uav.service.mapper.MissionStatusLogMapper;
import com.uav.service.service.MissionStatusLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务状态日志服务实现类
 */
@Slf4j
@Service
public class MissionStatusLogServiceImpl extends ServiceImpl<MissionStatusLogMapper, MissionStatusLog> implements MissionStatusLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logStatusChange(Long missionId, Integer beforeStatus, Integer afterStatus,
                                   Integer operateType, Long operatorId, Integer operatorType, String operateDesc) {
        MissionStatusLog log = new MissionStatusLog();
        log.setMissionId(missionId);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setOperateType(operateType);
        log.setOperatorId(operatorId);
        log.setOperatorType(operatorType);
        log.setOperateDesc(operateDesc);
        return this.save(log);
    }

    @Override
    public List<MissionStatusLog> getByMissionId(Long missionId) {
        LambdaQueryWrapper<MissionStatusLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionStatusLog::getMissionId, missionId)
                .orderByAsc(MissionStatusLog::getCreateTime);
        return this.list(wrapper);
    }
}