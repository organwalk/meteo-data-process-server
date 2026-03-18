package com.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.security")
public class UserSecurityProperties {
    private Duration tokenTtl = Duration.ZERO;
    private int passwordStrength = 10;
    private int tokenBytes = 32;
}
