package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 无人机设备实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("uav_device")
public class UavDevice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 归属飞手ID
     */
    private Long pilotId;

    /**
     * 设备序列号(SN码, 唯一标识)
     */
    private String serialNumber;

    /**
     * 型号(如: DJI M300)
     */
    private String model;

    /**
     * 类型 1:多旋翼 2:固定翼 3:垂直起降
     */
    private Integer type;

    /**
     * 最大载重(kg)
     */
    private BigDecimal loadCapacity;

    /**
     * 设备接入鉴权码(用于Netty登录)
     */
    private String authCode;

    /**
     * 状态 0:离线 1:在线 2:任务中
     */
    private Integer status;
}