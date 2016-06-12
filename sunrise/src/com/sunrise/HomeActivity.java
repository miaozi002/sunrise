package com.sunrise;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class HomeActivity extends Activity {
    private String[] me_data_list;
    private Spinner spinner;
    private ArrayAdapter<String> arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        spinner = (Spinner) findViewById(R.id.spinner1);

       /* me_data_list=new ArrayList<String>();
        me_data_list.add("退出登录");
        me_data_list.add("关于系统");*/
        setData();

        arr_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, me_data_list);
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arr_adapter);
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });*/
    }

    private void setData() {
        me_data_list=new String[]{
                "退出登录", "关于系统"
        };

    }


}
