package com.example.prmgclient.bean;

import java.io.Serializable;

/**
 * Author: lovegod
 * Created by 123 on 2016/11/10.
 */
public class ParkName implements Serializable {
    private static final long serialVersionUID = 1L;
    private String Pname;

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }
}
