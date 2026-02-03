package com.uav.gateway.config;

import org.apache.dubbo.config.ProviderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfig {

    @Bean
    public ProviderConfig providerConfig() {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setSerialization("fastjson2");
        return providerConfig;
    }
    // 通过配置文件设置序列化协议，此处不需要额外的配置类
}