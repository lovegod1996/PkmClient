 package com.example.prmgclient.view.account;


 import android.app.Activity;
 import android.app.AlertDialog;
 import android.app.ProgressDialog;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.os.Bundle;
 import android.os.Handler;
 import android.os.Message;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.widget.Button;
 import android.widget.CheckBox;
 import android.widget.EditText;
 import android.widget.ListView;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.example.prmgclient.R;
 import com.example.prmgclient.bean.User;
 import com.example.prmgclient.engine.UserEngineImpl;


 public class RegisterActivity extends Activity {
	Button button_car_number;
	Button button_my_register;
	CheckBox ck_agreen;
	ListView lv=null;
	static String phone1;
	 String carnum;
	EditText user_name;
	EditText password1;
	EditText password2;
	EditText car_number_shuzi;
	TextView t_title;//����������
	Button imageButton;//ImageButton
	Button buttonback;
	 ProgressDialog progressDialog;

	Handler  handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
            if(msg.what==3){
				progressDialog.dismiss();
				boolean registerState=msg.obj.toString().equals("true");
				if(registerState){
					Toast toast=Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT);
					toast.show();
					Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
					startActivity(intent);
				}else{
					Toast toast=Toast.makeText(getApplicationContext(), "注册失败！（该手机号已注册）", Toast.LENGTH_SHORT);
					toast.show();
				}
			}

		}
	};
	private ButtonOnClick buttonOnClick = new ButtonOnClick(1);
	private String[] provinces = new String[]{ "豫", "浙", "苏", "鲁", "皖", "鄂","桂","甘","晋","蒙","陕","吉","贵",
			"粤","藏","川","宁","琼","京","津","沪","冀","黑","闽","赣","青","云","辽"};
		

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
      
        init();
        t_title.setText("注册页面");
        imageButton.setVisibility(View.GONE);


			  button_my_register.setOnClickListener(new OnClickListener() {

				  @Override
				  public void onClick(View arg0) {
					  // TODO Auto-generated method stub
					  if (ck_agreen.isChecked()) {
						  carnum = button_car_number.getText().toString() + car_number_shuzi.getText().toString();
						  if (password1.getText().toString().equals(password2.getText().toString())) {
							  try {

								  final User user = new User();
								  user.setPnum(phone1);
								  user.setPasw(password1.getText().toString());
								  user.setPname(user_name.getText().toString());
								  user.setCnum(carnum);
								  progressDialog = ProgressDialog.show(RegisterActivity.this, "正在注册", "获取数据中..", true);
								  new Thread() {
									  @Override
									  public void run() {
										  UserEngineImpl userEngineImpl = new UserEngineImpl();
										  try {
											  boolean bb = userEngineImpl.register(user);
											  Message msg = new Message();
											  msg.what = 3;
											  msg.obj = bb;
											  handler.sendMessage(msg);
										  } catch (Exception e) {
											  e.printStackTrace();
										  }

									  }
								  }.start();

							  } catch (Exception e) {
								  // TODO Auto-generated catch block
								  e.printStackTrace();
							  }
						  } else {
							  Toast toast1 = Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT);
							  toast1.show();
						  }

					  } else {
						  Toast toast1 = Toast.makeText(getApplicationContext(), "请同意协议", Toast.LENGTH_SHORT);
						  toast1.show();
					  }
				  }
			  });


        
        
        addListener();	
 }  
	
	public void init()
	  {
		user_name=(EditText)findViewById(R.id.user_name);
		password1=(EditText)findViewById(R.id.password1);
		password2=(EditText)findViewById(R.id.password2);
		car_number_shuzi=(EditText)findViewById(R.id.car_number_shuzi);
		   button_car_number=(Button)findViewById(R.id.car_number);//���ƺŵĺ���λ
	       button_my_register=(Button)findViewById(R.id.my_register);//ע��
		  ck_agreen= (CheckBox) findViewById(R.id.checkbox);

	       t_title=(TextView) findViewById(R.id.text_title);
	       imageButton = (Button) findViewById(R.id.button_other); 
	       buttonback=(Button)findViewById(R.id.button_back);
	       Intent intent = getIntent();  
		     phone1 = intent.getStringExtra("phone");
		         
	       
	  }
	
	
	public void addListener()
	{
		buttonback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		button_car_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showSingleChoiceDialog();
				carnum=button_car_number.getText().toString()+car_number_shuzi.getText().toString();
			}
		});
		      
	}
	private void showSingleChoiceDialog()
	{
		new AlertDialog.Builder(this).setTitle("选择省份").setSingleChoiceItems(
				provinces, 1, buttonOnClick).setPositiveButton("确定",buttonOnClick).setNegativeButton("取消", buttonOnClick).show();
		
	}
	private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		private int index;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}
		@Override
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;					
			}
			else
			{
				if (whichButton == DialogInterface.BUTTON_POSITIVE)
				{
					   button_car_number=(Button)findViewById(R.id.car_number);
					   button_car_number.setText(provinces[index]);
					
				}
				else if (whichButton == DialogInterface.BUTTON_NEGATIVE)
				{
					
				}
			}
			
		}
   }		
}
