package com.uav.mission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.mission.UavMission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务 Mapper 接口
 */
@Mapper
public interface UavMissionMapper extends BaseMapper<UavMission> {
}