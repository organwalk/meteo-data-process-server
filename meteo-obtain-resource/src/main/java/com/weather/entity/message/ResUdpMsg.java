package com.weather.entity.message;

import lombok.Data;

@Data
public class ResUdpMsg {
    private int code;
    private int last;
    private String username;
    private String token;
    private String data;
    private String station;
    private String date;

    public static ResUdpMsg token(int code,String username,String token){
        ResUdpMsg msg = new ResUdpMsg();
        msg.setCode(code);
        msg.setUsername(username);
        msg.setToken(token);
        return msg;
    }

    public ResUdpMsg(int code, String data) {
        this.code = code;
        this.data = data;
    }

    public ResUdpMsg(int code, String station, String date){
        this.code = code;
        this.station = station;
        this.date = date;
    }

    public ResUdpMsg(int code, int last, String station, String date, String data){
        this.code = code;
        this.last = last;
        this.station = station;
        this.date = date;
        this.data = data;
    }

    public ResUdpMsg(){}
}
