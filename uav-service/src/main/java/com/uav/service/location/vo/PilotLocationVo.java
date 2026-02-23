package com.uav.service.location.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 飞手位置信息VO
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Schema(description = "飞手位置信息")
public class PilotLocationVo {

    @Schema(description = "飞手ID")
    private Long pilotId;

    @Schema(description = "飞手姓名")
    private String pilotName;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "精度（米）")
    private BigDecimal accuracy;

    @Schema(description = "方向角（0-360度）")
    private BigDecimal bearing;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否在线")
    private Boolean online;
}