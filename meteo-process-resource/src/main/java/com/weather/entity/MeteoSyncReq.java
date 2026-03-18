package com.weather.entity;

import lombok.Data;

@Data
public class MeteoSyncReq {
    private String station;
    private String start;
    private String end;
}
