package com.uav.model.entity.mission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.uav.model.entity.base.BaseEntity;
import lombok.EqualsAndHashCode;

/**
 * 任务调度关联表
 * 记录任务与XXL-JOB调度任务的关联关系
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mission_job")
public class MissionJob extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long missionId;

    /**
     * XXL-JOB任务ID
     */
    private Integer jobId;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * Cron表达式
     */
    private String scheduleConf;

    /**
     * JobHandler名称
     */
    private String executorHandler;

    /**
     * 任务参数（JSON格式）
     */
    private String executorParam;

    /**
     * 任务状态
     * 0-停止 1-运行中
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}