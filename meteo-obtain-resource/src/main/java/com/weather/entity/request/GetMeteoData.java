package com.weather.entity.request;

import lombok.Data;

@Data
public class GetMeteoData {
    int code;
    String token;
    Integer station;
    String start;
    String end;

    public GetMeteoData(int code, String token,Integer station, String start, String end) {
        this.code = code;
        this.token = token;
        this.station = station;
        this.start = start;
        this.end = end;
    }
}
