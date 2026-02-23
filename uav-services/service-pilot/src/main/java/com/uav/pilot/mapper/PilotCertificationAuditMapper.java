package com.uav.pilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.pilot.PilotCertificationAudit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 飞手认证审核记录 Mapper
 */
@Mapper
public interface PilotCertificationAuditMapper extends BaseMapper<PilotCertificationAudit> {
}