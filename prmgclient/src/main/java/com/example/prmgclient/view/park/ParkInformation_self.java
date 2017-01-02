package com.example.prmgclient.view.park;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.prmgclient.MyApplication;
import com.example.prmgclient.R;
import com.example.prmgclient.bean.ParkDetail;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by 123 on 2016/11/10.
 */
public class ParkInformation_self extends Activity {
    TextView t_title;//标题栏
    ImageView other;
    ImageView back;
    TextView p_name;
    TextView p_lot;
    TextView p_address;
    TextView p_phone;
    TextView p_fee;

    Location location;
    ParkDetail parkDetail;
    /**
     * 以下为导航相关配置
     */
    public static List<Activity> activityList = new LinkedList<Activity>();
    /**
     * App在SD卡中的目录名
     */
    private static final String APP_FOLDER_NAME = "intvehapp";

    /**
     * 导航按钮
     */
    Button btn_daohang;
    /**
     * SD卡的路径
     */
    private String mSDCardPath = null;

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_info_one);
        MyApplication.getInstance().addActivity(this);
        init();
        activityList.add(this);

        if (initDirs()) {
            /**
             * 使用SDK前，先进行百度服务授权和引擎初始化。
             */
            initNavi();
        }
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

        location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            System.out.println("网络位置" + location);
        }
        if (location == null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || !locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                /**
                 *
                 * 提示用户打开GPS
                 */
                AlertDialog.Builder builderdialog = new AlertDialog.Builder(this);
                builderdialog.setMessage("请打开GPS");
                builderdialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
                    }
                });
                builderdialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                builderdialog.show();
            }
//            Intent gpsIntent = new Intent();
//            gpsIntent.setClassName("com.Android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
//            gpsIntent.setData(Uri.parse("custom:3"));
//            try {
//                PendingIntent.getBroadcast(ParkInformation_self.this, 0, gpsIntent, 0).send();
//            } catch (PendingIntent.CanceledException e) {
//                e.printStackTrace();
//            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    String authinfo = null;

    private void initNavi() {
        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {

                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                ParkInformation_self.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ParkInformation_self.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                btn_daohang.setVisibility(View.VISIBLE);
                Toast.makeText(ParkInformation_self.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
                Toast.makeText(ParkInformation_self.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(ParkInformation_self.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, null);
    }

    /**
     * 导航设置管理器
     */
    private void initSetting() {
//        /**
//         * 日夜模式 1：自动模式 2：白天模式 3：夜间模式
//         */
//        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
//        /**
//         * 设置全程路况显示
//         */
//        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
//        /**
//         * 设置语音播报模式
//         */
//        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
//        /**
//         * 设置省电模式
//         */
//        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
//        /**
//         * 设置实时路况条
//         */
//        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    /**
     * 初始化SD卡，在SD卡路径下新建文件夹：App目录名，文件中包含了很多东西，比如log、cache等等
     *
     * @return
     */
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 获取sd卡
     *
     * @return
     */
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void showToastMsg(final String s) {
        ParkInformation_self.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ParkInformation_self.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        p_name = (TextView) findViewById(R.id.p_name);
        p_lot = (TextView) findViewById(R.id.p_lot);
        p_address = (TextView) findViewById(R.id.p_address);
        p_phone = (TextView) findViewById(R.id.p_phone);
        p_fee = (TextView) findViewById(R.id.p_fee);

        btn_daohang = (Button) findViewById(R.id.btn2);
        btn_daohang.setVisibility(View.GONE);

        t_title = (TextView) findViewById(R.id.text_title);
        other = (ImageView) findViewById(R.id.button_other);
        other.setVisibility(View.GONE);
        back = (ImageView) findViewById(R.id.button_back);

        parkDetail = (ParkDetail) this.getIntent().getSerializableExtra("parkdetail");
        t_title.setText(parkDetail.getPname());
        p_name.setText(parkDetail.getPname());//-----------------------------
        p_address.setText(parkDetail.getAdress());
        p_fee.setText(parkDetail.getPayCri());
        p_phone.setText(parkDetail.getPhone());
        p_lot.setText(parkDetail.getParking_left());

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        btn_daohang.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
//                double[] end={parkDetail.getLatitude(),parkDetail.getLongitude()};
//                Intent intent = new Intent(ParkInformation_self.this, Park_baidu_Map.class);
//               // Bundle bundle = new Bundle();
//              //  bundle.putSerializable("end", end);
//                intent.putExtra("end",end);
//                startActivity(intent);
                System.out.println("导航目标 ：" + parkDetail.getLongitude() + "  " + parkDetail.getLatitude());
                /**
                 * 判断百度导航是否初始化
                 */
                if (BaiduNaviManager.isNaviInited()) {
                    /**
                     * 添加起点、终点
                     */
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.WGS84);
                }
            }
        });


    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
                sNode = new BNRoutePlanNode(location.getLongitude(), location.getLatitude(), "我的位置", null, coType);
                eNode = new BNRoutePlanNode(parkDetail.getLongitude(), parkDetail.getLatitude(), parkDetail.getPname(), null, BNRoutePlanNode.CoordinateType.BD09LL);
                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            /**
             * 发起算路操作并在算路成功后通过回调监听器进入导航过程,返回是否执行成功
             */
            BaiduNaviManager
                    .getInstance()
                    .launchNavigator(
                            this,                            //建议是应用的主Activity
                            list,                            //传入的算路节点，顺序是起点、途经点、终点，其中途经点最多三个
                            1,                                //算路偏好 1:推荐 8:少收费 2:高速优先 4:少走高速 16:躲避拥堵
                            true,                            //true表示真实GPS导航，false表示模拟导航
                            new DemoRoutePlanListener(sNode)//开始导航回调监听器，在该监听器里一般是进入导航过程页面
                    );
        }
    }


    private class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {
        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode sNode) {
            mBNRoutePlanNode = sNode;
        }

        @Override
        public void onJumpToNavigator() {
/*
             * 设置途径点以及resetEndNode会回调该接口
			 */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("ParkGuideActivity")) {
                    return;
                }
            }
            /**
             * 导航activity
             */
            Intent intent = new Intent(ParkInformation_self.this, ParkGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(ParkInformation_self.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };
}
