package com.uav.service.pilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.common.service.CosStorageService;
import com.uav.model.entity.mission.UavMission;
import com.uav.service.mapper.UavPilotMapper;
import com.uav.service.pilot.mapper.PilotCertificationAuditMapper;
import com.uav.service.pilot.service.PilotInfoService;
import com.uav.service.mapper.UavMissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 飞手信息服务实现
 */
@Slf4j
@Service
public class PilotInfoServiceImpl extends ServiceImpl<UavPilotMapper, UavPilot> implements PilotInfoService {

    @Autowired
    private UavPilotMapper pilotMapper;

    @Autowired
    private PilotCertificationAuditMapper auditMapper;

    @Autowired
    private CosStorageService cosStorageService;

    @Autowired
    private UavMissionMapper missionMapper;

    /**
     * 获取飞手认证信息
     */
    @Override
    public PilotAuthInfoVo getPilotAuthInfo(Long pilotId) {
        log.info("获取飞手认证信息，pilotId: {}", pilotId);
        
        UavPilot pilot = pilotMapper.selectById(pilotId);
        if (pilot == null) {
            throw new RuntimeException("飞手不存在");
        }

        PilotAuthInfoVo vo = new PilotAuthInfoVo();
        BeanUtils.copyProperties(pilot, vo);

        // 生成带签名的图片URL（1小时有效期）
        if (StringUtils.hasText(pilot.getIdcardFrontUrl())) {
            vo.setIdcardFrontShowUrl(cosStorageService.getSignedUrl(pilot.getIdcardFrontUrl()));
        }
        if (StringUtils.hasText(pilot.getIdcardBackUrl())) {
            vo.setIdcardBackShowUrl(cosStorageService.getSignedUrl(pilot.getIdcardBackUrl()));
        }
        if (StringUtils.hasText(pilot.getIdcardHandUrl())) {
            vo.setIdcardHandShowUrl(cosStorageService.getSignedUrl(pilot.getIdcardHandUrl()));
        }
        if (StringUtils.hasText(pilot.getDriverLicenseFrontUrl())) {
            vo.setDriverLicenseFrontShowUrl(cosStorageService.getSignedUrl(pilot.getDriverLicenseFrontUrl()));
        }
        if (StringUtils.hasText(pilot.getDriverLicenseBackUrl())) {
            vo.setDriverLicenseBackShowUrl(cosStorageService.getSignedUrl(pilot.getDriverLicenseBackUrl()));
        }
        if (StringUtils.hasText(pilot.getDriverLicenseHandUrl())) {
            vo.setDriverLicenseHandShowUrl(cosStorageService.getSignedUrl(pilot.getDriverLicenseHandUrl()));
        }

        log.info("获取飞手认证信息成功，pilotId: {}, authStatus: {}", pilotId, vo.getAuthStatus());
        return vo;
    }

