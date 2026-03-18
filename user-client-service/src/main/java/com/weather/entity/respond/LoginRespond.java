package com.weather.entity.respond;

import lombok.Builder;
import lombok.Data;

/**
 * 定义登录响应实体
 * by organwalk 2023-04-02
 */
@Data
@Builder
public class LoginRespond {
    private Integer success;
    private Auth auth;

    @Data
    @Builder
    static class Auth {
        private String name;
        private String access_token;
    }

    /**
     * 表示登录信息正确的情况
     * @param name 用户名
     * @param accessToken 认证令牌
     * @return 表示登录信息正确的LoginRespond对象
     *
     * by organwalk 2023-04-02
     */
    public static LoginRespond ok (String name,String accessToken){
        Auth auth = Auth.builder()
                .name(name)
                .access_token(accessToken)
                .build();
        return LoginRespond.builder()
                .success(1)
                .auth(auth)
                .build();
    }

    /**
     * 表示登录信息无法找到
     * @return 表示登录信息无法找到的LoginRespond对象
     *
     * by organwalk 2023-04-02
     */
    public static LoginRespond not_found(){
        return LoginRespond.builder()
                .success(0)
                .build();
    }
}
