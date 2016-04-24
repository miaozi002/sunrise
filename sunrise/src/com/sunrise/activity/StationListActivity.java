package com.sunrise.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sunrise.R;
import com.sunrise.adapter.StationListAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.DataSubmit;
import com.sunrise.model.FileCleaner;
import com.sunrise.model.FileUploader;
import com.sunrise.model.NFCSearchInfo;
import com.sunrise.model.StationDetail;
import com.sunrise.model.StationVersionManager;
import com.sunrise.model.VersionInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StationListActivity extends Activity {
    public static final int MSG_DETAIL_LOAD = 1;
    public static final int MSG_VERSION_UPDATED = 2;
    public static final int MSG_UPLOAD_USERCHANGE_SUCCESS = 3;
    public static final int MSG_UPLOAD_USERCHANGE_FAIL = 4;

    private TextView tvFailure;
    private ListView lvStationList;
    private String serverUrl;
    private ImageButton downloadButton;
    private ImageButton uploadButton;
    private TextView tv_switch;
    AlertDialog dialog = null;

    private StationListAdapter stationListAdapter;
    private List<StationDetail> stationList;
    private List<Integer> updateList;

    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter;
    private String[][] mTechList;

    private final StationDetailMsgHandler mHandler = new StationDetailMsgHandler(this);

    public static class StationDetailMsgHandler extends Handler {
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
            case MSG_VERSION_UPDATED:
                @SuppressWarnings("unchecked")
                List<VersionInfo> list = (List<VersionInfo>) msg.obj;
                List<Integer> updateList = new ArrayList<Integer>();
                for (VersionInfo vi : list) {
                    Log.i(StationListActivity.class.getSimpleName(),
                            String.format("station %d has update. Local version=%d,Server version=%d", vi.id, vi.localVersion, vi.serverVersion));
                    updateList.add(vi.id);
                }
                mActivity.get().updateVersionUpdateList(updateList);
                break;
            case MSG_UPLOAD_USERCHANGE_SUCCESS:
                Toast.makeText(mActivity.get(), "上传成功!", Toast.LENGTH_SHORT).show();
                break;

            case MSG_UPLOAD_USERCHANGE_FAIL:
                Toast.makeText(mActivity.get(), "上传失败!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationlist);

        lvStationList = (ListView) findViewById(R.id.lv_station_name);
        stationListAdapter = new StationListAdapter(this);
        downloadButton = (ImageButton) findViewById(R.id.ib_download);
        downloadButton.setVisibility(View.INVISIBLE);
        uploadButton = (ImageButton) findViewById(R.id.ib_upload);
        tv_switch = (TextView) findViewById(R.id.tv_switch);

        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        serverUrl = sp.getString("serverUrl", "192.168.0.99");
        StationVersionManager.getInstance().setServerUrl(serverUrl);

        nfcCheck();

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilter = new IntentFilter[] { ndef, td };
        mTechList = new String[][] { new String[] { NfcV.class.getName(), NfcF.class.getName(), NfcA.class.getName(), NfcB.class.getName() } };

        sendRequestWithHttpClient();

        lvStationList.setAdapter(stationListAdapter);

        lvStationList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (stationList.get(position) == null) {
                    Toast.makeText(StationListActivity.this, "请先下载json文件", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(StationListActivity.this, HighCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("stationId", stationList.get(position).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    public void updateVersionUpdateList(List<Integer> updateList) {
        this.updateList = updateList;
        stationListAdapter.setUpdateList(updateList);
        downloadButton.setVisibility(View.VISIBLE);
    }

    public void updateStationList(List<StationDetail> stationDetails) {
        this.stationList = stationDetails;
        stationListAdapter.setStationList(this.stationList);

        StationVersionManager instance = StationVersionManager.getInstance();
        instance.setStationDetailList(this.stationList);
        instance.setJsonDir(getFilesDir());
        instance.setMsgHandler(mHandler);
        instance.startCheck();
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

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("HighCategoryActivity:OnResume");
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, mTechList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity:OnPause");
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("HighCategoryActivity:OnNewIntent");
        NdefMessage[] messages = null;
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null) {
            messages = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                messages[i] = (NdefMessage) rawMsgs[i];
            }
        } else {
            byte[] empty = new byte[] {};
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
            NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
            messages = new NdefMessage[] { msg };
        }
        processNDEFMsg(messages);

    }

    private void nfcCheck() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "您的手机不支持NFC功能", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                new AlertDialog.Builder(StationListActivity.this).setTitle("请打开NFC开关").setMessage("请打开NFC开关，即可获取NFC标签读取服务")
                        .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            }
                        }

                        ).show();
            }
        }
    }

    /**
     * 获取待解析的NdefMessage
     *
     * @param messages
     */
    void processNDEFMsg(NdefMessage[] messages) {
        if (messages == null || messages.length == 0) {
            return;
        }
        for (int i = 0; i < messages.length; i++) {
            int length = messages[i].getRecords().length;
            NdefRecord[] records = messages[i].getRecords();
            for (int j = 0; j < length; j++) {
                for (NdefRecord record : records) {

                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                        parseTextRecord(record);
                    }
                }
            }
        }
    }

    private void parseTextRecord(NdefRecord record) {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        String palyloadStr = "";
        byte[] payload = record.getPayload();
        Byte statusByte = record.getPayload()[0];
        String textEncoding = "";
        textEncoding = ((statusByte & 0200) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = 0;
        languageCodeLength = statusByte & 0077;
        String languageCode = "";
        languageCode = new String(payload, 1, languageCodeLength, Charset.forName("UTF-8"));
        try {
            palyloadStr = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tv_switch.setText(palyloadStr);
        NFCSearchInfo info = new NFCSearchInfo();
        if (!JsonFileParser.findStationByNFC(palyloadStr, info))
            return;
        startNextActivity(info.stationId, info.highCategoryId, info.lowCategoryId, info.dataId);
        tv_switch.setText("可在本页面扫描NFC");
    }

    private void startNextActivity(int stationId, int id1, int id2, int id3) {
        Intent intent = new Intent(StationListActivity.this, LowCategoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stationId", stationId);
        bundle.putInt("highActivityId", id1);
        bundle.putInt("midActivityId", id2);
        bundle.putInt("dataId", id3);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected List<StationDetail> parseJSONWithJSONObject(String jsonData) {
        Gson gson = new Gson();
        Type typeOfObjectsList = new TypeToken<List<StationDetail>>() {
        }.getType();
        return gson.fromJson(jsonData, typeOfObjectsList);
    }

    public void cleanTempFiles() {
        FileCleaner.cleanFiles(getFilesDir(), ".*json_new");
    }

    public void upload(View v) {
        String jsonContent = DataSubmit.instance().commit();
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
            String time = sdf.format(date);
            final String filename = String.format("modifydev%s.json", time);
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(jsonContent.getBytes());
            fos.close();

            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
            final String serverUrl = sp.getString("serverUrl", "");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getFilesDir(), filename);
                    String path = String.format("http://%s/php_data/uiinterface.php?reqType=receivefile&filetype=appdevjson&stid=1", serverUrl);
                    String r= FileUploader.uploadFile(file, path);
                    if (r != null) {// upload success
                        FileCleaner.cleanFiles(getFilesDir(), "modifydev*.json");
                        DataSubmit.instance().expireOldData();
                        mHandler.obtainMessage(MSG_UPLOAD_USERCHANGE_SUCCESS).sendToTarget();
                    }else {
                        mHandler.obtainMessage(MSG_UPLOAD_USERCHANGE_FAIL).sendToTarget();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(View v) {
        if (updateList.size() == 0) {
            Toast.makeText(StationListActivity.this, "没有网络或者已经是最新版本!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            AlertDialog.Builder builder = new Builder(this);
            // 初始化对话框布局
            View dialogView = View.inflate(getApplicationContext(), R.layout.dialog, null);
            dialog = builder.create();
            dialog.setView(dialogView, 0, 0, 0, 0);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        String serverUrl = sp.getString("serverUrl", "");
        final AtomicInteger fileCount = new AtomicInteger(updateList.size());
        Log.i("LM", "count=" + fileCount.get());

        for (int i = 0; i < updateList.size(); i++) {
            final int stationId = updateList.get(i);
            String jsonFileName = String.format("pack.station%d.json", stationId);
            String remoteFilePath = String.format("http://%s/stationfile/station%d/%s", serverUrl, stationId, jsonFileName);
            final String localFile = String.format("%s/%s", getFilesDir().getAbsolutePath(), jsonFileName);
            final String newFilePath = localFile + "_new";
            HttpUtils utils = new HttpUtils();
            cleanTempFiles();
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
                                // Toast.makeText(StationListActivity.this,
                                // arg0.result.getPath(),
                                // Toast.LENGTH_SHORT).show();
                                updateList.remove((Integer) stationId);
                                StationListActivity.this.updateVersionUpdateList(updateList);
                                JsonFileParser.reparseJsonFile(stationId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (fileCount.decrementAndGet() == 0)
                                    dialog.dismiss();
                            }
                        }
                    });
        }

    }

}
