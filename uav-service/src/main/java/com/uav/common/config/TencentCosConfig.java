package com.uav.common.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tencent.cos")
public class TencentCosConfig {

    /**
     * 腾讯云SecretId
     */
    private String secretId;

    /**
     * 腾讯云SecretKey
     */
    private String secretKey;

    /**
     * 地域
     */
    private String region;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 访问域名
     */
    private String baseUrl;

    /**
     * 签名URL过期时间（秒）
     */
    private Long expireTime;

    /**
     * 创建COS客户端Bean
     */
    @Bean
    public COSClient cosClient() {
        // 1. 初始化用户身份信息（secretId, secretKey）
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        
        // 2. 设置bucket的地域
        Region regionObj = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionObj);
        
        // 3. 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}