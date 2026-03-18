package com.weather.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/obtain")
public interface ObtainClient {
    @GetExchange("/token/user")
    boolean getToken(@RequestParam String name);

    @GetExchange("/meteo/station")
    boolean getStationCode(@RequestParam String name);

    @GetExchange("/meteo/date_range")
    boolean getDateRange(@RequestParam(name = "name") String name,
                         @RequestParam(name = "station") String station);

    @GetExchange("/meteo/data")
    boolean getMeteoData(@RequestParam String name,
                         @RequestParam String station,
                         @RequestParam String start,
                         @RequestParam String end);

    @PostExchange("/token")
    boolean voidToken(@RequestParam String name);
}
