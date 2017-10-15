/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

/**
 *
 * @author LFeng
 */
public class Location {
    private String SystemNo;
    private String GpsTime;
    private String Lon;
    private String Lat;
    private String Speed;
    private String Angle;
    private String Locate;
    private String Oil;

    public String getSystemNo() {
        return SystemNo;
    }

    public void setSystemNo(String SystemNo) {
        this.SystemNo = SystemNo;
    }

    public String getGpsTime() {
        return GpsTime;
    }

    public void setGpsTime(String GpsTime) {
        this.GpsTime = GpsTime;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String Lon) {
        this.Lon = Lon;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String Lat) {
        this.Lat = Lat;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String Speed) {
        this.Speed = Speed;
    }

    public String getAngle() {
        return Angle;
    }

    public void setAngle(String Angle) {
        this.Angle = Angle;
    }

    public String getLocate() {
        return Locate;
    }

    public void setLocate(String Locate) {
        this.Locate = Locate;
    }

    public String getOil() {
        return Oil;
    }

    public void setOil(String Oil) {
        this.Oil = Oil;
    }
    
}
