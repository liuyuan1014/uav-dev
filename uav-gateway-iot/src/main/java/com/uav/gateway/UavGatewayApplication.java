package com.uav.gateway;

import com.uav.gateway.server.NettyServer;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class UavGatewayApplication implements CommandLineRunner {
    
    @Autowired
    private NettyServer nettyServer;
    
    public static void main(String[] args) {
        SpringApplication.run(UavGatewayApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 在新线程中启动Netty服务器，避免阻塞主线程
        Thread nettyThread = new Thread(() -> {
            try {
                nettyServer.start();
            } catch (InterruptedException e) {
                System.err.println("Netty服务器启动失败: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }, "NettyServer");
        
        nettyThread.setDaemon(false); // 设置为非守护线程，确保主程序不会提前退出
        nettyThread.start();
    }
}