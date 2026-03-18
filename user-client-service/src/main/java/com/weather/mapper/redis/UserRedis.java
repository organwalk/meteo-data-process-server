package com.weather.mapper.redis;
import org.springframework.stereotype.Repository;

/**
 * 定义用户信息的Redis接口
 * by organwalk 2023-04-02
 */
@Repository
public interface UserRedis {
    void saveToken(String username,String token);
    void voidAccessToken(String username);
    Boolean checkUserStatus(String username);
    String getAccessToken(String username);
}
