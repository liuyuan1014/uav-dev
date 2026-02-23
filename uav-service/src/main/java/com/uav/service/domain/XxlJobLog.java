package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * XXL-JOB执行日志
 * 记录任务执行的详细信息
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("xxl_job_log")
public class XxlJobLog extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Integer jobId;

    /**
     * 执行器地址
     */
    private String executorAddress;

    /**
     * JobHandler名称
     */
    private String executorHandler;

    /**
     * 执行参数
     */
    private String executorParam;

    /**
     * 执行时间
     */
    private Date triggerTime;

    /**
     * 调度结果
     * 200-成功 500-失败
     */
    private Integer triggerCode;

    /**
     * 调度日志
     */
    private String triggerMsg;

    /**
     * 执行时间
     */
    private Date handleTime;

    /**
     * 执行结果
     * 200-成功 500-失败
     */
    private Integer handleCode;

    /**
     * 执行日志
     */
    private String handleMsg;

    /**
     * 告警状态
     * 0-默认 1-无需告警 2-告警成功 3-告警失败
     */
    private Integer alarmStatus;
}