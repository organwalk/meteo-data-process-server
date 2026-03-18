package com.weather.controller;

import com.weather.common.response.DataResult;
import com.weather.common.response.MeteorologyResult;
import com.weather.service.station.StationService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qx")
@AllArgsConstructor
@Validated
public class StationController {
    private final StationService stationService;

    @GetMapping("/stations")
    public Object getStationInfo(@RequestParam(name = "station", required = false) String station) {
        return station == null
                ? stationService.getStationInfo()
                : stationService.getStationDateByStationId(station);
    }

    @GetMapping("/collection_year")
    public DataResult getCollectionYear(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station) {
        return stationService.getCollectionYear(station);
    }

    @GetMapping("/collection_month")
    public DataResult getCollectionMonth(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                         @RequestParam @NotBlank(message = "year涓嶈兘涓虹┖") String year) {
        return stationService.getCollectionMonth(station, year);
    }

    @GetMapping("/collection_day")
    public DataResult getCollectionDay(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                       @RequestParam @NotBlank(message = "year涓嶈兘涓虹┖") String year,
                                       @RequestParam @NotBlank(message = "month涓嶈兘涓虹┖") String month) {
        return stationService.getCollectionDay(station, year, month);
    }

    @GetMapping("/data_sum")
    public MeteorologyResult getDataSumByMonth(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                               @RequestParam @NotBlank(message = "year涓嶈兘涓虹┖") String year,
                                               @RequestParam @NotBlank(message = "month涓嶈兘涓虹┖") String month) {
        return stationService.getStationDataSum(station, year, month);
    }
}
