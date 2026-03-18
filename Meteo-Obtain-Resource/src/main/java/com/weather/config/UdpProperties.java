package com.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.udp")
public class UdpProperties {
    private String remoteHost = "127.0.0.1";
    private int remotePort = 9092;
    private String authUsername = "root";
    private String authPassword = "change_me";
    private Duration awaitTimeout = Duration.ofSeconds(60);
}
