package com.uav.model.vo.dispatch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 派单记录VO
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Schema(description = "派单记录")
public class DispatchRecordVo {

    @Schema(description = "任务ID")
    private Long missionId;

    @Schema(description = "任务标题")
    private String missionTitle;

    @Schema(description = "飞手ID")
    private Long pilotId;

    @Schema(description = "飞手姓名")
    private String pilotName;

    @Schema(description = "距离（千米）")
    private BigDecimal distance;

    @Schema(description = "派单时间")
    private LocalDateTime dispatchTime;

    @Schema(description = "派单状态：0-已推送 1-已查看 2-已接单 3-已拒绝")
    private Integer status;

    @Schema(description = "派单状态描述")
    private String statusDesc;

    @Schema(description = "查看时间")
    private LocalDateTime viewTime;

    @Schema(description = "响应时间")
    private LocalDateTime responseTime;

    @Schema(description = "拒绝原因")
    private String rejectReason;
}