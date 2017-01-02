package com.example.prmgclient;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * *****************************************
 * Created by thinking on 2016/12/30.
 * 创建时间：
 * <p>
 * 描述：
 * <p/>
 * <p/>
 * *******************************************
 */

public class MyApplication extends Application {

    // 存储应用锁打开的Activity，方便退出应用的时候回收
    private List<Activity> mList = new LinkedList<Activity>();
    // 单例模式的入口
    private static MyApplication instance;
    public MyApplication(){}
    //得到当前实例，必须使用synchronized关键字,防止实例化多个对象
    //这是一个static函数，可以通过类名应用，无需实例化，在登陆界面中就引用了该函数（通过类名引用的）
    public synchronized static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }
    // 启动一个Activity的时候记录当前Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    // 程序退出的方法，退出所有Activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    //当资源（内存）不够用时，启动垃圾回收机制，
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();//启动系统VM垃圾回收线程
    }
}
