package com.uav.dispatch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 调度服务启动类
 * 
 * @author Roo
 * @date 2026-02-23
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.uav.dispatch.mapper")
public class ServiceDispatchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceDispatchApplication.class, args);
    }
}