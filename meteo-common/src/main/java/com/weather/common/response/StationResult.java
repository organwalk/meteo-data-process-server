package com.weather.common.response;

import com.weather.common.constants.ApiStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationResult {
    private int success;
    private Object station;

    public static StationResult success(Object station) {
        return StationResult.builder()
                .success(ApiStatus.SUCCESS)
                .station(station)
                .build();
    }

    public static StationResult fail(Object station) {
        return StationResult.builder()
                .success(ApiStatus.FAIL)
                .station(station)
                .build();
    }
}
