package com.example.prmgclient.engine;

import com.example.prmgclient.bean.User;

/**
 * Created by 123 on 2016/11/17.
 */

public interface UserEngine {
    //登陆验证
    public boolean login(String username,String password) throws  Exception;
    //注册验证
    public boolean register(User user)throws  Exception;
    //根据用户名查找用户
    public User findUserByName(String name) throws  Exception;
}
