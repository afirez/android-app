package com.afirez.log;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tencent.bugly.crashreport.CrashReport;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            Fragment fragment = new MainFragment();
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(android.R.id.content, fragment, MainFragment.class.getName())
//                    .addToBackStack(null)
//                    .commitAllowingStateLoss();
//        }

//        SystemClock.sleep(2000);
//        Singleton.getInstance(this);
    }

    public void onHello(View view) {
//        CrashReport.testJavaCrash();
//        CrashReport.testNativeCrash();
        CrashReport.testANRCrash();
//        Intent intent = new Intent();
//        intent.setClassName("com.ludashi.benchmarkhd", "com.ludashi.benchmarkhd.MainActivity");
//        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
