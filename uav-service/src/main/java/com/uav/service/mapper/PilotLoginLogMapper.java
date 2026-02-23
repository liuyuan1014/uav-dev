package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.PilotLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 飞手登录日志Mapper接口
 */
@Mapper
public interface PilotLoginLogMapper extends BaseMapper<PilotLoginLog> {
}