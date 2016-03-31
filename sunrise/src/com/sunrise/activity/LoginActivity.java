package com.sunrise.activity;

import com.sunrise.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        setViews();
        setListeners();
        readAccount();
    }
    
    private void setViews() {
        etUsername = (EditText) findViewById(R.id.et_login_username);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        btnLogin=(Button) findViewById(R.id.btn_login);
    }
    
    public void readAccount(){
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
                
        etUsername.setText(username);
        etPassword.setText(password);
    }


    private void setListeners() {
        btnLogin.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                
                CheckBox cb = (CheckBox) findViewById(R.id.cb);
                //判断选框是否被勾选
                if(cb.isChecked()){
                    //使用sharedPreference来保存用户名和密码
                    //路径在data/data/com.itheima.sharedpreference/share_
                    SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                    //拿到sp的编辑器
                    Editor ed = sp.edit();
                    ed.putString("username", username);
                    ed.putString("password", password);
                    //提交
                    ed.commit();
                }
                
                startActivity(new Intent(LoginActivity.this, HomeManageActivity.class));
                finish();

            }
        });
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
