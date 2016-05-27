package com.sunrise.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.sunrise.PublicInterface;
import com.sunrise.R;
import com.sunrise.adapter.LowCategoryListViewAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.DataSubmit;
import com.sunrise.model.DataSubmitItem;
import com.sunrise.model.EditingData;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Level2Data;
import com.sunrise.model.Station;
import com.sunrise.model.StationId;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LowCategoryActivity extends Activity {
    private static final String LOG_TAG = "sunrise";
    private StationId stationId;
    private Level2Data level2Data;
    private ListView lvDetail;
    private Button saveButton;
    LowCategoryListViewAdapter lowCategoryListViewAdapter;
    private TextView tv_pre2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowcategory);
        lvDetail = (ListView) findViewById(R.id.lv_detail);
        saveButton = (Button) findViewById(R.id.btn_save);
        saveButton.setVisibility(View.INVISIBLE);
        tv_pre2=(TextView) findViewById(R.id.tv_pre2);


        Bundle bundle = this.getIntent().getExtras();
        stationId = (StationId) bundle.getSerializable("stationId");

        try {
            Station station = JsonFileParser.getStationWrapper(stationId.stid).getStation();
            Level1Data level1Data = station.getDataItem(stationId.level1Id);
            level2Data = level1Data.getLevel2DataItem(stationId.level2Id);
            lowCategoryListViewAdapter = new LowCategoryListViewAdapter(this);
            if (EditingData.instance().getEditValues(stationId) == null) {
                EditingData.instance().createEditValues(stationId).addAll(level2Data.getValues(stationId.level3Id));
            }
            lowCategoryListViewAdapter.setData(level2Data.getKeyDisplayName(), EditingData.instance().getEditValues(stationId));
            lvDetail.setAdapter(lowCategoryListViewAdapter);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        tv_pre2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void save(final View saveButton) {
        StringBuilder sb = new StringBuilder();

        List<String> dbKeys = level2Data.getKeyDbName();
        final List<String> newValues = lowCategoryListViewAdapter.getValues();
        List<String> editValues = EditingData.instance().getEditValues(stationId);

        final DataSubmitItem detail = new DataSubmitItem();
        detail.setId(editValues.get(dbKeys.indexOf("id")));
        detail.setTbl(editValues.get(dbKeys.indexOf("tbl")));

        for (int i = 0; i < editValues.size(); i++) {
            String editValue = editValues.get(i);
            String newValue = newValues.get(i);

            if (editValue == null) {
                if (newValue != null) {
                    detail.saveToMap(dbKeys.get(i), newValue);
                    sb.append(level2Data.getKeyDisplayName().get(i) + ":" + editValue + "->" + newValue + "\n");
                }
            } else {
                if (!editValue.equals(newValue)) {

                    detail.saveToMap(dbKeys.get(i), newValue);
                    sb.append(level2Data.getKeyDisplayName().get(i) + ":" + editValue + "->" + newValue + "\n");
                }
            }
        }

        String changedContentDesc = sb.toString();
        if (changedContentDesc.trim().equals("")) {
            Toast.makeText(LowCategoryActivity.this, R.string.no_data_need_update, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(LowCategoryActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.reminder);
        builder.setMessage("确定更改一下数据:\n"+ changedContentDesc);
        builder.setPositiveButton(R.string.confirm, new  DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditingData.instance().setEditValues(stationId, newValues);
                DataSubmit.instance().addDataToSubmit(detail);
                String jsonContent = DataSubmit.instance().commit();

                try
                {
                    PublicInterface piPI = new PublicInterface(LowCategoryActivity.this);
                    File file = new File(piPI.m_strUploadDir,piPI.MODIFY_DEV_FILE);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(jsonContent.getBytes());
                    fos.close();

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                saveButton.setVisibility(View.INVISIBLE);

            }

        });


        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                saveButton.setVisibility(View.INVISIBLE);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public void setSaveButtonVisible() {
        saveButton.setVisibility(View.VISIBLE);

    }

}
