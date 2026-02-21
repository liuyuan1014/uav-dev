package com.uav.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.MissionStatus;
import com.uav.service.domain.UavMission;
import com.uav.service.mapper.UavMissionMapper;
import com.uav.service.service.UavMissionService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 无人机任务服务实现类
 * 使用 Redisson 分布式锁 + MyBatis-Plus 乐观锁保证并发安全
 */
@Slf4j
@Service
public class UavMissionServiceImpl extends ServiceImpl<UavMissionMapper, UavMission> 
        implements UavMissionService {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public UavMission publishMission(Long clientId, String startLocation, String endLocation) {
        // 生成唯一任务编号：M + yyyyMMddHHmmss + 4位随机数
        String missionNo = generateMissionNo();
        
        UavMission mission = new UavMission();
        mission.setMissionNo(missionNo);
        mission.setClientId(clientId);
        mission.setStartLocation(startLocation);
        mission.setEndLocation(endLocation);
        mission.setStatus(MissionStatus.PENDING.getCode());
        mission.setExpectedDistance(BigDecimal.ZERO);
        mission.setActualDistance(BigDecimal.ZERO);
        mission.setFee(BigDecimal.ZERO);
        mission.setVersion(0);
        
        // 保存到数据库
        save(mission);
        
        log.info("任务发布成功: missionNo={}, clientId={}", missionNo, clientId);
        return mission;
    }

    @Override
    public boolean acceptMission(Long missionId, Long pilotId, String deviceId) {
        // 构建分布式锁的 key（针对每个任务）
        String lockKey = "mission:accept:" + missionId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 第一层防护：Redisson 分布式锁
            // 尝试获取锁（等待时间5秒，锁自动释放时间10秒）
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            
            if (!isLocked) {
                log.warn("获取分布式锁失败，任务可能正在被其他飞手接单: missionId={}", missionId);
                throw new RuntimeException("系统繁忙，请稍后重试");
            }
            
            log.info("获取分布式锁成功: missionId={}, pilotId={}", missionId, pilotId);
            
            // 第二层防护：查询任务并校验状态
            UavMission mission = getById(missionId);
            
            if (mission == null) {
                log.error("任务不存在: missionId={}", missionId);
                throw new RuntimeException("任务不存在");
            }
            
            if (!MissionStatus.PENDING.getCode().equals(mission.getStatus())) {
                log.warn("任务状态不正确，无法接单: missionId={}, currentStatus={}", 
                        missionId, mission.getStatus());
                throw new RuntimeException("任务已被接单或状态不正确");
            }
            
            // 更新任务信息
            mission.setStatus(MissionStatus.ACCEPTED.getCode());
            mission.setPilotId(pilotId);
            mission.setDeviceId(deviceId);
            
            // 第三层防护：MyBatis-Plus 乐观锁
            // updateById 会自动在 WHERE 条件中加上 version 字段
            // SQL: UPDATE uav_mission SET ... WHERE id=? AND version=?
            boolean success = updateById(mission);
            
            if (!success) {
                log.warn("乐观锁更新失败，任务可能已被其他飞手接单: missionId={}", missionId);
                throw new RuntimeException("抢单失败，任务已被其他飞手接单");
            }
            
            log.info("飞手接单成功: missionId={}, pilotId={}, deviceId={}", 
                    missionId, pilotId, deviceId);
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断: missionId={}", missionId, e);
            throw new RuntimeException("获取锁被中断", e);
        } catch (RuntimeException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("接单失败: missionId={}, pilotId={}", missionId, pilotId, e);
            throw new RuntimeException("接单失败: " + e.getMessage(), e);
        } finally {
            // 释放锁（只有锁的持有者才能释放）
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("释放分布式锁: missionId={}", missionId);
            }
        }
    }

    @Override
    public boolean startMission(Long missionId) {
        UavMission mission = getById(missionId);
        
        if (mission == null) {
            log.error("任务不存在: missionId={}", missionId);
            throw new RuntimeException("任务不存在");
        }
        
        if (!MissionStatus.ACCEPTED.getCode().equals(mission.getStatus())) {
            log.warn("任务状态不正确，无法开始执行: missionId={}, currentStatus={}", 
                    missionId, mission.getStatus());
            throw new RuntimeException("任务状态不正确，只有已接单的任务才能开始执行");
        }
        
        // 更新任务状态为执行中
        mission.setStatus(MissionStatus.IN_PROGRESS.getCode());
        mission.setStartTime(LocalDateTime.now());
        
        boolean success = updateById(mission);
        
        if (success) {
            log.info("任务开始执行: missionId={}, pilotId={}", missionId, mission.getPilotId());
        } else {
            log.error("任务开始执行失败: missionId={}", missionId);
            throw new RuntimeException("任务开始执行失败");
        }
        
        return success;
    }

    /**
     * 生成唯一任务编号
     * 格式：M + yyyyMMddHHmmss + 4位随机数
     * 示例：M202602211400001234
     */
    private String generateMissionNo() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomNum = String.format("%04d", new Random().nextInt(10000));
        return "M" + timestamp + randomNum;
    }
}