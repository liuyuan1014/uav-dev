package com.uav.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.CancelReasonEnum;
import com.uav.service.domain.MissionStatus;
import com.uav.service.domain.OperatorTypeEnum;
import com.uav.service.domain.UavMission;
import com.uav.service.mapper.UavMissionMapper;
import com.uav.service.service.UavMissionService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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

    @Override
    public boolean completeMission(Long missionId, BigDecimal actualDistance) {
        UavMission mission = getById(missionId);
        
        if (mission == null) {
            log.error("任务不存在: missionId={}", missionId);
            throw new RuntimeException("任务不存在");
        }
        
        if (!MissionStatus.IN_PROGRESS.getCode().equals(mission.getStatus())) {
            log.warn("任务状态不正确，无法完成: missionId={}, currentStatus={}",
                    missionId, mission.getStatus());
            throw new RuntimeException("任务状态不正确，只有执行中的任务才能完成");
        }
        
        // 简单计费公式：基础费用 + 里程费用
        // 基础费用：10元
        // 里程费用：5元/公里
        BigDecimal baseFee = new BigDecimal("10.00");
        BigDecimal distanceFee = actualDistance.multiply(new BigDecimal("5.00"));
        BigDecimal totalFee = baseFee.add(distanceFee).setScale(2, RoundingMode.HALF_UP);
        
        // 更新任务信息
        mission.setStatus(MissionStatus.COMPLETED.getCode());
        mission.setActualDistance(actualDistance);
        mission.setFee(totalFee);
        mission.setEndTime(LocalDateTime.now());
        mission.setCompleteTime(LocalDateTime.now());
        
        // 使用乐观锁更新
        boolean success = updateById(mission);
        
        if (!success) {
            log.error("任务完成失败（乐观锁冲突）: missionId={}", missionId);
            throw new RuntimeException("任务完成失败，请重试");
        }
        
        log.info("任务完成成功: missionId={}, actualDistance={}, fee={}",
                missionId, actualDistance, totalFee);
        return true;
    }

    @Override
    public Map<String, Object> cancelMission(Long missionId, Integer cancelType, Integer operatorType) {
        UavMission mission = getById(missionId);
        
        if (mission == null) {
            log.error("任务不存在: missionId={}", missionId);
            throw new RuntimeException("任务不存在");
        }
        
        // 已完成的任务不能取消
        if (MissionStatus.COMPLETED.getCode().equals(mission.getStatus())) {
            log.warn("任务已完成，无法取消: missionId={}", missionId);
            throw new RuntimeException("任务已完成，无法取消");
        }
        
        // 已取消的任务不能重复取消
        if (MissionStatus.CANCELLED.getCode().equals(mission.getStatus())) {
            log.warn("任务已取消，无法重复取消: missionId={}", missionId);
            throw new RuntimeException("任务已取消，无法重复取消");
        }
        
        // 验证取消类型和操作者类型
        CancelReasonEnum cancelReason = CancelReasonEnum.getByCode(cancelType);
        OperatorTypeEnum operator = OperatorTypeEnum.getByCode(operatorType);
        
        if (cancelReason == null) {
            log.error("无效的取消类型: cancelType={}", cancelType);
            throw new RuntimeException("无效的取消类型");
        }
        
        if (operator == null) {
            log.error("无效的操作者类型: operatorType={}", operatorType);
            throw new RuntimeException("无效的操作者类型");
        }
        
        // 计算退款金额（基于任务状态和操作者）
        BigDecimal refundAmount = calculateRefund(mission, operatorType);
        String refundReason = buildRefundReason(mission.getStatus(), operator, cancelReason);
        
        // 更新任务信息
        mission.setStatus(MissionStatus.CANCELLED.getCode());
        mission.setCancelType(cancelType);
        mission.setOperatorType(operatorType);
        mission.setCancelReason(cancelReason.getDescription());
        mission.setCancelTime(LocalDateTime.now());
        mission.setEndTime(LocalDateTime.now());
        
        // 使用乐观锁更新
        boolean success = updateById(mission);
        
        if (!success) {
            log.error("任务取消失败（乐观锁冲突）: missionId={}", missionId);
            throw new RuntimeException("任务取消失败，请重试");
        }
        
        log.info("任务取消成功: missionId={}, cancelType={}, operatorType={}, refundAmount={}",
                missionId, cancelType, operatorType, refundAmount);
        
        // 返回退款信息
        Map<String, Object> result = new HashMap<>();
        result.put("refundAmount", refundAmount);
        result.put("refundReason", refundReason);
        result.put("cancelReason", cancelReason.getDescription());
        result.put("operator", operator.getDescription());
        
        return result;
    }

    /**
     * 计算退款金额
     *
     * @param mission 任务对象
     * @param operatorType 操作者类型（1=客户, 2=飞手, 3=系统）
     * @return 退款金额
     */
    private BigDecimal calculateRefund(UavMission mission, Integer operatorType) {
        // 假设任务费用为100元（实际应该从订单中获取）
        BigDecimal totalFee = mission.getFee() != null && mission.getFee().compareTo(BigDecimal.ZERO) > 0
                ? mission.getFee()
                : new BigDecimal("100.00");
        
        Integer status = mission.getStatus();
        
        // 待接单状态（status=0）：任何人取消都全额退款
        if (MissionStatus.PENDING.getCode().equals(status)) {
            return totalFee;
        }
        
        // 已接单状态（status=1）
        if (MissionStatus.ACCEPTED.getCode().equals(status)) {
            if (OperatorTypeEnum.CLIENT.getCode().equals(operatorType)) {
                // 客户取消：退款90%（扣除10%违约金）
                return totalFee.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
            } else if (OperatorTypeEnum.PILOT.getCode().equals(operatorType)) {
                // 飞手取消：全额退款 + 补偿（这里简化为全额退款）
                return totalFee;
            } else {
                // 系统取消：全额退款
                return totalFee;
            }
        }
        
        // 执行中状态（status=2）
        if (MissionStatus.IN_PROGRESS.getCode().equals(status)) {
            if (OperatorTypeEnum.CLIENT.getCode().equals(operatorType)) {
                // 客户取消：退款70%（扣除30%违约金）
                return totalFee.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
            } else if (OperatorTypeEnum.PILOT.getCode().equals(operatorType)) {
                // 飞手取消：全额退款 + 补偿（这里简化为全额退款）
                return totalFee;
            } else {
                // 系统取消：全额退款
                return totalFee;
            }
        }
        
        // 其他状态：不退款
        return BigDecimal.ZERO;
    }

    /**
     * 构建退款原因描述
     */
    private String buildRefundReason(Integer status, OperatorTypeEnum operator, CancelReasonEnum cancelReason) {
        String statusDesc = MissionStatus.getDescriptionByCode(status);
        return String.format("任务状态：%s，%s发起取消，原因：%s",
                statusDesc, operator.getDescription(), cancelReason.getDescription());
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