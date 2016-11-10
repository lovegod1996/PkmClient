package com.example.prmgclient.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.prmgclient.R;
import com.example.prmgclient.bean.ParkDetail;
import com.example.prmgclient.bean.ParkName;
import com.example.prmgclient.engine.ParkEngineImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 2016/11/10.
 */
public class ParkInformation extends  TitlebarActivity {
    private TextView t_title;
    private Button other;
    private Button back;
     private Button search;
    private EditText ed_search;
     private ImageView is_search;
     private ListView ListView_park_name;

       ArrayList<String> arr1 = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    String editname;
    private static  List<ParkName> parklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_info);
        init();
        addListener();
        ListView_park_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pname=arr1.get(i).toString();//得到点击的列表项的值
                ParkEngineImpl parkEngineImpl=new ParkEngineImpl();
                try {
                    ParkDetail parkDeatil=parkEngineImpl.getParkDetailByName(pname);
                    Intent intent=new Intent(ParkInformation.this, ParkInformation_self.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("parkdetail",parkDeatil);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void addListener() {
      back.setOnClickListener(new View.OnClickListener() {

      			@Override
      			public void onClick(View arg0) {
      				// TODO Auto-generated method stub
      				finish();
      			}
      		});
       is_search.setOnClickListener(new View.OnClickListener() {

       			@Override
       			public void onClick(View arg0) {
       				// TODO Auto-generated method stub
       				ed_search.setText("");
       			}
       		});
        ed_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if(s.length()==0){
                    is_search.setVisibility(View.GONE);
                }else {
                    is_search.setVisibility(View.VISIBLE);
                    editname=ed_search.getText().toString();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                adapter1.clear();
                adapter1.notifyDataSetChanged();

                for (ParkName parkName : parklist) {
                    if(parkName.getPname().contains(editname)){
                        System.out.println(parkName.getPname());
                        arr1.add(parkName.getPname());
                    }
                }
                ListView_park_name.setAdapter(adapter1);
            }
        });




    }

    private void init() {
        t_title=(TextView)findViewById(R.id.text_title);
        other=(Button)findViewById(R.id.button_other);
        t_title.setText("停车场信息查询");
        other.setVisibility(View.GONE);
        back=(Button)findViewById(R.id.button_back);
        search=(Button)findViewById(R.id.search);
        ed_search=(EditText)findViewById(R.id.ed_search);
        ed_search.clearFocus();
         is_search=(ImageView)findViewById(R.id.is_search);
         ListView_park_name=(ListView)findViewById(R.id.listView_park_name);
        parklist= (List<ParkName>) this.getIntent().getSerializableExtra("parklist");
          for (ParkName pname : parklist){
              arr1.add(pname.getPname());
          }
        adapter1 =new ArrayAdapter<String>(this,R.layout.array_item,arr1);
        ListView_park_name.setAdapter(adapter1);
    }














}
