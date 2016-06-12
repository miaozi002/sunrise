package com.sunrise.activity;

import com.sunrise.PublicInterface;
import com.sunrise.R;
import com.sunrise.model.DataSubmit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class UploadCenterActivity extends Activity {
    private CheckBox cbDev;
    private CheckBox cbTour;
    PublicInterface m_piPI;
    private String m_strServerUrl;
    private String m_strUsrId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_center);

        cbDev=(CheckBox) findViewById(R.id.cb_dev);
        cbTour=(CheckBox) findViewById(R.id.cb_tour);
/*
        SharedPreferences spPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Activity.MODE_PRIVATE);
        m_strServerUrl = spPreferences.getString("serverurl", "");
        StationVersionManager.getInstance().setServerUrl(m_strServerUrl);
        m_strUsrId = spPreferences.getString("usrid", "");
*/
        m_piPI = new PublicInterface(UploadCenterActivity.this);
    }
    public void uploadAll(View view){
        if (cbDev.isChecked()) {
            m_piPI.uploadDevFile();
            DataSubmit.instance().expireOldData();
        }

    }

    public void getBack(View view){
        finish();
    }

}
