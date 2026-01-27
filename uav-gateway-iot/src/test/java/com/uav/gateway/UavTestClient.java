package com.uav.gateway;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * 无人机测试客户端 - 用于验证完整数据链路
 * 模拟无人机发送心跳消息到网关
 */
public class UavTestClient {
    
    private static final String HOST = "localhost";
    private static final int PORT = 8090;
    
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TestClientHandler());
                        }
                    });
            
            System.out.println("正在连接到网关服务器: " + HOST + ":" + PORT);
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            
            // 发送心跳消息
            System.out.println("连接建立成功，开始发送心跳消息...");
            sendHeartbeatMessage(future.channel());
            
            // 等待连接关闭
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
    
    /**
     * 发送心跳消息
     * 心跳消息格式：魔数(2字节) + 版本(1字节) + 命令(1字节) + 长度(4字节) + 消息体(N字节)
     */
    private static void sendHeartbeatMessage(Channel channel) {
        // 创建心跳消息的JSON体
        String heartbeatJson = "{\"deviceId\":\"TEST_UAV_001\",\"lat\":39.9042,\"lon\":116.4074,\"altitude\":100.0,\"speed\":10.5,\"battery\":85,\"timestamp\":" + System.currentTimeMillis() + "}";
        
        System.out.println("准备发送的心跳消息JSON: " + heartbeatJson);
        
        // 计算消息体长度
        byte[] bodyBytes = heartbeatJson.getBytes(StandardCharsets.UTF_8);
        int bodyLength = bodyBytes.length;
        
        // 创建完整的消息包
        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeShort((short) 0xACED);  // 魔数
        buffer.writeByte((byte) 1);         // 版本号
        buffer.writeByte((byte) 2);         // 命令：心跳 (2)
        buffer.writeInt(bodyLength);        // 消息体长度
        buffer.writeBytes(bodyBytes);       // 消息体
        
        System.out.println("消息总长度: " + buffer.readableBytes() + " 字节");
        System.out.println("发送消息到网关...");
        
        // 发送消息
        channel.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * 测试客户端处理器
     */
    static class TestClientHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("客户端已连接到网关服务器");
            super.channelActive(ctx);
        }
        
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("收到服务器响应: " + msg);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("客户端发生异常: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
    }
}