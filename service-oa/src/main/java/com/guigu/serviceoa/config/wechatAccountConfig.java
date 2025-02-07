package com.guigu.serviceoa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class wechatAccountConfig {
    private String mpAppId;

    private String mpAppSecret;
}
