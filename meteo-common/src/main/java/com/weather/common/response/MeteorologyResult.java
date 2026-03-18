package com.weather.common.response;

import com.weather.common.constants.ApiStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeteorologyResult {
    private int success;
    private String station;
    private int total;
    private Object data;

    public static MeteorologyResult success(String station, int total, Object data) {
        return MeteorologyResult.builder()
                .success(ApiStatus.SUCCESS)
                .station(station)
                .total(total)
                .data(data)
                .build();
    }

    public static MeteorologyResult fail() {
        return MeteorologyResult.builder()
                .success(ApiStatus.FAIL)
                .build();
    }
}
