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
    //获取停车场列表
    public List<ParkDetail> getParkDetailList()throws Exception;
   //根据停车场名获取停车场详细信息
    public ParkDetail getParkDetailByName(String Pname) throws  Exception;
   //根据wifi名获取停车场
    public ParkDetail getParDetailkByWifiname(String wifiname) throws  Exception;
//车辆进场车位减少
    public boolean updateIn(String parkname) throws  Exception;
    //车辆出场车位加一
    public boolean updateout(String parkname)throws  Exception;
}
