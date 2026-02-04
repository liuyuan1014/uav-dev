package com.uav.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {
    "com.uav.service",
    "com.uav.controller",
    "com.uav.common",
    "com.uav.model"
})

@EnableDubbo
@EnableKafka
@MapperScan("com.uav.service.mapper")  // 添加Mapper扫描
public class UavServiceApplication {
    public static void main(String[] args) {
        // 设置JVM参数以解决Java 17模块系统限制
        System.setProperty("jdk.serialSetAccessOnly", "false");
        System.setProperty("hessian.allowNonSerializable", "true");
        
        SpringApplication.run(UavServiceApplication.class, args);
    }
}