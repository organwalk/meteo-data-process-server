package com.weather.mapper.Redis;

import com.weather.application.query.MeteorologyQueryCriteria;

import java.util.List;

public interface RedisRepository {
    boolean saveHourMeteoCache(MeteorologyQueryCriteria criteria, List<List<String>> meteoData);

    List<List<String>> getHourMeteoCache(MeteorologyQueryCriteria criteria);

    boolean saveDayMeteoCache(MeteorologyQueryCriteria criteria, List<List<String>> meteoData);

    List<List<String>> getDayMeteoCache(MeteorologyQueryCriteria criteria);

    boolean saveDateRangeCache(MeteorologyQueryCriteria criteria, List<List<String>> meteoData);

    List<List<String>> getDateRangeCache(MeteorologyQueryCriteria criteria);
}
