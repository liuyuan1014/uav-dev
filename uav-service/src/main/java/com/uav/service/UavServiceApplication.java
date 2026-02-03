package com.uav.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class},
        scanBasePackages = {
            "com.uav.service",
            "com.uav.controller",
            "com.uav.common",
            "com.uav.model"
        })
@EnableDubbo
@EnableKafka
public class UavServiceApplication {
    public static void main(String[] args) {
        // 设置JVM参数以解决Java 17模块系统限制
        System.setProperty("jdk.serialSetAccessOnly", "false");
        System.setProperty("hessian.allowNonSerializable", "true");
        
        SpringApplication.run(UavServiceApplication.class, args);
    }
}