package com.uav.service.config.xxl;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-JOB客户端配置
 * 用于调用XXL-JOB Admin的API接口
 * 
 * @author Roo
 * @date 2026-02-23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "xxl.job.admin")
public class XxlJobClientConfig {

    /**
     * 调度中心地址
     * 例如: http://localhost:8080/xxl-job-admin
     */
    private String addresses;

    /**
     * 访问令牌（可选）
     * 如果调度中心配置了accessToken，这里需要保持一致
     */
    private String accessToken;

    /**
     * 管理员用户名
     * 用于调用Admin API时的认证
     */
    private String username = "admin";

    /**
     * 管理员密码
     * 用于调用Admin API时的认证
     */
    private String password = "123456";

    /**
     * 获取调度中心的基础URL
     * 去除末尾的斜杠
     */
    public String getBaseUrl() {
        if (addresses == null || addresses.isEmpty()) {
            return "";
        }
        String url = addresses.split(",")[0].trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}