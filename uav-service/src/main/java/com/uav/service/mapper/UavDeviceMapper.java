package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.UavDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机设备 Mapper 接口
 */
@Mapper
public interface UavDeviceMapper extends BaseMapper<UavDevice> {

}