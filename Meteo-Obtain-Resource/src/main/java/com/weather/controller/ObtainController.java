package com.weather.controller;

import com.weather.service.ObtainSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/obtain")
@RequiredArgsConstructor
public class ObtainController {
    private final ObtainSyncService obtainSyncService;

    @GetMapping("/token/user")
    public boolean getToken(@RequestParam String name) {
        return obtainSyncService.getToken(name);
    }

    @PostMapping("/token")
    public boolean voidToken(@RequestParam String name) {
        return obtainSyncService.voidToken(name);
    }

    @GetMapping("/meteo/station")
    public boolean getStationCode(@RequestParam String name) {
        return obtainSyncService.getStationCode(name);
    }

    @GetMapping("/meteo/date_range")
    public boolean getDateRange(@RequestParam(name = "name") String name,
                                @RequestParam(name = "station") String station) {
        return obtainSyncService.getDateRange(name, station);
    }

    @GetMapping("/meteo/data")
    public boolean getMeteoData(@RequestParam String name,
                                @RequestParam String station,
                                @RequestParam String start,
                                @RequestParam String end) {
        return obtainSyncService.getMeteoData(name, station, start, end);
    }
}
