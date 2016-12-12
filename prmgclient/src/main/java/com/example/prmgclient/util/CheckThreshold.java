package com.example.prmgclient.util;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by 123 on 2016/11/23.
 */

public class CheckThreshold {
  private  WifiManager     wifiManager ;
    WifiInfo wifiInfo;
    public CheckThreshold(WifiManager wifiManeger) {
        this.wifiManager=wifiManeger;
        wifiInfo=wifiManager.getConnectionInfo();
    }

    public void delay(){
        System.out.println("正在执行异步 延时 当前  "+wifiInfo.getRssi());
       while(wifiInfo.getRssi()<-50){
           try {
               System.out.println("延时部分输出   "+wifiInfo.getRssi());
               Thread.sleep(time(wifiInfo.getRssi()));
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
    }

    private int time(int rssi) {
        int tt;
        int rs=0-rssi;
         tt=16*rs-600;
        return  tt;
    }
}
