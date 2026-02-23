package com.uav.service.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 飞手设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "飞手设置")
@TableName("pilot_settings")
public class PilotSettings extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "飞手ID")
    @TableField("pilot_id")
    private Long pilotId;

    @Schema(description = "服务状态 1：开始接单 0：未接单")
    @TableField("service_status")
    private Integer serviceStatus;

    @Schema(description = "任务距离设置（公里）")
    @TableField("mission_distance")
    private BigDecimal missionDistance;

    @Schema(description = "接单距离设置（公里）")
    @TableField("accept_distance")
    private BigDecimal acceptDistance;

    @Schema(description = "是否自动接单 1：是 0：否")
    @TableField("is_auto_accept")
    private Integer isAutoAccept;

    @Schema(description = "最大飞行高度设置（米）")
    @TableField("max_flight_height")
    private BigDecimal maxFlightHeight;

    @Schema(description = "最大飞行距离设置（公里）")
    @TableField("max_flight_distance")
    private BigDecimal maxFlightDistance;

    @Schema(description = "是否接受夜间任务 1：是 0：否")
    @TableField("accept_night_mission")
    private Integer acceptNightMission;

}