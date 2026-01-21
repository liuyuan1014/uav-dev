package com.uav.service;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedissonAutoConfiguration.class})
@EnableDubbo
public class UavServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UavServiceApplication.class, args);
    }
}