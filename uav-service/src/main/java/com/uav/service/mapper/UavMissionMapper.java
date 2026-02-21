package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.UavMission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机任务 Mapper 接口
 */
@Mapper
public interface UavMissionMapper extends BaseMapper<UavMission> {
}