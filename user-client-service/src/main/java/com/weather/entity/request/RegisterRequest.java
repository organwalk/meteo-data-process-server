package com.weather.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 定义注册请求实体
 * by organwalk 2023-04-02
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "username不能为空")
    private String username;
    @NotBlank(message = "password不能为空")
    private String password;
}
