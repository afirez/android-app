package com.afirez.common.lifecycle;

import android.app.Application;
import android.content.Context;

/**
 * Created by afirez on 2018/6/15.
 */

public interface AppLifecycleCallbacks {

    void attachBaseContext(Application app, Context base);

    void onCreate(Application application);

    void onTerminate(Application application);

}
