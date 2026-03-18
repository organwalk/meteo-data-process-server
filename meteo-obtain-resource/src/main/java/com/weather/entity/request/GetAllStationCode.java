package com.weather.entity.request;

import lombok.Data;

@Data
public class GetAllStationCode {
    int code;
    String token;

    public GetAllStationCode(int code, String token) {
        this.code = code;
        this.token = token;
    }
}
