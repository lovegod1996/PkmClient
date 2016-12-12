package com.example.prmgclient.util;

import android.app.ProgressDialog;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

/**
 * Created by 123 on 2016/11/23.
 */

public class CheckThresholdAsyncTask extends AsyncTask <Integer,Integer,Integer>{
  private WifiManager wifiManager;
    private ProgressDialog progressDialog;

    public CheckThresholdAsyncTask(WifiManager wifiManager,ProgressDialog progressDialog){
        super();
        this.wifiManager=wifiManager;
        this.progressDialog=progressDialog;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        System.out.println("正在执行异步"+integers[0]);
        CheckThreshold checkThreshold=new CheckThreshold(wifiManager);
        int rs=0-integers[0];
        System.out.println(rs);
        if(rs>50){
            System.out.println("正在执行异步 延时");
            checkThreshold.delay();
            return rs;
        }else{
            return rs;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        System.out.println("异步操作完成");
        progressDialog.dismiss();
    }
}
