package com.weather.entity.message;

import lombok.Data;

@Data
public class ReqUdpMsg {
    private String msgType;
    private String name;
    private String station;
    private String start;
    private String end;

    public ReqUdpMsg(String msgType, String name) {
        this.msgType = msgType;
        this.name = name;
    }

    public ReqUdpMsg(String msgType, String name, String station) {
        this.msgType = msgType;
        this.name = name;
        this.station = station;
    }

    public ReqUdpMsg(String msgType, String name, String station, String start, String end) {
        this.msgType = msgType;
        this.name = name;
        this.station = station;
        this.start = start;
        this.end = end;
    }

    public ReqUdpMsg(){}
}
