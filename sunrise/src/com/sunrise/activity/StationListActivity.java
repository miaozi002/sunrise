package com.sunrise.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.sunrise.FileStateInterface;
import com.sunrise.FileUpLoader;
import com.sunrise.PublicInterface;
import com.sunrise.R;
import com.sunrise.adapter.StationListAdapter;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.FileCleaner;
import com.sunrise.model.NFCSearchInfo;
import com.sunrise.model.StationDetail;
import com.sunrise.model.StationVersionManager;
import com.sunrise.model.VersionInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StationListActivity extends AddStationList implements FileStateInterface {
	public static final String  Tag = "StationListActivity";
    public static final int MSG_DETAIL_LOAD = 1;
    public static final int MSG_VERSION_UPDATED = 2;
    public static final int MSG_UPLOAD_USERCHANGE_SUCCESS = 3;
    public static final int MSG_UPLOAD_USERCHANGE_FAIL = 4;
    public static final int MSG_ADD_STATION_LIST = 5;
    public static final int MSG_UPDATE_TOTAL_PROGRESS = 6;
    public static final int MSG_UPDATE_SINGLE_PROGRESS = 7;
    public static final int MSG_UPDATE_STATION_END = 8;
    
    private final static int REQUEST_CODE = 1;
    private final static int REQUEST_CODE_LIST = 2;

    private TextView tvFailure;
    private ListView lvStationList;
    private String m_strServerUrl;
    private String m_strUsrId;
    private ImageButton downloadButton;
    private ImageButton uploadButton;
    private TextView tv_switch;

    private StationListAdapter stationListAdapter;
    private List<StationDetail> stationList;
    private List<Integer> m_liUpdateList;

    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter;
    private String[][] mTechList;
    private int m_iFilesize;
    private static PublicInterface m_piPI;


    private List<Integer> m_liDownloadedList = new ArrayList<Integer>();
    private AlertDialog   m_adDialog = null;
    private ProgressBar   m_pbSingle;
    private ProgressBar   m_pbTotal;
    private AlertDialog.Builder m_abBuilder;
    private View          m_vwDialogView;
    private TextView      m_tvSingle;
    private TextView      m_tvTotal;

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
            case MSG_ADD_STATION_LIST:
                mActivity.get().addStationList();
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
            case MSG_UPDATE_SINGLE_PROGRESS:
                mActivity.get().m_pbSingle.setProgress(msg.getData().getInt("size"));
                float num = (float)mActivity.get().m_pbSingle.getProgress()/(float)mActivity.get().m_pbSingle.getMax();
                int result = (int)(num*100);
                mActivity.get().m_tvSingle.setText(result+ "%");
                break;
            case MSG_UPDATE_TOTAL_PROGRESS:
                mActivity.get().m_pbTotal.setProgress(msg.getData().getInt("count"));
                mActivity.get().m_tvTotal.setText(mActivity.get().m_pbTotal.getProgress()+ "/" + mActivity.get().m_pbTotal.getMax());
                break;
            case MSG_UPLOAD_USERCHANGE_SUCCESS:
                Toast.makeText(mActivity.get(), "上传成功!", Toast.LENGTH_SHORT).show();
                break;
            case MSG_UPDATE_STATION_END:
            	mActivity.get().updateStation();
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

        SharedPreferences spPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Activity.MODE_PRIVATE);
        m_strServerUrl = spPreferences.getString("serverurl", "");
        StationVersionManager.getInstance().setServerUrl(m_strServerUrl);
        m_strUsrId = spPreferences.getString("usrid", "");
        m_piPI = new PublicInterface(StationListActivity.this, m_strServerUrl, m_strUsrId);

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

        lvStationList.setAdapter(stationListAdapter);

        lvStationList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = String.format("pack.station%s.json", stationList.get(position).getId());
                File file = new File(m_piPI.m_strDownloadDir, filename);
                if (!file.exists()) 
                {
                    Toast.makeText(StationListActivity.this, "请先下载json文件", Toast.LENGTH_SHORT).show();
                } 
                else 
                {
                    Intent intent = new Intent(StationListActivity.this, HighCategoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("stationId", stationList.get(position).getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });

        if (m_piPI.handleStationListFile(StationListActivity.this)) {
            addStationList();
        }

    }

    private void addStationList() {
        List<StationDetail> list = m_piPI.readStationList();
        if (list != null) {
            updateStationList(list);
        } else {
            Toast.makeText(StationListActivity.this, "加载stationList.json文件失败!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateVersionUpdateList(List<Integer> updateList) {
        this.m_liUpdateList = updateList;
        stationListAdapter.setUpdateList(updateList);
        downloadButton.setVisibility(View.VISIBLE);
    }

    public void updateStationList(List<StationDetail> stationDetails) {
        this.stationList = stationDetails;
        stationListAdapter.setStationList(this.stationList);

        StationVersionManager instance = StationVersionManager.getInstance();
        instance.setStationDetailList(this.stationList);
        instance.setJsonDir(m_piPI.m_strDownloadDir);
        instance.setMsgHandler(mHandler);
        instance.startCheck();
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

    public void upload(View v) {
    	m_piPI.uploadDevFile();
    }

    @SuppressLint("DefaultLocale")
    public void download(View v) {
        if (m_liUpdateList.size() == 0)
        {
            Toast.makeText(StationListActivity.this, "没有网络或者已经是最新版本!", Toast.LENGTH_SHORT).show();
            return;
        }

        onViewProgress();
        m_piPI.handleStation(StationListActivity.this);
    }

    private void onViewProgress() {
        // TODO Auto-generated method stub
    	m_abBuilder = new Builder(this);

        // 初始化对话框布局
    	m_vwDialogView = View.inflate(getApplicationContext(), R.layout.dialog, null);
        m_adDialog = m_abBuilder.create();
        m_adDialog.setView(m_vwDialogView);

        m_tvSingle =(TextView) m_vwDialogView.findViewById(R.id.tv_progress0);
        m_tvTotal =(TextView) m_vwDialogView.findViewById(R.id.tv_progress);
        
        m_pbSingle =(ProgressBar) m_vwDialogView.findViewById(R.id.progressBar1);
        m_pbTotal = (ProgressBar) m_vwDialogView.findViewById(R.id.progressBar2);
        m_pbTotal.setMax(m_piPI.getStationListNums());
        m_adDialog.show();
        
        m_liDownloadedList.clear();
        
        Button btcancel = (Button) m_vwDialogView.findViewById(R.id.exit_dialog);
        btcancel.setOnClickListener((new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateStation();
			}
		}));
    }
    
    private void updateStation()
    {
    	m_adDialog.dismiss();
    	StationListActivity.this.updateVersionUpdateList(m_liUpdateList);
    	
    	for (int i = 0; i < m_liUpdateList.size(); i++)
    	{
    		int Id = m_liUpdateList.get(i);
    		JsonFileParser.reparseJsonFile(Id);
    		Log.d("Id",String.valueOf(Id));
    	}
    		
    	if(m_liUpdateList.size() == m_liDownloadedList.size())
    		downloadButton.setVisibility(View.INVISIBLE);
    	
    	m_liUpdateList.removeAll(m_liDownloadedList);
    }
    
	@Override
	public void onFileStart(int requestCode,int stationId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnd(int requestCode,int stationId) {
		// TODO Auto-generated method stub

		if (requestCode == m_piPI.REQUEST_STATION_CODE)
		{
			m_liDownloadedList.add(stationId);
			
			Message msg = new Message();
            msg.what = StationListActivity.MSG_UPDATE_TOTAL_PROGRESS;
            msg.getData().putInt("count", m_liDownloadedList.size());
            mHandler.sendMessage(msg);//发送消息
		}
		
	    if ((requestCode == m_piPI.REQUEST_STATION_CODE) && (m_liDownloadedList.size() >= m_piPI.getStationListNums()))
	    	mHandler.obtainMessage(StationListActivity.MSG_UPDATE_STATION_END).sendToTarget();
	}

	@Override
	public void onFileSize(int requestCode,int stationId,int fileSize)
	{
		// TODO Auto-generated method stub
	    if (requestCode == m_piPI.REQUEST_STATIONLIST_CODE)
	        m_iFilesize = fileSize;
	    if (requestCode == m_piPI.REQUEST_STATION_CODE)
	    	m_pbSingle.setMax(fileSize);
	}

	@Override
	public void onDownloadSize(int requestCode,int stationId,int size) {
		// TODO Auto-generated method stub
		if ((requestCode == m_piPI.REQUEST_STATIONLIST_CODE) && (size >= m_iFilesize))
		    mHandler.obtainMessage(StationListActivity.MSG_ADD_STATION_LIST).sendToTarget();

		if ((requestCode == m_piPI.REQUEST_STATION_CODE) && (size >= m_iFilesize))
		{
		    Message msg = new Message();
            msg.what = StationListActivity.MSG_UPDATE_SINGLE_PROGRESS;
            msg.getData().putInt("size", size);
            mHandler.sendMessage(msg);//发送消息
		}
	}

	@Override
	public void onDownloadFailed(int requestCode,int stationId) {
		// TODO Auto-generated method stub
    }

}
