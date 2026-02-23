package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务评价实体类
 * 对应表：mission_comment
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mission_comment")
public class MissionComment extends BaseEntity {

    /**
     * 任务ID
     */
    private Long missionId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 飞手ID
     */
    private Long pilotId;

    /**
     * 评分：1-5星
     */
    private Integer rate;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价标签（多个标签用逗号分隔）
     */
    private String tags;

    /**
     * 是否匿名：0-否 1-是
     */
    private Integer isAnonymous;

    /**
     * 状态：0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;
}