package com.uav.model.vo.dispatch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 派单统计VO
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Schema(description = "派单统计")
public class DispatchStatisticsVo {

    @Schema(description = "任务ID")
    private Long missionId;

    @Schema(description = "总派单数")
    private Integer totalDispatched;

    @Schema(description = "已查看数")
    private Integer viewedCount;

    @Schema(description = "已接单数")
    private Integer acceptedCount;

    @Schema(description = "已拒绝数")
    private Integer rejectedCount;

    @Schema(description = "未响应数")
    private Integer noResponseCount;

    @Schema(description = "派单轮次")
    private Integer dispatchRound;

    @Schema(description = "是否已接单")
    private Boolean isAccepted;

    @Schema(description = "接单飞手ID")
    private Long acceptedPilotId;

    @Schema(description = "接单飞手姓名")
    private String acceptedPilotName;
}