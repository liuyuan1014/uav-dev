package com.uav.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uav.model.entity.mission.MissionComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务评价Mapper接口
 */
@Mapper
public interface MissionCommentMapper extends BaseMapper<MissionComment> {
}