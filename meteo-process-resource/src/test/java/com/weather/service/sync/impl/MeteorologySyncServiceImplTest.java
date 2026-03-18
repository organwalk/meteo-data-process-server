package com.weather.service.sync.impl;

import com.weather.client.ObtainClient;
import com.weather.common.response.DataResult;
import com.weather.common.support.StationTableNameResolver;
import com.weather.mapper.MySQL.station.StationMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeteorologySyncServiceImplTest {
    @Test
    void shouldFailWhenNoStationsAvailableForDateRangeSync() {
        ObtainClient obtainClient = mock(ObtainClient.class);
        StationMapper stationMapper = mock(StationMapper.class);
        when(stationMapper.getStationList()).thenReturn(Collections.emptyList());

        MeteorologySyncServiceImpl service = new MeteorologySyncServiceImpl(
                obtainClient,
                stationMapper,
                new StationTableNameResolver()
        );

        DataResult result = service.syncDateRange("tester");

        assertEquals(0, result.getSuccess());
    }
}
