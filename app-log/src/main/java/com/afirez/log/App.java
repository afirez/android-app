package com.afirez.log;

import android.app.Application;
import android.content.Context;

import com.gcml.common.app.lifecycle.AppDelegate;


/**
 * Created by afirez on 2018/5/3.
 */

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppDelegate.INSTANCE.attachBaseContext(this, base);
    }

    @Override public void onCreate() {
        super.onCreate();
        AppDelegate.INSTANCE.onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppDelegate.INSTANCE.onTerminate(this);
    }
}
