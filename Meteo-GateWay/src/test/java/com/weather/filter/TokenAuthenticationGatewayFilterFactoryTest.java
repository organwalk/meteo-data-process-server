package com.weather.filter;

import com.weather.common.constants.HeaderNames;
import com.weather.config.GatewayAuthProperties;
import com.weather.userclient.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenAuthenticationGatewayFilterFactoryTest {
    @Test
    void shouldAllowRequestWhenTokenMatches() {
        UserClient userClient = username -> Mono.just("access");
        GatewayAuthProperties properties = new GatewayAuthProperties();
        TokenAuthenticationGatewayFilterFactory factory = new TokenAuthenticationGatewayFilterFactory(userClient, properties);

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/qx/test")
                .header(HeaderNames.USER_NAME, "tester")
                .header(HeaderNames.ACCESS_TOKEN, "access")
                .build());
        AtomicBoolean invoked = new AtomicBoolean(false);
        GatewayFilterChain chain = serverWebExchange -> {
            invoked.set(true);
            return Mono.empty();
        };

        factory.apply(new TokenAuthenticationGatewayFilterFactory.Config()).filter(exchange, chain).block();

        assertEquals(true, invoked.get());
    }

    @Test
    void shouldRejectRequestWhenTokenDoesNotMatch() {
        UserClient userClient = username -> Mono.just("access");
        GatewayAuthProperties properties = new GatewayAuthProperties();
        TokenAuthenticationGatewayFilterFactory factory = new TokenAuthenticationGatewayFilterFactory(userClient, properties);

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/qx/test")
                .header(HeaderNames.USER_NAME, "tester")
                .header(HeaderNames.ACCESS_TOKEN, "invalid")
                .build());

        factory.apply(new TokenAuthenticationGatewayFilterFactory.Config())
                .filter(exchange, serverWebExchange -> Mono.empty())
                .block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }
}
