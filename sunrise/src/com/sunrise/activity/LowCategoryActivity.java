package com.sunrise.activity;

import java.util.List;

import com.sunrise.R;
import com.sunrise.jsonparser.JsonParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Level2Data;
import com.sunrise.model.Level3Data;
import com.sunrise.model.Station;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LowCategoryActivity extends Activity {
    private static final String LOG_TAG = "sunrise";
    private int stationId = 0;
    private int highCategoryId = 0;
    private int midCategoryId = 0;
    private int dataId = 0;
    private Level3Data level3Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowcategory);

        Bundle bundle = this.getIntent().getExtras();
        highCategoryId = bundle.getInt("highActivityId");
        midCategoryId = bundle.getInt("midActivityId");
        dataId = bundle.getInt("dataId");
        try {
            Station station = JsonParser.parseAllJsonFiles(getFilesDir()).get(stationId);
            List<Level1Data> highCategories = station.getData();
            Level1Data highCategory = highCategories.get(highCategoryId);
            List<Level2Data> categoryList = highCategory.getData();
            Level2Data midCategoryList = categoryList.get(midCategoryId);
            List<Level3Data> cellDataList = midCategoryList.getData();
            level3Data = cellDataList.get(dataId);

            TextView tvDataDetail = (TextView) findViewById(R.id.btn_data_detail);
            tvDataDetail.setText(
                    "id: " + level3Data.getId() + "\nstid: " + level3Data.getStid() + "\ncode: " + level3Data.getCode()
                    + "\nName: " + level3Data.getName() + "\nCapacity: " + level3Data.getCapacity());

        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }
}
