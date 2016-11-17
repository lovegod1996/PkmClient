package com.example.prmgclient.engine;

import com.alibaba.fastjson.JSON;
import com.example.prmgclient.ConstantValue;
import com.example.prmgclient.bean.User;
import com.example.prmgclient.net.HttpClientUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 2016/11/17.
 */

public class UserEngineImpl implements UserEngine {
    @Override
    public boolean login(String username, String password) throws Exception {

        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("username",username);
        params.put("password",password);
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.LOGIN,params);

        JSONObject object=new JSONObject(json);

        if(checkError(object)){
            String loginState=object.getString("loginState");
            if(loginState.equals("true")){
                return  true;
            }else{
                return  false;
            }
        }
        return false;
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
    public boolean register(User user) throws Exception {

        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("username",user.getPnum());
        params.put("password",user.getPasw());
        params.put("pname",user.getPname());
        params.put("carnum",user.getCnum());
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.REGISTER,params);

        JSONObject object=new JSONObject(json);

        if(checkError(object)){
            String registerState=object.getString("registerState");
            if(registerState.equals("true")){
                return  true;
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public User findUserByName(String name) throws Exception {
        HttpClientUtil util=new HttpClientUtil();
        Map<String,Object> params=new HashMap<String,Object>();
        params.put("username",name);
        String json=util.sendPost(ConstantValue.COMMON+ConstantValue.FINDUSERBYNAME,params);
        JSONObject object=new JSONObject(json);
         User user=null;
        if(checkError(object)){
          String userStr=object.getString("user");
           user= JSON.parseObject(userStr,User.class);
            return user;
        }
        return null;
    }
}
