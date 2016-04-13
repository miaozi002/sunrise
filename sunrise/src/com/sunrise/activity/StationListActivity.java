package com.sunrise.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sunrise.R;
import com.sunrise.adapter.StationListAdapter;
import com.sunrise.model.StationDetail;
import com.sunrise.model.StationVersionManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StationListActivity extends Activity {
    private static final int MSG_DETAIL_LOAD = 1;
    private ProgressBar pBar;
    private TextView tvProgress;
    private TextView tvFailure;
    private ListView lvStationList;
    private Context context;
    private String serverUrl;

    private StationListAdapter stationListAdapter;
    private List<StationDetail> stationList;

    private final StationDetailMsgHandler mHandler = new StationDetailMsgHandler(this);

    private static class StationDetailMsgHandler extends Handler {
        private final WeakReference<StationListActivity> mActivity;

        public StationDetailMsgHandler(StationListActivity activity) {
            mActivity = new WeakReference<StationListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            StationListActivity activity = mActivity.get();
            if (activity == null)
                return;
            switch (msg.what) {
            case MSG_DETAIL_LOAD:
                @SuppressWarnings("unchecked")
                List<StationDetail> stationDetails = (List<StationDetail>) msg.obj;
                mActivity.get().updateStationList(stationDetails);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationlist);


        pBar = (ProgressBar) findViewById(R.id.pb);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvFailure = (TextView) findViewById(R.id.tv_failure);
        lvStationList = (ListView) findViewById(R.id.lv_station_name);
        stationListAdapter = new StationListAdapter(this);

        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        serverUrl = sp.getString("serverUrl", "192.168.0.99");
        StationVersionManager.getInstance().setServerUrl(serverUrl);

        sendRequestWithHttpClient();

        lvStationList.setAdapter(stationListAdapter);

        lvStationList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(StationListActivity.this, HighCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("stationId", stationList.get(position).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    public void updateStationList(List<StationDetail> stationDetails) {
        this.stationList = stationDetails;
        stationListAdapter.setStationList(this.stationList);
        StationVersionManager.getInstance().setJsonDir(getFilesDir());
        StationVersionManager.getInstance().setStationDetailList(this.stationList);
        StationVersionManager.getInstance().startCheck();
    }

    private void sendRequestWithHttpClient() {

        try {
            File file = new File(getFilesDir(), "stationList.json");
            if (file.exists()) {
                FileInputStream openFileInput = StationListActivity.this.openFileInput("stationList.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput));
                String content = "";
                String line = reader.readLine();
                while (line != null) {
                    content += line;
                    line = reader.readLine();
                }
                List<StationDetail> list = parseJSONWithJSONObject(content);
                updateStationList(list);
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = String.format(Locale.getDefault(),
                            "http://%s/php_data/uiinterface.php?reqType=GetStRtdbofUsr&userid=1&arid=-1&time=1459826514809", serverUrl);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(path);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");

                        FileOutputStream fos = StationListActivity.this.openFileOutput("stationList.json", Context.MODE_PRIVATE);
                        fos.write(response.getBytes());
                        fos.close();

                        mHandler.obtainMessage(MSG_DETAIL_LOAD, parseJSONWithJSONObject(response)).sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected List<StationDetail> parseJSONWithJSONObject(String jsonData) {
        //jsonData = jsonData.replace("Array()", "").trim();
        Gson gson = new Gson();
        Type typeOfObjectsList = new TypeToken<List<StationDetail>>() {
        }.getType();
        return gson.fromJson(jsonData, typeOfObjectsList);
    }

    public void click(View v) {

        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        String serverUrl = sp.getString("serverUrl", "");

        for (int i = 0; i < stationList.size(); i++) {
            StationDetail detail = stationList.get(i);
            int stationId = detail.getId();
            String jsonFileName = String.format("pack.station%d.json", stationId);
            String remoteFilePath = String.format("http://%s/stationfile/station%d/%s", serverUrl, stationId, jsonFileName);
            final String localFile = String.format("%s/%s", getFilesDir().getAbsolutePath(), jsonFileName);
            final String newFilePath = localFile + "_new";
            HttpUtils utils = new HttpUtils();
            utils.download(remoteFilePath, newFilePath, // 文件保存路径
                    true, // 是否支持断点续传
                    true, new RequestCallBack<File>() {

                        // 下载失败后调用
                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            tvFailure.setText(arg1);
                        }

                        // 下载成功后调用
                        @Override
                        public void onSuccess(ResponseInfo<File> arg0) {
                            try {
                                File f0 = new File(localFile);
                                boolean d0 = f0.getCanonicalFile().delete();
                                File f1 = new File(newFilePath);
                                boolean d1 = f1.getCanonicalFile().renameTo(new File(localFile));
                                Toast.makeText(StationListActivity.this, arg0.result.getPath(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            pBar.setMax((int) total);
                            pBar.setProgress((int) current);
                            tvProgress.setText(current * 100 / total + "%");
                        }
                    });
        }

    }

}
