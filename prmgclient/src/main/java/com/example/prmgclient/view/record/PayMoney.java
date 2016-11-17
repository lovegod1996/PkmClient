package com.example.prmgclient.view.record;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.prmgclient.R;
import com.example.prmgclient.bean.Record;
import com.example.prmgclient.engine.RecordEngineImpl;

import java.io.Serializable;
import java.util.List;

public class PayMoney extends Activity{
TextView t_title;//������
Button other;
Button back;
TextView balance;
View relativelayout_w3;
View relativelayout_w4;

	ProgressDialog progressDialog;
 SharedPreferences sp ;
 String name_nu;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==4){
				progressDialog.dismiss();
                Bundle bundle=msg.getData();
				Intent intent=new Intent(PayMoney.this,PayMoney_self.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	};

	  protected void onCreate(Bundle savedInstanceState){  
		 super.onCreate(savedInstanceState);
	       setContentView(R.layout.wallet);
	       init();
	       addListener();

	         relativelayout_w3.setOnClickListener(new OnClickListener() {				          	   
	 			@Override
	 			public void onClick(View arg0) {
	 				// TODO Auto-generated method stub							
	 				try {
	 					sp = getSharedPreferences("userInfo", 0);
	 					name_nu = sp.getString("USER_NAME",null);

						progressDialog=ProgressDialog.show(PayMoney.this,"请稍等","获取数据中..",true);
						new Thread(){
							@Override
							public void run() {
								RecordEngineImpl recordEngineImpl=new RecordEngineImpl();
								try {
									List<Record> recordList=recordEngineImpl.getListRecord(name_nu);

									Message msg=new Message();
									msg.what=4;
									Bundle bundle=new Bundle();
									bundle.putSerializable("recordList", (Serializable) recordList);
									msg.setData(bundle);
                                    handler.sendMessage(msg);
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}.start();
	 			    } 
	 	    	   catch (Exception e)
	 	    	   {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 			   }		
	 		
	 				
	 			}
	 		});
		 
	  }

	private void init() {
		// TODO Auto-generated method stub
		 t_title=(TextView) findViewById(R.id.text_title);
		 
		 t_title.setText("我的钱包");
	     other=(Button)findViewById(R.id.button_other);	     
	     relativelayout_w3=(View)findViewById(R.id.relativelayout_w3);
	     relativelayout_w4=(View)findViewById(R.id.relativelayout_w4);
	     other.setVisibility(View.GONE);
	     back=(Button)findViewById(R.id.button_back);
	   
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
