package com.example.aixuan.xmodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtAccount;
    private EditText mEtPassword;
    private Button mButton;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(){
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mButton = (Button) findViewById(R.id.btn);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn){
            String username = mEtAccount.getText() + "";
            String password = mEtPassword.getText() + "";

            if (isCorrectInfo(username, password)) {
                Toast.makeText(MainActivity.this, "登陆成功！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isCorrectInfo(String username, String password) {
        if ("123".equals(username) && "123456".equals(password))
            return true;
        return false;
    }

}