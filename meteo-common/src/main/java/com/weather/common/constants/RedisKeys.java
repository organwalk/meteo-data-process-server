package com.weather.common.constants;

public final class RedisKeys {
    public static final String USER_TOKEN_PREFIX = "meteo:user:token:";
    public static final String USER_TOKEN_HASH = "Meteo-UserClient-User";
    public static final String OBTAIN_TOKEN_PREFIX = "meteo:obtain:token:";
    public static final String OBTAIN_TOKEN_HASH = "tokens";
    public static final String STATION_DATE_RANGE_PREFIX = "meteo:station:date-range:";
    public static final String METEO_DATA_PREFIX = "meteo:station:data:";
    public static final String CACHE_PREFIX = "meteo:cache:";

    private RedisKeys() {
    }
}
