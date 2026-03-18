package com.weather.common.support;

public final class SyncTaskKey {
    private SyncTaskKey() {
    }

    public static String token(String username) {
        return "TOKEN:" + username;
    }

    public static String stationCode(String username) {
        return "STATION_CODE:" + username;
    }

    public static String dateRange(String station) {
        return "DATE_RANGE:" + station;
    }

    public static String meteoData(String station, String start, String end) {
        return "METEO_DATA:" + station + ":" + start + ":" + end;
    }
}
