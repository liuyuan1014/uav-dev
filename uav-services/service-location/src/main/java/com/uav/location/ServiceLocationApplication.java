package com.uav.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 位置服务启动类
 * 
 * @author Roo
 * @date 2026-02-23
 */
@SpringBootApplication(scanBasePackages = {
    "com.uav.location",      // 扫描本服务包
    "com.uav.common"         // 扫描公共模块
})
@EnableDiscoveryClient
public class ServiceLocationApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceLocationApplication.class, args);
        System.out.println("========================================");
        System.out.println("位置服务启动成功！");
        System.out.println("========================================");
    }
}