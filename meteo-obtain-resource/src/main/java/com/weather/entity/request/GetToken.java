package com.weather.entity.request;

import lombok.Data;

@Data
public class GetToken {
    int code;
    String name;
    String password;

    public GetToken(int code, String name, String password) {
        this.code = code;
        this.name = name;
        this.password = password;
    }
}
