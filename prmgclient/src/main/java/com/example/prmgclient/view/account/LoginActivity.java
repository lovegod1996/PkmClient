   package com.example.prmgclient.view.account;

   import android.app.Activity;
   import android.app.ProgressDialog;
   import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
   import android.os.Handler;
   import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prmgclient.MainActivity;
import com.example.prmgclient.R;
import com.example.prmgclient.engine.UserEngineImpl;

   public class LoginActivity extends Activity {

	EditText Account; //�û��˺�
	EditText Password;//�û�����
	Button   Login;   //��¼��ť
	Button   Register;//ע�ᰴť
	//TextView Show;    //�����ı�����ʾע����˺�
	
	TextView t_title;//����������
	Button imageButton;//账户管理按钮
	   Button back;
	  static  String name;
	   static  String password;
   ProgressDialog progressDialog;

	SharedPreferences sp;
      Handler handle=new Handler(){
		  @Override
		  public void handleMessage(Message msg) {
			  super.handleMessage(msg);
			  if(msg.what==1){
				  boolean bb=msg.obj.toString().equals("true");
				  if(bb){
					  progressDialog.dismiss();   //关闭进度条

					  Editor editor = sp.edit();
					  editor.putString("USER_NAME", name);//�û��� �� SharedPreferences��sp������
					  editor.putString("PASSWORD",password);//���� �� SharedPreferences��sp������
					  editor.commit();
					  sp.edit().putBoolean("ISCHECK", true).commit();  //��������
					  sp.edit().putBoolean("AUTO_ISCHECK", true).commit(); //�Զ���½

					  Toast toast=Toast.makeText(getApplicationContext(), "欢迎登录", Toast.LENGTH_SHORT);
					  toast.show();

					   Intent intent=new Intent(LoginActivity.this,MainActivity.class);
					  startActivity(intent);
					  finish();
				  }else{
					  progressDialog.dismiss();    //关闭进度条
					  Toast toast=Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT);
					  toast.show();
				  }
			  }
		  }
	  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
       super.onCreate(savedInstanceState);
       setContentView(R.layout.login);
       initView();
       t_title.setText("登录界面");
       imageButton.setVisibility(View.GONE);


       addListener(); //��Ӽ�����Ӧ�¼�
    } 


	private void initView() {
		// TODO Auto-generated method stub
    
	    sp = this.getSharedPreferences("userInfo", 0); //���ʵ������ 
		
		Account=(EditText) findViewById(R.id.account);
		Password=(EditText) findViewById(R.id.password);
		Login=(Button) findViewById(R.id.login);
		Register=(Button) findViewById(R.id.register);
		back= (Button) findViewById(R.id.button_back);
	 //   Show=(TextView) findViewById(R.id.show);     
	    t_title=(TextView) findViewById(R.id.text_title);	    
	    imageButton = (Button) findViewById(R.id.button_other); 
	    if(sp.getBoolean("ISCHECK", false))  //��������
        {  
	    Account.setText(sp.getString("USER_NAME", ""));  //
        Password.setText(sp.getString("PASSWORD", ""));// 
        }
        if(sp.getBoolean("AUTO_ISCHECK", false))  //�Զ���¼
        {  

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
              startActivity(intent);
              finish();
        } 
	}
	
	 
	private void addListener() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		 Login.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					
				      name = Account.getText().toString();//��ȡ�û���
				     password =Password.getText().toString();//��ȡ�û�����
				     if (name.equals("") || password.equals("")) 
				      {
				    	 Toast toast1=Toast.makeText(getApplicationContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT);
	 				     toast1.show();	
				      }
				     else
				     {//
				    	 try {

							 progressDialog=ProgressDialog.show(LoginActivity.this,"正在登陆","获取数据中..",true);
							 new Thread(){
								 @Override
								 public void run() {
									 UserEngineImpl userEngineImpl=new UserEngineImpl();
									 try {
										 boolean loginState=userEngineImpl.login(name,password);

										 Message msg=new Message();
										 msg.what=1;
										 msg.obj=loginState;
                                         handle.sendMessage(msg);

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
				   }//onClick
				});//LOGIN_OnClickListener
		 
			Register.setOnClickListener(new OnClickListener()
			{		
				@Override
				public void onClick(View v) {
					
					Intent intent=new Intent(LoginActivity.this, pre_registerActivity.class);
					startActivity(intent);
				}
			});	//Register_OnClickListener		
		   }  
	
}

 


