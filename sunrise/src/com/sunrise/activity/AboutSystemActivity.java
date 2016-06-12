package com.sunrise.activity;

import com.sunrise.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutSystemActivity extends Activity {
    private TextView tvVersionNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_system);
        tvVersionNumber=(TextView) findViewById(R.id.tv_version_number);

        try {
            PackageManager pManager=getPackageManager();
            PackageInfo packInfo=pManager.getPackageInfo(getPackageName(), 0);
            String versionName=packInfo.versionName;
            tvVersionNumber.setText(versionName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void getBack(View view){
        finish();
    }

}
