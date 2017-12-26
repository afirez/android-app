package com.afirez.crash;

import android.app.Application;

/**
 * Created by lenovo on 2017/12/26.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoCrash.init(this);
        NoCrash.getInstance().install();
    }
}
