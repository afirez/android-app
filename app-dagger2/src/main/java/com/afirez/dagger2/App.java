package com.afirez.dagger2;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by afirez on 18-1-4.
 */

public class App extends DaggerApplication {

    DaggerDelegate mDaggerDelegate;

    @Override
    public void onCreate() {
        mDaggerDelegate = new DaggerDelegate(this);
        mDaggerDelegate.onCreate();
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .daggerComponent(mDaggerDelegate.component())
                .create(this);
    }
}
