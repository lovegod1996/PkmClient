package com.example.prmgclient.engine;

import com.alibaba.fastjson.JSON;
import com.example.prmgclient.ConstantValue;
import com.example.prmgclient.bean.Record;
import com.example.prmgclient.net.HttpClientUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2016/11/17.
 */

public class RecordEngineImpl implements RecordEngine {
    @Override
    public List<Record> getListRecord(String name) throws Exception {
        List<Record> recordList=null;

        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("username",name);
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.RECORD,params);
        JSONObject object=new JSONObject(json);
        if(checkError(object)){
            String recordListStr=object.getString("recordlist");

            recordList= JSON.parseArray(recordListStr,Record.class);

            return  recordList;
        }
        return null;
    }


    private boolean checkError(JSONObject object) {
        try {
            String response  =object.getString("response");
            if(ConstantValue.ERROR.equals(response)){
                return  false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  true;

    }
}
