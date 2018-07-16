package com.afirez.log;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Fragment fragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, MainFragment.class.getName())
                    .commitAllowingStateLoss();
        }
    }

    public void onHello(View view) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.antutu.ABenchMark", "com.antutu.ABenchMark.ABenchMarkStart");
        startActivity(intent);
    }
}
