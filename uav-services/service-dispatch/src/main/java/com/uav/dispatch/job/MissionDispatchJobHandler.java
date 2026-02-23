package com.uav.dispatch.job;

import cn.hutool.json.JSONUtil;
import com.uav.dispatch.service.DispatchService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务派单JobHandler
 * 由XXL-JOB定时调度执行
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@Component
public class MissionDispatchJobHandler {

    @Autowired
    private DispatchService dispatchService;

    /**
     * 派单任务处理器
     * 每分钟执行一次，搜索附近飞手并推送任务
     */
    @XxlJob("missionDispatchJobHandler")
    public void execute() {
        try {
            // 1. 获取任务参数
            String param = XxlJobHelper.getJobParam();
            log.info("开始执行派单任务, 参数: {}", param);

            if (param == null || param.isEmpty()) {
                XxlJobHelper.handleFail("任务参数为空");
                return;
            }

            // 2. 解析参数
            DispatchParam dispatchParam = JSONUtil.toBean(param, DispatchParam.class);
            Long missionId = dispatchParam.getMissionId();

            if (missionId == null) {
                XxlJobHelper.handleFail("任务ID为空");
                return;
            }

            // 3. 执行派单
            Integer count = dispatchService.executeDispatch(missionId);

            // 4. 记录执行结果
            String result = String.format("派单完成: missionId=%d, 推送数量=%d", missionId, count);
            log.info(result);
            XxlJobHelper.handleSuccess(result);

        } catch (Exception e) {
            log.error("派单任务执行失败", e);
            XxlJobHelper.handleFail("派单任务执行失败: " + e.getMessage());
        }
    }

    /**
     * 派单参数
     */
    private static class DispatchParam {
        private Long missionId;
        private Double radiusKm;
        private Integer dispatchCount;

        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }
        public Double getRadiusKm() { return radiusKm; }
        public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }
        public Integer getDispatchCount() { return dispatchCount; }
        public void setDispatchCount(Integer dispatchCount) { this.dispatchCount = dispatchCount; }
    }
}