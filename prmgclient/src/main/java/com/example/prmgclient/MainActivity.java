package com.example.prmgclient;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.bean.ParkName;
import com.example.prmgclient.engine.ParkEngineImpl;
import com.example.prmgclient.engine.RecordEngineImpl;
import com.example.prmgclient.util.GetlocationJson;
import com.example.prmgclient.util.WifiAutoConnectManager;
import com.example.prmgclient.view.account.Account_information;
import com.example.prmgclient.view.inorout.GateInOut;
import com.example.prmgclient.view.park.ParkInformation;
import com.example.prmgclient.view.record.PayMoney;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {
    //主界面四个主要按钮
    ImageView image_park_information;
    ImageView image_gate_in_out;
    ImageView imagepay_money;
    ImageView imagefind_car;

    WifiManager wifiManager;
    WifiAutoConnectManager wac;//
    Bitmap bitmap;
    WifiInfo wifiInfo;

    static SharedPreferences sp;
    static String name_number;
    static String wifiname;

    View weather_re;
    private ImageView img;
    private TextView textview1;
    private TextView wrong;
    private String cityname;
    private TextView weatherDespText;    //用于显示天气描述信息
    private TextView temp1Text;          //用于显示最低气温
    private TextView temp2Text;          //用于显示最高气温
    private TextView currentDateText;    //用于显示当前日期
    private ImageView imgday;
    private ImageView imgnight;
    private TextView temp1_1;
    private TextView current_date2;
    private Button back;
    private Button other;
    private TextView title;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
private ProgressDialog progressDialog;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==7){
                progressDialog.dismiss();
                Bundle bundle=msg.getData();
                String intime=msg.obj.toString();
                Intent intent=new Intent(MainActivity.this, GateInOut.class);
                intent.putExtras(bundle);
                intent.putExtra("inttime",intime);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        init();
        image_park_information.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ParkEngineImpl parkEngineImpl = new ParkEngineImpl();
                try {
                    List<ParkName> parklist = parkEngineImpl.getParkNameList();
                    Intent intent = new Intent(MainActivity.this, ParkInformation.class);
                    // intent.putExtra("parklist", parklist);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parklist", (Serializable) parklist);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /**
         * 获取停车记录
         */
        imagepay_money.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(MainActivity.this,PayMoney.class);
                startActivity(intent);
            }
        });

        other.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Account_information.class);
                startActivity(intent);
            }
        });
        /**
         * 进出场模块
         */
        image_gate_in_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = getSharedPreferences("userInfo", 0);
                //取出手机号数据
                name_number = sp.getString("USER_NAME",null);
                String wifipwd="";//定义连接wifimima

                if(isWifiOpen()){
                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    wifiInfo = wifiManager.getConnectionInfo();
                    wifiname=getWifiName();  //获取应该连接的wifi名，根据预设的wifi阀值
                    if(isWifiConnect()){

                    String nowwifi=getConnectWifiSsid().replaceAll("\"", "");   ///获取当前连接的wifi名
                    if(wifiname==null){
                        Toast toast = Toast.makeText(MainActivity.this, "未找到正确wifi", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }else{   //获取到应该连接wifi名
                        if(wifiname.equals(nowwifi)){  //当前连接wifi名与应该连接的wifi名相同
                            if(wifiInfo.getRssi()>-50){
                                //执行进场操作
                               //所要执行的操作内容：查询停车场信息，查询停车记录
                                progressDialog= ProgressDialog.show(MainActivity.this,"请稍候","获取数据中..",true);
                                 new Thread(){
                                     @Override
                                     public void run() {
                                         ParkEngineImpl parkEngineImpl=new ParkEngineImpl();
                                         RecordEngineImpl recordEngineImpl=new RecordEngineImpl();
                                         try {
                                             ParkDetail parkDetail=parkEngineImpl.getParDetailkByWifiname(wifiname);
                                             String intime=recordEngineImpl.getInTime(name_number);

                                             Message msg=new Message();
                                             msg.what=7;
                                             Bundle bundle=new Bundle();
                                             bundle.putSerializable("parkdetail",parkDetail);
                                             msg.setData(bundle);
                                             msg.obj=intime;
                                             handler.sendMessage(msg);
                                         } catch (Exception e) {
                                             e.printStackTrace();
                                         }

                                     }
                                 }.start();
                            }else{
                                Toast toast1 = Toast.makeText(MainActivity.this, "Wifi 强度值不够，请再向前行驶", Toast.LENGTH_SHORT);
                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                            }
                        }else{//连接应该连接的wifi
                            wac.connect(wifiname, ConstantValue.WIFIPSD,ConstantValue.WIFIPSD.equals("")? WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS: WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
                            if(isWifiConnect()){
                                Toast toast1 = Toast.makeText(MainActivity.this, "连接wifi成功！，请重试..", Toast.LENGTH_SHORT);
                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                            }

                        }

                    }
                    }else{
                        wac.connect(wifiname, ConstantValue.WIFIPSD,ConstantValue.WIFIPSD.equals("")? WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS: WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA);
                        if(isWifiConnect()){
                            Toast toast1 = Toast.makeText(MainActivity.this, "连接wifi成功！，请重试..", Toast.LENGTH_SHORT);
                            toast1.setGravity(Gravity.CENTER, 0, 0);
                            toast1.show();
                        }
                    }
                }else{
                    //预设请求是否打开wifi
                    wifiManager.setWifiEnabled(true);//打开wifi模块

                    Toast toast = Toast.makeText(MainActivity.this, "wifi已打开！，请重试..", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {

        //初始化主界面按钮
        image_park_information = (ImageView) findViewById(R.id.image_park_information);
        image_gate_in_out = (ImageView) findViewById(R.id.image_gate_in_out);
        imagepay_money = (ImageView) findViewById(R.id.image_pay_money);
        imagefind_car = (ImageView) findViewById(R.id.image_find_car);

        weather_re = findViewById(R.id.weather_re);
        weather_re.getBackground().setAlpha(150);//背景半透明

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wac = new WifiAutoConnectManager(wifiManager);

        wrong = (TextView) findViewById(R.id.wrong);
        current_date2 = (TextView) findViewById(R.id.current_date2);
        textview1 = (TextView) findViewById(R.id.weather);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        imgday=(ImageView) findViewById(R.id.imageday);
        imgnight=(ImageView) findViewById(R.id.imagenight);
        temp1_1=(TextView) findViewById(R.id.temp1_1);


        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.parking_info);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.in_out);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.mipmap.payment_money1);
        Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.mipmap.find_car_navigation);

        image_park_information.setImageBitmap(bitmap1);
        image_gate_in_out.setImageBitmap(bitmap2);
        imagepay_money.setImageBitmap(bitmap3);
        imagefind_car.setImageBitmap(bitmap4);

        back = (Button) findViewById(R.id.button_back);
        other = (Button) findViewById(R.id.button_other);
        back.setVisibility(GONE);

        if (NetIsConnect(MainActivity.this)) {
            if (this.GpsIsOpen()) {
                //Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();

                String addJson = this.GetLocation();

                JSONObject adjson;
                try {
                    adjson = new JSONObject(addJson);
                    JSONObject resultJson = adjson.getJSONObject("result");
                    String address = resultJson.getString("formatted_address");
                    System.out.println(address);
                    JSONObject detail = resultJson.getJSONObject("addressComponent");
                    String city = detail.getString("city");
                    String district = detail.getString("district");
                    String province = detail.getString("province");
                    String street = detail.getString("street");
                    String streetnum = detail.getString("street_number");
                    cityname = district;
                    textview1.setText(district);//---------------------------------------------
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String ccode = null;
                try {
                    ccode = URLEncoder.encode(cityname);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ccode != null) {
                    String weatherUrl = "http://api.map.baidu.com/telematics/v3/weather?location=" + ccode + "&output=json&ak=MPDgj92wUYvRmyaUdQs1XwCf";

                    String weatherJson = queryStringForGet(weatherUrl);
                    System.out.println(weatherJson);

                    JSONObject jsonweaObject;
                    JSONObject jobweather = null;

                    try {
                        jsonweaObject = new JSONObject(weatherJson);
                        JSONArray jarrywea = jsonweaObject.getJSONArray("results");
                        JSONObject jsondatawea = jarrywea.getJSONObject(0);
                        JSONArray jarrywee = jsondatawea.getJSONArray("weather_data");
                        jobweather = jarrywee.getJSONObject(0);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
    /*
	 * 显示天气信息在界面上
	 */
                    try {
                        //current_date2.setText(jsondatawea.getString("results"));//---------------------------------------------
                        //	currentDateText.setText(jobweather.getString("date"));
                        ArrayList<String> Pinfo = new ArrayList<String>();
                        Pinfo = SplitS(jobweather.getString("date"));
                        try {
                            currentDateText.setText(Pinfo.get(0));
                            current_date2.setText(Pinfo.get(1).replace("实时：", " ").replace(")", " "));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        weatherDespText.setText(jobweather.getString("weather"));
                        //获取图片链接显示

                        try {
                            byte[] data = getImage(jobweather.getString("dayPictureUrl"));
                            if (data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                                if(bitmap==null){
                                    System.out.println("获取团片数据为空");
                                    bitmap=BitmapFactory.decodeStream(getStream(jobweather.getString("dayPictureUrl")));
                                }
                                imgday.setImageBitmap(bitmap);// display image
                            } else {
                                Toast.makeText(MainActivity.this, "Image error!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                              Toast.makeText(MainActivity.this,"Network error!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        //获取图片链接显示

                        try {
                            byte[] data = getImage(jobweather.getString("nightPictureUrl"));
                            if (data != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                                if(bitmap==null){
                                    System.out.println("获取团片数据为空");
                                    bitmap=BitmapFactory.decodeStream(getStream(jobweather.getString("nightPictureUrl")));
                                }
                                imgnight.setImageBitmap(bitmap);// display image
                            } else {
                                  Toast.makeText(MainActivity.this, "Image error!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                             Toast.makeText(MainActivity.this,"Network error!", Toast.LENGTH_SHORT).show();
                             e.printStackTrace();
                        }
                        temp1Text.setText(jobweather.getString("temperature"));
                        temp2Text.setText(jobweather.getString("wind"));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            } else {
               temp1_1.setVisibility(View.GONE);
                wrong.setText("请开启GPS！");

                //Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
            }
        } else {
            temp1_1.setVisibility(View.GONE);
            wrong.setText("请开启数据！");
            //Toast.makeText(this, "请开启数据！", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 网络查询
     */
    public static String queryStringForGet(String url) {
        HttpGet request = new HttpGet(url);
        String result = null;
        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                return result;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据连接获取输入流
     * @param path
     * @return
     */
    public  static InputStream getStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == 200) {
            return inStream;
        }
        return  null;
}
    /**
     * Get image from newwork
     *
     * @param path The path of image
     * @return
     * @throws Exception
     */
    public static byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == 200) {
            return readStream(inStream);
        }
        return null;
    }

    public static ArrayList<String> SplitS(String str) {
        String[] ss = str.split("\\(");
        ArrayList<String> list = new ArrayList<String>();
        for (String string : ss) {
            list.add(string);
        }
        return list;
    }

    /**
     * Get data from stream
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    //返回将要连接得wifi名
    private String getWifiName() {
        WifiManager wifiMg = (WifiManager) getSystemService(WIFI_SERVICE);

        List<ScanResult> list = wifiMg.getScanResults();
        String wifiname = null;
        if (list != null) {
            System.out.println("wifi数量： " + list.size());
            for (ScanResult scanResult : list) {
                System.out.println(scanResult.SSID + " " + scanResult.level);
                if (scanResult.level > -100) {
                    if (scanResult.SSID.contains("god")) {
                        wifiname = scanResult.SSID;
                    }
                }
            }
        }
        return wifiname;
    }

 /*
     *判断gps是否打开
     */

    public Boolean GpsIsOpen() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    private String GetLocation() {
        String cityName = "";
        // 获取位置管理服务
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // 低功耗

        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息

        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        if (location == null) {
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            System.out.println("网络位置" + location);

        }
        if (location != null) {
            String latitude = location.getLatitude() + "";
            String longitude = location.getLongitude() + "";
            GetlocationJson gl = new GetlocationJson();
            cityName = gl.GetAddr(latitude, longitude);
        }
        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        locationManager.requestLocationUpdates(provider, 100 * 1000, 500,
                locationListener);
        return cityName;
    }

    /**
     * 方位改变时触发，进行调用
     */
    private final static LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

     /*
     * 判断网络是否打开
     */

    public boolean NetIsConnect(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //返回当前连接得wifi名
    private String getConnectWifiSsid() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }
    //判断是否成功连上wifi

    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    /**
     * 检查wifi模块是否开启
     * @return
     */
    public boolean isWifiOpen(){
        WifiManager wifimanager;
        wifimanager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(wifimanager.isWifiEnabled()){
            return  true;
        }else{
            return  false;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
