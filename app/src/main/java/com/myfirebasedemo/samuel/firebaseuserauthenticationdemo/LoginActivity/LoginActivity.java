package com.myfirebasedemo.samuel.firebaseuserauthenticationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity {

    private Button button1, button2;
    private AVLoadingIndicatorView mLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.my_loading_indicator);
        mLoadingIndicatorView.hide();

        button1 = (Button) findViewById(R.id.btn_sign_in);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingIndicatorView.show();
            }
        });

        button2 = (Button) findViewById(R.id.btn_create_account);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingIndicatorView.hide();
            }
        });
    }
}
