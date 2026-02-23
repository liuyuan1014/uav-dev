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
     * 起点经度
     */
    private BigDecimal startPointLongitude;

    /**
     * 起点纬度
     */
    private BigDecimal startPointLatitude;

    /**
     * 终点位置名称
     */
    private String endLocation;

    /**
     * 终点经度
     */
    private BigDecimal endPointLongitude;

    /**
     * 终点纬度
     */
    private BigDecimal endPointLatitude;

    /**
     * 预估里程（公里）
     */
    private BigDecimal expectedDistance;

    /**
     * 实际飞行里程（公里）
     */
    private BigDecimal actualDistance;

    /**
     * 预估金额（元）
     */
    private BigDecimal expectAmount;

    /**
     * 实际金额（元）
     */
    private BigDecimal realAmount;

    /**
     * 任务费用（元）
     */
    private BigDecimal fee;

    /**
     * 客户好处费（元）
     */
    private BigDecimal favourFee;

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
     * 飞手接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 飞手到达时间
     */
    private LocalDateTime arriveTime;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 开始服务时间
     */
    private LocalDateTime startServiceTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime endTime;

    /**
     * 结束服务时间
     */
    private LocalDateTime endServiceTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 取消类型（对应 CancelReasonEnum 的 code）
     */
    private Integer cancelType;

    /**
     * 操作者类型（1=客户, 2=飞手, 3=系统）
     */
    private Integer operatorType;

    /**
     * 取消原因描述（冗余字段，方便查询）
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 无人机型号
     */
    private String uavModel;

    /**
     * 无人机序列号
     */
    private String uavSerialNumber;

    /**
     * 无人机起飞前照片
     */
    private String uavFrontPhoto;

    /**
     * 无人机降落后照片
     */
    private String uavLandingPhoto;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 取消规则ID
     */
    private Long cancelRuleId;
}