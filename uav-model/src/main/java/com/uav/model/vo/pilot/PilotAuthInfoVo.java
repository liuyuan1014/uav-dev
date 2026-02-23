package com.uav.model.vo.pilot;

import lombok.Data;

/**
 * 飞手认证信息VO
 */
@Data
public class PilotAuthInfoVo {

    /**
     * 飞手ID
     */
    private Long id;

    /**
     * 飞手姓名
     */
    private String name;

    /**
     * 性别：1-男，2-女
     */
    private String gender;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 身份证号
     */
    private String idcardNo;

    /**
     * 身份证地址
     */
    private String idcardAddress;

    /**
     * 身份证有效期限
     */
    private String idcardExpire;

    /**
     * 身份证正面图片URL（存储路径）
     */
    private String idcardFrontUrl;

    /**
     * 身份证正面图片显示URL（带签名）
     */
    private String idcardFrontShowUrl;

    /**
     * 身份证反面图片URL（存储路径）
     */
    private String idcardBackUrl;

    /**
     * 身份证反面图片显示URL（带签名）
     */
    private String idcardBackShowUrl;

    /**
     * 手持身份证图片URL（存储路径）
     */
    private String idcardHandUrl;

    /**
     * 手持身份证图片显示URL（带签名）
     */
    private String idcardHandShowUrl;

    /**
     * 驾驶证编号
     */
    private String driverLicenseNo;

    /**
     * 驾驶证类型
     */
    private String driverLicenseClass;

    /**
     * 驾驶证初次领证日期
     */
    private String driverLicenseIssueDate;

    /**
     * 驾驶证有效期限
     */
    private String driverLicenseExpire;

    /**
     * 驾驶证正面图片URL（存储路径）
     */
    private String driverLicenseFrontUrl;

    /**
     * 驾驶证正面图片显示URL（带签名）
     */
    private String driverLicenseFrontShowUrl;

    /**
     * 驾驶证反面图片URL（存储路径）
     */
    private String driverLicenseBackUrl;

    /**
     * 驾驶证反面图片显示URL（带签名）
     */
    private String driverLicenseBackShowUrl;

    /**
     * 手持驾驶证图片URL（存储路径）
     */
    private String driverLicenseHandUrl;

    /**
     * 手持驾驶证图片显示URL（带签名）
     */
    private String driverLicenseHandShowUrl;

    /**
     * 认证状态：0-未认证，1-审核中，2-已认证，3-认证失败
     */
    private Integer authStatus;

    /**
     * 联系手机
     */
    private String contactPhone;

    /**
     * 联系地址
     */
    private String contactAddress;
}