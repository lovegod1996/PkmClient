package com.example.prmgclient.view.map;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingTrafficPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.prmgclient.MyApplication;
import com.example.prmgclient.R;
import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.util.HCache;
import com.example.prmgclient.view.park.ParkInformation_self;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Park_baidu_Map extends Activity implements OnGetRoutePlanResultListener {
    DistanceUtil distanceUtil;//测距工具
    LatLng center;//定位点的坐标
    BDLocation location;//回调的百度坐标类
    public MyLocationListenner myListener = new MyLocationListenner();
    MapView bmapview = null;
    BaiduMap mbaidmap;
    ImageView realtime_traffic;//实时交通
    ImageView satellite_map;//卫星图
    ImageView location_map;//定位图


    boolean isFirstLoc = true; // 是否首次定位
    //对话框
    RelativeLayout park_info_relativelayout;//对话框停车场信息布局
    TextView dialog_park_name;//对话框停车场名称
    TextView dialog_park_distance;//对话框停车场距离
    TextView dialog_park_position;//对话框停车场位置信息
    Button dialog_park_navigation;//对话框导 航

    // 定位相关
    LocationClient mLocClient;//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    // 搜索相关
    RoutePlanSearch RPSearch = null;
    DrivingRouteLine Resultdriving = null;
    DrivingRouteResult nowResultdrive = null;
    boolean useDefaultIcon = false;
    OverlayManager routeOverlay = null;
    RouteLine route = null;
    final ArrayList<String> arr_node = new ArrayList<String>();
    ListView listview1;
    ArrayList<String> arr1 = new ArrayList<String>();
    ArrayList<String> arr2 = new ArrayList<String>();
    public ArrayAdapter<String> adapter = null;
    MarkerOptions optionA;

    Dialog parkDialog;
    LatLng markerlatlog = null;
    List<ParkDetail> parkDetailList;
    //基于gps基础相关，可删除
    private LocationManager locationManager;
    private String provider;
    double[] end;

    //获取缓存数据
    HCache mcahe;

    /*TextView t_title;
    ImageView other;
    ImageView back;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.park_baidumap);
        MyApplication.getInstance().addActivity(this);
        mcahe = HCache.get(this);
        try {
            parkDetailList = (List<ParkDetail>) this.getIntent().getSerializableExtra("parkDetailList");
        } catch (Exception e) {
            if (mcahe.getString("ParkDetailListJson")) {
                String parkDetailListJson = mcahe.getAsString("ParkDetailListJson");
                org.json.JSONObject object = null;
                try {
                    object = new org.json.JSONObject(parkDetailListJson);
                    String recordListstr = object.getString("ParkDetailListJson");
                    parkDetailList = JSON.parseArray(recordListstr, ParkDetail.class);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
/*ActionBar actionBar=getActionBar();
        actionBar.setTitle("查找停车场");*/

        mCurrentMode = LocationMode.NORMAL;
        bmapview = (MapView) findViewById(R.id.bmapView);

        listview1 = (ListView) findViewById(R.id.listview1);//列表
        mbaidmap = bmapview.getMap();

        // 开启定位图层
        mbaidmap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mbaidmap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        option.setOpenAutoNotifyMode(5000,5,LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //  option.setIgnoreKillProcess(true);
        option.setIsNeedLocationPoiList(true);

        mLocClient.setLocOption(option);
        mLocClient.start();      //调用这个方法进行定位

        System.out.println(option.getScanSpan());
        System.out.println(option.getLocationMode());



        init();//对话框and三图
        OnClickListener btn3ClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 实时交通图|卫星图|定位在中心

                switch (view.getId()) {
                    case R.id.realtime_traffic:
                        if (mbaidmap.isTrafficEnabled() == false) {
                            mbaidmap.setTrafficEnabled(true);
                            System.out.println("实时交通图");
                            realtime_traffic.setImageResource(R.mipmap.shishi122);
                            Toast.makeText(Park_baidu_Map.this, "实时交通图已开启", Toast.LENGTH_SHORT).show();
                        } else {
                            mbaidmap.setTrafficEnabled(false);
                            realtime_traffic.setImageResource(R.mipmap.shishi22);
                            Toast.makeText(Park_baidu_Map.this, "实时交通图已关闭", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.satellite_map:
                        if (mbaidmap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                            mbaidmap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                            satellite_map.setImageResource(R.mipmap.map_star);
                            Toast.makeText(Park_baidu_Map.this, "卫星图已开启", Toast.LENGTH_SHORT).show();
                        } else {
                            mbaidmap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                            satellite_map.setImageResource(R.mipmap.map_star);
                            Toast.makeText(Park_baidu_Map.this, "卫星图关闭", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.location_map:
                        if (center != null)
                            mbaidmap.setMapStatus(MapStatusUpdateFactory.newLatLng(center));
                        break;
                    default:
                        break;
                }
            }
        };
        // 初始化搜索模块，注册事件监听
        // RPSearch = RoutePlanSearch.newInstance();
        // RPSearch.setOnGetRoutePlanResultListener(this);

        realtime_traffic.setOnClickListener(btn3ClickListener);
        satellite_map.setOnClickListener(btn3ClickListener);
        location_map.setOnClickListener(btn3ClickListener);

    }


    private void init() {
        // TODO Auto-generated method stub

        realtime_traffic = (ImageView) findViewById(R.id.realtime_traffic);
        satellite_map = (ImageView) findViewById(R.id.satellite_map);
        location_map = (ImageView) findViewById(R.id.location_map);
       /* t_title=(TextView) findViewById(R.id.text_title);
        other = (ImageView) findViewById(R.id.button_other);
        back=(ImageView)findViewById(R.id.button_back);
        t_title.setText("地图导航");
        other.setVisibility(View.INVISIBLE);*/

        //对话框
        parkDialog = new Dialog(this, R.style.map_dialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_map_dialog, null);
        root.findViewById(R.id.park_info_relativelayout).setOnClickListener(dialoglistener);
        root.findViewById(R.id.dialog_park_navigation).setOnClickListener(dialoglistener);
        dialog_park_name = (TextView) root.findViewById(R.id.dialog_park_name);
        dialog_park_distance = (TextView) root.findViewById(R.id.dialog_park_distance);
        dialog_park_position = (TextView) root.findViewById(R.id.dialog_park_position);

        parkDialog.setCanceledOnTouchOutside(true);//点击外部使对话框消失
        parkDialog.setContentView(root);
        Window dialogWindow = parkDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
    }

    //对话框的点击事件
    private OnClickListener dialoglistener = new OnClickListener() {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.park_info_relativelayout:
                    ParkDetail parkDetail = findParkDetailByName(parkDetailList, dialog_park_name.getText().toString().trim());
                    System.out.println("导航发送目标 ："+parkDetail.getLongitude()+ "  "+parkDetail.getLatitude());
                    Intent intent = new Intent(Park_baidu_Map.this, ParkInformation_self.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("parkdetail", parkDetail);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.dialog_park_navigation:
                    ShowNavigationOverlay(markerlatlog);
                    parkDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private ParkDetail findParkDetailByName(List<ParkDetail> parkDetailList, String trim) {
        ParkDetail parkDetail = null;
        for (int i = 0; i < parkDetailList.size(); i++) {
            if (trim.equals(parkDetailList.get(i).getPname())) {
                parkDetail = parkDetailList.get(i);
                return parkDetail;
            }
        }
        return parkDetail;
    }


    private void initmark(LatLng center2) {
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        BitmapDescriptor bd1 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding1);
        BitmapDescriptor bd2 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding2);
        BitmapDescriptor bd3 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding3);
        BitmapDescriptor bd4 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding4);
        BitmapDescriptor bd5 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding5);
        BitmapDescriptor bd6 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding6);
        BitmapDescriptor bd7 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding7);
        BitmapDescriptor bd8 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding8);
        BitmapDescriptor bd9 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding9);
        BitmapDescriptor bd10 = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding10);

        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bd1);
        giflist.add(bd2);
        giflist.add(bd3);
        giflist.add(bd4);
        giflist.add(bd5);
        giflist.add(bd6);
        giflist.add(bd7);
        giflist.add(bd8);
        giflist.add(bd9);
        giflist.add(bd10);
