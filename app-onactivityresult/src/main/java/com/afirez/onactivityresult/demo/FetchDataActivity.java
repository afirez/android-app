package com.afirez.onactivityresult.demo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.afirez.onactivityresult.R;

public class FetchDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_data);
    }

    public void onBack(View view) {
        Intent intent = new Intent();
        intent.putExtra("text", "text from FetchDataActivity");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
