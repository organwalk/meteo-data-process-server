package com.weather.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeteorologyQueryCriteria {
    private String station;
    private String tableName;
    private String startDateTime;
    private String endDateTime;
    private String which;
    private String type;
    private int pageSize;
    private int offset;
}
