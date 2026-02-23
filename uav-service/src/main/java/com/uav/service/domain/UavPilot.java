package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 无人机飞手实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("uav_pilot")
public class UavPilot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 飞手姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别 1:男 2:女
     */
    private Integer gender;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 身份证正面照片URL
     */
    private String idCardFrontImg;

    /**
     * 身份证反面照片URL
     */
    private String idCardBackImg;

    /**
     * 飞手执照编号(CAAC/AOPA)
     */
    private String licenseNo;

    /**
     * 执照图片URL
     */
    private String licenseImg;

    /**
     * 状态 0:未认证 1:审核中 2:认证通过 -1:认证失败
     */
    private Integer status;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 飞手评分（1-5分）
     */
    private BigDecimal rating;

    /**
     * 累计完成任务数
     */
    private Integer totalMissions;

    /**
     * 身份证正面照片URL（新字段名）
     */
    private String idcardFrontUrl;

    /**
     * 身份证反面照片URL（新字段名）
     */
    private String idcardBackUrl;

    /**
     * 身份证手持照片URL
     */
    private String idcardHandUrl;

    /**
     * 驾驶证正面照片URL
     */
    private String driverLicenseFrontUrl;

    /**
     * 驾驶证反面照片URL
     */
    private String driverLicenseBackUrl;

    /**
     * 驾驶证手持照片URL
     */
    private String driverLicenseHandUrl;

    /**
     * 驾驶证领证日期
     */
    private String driverLicenseIssueDate;

    /**
     * 认证状态：0=未认证, 1=审核中, 2=认证通过, 3=认证拒绝
     */
    private Integer authStatus;

    /**
     * 平均评分
     */
    private BigDecimal avgRating;
}