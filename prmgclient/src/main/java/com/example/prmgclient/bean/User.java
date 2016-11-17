package com.example.prmgclient.bean;

import java.io.Serializable;

/**
 * Author: lovegod
 * Created by 123 on 2016/11/9.
 */
public class User implements Serializable{
   private int UID;
    private String Pnum;
    private String Pasw;
    private String Pname;
    private String CType;
    private String Cnum;
    private String RID;

    public User() {
        super();
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getPnum() {
        return Pnum;
    }

    public void setPnum(String pnum) {
        Pnum = pnum;
    }

    public String getPasw() {
        return Pasw;
    }

    public void setPasw(String pasw) {
        Pasw = pasw;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getCType() {
        return CType;
    }

    public void setCType(String CType) {
        this.CType = CType;
    }

    public String getCnum() {
        return Cnum;
    }

    public void setCnum(String cnum) {
        Cnum = cnum;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }


    @Override
    public String toString() {
        return "User{" +
                "UID=" + UID +
                ", Pnum='" + Pnum + '\'' +
                ", Pasw='" + Pasw + '\'' +
                ", Name='" + Pname + '\'' +
                ", CType='" + CType + '\'' +
                ", Cnum='" + Cnum + '\'' +
                ", RID='" + RID + '\'' +
                '}';
    }

}
