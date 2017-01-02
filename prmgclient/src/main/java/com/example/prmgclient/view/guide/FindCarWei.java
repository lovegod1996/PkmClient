package com.example.prmgclient.view.guide;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prmgclient.MyApplication;
import com.example.prmgclient.R;

public class FindCarWei extends Activity {
	TextView t_title;//������
	ImageView other;
	ImageView back;

	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.findcarwei);
		 MyApplication.getInstance().addActivity(this);
	       t_title=(TextView) findViewById(R.id.text_title);
		   t_title.setText("停车场地图");
		   other=(ImageView)findViewById(R.id.button_other);
		   other.setVisibility(View.GONE);
		   back=(ImageView)findViewById(R.id.button_back);
	       back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub							
			finish();  					        
			}
			});  
      
	     
	    } 

}
