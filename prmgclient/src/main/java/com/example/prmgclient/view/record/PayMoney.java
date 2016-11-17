package com.example.prmgclient.view.record;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.prmgclient.R;

public class PayMoney extends Activity{
TextView t_title;//������
Button other;
Button back;
TextView balance;
View relativelayout_w3;
View relativelayout_w4;


 SharedPreferences sp ;
 String name_nu;
/*public void handlerMsg(){
    handler=new Handler()
         {
         	@Override
  			public void handleMessage(Message msg)
  			{
  				// �����Ϣ���������߳�
  				if (msg.what == 0x123)
  				{// ��ȡ������
  					
 					if(msg.obj.toString()!=null&&msg.obj.toString().contains("payrecord"))
  					{	 
  					
  						 String record=msg.obj.toString();
  						
  						 Intent intent=new Intent(PayMoney.this,PayMoney_self.class);
  						 intent.putExtra("payrecords",record);//����������תҳ��
  						 startActivity(intent);	
  						
  					}
  					
  				}							
  			}
  		};    
        clientThread = new ClientThread(handler);
       	new Thread(clientThread).start();  	// �ͻ�������ClientThread�̴߳����������ӡ���ȡ���Է�����������          
   } */

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
	 					 
	 				    Message msg = new Message();
	 					msg.what = 0x345;
	 					msg.obj = "payrecords;"+name_nu;
	 				//	clientThread.revHandler.sendMessage(msg);
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
