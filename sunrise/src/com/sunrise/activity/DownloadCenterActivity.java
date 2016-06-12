package com.sunrise.activity;

import java.util.ArrayList;
import java.util.List;

import com.sunrise.FileStateInterface;
import com.sunrise.PublicInterface;
import com.sunrise.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadCenterActivity extends Activity implements FileStateInterface {
    private static final int MSG_STATION_LIST_PROGRESS = 1;
    private static final int STATIONLIST_REQUEST_CODE = 0;
    private String m_strServerUrl;
    private String m_strUsrId;
    private static PublicInterface m_piPI;
    private ProgressBar pb_station;
    private ProgressBar pb_station_list;
    private TextView tv_progress_station;
    private TextView tv_progress_station_list;
    private int stationListFileSize;
    public List<Integer> m_liDownloadedList = new ArrayList<Integer>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case StationListActivity.MSG_UPDATE_TOTAL_PROGRESS:
                pb_station.setProgress(msg.getData().getInt("count"));
                tv_progress_station.setText(pb_station.getProgress() + "/" + pb_station.getMax());
                break;
            case MSG_STATION_LIST_PROGRESS:
                pb_station_list.setProgress(msg.getData().getInt("size"));
                float num = (float) pb_station_list.getProgress() / (float) pb_station_list.getMax();
                int result = (int) (num * 100);
                tv_progress_station_list.setText(result + "%");
                break;

            default:
                break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_center);
        pb_station = (ProgressBar) findViewById(R.id.pb_station);
        pb_station_list = (ProgressBar) findViewById(R.id.pb_stationlist);
        tv_progress_station = (TextView) findViewById(R.id.tv_progress_station);
        tv_progress_station_list = (TextView) findViewById(R.id.tv_progress_stationlist);

       /* SharedPreferences spPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Activity.MODE_PRIVATE);
        m_strServerUrl = spPreferences.getString("serverurl", "");
        StationVersionManager.getInstance().setServerUrl(m_strServerUrl);
        m_strUsrId = spPreferences.getString("usrid", "");*/
        m_piPI = new PublicInterface(DownloadCenterActivity.this);

        m_piPI.handleStationListFile(DownloadCenterActivity.this);


       // downloadAllStations();

    }


    private void downloadAllStations() {
        pb_station.setMax(m_piPI.getStationListNums());
        m_liDownloadedList.clear();
        m_piPI.handleStation(DownloadCenterActivity.this);
    }

    public void getBack(View view) {
        finish();
    }

    @Override
    public void onFileStart(int requestCode, int stationId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEnd(int requestCode, int stationId) {
        if (requestCode == m_piPI.REQUEST_STATION_CODE) {
            m_liDownloadedList.add(stationId);

            Message msg = new Message();
            msg.what = StationListActivity.MSG_UPDATE_TOTAL_PROGRESS;
            msg.getData().putInt("count", m_liDownloadedList.size());
            handler.sendMessage(msg);// 发送消息
        }
    }

    @Override
    public void onFileSize(int requestCode, int stationId, int fileSize) {
        if (requestCode == STATIONLIST_REQUEST_CODE) {
            stationListFileSize=fileSize;
            pb_station_list.setMax(stationListFileSize);
        }

    }

    @Override
    public void onDownloadSize(int requestCode, int stationId, int size) {
        if ((requestCode == STATIONLIST_REQUEST_CODE)&&(size>=stationListFileSize)) {
            Message msg = new Message();
            msg.what = MSG_STATION_LIST_PROGRESS;
            msg.getData().putInt("size", size);
            handler.sendMessage(msg);
        }

    }

    @Override
    public void onDownloadFailed(int requestCode, int stationId) {
        // TODO Auto-generated method stub

    }

}
