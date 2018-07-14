package com.afirez.common.life;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppDelegate.getInstance().attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDelegate.getInstance().onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppDelegate.getInstance().onTerminate(this);
    }
}
