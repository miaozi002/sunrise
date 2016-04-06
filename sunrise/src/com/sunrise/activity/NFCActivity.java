package com.sunrise.activity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.sunrise.R;
import com.sunrise.model.LogUtil;
import com.sunrise.model.MyConstant;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

public class NFCActivity extends Activity {
    private static final String Tag_ASSIST = "[ReadTag_RTUrl-Book]";
    private Context mContext;
    private NfcAdapter mNfcAapter;
    private PendingIntent mNfcPendingIntent;
    //private TextView mTitle = null;
    public TextView tv_switch = (TextView)findViewById(R.id.tv_switch);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_highcategory);

        //tv_switch = (TextView) findViewById(R.id.tv_switch);
        //mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into onCreate");
        mContext = this;
       // nfcCheck();
        //checkNFCFunction();
        //initUI();
        initNFC();
    }


   /* private void nfcCheck(){
        mNfcAapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAapter == null){
            Log.w(com.sunrise.model.MyConstant.Tag, "Device not Suppport NFC");
        }else {
            if (!mNfcAapter.isEnabled()){
                Log.w(com.sunrise.model.MyConstant.Tag, "Your NFC not enable");
                //LogUtil.i(MyConstant.Tag, Tag_ASSIST + "Your NFC not enable");
                //������NFC������������
                //startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                return;
            }
        }
    }*/

    private void initUI()
    {
        tv_switch = (TextView) findViewById(R.id.tv_switch);
       // mTitle = (TextView) findViewById(R.id.read_ndef_tag_rtd_uri_title);
    }

    private void initNFC()
    {
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into initNFC");
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

//    public void setTitle(CharSequence title)
//    {
//        mTitle.setText(title);
//    }

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

    private void enableForegroundDispatch()
    {
        if (mNfcAapter != null)
        {
            mNfcAapter.enableForegroundDispatch(this, mNfcPendingIntent, null, null);
        }
    }

    private void disableForegroundDispatch()
    {
        if (mNfcAapter != null)
        {
            mNfcAapter.disableForegroundDispatch(this);
        }
    }

//    private void checkNFCFunction()
//    {
//        mNfcAapter = NfcAdapter.getDefaultAdapter(this);
//        if (mNfcAapter == null)
//        {
//            Dialog dialog = null;
//            CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);
//
//
//        }
//
//
//    }

    /*private Dialog SetDialogWidth(Dialog dialog)
    {
        DisplayMetrics dm = new DisplayMetrics();
        //������������������
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //���������������
        int screenWidth = dm.widthPixels;
        //���������������
        int screenHeight = dm.heightPixels;
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        if (screenWidth > screenHeight)
        {
            //params.width = (int) (((float) screenHeight) *0.875);
            params.height = (int) (((float) screenHeight) *0.875);

        }else
        {
            params.width = (int) (((float) screenHeight) *0.875);
        }
        dialog.getWindow().setAttributes(params);

        return dialog;
    }*/

    @Override
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

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        disableForegroundDispatch();
    }

//    ������NDEF������
//    @param intent
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

//    ������������������NdefMessage
//    @param message
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
//    ������NdefMessage
//    @param record
    /*private void parseUriRecord(NdefRecord record)
    {
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into parseUriRecord");
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN)
        {
            parseWellKnowUriRecord(record);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI)
        {
            parseAbsoluteUriRecord(record);
        } else
        {
            LogUtil.e(MyConstant.Tag, Tag_ASSIST + "Unknown TNF" + tnf);
        }
    }*/

   /* private void parseAbsoluteUriRecord(NdefRecord record)
    {
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into parseAbsolute");
        byte[] payload = record.getPayload();
        Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record Tnf:" + record.getTnf() + "\n");
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record type:" + new String(record.getType()) + "\n");
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record id:" + new String(record.getId()) + "\n");
        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record payload:" + uri + "\n");
        tv_switch.setText("REV:" + uri);

    }*/

//    private void parseWellKnowUriRecord(NdefRecord record)
//    {
//        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "into parseWellKnown");
//        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
//        byte[] payload = record.getPayload();
//
//        String prefix = URI_PREFIX_MAP.get(payload[0]);
//        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the prefix:" + prefix + "\n");
//        byte[] fullUri = Bytes.concat(prefix.getBytes(Charset.forName("UTF-8")), Arrays.copyOfRange(payload, 1, payload.length));
//        Uri uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
//        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record Tnf:" + record.getTnf() + "\n");
//        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record type:" + new String(record.getType()) + "\n");
//        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record id:" + new String(record.getId()) + "\n");
//        LogUtil.i(MyConstant.Tag, Tag_ASSIST + "the Record payload:" + uri + "\n");
//        tv_switch.setText("REV:" + uri);
//
//    }

   /* private static final BiMap<Byte,String>URI_PREFIX_MAP = ImmutableBiMap.<Byte,String> builder()
            .put((byte) 0x00, "").put((byte) 0x01, "http://www.").put((byte) 0x02, "https://www.")
            .put((byte) 0x03, "http://").put((byte) 0x04, "https//").put((byte) 0x05, "tel:")
            .put((byte) 0x06, "mailto:").put((byte) 0x07, "ftp://anonymous:anonymous@").put((byte) 0x08, "ftp://ftp.")
            .put((byte) 0x09, "ftps://").put((byte) 0x0A, "sftp://").put((byte) 0x0B, "smb://")
            .put((byte) 0x0C, "nfs://").put((byte) 0x0D, "ftp://").put((byte) 0x0E, "dav://").put((byte) 0x0F ,"news:")
            .put((byte) 0x10, "telnet://").put((byte) 0x11, "imap://").put((byte) 0x12, "rtsp://")
            .put((byte) 0x13, "urn://").put((byte) 0x14, "pop:").put((byte) 0x15, "sip:").put((byte) 0x16, "sips:")
            .put((byte) 0x17, "tftp://").put((byte) 0x18, "btspp://").put((byte) 0x19, "bt12cap://")
            .put((byte) 0x1A, "btgoep://").put((byte) 0x1B, "tcpobex://").put((byte) 0x1C, "irdaobex://")
            .put((byte) 0x1D, "file://").put((byte) 0x1E, "urn:epc:id:").put((byte) 0x1F, "urn:epc:tag:")
            .put((byte) 0x20, "urn:epc:pat:").put((byte) 0x21, "urn:epc:raw:").put((byte) 0x22, "urn:epc:")
            .put((byte) 0x23, "urn:nfc:").build();*/



}

