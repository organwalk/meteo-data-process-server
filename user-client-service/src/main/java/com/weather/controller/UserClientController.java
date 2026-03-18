package com.weather.controller;

import com.weather.common.response.DataResult;
import com.weather.entity.request.LoginRequest;
import com.weather.entity.request.RegisterRequest;
import com.weather.entity.respond.LoginRespond;
import com.weather.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Validated
public class UserClientController {
    private final UserService userService;

    @PostMapping("/login")
    public LoginRespond login(@Validated @RequestBody LoginRequest loginRequest) {
        return userService.authUser(loginRequest);
    }

    @PostMapping("/register")
    public DataResult register(@Validated @RequestBody RegisterRequest registerRequest) {
        return userService.insertUser(registerRequest);
    }

    @PostMapping("/logout")
    public DataResult logout(@RequestParam @NotBlank(message = "username涓嶈兘涓虹┖") String username) {
        return userService.logout(username);
    }

    @GetMapping("/token")
    public String getAccessToken(@RequestParam String username) {
        return userService.getAccessToken(username);
    }
}
