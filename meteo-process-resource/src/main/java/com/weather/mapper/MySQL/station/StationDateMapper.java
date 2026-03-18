package com.weather.mapper.MySQL.station;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StationDateMapper {
    List<String> selectDatesByStation(@Param("station") String station);

    List<Map<String, Object>> getCollectionYear(@Param("station") String station);

    List<Map<String, Object>> getCollectionMonth(@Param("station") String station, @Param("year") String year);

    List<Map<String, Object>> getCollectionDay(@Param("station") String station,
                                               @Param("year") String year,
                                               @Param("month") String month);

    List<Map<String, Object>> countByMonth(@Param("tableName") String tableName, @Param("month") String month);
}
