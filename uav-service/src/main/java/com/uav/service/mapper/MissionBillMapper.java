package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.service.domain.MissionBill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务账单Mapper接口
 */
@Mapper
public interface MissionBillMapper extends BaseMapper<MissionBill> {
}