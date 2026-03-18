package com.weather.service.meteorology.Impl;

import com.weather.application.query.ComplexQueryCriteria;
import com.weather.application.query.MeteorologyQueryCriteria;
import com.weather.common.response.MeteorologyResult;
import com.weather.common.support.StationTableNameResolver;
import com.weather.entity.Meteorology;
import com.weather.mapper.MySQL.meteorology.ComplexMeteorologyMapper;
import com.weather.mapper.MySQL.meteorology.DateMeteorologyMapper;
import com.weather.mapper.MySQL.meteorology.DayMeteorologyMapper;
import com.weather.mapper.MySQL.meteorology.DayToChartsMeteorologyMapper;
import com.weather.mapper.MySQL.meteorology.HourMeteorologyMapper;
import com.weather.mapper.Redis.RedisRepository;
import com.weather.service.meteorology.MeteorologyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class MeteorologyServiceImpl implements MeteorologyService {
    private final HourMeteorologyMapper hourMapper;
    private final DayMeteorologyMapper dayMapper;
    private final DayToChartsMeteorologyMapper dayChartMapper;
    private final DateMeteorologyMapper dateMapper;
    private final ComplexMeteorologyMapper complexMapper;
    private final RedisRepository cache;
    private final StationTableNameResolver tableNameResolver;

    @Override
    public MeteorologyResult getMeteorologyByHour(String station, String date, String hour, String which, int pageSize, int offset) {
        MeteorologyQueryCriteria criteria = MeteorologyQueryCriteria.builder()
                .station(station)
                .tableName(tableNameResolver.resolve(station))
                .which(which)
                .pageSize(pageSize)
                .offset(offset)
                .startDateTime(date + " " + hour + ":00:00")
                .endDateTime(date + " " + hour + ":59:59")
                .build();

        int total = hourMapper.selectMeteorologyHourCount(criteria);
        List<List<String>> cacheResult = cache.getHourMeteoCache(criteria);
        if (!cacheResult.isEmpty()) {
            return MeteorologyResult.success(station, total, cacheResult);
        }

        List<List<String>> sqlResults = toRows(hourMapper.selectMeteorologyHour(criteria), true);
        if (sqlResults.isEmpty()) {
            return MeteorologyResult.fail();
        }
        cache.saveHourMeteoCache(criteria, sqlResults);
        return MeteorologyResult.success(station, total, sqlResults);
    }

    @Override
    public MeteorologyResult getMeteorologyByDay(String station, String date, String which, String type) {
        MeteorologyQueryCriteria criteria = MeteorologyQueryCriteria.builder()
                .station(station)
                .tableName(tableNameResolver.resolve(station))
                .which(which)
                .type(type)
                .startDateTime(date + " 00:00:00")
                .endDateTime(date + " 23:59:59")
                .build();

        List<List<String>> cacheResult = cache.getDayMeteoCache(criteria);
        if (!cacheResult.isEmpty()) {
            return MeteorologyResult.success(station, 0, cacheResult);
        }

        List<Meteorology> meteorologyList = "1".equals(type)
                ? dayMapper.selectMeteorologyDay(criteria)
                : dayChartMapper.selectMeteorologyDayToCharts(criteria);
        List<List<String>> sqlResults = toRows(meteorologyList, true);
        if (sqlResults.isEmpty()) {
            return MeteorologyResult.fail();
        }
        cache.saveDayMeteoCache(criteria, sqlResults);
        return MeteorologyResult.success(station, 0, sqlResults);
    }

    @Override
    public MeteorologyResult getMeteorologyByDate(String station, String startDate, String endDate, String which, int pageSize, int offset) {
        MeteorologyQueryCriteria criteria = MeteorologyQueryCriteria.builder()
                .station(station)
                .tableName(tableNameResolver.resolve(station))
                .which(which)
                .pageSize(pageSize)
                .offset(offset)
                .startDateTime(startDate)
                .endDateTime(startDate.equals(endDate)
                        ? LocalDate.parse(endDate).plusDays(1).toString()
                        : endDate)
                .build();

        int total = dateMapper.selectMeteorologyDateCount(criteria);
        List<List<String>> cacheResult = cache.getDateRangeCache(criteria);
        if (!cacheResult.isEmpty()) {
            return MeteorologyResult.success(station, total, cacheResult);
        }

        List<List<String>> sqlResults = toRows(dateMapper.selectMeteorologyDate(criteria), false);
        if (sqlResults.isEmpty()) {
            return MeteorologyResult.fail();
        }
        cache.saveDateRangeCache(criteria, sqlResults);
        return MeteorologyResult.success(station, total, sqlResults);
    }

    @Override
    public MeteorologyResult getComplexMeteorology(String station,
                                                   String startDate,
                                                   String endDate,
                                                   String startTemperature,
                                                   String endTemperature,
                                                   String startHumidity,
                                                   String endHumidity,
                                                   String startSpeed,
                                                   String endSpeed,
                                                   String startDirection,
                                                   String endDirection,
                                                   String startRain,
                                                   String endRain,
                                                   String startSunlight,
                                                   String endSunlight,
                                                   String startPm25,
                                                   String endPm25,
                                                   String startPm10,
                                                   String endPm10,
                                                   int pageSize,
                                                   int offset) {
        ComplexQueryCriteria criteria = ComplexQueryCriteria.builder()
                .station(station)
                .tableName(tableNameResolver.resolve(station))
                .startDateTime(startDate + " 00:00:00")
                .endDateTime(endDate + " 23:59:59")
                .startTemperature(startTemperature)
                .endTemperature(endTemperature)
                .startHumidity(startHumidity)
                .endHumidity(endHumidity)
                .startSpeed(startSpeed)
                .endSpeed(endSpeed)
                .startDirection(startDirection)
                .endDirection(endDirection)
                .startRain(startRain)
                .endRain(endRain)
                .startSunlight(startSunlight)
                .endSunlight(endSunlight)
                .startPm25(startPm25)
                .endPm25(endPm25)
                .startPm10(startPm10)
                .endPm10(endPm10)
                .pageSize(pageSize)
                .offset(offset)
                .build();

        int total = complexMapper.selectMeteorologyComplexCount(criteria);
        List<List<String>> sqlResults = toRows(complexMapper.selectMeteorologyComplex(criteria), true);
        return sqlResults.isEmpty() ? MeteorologyResult.fail() : MeteorologyResult.success(station, total, sqlResults);
    }

    private List<List<String>> toRows(List<Meteorology> meteorologyList, boolean includeDateTime) {
        if (meteorologyList == null) {
            return Collections.emptyList();
        }
        List<List<String>> results = new ArrayList<>();
        for (Meteorology meteorology : meteorologyList) {
            List<String> row = new ArrayList<>();
            if (includeDateTime) {
                if (meteorology.getDatetime() != null) {
                    row.add(meteorology.getDatetime());
                }
            } else if (meteorology.getDate() != null) {
                row.add(meteorology.getDate());
            }
            addIfPresent(row, meteorology.getTemperature());
            addIfPresent(row, meteorology.getHumidity());
            addIfPresent(row, meteorology.getSpeed());
            addIfPresent(row, meteorology.getDirection());
            addIfPresent(row, meteorology.getRain());
            addIfPresent(row, meteorology.getSunlight());
            addIfPresent(row, meteorology.getPm25());
            addIfPresent(row, meteorology.getPm10());
            results.add(row);
        }
        return results;
    }

    private void addIfPresent(List<String> row, String value) {
        if (value != null) {
            row.add(value);
        }
    }
}
