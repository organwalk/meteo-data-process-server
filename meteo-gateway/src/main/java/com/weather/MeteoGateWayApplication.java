package com.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.weather")
@EnableDiscoveryClient
public class MeteoGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeteoGateWayApplication.class);
    }
}
