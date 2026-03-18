package com.weather.mapper.MySQL.station;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StationMapper {
    List<Map<String, Object>> selectStationInfo();

    List<String> getStationList();

    String meteoDataLatestDate(@Param("tableName") String tableName, @Param("station") String station);

    Integer havingDataByStationCode(@Param("tableName") String tableName, @Param("station") String station);
}
