package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.MissionMonitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务监控Mapper接口
 */
@Mapper
public interface MissionMonitorMapper extends BaseMapper<MissionMonitor> {
}