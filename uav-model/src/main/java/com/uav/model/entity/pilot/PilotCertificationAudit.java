package com.uav.model.entity.pilot;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.uav.model.entity.base.BaseEntity;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 飞手认证审核记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pilot_certification_audit")
public class PilotCertificationAudit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 飞手ID
     */
    private Long pilotId;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核状态：0=待审核, 1=审核通过, 2=审核拒绝
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 审核说明（新字段名，与auditRemark功能相同）
     */
    private String auditNote;

    /**
     * 拒绝原因
     */
    private String rejectReason;
}