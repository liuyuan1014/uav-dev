package com.uav.controller;

import com.uav.common.Result;
import com.uav.model.vo.UavStatusVO;
import com.uav.service.query.UavQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 无人机查询控制器
 * 提供RESTful API用于查询无人机状态和历史轨迹
 */
@RestController
@RequestMapping("/api/uav")
@CrossOrigin(origins = "*") // 支持跨域请求
@Slf4j
public class UavQueryController {

    @Autowired
    private UavQueryService uavQueryService;

    /**
     * 获取无人机最新状态
     * 
     * @param deviceId 设备ID
     * @return Result<UavStatusVO> 最新状态信息
     */
    @GetMapping("/status")
    public Result<UavStatusVO> getLatestStatus(@RequestParam String deviceId) {
        try {
            log.info("查询设备ID: {} 的最新状态", deviceId);
            
            UavStatusVO status = uavQueryService.getLastStatus(deviceId);
            
            if (status != null) {
                log.info("成功获取设备ID: {} 的最新状态", deviceId);
                return Result.success(status);
            } else {
                log.info("未找到设备ID: {} 的最新状态", deviceId);
                return Result.error("未找到设备状态信息");
            }
        } catch (Exception e) {
            log.error("查询设备ID: {} 的最新状态时发生异常", deviceId, e);
            return Result.error("查询设备状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取无人机历史轨迹
     * 
     * @param deviceId 设备ID
     * @param start 开始时间戳（可选，默认为当前时间-1小时）
     * @param end 结束时间戳（可选，默认为当前时间）
     * @return Result<List<UavStatusVO>> 历史轨迹信息
     */
    @GetMapping("/track")
    public Result<List<UavStatusVO>> getHistoricalTrack(
            @RequestParam String deviceId,
            @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long end) {
        try {
            log.info("查询设备ID: {} 的历史轨迹, 开始时间: {}, 结束时间: {}", deviceId, start, end);
            
            List<UavStatusVO> track = uavQueryService.getHistoryTrack(deviceId, start, end);
            
            log.info("成功获取设备ID: {} 的历史轨迹，共 {} 条记录", deviceId, track.size());
            return Result.success(track);
        } catch (Exception e) {
            log.error("查询设备ID: {} 的历史轨迹时发生异常", deviceId, e);
            return Result.error("查询历史轨迹失败: " + e.getMessage());
        }
    }
}