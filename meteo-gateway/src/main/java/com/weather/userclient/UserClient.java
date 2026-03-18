package com.weather.userclient;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("/user")
public interface UserClient {
    @GetExchange("/token")
    Mono<String> getAccessToken(@RequestParam String username);
}
