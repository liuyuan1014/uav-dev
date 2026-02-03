package com.uav.gateway.impl;

import com.uav.api.service.UavCommandService;
import com.uav.gateway.config.NettyConnectManageService;
import com.uav.gateway.protocol.UavPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 无人机指令服务实现
 * 通过Netty连接向无人机下发指令
 */
@DubboService
@Service
public class UavCommandServiceImpl implements UavCommandService {

    @Autowired
    private NettyConnectManageService nettyConnectManageService;

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
            // 构建UavPacket用于发送指令
            // 使用命令类型3表示指令下发
            byte commandType = 3; // 假设3为指令下发命令类型
            
            // 计算消息体长度
            byte[] bodyBytes = jsonCmd.getBytes(StandardCharsets.UTF_8);
            int bodyLength = bodyBytes.length;
            
            // 创建完整的消息包 - 遵循与客户端相同的协议格式
            ByteBuf buffer = channel.alloc().buffer();
            buffer.writeShort((short) 0xACED);  // 魔数
            buffer.writeByte((byte) 1);         // 版本号
            buffer.writeByte(commandType);      // 命令类型：指令下发
            buffer.writeInt(bodyLength);        // 消息体长度
            buffer.writeBytes(bodyBytes);       // 消息体
            
            // 发送指令
            channel.writeAndFlush(buffer);
            
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
}