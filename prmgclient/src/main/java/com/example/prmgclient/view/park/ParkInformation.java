package com.example.prmgclient.view.park;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.prmgclient.MainActivity;
import com.example.prmgclient.MyApplication;
import com.example.prmgclient.R;
import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.bean.ParkName;
import com.example.prmgclient.engine.ParkEngine;
import com.example.prmgclient.engine.ParkEngineImpl;
import com.example.prmgclient.util.HCache;
import com.example.prmgclient.util.NetWorkUtil;
import com.example.prmgclient.view.map.Park_baidu_Map;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2016/11/10.
 */
public class ParkInformation extends Activity {
    private TextView t_title;
    private ImageView other;
    private ImageView back;
     private Button search;
    private EditText ed_search;
     private ImageView is_search;
     private ListView ListView_park_name;
    private Button go_to_map;

    ArrayList<String> arr1 = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    String editname;
    private ProgressDialog progressDialog;
    private static  List<ParkName> parklist;
    private HCache mcache;
Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 12:
                progressDialog.dismiss();
                Bundle bundle=msg.getData();
                Intent intent=new Intent(ParkInformation.this, ParkInformation_self.class);
                intent.putExtras(bundle);
                 startActivity(intent);
                break;
            case 17:
                progressDialog.dismiss();
                Bundle bundle3 = new Bundle();
                bundle3 = msg.getData();
                Intent intent3 = new Intent(ParkInformation.this, Park_baidu_Map.class);
                intent3.putExtras(bundle3);
                startActivity(intent3);


//                   Toast.makeText(MainActivity.this, "服务器正在维护....", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_info);
        MyApplication.getInstance().addActivity(this);
        mcache=HCache.get(this);
        init();
        addListener();
        ListView_park_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final String pname=arr1.get(i).toString();//得到点击的列表项的值
                progressDialog= ProgressDialog.show(ParkInformation.this,"请稍候","获取数据中..",true);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        ParkEngineImpl parkEngineImpl=new ParkEngineImpl();
                        try {
                            ParkDetail parkDeatil=parkEngineImpl.getParkDetailByName(pname);
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("parkdetail",parkDeatil);

                            Message msg=new Message();
                            msg.what=12;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        });

        /**
         * 进入地图
         */
        go_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetWorkUtil.isNetworkConnected(ParkInformation.this)) {
                    progressDialog = ProgressDialog.show(ParkInformation.this, "请稍候", "获取数据中..", true);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (MainActivity.isConnByHttpServer()) {
                                ParkEngine parkEngine = new ParkEngineImpl();
                                try {
                                    List<ParkDetail> parkDetailList = parkEngine.getParkDetailList();
                                    //封装json
                                    Map<String, Object> data = new HashMap<String, Object>();
                                    data.put("parkDetailList", parkDetailList);
                                    String ParkDetailListJson = com.alibaba.fastjson.JSONObject.toJSONString(data);

                                    if (mcache.getString("ParkDetailListJson")) {
                                        mcache.remove("ParkDetailListJson");
                                    }
                                    mcache.put("ParkDetailListJson", ParkDetailListJson);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("parkDetailList", (Serializable) parkDetailList);
                                    Message msg = new Message();
                                    msg.what = 17;
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if(mcache.getString("ParkDetailListJson")) {
                                    String ParkDetailListJson = mcache.getAsString("ParkDetailListJson");
                                    try {
                                        org.json.JSONObject object = new org.json.JSONObject(ParkDetailListJson);
                                        String recordListstr = object.getString("ParkDetailListJson");
                                        List<ParkDetail> parkDetailList = JSON.parseArray(recordListstr, ParkDetail.class);
                                        Message msg = new Message();
                                        msg.what = 17;
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("parkDetailList", (Serializable) parkDetailList);
                                        msg.setData(bundle);
                                        handler.sendMessage(msg);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{

                                }

                            }
                        }
                    }.start();
                } else {

                    Toast toast1 = Toast.makeText(ParkInformation.this, "请检查网络。。。。", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                }
            }
        });
    }

    private void addListener() {
      back.setOnClickListener(new View.OnClickListener() {

      			@Override
      			public void onClick(View arg0) {
      				// TODO Auto-generated method stub
      				finish();
      			}
      		});
       is_search.setOnClickListener(new View.OnClickListener() {

       			@Override
       			public void onClick(View arg0) {
       				// TODO Auto-generated method stub
       				ed_search.setText("");
       			}
       		});
        ed_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(s.length()==0){
                    is_search.setVisibility(View.GONE);
                }else {
                    is_search.setVisibility(View.VISIBLE);
                    editname=ed_search.getText().toString();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                adapter1.clear();
                adapter1.notifyDataSetChanged();

                for (ParkName parkName : parklist) {
                    if(parkName.getPname().contains(editname)){
                        System.out.println(parkName.getPname());
                        arr1.add(parkName.getPname());
                    }
                }
                ListView_park_name.setAdapter(adapter1);
            }
        });




    }

    private void init() {
        t_title=(TextView)findViewById(R.id.text_title);
        other=(ImageView)findViewById(R.id.button_other);
        t_title.setText("停车场信息查询");
        other.setVisibility(View.GONE);
        back=(ImageView)findViewById(R.id.button_back);
        search=(Button)findViewById(R.id.search);
        ed_search=(EditText)findViewById(R.id.ed_search);
        ed_search.clearFocus();
         is_search=(ImageView)findViewById(R.id.is_search);
         ListView_park_name=(ListView)findViewById(R.id.listView_park_name);
        parklist= (List<ParkName>) this.getIntent().getSerializableExtra("parklist");
          for (ParkName pname : parklist){
              arr1.add(pname.getPname());
          }
        adapter1 =new ArrayAdapter<String>(this,R.layout.array_item,arr1);
        ListView_park_name.setAdapter(adapter1);
        go_to_map=(Button)findViewById(R.id.go_to_map);
    }














}
