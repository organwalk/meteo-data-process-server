package com.weather.service;

import com.weather.common.response.DataResult;
import com.weather.entity.request.LoginRequest;
import com.weather.entity.request.RegisterRequest;
import com.weather.entity.respond.LoginRespond;

public interface UserService {
    LoginRespond authUser(LoginRequest loginRequest);

    DataResult insertUser(RegisterRequest registerRequest);

    DataResult logout(String username);

    String getAccessToken(String username);
}
