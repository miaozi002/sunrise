package com.sunrise.activity;

import com.sunrise.R;
import com.sunrise.adapter.LowCategoryListViewAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Level2Data;
import com.sunrise.model.Level3Data;
import com.sunrise.model.Station;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LowCategoryActivity extends Activity {
    private static final String LOG_TAG = "sunrise";
    private int stationId = 0;
    private int highCategoryId = 0;
    private int midCategoryId = 0;
    private int dataId = 0;
    private Level3Data level3Data;
    private ListView lvDetail;
    LowCategoryListViewAdapter lowCategoryListViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowcategory);
        lvDetail=(ListView) findViewById(R.id.lv_detail);

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
            Level3Data fields = level2Data.getFields();

//            JsonObject level3DataAsMap = level3Data.asJsonObj();
//            JsonObject jsonFields = level2Data.getFieldsAsMap();
//            String tbl = jsonFields.get("tbl").getAsString();// "表ID"
//            String stid = jsonFields.get("stid").getAsString();// "变电站ID"
//
//            Set<Entry<String, JsonElement>> entrySet = jsonFields.entrySet();
//            List<String> keys = new ArrayList<String>();// 依次存放 tbl, stid....
//            List<String> keyHints = new ArrayList<String>();// 依次存放 "表ID",  "变电站ID"...
//            List<String> keyValues = new ArrayList<String>();// 依次存放 "8", "1"...
//            for (Entry<String, JsonElement> entry : entrySet) {
//                keys.add(entry.getKey());
//                keyHints.add(entry.getValue().getAsString());
//                keyValues.add(level3DataAsMap.get(entry.getKey()).getAsString());
//            }


            lowCategoryListViewAdapter = new LowCategoryListViewAdapter(this);
            lowCategoryListViewAdapter.setData(fields.valueList(), level3Data.valueList());
            lvDetail.setAdapter(lowCategoryListViewAdapter);
            lvDetail.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            
            /*EditText tvDataDetail = (EditText) findViewById(R.id.et_data_detail);
            tvDataDetail.setText(fields.getTbl()+": "+level3Data.getTbl()+"\n"+fields.getStid()+": "+level3Data.getStid()+"\n" +fields.getId()+": " + level3Data.getId() + "\n"+fields.getVlid()+": "+level3Data.getVlid()+"\n"
            +fields.getCode()+": "+level3Data.getCode()+"\n"+fields.getName()+": "+ level3Data.getName()+"\n"+fields.getNFC()+": " + level3Data.getNFC() +"\n"+fields.getManufactor()+": "+ level3Data.getManufactor() + "\n"+fields.getFacCode()+": " + level3Data.getFacCode()
            +  "\n"+fields.getInstalldate()+": " + level3Data.getInstalldate() + "\n"+fields.getPutdate()+": " + level3Data.getPutdate()+"\n"+fields.getFaultdate()+": "+ level3Data.getFaultdate()
            + "\n" +fields.getUpdatedate()+": "+level3Data.getUpdatedate()+"\n"+fields.getMedev()+": " + level3Data.getMedev()
            + "\n"+fields.getRepairdate()+": "+level3Data.getRepairdate()+"\n"+fields.getTestdate()+": "+level3Data.getTestdate()+"\n"
            +fields.getMakedate()+": "+level3Data.getMakedate()+"\n"+fields.getRemark()+": "+level3Data.getRemark()+"\n"+fields.getCapacity()+": "+level3Data.getCapacity()+"\n"+fields.getDevtype()+": "+level3Data.getDevtype()+"\n"
            +fields.getMaintbl()+": "+level3Data.getMaintbl()+"\n"+fields.getMaindev()+": "+level3Data.getMaindev()+"\n"+fields.getIsused()+": "+level3Data.getIsused()+"\n"+fields.getIsfault()+": "+level3Data.getIsfault()+"\n"
            +fields.getDefine1()+": "+level3Data.getDefine1()+"\n"+fields.getDefine2()+": "+level3Data.getDefine2()+"\n"+fields.getDefine3()+": "+level3Data.getDefine3()+"\n");*/


        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }


}
