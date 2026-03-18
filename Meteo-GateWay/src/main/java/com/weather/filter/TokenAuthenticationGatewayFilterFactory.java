package com.weather.filter;

import com.weather.common.constants.HeaderNames;
import com.weather.config.GatewayAuthProperties;
import com.weather.userclient.UserClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<TokenAuthenticationGatewayFilterFactory.Config> {

    private final UserClient userClient;
    private final GatewayAuthProperties properties;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!properties.isEnabled()) {
                return chain.filter(exchange);
            }

            String name = exchange.getRequest().getHeaders().getFirst(HeaderNames.USER_NAME);
            String accessToken = exchange.getRequest().getHeaders().getFirst(HeaderNames.ACCESS_TOKEN);
            if (isBlank(name) || isBlank(accessToken)) {
                return unauthorized(exchange);
            }

            return userClient.getAccessToken(name)
                    .defaultIfEmpty("")
                    .flatMap(savedToken -> accessToken.equals(savedToken)
                            ? chain.filter(exchange)
                            : unauthorized(exchange))
                    .onErrorResume(exception -> unauthorized(exchange));
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Data
    public static class Config {
    }
}
