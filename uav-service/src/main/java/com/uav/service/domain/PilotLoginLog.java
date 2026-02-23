package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 飞手登录日志实体类
 * 对应表：pilot_login_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pilot_login_log")
public class PilotLoginLog extends BaseEntity {

    /**
     * 飞手ID
     */
    private Long pilotId;

    /**
     * 登录IP地址
     */
    private String ipAddress;

    /**
     * 登录设备类型：1-Android 2-iOS 3-Web
     */
    private Integer deviceType;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * APP版本
     */
    private String appVersion;

    /**
     * 登录状态：0-失败 1-成功
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;
}