    /**
     * 更新飞手认证信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePilotAuthInfo(UpdatePilotAuthInfoForm form) {
        log.info("更新飞手认证信息，pilotId: {}", form.getPilotId());

        UavPilot pilot = pilotMapper.selectById(form.getPilotId());
        if (pilot == null) {
            throw new RuntimeException("飞手不存在");
        }

        // 只有未认证或认证失败的飞手才能更新认证信息
        if (pilot.getAuthStatus() != null && 
            pilot.getAuthStatus() != PilotCertificationStatus.UNCERTIFIED.getCode() &&
            pilot.getAuthStatus() != PilotCertificationStatus.REJECTED.getCode()) {
            throw new RuntimeException("当前状态不允许修改认证信息");
        }

        // 更新飞手信息
        UavPilot updatePilot = new UavPilot();
        updatePilot.setId(form.getPilotId());
        BeanUtils.copyProperties(form, updatePilot);
        
        // 提交认证后，状态变为审核中
        updatePilot.setAuthStatus(PilotCertificationStatus.UNDER_REVIEW.getCode());
        updatePilot.setUpdateTime(LocalDateTime.now());

        int updated = pilotMapper.updateById(updatePilot);
        
        if (updated > 0) {
            // 记录审核日志
            PilotCertificationAudit audit = new PilotCertificationAudit();
            audit.setPilotId(form.getPilotId());
            audit.setAuditStatus(PilotCertificationStatus.UNDER_REVIEW.getCode());
            audit.setAuditTime(LocalDateTime.now());
            audit.setAuditNote("飞手提交认证信息");
            auditMapper.insert(audit);
            
            log.info("更新飞手认证信息成功，pilotId: {}", form.getPilotId());
            return true;
        }

        log.error("更新飞手认证信息失败，pilotId: {}", form.getPilotId());
        return false;
    }

    /**
     * 获取飞手基本信息
     */
    @Override
    public PilotInfoVo getPilotInfo(Long pilotId) {
        log.info("获取飞手基本信息，pilotId: {}", pilotId);

        UavPilot pilot = pilotMapper.selectById(pilotId);
        if (pilot == null) {
            throw new RuntimeException("飞手不存在");
        }

        PilotInfoVo vo = new PilotInfoVo();
        BeanUtils.copyProperties(pilot, vo);

        // 计算驾龄
        if (StringUtils.hasText(pilot.getDriverLicenseIssueDate())) {
            try {
                LocalDate issueDate = LocalDate.parse(pilot.getDriverLicenseIssueDate(), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                int driverLicenseAge = LocalDate.now().getYear() - issueDate.getYear();
                vo.setDriverLicenseAge(driverLicenseAge);
            } catch (Exception e) {
                log.warn("解析驾驶证领证日期失败，pilotId: {}, date: {}", pilotId, pilot.getDriverLicenseIssueDate());
                vo.setDriverLicenseAge(0);
            }
        }

        // 查询完成的任务数
        LambdaQueryWrapper<UavMission> missionWrapper = new LambdaQueryWrapper<>();
        missionWrapper.eq(UavMission::getPilotId, pilotId);
        missionWrapper.eq(UavMission::getStatus, 4); // 4-已完成
        Long completedCount = missionMapper.selectCount(missionWrapper);
        vo.setCompletedMissions(completedCount.intValue());

        // 设置平均评分（从pilot表的avg_rating字段获取）
        vo.setAvgRating(pilot.getAvgRating() != null ? pilot.getAvgRating() : BigDecimal.ZERO);

        log.info("获取飞手基本信息成功，pilotId: {}, completedMissions: {}, avgRating: {}", 
            pilotId, vo.getCompletedMissions(), vo.getAvgRating());
        return vo;
    }

    /**
     * 审核飞手认证
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditPilotCertification(Long pilotId, Integer status, String rejectReason) {
        log.info("审核飞手认证，pilotId: {}, status: {}, rejectReason: {}", pilotId, status, rejectReason);

        UavPilot pilot = pilotMapper.selectById(pilotId);
        if (pilot == null) {
            throw new RuntimeException("飞手不存在");
        }

        // 只有审核中的飞手才能进行审核
        if (pilot.getAuthStatus() == null || 
            pilot.getAuthStatus() != PilotCertificationStatus.UNDER_REVIEW.getCode()) {
            throw new RuntimeException("飞手当前状态不是审核中，无法审核");
        }

        // 验证审核状态
        if (status != PilotCertificationStatus.CERTIFIED.getCode() && 
            status != PilotCertificationStatus.REJECTED.getCode()) {
            throw new RuntimeException("审核状态无效，必须是2(通过)或3(拒绝)");
        }

        // 如果是拒绝，必须提供拒绝原因
        if (status == PilotCertificationStatus.REJECTED.getCode() && !StringUtils.hasText(rejectReason)) {
            throw new RuntimeException("拒绝认证必须提供拒绝原因");
        }

        // 更新飞手认证状态
        UavPilot updatePilot = new UavPilot();
        updatePilot.setId(pilotId);
        updatePilot.setAuthStatus(status);
        updatePilot.setUpdateTime(LocalDateTime.now());

        int updated = pilotMapper.updateById(updatePilot);

        if (updated > 0) {
            // 记录审核日志
            PilotCertificationAudit audit = new PilotCertificationAudit();
            audit.setPilotId(pilotId);
            audit.setAuditStatus(status);
            audit.setAuditTime(LocalDateTime.now());
            audit.setAuditNote(status == PilotCertificationStatus.CERTIFIED.getCode() 
                ? "认证审核通过" 
                : "认证审核拒绝：" + rejectReason);
            audit.setRejectReason(rejectReason);
            auditMapper.insert(audit);

            log.info("审核飞手认证成功，pilotId: {}, status: {}", pilotId, status);
            return true;
        }

        log.error("审核飞手认证失败，pilotId: {}", pilotId);
        return false;
    }
}