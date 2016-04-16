package com.sunrise.activity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.sunrise.R;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.NFCSearchInfo;
import com.sunrise.model.Station;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class NFCActivity extends Activity {
    private int stationId = 0;
    private int level1Id = -1;
    private int level2Id = -1;
    private int level3Id = -1;
    private Station station;
    private TextView tv_switch;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter;
    private String[][] mTechList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        tv_switch = (TextView) findViewById(R.id.tv_switch);
        nfcCheck();
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
        initStation();

    }
    private void initStation() {
        try {
            Bundle bundle = this.getIntent().getExtras();
            stationId = bundle.getInt("stationId");
            station = JsonFileParser.getStationWrapper(stationId).getStation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("HighCategoryActivity:OnResume");
        if(mNfcAdapter != null){
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, mTechList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity:OnPause");
        if(mNfcAdapter != null){
            mNfcAdapter.disableForegroundDispatch(this);
        }
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

    /**
     * 获取待解析的NdefMessage
     *
     * @param messages
     */
    void processNDEFMsg(NdefMessage[] messages) {
        // LogUtil.i(MyConstant.Tag,Tag_ASSIST + "into processNDEFMsg");
        if (messages == null || messages.length == 0) {
            return;
        }
        for (int i = 0; i < messages.length; i++) {
            int length = messages[i].getRecords().length;
            // LogUtil.i(MyConstant.Tag, Tag_ASSIST + "Message" + (i + 1) + ","
            // + "length=" + length);
            NdefRecord[] records = messages[i].getRecords();
            for (int j = 0; j < length; j++) {
                for (NdefRecord record : records) {
                    // LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into
                    // resolveIntent");
                    // short tnf = record.getTnf();
                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                        parseTextRecord(record);
                    }
                }
            }
        }
    }

    /**
     * 解析NdefMessage
     *
     * @param record
     */
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
        if (!station.findByNfc(palyloadStr, info))
            return;
        level1Id = info.highCategoryId;
        level2Id = info.lowCategoryId;
        level3Id = info.dataId;
        startNextActivity();
    }

    private void startNextActivity() {
        Intent intent = new Intent(NFCActivity.this, LowCategoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("stationId", stationId);
        bundle.putInt("highActivityId", level1Id);
        bundle.putInt("midActivityId", level2Id);
        bundle.putInt("dataId", level3Id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void nfcCheck(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null){
            Toast.makeText(this, "您的手机不支持NFC功能", Toast.LENGTH_LONG).show();
            return;
        }
        else{
          if (!mNfcAdapter.isEnabled()){
              new AlertDialog.Builder(NFCActivity.this).setTitle("请打开NFC开关").setMessage("请打开NFC开关，即可获取NFC标签读取服务")
                      .setPositiveButton("开启", new DialogInterface.OnClickListener(){
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                                  }
                              }

                      ).show();
          }
        }
    }


}
