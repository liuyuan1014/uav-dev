package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * XXL-JOB执行日志Mapper
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Mapper
public interface XxlJobLogMapper extends BaseMapper<XxlJobLog> {

    /**
     * 根据任务ID查询执行日志
     * 
     * @param jobId 任务ID
     * @param limit 查询数量
     * @return 执行日志列表
     */
    List<XxlJobLog> selectByJobId(@Param("jobId") Integer jobId, @Param("limit") Integer limit);

    /**
     * 查询失败的执行日志
     * 
     * @param jobId 任务ID
     * @param limit 查询数量
     * @return 执行日志列表
     */
    List<XxlJobLog> selectFailedLogs(@Param("jobId") Integer jobId, @Param("limit") Integer limit);

    /**
     * 统计任务执行次数
     * 
     * @param jobId 任务ID
     * @return 执行次数
     */
    int countByJobId(@Param("jobId") Integer jobId);

    /**
     * 统计任务成功次数
     * 
     * @param jobId 任务ID
     * @return 成功次数
     */
    int countSuccessByJobId(@Param("jobId") Integer jobId);

    /**
     * 统计任务失败次数
     * 
     * @param jobId 任务ID
     * @return 失败次数
     */
    int countFailedByJobId(@Param("jobId") Integer jobId);
}