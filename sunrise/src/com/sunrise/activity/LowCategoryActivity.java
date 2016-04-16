package com.sunrise.activity;

import java.util.Map;

import com.sunrise.R;
import com.sunrise.adapter.LowCategoryListViewAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Level2Data;
import com.sunrise.model.Station;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage.SubmitPdu;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LowCategoryActivity extends Activity {
    private static final String LOG_TAG = "sunrise";
    private Button btnSave;
    private Button btnSubmit;
    
    private int stationId = 0;
    private int highCategoryId = 0;
    private int midCategoryId = 0;
    private int dataId = 0;
    private Map<String, String> level3Data;
    private ListView lvDetail;
    LowCategoryListViewAdapter lowCategoryListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowcategory);
        lvDetail = (ListView) findViewById(R.id.lv_detail);
        btnSave=(Button) findViewById(R.id.btn_save);
        btnSubmit=(Button) findViewById(R.id.btn_submit);

        Bundle bundle = this.getIntent().getExtras();
        stationId = bundle.getInt("stationId");
        highCategoryId = bundle.getInt("highActivityId");
        midCategoryId = bundle.getInt("midActivityId");
        dataId = bundle.getInt("dataId");

        try {
            Station station = JsonFileParser.getStationWrapper(stationId).getStation();
            Level1Data level1Data = station.getDataItem(highCategoryId);
            Level2Data level2Data = level1Data.getLevel2DataItem(midCategoryId);
            level3Data = level2Data.getLevel3DataItem(dataId);
            Map<String, String> fields = level2Data.getFields();

            lowCategoryListViewAdapter = new LowCategoryListViewAdapter(this);
            lowCategoryListViewAdapter.setData(fields.values(), level3Data.values());
            lvDetail.setAdapter(lowCategoryListViewAdapter);
            lvDetail.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    

                }
            });

        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        
        save();
        submit();
    }

    private void save() {
        
    }

    private void submit() {
    }
}
