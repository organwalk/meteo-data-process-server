package com.weather;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.weather")
@MapperScan("com.weather.mapper")
public class MeteoProcessApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeteoProcessApplication.class);
    }
}
