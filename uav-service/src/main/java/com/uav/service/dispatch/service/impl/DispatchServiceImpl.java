package com.uav.service.dispatch.service.impl;

import cn.hutool.json.JSONUtil;
import com.uav.common.constant.RedisConstant;
import com.uav.common.util.DispatchUtil;
import com.uav.service.config.xxl.XxlJobClient;
import com.uav.service.dispatch.service.DispatchService;
import com.uav.model.entity.mission.MissionJob;
import com.uav.model.entity.mission.UavMission;
import com.uav.service.location.service.PilotLocationService;
import com.uav.service.mapper.MissionJobMapper;
import com.uav.service.mapper.UavMissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 派单服务实现
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Slf4j
@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private XxlJobClient xxlJobClient;

    @Autowired
    private PilotLocationService locationService;

    @Autowired
    private UavMissionMapper missionMapper;

    @Autowired
    private MissionJobMapper missionJobMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 执行器组ID（需要在XXL-JOB Admin中配置）
     */
    private static final Integer JOB_GROUP_ID = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer startDispatch(StartDispatchForm form) {
        Long missionId = form.getMissionId();
        
        try {
            // 1. 检查任务是否存在
            UavMission mission = missionMapper.selectById(missionId);
            if (mission == null) {
                log.error("任务不存在: missionId={}", missionId);
                return null;
            }

            // 2. 检查是否已经创建过派单任务
            MissionJob existingJob = missionJobMapper.selectByMissionId(missionId);
            if (existingJob != null && existingJob.getStatus() == 1) {
                log.warn("派单任务已存在且正在运行: missionId={}, jobId={}", 
                    missionId, existingJob.getJobId());
                return existingJob.getJobId();
            }

            // 3. 获取有效的搜索半径
            Double radiusKm = DispatchUtil.getValidRadius(form.getRadiusKm());
            Integer dispatchCount = form.getDispatchCount() != null ? 
                form.getDispatchCount() : DispatchUtil.DEFAULT_DISPATCH_COUNT;

            // 4. 创建XXL-JOB任务
            String jobDesc = DispatchUtil.generateJobDesc(missionId);
            String scheduleConf = DispatchUtil.DISPATCH_JOB_CRON;
            String executorHandler = DispatchUtil.getDispatchJobHandler();
            String executorParam = JSONUtil.toJsonStr(new DispatchParam(missionId, radiusKm, dispatchCount));

            Integer jobId = xxlJobClient.addJob(JOB_GROUP_ID, jobDesc, scheduleConf, 
                executorHandler, executorParam);

            if (jobId == null) {
                log.error("创建XXL-JOB任务失败: missionId={}", missionId);
                return null;
            }

            // 5. 保存任务关联信息
            MissionJob missionJob = new MissionJob();
            missionJob.setMissionId(missionId);
            missionJob.setJobId(jobId);
            missionJob.setJobDesc(jobDesc);
            missionJob.setScheduleConf(scheduleConf);
            missionJob.setExecutorHandler(executorHandler);
            missionJob.setExecutorParam(executorParam);
            missionJob.setStatus(0); // 初始状态：停止

            if (existingJob != null) {
                // 更新现有记录
                missionJob.setId(existingJob.getId());
                missionJobMapper.updateById(missionJob);
            } else {
                // 插入新记录
                missionJobMapper.insert(missionJob);
            }

            // 6. 启动任务
            boolean started = xxlJobClient.startJob(jobId);
            if (started) {
                missionJobMapper.updateStatus(missionJob.getId(), 1);
                log.info("派单任务启动成功: missionId={}, jobId={}", missionId, jobId);
            }

            // 7. 如果需要立即执行一次
            if (Boolean.TRUE.equals(form.getExecuteNow())) {
                xxlJobClient.triggerJob(jobId, executorParam);
                log.info("立即触发派单任务: missionId={}, jobId={}", missionId, jobId);
            }

            return jobId;
        } catch (Exception e) {
            log.error("开始派单失败: missionId={}", missionId, e);
            throw new RuntimeException("开始派单失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean stopDispatch(Long missionId) {
        try {
            // 1. 查询任务关联信息
            MissionJob missionJob = missionJobMapper.selectByMissionId(missionId);
            if (missionJob == null) {
                log.warn("派单任务不存在: missionId={}", missionId);
                return false;
            }

            Integer jobId = missionJob.getJobId();

            // 2. 停止XXL-JOB任务
            boolean stopped = xxlJobClient.stopJob(jobId);
            if (stopped) {
                // 3. 更新状态
                missionJobMapper.updateStatus(missionJob.getId(), 0);
                
                // 4. 清理Redis数据
                cleanupDispatchData(missionId);
                
                log.info("派单任务停止成功: missionId={}, jobId={}", missionId, jobId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("停止派单失败: missionId={}", missionId, e);
            return false;
        }
    }

    @Override
    public Integer executeDispatch(Long missionId) {
        try {
            log.info("开始执行派单: missionId={}", missionId);

            // 1. 检查任务是否超时
            if (checkAndHandleTimeout(missionId)) {
                log.info("任务已超时，停止派单: missionId={}", missionId);
                return 0;
            }

            // 2. 检查任务是否已被接单
            UavMission mission = missionMapper.selectById(missionId);
            if (mission == null || mission.getStatus() != 1) { // 1-待接单
                log.info("任务状态不是待接单，停止派单: missionId={}, status={}", 
                    missionId, mission != null ? mission.getStatus() : null);
                stopDispatch(missionId);
                return 0;
            }

            // 3. 获取派单参数
            MissionJob missionJob = missionJobMapper.selectByMissionId(missionId);
            if (missionJob == null) {
                log.error("派单任务不存在: missionId={}", missionId);
                return 0;
            }

            DispatchParam param = JSONUtil.toBean(missionJob.getExecutorParam(), DispatchParam.class);

            // 4. 搜索附近空闲飞手
            List<NearbyPilotVo> nearbyPilots = locationService.searchNearbyIdlePilots(
                mission.getStartLatitude(),
                mission.getStartLongitude(),
                param.getRadiusKm(),
                param.getDispatchCount()
            );

            if (nearbyPilots.isEmpty()) {
                log.info("未找到附近空闲飞手: missionId={}", missionId);
                return 0;
            }

            // 5. 过滤已推送过的飞手
            List<Long> pilotIds = nearbyPilots.stream()
                .map(NearbyPilotVo::getPilotId)
                .filter(pilotId -> !isAlreadyDispatched(missionId, pilotId))
                .collect(Collectors.toList());

            if (pilotIds.isEmpty()) {
                log.info("所有附近飞手都已推送过: missionId={}", missionId);
                return 0;
            }

            // 6. 批量推送任务
            Integer count = batchPushToPilots(missionId, pilotIds);
            log.info("派单执行完成: missionId={}, 推送数量={}", missionId, count);

            return count;
        } catch (Exception e) {
            log.error("执行派单失败: missionId={}", missionId, e);
            return 0;
        }
    }

    @Override
    public Boolean pushToPilot(Long missionId, Long pilotId) {
        try {
            // 1. 构造任务信息
            UavMission mission = missionMapper.selectById(missionId);
            if (mission == null) {
                return false;
            }

            // 2. 推送到Redis临时队列
            String queueKey = RedisConstant.PILOT_TEMP_QUEUE_KEY + pilotId;
            redisTemplate.opsForList().rightPush(queueKey, missionId);
            
            // 设置过期时间（15分钟）
            redisTemplate.expire(queueKey, 15, TimeUnit.MINUTES);

            // 3. 记录已推送
            String dispatchedKey = RedisConstant.MISSION_DISPATCHED_KEY + missionId;
            redisTemplate.opsForSet().add(dispatchedKey, pilotId);
            redisTemplate.expire(dispatchedKey, 1, TimeUnit.HOURS);

            log.info("推送任务成功: missionId={}, pilotId={}", missionId, pilotId);
            return true;
        } catch (Exception e) {
            log.error("推送任务失败: missionId={}, pilotId={}", missionId, pilotId, e);
            return false;
        }
    }

    @Override
    public Integer batchPushToPilots(Long missionId, List<Long> pilotIds) {
        int successCount = 0;
        for (Long pilotId : pilotIds) {
            if (pushToPilot(missionId, pilotId)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public List<DispatchRecordVo> getDispatchRecords(Long missionId) {
        // TODO: 实现派单记录查询
        // 可以从Redis或数据库中查询派单记录
        return new ArrayList<>();
    }

    @Override
    public DispatchStatisticsVo getDispatchStatistics(Long missionId) {
        // TODO: 实现派单统计
        DispatchStatisticsVo vo = new DispatchStatisticsVo();
        vo.setMissionId(missionId);
        
        // 从Redis获取已推送的飞手数量
        String dispatchedKey = RedisConstant.MISSION_DISPATCHED_KEY + missionId;
        Long totalDispatched = redisTemplate.opsForSet().size(dispatchedKey);
        vo.setTotalDispatched(totalDispatched != null ? totalDispatched.intValue() : 0);
        
        return vo;
    }

    @Override
    public Boolean checkAndHandleTimeout(Long missionId) {
        UavMission mission = missionMapper.selectById(missionId);
        if (mission == null) {
            return true;
        }

        boolean timeout = DispatchUtil.isTimeout(mission.getCreateTime());
        if (timeout) {
            // 停止派单
            stopDispatch(missionId);
            
            // TODO: 更新任务状态为超时
            log.info("任务超时，已停止派单: missionId={}", missionId);
        }

        return timeout;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean acceptMission(Long missionId, Long pilotId) {
        try {
            // 1. 停止派单
            stopDispatch(missionId);

            // 2. 更新任务状态
            // TODO: 调用任务服务更新状态

            log.info("飞手接单成功: missionId={}, pilotId={}", missionId, pilotId);
            return true;
        } catch (Exception e) {
            log.error("飞手接单失败: missionId={}, pilotId={}", missionId, pilotId, e);
            return false;
        }
    }

    @Override
    public Boolean rejectMission(Long missionId, Long pilotId, String reason) {
        try {
            // TODO: 记录拒单信息
            log.info("飞手拒单: missionId={}, pilotId={}, reason={}", missionId, pilotId, reason);
            return true;
        } catch (Exception e) {
            log.error("飞手拒单失败: missionId={}, pilotId={}", missionId, pilotId, e);
            return false;
        }
    }

    @Override
    public void cleanupDispatchData(Long missionId) {
        try {
            // 清理已推送记录
            String dispatchedKey = RedisConstant.MISSION_DISPATCHED_KEY + missionId;
            redisTemplate.delete(dispatchedKey);

            log.info("清理派单数据成功: missionId={}", missionId);
        } catch (Exception e) {
            log.error("清理派单数据失败: missionId={}", missionId, e);
        }
    }

    /**
     * 检查飞手是否已被推送过
     */
    private boolean isAlreadyDispatched(Long missionId, Long pilotId) {
        String dispatchedKey = RedisConstant.MISSION_DISPATCHED_KEY + missionId;
        Boolean isMember = redisTemplate.opsForSet().isMember(dispatchedKey, pilotId);
        return Boolean.TRUE.equals(isMember);
    }

    /**
     * 派单参数内部类
     */
    private static class DispatchParam {
        private Long missionId;
        private Double radiusKm;
        private Integer dispatchCount;

        public DispatchParam() {}

        public DispatchParam(Long missionId, Double radiusKm, Integer dispatchCount) {
            this.missionId = missionId;
            this.radiusKm = radiusKm;
            this.dispatchCount = dispatchCount;
        }

        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }
        public Double getRadiusKm() { return radiusKm; }
        public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }
        public Integer getDispatchCount() { return dispatchCount; }
        public void setDispatchCount(Integer dispatchCount) { this.dispatchCount = dispatchCount; }
    }
}