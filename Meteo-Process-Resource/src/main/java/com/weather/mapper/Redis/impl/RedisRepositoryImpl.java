package com.weather.mapper.Redis.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.application.query.MeteorologyQueryCriteria;
import com.weather.common.properties.CacheProperties;
import com.weather.mapper.Redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    public boolean saveHourMeteoCache(MeteorologyQueryCriteria criteria, List<List<String>> meteoData) {
        return save(cacheKey("hour", criteria), meteoData);
    }

    @Override
    public List<List<String>> getHourMeteoCache(MeteorologyQueryCriteria criteria) {
        return read(cacheKey("hour", criteria));
    }

    @Override
    public boolean saveDayMeteoCache(MeteorologyQueryCriteria criteria, List<List<String>> meteoData) {
        return save(cacheKey("day", criteria), meteoData);
    }

    @Override
    public List<List<String>> getDayMeteoCache(MeteorologyQueryCriteria criteria) {
        return read(cacheKey("day", criteria));
    }

    @Override
    public boolean saveDateRangeCache(MeteorologyQueryCriteria criteria, List<List<String>> meteoData) {
        return save(cacheKey("range", criteria), meteoData);
    }

    @Override
    public List<List<String>> getDateRangeCache(MeteorologyQueryCriteria criteria) {
        return read(cacheKey("range", criteria));
    }

    private boolean save(String key, List<List<String>> meteoData) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(meteoData), cacheProperties.getMeteorologyTtl());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private List<List<String>> read(String key) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(cachedValue.toString(), new TypeReference<>() { });
        } catch (Exception exception) {
            return Collections.emptyList();
        }
    }

    private String cacheKey(String type, MeteorologyQueryCriteria criteria) {
        return String.join(":",
                cacheProperties.getPrefix(),
                type,
                criteria.getTableName(),
                nullToEmpty(criteria.getStartDateTime()),
                nullToEmpty(criteria.getEndDateTime()),
                nullToEmpty(criteria.getType()),
                nullToEmpty(criteria.getWhich()),
                String.valueOf(criteria.getPageSize()),
                String.valueOf(criteria.getOffset()));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
