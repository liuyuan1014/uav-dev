package com.uav.dispatch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.mission.MissionJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务调度Job Mapper
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Mapper
public interface MissionJobMapper extends BaseMapper<MissionJob> {
}