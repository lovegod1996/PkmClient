package com.example.prmgclient.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prmgclient.R;
import com.example.prmgclient.view.account.Account_information;

public class TitlebarActivity extends Activity  {
	  FrameLayout mContentLayout;
	  ImageView back;
    ImageView other;
	  TextView t_title;

	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setupViews();//加载 activity_title 布局 ，并获取标题及两侧按钮
     back.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             finish();
         }
     });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TitlebarActivity.this,Account_information.class);
                startActivity(intent);
            }
        });
    }

	private void setupViews() {
		// TODO Auto-generated method stub
		super.setContentView(R.layout.actitvity_title);
		t_title = (TextView) findViewById(R.id.text_title);         
        back = (ImageView) findViewById(R.id.button_back);
        other = (ImageView) findViewById(R.id.button_other);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
	}





    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }

    @Override
    public void setTitleColor(int textColor) {
        super.setTitleColor(textColor);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }

    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    private void onForward(View view) {
        Intent intent=new Intent(TitlebarActivity.this,Account_information.class);
        startActivity(intent);
    }

    private void onBackward(View view) {
        finish();
    }
}

