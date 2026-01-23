package com.uav.gateway.handler;

import com.uav.api.dto.UavHeartbeatDTO;
import com.uav.api.service.UavConnectService;
import com.uav.gateway.protocol.UavPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
//继承SimpleChannelInboundHandler，会自动将UavPacket里的资源释放掉，因为ByteBuf是占用堆外内存的，所以需要手动释放掉
public class UavServerHandler extends SimpleChannelInboundHandler<UavPacket> {

    @DubboReference
    private UavConnectService uavConnectService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新设备连接: " + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    // Netty 是基于事件的，当一个数据包到达时，会调用这个方法，这里不能写死循环或者sleep，会堵塞住整个线程，导致其他无人机的消息处理不过来
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UavPacket packet) throws Exception {
        byte command = packet.getCommand();

        switch (command) {
            case 1: // LOGIN
                System.out.println("设备登录请求: " + packet.getBody());//目前只是打印日志，后续优化：鉴权
                break;
            case 2: // HEARTBEAT
                System.out.println("收到心跳");
                // 从body中解析设备ID
                String deviceId = parseDeviceIdFromJson(packet.getBody());
                
                // 调用Dubbo服务---跨进程通信
                // 网关只负责“收发信”，复杂的业务逻辑（存数据库、分析数据）交给Dubbo服务，这样做到了架构解耦
                try {
                    UavHeartbeatDTO heartbeatDTO = new UavHeartbeatDTO();
                    if (deviceId != null && !deviceId.isEmpty()) {
                        heartbeatDTO.setDeviceId(deviceId);
                    } else {
                        heartbeatDTO.setDeviceId("UNKNOWN_DEVICE");
                    }
                    heartbeatDTO.setTimestamp(System.currentTimeMillis());
                    
                    Boolean result = uavConnectService.connect(heartbeatDTO);
                    System.out.println("调用Dubbo服务结果: " + result);
                } catch (Exception e) {
                    System.err.println("调用Dubbo服务失败: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("未知命令: " + command);
                break;
        }
    }

    // 解析JSON中的deviceId字段
    private String parseDeviceIdFromJson(String jsonBody) {
        // 简单解析JSON中的deviceId字段
        try {
            // 查找"deviceId":"
            int startIndex = jsonBody.indexOf("\"deviceId\":\"");
            if (startIndex != -1) {
                startIndex += 12; // 跳过 "deviceId":" 
                int endIndex = jsonBody.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    return jsonBody.substring(startIndex, endIndex);
                }
            }
        } catch (Exception e) {
            System.err.println("解析JSON失败: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("处理客户端消息时发生异常: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    // 当无人机断网、掉线、或者主动断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("设备断开连接: " + ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}