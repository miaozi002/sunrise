package com.sunrise.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.google.gson.Gson;
import com.sunrise.R;
import com.sunrise.adapter.LowCategoryListViewAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Level2Data;
import com.sunrise.model.SaveToJson;
import com.sunrise.model.Station;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class LowCategoryActivity extends Activity {
    private static final String LOG_TAG = "sunrise";
    private Button btnSave;
    private Button btnSubmit;

    private int stationId = 0;
    private int highCategoryId = 0;
    private int midCategoryId = 0;
    private int dataId = 0;
    private Level2Data level2Data;
    private ListView lvDetail;
    private EditText editText;
    LowCategoryListViewAdapter lowCategoryListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowcategory);
        lvDetail = (ListView) findViewById(R.id.lv_detail);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        Bundle bundle = this.getIntent().getExtras();
        stationId = bundle.getInt("stationId");
        highCategoryId = bundle.getInt("highActivityId");
        midCategoryId = bundle.getInt("midActivityId");
        dataId = bundle.getInt("dataId");

        try {
            Station station = JsonFileParser.getStationWrapper(stationId).getStation();
            Level1Data level1Data = station.getDataItem(highCategoryId);
            level2Data = level1Data.getLevel2DataItem(midCategoryId);
            lowCategoryListViewAdapter = new LowCategoryListViewAdapter(this);
            lowCategoryListViewAdapter.setData(level2Data.getKeyDisplayName(), level2Data.getValues(dataId));
            lvDetail.setAdapter(lowCategoryListViewAdapter);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    public void save(View saveButton) {
        StringBuilder sb = new StringBuilder();
        sb.append("You have changed values for:\n");

        List<String> dbKeys = level2Data.getKeyDbName();
        List<String> originalData = level2Data.getValues(dataId);
        List<String> currentValues = lowCategoryListViewAdapter.getValues();

        SaveToJson detail = new SaveToJson();
        detail.setId(originalData.get(dbKeys.indexOf("id")));
        detail.setTbl(originalData.get(dbKeys.indexOf("tbl")));

        for (int i = 0; i < originalData.size(); i++) {
            if (!originalData.get(i).equals(currentValues.get(i))) {
                detail.saveToMap(dbKeys.get(i), currentValues.get(i));

                sb.append(level2Data.getKeyDisplayName().get(i) + ":" + originalData.get(i) + "-->"
                        + currentValues.get(i) + "\n");
            }
        }

        Toast.makeText(LowCategoryActivity.this, sb.toString(), Toast.LENGTH_LONG).show();

        Gson gson = new Gson();
        String jsonContent = gson.toJson(detail);
        try {
            FileOutputStream fos = openFileOutput("userUpdated.json", Context.MODE_PRIVATE);
            fos.write(jsonContent.getBytes());
            fos.close();
            // next step is when click submit button, you should upload the file
            // userUpdated.json to server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submit(View submitButton) {
    }
}
