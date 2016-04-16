package com.sunrise.activity;

import com.sunrise.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {
    private LinearLayout llSplash_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        llSplash_root=(LinearLayout) findViewById(R.id.ll_splash_root);
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(2000);
        llSplash_root.startAnimation(aa);

        startActivityForDelayAndFinish(SplashActivity.this, LoginActivity.class, 2000);


    }

    private void startActivityForDelayAndFinish(final SplashActivity splashActivity, final Class<LoginActivity> class1, final long delaytime) {

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(splashActivity, class1);
                splashActivity.startActivity(intent);
                splashActivity.finish();

            };
        }.start();

    }



}
