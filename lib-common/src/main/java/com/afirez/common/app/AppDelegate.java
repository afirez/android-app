package com.afirez.common.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by afirez on 2018/6/15.
 */

public enum AppDelegate implements AppLifecycleCallbacks {
    @SuppressLint("StaticFieldLeak")
    INSTANCE;

    private Application app;

    private ArrayList<AppLifecycleCallbacks> mAppLifecycleCallbacks = new ArrayList<>();

    private ArrayList<Application.ActivityLifecycleCallbacks> mActivityLifecycleCallbacks = new ArrayList<>();

    @Override
    public void attachBaseContext(Application app, Context base) {
        this.app = app;
    }

    @Override
    public void onCreate(Application app) {

    }

    @Override
    public void onTerminate(Application app) {

    }

    public Application app() {
        return app;
    }


}
