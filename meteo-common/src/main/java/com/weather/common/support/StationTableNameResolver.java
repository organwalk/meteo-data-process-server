package com.weather.common.support;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
public class StationTableNameResolver {
    private static final Pattern STATION_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    private static final String TABLE_SUFFIX = "_meteo_data";

    public String resolve(String station) {
        if (!StringUtils.hasText(station) || !STATION_CODE_PATTERN.matcher(station).matches()) {
            throw new IllegalArgumentException("station参数非法");
        }
        return station + TABLE_SUFFIX;
    }
}
