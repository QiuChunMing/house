package com.example.house.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "baidu")
public class BaiduMapProperties {
    private String accessKey;
}
