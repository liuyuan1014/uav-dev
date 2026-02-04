package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}