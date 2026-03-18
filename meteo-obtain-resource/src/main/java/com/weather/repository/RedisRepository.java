package com.weather.repository;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface RedisRepository {
    String getToken(String username);

    String getDefaultToken();

    void saveToken(String username, String token);

    void deleteToken(String username);

    void saveDateRange(String station, String date);

    void saveMeteoData(String station, String date, String payload, double score);

    Set<ZSetOperations.TypedTuple<Object>> getMeteoData(String station, String date);
}
