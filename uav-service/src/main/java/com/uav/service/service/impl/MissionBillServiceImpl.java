package com.uav.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uav.service.domain.MissionBill;
import com.uav.service.mapper.MissionBillMapper;
import com.uav.service.service.MissionBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务账单服务实现类
 */
@Slf4j
@Service
public class MissionBillServiceImpl extends ServiceImpl<MissionBillMapper, MissionBill> implements MissionBillService {

    @Override
    public MissionBill getByMissionId(Long missionId) {
        LambdaQueryWrapper<MissionBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MissionBill::getMissionId, missionId);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createBill(MissionBill bill) {
        return this.save(bill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePayStatus(Long missionId, Integer payStatus) {
        LambdaUpdateWrapper<MissionBill> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MissionBill::getMissionId, missionId)
                .set(MissionBill::getPayStatus, payStatus);
        return this.update(wrapper);
    }
}