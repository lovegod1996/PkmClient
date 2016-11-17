package com.example.prmgclient.bean;

import java.io.Serializable;

/**
 * Author: lovegod
 * Created by 123 on 2016/11/16.
 */
public class Record  implements Serializable{
    private int fid;
    private String Unum;
    private String Pname;
    private String Intime;
    private String Outtime;
    private String Pay;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getUnum() {
        return Unum;
    }

    public void setUnum(String unum) {
        Unum = unum;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getIntime() {
        return Intime;
    }

    public void setIntime(String intime) {
        Intime = intime;
    }

    public String getOuttime() {
        return Outtime;
    }

    public void setOuttime(String outtime) {
        Outtime = outtime;
    }

    public String getPay() {
        return Pay;
    }

    public void setPay(String pay) {
        Pay = pay;
    }

    @Override
    public String toString() {
        return "Record{" +
                "fid=" + fid +
                ", Unum='" + Unum + '\'' +
                ", Pname='" + Pname + '\'' +
                ", Intime='" + Intime + '\'' +
                ", Outtime='" + Outtime + '\'' +
                ", Pay='" + Pay + '\'' +
                '}';
    }
}
