package com.weather.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 定义登录请求实体
 * by organwalk 2023-04-02
 */
@Data
public class LoginRequest {
    @NotBlank(message = "username不能为空")
    private String username;
    @NotBlank(message = "password不能为空")
    private String password;
}
