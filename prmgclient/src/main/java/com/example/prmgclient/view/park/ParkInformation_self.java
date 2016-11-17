package com.example.prmgclient.view.park;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.prmgclient.R;
import com.example.prmgclient.bean.ParkDetail;

/**
 * Created by 123 on 2016/11/10.
 */
public class ParkInformation_self extends Activity {
    TextView t_title;//标题栏
    Button other;
    Button back;
    TextView p_name;
    TextView p_lot;
    TextView p_address;
    TextView p_phone;
    TextView p_fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_info_one);
        init();
    }

    private void init() {
        p_name=(TextView)findViewById(R.id.p_name);
        p_lot=(TextView)findViewById(R.id.p_lot);
        p_address=(TextView)findViewById(R.id.p_address);
        p_phone=(TextView)findViewById(R.id.p_phone);
        p_fee=(TextView)findViewById(R.id.p_fee);

        t_title=(TextView)findViewById(R.id.text_title);
        other=(Button)findViewById(R.id.button_other);
        other.setVisibility(View.GONE);
        back=(Button)findViewById(R.id.button_back);

        ParkDetail parkDetail= (ParkDetail) this.getIntent().getSerializableExtra("parkdetail");
        t_title.setText(parkDetail.getPname());
        p_name.setText(parkDetail.getPname());//-----------------------------
        p_address.setText(parkDetail.getAdress());
        p_fee.setText(parkDetail.getPayCri());
        p_phone.setText(parkDetail.getPhone());
        p_lot.setText(parkDetail.getParking_left());

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }
}
