package com.uav.dispatch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * XXL-JOB客户端配置
 * 用于调用XXL-JOB Admin的API接口
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Component
@ConfigurationProperties(prefix = "xxl.job.admin")
public class XxlJobClientConfig {
    
    /**
     * XXL-JOB调度中心地址
     */
    private String addresses;
    
    /**
     * 登录用户名
     */
    private String username = "admin";
    
    /**
     * 登录密码
     */
    private String password = "123456";
    
    /**
     * 执行器组ID
     */
    private Integer executorGroupId = 2;
    
    /**
     * 获取基础URL（第一个地址）
     */
    public String getBaseUrl() {
        if (addresses != null && addresses.contains(",")) {
            return addresses.split(",")[0];
        }
        return addresses;
    }
}