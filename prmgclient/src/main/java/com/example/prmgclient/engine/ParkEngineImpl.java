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
        System.out.println("查询停车场名："+Pname);
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.PARKDETAIL,params);

        //数据处理，检验数据是否回复正常
        try {
            JSONObject object=new JSONObject(json);
            if(checkError(object)){
                //幫助數據處理
                System.out.println(object);
                parkDetail=new ParkDetail();
                String parkDetailstr=  object.getString("parkdetail");//[{....},{.....}]
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

    @Override
    public ParkDetail getParDetailkByWifiname(String wifiname) throws Exception {
        ParkDetail parkDetail=null;

        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("wifiname",wifiname);
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.FINDPARKBYWIFI,params);
        JSONObject object=new JSONObject(json);
        if(checkError(object)){
            parkDetail=new ParkDetail();
            String parkDetailStr=object.getString("parkdetail");
            parkDetail= JSON.parseObject(parkDetailStr,ParkDetail.class);


            return  parkDetail;
        }

        return null;
    }

    @Override
    public boolean updateIn(String parkname) throws Exception {
        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("parkname",parkname);

        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.UPDATEPARKIN,params);
        JSONObject object=new JSONObject(json);
        if(checkError(object)){
            String updateState=object.getString("updateState");
            if(updateState.equals("true")){
                return true;
            }else{
                return false;
            }
        }else{

        }
        return false;
    }

    @Override
    public boolean updateout(String parkname) throws Exception {

        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("parkname",parkname);

        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.UPDATEPARKOUT,params);
        JSONObject object=new JSONObject(json);
        if(checkError(object)){
            String updateState=object.getString("updateState");
            if(updateState.equals("true")){
                return true;
            }else{
                return false;
            }
        }else{

        }
        return false;
    }
}
