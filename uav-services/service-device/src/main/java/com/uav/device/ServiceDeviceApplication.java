package com.uav.device;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 设备服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class ServiceDeviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDeviceApplication.class, args);
    }
}