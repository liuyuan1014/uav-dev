package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.MissionStatusLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务状态日志Mapper接口
 */
@Mapper
public interface MissionStatusLogMapper extends BaseMapper<MissionStatusLog> {
}