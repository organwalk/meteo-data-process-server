package com.weather.service.sync;

import com.weather.common.response.DataResult;
import com.weather.entity.MeteoSyncReq;

public interface MeteorologySyncService {
    DataResult connectDataSaveServer(String name);

    DataResult syncStationData(String name);

    DataResult syncDateRange(String name);

    DataResult syncLatestDate(String station);

    DataResult syncHavingData(String station);

    DataResult syncMeteoData(String name, MeteoSyncReq req);

    DataResult closeDataSaveServer(String name);
}
