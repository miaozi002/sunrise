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
import com.sunrise.model.LogUtil;
import com.sunrise.model.MyConstant;
import com.sunrise.model.Station;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
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
    private static final String Tag_ASSIST = "[ReadTag_RTUrl-Book]";
    private Context mContext;
    private NfcAdapter mNfcAapter;
    private PendingIntent mNfcPendingIntent;
    
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highcategory);
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "OnCreat");
        initNFC();

        tv_title = (TextView) findViewById(R.id.tv_title);
        gv = (GridView) findViewById(R.id.gridView1);
        lv_left = (ListView) findViewById(R.id.listView1);
        lv_right = (ListView) findViewById(R.id.listView2);
        btn_query = (Button) findViewById(R.id.btn_check);
        tv_switch = (TextView) findViewById(R.id.tv_switch);

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
                lv_right.clearChoices();
                level3Adapter.setLevelData(level1Data.getDataItem(level2Id).getData());
                lv_right.setVisibility(View.VISIBLE);
            }
        });

        lv_right.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                level3Id = position;
                String level3DataId = level1Data.getDataItem(level2Id).getData().get(level3Id).getId();
                tv_switch.setText(level3DataId);
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
    
    protected void onResume()
    {
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into onResume");
        super.onResume();
        enableForegroundDispatch();
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "enableForegroundDispatch");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "ACTION_NDEF_DISCOVERED");
            resolveIntent(getIntent());
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "ACTION_TECH_DISCOVERED");

        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()))
        {
            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "ACTION_Tag_DISCOVERED");
        }
    }
    
   /* protected void onStop(){
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into onStop");
        super.onStop();
        disableForegroundDispatch();
    }*/
    

    private void initStation() {
        try {
            Bundle bundle = this.getIntent().getExtras();
            stationId = bundle.getInt("stationId");
            station = JsonParser.parseAllJsonFiles(getFilesDir()).get(stationId);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }
    
    private void initNFC()
    {
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into initNFC");
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }
    
    private void enableForegroundDispatch()
    {
        if (mNfcAapter != null)
        {
            mNfcAapter.enableForegroundDispatch(this, mNfcPendingIntent, null, null);
        }
    }

    private void disableForegroundDispatch()
    {
        if (mNfcAapter != null){
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "disableForegroundDispatch");
        mNfcAapter.disableForegroundDispatch(this);
        }
    }
    
    void resolveIntent(Intent intent){
        LogUtil.i(MyConstant.Tag,Tag_ASSIST + "into resolveIntent");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
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
           // setTitle(R.string.title_scanned_tag);
            LogUtil.i(MyConstant.Tag,Tag_ASSIST + "New tag collected");
            processNDEFMsg(messages);
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))
        {

        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {

        } else
        {
            finish();
            return;
        }

    }
    
    void processNDEFMsg(NdefMessage[] messages)
    {
        LogUtil.i(MyConstant.Tag,Tag_ASSIST + "into processNDEFMsg");
        if (messages == null || messages.length == 0)
        {
            return;
        }
        for (int i = 0; i < messages.length; i++)
        {
            int length = messages[i].getRecords().length;
            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "Message" + (i + 1) + "," + "length=" + length);
            NdefRecord[] records = messages[i].getRecords();
            for (int j = 0; j <length; j++)
            {
                for (NdefRecord record : records)
                {
                    //LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into resolveIntent");
                    //short tnf = record.getTnf();
                    if (isText(record))
                    {
                        parseTextRecord(record);
                    }else if (isUri(record)) {
                        //parseUriRecord(record);
                    }
                }
            }
        }
    }

    private void parseTextRecord(NdefRecord record){
        /*short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN)
        {
            parseWellKnowUriRecord(record);
        }*/
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
    
    public static boolean isText (NdefRecord record){
        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {

            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "TNF_WELL_KNOWN");
            //tv_switch.setText(record.getType().toString());
            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                LogUtil.i(MyConstant.Tag, Tag_ASSIST + "RTD_TEXT");
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }
    
    public static boolean isUri (NdefRecord record)
    {
        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN)
        {
            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "TNF_WELL_KNOWN");
            //tv_switch.setText(record.getType().toString());
            if (Arrays.equals(record.getType(), NdefRecord.RTD_URI))
            {
                LogUtil.i(MyConstant.Tag, Tag_ASSIST + "RTD_URI");
                return true;
            } else
            {
                return false;
            }
        } else if (record.getTnf() == NdefRecord.TNF_ABSOLUTE_URI)
        {
            LogUtil.i(MyConstant.Tag, Tag_ASSIST + "TNF_ABSOLUTE_URI");
            return true;
        } else
        {
            return false;
        }
    }
}