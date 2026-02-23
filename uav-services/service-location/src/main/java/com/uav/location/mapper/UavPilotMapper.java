package com.uav.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.pilot.UavPilot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机飞手 Mapper 接口
 * 
 * @deprecated 临时依赖，将在阶段十一被 service-pilot-client 的 Feign 调用替换
 * TODO: 在阶段十一（创建service-client模块）时，使用 PilotClient 替换此 Mapper
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Deprecated
@Mapper
public interface UavPilotMapper extends BaseMapper<UavPilot> {

}