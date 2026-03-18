package com.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.gateway.auth")
public class GatewayAuthProperties {
    private boolean enabled = true;
    private List<String> allowOriginPatterns = new ArrayList<>(List.of("*"));
    private List<String> allowMethods = new ArrayList<>(List.of("*"));
    private List<String> allowHeaders = new ArrayList<>(List.of("*"));
}
