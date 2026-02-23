package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务状态日志实体类
 * 对应表：mission_status_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mission_status_log")
public class MissionStatusLog extends BaseEntity {

    /**
     * 任务ID
     */
    private Long missionId;

    /**
     * 操作前状态
     */
    private Integer beforeStatus;

    /**
     * 操作后状态
     */
    private Integer afterStatus;

    /**
     * 操作类型：1-创建 2-接单 3-开始 4-完成 5-取消
     */
    private Integer operateType;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人类型：1-客户 2-飞手 3-系统
     */
    private Integer operatorType;

    /**
     * 操作描述
     */
    private String operateDesc;
}