package com.zkteco.attpush.entity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jobs")
public class Jobs {
    private String cron;
}
