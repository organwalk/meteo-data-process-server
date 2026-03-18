package com.weather.entity.request;

import lombok.Data;

@Data
public class GetStationDateRange {
    int code;
    String token;
    Integer station;

    public GetStationDateRange(int code, String token, Integer station) {
        this.code = code;
        this.token = token;
        this.station = station;
    }
}
