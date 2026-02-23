package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.mapper.PilotAccountMapper;
import com.uav.service.service.PilotAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 飞手账户服务实现类
 */
@Slf4j
@Service
public class PilotAccountServiceImpl extends ServiceImpl<PilotAccountMapper, PilotAccount> implements PilotAccountService {

    @Override
    public PilotAccount getByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotAccount::getPilotId, pilotId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAmount(Long pilotId, BigDecimal amount) {
        return baseMapper.addAmount(pilotId, amount) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceAmount(Long pilotId, BigDecimal amount) {
        return baseMapper.reduceAmount(pilotId, amount) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeAmount(Long pilotId, BigDecimal amount) {
        return baseMapper.freezeAmount(pilotId, amount) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeAmount(Long pilotId, BigDecimal amount) {
        return baseMapper.unfreezeAmount(pilotId, amount) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAccount(Long pilotId) {
        PilotAccount account = new PilotAccount();
        account.setPilotId(pilotId);
        account.setTotalAmount(BigDecimal.ZERO);
        account.setLockAmount(BigDecimal.ZERO);
        account.setAvailableAmount(BigDecimal.ZERO);
        account.setTotalIncomeAmount(BigDecimal.ZERO);
        account.setTotalPayAmount(BigDecimal.ZERO);
        return this.save(account);
    }
}