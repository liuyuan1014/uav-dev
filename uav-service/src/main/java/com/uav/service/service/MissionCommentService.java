package com.uav.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.service.domain.MissionComment;

import java.util.List;

/**
 * 任务评价服务接口
 */
public interface MissionCommentService extends IService<MissionComment> {

    /**
     * 创建任务评价
     *
     * @param missionId 任务ID
     * @param pilotId 飞手ID
     * @param customerId 客户ID
     * @param rate 评分（1-5星）
     * @param content 评价内容
     * @param tags 评价标签（逗号分隔）
     * @return 是否成功
     */
    boolean createComment(Long missionId, Long pilotId, Long customerId, Integer rate, String content, String tags);

    /**
     * 根据任务ID获取评价
     *
     * @param missionId 任务ID
     * @return 评价信息
     */
    MissionComment getByMissionId(Long missionId);

    /**
     * 根据飞手ID获取评价列表
     *
     * @param pilotId 飞手ID
     * @return 评价列表
     */
    List<MissionComment> getByPilotId(Long pilotId);

    /**
     * 计算飞手平均评分
     *
     * @param pilotId 飞手ID
     * @return 平均评分
     */
    Double calculateAverageRate(Long pilotId);
}