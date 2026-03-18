package com.weather.repository.repositoryimpl;

import com.weather.common.constants.RedisKeys;
import com.weather.config.UdpProperties;
import com.weather.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UdpProperties udpProperties;

    @Override
    public String getToken(String username) {
        Object token = redisTemplate.opsForValue().get(tokenKey(username));
        if (token == null) {
            token = redisTemplate.opsForHash().get(RedisKeys.OBTAIN_TOKEN_HASH, username);
        }
        return token == null ? null : token.toString();
    }

    @Override
    public String getDefaultToken() {
        return getToken(udpProperties.getAuthUsername());
    }

    @Override
    public void saveToken(String username, String token) {
        redisTemplate.opsForValue().set(tokenKey(username), token);
        redisTemplate.opsForHash().put(RedisKeys.OBTAIN_TOKEN_HASH, username, token);
    }

    @Override
    public void deleteToken(String username) {
        redisTemplate.delete(tokenKey(username));
        redisTemplate.opsForHash().delete(RedisKeys.OBTAIN_TOKEN_HASH, username);
    }

    @Override
    public void saveDateRange(String station, String date) {
        redisTemplate.opsForSet().add(RedisKeys.STATION_DATE_RANGE_PREFIX + station, date);
    }

    @Override
    public void saveMeteoData(String station, String date, String payload, double score) {
        redisTemplate.opsForZSet().add(RedisKeys.METEO_DATA_PREFIX + station + ":" + date, payload, score);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getMeteoData(String station, String date) {
        return redisTemplate.opsForZSet().rangeWithScores(RedisKeys.METEO_DATA_PREFIX + station + ":" + date, 0, -1);
    }

    private String tokenKey(String username) {
        return RedisKeys.OBTAIN_TOKEN_PREFIX + username;
    }
}
