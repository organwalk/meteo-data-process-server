package com.weather.mapper;

import com.weather.entity.table.MeteoData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaveToMySQLMapper {
    void insertStation(@Param("station") String station, @Param("name") String name);

    void insertStationDateRange(@Param("date") String date, @Param("station") String station);

    void createMeteoDataTableIfNotExists(@Param("tableName") String tableName);

    void insertMeteoData(@Param("tableName") String tableName,
                         @Param("meteoDataList") List<MeteoData> meteoDataList);
}
