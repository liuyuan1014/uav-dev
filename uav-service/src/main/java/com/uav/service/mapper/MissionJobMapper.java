package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.mission.MissionJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务调度关联Mapper
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Mapper
public interface MissionJobMapper extends BaseMapper<MissionJob> {

    /**
     * 根据任务ID查询调度任务
     * 
     * @param missionId 任务ID
     * @return 调度任务信息
     */
    MissionJob selectByMissionId(@Param("missionId") Long missionId);

    /**
     * 根据XXL-JOB任务ID查询
     * 
     * @param jobId XXL-JOB任务ID
     * @return 调度任务信息
     */
    MissionJob selectByJobId(@Param("jobId") Integer jobId);

    /**
     * 更新任务状态
     * 
     * @param id 主键ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}