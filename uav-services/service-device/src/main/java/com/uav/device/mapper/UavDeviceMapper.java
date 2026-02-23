package com.uav.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.device.UavDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 无人机设备 Mapper 接口
 */
@Mapper
public interface UavDeviceMapper extends BaseMapper<UavDevice> {

}