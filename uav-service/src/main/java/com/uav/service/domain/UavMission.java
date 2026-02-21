package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 无人机任务实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("uav_mission")
public class UavMission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号（唯一）
     */
    private String missionNo;

    /**
     * 发布任务的客户ID
     */
    private Long clientId;

    /**
     * 接单飞手ID
     */
    private Long pilotId;

    /**
     * 执行任务的设备SN
     */
    private String deviceId;

    /**
     * 起点位置名称
     */
    private String startLocation;

    /**
     * 终点位置名称
     */
    private String endLocation;

    /**
     * 预估里程（公里）
     */
    private BigDecimal expectedDistance;

    /**
     * 实际飞行里程（公里）
     */
    private BigDecimal actualDistance;

    /**
     * 任务费用（元）
     */
    private BigDecimal fee;

    /**
     * 状态：0=待接单, 1=已接单, 2=执行中, 3=已完成, 4=已取消
     */
    private Integer status;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime endTime;
}