package com.example.prmgclient.view.inorout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.prmgclient.MainActivity;
import com.example.prmgclient.R;
import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.engine.ParkEngineImpl;
import com.example.prmgclient.engine.RecordEngineImpl;
import com.example.prmgclient.engine.UserEngineImpl;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class GateInOut extends Activity {

	TextView t_title;
	Button other;

	//TextView wifi_name;
//	TextView wifi_du;
	TextView welcome;
	TextView pa_name;//ͣ������
	TextView pa_left;//���пճ�λ


	TextView we_name;//�û���
	TextView pa_time2;//����ʱ��
	TextView pa_standard2;//�շѱ�׼		
	Button in;//��ť

	String pname = "";
	String pcri = "";
	String intime = "";


	static SharedPreferences sp;
	static String name_number;

	String save_time;
	double pay;
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
        switch (msg.what){
			case 8:
				progressDialog.dismiss();
				AlertDialog.Builder builder =new AlertDialog.Builder(GateInOut.this);
				builder.setTitle(pname);
				//builder.setAdapter(new ArrayAdapter<String>(this, resource, textViewResourceId), null);
				builder.setMessage(name_number+"\n"+"抬杆进场成功！"+"\n"+"时间:"+getNowtime());
				builder.show();
				Timer timer = new Timer();
				TimerTask task=new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent intent=new Intent(GateInOut.this, MainActivity.class);
						startActivity(intent);
						GateInOut.this.finish();
					}
				};
				timer.schedule(task, 1000 * 3);
				break;
			case 9:
				progressDialog.dismiss();
				AlertDialog.Builder builder1 =new AlertDialog.Builder(GateInOut.this);

				builder1.setTitle(pname);
				builder1.setMessage(name_number+"\n"+"抬杆出场成功！"+"\n"+"费用:"+pay+"  元"+"\n"+"出场时间："+getintime(save_time));
				builder1.show();
				Timer timer1 = new Timer();
				TimerTask task1=new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent intent=new Intent(GateInOut.this, MainActivity.class);
						startActivity(intent);
						GateInOut.this.finish();
					}
				};
				timer1.schedule(task1, 1000 * 3);

				break;
			default:
				break;
		}
	}
};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_inout);

		init();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	public void init() {
		t_title = (TextView) findViewById(R.id.text_title);
		other = (Button) findViewById(R.id.button_other);
		t_title.setText("进<——>出");
		other.setVisibility(View.GONE);


		we_name = (TextView) findViewById(R.id.we_name);
		pa_time2 = (TextView) findViewById(R.id.pa_time2);
		pa_standard2 = (TextView) findViewById(R.id.pa_standard2);///////////////////////


		welcome = (TextView) findViewById(R.id.welcome);
		pa_name = (TextView) findViewById(R.id.pa_name);
		pa_left = (TextView) findViewById(R.id.pa_left);
		in = (Button) findViewById(R.id.in);

	    //	Intent intent = getIntent();
		//String pi = intent.getStringExtra("inttime");

		final ParkDetail parkDetail= (ParkDetail) this.getIntent().getSerializableExtra("parkdetail");
		String pi=this.getIntent().getStringExtra("inttime");
		//	String WIFI = intent.getStringExtra("wifiname");
		//	String wifiQ = intent.getStringExtra("wifiq");
		//	wifi_name.setText(WIFI);
		//	wifi_du.setText(wifiQ);

	//	ArrayList<String> pinfo = new ArrayList<String>();
	//	pinfo = SplitString(PI);

		pa_name.setText(parkDetail.getPname());
		pa_left.setText(parkDetail.getParking_left()+ "个");

		pname = parkDetail.getPname();   //��ȡͣ������
		pcri = parkDetail.getPayCri();    //��ȡͣ��������
		intime = pi;   //��ȡ����ʱ��


		sp = getSharedPreferences("userInfo", 0);
		//取出手机号数据
		name_number = sp.getString("USER_NAME", null);


		we_name.setText(name_number);
		pa_time2.setText(getintime(getNowtime()));
		pa_standard2.setText(pcri + "元/小时");



		if (intime.contains("null")) {
			in.setText("进场");

			in.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub


					try {
					  /*  Message msg = new Message();
						msg.what = 0x345;
						msg.obj = "getin;"+name_number+";"+pname+";"+getNowtime();
						clientThread.revHandler.sendMessage(msg);*/
						progressDialog=ProgressDialog.show(GateInOut.this,"正在抬杆","请稍等..",true);
						new Thread(){
							@Override
							public void run() {
								super.run();
								ParkEngineImpl parkEngineImpl=new ParkEngineImpl();
								RecordEngineImpl recordEngineImpl=new RecordEngineImpl();
								try {
									boolean upState=parkEngineImpl.updateIn(pname);
									boolean inState=recordEngineImpl.getIn(name_number,pname,getNowtime());
									if(upState&&inState){
										handler.sendEmptyMessage(8);
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
			});
		} else {
			in.setText("出场");

			in.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub				
					///����ʱ���㷨
					  pay = CalPfees(intime, getNowtime(), pcri);
					save_time = getNowtime();
					try {
					   /* Message msg = new Message();
						msg.what = 0x345;
					//	msg.obj = "getout;"+name_number+";"+pname+";"+getNowtime()+";"+pay;
						msg.obj = "getout;"+name_number+";"+pname+";"+save_time+";"+pay;//----------------------
						clientThread.revHandler.sendMessage(msg);*/
						progressDialog=ProgressDialog.show(GateInOut.this,"正在抬杆","请稍等..",true);
						new Thread(){
							@Override
							public void run() {
								super.run();
								ParkEngineImpl parkEngineImpl=new ParkEngineImpl();
								RecordEngineImpl recordEngineImpl=new RecordEngineImpl();
								UserEngineImpl userEngineImpl=new UserEngineImpl();
								try {
									boolean upout=parkEngineImpl.updateout(pname);
									boolean upfee=recordEngineImpl.updateFee(name_number,pay,save_time);
									boolean payState=userEngineImpl.pay(name_number,pay);
									if(upfee&&upout&&payState){
										handler.sendEmptyMessage(9);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}.start();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

	}

	public static ArrayList<String> SplitString(String str) {
		String[] ss = str.split("\\+");
		ArrayList<String> list = new ArrayList<String>();
		for (String string : ss) {
			list.add(string);
		}
		return list;
	}

	public static String getNowtime() {
		//��ȡ��ǰʱ��
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
		String str = formatter.format(curDate);
		return str;
	}

	/*
	 * ����ͣ������
	 * 
	 */
	public static double CalPfees(String inTime, String ouTime, String pcri2) {

		double fees = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ot = df.parse(ouTime);
			Date it = df.parse(inTime);
			long l = ot.getTime() - it.getTime();

			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

			double p = Double.parseDouble(pcri2);

			System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
			if (min < 30 & day == 0 && hour == 0) {
				fees = 0;
			} else {
				fees = (day * 24 + hour + 1) * p;
			}


		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fees;

	}

	public static String getintime(String in_time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("MM" + "月" + "dd" + "日" + "HH:mm");
		Date d = new Date();
		String str = null;
		try {
			str = df2.format(df.parse(in_time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;


	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("GateInOut Page") // TODO: Define a title for the content shown.
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