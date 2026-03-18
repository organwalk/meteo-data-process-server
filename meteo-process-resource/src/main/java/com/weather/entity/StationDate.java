package com.weather.entity;

import lombok.Data;

/**
 * 气象站点有效日期表实体
 * by organwalk 2023-04-02
 */
@Data
public class StationDate {
    private Integer id;
    private String date;
    private String station;
}
