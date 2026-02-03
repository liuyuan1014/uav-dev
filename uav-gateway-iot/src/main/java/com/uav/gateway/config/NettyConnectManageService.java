package com.uav.gateway.config;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty连接管理服务
 * 维护设备ID与Channel的映射关系，用于实现服务器向无人机下发指令
 */
@Service
public class NettyConnectManageService {

    private static final Logger logger = LoggerFactory.getLogger(NettyConnectManageService.class);

    // 设备ID与Channel的映射关系
    // ConcurrentHashMap保证多线程下的安全操作（比如同时添加/移除连接）
    private final ConcurrentHashMap<String, Channel> deviceChannelMap = new ConcurrentHashMap<>();

    // 定义Channel的属性键：用于给Channel绑定设备ID
    // 静态常量，全局唯一，命名为DEVICE_ID_KEY，值是"deviceId"
    public static final AttributeKey<String> DEVICE_ID_KEY = AttributeKey.valueOf("deviceId");

    /**
     * 添加设备连接
     *
     * @param deviceId 设备ID
     * @param channel  连接通道
     */
    public void addChannel(String deviceId, Channel channel) {
        // 将设备ID绑定到Channel上，方便后续断开连接时获取设备ID
        channel.attr(DEVICE_ID_KEY).set(deviceId);
        
        // 将设备ID与Channel的映射关系存入Map
        deviceChannelMap.put(deviceId, channel);
        
        logger.info("设备上线: {}", deviceId);
        System.out.println("设备上线: " + deviceId);
    }

    /**
     * 根据设备ID移除连接
     *
     * @param deviceId 设备ID
     * @return 是否成功移除
     */
    public boolean removeChannel(String deviceId) {
        Channel removed = deviceChannelMap.remove(deviceId);
        if (removed != null) {
            logger.info("设备连接已移除: {}", deviceId);
            return true;
        }
        return false;
    }

    /**
     * 根据Channel移除连接
     *
     * @param channel 连接通道
     * @return 是否成功移除
     */
    public boolean removeChannel(Channel channel) {
        // 从Channel上获取设备ID
        String deviceId = channel.attr(DEVICE_ID_KEY).get();
        if (deviceId != null) {
            return removeChannel(deviceId);
        }
        
        // 如果Channel上没有绑定设备ID，则遍历查找该Channel
        for (String key : deviceChannelMap.keySet()) {
            if (deviceChannelMap.get(key) == channel || deviceChannelMap.get(key).equals(channel)) {
                deviceChannelMap.remove(key);
                logger.info("设备连接已移除: {}", key);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备的连接通道
     *
     * @param deviceId 设备ID
     * @return 连接通道
     */
    public Channel getChannel(String deviceId) {
        return deviceChannelMap.get(deviceId);
    }

    /**
     * 检查设备是否在线
     *
     * @param deviceId 设备ID
     * @return 是否在线
     */
    public boolean isDeviceOnline(String deviceId) {
        Channel channel = deviceChannelMap.get(deviceId);
        return channel != null && channel.isActive();
    }

    /**
     * 获取在线设备数量
     *
     * @return 在线设备数量
     */
    public int getOnlineDeviceCount() {
        return deviceChannelMap.size();
    }
}