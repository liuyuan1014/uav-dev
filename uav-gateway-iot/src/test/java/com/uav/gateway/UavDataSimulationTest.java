package com.uav.gateway;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 无人机数据模拟测试类
 * 模拟无人机发送JSON数据，验证完整链路（网关->Kafka->业务服务->Redis/MongoDB）
 */
public class UavDataSimulationTest {
    
    private static final String GATEWAY_HOST = "localhost";
    private static final int GATEWAY_PORT = 8090;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 无人机数据模拟测试 ===");
        System.out.println("请输入要发送的JSON数据，每行一个消息，输入 'quit' 结束:");
        System.out.println("示例JSON格式: {\"deviceId\":\"UAV_001\",\"lat\":39.9042,\"lon\":116.4074,\"altitude\":100.0,\"speed\":10.5,\"battery\":85,\"timestamp\":" + System.currentTimeMillis() + "}");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("请输入JSON数据: ");
            String input = scanner.nextLine().trim();
            
            if ("quit".equalsIgnoreCase(input)) {
                System.out.println("测试结束");
                break;
            }
            
            if (input.isEmpty()) {
                continue;
            }
            
            // 发送消息
            try {
                sendMessage(input);
            } catch (Exception e) {
                System.err.println("发送消息失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            Thread.sleep(1000); // 等待1秒再发送下一条
        }
        
        scanner.close();
    }
    
    /**
     * 发送消息到网关
     */
    private static void sendMessage(String jsonBody) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimulationTestHandler(jsonBody));
                        }
                    });
            
            System.out.println("正在连接到网关服务器: " + GATEWAY_HOST + ":" + GATEWAY_PORT);
            ChannelFuture future = bootstrap.connect(GATEWAY_HOST, GATEWAY_PORT).sync();
            
            System.out.println("连接建立成功，发送JSON数据...");
            sendJsonMessage(future.channel(), jsonBody);
            
            // 等待消息发送完成
            Thread.sleep(500);
            
            // 关闭连接
            future.channel().close().sync();
            
        } finally {
            group.shutdownGracefully();
        }
    }
    
    /**
     * 发送JSON消息
     * 消息格式：魔数(2字节) + 版本(1字节) + 命令(1字节) + 长度(4字节) + 消息体(N字节)
     */
    private static void sendJsonMessage(Channel channel, String jsonBody) {
        System.out.println("准备发送的JSON: " + jsonBody);
        
        // 计算消息体长度
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        int bodyLength = bodyBytes.length;
        
        // 创建完整的消息包
        // 使用命令2（心跳）来触发遥测数据处理
        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeShort((short) 0xACED);  // 魔数
        buffer.writeByte((byte) 1);         // 版本号
        buffer.writeByte((byte) 2);         // 命令：心跳 (2)
        buffer.writeInt(bodyLength);        // 消息体长度
        buffer.writeBytes(bodyBytes);       // 消息体
        
        System.out.println("消息总长度: " + buffer.readableBytes() + " 字节");
        System.out.println("发送消息到网关...");
        
        // 发送消息
        channel.writeAndFlush(buffer).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("消息发送成功");
            } else {
                System.err.println("消息发送失败: " + future.cause().getMessage());
            }
        });
    }
    
    /**
     * 模拟测试处理器
     */
    static class SimulationTestHandler extends ChannelInboundHandlerAdapter {
        private final String jsonMessage;
        
        public SimulationTestHandler(String jsonMessage) {
            this.jsonMessage = jsonMessage;
        }
        
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