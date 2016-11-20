package com.example.prmgclient.engine;

import com.example.prmgclient.bean.Record;

import java.util.List;

/**
 * Created by 123 on 2016/11/17.
 */

public interface RecordEngine {
    //返回查询到的记录列表
    public List<Record> getListRecord(String name) throws  Exception;
    //添加记录到记录表
public boolean getIn(String username,String parkname,String intime) throws  Exception;
    //更新记录到记录表
public boolean updateFee(String username,Double pay,String outtime) throws Exception;
    //获取进场时间
public String getInTime(String username)throws Exception;
}
