package com.sunrise.activity;

import java.io.File;
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
import com.sunrise.PublicInterface;
import com.sunrise.R;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.AuthResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    public static final String PREF_NAME = "info";
    private SharedPreferences m_spPreference;
    private TextView m_tvSetting;
    private EditText m_etUsername;
    private EditText m_etPassword;
    private EditText m_etServerUrl;
    private Button m_btnLogin;
    private CheckBox m_chPwd;
    private String m_strUsrId;
    private String serverUrl;


    private static final int MSG_NETWORK_ERROR = 1;
    private static final int MSG_AUTH_ERROR = 2;
    private static final int MSG_AUTH_SUCESS = 3;

    private PublicInterface piPI;

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
                Toast.makeText(mActivity.get(), R.string.server_address_error_or_network_not_connection, Toast.LENGTH_SHORT).show();
                break;
            case MSG_AUTH_ERROR:
                Toast.makeText(mActivity.get(), R.string.username_or_password_error + String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                break;
            case MSG_AUTH_SUCESS:
                Editor ed = m_spPreference.edit();
                ed.putString("serverurl", m_etServerUrl.getText().toString());
                ed.putString("username", m_etUsername.getText().toString());
                ed.putString("password", m_etPassword.getText().toString());
                ed.putString("usrid", m_strUsrId);
                ed.putBoolean("ischecked", m_chPwd.isChecked());
                ed.commit();
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

        piPI = new PublicInterface(LoginActivity.this);
        JsonFileParser.setJsonDir(piPI.m_strDownloadDir);
        m_spPreference = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        m_etServerUrl = new EditText(LoginActivity.this);
        setViews();
        setListeners();
        readAccount();
    }

    private void setViews() {
        m_tvSetting = (TextView) findViewById(R.id.tv_setting);
        m_etUsername = (EditText) findViewById(R.id.et_login_username);
        m_etPassword = (EditText) findViewById(R.id.et_login_password);
        m_btnLogin = (Button) findViewById(R.id.btn_login);
        m_chPwd = (CheckBox) findViewById(R.id.cb);
    }

    private void startNextScreen() {
        startActivity(new Intent(LoginActivity.this, HomeManageActivity.class));
        finish();
    }

    public void readAccount() {

        m_etUsername.setText(m_spPreference.getString("username", ""));
        m_etServerUrl.setText(m_spPreference.getString("serverurl", ""));
        m_etPassword.setText("");
        m_strUsrId = m_spPreference.getString("usrid", "");
        m_chPwd.setChecked(m_spPreference.getBoolean("ischecked", false));
        if (m_chPwd.isChecked()) {
            m_etPassword.setText(m_spPreference.getString("password", ""));
        }
    }

    private AuthResponse parseJsonWithGson(String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, AuthResponse.class);
    }

    private void setListeners() {
        m_tvSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new Builder(LoginActivity.this);
                builder.setTitle("请输入服务器IP地址:").setIcon(android.R.drawable.ic_dialog_info);
                ViewGroup parent= (ViewGroup)m_etServerUrl.getParent();
                if(parent!=null){
                    parent.removeView(m_etServerUrl);
                }
                builder.setView(m_etServerUrl);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editor ed = m_spPreference.edit();
                        serverUrl=m_etServerUrl.getText().toString();
                        ed.putString("serverurl", serverUrl);
                        if (TextUtils.isEmpty(m_etServerUrl.getText().toString())) {
                            Toast.makeText(LoginActivity.this, R.string.the_address_of_server_can_not_be_empty, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });

        m_btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               serverUrl = m_etServerUrl.getText().toString();
                final String username = m_etUsername.getText().toString();
                final String password = m_etPassword.getText().toString();


                  if ( TextUtils.isEmpty(serverUrl)) {
                  Toast.makeText(LoginActivity.this,
                  R.string.the_address_of_server_can_not_be_empty,
                  Toast.LENGTH_SHORT).show(); return; }else

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this, R.string.username_can_not_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, R.string.password_can_not_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checkXmlFile()) {

                    mHandler.obtainMessage(MSG_AUTH_SUCESS).sendToTarget();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String responseStr = getPhpData(serverUrl, username, password);
                            if (responseStr == null) {
                                mHandler.obtainMessage(MSG_NETWORK_ERROR).sendToTarget();
                                return;
                            }
                            AuthResponse response = parseJsonWithGson(responseStr);
                            m_strUsrId = response.getUserid();
                            if (!TextUtils.isEmpty(response.getErr())) {
                                mHandler.obtainMessage(MSG_AUTH_ERROR, response.getErr()).sendToTarget();
                                return;
                            } else {
                                // 删除model中的json文件
                                for (File file : piPI.m_strDownloadDir.listFiles()) {
                                    file.delete();
                                }

                            }
                            mHandler.obtainMessage(MSG_AUTH_SUCESS).sendToTarget();

                        }
                    }).start();
                }

            }
        });
    }

    protected Boolean checkXmlFile() {
        String username = m_spPreference.getString("username", "");
        String password = m_spPreference.getString("password", "");
       String serverUrl = m_spPreference.getString("serverurl", "");
        if ((!TextUtils.isEmpty(username)) && username.equals(m_etUsername.getText().toString()) && password.equals(m_etPassword.getText().toString())&&(serverUrl.equals(m_etServerUrl.getText().toString()))
                ) {
            return true;
        }
        return false;
    }
}
