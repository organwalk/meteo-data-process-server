package com.weather.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class MeteoData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String station;
    private Date date;
    private Date datetime;
    private Time time;
    private Float temperature;
    private Float humidity;
    private Float speed;
    private Float direction;
    private Float rain;
    private Float sunlight;
    private Float pm25;
    private Float pm10;

    public MeteoData(String station, Date date, Date datetime, Time time, Float temperature, Float humidity, Float speed, Float direction, Float rain, Float sunlight, Float pm25, Float pm10) {
        this.station = station;
        this.date = date;
        this.datetime = datetime;
        this.time = time;
        this.temperature = temperature;
        this.humidity = humidity;
        this.speed = speed;
        this.direction = direction;
        this.rain = rain;
        this.sunlight = sunlight;
        this.pm25 = pm25;
        this.pm10 = pm10;
    }
}
