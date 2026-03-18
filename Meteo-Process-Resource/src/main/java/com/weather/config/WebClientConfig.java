package com.weather.config;

import com.weather.client.ObtainClient;
import com.weather.common.properties.ServiceClientProperties;
import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder processWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ObtainClient obtainClient(@LoadBalanced WebClient.Builder webClientBuilder,
                                     ServiceClientProperties properties) {
        ServiceClientProperties.Client clientProperties = properties.getObtain();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) nonNull(clientProperties.getConnectTimeout()).toMillis())
                .responseTimeout(nonNull(clientProperties.getReadTimeout()));
        WebClient client = webClientBuilder
                .baseUrl(clientProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ObtainClient.class);
    }

    private Duration nonNull(Duration duration) {
        return duration == null ? Duration.ofSeconds(3) : duration;
    }
}
