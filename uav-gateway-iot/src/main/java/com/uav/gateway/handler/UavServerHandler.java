package com.uav.gateway.handler;

import com.uav.gateway.config.NettyConnectManageService;
import com.uav.gateway.config.SpringContextUtil;
import com.uav.gateway.protocol.UavPacket;
import com.uav.gateway.producer.UavTelemetryProducer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import org.springframework.stereotype.Service;

// @Sharable注解表示这个类是共享的，多个线程可以共享同一个实例
@Service
@Sharable
//继承SimpleChannelInboundHandler，会自动将UavPacket里的资源释放掉，因为ByteBuf是占用堆外内存的，所以需要手动释放掉
public class UavServerHandler extends SimpleChannelInboundHandler<UavPacket> {

    private final UavTelemetryProducer uavTelemetryProducer;

    public UavServerHandler(UavTelemetryProducer uavTelemetryProducer) {
        this.uavTelemetryProducer = uavTelemetryProducer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新设备连接: " + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    // Netty 是基于事件的，当一个数据包到达时，会调用这个方法，这里不能写死循环或者sleep，会堵塞住整个线程，导致其他无人机的消息处理不过来
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UavPacket packet) throws Exception {
        // 添加详细日志来调试消息接收
        System.out.println("=== 收到消息 ===");
        System.out.println("远程地址: " + ctx.channel().remoteAddress());
        System.out.println("命令类型: " + packet.getCommand());
        System.out.println("消息体: " + packet.getBody());
        System.out.println("消息长度: " + (packet.getBody() != null ? packet.getBody().length() : 0));
        
        byte command = packet.getCommand();

        switch (command) {
            case 1: // LOGIN
                System.out.println("设备登录请求: " + packet.getBody());
                
                // 解析设备ID并注册连接
                String loginDeviceId = parseDeviceIdFromLogin(packet.getBody());
                if (loginDeviceId != null && !loginDeviceId.isEmpty()) {
                    // 获取连接管理服务并注册设备连接
                    NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
                    connectManageService.addChannel(loginDeviceId, ctx.channel());
                }
                break;
            case 2: // HEARTBEAT
                System.out.println("收到心跳");
                
                // 从心跳数据中解析设备ID并注册连接（如果尚未注册）
                String heartbeatDeviceId = parseDeviceIdFromHeartbeat(packet.getBody());
                if (heartbeatDeviceId != null && !heartbeatDeviceId.isEmpty()) {
                    // 获取连接管理服务并注册设备连接
                    NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
                    if (connectManageService.getChannel(heartbeatDeviceId) == null) {
                        connectManageService.addChannel(heartbeatDeviceId, ctx.channel());
                    }
                }
                
                // 调用Kafka生产者发送遥测数据
                try {
                    System.out.println("开始发送遥测数据到Kafka...");
                    uavTelemetryProducer.sendTelemetry(packet);
                } catch (Exception e) {
                    System.err.println("发送Kafka消息失败: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            case 3: // COMMAND RESPONSE (来自无人机对指令的响应)
                System.out.println("收到指令响应");
                // 这里可以处理无人机对指令的响应
                break;
            default:
                System.out.println("未知命令: " + command);
                break;
        }
        System.out.println("=== 消息处理完成 ===");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("处理客户端消息时发生异常: " + cause.getMessage());
        cause.printStackTrace();
        
        // 异常时移除连接
        NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
        connectManageService.removeChannel(ctx.channel());
        
        System.out.println("设备断开连接（异常）: " + ctx.channel().remoteAddress());
        ctx.close();
    }

    // 当无人机断网、掉线、或者主动断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("设备断开连接: " + ctx.channel().remoteAddress());
        
        // 从Channel上获取设备ID
        String deviceId = ctx.channel().attr(NettyConnectManageService.DEVICE_ID_KEY).get();
        
        // 移除连接
        NettyConnectManageService connectManageService = SpringContextUtil.getBean(NettyConnectManageService.class);
        connectManageService.removeChannel(ctx.channel());
        
        if (deviceId != null) {
            System.out.println("设备断开连接: " + deviceId);
        }
        
        super.channelInactive(ctx);
    }
    
    /**
     * 从登录数据中解析设备ID
     * 
     * @param loginData 登录数据
     * @return 设备ID
     */
    private String parseDeviceIdFromLogin(String loginData) {
        // 如果登录数据是JSON格式，尝试从中解析设备ID
        if (loginData != null && loginData.trim().startsWith("{")) {
            return extractDeviceIdFromJson(loginData.trim());
        }
        // 否则直接返回登录数据作为设备ID（假设是纯文本格式）
        return loginData != null ? loginData.trim() : null;
    }
    
    /**
     * 从心跳数据中解析设备ID
     * 
     * @param heartbeatData 心跳数据
     * @return 设备ID
     */
    private String parseDeviceIdFromHeartbeat(String heartbeatData) {
        // 心跳数据通常是JSON格式，从中提取设备ID
        return extractDeviceIdFromJson(heartbeatData);
    }
    
    /**
     * 从JSON字符串中提取deviceId字段的值
     * 
     * @param json JSON字符串
     * @return 设备ID
     */
    private String extractDeviceIdFromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 简单的字符串解析方式提取deviceId
            int start = json.indexOf("\"deviceId\"");
            if (start == -1) {
                // 尝试小写形式
                start = json.indexOf("\"deviceid\"");
                if (start == -1) {
                    return null;
                }
            }
            
            int colonIndex = json.indexOf(":", start);
            if (colonIndex == -1) {
                return null;
            }
            
            int quoteStart = json.indexOf("\"", colonIndex);
            if (quoteStart == -1) {
                return null; // 没有找到引号，可能不是字符串值
            }
            
            int quoteEnd = json.indexOf("\"", quoteStart + 1);
            if (quoteEnd == -1) {
                return null;
            }
            
            return json.substring(quoteStart + 1, quoteEnd);
        } catch (Exception e) {
            System.err.println("解析JSON中的设备ID失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}