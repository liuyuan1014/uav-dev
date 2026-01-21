package com.uav.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class UavServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UavServiceApplication.class, args);
    }
}