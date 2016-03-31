package com.sunrise.activity;

import java.io.File;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sunrise.R;
import com.sunrise.adapter.StationListAdapter;
import com.sunrise.jsonparser.JsonParser;
import com.sunrise.model.Station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadActivity extends Activity {
    private ProgressBar pBar;
    private TextView tvProgress;
    private TextView tvFailure;
    private GridView gvStationList;
    public Context context;

    private StationListAdapter stationListAdapter;
    private List<Station> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        pBar = (ProgressBar) findViewById(R.id.pb);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvFailure = (TextView) findViewById(R.id.tv_failure);
        gvStationList = (GridView) findViewById(R.id.gv_station_name);

        initStation();

        stationListAdapter = new StationListAdapter(this);
        stationListAdapter.setStationList(stationList);
        gvStationList.setAdapter(stationListAdapter);

        gvStationList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                Intent intent = new Intent(DownloadActivity.this, HighCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("stationId", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public void click(View v){
        //downloadAllFiles();
        
        
            String path = "http://192.168.0.99/stationfile/station1/pack.station1.json";
            String target = "data/data/com.sunrise/files/pack.station1.json";
            download(path, target);
         
            Intent intent = new Intent(DownloadActivity.this, HighCategoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("stationId", 0);
            intent.putExtras(bundle);
            startActivity(intent);
        
    }

    /*private void downloadAllFiles() {
        for (int i = 1;; i++) {
            String remoteFile = String.format("http://192.168.0.99/stationfile/station1/pack.station%d.json", i);
            String localFile = String.format("data/data/com.sunrise.files.pack.station%d.json", i);
            if (!download(remoteFile, localFile)) {
                return;
            };
        }
    }*/

    private boolean download(String path, String target) {

        HttpUtils utils = new HttpUtils();
        utils.download(path, target, // 文件保存路径
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
                        Toast.makeText(DownloadActivity.this, arg0.result.getPath(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        pBar.setMax((int) total);
                        pBar.setProgress((int) current);
                        tvProgress.setText(current * 100 / total + "%");
                    }
                });
        return true;

    }

    public void initStation() {
        try {
            stationList = JsonParser.parseAllJsonFiles(getFilesDir());
        } catch (Exception e) {
            Log.d("sunrise", e.getMessage());
        }
    }

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
