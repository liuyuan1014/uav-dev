package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.mapper.PilotAccountDetailMapper;
import com.uav.service.service.PilotAccountDetailService;
import com.uav.service.service.PilotAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 飞手账户明细服务实现类
 */
@Slf4j
@Service
public class PilotAccountDetailServiceImpl extends ServiceImpl<PilotAccountDetailMapper, PilotAccountDetail> implements PilotAccountDetailService {

    @Autowired
    private PilotAccountService pilotAccountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDetail(Long pilotId, Integer tradeType, BigDecimal tradeAmount, String content, Long missionId) {
        // 获取当前账户余额
        PilotAccount account = pilotAccountService.getByPilotId(pilotId);
        BigDecimal balance = account != null ? account.getAvailableAmount() : BigDecimal.ZERO;

        // 创建明细记录
        PilotAccountDetail detail = new PilotAccountDetail();
        detail.setPilotId(pilotId);
        detail.setTradeType(tradeType);
        detail.setAmount(tradeAmount);
        detail.setBalance(balance);
        detail.setContent(content);
        detail.setMissionId(missionId);
        
        return this.save(detail);
    }

    @Override
    public List<PilotAccountDetail> getByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotAccountDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotAccountDetail::getPilotId, pilotId)
                .orderByDesc(PilotAccountDetail::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public PilotAccountDetail getByMissionId(Long missionId) {
        LambdaQueryWrapper<PilotAccountDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotAccountDetail::getMissionId, missionId);
        return this.getOne(wrapper);
    }

    @Override
    public BigDecimal calculateTotalIncome(Long pilotId) {
        LambdaQueryWrapper<PilotAccountDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotAccountDetail::getPilotId, pilotId)
                .eq(PilotAccountDetail::getTradeType, 1); // 1-收入
        
        List<PilotAccountDetail> details = this.list(wrapper);
        return details.stream()
                .map(PilotAccountDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalExpense(Long pilotId) {
        LambdaQueryWrapper<PilotAccountDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotAccountDetail::getPilotId, pilotId)
                .eq(PilotAccountDetail::getTradeType, 2); // 2-支出
        
        List<PilotAccountDetail> details = this.list(wrapper);
        return details.stream()
                .map(PilotAccountDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}