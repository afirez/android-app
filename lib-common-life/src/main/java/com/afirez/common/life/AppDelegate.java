package com.afirez.common.life;

import android.app.Application;
import android.content.Context;

public class AppDelegate implements AppLifecycleCallbacks {

    private static AppDelegate sInstance;

    public static AppDelegate getInstance() {
        if (sInstance == null) {
            synchronized (AppDelegate.class) {
                if (sInstance == null) {
                    sInstance = new AppDelegate();
                }
            }

        }
        return sInstance;
    }

    @Override
    public void attachBaseContext(Application app, Context base) {


    }

    @Override
    public void onCreate(Application app) {

    }

    @Override
    public void onTerminate(Application app) {

    }
}
