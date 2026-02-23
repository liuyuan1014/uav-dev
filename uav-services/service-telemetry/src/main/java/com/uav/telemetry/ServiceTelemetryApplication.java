package com.uav.telemetry;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 遥测服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@EnableKafka
@EnableMongoRepositories(basePackages = "com.uav.telemetry.repository")
public class ServiceTelemetryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTelemetryApplication.class, args);
    }
}