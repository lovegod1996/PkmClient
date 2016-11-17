package com.example.prmgclient.view.account;

import android.app.Activity;
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

import com.example.prmgclient.R;
import com.example.prmgclient.bean.User;
import com.example.prmgclient.engine.UserEngineImpl;

public class Account_information extends Activity {
	TextView t_title;//������
	Button imageButton;
	Button login_register;//��¼ע�ᰴť	
	TextView login_number;//��¼�˺�
	TextView ticket_number;
	TextView money_number;
	View user_money;//�û�Ǯ��
	View user_record;//ͣ����¼
	View user_parking_ticket;//ͣ��ȯ
	View user_find_illegal;//��Υ��
	View user_setting;//�˻�����
	View about_us;//��������
	View user_feedback;//�������

	 Button back;
	 Button other;//ͷ��ť

	ProgressDialog progressDialog;
	 static SharedPreferences sp ;
	 static String name_nu;

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==2){
				progressDialog.dismiss();
				Bundle bundle=msg.getData();
				Intent intent=new Intent(Account_information.this, Account_manage.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	};

	public void init()
	{
		  t_title=(TextView) findViewById(R.id.text_title);
	      login_register=(Button) findViewById(R.id.login_register);
	      imageButton=(Button)findViewById(R.id.to_information);
	      login_number=(TextView)findViewById(R.id.login_number);
		back= (Button) findViewById(R.id.button_back);
	    	t_title.setText("我的");
	      other=(Button)findViewById(R.id.button_other);
	      other.setVisibility(View.GONE);

	      money_number=(TextView) findViewById(R.id.money_number);
	      ticket_number=(TextView) findViewById(R.id.ticket_number);
	      //ȡ��Ǯ������
		/* Intent intent = getIntent();  
	     String moneyString = intent.getStringExtra("money_number");
	     money_number.setText(moneyString);
	      */

	      user_money=(View)findViewById(R.id.relativelayout_user_money);
	      user_record=(View)findViewById(R.id.relativelayout_user_record);
	      user_parking_ticket=(View)findViewById(R.id.relativelayout_user_parking_ticket);
	      user_find_illegal=(View)findViewById(R.id.relativelayout_user_find_illegal);
	      user_setting=(View)findViewById(R.id.relativelayout_user_setting);
	      about_us=(View)findViewById(R.id.relativelayout_user_about_as_our);
	      user_feedback=(View)findViewById(R.id.relativelayout_user_find_back);

	      //��ȡPreferences
	       sp = getSharedPreferences("userInfo", 0);
	      //ȡ������
	       name_nu = sp.getString("USER_NAME",null);
	      if(name_nu!=null)
	      {
	    	  login_register.setVisibility(View.GONE);//ע���¼��ť����
	            login_number.setText(name_nu);//��ʾ�˻�
	      }
	      else
	      {
	    	  imageButton.setVisibility(View.GONE);
	      }
	}






	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.account_infomation);
            init();
           imageButton.setOnClickListener(new OnClickListener() {
			   @Override
			   public void onClick(View view) {
				   try{
					   progressDialog=ProgressDialog.show(Account_information.this,"请稍候","获取数据中..",true);
					   new Thread(){
						   @Override
						   public void run() {
							   UserEngineImpl userEngineImpl=new UserEngineImpl();
							   try {

								   User user=userEngineImpl.findUserByName(name_nu);
								   System.out.println(user);
								   Message msg=new Message();
								   msg.what=2;
								   Bundle bb=new Bundle();
								   bb.putSerializable("user",user);
								   msg.setData(bb);
                                   handler.sendMessage(msg);
							   } catch (Exception e) {
								   e.printStackTrace();
							   }
						   }
					   }.start();
				   }catch (Exception e){
					   e.printStackTrace();
				   }
			   }
		   });
             back.setOnClickListener(new OnClickListener() {
				 @Override
				 public void onClick(View view) {
					 finish();
				 }
			 });
            login_register.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					 Intent intent=new Intent(Account_information.this, LoginActivity.class);
		 			 startActivity(intent);
				}
			});
            user_money.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					/* Intent intent=new Intent(Account_information.this, Wallet.class);
		 			 startActivity(intent);*/
				}
			});
            user_find_illegal.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				sendToTwitter();
    			}

				private void sendToTwitter() {
					// TODO Auto-generated method stub
				   	    String url = "http://www.weizhang8.cn/";
			    	    Intent i = new Intent(Intent.ACTION_VIEW);
			    	    i.setData(Uri.parse(url));
			    	    startActivity(i);
				}
    		});
            user_feedback.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				/*Intent intent=new Intent(Account_information.this, feed_back.class);
        			startActivity(intent);*/
    			}
    		});
	 }

}
