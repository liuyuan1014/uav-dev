package com.uav.gateway;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

/**
 * 完整集成测试类 - 验证从无人机到业务服务的完整链路
 * 测试步骤：
 * 1. 确认Netty是否收到消息
 * 2. 确认Netty收到的消息内容
 * 3. 确认是否向Kafka发送消息
 * 4. 确认Kafka是否收到消息
 */
public class IntegrationTest {
    
    private static final String GATEWAY_HOST = "localhost";
    private static final int GATEWAY_PORT = 8090;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 开始无人机数据链路集成测试 ===");
        
        // 测试登录消息
        testLoginMessage();
        
        Thread.sleep(2000); // 等待处理
        
        // 测试心跳消息
        testHeartbeatMessage();
        
        System.out.println("=== 测试完成，请检查各服务的日志输出 ===");
    }
    
    /**
     * 测试登录消息
     */
    private static void testLoginMessage() throws InterruptedException {
        System.out.println("\n--- 测试登录消息 ---");
        
        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageTestHandler("LOGIN"));
                        }
                    });
            
            System.out.println("正在连接到网关服务器: " + GATEWAY_HOST + ":" + GATEWAY_PORT);
            ChannelFuture future = bootstrap.connect(GATEWAY_HOST, GATEWAY_PORT).sync();
            
            // 发送登录消息
            System.out.println("连接建立成功，开始发送登录消息...");
            sendLoginMessage(future.channel());
            
            // 等待消息发送完成
            Thread.sleep(1000);
            
            // 关闭连接
            future.channel().close().sync();
            
        } finally {
            group.shutdownGracefully();
        }
    }
    
    /**
     * 测试心跳消息
     */
    private static void testHeartbeatMessage() throws InterruptedException {
        System.out.println("\n--- 测试心跳消息 ---");
        
        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageTestHandler("HEARTBEAT"));
                        }
                    });
            
            System.out.println("正在连接到网关服务器: " + GATEWAY_HOST + ":" + GATEWAY_PORT);
            ChannelFuture future = bootstrap.connect(GATEWAY_HOST, GATEWAY_PORT).sync();
            
            // 发送心跳消息
            System.out.println("连接建立成功，开始发送心跳消息...");
            sendHeartbeatMessage(future.channel());
            
            // 等待消息发送完成
            Thread.sleep(1000);
            
            // 关闭连接
            future.channel().close().sync();
            
        } finally {
            group.shutdownGracefully();
        }
    }
    
    /**
     * 发送登录消息
     */
    private static void sendLoginMessage(Channel channel) {
        String loginJson = "{\"deviceId\":\"TEST_UAV_001\",\"loginTime\":" + System.currentTimeMillis() + "}";
        
        System.out.println("准备发送的登录消息JSON: " + loginJson);
        
        sendMessage(channel, (byte) 1, loginJson); // 命令1：登录
    }
    
    /**
     * 发送心跳消息
     */
    private static void sendHeartbeatMessage(Channel channel) {
        String heartbeatJson = "{\"deviceId\":\"TEST_UAV_001\",\"lat\":39.9042,\"lon\":116.4074,\"altitude\":100.0,\"speed\":10.5,\"battery\":85,\"timestamp\":" + System.currentTimeMillis() + "}";
        
        System.out.println("准备发送的心跳消息JSON: " + heartbeatJson);
        
        sendMessage(channel, (byte) 2, heartbeatJson); // 命令2：心跳
    }
    
    /**
     * 发送通用消息
     */
    private static void sendMessage(Channel channel, byte command, String jsonBody) {
        // 计算消息体长度
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        int bodyLength = bodyBytes.length;
        
        // 创建完整的消息包
        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeShort((short) 0xACED);  // 魔数
        buffer.writeByte((byte) 1);         // 版本号
        buffer.writeByte(command);          // 命令
        buffer.writeInt(bodyLength);        // 消息体长度
        buffer.writeBytes(bodyBytes);       // 消息体
        
        System.out.println("消息总长度: " + buffer.readableBytes() + " 字节");
        System.out.println("发送消息到网关...");
        
        // 发送消息
        channel.writeAndFlush(buffer);
    }
    
    /**
     * 消息测试处理器
     */
    static class MessageTestHandler extends ChannelInboundHandlerAdapter {
        private final String messageType;
        
        public MessageTestHandler(String messageType) {
            this.messageType = messageType;
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(messageType + " - 客户端已连接到网关服务器");
            super.channelActive(ctx);
        }
        
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(messageType + " - 收到服务器响应: " + msg);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println(messageType + " - 客户端发生异常: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
    }
}