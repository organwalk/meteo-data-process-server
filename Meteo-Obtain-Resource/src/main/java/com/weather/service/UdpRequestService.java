package com.weather.service;

public interface UdpRequestService {
    boolean getToken(String userName);

    boolean voidToken(String userName);

    boolean getAllStationCode(String name);

    boolean getAllStationDataRange(String name, String station);

    boolean getMeteoData(String name, String station, String start, String end);
}
