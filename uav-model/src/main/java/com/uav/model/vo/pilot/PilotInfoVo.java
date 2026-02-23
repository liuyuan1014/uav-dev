package com.uav.model.vo.pilot;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 飞手基本信息VO
 */
@Data
public class PilotInfoVo {

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
     * 联系手机
     */
    private String contactPhone;

    /**
     * 驾驶证类型
     */
    private String driverLicenseClass;

    /**
     * 驾龄（年）
     */
    private Integer driverLicenseAge;

    /**
     * 认证状态：0-未认证，1-审核中，2-已认证，3-认证失败
     */
    private Integer authStatus;

    /**
     * 平均评分
     */
    private BigDecimal avgRating;

    /**
     * 完成任务数
     */
    private Integer completedMissions;
}