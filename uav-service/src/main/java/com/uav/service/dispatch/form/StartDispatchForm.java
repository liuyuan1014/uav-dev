package com.uav.service.dispatch.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 开始派单表单
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Schema(description = "开始派单表单")
public class StartDispatchForm {

    @Schema(description = "任务ID", required = true)
    private Long missionId;

    @Schema(description = "搜索半径（千米）", example = "5.0")
    private Double radiusKm;

    @Schema(description = "派单数量", example = "20")
    private Integer dispatchCount;

    @Schema(description = "是否立即执行一次", example = "true")
    private Boolean executeNow;
}