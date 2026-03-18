package com.weather.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {
    private String prefix = "meteo:cache";
    private Duration meteorologyTtl = Duration.ofMinutes(10);
}
