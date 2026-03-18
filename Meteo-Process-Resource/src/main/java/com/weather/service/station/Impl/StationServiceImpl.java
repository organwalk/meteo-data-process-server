package com.weather.service.station.Impl;

import com.weather.common.response.DataResult;
import com.weather.common.response.MeteorologyResult;
import com.weather.common.response.StationResult;
import com.weather.common.support.StationTableNameResolver;
import com.weather.mapper.MySQL.station.StationDateMapper;
import com.weather.mapper.MySQL.station.StationMapper;
import com.weather.service.station.StationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StationServiceImpl implements StationService {
    private final StationMapper stationMapper;
    private final StationDateMapper stationDateMapper;
    private final StationTableNameResolver tableNameResolver;

    @Override
    public StationResult getStationInfo() {
        List<Map<String, Object>> stationList = stationMapper.selectStationInfo();
        return stationList.isEmpty()
                ? StationResult.fail("鏃犳硶鑾峰彇姘旇薄绔欏垪琛紝璇风◢鍚庡啀璇?")
                : StationResult.success(stationList);
    }

    @Override
    public DataResult getStationDateByStationId(String station) {
        List<String> stationDates = stationDateMapper.selectDatesByStation(station).stream()
                .map(LocalDate::parse)
                .sorted(Comparator.naturalOrder())
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        return DataResult.success(stationDates);
    }

    @Override
    public DataResult getCollectionYear(String station) {
        List<Map<String, Object>> collectionYearList = stationDateMapper.getCollectionYear(station);
        return collectionYearList.isEmpty()
                ? DataResult.fail("鏈兘鑾峰彇鍒版湁鏁堥噰闆嗗勾浠?")
                : DataResult.success(collectionYearList);
    }

    @Override
    public DataResult getCollectionMonth(String station, String year) {
        List<Map<String, Object>> collectionMonthList = stationDateMapper.getCollectionMonth(station, year);
        return collectionMonthList.isEmpty()
                ? DataResult.fail("鏈兘鑾峰彇鍒版湁鏁堥噰闆嗘湀浠?")
                : DataResult.success(collectionMonthList);
    }

    @Override
    public DataResult getCollectionDay(String station, String year, String month) {
        List<Map<String, Object>> collectionDayList = stationDateMapper.getCollectionDay(station, year, month);
        return collectionDayList.isEmpty()
                ? DataResult.fail("鏈兘鑾峰彇鍒版湁鏁堥噰闆嗗ぉ鏁?")
                : DataResult.success(collectionDayList);
    }

    @Override
    public MeteorologyResult getStationDataSum(String station, String year, String month) {
        String tableName = tableNameResolver.resolve(station);
        List<Map<String, Object>> dataSum = stationDateMapper.countByMonth(tableName, year + "-" + month);
        return dataSum.isEmpty() ? MeteorologyResult.fail() : MeteorologyResult.success(station, 0, dataSum);
    }
}
