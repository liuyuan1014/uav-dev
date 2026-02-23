package com.uav.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.pilot.PilotAccountDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 飞手账户明细Mapper接口
 */
@Mapper
public interface PilotAccountDetailMapper extends BaseMapper<PilotAccountDetail> {
}