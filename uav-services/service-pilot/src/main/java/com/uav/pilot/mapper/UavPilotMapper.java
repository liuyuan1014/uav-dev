package com.uav.pilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.pilot.UavPilot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机飞手 Mapper 接口
 */
@Mapper
public interface UavPilotMapper extends BaseMapper<UavPilot> {

}