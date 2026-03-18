package com.weather.service.meteorology;

import com.weather.common.response.MeteorologyResult;

public interface MeteorologyService {
    MeteorologyResult getMeteorologyByHour(String station, String date, String hour, String which, int pageSize, int offset);

    MeteorologyResult getMeteorologyByDay(String station, String date, String which, String type);

    MeteorologyResult getMeteorologyByDate(String station, String startDate, String endDate, String which, int pageSize, int offset);

    MeteorologyResult getComplexMeteorology(String station,
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
                                            int offset);
}
