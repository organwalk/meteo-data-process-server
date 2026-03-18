package com.weather.entity.request;

import lombok.Data;

@Data
public class VoidToken {
    int code;
    String token;

    public VoidToken(int code, String token) {
        this.code = code;
        this.token = token;
    }
}
