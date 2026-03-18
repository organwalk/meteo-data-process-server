package com.weather.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.clients")
public class ServiceClientProperties {
    private Client user = new Client("http://user-client-service", Duration.ofSeconds(3), Duration.ofSeconds(10));
    private Client obtain = new Client("http://meteo-obtain-resource", Duration.ofSeconds(3), Duration.ofSeconds(60));
    private Client anaPredict = new Client("http://localhost:9594", Duration.ofSeconds(3), Duration.ofSeconds(10));

    @Data
    public static class Client {
        private String baseUrl;
        private Duration connectTimeout;
        private Duration readTimeout;

        public Client() {
        }

        public Client(String baseUrl, Duration connectTimeout, Duration readTimeout) {
            this.baseUrl = baseUrl;
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
        }
    }
}
