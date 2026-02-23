package com.uav.mission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 任务服务启动类
 * 负责任务发布、接单、执行、完成、取消等核心业务
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.uav.mission.mapper")
public class ServiceMissionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceMissionApplication.class, args);
    }
}