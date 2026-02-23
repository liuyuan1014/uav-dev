package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.PilotSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 飞手设置Mapper接口
 */
@Mapper
public interface PilotSettingsMapper extends BaseMapper<PilotSettings> {
}