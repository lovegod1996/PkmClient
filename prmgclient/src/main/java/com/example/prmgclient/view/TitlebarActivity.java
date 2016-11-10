package com.example.prmgclient.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prmgclient.R;

public class TitlebarActivity extends Activity implements OnClickListener {
	  RelativeLayout mContentLayout;
	  Button back;
	  Button other;
	  TextView t_title;
	  
	  
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {  
        case R.id.button_back:  
            onBackward(v);  
            break;  

        case R.id.button_other:  
            onForward(v);  
            break;  

        default:  
            break;  
    }  
	}
	
	private void onForward(View v) {
		// TODO Auto-generated method stub
	//	Intent intent=new Intent(TitlebarActivity.this,Account_information.class);
	//	startActivity(intent);
	}

	private void onBackward(View v) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setupViews();   //���� title_bar ���� ������ȡ���⼰���ఴť  
    }

	private void setupViews() {
		// TODO Auto-generated method stub
		super.setContentView(R.layout.title_bar);
		t_title = (TextView) findViewById(R.id.text_title);         
        back = (Button) findViewById(R.id.button_back);  
        other = (Button) findViewById(R.id.button_other); 
        mContentLayout = (RelativeLayout) findViewById(R.id.relativelayout_main); 
	}  
	
  
    //���ñ�������  
    @Override  
    public void setTitle(int titleId) {  
    	t_title.setText(titleId);  
    }  
  
    //���ñ�������  
    @Override  
    public void setTitle(CharSequence title) {  
    	t_title.setText(title);  
    }  
  
    //���ñ���������ɫ  
    @Override  
    public void setTitleColor(int textColor) {  
    	t_title.setTextColor(textColor);  
    }  
    
    @Override  
    public void setContentView(View view) {  
        mContentLayout.removeAllViews();  
        mContentLayout.addView(view);  
        onContentChanged();  
    }  
     
}

