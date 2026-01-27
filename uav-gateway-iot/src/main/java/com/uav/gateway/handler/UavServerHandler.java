package com.uav.gateway.handler;

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
                System.out.println("设备登录请求: " + packet.getBody());//目前只是打印日志，后续优化：鉴权
                break;
            case 2: // HEARTBEAT
                System.out.println("收到心跳");
                // 调用Kafka生产者发送遥测数据
                try {
                    System.out.println("开始发送遥测数据到Kafka...");
                    uavTelemetryProducer.sendTelemetry(packet);
                } catch (Exception e) {
                    System.err.println("发送Kafka消息失败: " + e.getMessage());
                    e.printStackTrace();
                }
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
        ctx.close();
    }

    // 当无人机断网、掉线、或者主动断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("设备断开连接: " + ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}