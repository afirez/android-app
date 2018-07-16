package com.afirez.common.life;

import android.app.Application;
import android.content.Context;

public interface AppLifecycleCallbacks {

    void attachBaseContext(Application app, Context base);

    void onCreate(Application app);

    void onTerminate(Application app);

}
