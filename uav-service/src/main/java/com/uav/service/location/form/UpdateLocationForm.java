package com.uav.service.location.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新位置表单
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Schema(description = "更新位置表单")
public class UpdateLocationForm {

    @Schema(description = "飞手ID", required = true)
    private Long pilotId;

    @Schema(description = "纬度", required = true, example = "23.1291")
    private BigDecimal latitude;

    @Schema(description = "经度", required = true, example = "113.2644")
    private BigDecimal longitude;

    @Schema(description = "精度（米）", example = "10.5")
    private BigDecimal accuracy;

    @Schema(description = "方向角（0-360度）", example = "90.0")
    private BigDecimal bearing;
}