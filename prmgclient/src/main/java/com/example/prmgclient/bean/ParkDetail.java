package com.example.prmgclient.bean;

import java.io.Serializable;

/**
 * Author: lovegod
 * Created by 123 on 2016/11/9.
 */
public class ParkDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private int Pid;
    private String Pname;
    private String adress;
    private String CarCount;
    private String PayCri;
    private int rssi;
    private String wifipwd;
    private String wifiname;
    private String parking_left;
    private String phone;
    private double latitude;
    private double longitude;

    public int getPid() {
        return Pid;
    }

    public void setPid(int pid) {
        Pid = pid;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCarCount() {
        return CarCount;
    }

    public void setCarCount(String carCount) {
        CarCount = carCount;
    }

    public String getPayCri() {
        return PayCri;
    }

    public void setPayCri(String payCri) {
        PayCri = payCri;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getWifipwd() {
        return wifipwd;
    }

    public void setWifipwd(String wifipwd) {
        this.wifipwd = wifipwd;
    }

    public String getWifiname() {
        return wifiname;
    }

    public void setWifiname(String wifiname) {
        this.wifiname = wifiname;
    }

    public String getParking_left() {
        return parking_left;
    }

    public void setParking_left(String parking_left) {
        this.parking_left = parking_left;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "ParkDetail{" +
                "Pid=" + Pid +
                ", Pname='" + Pname + '\'' +
                ", adress='" + adress + '\'' +
                ", CarCount='" + CarCount + '\'' +
                ", PayCri='" + PayCri + '\'' +
                ", rssi=" + rssi +
                ", wifipwd='" + wifipwd + '\'' +
                ", wifiname='" + wifiname + '\'' +
                ", parking_left='" + parking_left + '\'' +
                ", phone='" + phone + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
