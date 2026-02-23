package com.uav.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.account.mapper.PilotAccountMapper;
import com.uav.account.service.PilotAccountService;
import com.uav.model.entity.pilot.PilotAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 飞手账户服务实现类
 */
@Slf4j
@Service
public class PilotAccountServiceImpl extends ServiceImpl<PilotAccountMapper, PilotAccount> 
        implements PilotAccountService {

    @Override
    public PilotAccount getByPilotId(Long pilotId) {
        LambdaQueryWrapper<PilotAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PilotAccount::getPilotId, pilotId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAmount(Long pilotId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("增加金额必须大于0, pilotId: {}, amount: {}", pilotId, amount);
            return false;
        }
        int rows = baseMapper.addAmount(pilotId, amount);
        if (rows > 0) {
            log.info("账户余额增加成功, pilotId: {}, amount: {}", pilotId, amount);
            return true;
        }
        log.error("账户余额增加失败, pilotId: {}, amount: {}", pilotId, amount);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceAmount(Long pilotId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("减少金额必须大于0, pilotId: {}, amount: {}", pilotId, amount);
            return false;
        }
        int rows = baseMapper.reduceAmount(pilotId, amount);
        if (rows > 0) {
            log.info("账户余额减少成功, pilotId: {}, amount: {}", pilotId, amount);
            return true;
        }
        log.error("账户余额减少失败, pilotId: {}, amount: {}", pilotId, amount);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeAmount(Long pilotId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("冻结金额必须大于0, pilotId: {}, amount: {}", pilotId, amount);
            return false;
        }
        int rows = baseMapper.freezeAmount(pilotId, amount);
        if (rows > 0) {
            log.info("账户金额冻结成功, pilotId: {}, amount: {}", pilotId, amount);
            return true;
        }
        log.error("账户金额冻结失败, pilotId: {}, amount: {}", pilotId, amount);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeAmount(Long pilotId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("解冻金额必须大于0, pilotId: {}, amount: {}", pilotId, amount);
            return false;
        }
        int rows = baseMapper.unfreezeAmount(pilotId, amount);
        if (rows > 0) {
            log.info("账户金额解冻成功, pilotId: {}, amount: {}", pilotId, amount);
            return true;
        }
        log.error("账户金额解冻失败, pilotId: {}, amount: {}", pilotId, amount);
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAccount(Long pilotId) {
        // 检查账户是否已存在
        PilotAccount existAccount = this.getByPilotId(pilotId);
        if (existAccount != null) {
            log.warn("飞手账户已存在, pilotId: {}", pilotId);
            return true;
        }

        // 创建新账户
        PilotAccount account = new PilotAccount();
        account.setPilotId(pilotId);
        account.setTotalAmount(BigDecimal.ZERO);
        account.setAvailableAmount(BigDecimal.ZERO);
        account.setLockAmount(BigDecimal.ZERO);
        account.setTotalIncomeAmount(BigDecimal.ZERO);
        account.setTotalPayAmount(BigDecimal.ZERO);
        
        boolean success = this.save(account);
        if (success) {
            log.info("飞手账户初始化成功, pilotId: {}", pilotId);
        } else {
            log.error("飞手账户初始化失败, pilotId: {}", pilotId);
        }
        return success;
    }
}