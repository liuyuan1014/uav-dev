package com.uav.model.vo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 附近飞手信息VO
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Schema(description = "附近飞手信息")
public class NearbyPilotVo {

    @Schema(description = "飞手ID")
    private Long pilotId;

    @Schema(description = "飞手姓名")
    private String pilotName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "距离（千米）")
    private BigDecimal distance;

    @Schema(description = "距离描述（如：1.5公里、500米）")
    private String distanceDesc;

    @Schema(description = "飞手状态：0-离线 1-空闲 2-接单中 3-执行中")
    private Integer status;

    @Schema(description = "飞手状态描述")
    private String statusDesc;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "完成任务数")
    private Integer completedMissions;
}