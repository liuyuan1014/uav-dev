package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.mission.MissionMonitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务监控Mapper接口
 */
@Mapper
public interface MissionMonitorMapper extends BaseMapper<MissionMonitor> {
}