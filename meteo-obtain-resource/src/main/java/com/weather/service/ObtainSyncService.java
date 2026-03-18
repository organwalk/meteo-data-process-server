package com.weather.service;

public interface ObtainSyncService {
    boolean getToken(String name);

    boolean voidToken(String name);

    boolean getStationCode(String name);

    boolean getDateRange(String name, String station);

    boolean getMeteoData(String name, String station, String start, String end);
}