//
//        LatLng llA = new LatLng(34.598552, 113.693076);
//        LatLng llB = new LatLng(34.597930, 113.692857);
//        LatLng llC = new LatLng(34.600023, 113.694204);
//        LatLng llD = new LatLng(34.608374, 113.694178);
        final LatLng[] latLngs = new LatLng[parkDetailList.size()];
        for (int i = 0; i < parkDetailList.size(); i++) {
            latLngs[i] = new LatLng(parkDetailList.get(i).getLatitude(), parkDetailList.get(i).getLongitude());
        }

        Integer[] llsort = new Integer[latLngs.length];
        for (int i = 0; i < latLngs.length; i++) {
            llsort[i] = (int) DistanceUtil.getDistance(latLngs[i], center2);//返回两个点之间的距离
            System.err.println("距离      " + llsort[i]);
        }
        getSort(latLngs, llsort);
        for (int i = 0; i < llsort.length; i++) {
            System.err.println("第" + i + "个距离简单排序：" + llsort[i]);
        }
        // Ascendingsort(latLngs, llsort);//按照距离的远近排序（升序）
//        for(int i=0;i<llsort.length;i++) {
//            System.err.println("第"+i+"个距离："+llsort[i]);
//        }
        List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();

        final List<Marker> markerlist = new ArrayList<Marker>();

