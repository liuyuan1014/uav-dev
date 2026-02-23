package com.uav.pilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uav.model.entity.pilot.UavPilot;
import com.uav.model.form.pilot.UpdatePilotAuthInfoForm;
import com.uav.model.vo.pilot.PilotAuthInfoVo;
import com.uav.model.vo.pilot.PilotInfoVo;

/**
 * 飞手信息服务接口
 */
public interface PilotInfoService extends IService<UavPilot> {

    /**
     * 获取飞手认证信息
     * @param pilotId 飞手ID
     * @return 飞手认证信息VO
     */
    PilotAuthInfoVo getPilotAuthInfo(Long pilotId);

    /**
     * 更新飞手认证信息
     * @param updatePilotAuthInfoForm 更新表单
     * @return 是否成功
     */
    Boolean updatePilotAuthInfo(UpdatePilotAuthInfoForm updatePilotAuthInfoForm);

    /**
     * 获取飞手基本信息
     * @param pilotId 飞手ID
     * @return 飞手基本信息VO
     */
    PilotInfoVo getPilotInfo(Long pilotId);

    /**
     * 审核飞手认证
     * @param pilotId 飞手ID
     * @param status 审核状态：2-通过，3-拒绝
     * @param rejectReason 拒绝原因（status=3时必填）
     * @return 是否成功
     */
    Boolean auditPilotCertification(Long pilotId, Integer status, String rejectReason);
}