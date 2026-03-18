package com.weather.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexQueryCriteria {
    private String station;
    private String tableName;
    private String startDateTime;
    private String endDateTime;
    private String startTemperature;
    private String endTemperature;
    private String startHumidity;
    private String endHumidity;
    private String startSpeed;
    private String endSpeed;
    private String startDirection;
    private String endDirection;
    private String startRain;
    private String endRain;
    private String startSunlight;
    private String endSunlight;
    private String startPm25;
    private String endPm25;
    private String startPm10;
    private String endPm10;
    private int pageSize;
    private int offset;
}
