package com.uav.gateway.impl;

import com.uav.api.service.UavCommandService;
import com.uav.gateway.config.NettyConnectManageService;
import com.uav.gateway.protocol.UavCommandType;
import com.uav.gateway.protocol.UavPacket;
import io.netty.channel.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 无人机指令服务实现（Gateway端）
 * 通过Netty连接向无人机下发指令
 *
 * 作为 Dubbo 服务提供者，供 uav-service 模块调用
 */
@Component
@DubboService(group = "gateway")
public class UavCommandServiceGatewayImpl implements UavCommandService {

    @Autowired
    private NettyConnectManageService nettyConnectManageService;
    
    /**
     * 引用Service端的Dubbo服务，用于设备验证
     */
    @DubboReference(group = "service")
    private UavCommandService uavCommandService;

    @Override
    public boolean sendCommand(String deviceId, String jsonCmd) {
        // 获取设备连接
        Channel channel = nettyConnectManageService.getChannel(deviceId);
        
        if (channel == null) {
            System.out.println("设备 " + deviceId + " 不在线，无法发送指令");
            return false;
        }
        
        if (!channel.isActive()) {
            System.out.println("设备 " + deviceId + " 连接不活跃，无法发送指令");
            // 从连接管理服务中移除此失效连接
            nettyConnectManageService.removeChannel(deviceId);
            return false;
        }
        
        try {
            // 构建 UavPacket 对象
            UavPacket packet = new UavPacket();
            packet.setMagic((short) 0xACED);           // 魔数
            packet.setVersion((byte) 1);               // 版本号
            packet.setCommand(UavCommandType.CONTROL); // 命令类型：控制指令
            packet.setBody(jsonCmd);                   // 消息体
            packet.setLength(jsonCmd.getBytes(StandardCharsets.UTF_8).length);
            
            // 发送 UavPacket 对象，UavEncoder 会自动编码
            channel.writeAndFlush(packet);
            
            System.out.println("指令已发送至设备 " + deviceId + ": " + jsonCmd);
            return true;
        } catch (Exception e) {
            System.err.println("发送指令到设备 " + deviceId + " 失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isOnline(String deviceId) {
        return nettyConnectManageService.isDeviceOnline(deviceId);
    }
    
    /**
     * 验证设备登录（通过Dubbo调用Service端）
     */
    @Override
    public boolean validateLogin(String deviceId, String authCode) {
        try {
            return uavCommandService.validateLogin(deviceId, authCode);
        } catch (Exception e) {
            System.err.println("调用设备验证服务失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}