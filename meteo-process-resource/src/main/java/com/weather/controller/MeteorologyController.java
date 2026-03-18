package com.weather.controller;

import com.weather.common.constants.HeaderNames;
import com.weather.common.response.DataResult;
import com.weather.common.response.MeteorologyResult;
import com.weather.entity.MeteoSyncReq;
import com.weather.service.meteorology.MeteorologyService;
import com.weather.service.sync.MeteorologySyncService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qx")
@AllArgsConstructor
@Validated
public class MeteorologyController {
    private final MeteorologyService meteorologyService;
    private final MeteorologySyncService syncService;

    @PostMapping("/stat_hour")
    public MeteorologyResult getMeteorologyByHour(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                                  @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                                                          message = "date瀛楁蹇呴』鏄痽yyy-mm-dd鏍煎紡鏁版嵁") String date,
                                                  @RequestParam @Pattern(regexp = "([01]?[0-9]|2[0-3])",
                                                          message = "hour瀛楁蹇呴』鏄?0~23鐨?4灏忔椂鏍煎紡鏁版嵁") String hour,
                                                  @RequestParam @Pattern(regexp = "^(?:[1-8],?)+$",
                                                          message = "which瀛楁蹇呴』鏄?-8鑼冨洿鍐呯殑鍙€夎绱?") String which,
                                                  @RequestParam @Min(value = 1, message = "pageSize蹇呴』涓哄ぇ浜?鐨勬暣鏁?")
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0) int pageSize,
                                                  @RequestParam @Min(value = 0, message = "offset蹇呴』涓哄ぇ浜庢垨绛変簬0鐨勬暣鏁?")
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0) int offset) {
        return meteorologyService.getMeteorologyByHour(station, date, hour, which, pageSize, offset);
    }

    @PostMapping("/stat_day")
    public MeteorologyResult getMeteorologyByDay(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                                 @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                                                         message = "date瀛楁蹇呴』鏄痽yyy-mm-dd鏍煎紡鏁版嵁") String date,
                                                 @RequestParam @Pattern(regexp = "^(?:[1-8],?)+$",
                                                         message = "which瀛楁蹇呴』鏄?-8鑼冨洿鍐呯殑鍙€夎绱?") String which,
                                                 @RequestParam @Pattern(regexp = "^([12])$",
                                                         message = "type瀛楁蹇呴』鏄?鎴?") String type) {
        return meteorologyService.getMeteorologyByDay(station, date, which, type);
    }

    @PostMapping("/stat_day_range")
    public MeteorologyResult getMeteorologyByDayRange(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                                      @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                                                              message = "start_date瀛楁蹇呴』鏄痽yyy-mm-dd鏍煎紡鏁版嵁") String startDate,
                                                      @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                                                              message = "end_date瀛楁蹇呴』鏄痽yyy-mm-dd鏍煎紡鏁版嵁") String endDate,
                                                      @RequestParam @Pattern(regexp = "^(?:[1-8],?)+$",
                                                              message = "which瀛楁蹇呴』鏄?-8鑼冨洿鍐呯殑鍙€夎绱?") String which,
                                                      @RequestParam @Min(value = 1, message = "pageSize蹇呴』涓哄ぇ浜?鐨勬暣鏁?")
                                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0) int pageSize,
                                                      @RequestParam @Min(value = 0, message = "offset蹇呴』涓哄ぇ浜庢垨绛變簬0鐨勬暣鏁?")
                                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0) int offset) {
        return meteorologyService.getMeteorologyByDate(station, startDate, endDate, which, pageSize, offset);
    }

    @PostMapping("/query")
    public MeteorologyResult getComplexMeteorology(@RequestParam @NotBlank(message = "station涓嶈兘涓虹┖") String station,
                                                   @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                                                           message = "start_date瀛楁蹇呴』鏄痽yyy-mm-dd鏍煎紡鏁版嵁") String startDate,
                                                   @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                                                           message = "end_date瀛楁蹇呴』鏄痽yyy-mm-dd鏍煎紡鏁版嵁") String endDate,
                                                   @RequestParam(required = false) String startTemperature,
                                                   @RequestParam(required = false) String endTemperature,
                                                   @RequestParam(required = false) String startHumidity,
                                                   @RequestParam(required = false) String endHumidity,
                                                   @RequestParam(required = false) String startSpeed,
                                                   @RequestParam(required = false) String endSpeed,
                                                   @RequestParam(required = false) String startDirection,
                                                   @RequestParam(required = false) String endDirection,
                                                   @RequestParam(required = false) String startRain,
                                                   @RequestParam(required = false) String endRain,
                                                   @RequestParam(required = false) String startSunlight,
                                                   @RequestParam(required = false) String endSunlight,
                                                   @RequestParam(required = false) String startPm25,
                                                   @RequestParam(required = false) String endPm25,
                                                   @RequestParam(required = false) String startPm10,
                                                   @RequestParam(required = false) String endPm10,
                                                   @RequestParam @Min(value = 1, message = "pageSize蹇呴』涓哄ぇ浜?鐨勬暣鏁?")
                                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0) int pageSize,
                                                   @RequestParam @Min(value = 0, message = "offset蹇呴』涓哄ぇ浜庢垨绛変簬0鐨勬暣鏁?")
                                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0) int offset) {
        return meteorologyService.getComplexMeteorology(station, startDate, endDate,
                startTemperature, endTemperature, startHumidity, endHumidity, startSpeed, endSpeed,
                startDirection, endDirection, startRain, endRain, startSunlight, endSunlight,
                startPm25, endPm25, startPm10, endPm10, pageSize, offset);
    }

    @GetMapping("/obtain/connect")
    public DataResult connectDataSaveServer(@RequestHeader(name = HeaderNames.USER_NAME) String name) {
        return syncService.connectDataSaveServer(name);
    }

    @GetMapping("/obtain/sync/station")
    public DataResult syncStationData(@RequestHeader(name = HeaderNames.USER_NAME) String name) {
        return syncService.syncStationData(name);
    }

    @GetMapping("/obtain/sync/date_range")
    public DataResult syncDateRange(@RequestHeader(name = HeaderNames.USER_NAME) String name) {
        return syncService.syncDateRange(name);
    }

    @GetMapping("/obtain/sync/latest_date")
    public DataResult syncLatestDate(@RequestParam("station") String station) {
        return syncService.syncLatestDate(station);
    }

    @GetMapping("/obtain/sync/exist")
    public DataResult syncHavingData(@RequestParam("station") String station) {
        return syncService.syncHavingData(station);
    }

    @PostMapping("/obtain/sync/meteo_data")
    public DataResult syncMeteoData(@RequestHeader(name = HeaderNames.USER_NAME) String name,
                                    @RequestBody MeteoSyncReq req) {
        return syncService.syncMeteoData(name, req);
    }

    @GetMapping("/obtain/close")
    public DataResult closeDataSaveServer(@RequestHeader(name = HeaderNames.USER_NAME) String name) {
        return syncService.closeDataSaveServer(name);
    }
}
