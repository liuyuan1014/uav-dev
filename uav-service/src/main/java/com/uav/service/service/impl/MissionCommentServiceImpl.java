package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.model.entity.mission.MissionComment;
import com.uav.service.mapper.MissionCommentMapper;
import com.uav.service.service.MissionCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务评价服务实现类
 */
@Slf4j
@Service
public class MissionCommentServiceImpl extends ServiceImpl<MissionCommentMapper, MissionComment> implements MissionCommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createComment(Long missionId, Long pilotId, Long customerId, Integer rate, String content, String tags) {
        MissionComment comment = new MissionComment();
        comment.setMissionId(missionId);
        comment.setPilotId(pilotId);
        comment.setCustomerId(customerId);
        comment.setRate(rate);
        comment.setContent(content);
        comment.setTags(tags);
        return this.save(comment);
    }

    @Override
    public MissionComment getByMissionId(Long missionId) {
        LambdaQueryWrapper<MissionComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionComment::getMissionId, missionId);
        return this.getOne(wrapper);
    }

    @Override
    public List<MissionComment> getByPilotId(Long pilotId) {
        LambdaQueryWrapper<MissionComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionComment::getPilotId, pilotId)
                .orderByDesc(MissionComment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Double calculateAverageRate(Long pilotId) {
        List<MissionComment> comments = getByPilotId(pilotId);
        if (comments == null || comments.isEmpty()) {
            return 5.0; // 默认5星
        }
        
        double sum = comments.stream()
                .mapToInt(MissionComment::getRate)
                .sum();
        return sum / comments.size();
    }
}