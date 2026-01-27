package com.uav.gateway;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 批量无人机数据测试类
 * 用于发送批量测试数据，验证链路完整性和数据存储
 */
public class BatchUavDataTest {
    
    private static final String GATEWAY_HOST = "localhost";
    private static final int GATEWAY_PORT = 8090;
    private static final Random RANDOM = new Random();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 批量无人机数据测试 ===");
        
        // 创建测试数据
        List<String> testData = generateTestData(5); // 生成5条测试数据
        
        System.out.println("准备发送 " + testData.size() + " 条测试数据");
        
        for (int i = 0; i < testData.size(); i++) {
            String jsonData = testData.get(i);
            System.out.println("第 " + (i + 1) + " 条数据: " + jsonData);
            
            try {
                sendMessage(jsonData);
                Thread.sleep(1000); // 每条消息间隔1秒
            } catch (Exception e) {
                System.err.println("发送第 " + (i + 1) + " 条消息失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=== 所有数据发送完成，请检查Redis和MongoDB中的数据 ===");
    }
    
    /**
     * 生成测试数据
     */
    private static List<String> generateTestData(int count) {
        List<String> testData = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String jsonData = String.format(
                "{\"deviceId\":\"TEST_UAV_%03d\",\"lat\":%.4f,\"lon\":%.4f,\"altitude\":%.1f,\"speed\":%.1f,\"battery\":%d,\"timestamp\":%d}",
                i + 1,
                39.0 + RANDOM.nextDouble() * 0.9, // 纬度
                116.0 + RANDOM.nextDouble() * 0.5, // 经度
                50.0 + RANDOM.nextDouble() * 100, // 高度
                5.0 + RANDOM.nextDouble() * 20, // 速度
                50 + RANDOM.nextInt(50), // 电量
                System.currentTimeMillis()
            );
            testData.add(jsonData);
        }
        
        return testData;
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
                            ch.pipeline().addLast(new BatchTestHandler(jsonBody));
                        }
                    });
            
            ChannelFuture future = bootstrap.connect(GATEWAY_HOST, GATEWAY_PORT).sync();
            
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
        System.out.println("发送JSON: " + jsonBody);
        
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
        
        System.out.println("消息长度: " + buffer.readableBytes() + " 字节");
        
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
     * 批量测试处理器
     */
    static class BatchTestHandler extends ChannelInboundHandlerAdapter {
        private final String jsonMessage;
        
        public BatchTestHandler(String jsonMessage) {
            this.jsonMessage = jsonMessage;
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("批量测试 - 客户端已连接");
            super.channelActive(ctx);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("批量测试 - 客户端异常: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
    }
}