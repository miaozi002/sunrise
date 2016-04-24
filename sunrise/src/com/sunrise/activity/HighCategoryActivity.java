package com.sunrise.activity;

import com.sunrise.R;
import com.sunrise.adapter.Level1DataAdapter;
import com.sunrise.adapter.Level2DataAdapter;
import com.sunrise.adapter.Level3DataAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Station;
import com.sunrise.model.StationId;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class HighCategoryActivity extends Activity {

    private static final String LOG_TAG = "sunrise";

    private Station station;
    private Level1Data level1Data;

    private TextView tv_title;
    private GridView gv;
    private ListView lv_left;
    private ListView lv_right;

    private int stationId = 0;
    private int level1Id = -1;
    private int level2Id = -1;
    private int level3Id = -1;

    private Level1DataAdapter level1Adapter;
    private Level2DataAdapter level2Adapter;
    private Level3DataAdapter level3Adapter;

    private Intent lowCategoryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highcategory);

        tv_title = (TextView) findViewById(R.id.tv_title);
        gv = (GridView) findViewById(R.id.gridView1);
        lv_left = (ListView) findViewById(R.id.listView1);
        lv_right = (ListView) findViewById(R.id.listView2);
        lowCategoryIntent = new Intent(HighCategoryActivity.this, LowCategoryActivity.class);
        lowCategoryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        initStation();

        level1Adapter = new Level1DataAdapter(this);
        level1Adapter.setLevelData(station.getData());

        level2Adapter = new Level2DataAdapter(this);
        level3Adapter = new Level3DataAdapter(this);

        tv_title.setText(station.getLabel());
        gv.setAdapter(level1Adapter);
        lv_left.setAdapter(level2Adapter);
        lv_right.setAdapter(level3Adapter);

        gv.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                level1Id = position;
                level2Id = -1;
                level3Id = -1;
                lv_left.clearChoices();
                level1Data = station.getDataItem(level1Id);
                level2Adapter.setLevelData(level1Data.getData());
                lv_left.setVisibility(View.VISIBLE);
                lv_right.setVisibility(View.INVISIBLE);
            }
        });

        lv_left.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                level2Id = position;
                level3Id = -1;
                lv_right.clearChoices();
                level3Adapter.setLevelData(level1Data.getLevel2DataItem(level2Id).getData());
                lv_right.setVisibility(View.VISIBLE);
            }
        });

        lv_right.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                level3Id = position;
                startNextActivity();
            }
        });
    }

    private void startNextActivity() {

        StationId id = new StationId();
        id.stid = stationId;
        id.level1Id = level1Id;
        id.level2Id = level2Id;
        id.level3Id = level3Id;

        Bundle bundle = new Bundle();
        bundle.putSerializable("stationId", id);
        lowCategoryIntent.putExtras(bundle);
        startActivity(lowCategoryIntent);
    }

    private void initStation() {
        try {
            Bundle bundle = this.getIntent().getExtras();
            stationId = bundle.getInt("stationId");
            station = JsonFileParser.getStationWrapper(stationId).getStation();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }
}