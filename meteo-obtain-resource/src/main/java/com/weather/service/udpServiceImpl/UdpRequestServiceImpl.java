package com.weather.service.udpServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.client.UDPClient;
import com.weather.common.constants.UdpCodes;
import com.weather.config.UdpProperties;
import com.weather.entity.request.GetAllStationCode;
import com.weather.entity.request.GetMeteoData;
import com.weather.entity.request.GetStationDateRange;
import com.weather.entity.request.GetToken;
import com.weather.entity.request.VoidToken;
import com.weather.repository.RedisRepository;
import com.weather.service.UdpRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UdpRequestServiceImpl implements UdpRequestService {
    private final UDPClient udpClient;
    private final RedisRepository repository;
    private final ObjectMapper objectMapper;
    private final UdpProperties properties;

    @Override
    public boolean getToken(String userName) {
        if (resolveToken(userName) != null) {
            return true;
        }
        return send(new GetToken(UdpCodes.GET_TOKEN, properties.getAuthUsername(), properties.getAuthPassword()));
    }

    @Override
    public boolean voidToken(String userName) {
        String token = resolveToken(userName);
        if (token == null) {
            return false;
        }
        boolean sent = send(new VoidToken(UdpCodes.VOID_TOKEN, token));
        if (sent) {
            repository.deleteToken(userName);
            repository.deleteToken(properties.getAuthUsername());
        }
        return sent;
    }

    @Override
    public boolean getAllStationCode(String name) {
        String token = resolveToken(name);
        return token != null && send(new GetAllStationCode(UdpCodes.GET_ALL_STATION_CODE, token));
    }

    @Override
    public boolean getAllStationDataRange(String name, String station) {
        String token = resolveToken(name);
        return token != null && send(new GetStationDateRange(UdpCodes.GET_STATION_DATE_RANGE, token, Integer.valueOf(station)));
    }

    @Override
    public boolean getMeteoData(String name, String station, String start, String end) {
        String token = resolveToken(name);
        return token != null && send(new GetMeteoData(UdpCodes.GET_METEO_DATA, token, Integer.valueOf(station), start, end));
    }

    private boolean send(Object request) {
        try {
            udpClient.send(objectMapper.writeValueAsString(request));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private String resolveToken(String userName) {
        String token = repository.getToken(userName);
        return token == null ? repository.getDefaultToken() : token;
    }
}
