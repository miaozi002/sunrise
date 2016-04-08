package com.sunrise.activity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.sunrise.R;
import com.sunrise.adapter.Level1DataAdapter;
import com.sunrise.adapter.Level2DataAdapter;
import com.sunrise.adapter.Level3DataAdapter;
import com.sunrise.jsonparser.JsonParser;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Station;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HighCategoryActivity extends Activity {

    private static final String LOG_TAG = "sunrise";

    private Station station;
    private Level1Data level1Data;

    private TextView tv_title;
    private GridView gv;
    private ListView lv_left;
    private ListView lv_right;
    private TextView tv_switch;
    private Button btn_query;

    private int stationId = 0;
    private int level1Id = -1;
    private int level2Id = -1;
    private int level3Id = -1;

    private Level1DataAdapter level1Adapter;
    private Level2DataAdapter level2Adapter;
    private Level3DataAdapter level3Adapter;
    //private TextView mTitle;
    //private TextView mPayload;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter;
    private String[][] mTechList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highcategory);
        //mTitle = (TextView)findViewById(R.id.read_ndef_tag_rtd_uri_title);
        //mTitle.setText("ReadActivity");
        tv_switch = (TextView)findViewById(R.id.tv_switch);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilter = new IntentFilter[] {ndef, td};
        mTechList = new String[][] {
                new String[] {
                NfcV.class.getName(),
                NfcF.class.getName(),
                NfcA.class.getName(),
               NfcB.class.getName()
            }
        };

        tv_title = (TextView) findViewById(R.id.tv_title);
        gv = (GridView) findViewById(R.id.gridView1);
        lv_left = (ListView) findViewById(R.id.listView1);
        lv_right = (ListView) findViewById(R.id.listView2);
        btn_query = (Button) findViewById(R.id.btn_query);
        //tv_switch = (TextView) findViewById(R.id.tv_switch);

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
                tv_switch.setText("");
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
                tv_switch.setText("");
                lv_right.clearChoices();
                level3Adapter.setLevelData(level1Data.getLevel2DataItem(level2Id).getData());
                lv_right.setVisibility(View.VISIBLE);
            }
        });

        lv_right.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                level3Id = position;
                String level3DataNFC = level1Data.getLevel2DataItem(level2Id).getLevel3DataItem(level3Id).getNFC();
                tv_switch.setText(level3DataNFC);
            }
        });

        btn_query.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (level3Id == -1) {
                    Toast.makeText(HighCategoryActivity.this, "请选择对应的子类别", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(HighCategoryActivity.this, LowCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("stationId", stationId);
                bundle.putInt("highActivityId", level1Id);
                bundle.putInt("midActivityId", level2Id);
                bundle.putInt("dataId", level3Id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("HighCategoryActivity:OnResume");
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, mTechList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity:OnPause");
        mNfcAdapter.disableForegroundDispatch(this);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("HighCategoryActivity:OnNewIntent");
        /**
         * 获取NDEF消息
         *
         * @param intent
         */
        NdefMessage[] messages = null;
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null)
        {
            messages = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++)
            {
                messages[i] = (NdefMessage) rawMsgs[i];
            }
        } else
        {
            byte[] empty = new byte[]{};
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
            NdefMessage msg = new NdefMessage(new NdefRecord[]{ record });
            messages = new NdefMessage[]{ msg};
        }
        processNDEFMsg(messages);

    }

    /**
     * 获取待解析的NdefMessage
     *
     * @param messages
     */
    void processNDEFMsg(NdefMessage[] messages)
    {
        //LogUtil.i(MyConstant.Tag,Tag_ASSIST + "into processNDEFMsg");
        if (messages == null || messages.length == 0)
        {
            return;
        }
        for (int i = 0; i < messages.length; i++)
        {
            int length = messages[i].getRecords().length;
            //LogUtil.i(MyConstant.Tag, Tag_ASSIST + "Message" + (i + 1) + "," + "length=" + length);
            NdefRecord[] records = messages[i].getRecords();
            for (int j = 0; j <length; j++)
            {
                for (NdefRecord record : records)
                {
                    //LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into resolveIntent");
                    //short tnf = record.getTnf();
                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN)
                    {
                        parseTextRecord(record);
                    }
                }
            }
        }
    }

    /**
     *解析NdefMessage
     *
     * @param record
     */
    private void parseTextRecord(NdefRecord record){
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        String palyloadStr = "";
        byte[] payload = record.getPayload();
        Byte statusByte = record.getPayload()[0];
        String textEncoding = "";
        textEncoding = ((statusByte & 0200) == 0) ? "UTF-8" : "UTF-16";
        int  languageCodeLength = 0;
        languageCodeLength = statusByte & 0077;
        String languageCode = "";
        languageCode = new String(payload, 1, languageCodeLength, Charset.forName("UTF-8"));
        try{
            palyloadStr = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        tv_switch.setText(palyloadStr);
    }

    private void initStation() {
        try {
            Bundle bundle = this.getIntent().getExtras();
            stationId = bundle.getInt("stationId");
            station = JsonParser.getStationWrapper(stationId).getStation();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

}