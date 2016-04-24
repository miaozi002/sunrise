package com.sunrise.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class LowCategoryActivity extends Activity {
    private static final String LOG_TAG = "sunrise";
    private StationId stationId;
    private Level2Data level2Data;
    private ListView lvDetail;
    LowCategoryListViewAdapter lowCategoryListViewAdapter;

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
    String time = sdf.format(date);
    String filename = String.format("modifydev%s.json", time);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lowcategory);
        lvDetail = (ListView) findViewById(R.id.lv_detail);
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

        lvDetail.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new Builder(getApplicationContext());
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("提示");
                builder.setMessage("确定要修改数据吗?");
                builder.setPositiveButton("确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNegativeButton("取消", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog aDialog = builder.create();
                aDialog.show();
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

    public void save(View saveButton) {
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
            Toast.makeText(LowCategoryActivity.this, "没有需要更新的数据!", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(LowCategoryActivity.this);
        builder.setMessage("确定更新以下数据:\n" + changedContentDesc);
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditingData.instance().setEditValues(stationId, newValues);
                DataSubmit.instance().addDataToSubmit(detail);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void submit(View submitButton) {

        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        final String serverUrl = sp.getString("serverUrl", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> dbKeys = level2Data.getKeyDbName();
                List<String> currentValues = lowCategoryListViewAdapter.getValues();
                String stid = currentValues.get(dbKeys.indexOf("stid"));
                File file = new File(getFilesDir(), filename);
                String path = String.format("http://%s/php_data/uiinterface.php?reqType=receivefile&filetype=appdevjson&stid=%s", serverUrl, stid);
                uploadFile(file, path);
            }
        }).start();
    }

    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(50000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if (file != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition:form-data; name=\"modifydevjson\";filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + "utf-8" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                int res = conn.getResponseCode();
                Log.e("com.sunrise", "response code:" + res);
                if (res == 200) {
                    Log.e("com.sunrise", "request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e("com.sunrise", "result: " + result);
                } else {
                    Log.e("com.sunrise", "request error");
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

}
