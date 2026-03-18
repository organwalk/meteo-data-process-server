package com.weather.service.station;

import com.weather.common.response.DataResult;
import com.weather.common.response.MeteorologyResult;
import com.weather.common.response.StationResult;

public interface StationService {
    StationResult getStationInfo();

    DataResult getStationDateByStationId(String station);

    DataResult getCollectionYear(String station);

    DataResult getCollectionMonth(String station, String year);

    DataResult getCollectionDay(String station, String year, String month);

    MeteorologyResult getStationDataSum(String station, String year, String month);
}
