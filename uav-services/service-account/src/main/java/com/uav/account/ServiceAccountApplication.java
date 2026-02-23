package com.uav.account;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 账户服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@MapperScan("com.uav.account.mapper")
public class ServiceAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAccountApplication.class, args);
    }
}