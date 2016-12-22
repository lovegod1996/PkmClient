package com.example.prmgclient.view.record;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prmgclient.MainActivity;
import com.example.prmgclient.R;
import com.example.prmgclient.bean.Record;
import com.example.prmgclient.engine.RecordEngineImpl;
import com.example.prmgclient.util.NetWorkUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PayMoney_self extends Activity implements SwipeRefreshLayout.OnRefreshListener{
	
	//ListView pay_records_list;
	RelativeLayout simple;
	TextView park_name;
	TextView data;
	TextView pay;
	TextView time;
	TextView time_start;
	TextView time_end;
	
	TextView t_title;
	Button other;
	Button back;
	ListView list;

	SharedPreferences sp ;
	String name_nu;
   private	SimpleAdapter simpleAdapter;
	private SwipeRefreshLayout swipeLayout;
	private static  List<Record> recordList;
   private static  List<Map<String,Object>> listItems;

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==5){
				Bundle bundle=msg.getData();
				List<Record> listRecord= (List<Record>) bundle.getSerializable("recordList");
				listItems=getList(listRecord);
               simpleAdapter.notifyDataSetChanged();
				swipeLayout.setRefreshing(false);
			}
			if(msg.what==16){
				swipeLayout.setRefreshing(false);
				Toast.makeText(PayMoney_self.this, "网络连接异常....", Toast.LENGTH_SHORT).show();
			}

		}
	};

     protected void onCreate(Bundle savedInstanceState){
    	  super.onCreate(savedInstanceState);
    	  setContentView(R.layout.payment_records);
    	  init();

		 recordList= (List<Record>) this.getIntent().getSerializableExtra("recordList");
   System.out.println(recordList);
		 listItems=getList(recordList);

		   simpleAdapter=new SimpleAdapter(this, listItems, R.layout.simple_item,
				  new String[] {"parknames","datas","pays","starttime","endtime","times"}, 
                  new int[] {R.id.park_name,R.id.data,R.id.pay,R.id.time_start,R.id.time_end,R.id.time});
		  list.setAdapter(simpleAdapter);

		  back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
}

	private List<Map<String,Object>> getList(List<Record> recordList) {
		List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
		for (Record record: recordList){
			Map<String, Object> listItem=new HashMap<String, Object>();
			listItem.put("parknames", record.getPname());
			listItem.put("datas", record.getIntime().replace(record.getIntime().substring(record.getIntime().indexOf(" ")), ""));
			listItem.put("pays", record.getPay());
			String longtime=getlongtime(record.getIntime(), record.getOuttime());
			listItem.put("starttime", getintime(record.getIntime()));
			listItem.put("endtime", getintime(record.getOuttime()));
			listItem.put("times", longtime);
			listItems.add(listItem);
		}
           return  listItems;
	}


	private void init() {
		// TODO Auto-generated method stub
	      t_title=(TextView) findViewById(R.id.text_title);
	      other=(Button)findViewById(R.id.button_other);
	      back=(Button)findViewById(R.id.button_back);
	      t_title.setText("缴费记录");
	      other.setVisibility(View.GONE);
	      
	    //  pay_records_list=(ListView)findViewById(R.id.pay_records_list);
	      
	      simple=(RelativeLayout)findViewById(R.id.simple);
	      park_name=(TextView)findViewById(R.id.park_name);
	      data=(TextView)findViewById(R.id.data);
	      pay=(TextView)findViewById(R.id.pay);
	      time=(TextView)findViewById(R.id.time);
	      time_start=(TextView)findViewById(R.id.time_start);
	      time_end=(TextView)findViewById(R.id.time_end);

	    	sp = getSharedPreferences("userInfo", 0);
	    	name_nu = sp.getString("USER_NAME",null);
	      
		  list=(ListView)findViewById(R.id.pay_records_list);
		  list.setDivider(null);
		  swipeLayout= (SwipeRefreshLayout) findViewById(R.id.id_swipe);
	      swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
         swipeLayout.setDistanceToTriggerSync(400);
		//swipeLayout.setSize(SwipeRefreshLayout.LARGE);
	}

	  /*
	   * ����ͣ��ʱ��
	   * 
	   */
	 public static String getlongtime(String intime,String outtime){
		 SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String longtime="";
		 try {
				Date ot=df.parse(outtime);
				Date it=df.parse(intime);
				long l=ot.getTime()-it.getTime();
				
				  long day=l/(24*60*60*1000);
				   long hour=(l/(60*60*1000)-day*24);
				   long min=((l/(60*1000))-day*24*60-hour*60);
				   long s=(l/1000-day*24*60*60-hour*60*60-min*60);

			 longtime=day+"天"+hour+"小时"+min+"分";
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return longtime;
	 }
	 
	 public static String getintime(String in_time)  {
		 SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd HH:mm");		
		Date d=new Date();
		String str = null;
		try {
			str = df2.format(df.parse(in_time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return str;
		 
	
	 }

	@Override
	public void onRefresh() {

		new Thread(){
			@Override
			public void run() {
				if(MainActivity.isConnByHttpServer()|| NetWorkUtil.isNetworkConnected(PayMoney_self.this)) {
					RecordEngineImpl recordEngineImpl = new RecordEngineImpl();
					try {
						List<Record> recordList = recordEngineImpl.getListRecord(name_nu);
						Message msg = new Message();
						msg.what = 5;
						Bundle bundle = new Bundle();
						bundle.putSerializable("recordList", (Serializable) recordList);
						msg.setData(bundle);
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					handler.sendEmptyMessage(16);
				}
			}
		}.start();
	}
}
