package com.weather.mapper.redis;

import com.weather.common.constants.RedisKeys;
import com.weather.config.UserSecurityProperties;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@AllArgsConstructor
public class UserRedisImpl implements UserRedis {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserSecurityProperties properties;

    @Override
    public void saveToken(String username, String token) {
        Duration tokenTtl = properties.getTokenTtl();
        String tokenKey = tokenKey(username);
        if (tokenTtl != null && !tokenTtl.isZero() && !tokenTtl.isNegative()) {
            redisTemplate.opsForValue().set(tokenKey, token, tokenTtl);
        } else {
            redisTemplate.opsForValue().set(tokenKey, token);
        }
        redisTemplate.opsForHash().put(RedisKeys.USER_TOKEN_HASH, username, token);
    }

    @Override
    public void voidAccessToken(String username) {
        redisTemplate.delete(tokenKey(username));
        redisTemplate.opsForHash().delete(RedisKeys.USER_TOKEN_HASH, username);
    }

    @Override
    public Boolean checkUserStatus(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey(username)))
                || Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(RedisKeys.USER_TOKEN_HASH, username));
    }

    @Override
    public String getAccessToken(String username) {
        Object accessToken = redisTemplate.opsForValue().get(tokenKey(username));
        if (accessToken == null) {
            accessToken = redisTemplate.opsForHash().get(RedisKeys.USER_TOKEN_HASH, username);
        }
        return accessToken == null ? null : accessToken.toString();
    }

    private String tokenKey(String username) {
        return RedisKeys.USER_TOKEN_PREFIX + username;
    }
}
