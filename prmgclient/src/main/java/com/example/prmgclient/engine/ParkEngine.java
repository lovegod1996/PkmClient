package com.example.prmgclient.engine;

import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.bean.ParkName;

import java.util.List;

/**
 * Created by 123 on 2016/11/10.
 */

public interface ParkEngine {
    //获取停车场名称列表
    public List<ParkName> getParkNameList() throws  Exception;
   //根据停车场名获取停车场详细信息
    public ParkDetail getParkDetailByName(String Pname) throws  Exception;

}
