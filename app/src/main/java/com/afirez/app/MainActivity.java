package com.afirez.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afirez.app.cache.RxCacheActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRxCache(View view) {
        Intent intent = new Intent(this, RxCacheActivity.class);
        startActivity(intent);
    }
}
