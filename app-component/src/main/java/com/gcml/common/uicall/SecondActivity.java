package com.gcml.common.uicall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gcml.common.demo.R;
import com.gcml.common.utils.UiCall;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.tv_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiCall.success(SecondActivity.this);
                finish();
            }
        });
    }
}
