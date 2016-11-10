package com.example.prmgclient.engine;

import com.alibaba.fastjson.JSON;
import com.example.prmgclient.ConstantValue;
import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.bean.ParkName;
import com.example.prmgclient.net.HttpClientUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2016/11/10.
 */

public class ParkEngineImpl implements ParkEngine {
    @Override
    public List<ParkName> getParkNameList() throws Exception {
        List<ParkName> parkList=null;

        HttpClientUtil util=new HttpClientUtil();
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.PARKLIST,null);

//数据处理，检验数据是否回复正常
        try {
            JSONObject object=new JSONObject(json);
            if(checkError(object)){
                //幫助數據處理
                parkList=new ArrayList<ParkName>();
            //    String helpListstr=  object.getString("helplist");//[{....},{.....}]
                String parkListStr=object.getString("parklist");
                /**
                 * 使用alibaba。jar包控件
                 */
                parkList= JSON.parseArray(parkListStr,ParkName.class);

                //持久化到本地
                //如果數據量過大，開啓子綫程完成數據擦歐縂


                return  parkList;
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return parkList;
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

    @Override
    public ParkDetail getParkDetailByName(String Pname) throws Exception {
            ParkDetail parkDetail=null;

        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("pname",Pname);
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.PARKDETAIL,params);

        //数据处理，检验数据是否回复正常
        try {
            JSONObject object=new JSONObject(json);
            if(checkError(object)){
                //幫助數據處理
                parkDetail=new ParkDetail();
                String parkDetailstr=  object.getString("helplist");//[{....},{.....}]
                /**
                 * 使用alibaba。jar包控件
                 */
                parkDetail= JSON.parseObject(parkDetailstr,ParkDetail.class);

                //持久化到本地
                //如果數據量過大，開啓子綫程完成數據擦歐縂


                return  parkDetail;
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
