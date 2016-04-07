package com.sunrise.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

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
    public Context context;

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


        sendRequestWithHttpClient();

        stationListAdapter = new StationListAdapter(this);
        stationListAdapter.setStationList(stationList);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://192.168.0.99/php_data/uiinterface.php?reqType=GetStRtdbofUsr&userid=1&arid=-1&time=1459826514809";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(path);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        mHandler.obtainMessage(MSG_DETAIL_LOAD, parseJSONWithJSONObject(response)).sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected List<StationDetail> parseJSONWithJSONObject(String jsonData) {
        Gson gson = new Gson();
        Type typeOfObjectsList = new TypeToken<List<StationDetail>>() {
        }.getType();
        return gson.fromJson(jsonData, typeOfObjectsList);
    }

    public void click(View v) {

        for (int i = 0; i < stationList.size(); i++) {
            StationDetail detail = stationList.get(i);
            int stationId = detail.getId();
            String jsonFileName = String.format("pack.station%d.json", stationId);
            String remoteFilePath = String.format("http://192.168.0.99/stationfile/station%d/%s", stationId, jsonFileName);
            String localFile = String.format("%s/%s", getFilesDir().getAbsolutePath(), jsonFileName);
            HttpUtils utils = new HttpUtils();
            utils.download(remoteFilePath, localFile, // 文件保存路径
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
                            Toast.makeText(StationListActivity.this, arg0.result.getPath(), Toast.LENGTH_SHORT).show();
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

    /*
     * private void downloadAllFiles() { for (int i = 1;; i++) { String
     * remoteFile = String.format(
     * "http://192.168.0.99/stationfile/station1/pack.station%d.json", i);
     * String localFile =
     * String.format("data/data/com.sunrise.files.pack.station%d.json", i); if
     * (!download(remoteFile, localFile)) { return; }; } }
     */

    /*
     * private void download(String path, String target) {
     *
     * HttpUtils utils = new HttpUtils(); utils.download(path, target, // 文件保存路径
     * true, // 是否支持断点续传 true, new RequestCallBack<File>() {
     *
     * // 下载失败后调用
     *
     * @Override public void onFailure(HttpException arg0, String arg1) {
     * tvFailure.setText(arg1); }
     *
     * // 下载成功后调用
     *
     * @Override public void onSuccess(ResponseInfo<File> arg0) {
     * Toast.makeText(StationListActivity.this, arg0.result.getPath(),
     * Toast.LENGTH_SHORT).show(); }
     *
     * @Override public void onLoading(long total, long current, boolean
     * isUploading) { super.onLoading(total, current, isUploading);
     * pBar.setMax((int) total); pBar.setProgress((int) current);
     * tvProgress.setText(current * 100 / total + "%"); } });
     *
     * }
     */
    /*
     * public void initStation() { try { stationList =
     * JsonParser.parseAllJsonFiles(getFilesDir()); } catch (Exception e) {
     * Log.d("sunrise", e.getMessage()); } }
     */

    /*
     * public void doClick(View v) { startActivity(new
     * Intent(DownloadActivity.this, HighCategoryActivity.class)); HttpUtils
     * utils=new HttpUtils(); // 确定下载地址 String
     * path="http://192.168.0.99/stationfile/station1/pack.station1.json";
     * String target="data/data/com.sunrise/files/pack.station1.json";
     * utils.download(path, target, // 文件保存路径 true, // 是否支持断点续传 true, new
     * RequestCallBack<File>() {
     *
     * // 下载失败后调用
     *
     * @Override public void onFailure(HttpException arg0, String arg1) {
     * tvFailure.setText(arg1); }
     *
     * // 下载成功后调用
     *
     * @Override public void onSuccess(ResponseInfo<File> arg0) {
     * Toast.makeText(DownloadActivity.this, arg0.result.getPath(), 0).show();
     *
     * }
     *
     * @Override public void onLoading(long total, long current, boolean
     * isUploading) { // TODO Auto-generated method stub super.onLoading(total,
     * current, isUploading); pBar.setMax((int) total); pBar.setProgress((int)
     * current); tvProgress.setText(current*100/total+"%"); } });
     *
     * }
     */

}
