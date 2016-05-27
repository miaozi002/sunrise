package com.sunrise.activity;

import com.sunrise.R;
import com.sunrise.adapter.Level1DataAdapter;
import com.sunrise.adapter.Level2DataAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Station;
import com.sunrise.model.StationId;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class HighCategoryActivity extends Activity {

    private static final String LOG_TAG = "sunrise";

    private Station station;
    private Level1Data level1Data;

    private TextView tv_title;
    private ExpandableListView exp_lv;
    private GridView gv;
    private TextView tv_pre1;

    private int stationId = 0;
    private int level1Id = -1;
    private int level2Id = -1;
    private int level3Id = -1;

    private Level1DataAdapter level1Adapter;
    private Level2DataAdapter level2Adapter;

    private Intent lowCategoryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highcategory);

        tv_title = (TextView) findViewById(R.id.tv_title);
        gv = (GridView) findViewById(R.id.gridView1);
        exp_lv =  (ExpandableListView) findViewById(R.id.exp_lv);
        lowCategoryIntent = new Intent(HighCategoryActivity.this, LowCategoryActivity.class);
        lowCategoryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        tv_pre1=(TextView) findViewById(R.id.tv_pre1);

        initStation();

        level1Adapter = new Level1DataAdapter(this);
        level1Adapter.setLevelData(station.getData());

        level2Adapter = new Level2DataAdapter(this);

        tv_title.setText(station.getLabel());
        gv.setAdapter(level1Adapter);
        exp_lv.setAdapter(level2Adapter);

        gv.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickGridViewItem(position);

            }
        });


        exp_lv.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                level2Id=groupPosition;
                level3Id=childPosition;
                startNextActivity();

                return false;
            }


        });

        if (station.getData().size()==1) {
            clickGridViewItem(0);
        } else {
            clickGridViewItem(1);
        }

        tv_pre1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void clickGridViewItem(int pos) {
        if (pos < 0 || pos >= station.getData().size())
            return;
        level1Id = pos;
        level2Id = -1;
        level3Id = -1;
        gv.setItemChecked(pos, true);
        gv.setSelection(pos);
        exp_lv.clearChoices();
        level1Data = station.getDataItem(level1Id);
        level2Adapter.setLevelData(level1Data.getData());
        exp_lv.setVisibility(View.VISIBLE);
    }

   /* public void previous(View v)
    {
    	Intent intent=new Intent();
        intent.putExtra("back", "Back Data");
        setResult(2, intent);

        finish();
    }*/

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