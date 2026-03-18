package com.weather.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 气象数据表实体
 * by organwalk 2023-04-02
 */
public class Meteorology extends Model<Meteorology> {

    private Integer id;
    private String station;
    private String date;
    private String datetime;
    private String time;
    private String temperature;
    private String humidity;
    private String speed;
    private String direction;
    private String rain;
    private String sunlight;
    private String pm25;
    private String pm10;
    private String dataSum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getSunlight() {
        return sunlight;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getDataSum() {
        return dataSum;
    }

    public void setDataSum(String dataSum) {
        this.dataSum = dataSum;
    }

    @Override
    public String toString() {
        return "[" +
                "\"" + datetime + "\"," +
                "\"" + temperature + "\"," +
                "\"" + humidity + "\"," +
                "\"" + speed + "\"," +
                "\"" + direction + "\"," +
                "\"" + rain + "\"," +
                "\"" + sunlight + "\"," +
                "\"" + pm25 + "\"," +
                "\"" + pm10 + "\"" +
                "]";
    }


}