//        String[] titlename = {"a停车场", "b停车场", "c停车场", "d停车场", "e停车场"};
        int count = 0;
        for (int i = 0; i < latLngs.length; i++) {
            if (count > 10) {
                break;
            }
            if (llsort[i] <= 2000.0) {
                /**
                 * 判断点pt是否在，以pCenter为中心点，radius为半径的圆内。
                 * SpatialRelationUtil.isCircleContainsPoint(pCenter, radius, pt);
                 */
                ParkDetail parkDetail = getParkByLatlng(parkDetailList, latLngs[i]);
                System.out.println(parkDetail);
                System.err.println("标注点" + i + " " + llsort[i] + "图标标号 " + count);
                System.out.println(DistanceUtil.getDistance(latLngs[i], center2));
                optionA = new MarkerOptions().position(latLngs[i]).icon(giflist.get(count)).title(parkDetail.getPname());
                //在地图上添加Marker，并显示
                Bundle bundle = new Bundle();
                bundle.putSerializable("parkDetail", parkDetail);
                Marker marker1 = (Marker) (mbaidmap.addOverlay(optionA));
                marker1.setExtraInfo(bundle);
                marker1.setPerspective(false);
                markerlist.add(marker1);
                count++;
                arr1.add(count + "、" + optionA.getTitle() + "  距离：" + (int) llsort[i] + "米");
                System.err.println(count + "、" + optionA.getTitle() + "  距离：" + (int) llsort[i] + "米");
            }

        }

        mbaidmap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker1) {
                // TODO Auto-generated method stub
                dialog_park_name.setText(marker1.getTitle());
                dialog_park_distance.setText((int) DistanceUtil.getDistance(marker1.getPosition(), center) + "米");
                Bundle bundle = marker1.getExtraInfo();
                ParkDetail parkDetail = (ParkDetail) bundle.getSerializable("parkDetail");
                dialog_park_position.setText(parkDetail.getAdress());

                parkDialog.show();
                markerlatlog = marker1.getPosition();
                System.out.println(markerlatlog.latitude + "" + markerlatlog.longitude);


                Toast toast = Toast.makeText(Park_baidu_Map.this, marker1.getTitle(), Toast.LENGTH_SHORT);
                toast.show();
                toast.setGravity(Gravity.CENTER, 0, 0);

                System.out.println(marker1.getTitle() + "  " + marker1.getPosition() + "  " + marker1.toString());
                return true;
            }
        });

        //listview1
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Park_baidu_Map.this, R.layout.array_item_map, arr1);
        listview1.setAdapter(adapter);

     /*   if (end.length!= 0) {
            LatLng end_position=new LatLng(end[0],end[1]);
            ShowNavigationOverlay(end_position);
        }*/
        listview1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int i, long arg3) {
                // TODO Auto-generated method stub
                String P_name = markerlist.get(i).getTitle();//得到点击的列表项的值
                Marker marker = markerlist.get(i);
                Bundle bundle = marker.getExtraInfo();
                final ParkDetail parkDetail = (ParkDetail) bundle.getSerializable("parkDetail");

                AlertDialog.Builder builder = new AlertDialog.Builder(Park_baidu_Map.this);
                builder.setTitle(P_name);
                builder.setMessage(parkDetail.getAdress());

                builder.setPositiveButton("导航", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        LatLng latLng = new LatLng(parkDetail.getLatitude(), parkDetail.getLongitude());
                        ShowNavigationOverlay(latLng);
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        // changeListView(adapter);
                        arr1.clear();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //clearClick();
                    }
                });
                builder.show();


            }
        });

    }

    /**
     * 根据提供的经纬度值来找到停车场
     *
     * @param parkDetailList
     * @param latLng
     * @return
     */
    private ParkDetail getParkByLatlng(List<ParkDetail> parkDetailList, LatLng latLng) {
        ParkDetail parkDetail = null;
        for (int i = 0; i < parkDetailList.size(); i++) {
            LatLng ll = new LatLng(parkDetailList.get(i).getLatitude(), parkDetailList.get(i).getLongitude());
            if (latLng.latitude == ll.latitude && latLng.longitude == ll.longitude) {
                parkDetail = parkDetailList.get(i);
                return parkDetail;
            }
        }
        return parkDetail;
    }

    /**
     * 对经纬度值表和距离进行排序
     *
     * @param latLngs
     * @param llsort
     */
    private void getSort(LatLng[] latLngs, Integer[] llsort) {
        for (int i = 0; i < llsort.length - 1; i++) {
            for (int j = i + 1; j < llsort.length; j++) {
                int temp;
                if (llsort[i] > llsort[j]) {
                    LatLng templa = latLngs[j];
                    temp = llsort[j];
                    llsort[j] = llsort[i];
                    latLngs[j] = latLngs[i];
                    llsort[i] = temp;
                    latLngs[i] = templa;
                }
            }
        }
    }

    private void getSort(Integer[] llsort) {
        for (int i = 0; i < llsort.length - 1; i++) {
            for (int j = i + 1; j < llsort.length; j++) {
                int temp;
                if (llsort[i] > llsort[j]) {
                    temp = llsort[j];
                    llsort[j] = llsort[i];
                    llsort[i] = temp;
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     *
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        // TODO Auto-generated method stub

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(Park_baidu_Map.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            // nodeIndex = -1;
            if (result.getRouteLines().size() >= 1) {
                Resultdriving = result.getRouteLines().get(0);
                for (int i = 1; i < Resultdriving.getAllStep().size(); i++) {
                    // System.out.println(Resultdriving.getAllStep().size());
                    final Object step = Resultdriving.getAllStep().get(i);
                    String nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
                    System.out.println(Resultdriving.getAllStep().get(i));
                    System.out.println(nodeTitle);
                    arr_node.add(nodeTitle);
                    System.out.println(arr_node.toString());
                }
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Park_baidu_Map.this, R.layout.array_item, arr_node);
                listview1.setAdapter(adapter2);

                listview1.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int i, long arg3) {
                        // TODO Auto-generated method stub
                        // 移动节点至中心
                        Object aa = Resultdriving.getAllStep().get(i);
                        LatLng nodeLocation = null;
                        try {
                            nodeLocation = ((DrivingRouteLine.DrivingStep) aa).getEntrance().getLocation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                      /* BitmapDescriptor da = BitmapDescriptorFactory.fromResource(R.mipmap.marker2);
                        OverlayOptions option = new MarkerOptions().position(nodeLocation).icon(da);
                        Marker marker_node=(Marker) (mbaidmap.addOverlay(option));
                        marker_node.setPerspective(true);
                        mbaidmap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
                        mbaidmap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));*/
                        mbaidmap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
                        mbaidmap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));

                    }
                });

                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mbaidmap);
                routeOverlay = overlay;
                mbaidmap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }


        }


    }

    private void ShowNavigationOverlay(LatLng latlng) {
        clearClick();// 清除所有图层
        // 初始化搜索模块，注册事件监听
        RPSearch = RoutePlanSearch.newInstance();
        RPSearch.setOnGetRoutePlanResultListener(this);
        PlanNode stNode = PlanNode.withLocation(center);
        PlanNode enNode = PlanNode.withLocation(latlng);
        RPSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode)
                .trafficPolicy(DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC).policy(DrivingPolicy.ECAR_AVOID_JAM));


    }

    /**
     * 清除所有图层
     */
    public void clearClick() {
        // 清除所有图层
        bmapview.getMap().clear();
        //mbaidmap.setMyLocationEnabled(false);
    }

    /**
     * 按照距离的远近排序（升序）
     *
     * @param abcd
     * @param llsort
     */
    private void Ascendingsort(LatLng[] abcd, Integer[] llsort) {
        // TODO Auto-generated method stub
        for (int i = 0; i < abcd.length - 1; i++) {
            for (int j = 0; j < abcd.length - i - 1; i++) {
                if (llsort[j] > llsort[j + 1]) {
                    int temp = llsort[j];
                    LatLng temp2 = abcd[j];

                    llsort[j] = llsort[j + 1];
                    abcd[j] = abcd[j + 1];

                    llsort[j + 1] = temp;
                    abcd[j + 1] = temp2;
                }
            }
        }
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || bmapview == null) {
                System.out.println("kongk");
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            center = new LatLng(locData.latitude, locData.longitude);
            System.out.println(locData.latitude + "  " + locData.longitude);

           // initmark(center);//添加标注点信息
            //    showNearbyArea(center, 1300);//绘制搜索范围

            System.out.println(locData.latitude + "第一个" + locData.longitude);
            mbaidmap.setMyLocationData(locData);


          //  mbaidmap.animateMapStatus(MapStatusUpdateFactory.newLatLng(center));
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                initmark(ll);//添加标注点信息
                System.err.println(ll.latitude + " " + ll.longitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);

                mbaidmap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }

        }

        public void onReceivePoi(BDLocation poiLocation) {
           // System.out.println(poiLocation.getCity() + " " + poiLocation.getAdUrl(getLocalClassName()));
        }
    }


    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        super.onPause();
        bmapview.onPause();
    }

    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        bmapview.onResume();
        super.onResume();
    }

    protected void onDestory() {

        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mbaidmap.setMyLocationEnabled(false);
        bmapview.onDestroy();
        //mPoiSearch.destroy();
        RPSearch.destroy();
        bmapview = null;
        super.onDestroy();
    }

    /**
     * 绘画周围搜索的范围
     *
     * @param center3
     * @param radius
     */
    public void showNearbyArea(LatLng center3, int radius) {

        OverlayOptions ooCircle = new CircleOptions().fillColor(0xCCCCCCCC)
                .center(center3).stroke(new Stroke(5, 0xFFFF00FF))
                .radius(radius);

        mbaidmap.addOverlay(ooCircle);
    }
    // 定制RouteOverly


    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.icon_en);
            }
            return null;
        }
    }


    @Override
    public void onGetBikingRouteResult(BikingRouteResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
        // TODO Auto-generated method stub

    }


}

