package com.example.prmgclient.view.record;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.prmgclient.MainActivity;
import com.example.prmgclient.R;
import com.example.prmgclient.bean.Record;
import com.example.prmgclient.bean.User;
import com.example.prmgclient.engine.RecordEngineImpl;
import com.example.prmgclient.engine.UserEngineImpl;
import com.example.prmgclient.util.HCache;

import org.json.JSONException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayMoney extends Activity {
    TextView t_title;//������
    Button other;
    Button back;
    TextView balance;
    View relativelayout_w3;
    View relativelayout_w4;

    ProgressDialog progressDialog;
    SharedPreferences sp;
    String name_nu;
    private HCache mcache;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 4:
                    progressDialog.dismiss();
                    Bundle bundle = msg.getData();
                    Intent intent = new Intent(PayMoney.this, PayMoney_self.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case 5:
                    progressDialog.dismiss();
                    Bundle bundle1 = msg.getData();
                    User uu = (User) bundle1.getSerializable("user");
                    balance.setText(uu.getMoney() + "元");
                    break;
                case 16:
                    progressDialog.dismiss();
                    Toast.makeText(PayMoney.this, "服务器正在维护....", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet);
        mcache = HCache.get(this);
        init();
        addListener();

        relativelayout_w3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    sp = getSharedPreferences("userInfo", 0);
                    name_nu = sp.getString("USER_NAME", null);

                    progressDialog = ProgressDialog.show(PayMoney.this, "请稍等", "获取数据中..", true);
                    new Thread() {
                        @Override
                        public void run() {
                            if (MainActivity.isConnByHttpServer()) {
                                RecordEngineImpl recordEngineImpl = new RecordEngineImpl();
                                try {
                                    List<Record> recordList = recordEngineImpl.getListRecord(name_nu);
                                    //封装json
                                    Map<String, Object> data = new HashMap<String, Object>();
                                    data.put("recordlist", recordList);
                                    String recordListJson = JSONObject.toJSONString(data);
                                    System.out.println(recordListJson);
                                    if (mcache.getString("recordListJson")) {
                                        mcache.remove("recordListJson");
                                    }
                                    mcache.put("recordListJson", recordListJson);
                                    Message msg = new Message();
                                    msg.what = 4;
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("recordList", (Serializable) recordList);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String recordListJson = mcache.getAsString("recordListJson");
                                try {
                                    org.json.JSONObject object = new org.json.JSONObject(recordListJson);
                                    String recordListstr = object.getString("recordlist");
                                    List<Record> recordList = JSON.parseArray(recordListstr, Record.class);
                                    Message msg = new Message();
                                    msg.what = 4;
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("recordList", (Serializable) recordList);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        balance.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sp = getSharedPreferences("userInfo", 0);
                name_nu = sp.getString("USER_NAME", null);
                progressDialog = ProgressDialog.show(PayMoney.this, "请稍候", "获取数据中..", true);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (MainActivity.isConnByHttpServer()) {
                            UserEngineImpl userEngineImpl = new UserEngineImpl();
                            try {
                                User user = userEngineImpl.findUserByName(name_nu);
                                System.out.println(user);
                                String jsonUser = JSON.toJSONString(user);
                                if (mcache.getString("jsonUser")) {
                                    mcache.remove("jsonUser");
                                }
                                mcache.put("jsonUser", jsonUser);
                                Message msg = new Message();
                                msg.what = 5;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("user", user);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(16);
                        }
                    }
                }.start();
            }
        });

    }

    private void init() {
        // TODO Auto-generated method stub
        t_title = (TextView) findViewById(R.id.text_title);

        t_title.setText("我的钱包");
        other = (Button) findViewById(R.id.button_other);
        relativelayout_w3 = (View) findViewById(R.id.relativelayout_w3);
        relativelayout_w4 = (View) findViewById(R.id.relativelayout_w4);
        other.setVisibility(View.GONE);
        back = (Button) findViewById(R.id.button_back);
        balance = (TextView) findViewById(R.id.balance);
        User user = (User) this.getIntent().getSerializableExtra("user");
        balance.setText(user.getMoney() + "元");
        if (user.getMoney() < 10) {
            Toast toast = Toast.makeText(PayMoney.this, "余额不足，请及时充值......", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void addListener() {
        // TODO Auto-generated method stub

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }
}
