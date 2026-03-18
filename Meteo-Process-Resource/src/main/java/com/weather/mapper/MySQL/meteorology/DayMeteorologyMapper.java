package com.weather.mapper.MySQL.meteorology;

import com.weather.application.query.MeteorologyQueryCriteria;
import com.weather.entity.Meteorology;

import java.util.List;

public interface DayMeteorologyMapper {
    List<Meteorology> selectMeteorologyDay(MeteorologyQueryCriteria criteria);
}
