package com.sunrise.activity;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.sunrise.R;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.AuthResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private EditText etServerUrl;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    private static final int MSG_NETWORK_ERROR = 1;
    private static final int MSG_AUTH_ERROR = 2;
    private static final int MSG_AUTH_SUCESS = 3;

    private class LoginActivityMsgHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        public LoginActivityMsgHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            if (activity == null)
                return;
            switch (msg.what) {
            case MSG_NETWORK_ERROR:
                Toast.makeText(mActivity.get(), "服务器地址错误或者网络异常!", Toast.LENGTH_SHORT).show();
                break;

            case MSG_AUTH_ERROR:
                Toast.makeText(mActivity.get(), "用户名或者密码错误" + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case MSG_AUTH_SUCESS:
                CheckBox cb = (CheckBox) findViewById(R.id.cb);
                if (cb.isChecked()) {
                    SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                    Editor ed = sp.edit();
                    ed.putString("serverUrl", etServerUrl.getText().toString());
                    ed.putString("username", etUsername.getText().toString());
                    ed.putString("password", etPassword.getText().toString());
                    ed.commit();
                }
                mActivity.get().startNextScreen();
                break;
            default:

                break;
            }
        }
    }

    private final LoginActivityMsgHandler mHandler = new LoginActivityMsgHandler(this);

    public String getPhpData(String serverUrl, String username, String password) {
        try {
            Date date = new Date();
            long time = date.getTime();

            // shadmin:a
            String path = String.format(Locale.getDefault(), "http://%s/php_data/uiinterface.php?reqType=Userlogin&username=%s&passwd=%s&time=%d",
                    serverUrl, username, password, time);
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(path);
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 6000);

            HttpResponse res = client.execute(httpGet);

            if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return null;

            return EntityUtils.toString(res.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        JsonFileParser.setJsonDir(getFilesDir());

        setViews();
        setListeners();
        readAccount();
    }

    private void setViews() {
        etServerUrl = (EditText) findViewById(R.id.et_server_url);
        etUsername = (EditText) findViewById(R.id.et_login_username);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void startNextScreen() {
        startActivity(new Intent(LoginActivity.this, HomeManageActivity.class));
        finish();
    }

    public void readAccount() {
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        String serverUrl = sp.getString("serverUrl", "");
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");

        etUsername.setText(username);
        etPassword.setText(password);
        etServerUrl.setText(serverUrl);
    }

    private AuthResponse parseJsonWithGon(String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, AuthResponse.class);
    }

    private void setListeners() {
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkXmlFile()) {
                    startActivity(new Intent(LoginActivity.this, HomeManageActivity.class));
                    finish();
                } else {
                    final String serverUrl = etServerUrl.getText().toString();
                    final String username = etUsername.getText().toString();
                    final String password = etPassword.getText().toString();

                    if (TextUtils.isEmpty(username)) {
                        Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }else if ( TextUtils.isEmpty(serverUrl)) {
                        Toast.makeText(LoginActivity.this, "服务器地址不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String responseStr = getPhpData(serverUrl, username, password);
                            if (responseStr == null) {
                                mHandler.obtainMessage(MSG_NETWORK_ERROR).sendToTarget();
                                return;
                            }
                            AuthResponse response = parseJsonWithGon(responseStr);
                            if (!TextUtils.isEmpty(response.getErr())) {
                                mHandler.obtainMessage(MSG_AUTH_ERROR, response.getErr()).sendToTarget();
                                return;
                            }
                            mHandler.obtainMessage(MSG_AUTH_SUCESS).sendToTarget();
                        }
                    }).start();



                }

            }
        });
    }

    protected Boolean checkXmlFile() {

        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        String serverUrl = sp.getString("serverUrl", "");
        if (username.equals(etUsername.getText().toString()) && password.equals(etPassword.getText().toString())
                && serverUrl.equals(etServerUrl.getText().toString())) {
            return true;
        }
        return false;

    }

}
