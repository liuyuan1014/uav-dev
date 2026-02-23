package com.uav.service.pilot.form;

import lombok.Data;

/**
 * 更新飞手认证信息表单
 */
@Data
public class UpdatePilotAuthInfoForm {

    /**
     * 飞手ID
     */
    private Long pilotId;

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
     * 身份证正面图片URL
     */
    private String idcardFrontUrl;

    /**
     * 身份证反面图片URL
     */
    private String idcardBackUrl;

    /**
     * 手持身份证图片URL
     */
    private String idcardHandUrl;

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
     * 驾驶证正面图片URL
     */
    private String driverLicenseFrontUrl;

    /**
     * 驾驶证反面图片URL
     */
    private String driverLicenseBackUrl;

    /**
     * 手持驾驶证图片URL
     */
    private String driverLicenseHandUrl;

    /**
     * 联系手机
     */
    private String contactPhone;

    /**
     * 联系地址
     */
    private String contactAddress;
}