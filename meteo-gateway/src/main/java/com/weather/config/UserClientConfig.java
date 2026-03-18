package com.weather.config;

import com.weather.common.properties.ServiceClientProperties;
import com.weather.userclient.UserClient;
import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class UserClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder gatewayWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public UserClient userClient(@LoadBalanced WebClient.Builder webClientBuilder,
                                 ServiceClientProperties properties) {
        ServiceClientProperties.Client clientProperties = properties.getUser();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMillis(clientProperties.getConnectTimeout()))
                .responseTimeout(nonNullDuration(clientProperties.getReadTimeout()));

        WebClient client = webClientBuilder
                .baseUrl(clientProperties.getBaseUrl())
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(client))
                .build();
        return factory.createClient(UserClient.class);
    }

    private int timeoutMillis(Duration duration) {
        return (int) nonNullDuration(duration).toMillis();
    }

    private Duration nonNullDuration(Duration duration) {
        return duration == null ? Duration.ofSeconds(3) : duration;
    }
}
