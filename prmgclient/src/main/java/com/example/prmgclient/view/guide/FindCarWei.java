package com.example.prmgclient.view.guide;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.prmgclient.R;

public class FindCarWei extends Activity {
	TextView t_title;//������
	Button other;
	Button back;

	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.findcarwei);
	       t_title=(TextView) findViewById(R.id.text_title);
		   t_title.setText("停车场地图");
		   other=(Button)findViewById(R.id.button_other);	
		   other.setVisibility(View.GONE);
		   back=(Button)findViewById(R.id.button_back); 
	       back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub							
			finish();  					        
			}
			});  
      
	     
	    } 

}
