package com.uav.pilot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 飞手服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.uav.pilot.mapper")
public class ServicePilotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePilotApplication.class, args);
    }
}