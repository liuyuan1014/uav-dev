package com.uav.gateway.server;

import com.uav.gateway.protocol.UavDecoder;
import com.uav.gateway.handler.UavServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    private static final int PORT = 8090;

    private EventLoopGroup bossGroup; // 用于处理服务器端接收到的连接请求 Boss
    private EventLoopGroup workerGroup; // Worker线程组，处理业务逻辑
    
    @Autowired
    private UavServerHandler uavServerHandler;

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1); // Boss线程组，处理连接请求
        workerGroup = new NioEventLoopGroup(); // Worker线程组，处理IO操作

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置等待队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接活跃
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 添加LengthFieldBasedFrameDecoder来处理粘包/拆包，这是Netty提供的
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                    1024 * 1024, // maxFrameLength，包的最大长度（防止超大包攻击）
                                    4,           // lengthFieldOffset  长度字段的偏移量（前4个字节是头，调过魔数2+版本1+命令1=4，正好是长度字段的位置）
                                    4,           // lengthFieldLength  长度字段本身的长度（int 是4字节）
                                    0,           // lengthAdjustment
                                    0            // initialBytesToStrip
                            ));
                            
                            // 添加自定义解码器，把字节变成对象
                            pipeline.addLast(new UavDecoder());
                            
                            // 添加业务处理器
                            pipeline.addLast(uavServerHandler);
                        }
                    });

            // 绑定端口并启动
            ChannelFuture future = bootstrap.bind(PORT).sync();
            System.out.println("Netty服务器启动成功，监听端口: " + PORT);

            // 等待服务器套接字关闭:主线程你就在这儿等着吧，别结束程序，直到服务器被关闭为止。
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}