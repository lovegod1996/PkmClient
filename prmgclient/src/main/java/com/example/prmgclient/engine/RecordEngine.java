package com.example.prmgclient.engine;

import com.example.prmgclient.bean.Record;

import java.util.List;

/**
 * Created by 123 on 2016/11/17.
 */

public interface RecordEngine {
    //返回查询到的记录列表
    public List<Record> getListRecord(String name) throws  Exception;
}
