package com.example.house.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "qiniu")
@Data
public class QiniuProperties {
    private String domain;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
