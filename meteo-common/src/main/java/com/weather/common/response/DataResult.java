package com.weather.common.response;

import com.weather.common.constants.ApiStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataResult {
    private Integer success;
    private Object data;

    public static DataResult success(Object data) {
        return DataResult.builder()
                .success(ApiStatus.SUCCESS)
                .data(data)
                .build();
    }

    public static DataResult fail(Object data) {
        return DataResult.builder()
                .success(ApiStatus.FAIL)
                .data(data)
                .build();
    }
}
