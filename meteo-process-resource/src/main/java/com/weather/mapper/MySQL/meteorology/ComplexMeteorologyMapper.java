package com.weather.mapper.MySQL.meteorology;

import com.weather.application.query.ComplexQueryCriteria;
import com.weather.entity.Meteorology;

import java.util.List;

public interface ComplexMeteorologyMapper {
    List<Meteorology> selectMeteorologyComplex(ComplexQueryCriteria criteria);

    int selectMeteorologyComplexCount(ComplexQueryCriteria criteria);
}
